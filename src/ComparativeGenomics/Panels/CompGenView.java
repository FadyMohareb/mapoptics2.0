package ComparativeGenomics.Panels;

import ComparativeGenomics.FileHandling.DataHandling.XmapData;
import ComparativeGenomics.FileHandling.DataHandling.Alignment;
import ComparativeGenomics.FileHandling.DataHandling.Gene;
import ComparativeGenomics.FileHandling.DataHandling.Genome;
import ComparativeGenomics.FileHandling.DataHandling.Chromosome;
import ComparativeGenomics.ServerHandling.*;
import ComparativeGenomics.FileHandling.*;
import ComparativeGenomics.StructuralVariant.*;
import com.opencsv.CSVWriter;
import com.qoppa.pdfWriter.PDFDocument;
import com.qoppa.pdfWriter.PDFPage;
import java.awt.FileDialog;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.knowm.xchart.style.Styler;

/**
 *
 * @author franpeters
 */
public class CompGenView extends javax.swing.JFrame {

    private Alignment alignment;
    private Genome refGenome;
//     private Genome qryGenome;
    private Cmap qryCmap;
    private Chromosome currentChr;
    private ArrayList<Indel> indels = new ArrayList();
    private ArrayList<Gene> genes = new ArrayList();
    private Job job;
    private String refGenomeName;
//    private String qryGenomeName;
    private Cmap cmapRef;
    private Cmap cmapQry;
    private Karyotype refKary;
//    private Karyotype qryKary;
    private Xmap xmap;
    private Fasta refFasta;
//    private Fasta qryFasta;
    private Annot refAnnot;
//    private Annot qryAnnot;

    /**
     * Creates new form GenomeView
     */
    public CompGenView() {
        initComponents();

    }

    /**
     * Method for loading a local alignment jobs' results
     *
     * @param refOrg
     * @param cmapref
     * @param cmapqry
     * @param refkary
     * @param xmapfile
     * @param reffasta
     * @param refannot
     */
    public void setData(String refOrg, String cmapref, String cmapqry,
            String refkary, String xmapfile, String reffasta,
            String refannot) {
        this.parsingDialog.setVisible(true);
        jobNameLabel.setText("Local Job");
        refGenomeName = refOrg;
//        String qryGenomeName = job.getQryOrg();

        // cmap files are parsed
        cmapRef = new Cmap(cmapref);
        cmapQry = new Cmap(cmapqry);

        // Karyotype file is parsed
        refKary = new Karyotype(refkary);
//       qryKary = new Karyotype(System.getProperty("user.dir")+"/download/"+job.getName()+File.separator+"qry_karyotype.txt");

        // XMAP file is parsed
        xmap = new Xmap(xmapfile);

        // FASTA file is parsed
        refFasta = new Fasta();
//        Fasta qryFasta=new Fasta();

        // Annotation file is parsed
        refAnnot = new Annot(refannot);
        populateData();
    }

    public void setJob(Job j) {
        this.job = j;
        this.parsingDialog.setVisible(true);

        jobNameLabel.setText(job.getName());
        enzymeLabel.setText(job.getEnz().getName());
        refSpeciesLabel.setText(job.getRefOrg());
        qrySpeciesLabel.setText(job.getQryOrg());
        alignerLabel.setText(job.getPipeline());

        refGenomeName = job.getRefOrg();
//        String qryGenomeName = job.getQryOrg();

        // Cmap files are parsed
        cmapRef = new Cmap(System.getProperty("user.dir") + "/download/" + job.getName() + File.separator + job.getName() + "_ref.cmap");

        cmapQry = new Cmap(System.getProperty("user.dir") + "/download/" + job.getName() + File.separator + job.getName() + "_qry.cmap");

        // Karyotype file is parsed
        refKary = new Karyotype(System.getProperty("user.dir") + "/download/" + job.getName() + File.separator + "ref_karyotype.txt");
//        Karyotype qryKary = new Karyotype(System.getProperty("user.dir")+"/download/"+job.getName()+File.separator+"qry_karyotype.txt");

        // xmap file is parsed
        xmap = new Xmap(System.getProperty("user.dir") + "/download/" + job.getName() + File.separator + job.getName() + ".xmap");

        // Fasta and annotation files are parsed
        refFasta = new Fasta();
//        qryFasta=new Fasta();
        //System.out.println(this.job.getRefAnnot());
        refAnnot = new Annot(this.job.getRefAnnot());

//        qryAnnot = new Annot(this.job.getQryAnnot());
        //this.qryCmap = cmapQry;
        populateData();
    }

    private void populateData() {
        xmap.setRefCmap(cmapRef);
        xmap.setQryCmap(cmapQry);
        // Set minimum InDel size for SV detection
        xmap.detectSVs(500);

        // Store reference genome
        this.refGenome = new Genome(refGenomeName, cmapRef, refKary, refFasta, refAnnot);

//        this.qryGenome = new Genome(qryGenomeName,cmapQry,qryKary,qryFasta,qryAnnot);
        // Store alignments files
        this.alignment = new Alignment(this.refGenome, cmapQry, xmap);

        // Pass information to chromosome panel
        //this.chromosomePanel1.setQueryCmap(qryCmap);
        this.chromosomePanel1.setQueryCmap(cmapQry);
        this.genomePanel1.setAlignment(this.alignment, "reference");
        this.genomePanel1.repaint();
        this.chromosomeChartPanel1.plotGenome(refGenome);
        this.alignmentsPerChromosomeChartPanel1.plotGenome(refGenome);
        this.queryPanel1.setRefOrg(this.refGenome.getName());
        this.queryPanel1.setXmap(this.alignment.getXmap());
        this.circosPanel1.setKaryotype(refKary, this.alignment);

        DefaultTableModel chrTable = (DefaultTableModel) this.chromosomeTable.getModel();
        this.chromosomeTable.setAutoCreateRowSorter(true);
//         First clear the chromosomes JTable of any previous data
        chrTable.setRowCount(0);
        chrTable.setColumnCount(0);

        String[] columnNames = {"Chromosome", "Size"};
        chrTable.setColumnIdentifiers(columnNames); //Set the column names of this table

        for (Map.Entry<Integer, Chromosome> entry : this.alignment.getRefGenome().getChromosomes().entrySet()) {
            Chromosome chr = entry.getValue();
            String[] chrData = {chr.getName(), String.valueOf(BigInteger.valueOf(chr.getSize().intValue()))};
            chrTable.addRow(chrData);
        }

        this.parsingDialog.setVisible(false);
        this.setVisible(true);

        DefaultTableModel transTableModel = (DefaultTableModel) this.translocationTable.getModel();
        this.translocationTable.setAutoCreateRowSorter(true);
//         First clear the chromosomes JTable of any previous data
        transTableModel.setRowCount(0);
        transTableModel.setColumnCount(0);

        String[] columnNamesTrans = {"Chromosome 1", "Chromosome 2"};
        transTableModel.setColumnIdentifiers(columnNamesTrans); //Set the column names of this table

        for (Translocation t : this.alignment.getTranslocations()) {

            String[] tData = {t.getRefChr1Name(), t.getRefChr2Name()};
            transTableModel.addRow(tData);
        }
        this.parsingDialog.setVisible(false);
        this.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        parsingDialog = new javax.swing.JDialog();
        jPanel9 = new javax.swing.JPanel();
        changePlotStyleDialog = new javax.swing.JDialog();
        ggplotButton = new javax.swing.JRadioButton();
        matlabButton = new javax.swing.JRadioButton();
        xchartButton = new javax.swing.JRadioButton();
        setChartStyle = new javax.swing.JButton();
        savePanelDialog = new javax.swing.JDialog();
        saveTableDialog = new javax.swing.JDialog();
        buttonGroup1 = new javax.swing.ButtonGroup();
        genomeViewTabPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel2 = new javax.swing.JPanel();
        genomePanel1 = new ComparativeGenomics.Panels.GenomePanel();
        jPanel20 = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        chromosomeChartPanel1 = new ComparativeGenomics.Panels.ChromosomeChartPanel();
        alignmentsPerChromosomeChartPanel1 = new ComparativeGenomics.Panels.AlignmentsPerChromosomeChartPanel();
        jPanel14 = new javax.swing.JPanel();
        circosPanel1 = new ComparativeGenomics.Panels.CircosPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        translocationTable = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jobNameLabel = new javax.swing.JLabel();
        enzymeLabel = new javax.swing.JLabel();
        refSpeciesLabel = new javax.swing.JLabel();
        qrySpeciesLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        alignerLabel = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        chromosomeTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel13 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        chrLabel = new javax.swing.JLabel();
        chromosomePanel1 = new ComparativeGenomics.Panels.ChromosomePanel();
        clearChrAlignmentHighlighted = new javax.swing.JButton();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        chrAlignTable = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jLayeredPane6 = new javax.swing.JLayeredPane();
        jPanel24 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        chrLabel2 = new javax.swing.JLabel();
        queryPanel1 = new ComparativeGenomics.Panels.QueryPanel();
        jPanel21 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        queryChrSize = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        queryStart = new javax.swing.JTextField();
        queryEnd = new javax.swing.JTextField();
        decreaseRange = new javax.swing.JButton();
        incrementSize = new javax.swing.JTextField();
        increaseRange = new javax.swing.JButton();
        updateRangeButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        variantTable = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        alignmentsOnChromosomeChartPanel1 = new ComparativeGenomics.Panels.AlignmentsOnChromosomeChartPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        geneTable = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        exitProgram = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        exportAlignmentsTable = new javax.swing.JMenuItem();
        exportIndelsTable = new javax.swing.JMenuItem();
        exportTranslocationsTable = new javax.swing.JMenuItem();
        exportGenesTable = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        exportChrView = new javax.swing.JMenuItem();
        exportQryView = new javax.swing.JMenuItem();
        exportCircos = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        exportGraphAlignments = new javax.swing.JMenuItem();
        exportGraphSV = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        changePlotStyle = new javax.swing.JMenuItem();

        parsingDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        parsingDialog.setTitle("Parsing Downloaded Files");
        parsingDialog.setBounds(new java.awt.Rectangle(500, 500, 300, 150));
        parsingDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 288, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 138, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout parsingDialogLayout = new javax.swing.GroupLayout(parsingDialog.getContentPane());
        parsingDialog.getContentPane().setLayout(parsingDialogLayout);
        parsingDialogLayout.setHorizontalGroup(
            parsingDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parsingDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        parsingDialogLayout.setVerticalGroup(
            parsingDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parsingDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        changePlotStyleDialog.setTitle("Choose Plot Style");
        changePlotStyleDialog.setLocationByPlatform(true);
        changePlotStyleDialog.setPreferredSize(new java.awt.Dimension(220, 80));
        changePlotStyleDialog.setSize(new java.awt.Dimension(213, 75));

        buttonGroup1.add(ggplotButton);
        ggplotButton.setSelected(true);
        ggplotButton.setText("GGPlot2");

        buttonGroup1.add(matlabButton);
        matlabButton.setText("Matlab");

        buttonGroup1.add(xchartButton);
        xchartButton.setText("XChart");

        setChartStyle.setText("Set");
        setChartStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setChartStyleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout changePlotStyleDialogLayout = new javax.swing.GroupLayout(changePlotStyleDialog.getContentPane());
        changePlotStyleDialog.getContentPane().setLayout(changePlotStyleDialogLayout);
        changePlotStyleDialogLayout.setHorizontalGroup(
            changePlotStyleDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changePlotStyleDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(changePlotStyleDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(setChartStyle)
                    .addGroup(changePlotStyleDialogLayout.createSequentialGroup()
                        .addComponent(ggplotButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(matlabButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xchartButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        changePlotStyleDialogLayout.setVerticalGroup(
            changePlotStyleDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changePlotStyleDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(changePlotStyleDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ggplotButton)
                    .addComponent(matlabButton)
                    .addComponent(xchartButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setChartStyle)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout savePanelDialogLayout = new javax.swing.GroupLayout(savePanelDialog.getContentPane());
        savePanelDialog.getContentPane().setLayout(savePanelDialogLayout);
        savePanelDialogLayout.setHorizontalGroup(
            savePanelDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        savePanelDialogLayout.setVerticalGroup(
            savePanelDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout saveTableDialogLayout = new javax.swing.GroupLayout(saveTableDialog.getContentPane());
        saveTableDialog.getContentPane().setLayout(saveTableDialogLayout);
        saveTableDialogLayout.setHorizontalGroup(
            saveTableDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        saveTableDialogLayout.setVerticalGroup(
            saveTableDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Comparative Genomics View");

        genomeViewTabPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                genomeViewTabPaneStateChanged(evt);
            }
        });

        jSplitPane1.setDividerLocation(300);

        genomePanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Genome View"));

        javax.swing.GroupLayout genomePanel1Layout = new javax.swing.GroupLayout(genomePanel1);
        genomePanel1.setLayout(genomePanel1Layout);
        genomePanel1Layout.setHorizontalGroup(
            genomePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        genomePanel1Layout.setVerticalGroup(
            genomePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 213, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(genomePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(genomePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Graphs"));

        javax.swing.GroupLayout chromosomeChartPanel1Layout = new javax.swing.GroupLayout(chromosomeChartPanel1);
        chromosomeChartPanel1.setLayout(chromosomeChartPanel1Layout);
        chromosomeChartPanel1Layout.setHorizontalGroup(
            chromosomeChartPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 887, Short.MAX_VALUE)
        );
        chromosomeChartPanel1Layout.setVerticalGroup(
            chromosomeChartPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 404, Short.MAX_VALUE)
        );

        jTabbedPane3.addTab("SVs Across Genome", chromosomeChartPanel1);

        javax.swing.GroupLayout alignmentsPerChromosomeChartPanel1Layout = new javax.swing.GroupLayout(alignmentsPerChromosomeChartPanel1);
        alignmentsPerChromosomeChartPanel1.setLayout(alignmentsPerChromosomeChartPanel1Layout);
        alignmentsPerChromosomeChartPanel1Layout.setHorizontalGroup(
            alignmentsPerChromosomeChartPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 887, Short.MAX_VALUE)
        );
        alignmentsPerChromosomeChartPanel1Layout.setVerticalGroup(
            alignmentsPerChromosomeChartPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 404, Short.MAX_VALUE)
        );

        jTabbedPane3.addTab("Alignments Per Chromosome", alignmentsPerChromosomeChartPanel1);

        javax.swing.GroupLayout circosPanel1Layout = new javax.swing.GroupLayout(circosPanel1);
        circosPanel1.setLayout(circosPanel1Layout);
        circosPanel1Layout.setHorizontalGroup(
            circosPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 459, Short.MAX_VALUE)
        );
        circosPanel1Layout.setVerticalGroup(
            circosPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        translocationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(translocationTable);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(circosPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                    .addComponent(circosPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Translocations", jPanel14);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 14, Short.MAX_VALUE))
        );

        jLayeredPane1.setLayer(jPanel2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jPanel20, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jLayeredPane1);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Job Information"));

        jLabel1.setText("Job Name:");

        jLabel2.setText("Enzyme:");

        jLabel3.setText("Ref genome:");

        jLabel5.setText("Query name:");

        jobNameLabel.setText("jLabel9");

        enzymeLabel.setText("jLabel9");

        refSpeciesLabel.setText("jLabel9");

        qrySpeciesLabel.setText("jLabel4");

        jLabel4.setText("Aligner:");

        alignerLabel.setText("jLabel6");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jobNameLabel)
                    .addComponent(enzymeLabel)
                    .addComponent(refSpeciesLabel)
                    .addComponent(qrySpeciesLabel)
                    .addComponent(alignerLabel))
                .addContainerGap(106, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jobNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(enzymeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(refSpeciesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(qrySpeciesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(alignerLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Chromosomes"));

        chromosomeTable.setModel(new javax.swing.table.DefaultTableModel());
        chromosomeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chromosomeTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(chromosomeTable);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel6);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 711, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        genomeViewTabPane.addTab("Genome View", jPanel1);

        jSplitPane2.setDividerLocation(300);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jLabel13.setText("Chromosome:");

        chrLabel.setText("");

        javax.swing.GroupLayout chromosomePanel1Layout = new javax.swing.GroupLayout(chromosomePanel1);
        chromosomePanel1.setLayout(chromosomePanel1Layout);
        chromosomePanel1Layout.setHorizontalGroup(
            chromosomePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        chromosomePanel1Layout.setVerticalGroup(
            chromosomePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 267, Short.MAX_VALUE)
        );

        clearChrAlignmentHighlighted.setText("Clear Highlighted");
        clearChrAlignmentHighlighted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearChrAlignmentHighlightedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chrLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 908, Short.MAX_VALUE)
                        .addComponent(clearChrAlignmentHighlighted))
                    .addComponent(chromosomePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(clearChrAlignmentHighlighted, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13)
                        .addComponent(chrLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chromosomePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane2.setTopComponent(jPanel13);

        jScrollPane4.setBorder(null);

        chrAlignTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        chrAlignTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                chrAlignTableMousePressed(evt);
            }
        });
        jScrollPane4.setViewportView(chrAlignTable);

        jLayeredPane4.setLayer(jScrollPane4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane4.setLayer(jTabbedPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane4Layout = new javax.swing.GroupLayout(jLayeredPane4);
        jLayeredPane4.setLayout(jLayeredPane4Layout);
        jLayeredPane4Layout.setHorizontalGroup(
            jLayeredPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1202, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jLayeredPane4Layout.setVerticalGroup(
            jLayeredPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane4Layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 13, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jSplitPane2.setRightComponent(jLayeredPane4);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane2))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane2))
        );

        genomeViewTabPane.addTab("Chromosome View", jPanel3);

        jLabel12.setText("Chromosome:");

        chrLabel2.setText("");

        javax.swing.GroupLayout queryPanel1Layout = new javax.swing.GroupLayout(queryPanel1);
        queryPanel1.setLayout(queryPanel1Layout);
        queryPanel1Layout.setHorizontalGroup(
            queryPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        queryPanel1Layout.setVerticalGroup(
            queryPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 311, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(queryPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chrLabel2)
                        .addGap(0, 1037, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(chrLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(queryPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder("Query Info"));

        jLabel14.setText("Chromosome size:");

        queryChrSize.setText("");

        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder("Set Range"));

        jLabel15.setText("Increment:");

        jLabel10.setText("Start (bp):");

        jLabel11.setText("End (bp):");

        queryStart.setText("");

        queryEnd.setText("");

        decreaseRange.setText("-");
        decreaseRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decreaseRangeActionPerformed(evt);
            }
        });

        incrementSize.setText("10000");

        increaseRange.setText("+");
        increaseRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                increaseRangeActionPerformed(evt);
            }
        });

        updateRangeButton.setText("Update");
        updateRangeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateRangeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(updateRangeButton))
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(queryStart)
                    .addComponent(queryEnd))
                .addContainerGap())
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addComponent(jLabel11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(decreaseRange)
                        .addGap(18, 18, 18)
                        .addComponent(incrementSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(increaseRange)))
                .addGap(10, 10, 10))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(decreaseRange)
                    .addComponent(incrementSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(increaseRange)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(queryStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(queryEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(updateRangeButton)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Highlight Genes"));

        jButton1.setText("Clear Highlighted Genes");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel6.setText("Select gene from table to highlight ");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(20, 20, 20))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(12, 12, 12)
                        .addComponent(queryChrSize)
                        .addGap(0, 63, Short.MAX_VALUE))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(queryChrSize))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Chromosome Info"));

        variantTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        variantTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                variantTableMousePressed(evt);
            }
        });
        jScrollPane6.setViewportView(variantTable);

        jTabbedPane2.addTab("Indels", jScrollPane6);

        javax.swing.GroupLayout alignmentsOnChromosomeChartPanel1Layout = new javax.swing.GroupLayout(alignmentsOnChromosomeChartPanel1);
        alignmentsOnChromosomeChartPanel1.setLayout(alignmentsOnChromosomeChartPanel1Layout);
        alignmentsOnChromosomeChartPanel1Layout.setHorizontalGroup(
            alignmentsOnChromosomeChartPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 909, Short.MAX_VALUE)
        );
        alignmentsOnChromosomeChartPanel1Layout.setVerticalGroup(
            alignmentsOnChromosomeChartPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 296, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(alignmentsOnChromosomeChartPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(alignmentsOnChromosomeChartPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Alignments", jPanel10);

        geneTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        geneTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                geneTableMousePressed(evt);
            }
        });
        jScrollPane7.setViewportView(geneTable);

        jTabbedPane2.addTab("Genes", jScrollPane7);

        jLayeredPane6.setLayer(jPanel24, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane6.setLayer(jPanel21, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane6.setLayer(jTabbedPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane6Layout = new javax.swing.GroupLayout(jLayeredPane6);
        jLayeredPane6.setLayout(jLayeredPane6Layout);
        jLayeredPane6Layout.setHorizontalGroup(
            jLayeredPane6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 919, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel24, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jLayeredPane6Layout.setVerticalGroup(
            jLayeredPane6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLayeredPane6))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLayeredPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        genomeViewTabPane.addTab("Query View", jPanel5);

        jMenu1.setText("File");

        exitProgram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        exitProgram.setText("Close Window");
        exitProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitProgramActionPerformed(evt);
            }
        });
        jMenu1.add(exitProgram);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem1.setText("Exit Program");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Export");

        jMenu4.setMnemonic('t');
        jMenu4.setText("Table");

        exportAlignmentsTable.setText("Alignments");
        exportAlignmentsTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportAlignmentsTableActionPerformed(evt);
            }
        });
        jMenu4.add(exportAlignmentsTable);

        exportIndelsTable.setText("Indels");
        exportIndelsTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportIndelsTableActionPerformed(evt);
            }
        });
        jMenu4.add(exportIndelsTable);

        exportTranslocationsTable.setText("Translocations");
        exportTranslocationsTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportTranslocationsTableActionPerformed(evt);
            }
        });
        jMenu4.add(exportTranslocationsTable);

        exportGenesTable.setText("Genes");
        exportGenesTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportGenesTableActionPerformed(evt);
            }
        });
        jMenu4.add(exportGenesTable);

        jMenu2.add(jMenu4);

        jMenu3.setMnemonic('i');
        jMenu3.setText("Image");

        exportChrView.setText("Chromosome View");
        exportChrView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportChrViewActionPerformed(evt);
            }
        });
        jMenu3.add(exportChrView);

        exportQryView.setText("Query View");
        exportQryView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportQryViewActionPerformed(evt);
            }
        });
        jMenu3.add(exportQryView);

        exportCircos.setText("Circos View");
        exportCircos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportCircosActionPerformed(evt);
            }
        });
        jMenu3.add(exportCircos);

        jMenu2.add(jMenu3);

        jMenu7.setText("Graph");

        exportGraphAlignments.setText("Alignments Across Genome");
        exportGraphAlignments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportGraphAlignmentsActionPerformed(evt);
            }
        });
        jMenu7.add(exportGraphAlignments);

        exportGraphSV.setText("SVs Across Genome");
        exportGraphSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportGraphSVActionPerformed(evt);
            }
        });
        jMenu7.add(exportGraphSV);

        jMenu2.add(jMenu7);

        jMenuBar1.add(jMenu2);

        jMenu5.setText("View");

        changePlotStyle.setText("Change Plot Style");
        changePlotStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePlotStyleActionPerformed(evt);
            }
        });
        jMenu5.add(changePlotStyle);

        jMenuBar1.add(jMenu5);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(genomeViewTabPane)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(genomeViewTabPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 764, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleDescription("Comparative Genomics View");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void exitProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitProgramActionPerformed
        this.setVisible(false);
        CompGenStart view = new CompGenStart();
        view.setVisible(true);
    }//GEN-LAST:event_exitProgramActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void exportChrViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportChrViewActionPerformed
        exportImage(this.chromosomePanel1);
    }//GEN-LAST:event_exportChrViewActionPerformed

    private void exportQryViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportQryViewActionPerformed
        exportImage(this.queryPanel1);
    }//GEN-LAST:event_exportQryViewActionPerformed

    private void changePlotStyleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePlotStyleActionPerformed
        this.changePlotStyleDialog.setVisible(true);
    }//GEN-LAST:event_changePlotStyleActionPerformed

    private void exportAlignmentsTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportAlignmentsTableActionPerformed
        exportTables(this.chrAlignTable);
    }//GEN-LAST:event_exportAlignmentsTableActionPerformed

    private void exportIndelsTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportIndelsTableActionPerformed
        exportTables(this.variantTable);
    }//GEN-LAST:event_exportIndelsTableActionPerformed

    private void exportGenesTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportGenesTableActionPerformed
        exportTables(this.geneTable);
    }//GEN-LAST:event_exportGenesTableActionPerformed

    private void exportTranslocationsTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportTranslocationsTableActionPerformed
        exportTables(this.translocationTable);
    }//GEN-LAST:event_exportTranslocationsTableActionPerformed

    private void exportCircosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportCircosActionPerformed
        exportImage(this.circosPanel1);
    }//GEN-LAST:event_exportCircosActionPerformed

    private void exportGraphAlignmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportGraphAlignmentsActionPerformed
        exportImage(this.alignmentsPerChromosomeChartPanel1);
    }//GEN-LAST:event_exportGraphAlignmentsActionPerformed

    private void exportGraphSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportGraphSVActionPerformed
        exportImage(this.chromosomeChartPanel1);
    }//GEN-LAST:event_exportGraphSVActionPerformed

    private void genomeViewTabPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_genomeViewTabPaneStateChanged

    }//GEN-LAST:event_genomeViewTabPaneStateChanged

    private void geneTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_geneTableMousePressed
        JTable source = (JTable) evt.getSource();
        int row = source.rowAtPoint(evt.getPoint());
        this.queryPanel1.highlightGene(genes.get(row));
        this.queryPanel1.repaint();
    }//GEN-LAST:event_geneTableMousePressed

    private void variantTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_variantTableMousePressed
        JTable source = (JTable) evt.getSource();
        int row = source.rowAtPoint(evt.getPoint());
        Indel indel = this.indels.get(row);

        String s = indel.getStart().toString();
        String e = indel.getEnd().toString();
        //        to prevent numberformatexception being raised if a large integer
        BigDecimal start = new BigDecimal(s);
        BigDecimal end = new BigDecimal(e);
        this.queryEnd.setText(start.toString());
        this.queryStart.setText(end.toString());
        this.queryPanel1.repaint();
    }//GEN-LAST:event_variantTableMousePressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.queryPanel1.clearHighlightedGenes();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void updateRangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateRangeButtonActionPerformed

        this.queryPanel1.setRange(Integer.valueOf(this.queryStart.getText()), Integer.valueOf(this.queryEnd.getText()));

        this.geneTable.setAutoCreateRowSorter(true);
        DefaultTableModel geneTableModel = (DefaultTableModel) this.geneTable.getModel();

        geneTableModel.setRowCount(0);
        geneTableModel.setColumnCount(0);

        String[] geneColumnNames = {"Name", "Source", "Start", "End", "Size"};
        geneTableModel.setColumnIdentifiers(geneColumnNames); //Set the column names of this table

        Integer start = this.queryPanel1.getStart();
        Integer end = this.queryPanel1.getEnd();

        for (Gene gene : this.currentChr.getAnnotations()) {
            Double genStrt = gene.getStart();
            Double genEnd = gene.getEnd();
            if (((genStrt.intValue() >= start) && (genStrt.intValue() <= end)) | (genEnd <= end && genEnd >= start)) {
                Matcher checkGene = Pattern.compile("gene").matcher(gene.getType());
                if (checkGene.find() == true) {
                    Double size = (gene.getEnd() - gene.getStart());
                    String[] geneData = {gene.getName(), gene.getSource(), gene.getStart().toString(), gene.getEnd().toString(), size.toString()};
                    geneTableModel.addRow(geneData);
                    genes.add(gene);
                }
            }
        }
    }//GEN-LAST:event_updateRangeButtonActionPerformed

    private void increaseRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_increaseRangeActionPerformed
        String increment = this.incrementSize.getText();
        if (Integer.valueOf(this.queryEnd.getText()) + Integer.valueOf(increment) < this.currentChr.getSize()) {
            this.queryStart.setText(String.valueOf(Integer.valueOf(this.queryStart.getText()) + Integer.valueOf(increment)));
            this.queryEnd.setText(String.valueOf(Integer.valueOf(this.queryEnd.getText()) + Integer.valueOf(increment)));
        }
    }//GEN-LAST:event_increaseRangeActionPerformed

    private void decreaseRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decreaseRangeActionPerformed
        String increment = this.incrementSize.getText();
        if (Integer.valueOf(this.queryStart.getText()) > Integer.valueOf(increment)) {
            this.queryStart.setText(String.valueOf(Integer.valueOf(this.queryStart.getText()) - Integer.valueOf(increment)));
        }
    }//GEN-LAST:event_decreaseRangeActionPerformed

    private void chrAlignTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chrAlignTableMousePressed
        JTable source = (JTable) evt.getSource();
        int row = source.rowAtPoint(evt.getPoint());
        try {
            XmapData map = currentChr.getAlignments().get(row);
            if (map != null) {
                this.chromosomePanel1.selectAlignment(map.getID());
            } else {
                JOptionPane.showMessageDialog(null,
                        "There are no alignments to this chromosome",
                        "No alignments",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NullPointerException e) {
            System.out.println("Map for row " + row + " is null :(");
        }
    }//GEN-LAST:event_chrAlignTableMousePressed

    private void clearChrAlignmentHighlightedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearChrAlignmentHighlightedActionPerformed
        this.chromosomePanel1.clearSelection();
    }//GEN-LAST:event_clearChrAlignmentHighlightedActionPerformed

    private void chromosomeTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chromosomeTableMouseClicked

        JTable source = (JTable) evt.getSource();
        // Get the number of the selected chromosome
        int row = source.rowAtPoint(evt.getPoint()) + 1;
        chooseChromosome(row);
        this.genomeViewTabPane.setSelectedIndex(1);
    }//GEN-LAST:event_chromosomeTableMouseClicked

    private void setChartStyleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setChartStyleActionPerformed
        if (this.ggplotButton.isSelected()) {
            changePlotStyles("ggplot");
        }
        if (this.matlabButton.isSelected()) {
            changePlotStyles("matlab");
        }
        if (this.xchartButton.isSelected()) {
            changePlotStyles("xchart");
        }
    }//GEN-LAST:event_setChartStyleActionPerformed
    private void changePlotStyles(String style) {
        if ("ggplot".equals(style)) {
            this.alignmentsOnChromosomeChartPanel1.setStyle(Styler.ChartTheme.GGPlot2);
            this.alignmentsOnChromosomeChartPanel1.repaint();
            this.alignmentsPerChromosomeChartPanel1.setStyle(Styler.ChartTheme.GGPlot2);
            this.alignmentsPerChromosomeChartPanel1.repaint();
            this.chromosomeChartPanel1.setStyle(Styler.ChartTheme.GGPlot2);
            this.chromosomeChartPanel1.repaint();
        }
        if ("matlab".equals(style)) {
            this.alignmentsOnChromosomeChartPanel1.setStyle(Styler.ChartTheme.Matlab);
            this.alignmentsOnChromosomeChartPanel1.repaint();
            this.alignmentsPerChromosomeChartPanel1.setStyle(Styler.ChartTheme.Matlab);
            this.alignmentsPerChromosomeChartPanel1.repaint();
            this.chromosomeChartPanel1.setStyle(Styler.ChartTheme.Matlab);
            this.chromosomeChartPanel1.repaint();
        }
        if ("xchart".equals(style)) {
            this.alignmentsOnChromosomeChartPanel1.setStyle(Styler.ChartTheme.XChart);
            this.alignmentsOnChromosomeChartPanel1.repaint();
            this.alignmentsPerChromosomeChartPanel1.setStyle(Styler.ChartTheme.XChart);
            this.alignmentsPerChromosomeChartPanel1.repaint();
            this.chromosomeChartPanel1.setStyle(Styler.ChartTheme.XChart);
            this.chromosomeChartPanel1.repaint();
        }
    }

    private void chooseChromosome(Integer x) {
        genes.clear();
//       this.alignmentsOnChromosomeChartPanel1 = new AlignmentsOnChromosomeChartPanel();
//       this.alignmentsPerChromosomeChartPanel1 = new AlignmentsPerChromosomeChartPanel();
        this.chromosomeChartPanel1 = new ChromosomeChartPanel();

        currentChr = this.alignment.getRefGenome().getChromosomes().get(x);

        if (currentChr != null) {
//            this.alignmentsOnChromosomeChartPanel1.plotCounts(currentChr);
            chrLabel.setText(currentChr.getName());
            chrLabel2.setText(currentChr.getName());
            this.queryPanel1.setChr(currentChr);
            this.queryStart.setText(this.queryPanel1.getStart().toString());
            this.queryEnd.setText(this.queryPanel1.getEnd().toString());
            this.chromosomePanel1.setChr(currentChr);

//          next need to update the sites table
            DefaultTableModel sitesTableModel = (DefaultTableModel) this.chrAlignTable.getModel();
            this.chrAlignTable.setAutoCreateRowSorter(true);
//          First clear the chromosomes JTable of any previous data
            sitesTableModel.setRowCount(0);
            sitesTableModel.setColumnCount(0);

            String[] columnNames = {"Alignment #", "Query Chr", "Number matched Sites", "Start", "End", "Size", "SV detected"};
            //the data is within the third line and beyond for gff files
            sitesTableModel.setColumnIdentifiers(columnNames); //Set the column names of this table

            Integer count = 0;
            for (XmapData map : currentChr.getAlignments()) {
                String sv;
                if (map.numIndels() > 0) {
                    sv = "true";
                } else {
                    sv = "false";
                }
                count += 1;
                String n = map.getQryID().toString();

                String[] chrData = {String.valueOf(count), n, String.valueOf(map.returnAlignments().size()), String.valueOf(map.getRefStart()), String.valueOf(map.getRefEnd()), String.valueOf(map.getRefEnd() - map.getRefStart()), sv};
                sitesTableModel.addRow(chrData);
            }
            BigDecimal chrSize = new BigDecimal(currentChr.getSize());
            this.queryChrSize.setText(chrSize.toPlainString());
            this.queryStart.setText(this.queryPanel1.getStart().toString());
            this.queryEnd.setText(this.queryPanel1.getEnd().toString());
            //System.out.println("query start and end set " + this.queryPanel1.getStart() + " " + this.queryPanel1.getEnd());
////         First clear the chromosomes JTable of any previous data
            this.geneTable.setAutoCreateRowSorter(true);
            DefaultTableModel geneTableModel = (DefaultTableModel) this.geneTable.getModel();

            geneTableModel.setRowCount(0);
            geneTableModel.setColumnCount(0);

            String[] geneColumnNames = {"Name", "Source", "Start", "End", "Size"};
            geneTableModel.setColumnIdentifiers(geneColumnNames); //Set the column names of this table

            Integer start = this.queryPanel1.getStart();
            Integer end = this.queryPanel1.getEnd();
            // Returns the features from gff / gtf file
            try {
                for (Gene gene : this.currentChr.getAnnotations()) {
                    System.out.println("Gene in CompGenView l539: " + gene.getName() + " " + gene);
                    Double genStrt = gene.getStart();
                    Double genEnd = gene.getEnd();
                    if (((genStrt.intValue() >= start) && (genStrt.intValue() <= end)) | (genEnd <= end && genEnd >= start)) {
                        Matcher checkGene = Pattern.compile("gene").matcher(gene.getType());
                        if (checkGene.find() == true) {
                            System.out.println("Check gene type in CompGenView l545: " + checkGene.find());
                            Double size = (gene.getEnd() - gene.getStart());
                            String[] geneData = {gene.getName(), gene.getSource(), gene.getStart().toString(), gene.getEnd().toString(), size.toString()};
                            geneTableModel.addRow(geneData);

                            genes.add(gene);
                        }
                    }
                }
            }
            catch (NullPointerException e) {
                //System.out.println(this.currentChr.getAnnotations());
                System.out.println("Chromosome " + this.currentChr.getName() + " is not annotated.");
            }

//        
            DefaultTableModel variantTableModel = (DefaultTableModel) this.variantTable.getModel();
            variantTableModel.setRowCount(0);
            variantTableModel.setColumnCount(0);
            this.variantTable.setAutoCreateRowSorter(true);
            String[] columnNames2 = {"Chr", "Variant Type", "Query ID", "Size", "Number Genes"};
            variantTableModel.setColumnIdentifiers(columnNames2); //Set the column names of this table

            int c = 0;

            for (Indel indel : currentChr.getIndels()) {
                indels.add(indel);
                String[] chrData = {currentChr.getName(), indel.getType(),
                    indel.getQryID().toString(), indel.getSize().toString(),
                    indel.numberGenes().toString()};
                variantTableModel.addRow(chrData);
            }
            c++;
        }

    }

    /**
     * @author Josephine Burgin
     * @param saveTable
     */
    private void exportTables(JTable saveTable) {
        // Table to be exported to CSV
        if (saveTable.getRowCount() != 0) {
            // Saves Query Contig table from Reference View to CSV output
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Enter File Name");

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                // Uses CSV Writer class to write to user defined file
                CSVWriter tableContents;
                try {
                    if (fileToSave.getPath().endsWith(".csv")) {
                        tableContents = new CSVWriter(new FileWriter(fileToSave));
                    } else {
                        tableContents = new CSVWriter(new FileWriter(fileToSave + ".csv"));
                    }

                    // A string array for table headers
                    String[] header = new String[saveTable.getColumnCount()];

                    // A string array for table output
                    String[] qryOut = new String[saveTable.getColumnCount()];
                    // Check that table is populated

                    // Get column names and add as CSV header
                    for (int name = 0; name < saveTable.getColumnCount(); name++) {
                        header[name] = saveTable.getColumnName(name);
                    }
                    tableContents.writeNext(header);

                    for (int i = 0; i < saveTable.getRowCount(); i++) {
                        // Nest for loops to take values from table and add to CSV file
                        for (int j = 0; j < saveTable.getColumnCount(); j++) {

                            qryOut[j] = saveTable.getValueAt(i, j).toString();

                        }

                        tableContents.writeNext(qryOut);
                    }
                    // Close CSV file
                    tableContents.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Table is not populated please select "
                    + "a reference", "Error in Export Table", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @author Joesphine Burgin
     * @param panel
     */
    private void exportImage(JPanel panel) {
        FileDialog fileBox;
        fileBox = new FileDialog(this, "Save PDF of reference alignment view", FileDialog.SAVE);
        fileBox.setVisible(true);

        if (fileBox.getFile() != null) {
            String chosenPath = fileBox.getDirectory();
            String chosenFile = fileBox.getFile();

            try {
                PDFDocument doc = new PDFDocument();

                // Use a Paper instance to change page dimensions, some plots can be long
                Paper p = new Paper();
                p.setSize(panel.getWidth(), panel.getHeight());
                p.setImageableArea(0, 0, panel.getWidth(), panel.getHeight());
                PageFormat pf = new PageFormat();
                pf.setPaper(p);
                PDFPage page = doc.createPage(pf);
                doc.addPage(page);
                // Directly paint the panel to the pdf page
                Graphics2D g2d = page.createGraphics();
                panel.paint(g2d);

                doc.saveDocument(chosenPath + chosenFile + ".pdf");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error saving image to pdf file", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(null, "No filename given", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel alignerLabel;
    private ComparativeGenomics.Panels.AlignmentsOnChromosomeChartPanel alignmentsOnChromosomeChartPanel1;
    private ComparativeGenomics.Panels.AlignmentsPerChromosomeChartPanel alignmentsPerChromosomeChartPanel1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenuItem changePlotStyle;
    private javax.swing.JDialog changePlotStyleDialog;
    private javax.swing.JTable chrAlignTable;
    private javax.swing.JLabel chrLabel;
    private javax.swing.JLabel chrLabel2;
    private ComparativeGenomics.Panels.ChromosomeChartPanel chromosomeChartPanel1;
    private ComparativeGenomics.Panels.ChromosomePanel chromosomePanel1;
    private javax.swing.JTable chromosomeTable;
    private ComparativeGenomics.Panels.CircosPanel circosPanel1;
    private javax.swing.JButton clearChrAlignmentHighlighted;
    private javax.swing.JButton decreaseRange;
    private javax.swing.JLabel enzymeLabel;
    private javax.swing.JMenuItem exitProgram;
    private javax.swing.JMenuItem exportAlignmentsTable;
    private javax.swing.JMenuItem exportChrView;
    private javax.swing.JMenuItem exportCircos;
    private javax.swing.JMenuItem exportGenesTable;
    private javax.swing.JMenuItem exportGraphAlignments;
    private javax.swing.JMenuItem exportGraphSV;
    private javax.swing.JMenuItem exportIndelsTable;
    private javax.swing.JMenuItem exportQryView;
    private javax.swing.JMenuItem exportTranslocationsTable;
    private javax.swing.JTable geneTable;
    private ComparativeGenomics.Panels.GenomePanel genomePanel1;
    private javax.swing.JTabbedPane genomeViewTabPane;
    private javax.swing.JRadioButton ggplotButton;
    private javax.swing.JButton increaseRange;
    private javax.swing.JTextField incrementSize;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane4;
    private javax.swing.JLayeredPane jLayeredPane6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JLabel jobNameLabel;
    private javax.swing.JRadioButton matlabButton;
    private javax.swing.JDialog parsingDialog;
    private javax.swing.JLabel qrySpeciesLabel;
    private javax.swing.JLabel queryChrSize;
    private javax.swing.JTextField queryEnd;
    private ComparativeGenomics.Panels.QueryPanel queryPanel1;
    private javax.swing.JTextField queryStart;
    private javax.swing.JLabel refSpeciesLabel;
    private javax.swing.JDialog savePanelDialog;
    private javax.swing.JDialog saveTableDialog;
    private javax.swing.JButton setChartStyle;
    private javax.swing.JTable translocationTable;
    private javax.swing.JButton updateRangeButton;
    private javax.swing.JTable variantTable;
    private javax.swing.JRadioButton xchartButton;
    // End of variables declaration//GEN-END:variables
}
