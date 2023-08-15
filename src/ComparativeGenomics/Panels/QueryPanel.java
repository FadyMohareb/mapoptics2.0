package ComparativeGenomics.Panels;

import ComparativeGenomics.FileHandling.DataHandling.Chromosome;
import ComparativeGenomics.FileHandling.DataHandling.Site;
import ComparativeGenomics.Drawing.XmapShape;
import ComparativeGenomics.Drawing.MapOpticsRectangle;
import ComparativeGenomics.Drawing.QueryShape;
import ComparativeGenomics.FileHandling.DataHandling.XmapData;
import ComparativeGenomics.FileHandling.DataHandling.CmapData;
import ComparativeGenomics.FileHandling.DataHandling.Gene;
import ComparativeGenomics.FileHandling.DataHandling.Match;
import ComparativeGenomics.FileHandling.DataHandling.Pair;
import ComparativeGenomics.FileHandling.Xmap;
import ComparativeGenomics.StructuralVariant.Indel;
import ComparativeGenomics.StructuralVariant.Translocation;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author franpeters
 */
public class QueryPanel extends javax.swing.JPanel implements MouseListener, MouseMotionListener {

    /**
     * Creates new form QueryPanel this panel will view the alignments of a
     * query for a chromosome. will need to show the chr with the bands then the
     * sites on the chr with matching sites the same colour then the gene
     * annotation as well
     *
     */
    private Xmap xmap;
    private Chromosome chr = null;
    private String refOrganism;

    private final Font chrFont = new Font("Arial", 1, 8);
    private Integer start = 0;
    private Integer end = 9000000;
    private Integer size = end - start;

    private final Integer startX = 25;
    private final Integer startY = 10;
    private boolean alignment = false;

    private HashMap<Integer, Double> refSitesRelPos = new HashMap(); //key ref Site ID
    private HashMap<Integer, Site> xmapPositions = new HashMap(); //key xmapID

    private ArrayList<Indel> indels = new ArrayList();
    private ArrayList<Gene> genesInRange = new ArrayList();

    private ArrayList<MapOpticsRectangle> geneRects = new ArrayList();
    private ArrayList<QueryShape> alignShapes = new ArrayList();

    private Integer w;
    private Integer h;
    private Point point;
    private boolean pressed = false;
    private Integer dx = 0;
    private Integer dy = 0;
    private Integer y = 0;
    private Double yPos = y.doubleValue();
    private Double relSize = 0.0;
    private Double qryWidth = 0.0;

    private Integer draggedShape = null;
    private Gene selectedGene;

    public QueryPanel() {
        this.setBackground(Color.white);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        initComponents();
    }

    public void setRefOrg(String org) {
        this.refOrganism = org;
    }

    public void setXmap(Xmap xmap) {
        this.xmap = xmap;
    }

    public void setChr(Chromosome chr) {
        //Reset th array list
        reset();
        if (chr != null) {
            this.chr = chr;
            findSites();

        } else {
            System.out.println("Query panel chr is null");
        }
        w = this.getWidth();
        h = this.getHeight();
        qryWidth = w.doubleValue() - (2 * startX);
    }

    public void findSites() {
        relSize = qryWidth / this.size;
//        get the sites within the range and record where they should be drawn on the panel in relsites relpos hashmap
        for (HashMap.Entry<Integer, Site> entry : this.chr.getRefSites().entrySet()) {
            Site site = entry.getValue(); // Digestion site
            Double position = site.getPosition();
            // Position of site is within the range
            if (position.intValue() > this.start && position.intValue() < this.end) {
                // Calculate relative position of the reference
                Double relPosRef = ((site.getPosition() - start) * relSize) + startX.doubleValue();
                this.refSitesRelPos.put(site.getSiteID(), relPosRef); // Save relative position into a hashmap, the key is the site ID
//                    System.out.println("REF "+ site.getSiteID()+ " "+ relPosRef +" "+ chr.getCmapID());
//                    System.out.println(site.getSiteID()+" "+ site.getPosition()+ " " + relPosRef);
                if (site.isMatch()) { // Other sites matche ie have the same ID in XMAP file
                    for (HashMap.Entry<Integer, ArrayList<Match>> matches : site.getMatches().entrySet()) {
                        if (matches.getValue().size() == 1) {
//                                only add sites that have been matched to this xmapID once
                            if (!this.xmapPositions.containsKey(matches.getKey())) {
                                this.xmapPositions.put(matches.getKey(), site);
//                                System.out.println(matches.getKey()+ " xmap iD " + site.getSiteID()+ " site id");
                            }

                        }
                    }
                }
            }
        }
        // For every XMAP position
        for (HashMap.Entry<Integer, Site> entry : this.xmapPositions.entrySet()) {
            Integer xmapID = entry.getKey();
            Site site = entry.getValue();

            Integer siteID = site.getSiteID();

            XmapData map = this.xmap.getXmapByXmapID(xmapID);
            Double sitePosPx = this.refSitesRelPos.get(siteID);

            Double relFirstPosRef = 0.0;
            if (map.getOri()) {
                Double mapFirstSite = map.returnAlignments()
                        .get(0)
                        .getRefSite().getPosition();
                Double sizeDiffPx = (site.getPosition() - mapFirstSite) * relSize;
                relFirstPosRef = sitePosPx - sizeDiffPx;

            }
            if (!map.getOri()) {
                Double mapLastSite = map.returnAlignments()
                        .get(map.returnAlignments().size() - 1)
                        .getRefSite().getPosition();
                Double sizeDiffPx = (site.getPosition() - mapLastSite) * relSize;
                relFirstPosRef = sitePosPx - sizeDiffPx;
            }

            QueryShape shape = new QueryShape(refSitesRelPos, relFirstPosRef,
                    220.0,
                    relSize,
                    map,
                    this.chr.getQryCmapsByID(map.getQryID()),
                    this.start * relSize);
            alignShapes.add(shape);

            // Check that the query ID exists in the list of cmaps of the chromosome
            // The query ID is the cmap ID saved in the XMAP file for this chr
            if (this.chr.getQryCmapsByID(map.getQryID()) != null) {
                /*System.out.println("Shape coords:" + map.getID() + " " + this.chr.getQryCmapsByID(map.getQryID()).getID() + " "
                        + relFirstPosRef + " "
                        + relSize + " "
                        + start + " "
                        + end);
                */
            } else {
                System.out.println("Query CMAP does not exist for the following ID from XMAP file: " + map.getQryID());
            }

        }
        for (Indel indel : this.chr.getIndels()) {
            if ((indel.getStart() > start | indel.getStart() < end) & (indel.getEnd() > start | indel.getEnd() > end)) {
                indels.add(indel);
            }
        }
        this.repaint();
    }

    public void setRange(Integer start, Integer end) {
//        reset the arraylists
        reset();
        if (start < end) {
            if (start >= 0 && start < this.chr.getSize()) {
                this.start = start;
            }
            if (end > 0 && end <= this.chr.getSize() && end > start) {
                this.end = end;
            }
            this.size = this.end - this.start;

        } else {
            System.out.println("Start cannot be more than end");
        }
        findSites();
    }

    private void drawQuery(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(chrFont);
        if (chr == null) {
            drawTextCentre(g2d, "No Chromosome selected, please select a chromosome", this.getWidth(), this.getHeight());
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
            MapOpticsRectangle ref = new MapOpticsRectangle(startX, 70, qryWidth, 50);
            g2d.draw(ref);
            g2d.fill(ref);
            for (HashMap.Entry<Integer, Double> entry : this.refSitesRelPos.entrySet()) {
                Integer siteID = entry.getKey();
                Double relPosRef = entry.getValue();
                Site site = this.chr.getRefSites().get(siteID);
//                if(!site.isMatch()){
                g2d.setColor(Color.black);
//                }  
//                if (site.isMatch()){
//                    g2d.setColor(Color.blue);
//                }
                Shape r = new Line2D.Double(relPosRef, 70, relPosRef, 120);
                g2d.draw(r);
            }

            drawScaleBar(g2d, ref);
            drawGenes(this.chr.getAnnotations(), g2d);
            drawAlignments(g2d);
            drawIndels(g2d);
        }
    }

    private void drawAlignments(Graphics2D g2d) {
        Integer count = 0;
        for (QueryShape s : alignShapes) {
//          Set the colour
            Color colour = setAlignmentColour(count);
            s.setRectColour(Color.LIGHT_GRAY);
            s.drawRect(g2d);
            count += 1;
        }
//        try{QueryShape s = alignShapes.get(0);
//        s.setRectColour(Color.LIGHT_GRAY);
//            s.drawRect(g2d);
////            count +=1;   
//        }catch(IndexOutOfBoundsException ex){
//            System.out.println("no alignments");
//        }
    }

    private void drawGenes(ArrayList<Gene> genes, Graphics2D g2d) {
        if (genes != null) {
            for (Gene gene : genes) {
                Double genStrt = gene.getStart();
                Double genEnd = gene.getEnd();

                if (((genStrt.intValue() >= this.start) && (genStrt.intValue() <= this.end)) | (genEnd <= this.end && genEnd >= this.start)) {
                    Matcher checkGene = Pattern.compile("gene").matcher(gene.getType());
                    if (checkGene.find() == true) {
                        this.genesInRange.add(gene);
                    }
                }
            }
            int count = 1;
            for (Gene gene : this.genesInRange) {
                Double relStart = (gene.getStart() - start + startX) * relSize;
                if (relStart > 25.0) {
                    Double relEnd = gene.getEnd() * relSize;
                    Double gSize = relEnd - relStart;
                    MapOpticsRectangle geneRect = new MapOpticsRectangle(relStart, 35, gSize, 25);
                    geneRect.setGene(gene);
                    geneRects.add(geneRect);
                    Shape geneShape = geneRect;
                    if (gene.getSelected()) {
                        g2d.setStroke(new BasicStroke(2));
                        g2d.setColor(Color.red);
                        g2d.draw(geneShape);
                        g2d.setStroke(new BasicStroke(1));
                    }

                    if (!gene.getSelected()) {
                        g2d.setColor(Color.DARK_GRAY);
                        g2d.draw(geneShape);
                    }

                    count++;
                }
            }
        }
    }

    private void drawIndels(Graphics2D g2d) {
        for (Indel indel : indels) {
            Double indelStart = (indel.getStart() - start) * relSize + startX;
//            System.out.println("indel start: " + indel.getStart()+ " x pos" + indelStart + " relSize "+ relSize);
            Double indelSize = indel.getSize() * relSize;
            if (indel.getType().equals("Insertion")) {
                g2d.setColor(Color.green);
            }
            if (indel.getType().equals("Deletion")) {
                g2d.setColor(Color.red);
            }
            MapOpticsRectangle rect = new MapOpticsRectangle(indelStart, 280, indelSize, 20);
            g2d.draw(rect);
            g2d.fill(rect);
        }
    }

    private void drawScaleBar(Graphics2D g2d, MapOpticsRectangle refRect) {
        g2d.setColor(Color.black);
        g2d.drawLine((int) refRect.getMinX(), startY, (int) (refRect.getMinX() + refRect.getWidth()), startY);

        int numScales = (int) refRect.getWidth() / 100;
        double scaleIncrement = (double) size / numScales;
        double length = size;
        g2d.setFont(chrFont);
        String s;
        String e;
        if (start == 0) {
            s = "0";
        } else {
            s = String.format("%.2f", (double) start / 1000);

        }
        e = String.format("%.2f", (double) end / 1000);
        g2d.drawString(s + " kb", (int) (refRect.getMinX() - g2d.getFontMetrics().stringWidth(s + " kb") / 2), startY + 15);
        g2d.drawLine((int) (refRect.getMinX() + (refRect.getWidth() / numScales) * 0), startY, (int) (refRect.getMinX() + (refRect.getWidth() / numScales) * 0), startY + 5);
        g2d.drawString(e + " kb", (int) (refRect.getMinX() + refRect.getWidth() - g2d.getFontMetrics().stringWidth(e + " kb") / 2), startY + 15);
        g2d.drawLine((int) (refRect.getMinX() + (refRect.getWidth() / numScales) * 0), startY, (int) (refRect.getMinX() + (refRect.getWidth() / numScales) * 100), startY + 5);
        if (numScales != 0) {
            for (int i = 1; i < numScales; i++) {
                g2d.drawLine((int) (refRect.getMinX() + (refRect.getWidth() / numScales) * i), startY, (int) (refRect.getMinX() + (refRect.getWidth() / numScales) * i), startY + 5);
                g2d.drawString(String.format("%.2f", ((double) (start + (scaleIncrement * i)) / 1000)) + " kb", (int) (refRect.getMinX() + ((refRect.getWidth() / numScales) * i) - g2d.getFontMetrics().stringWidth(String.format("%.2f", ((double) (start + (scaleIncrement * i)) / 1000)) + " kb") / 2), startY + 15);
            }
        }
    }

    private Color setAlignmentColour(Integer Int) {
        Color[] colours = {Color.decode("#3792ff"), Color.decode("#20cdf5"), Color.decode("#8ef2f4"), Color.decode("#f179a7"), Color.decode("#fcc5f1")};
        Integer number = colours.length;

        if (Int > number) {
            if (Int % number == 0 && ((Int / number) < 5)) {
                return colours[Int / number];
            } else {
                Double newInt = (Int - 5 * Math.floor((Int / number)));
                return colours[newInt.intValue()];
            }
        }
        if (Int < number) {
            return colours[Int];
        } else {
            System.out.println(Int);
            return Color.MAGENTA;
        }
    }

    private void drawTextCentre(Graphics2D g2d, String string,
            Integer width, Integer height) {
        int stringWidth = (int) g2d.getFontMetrics().getStringBounds(string, g2d).getWidth();
        int stringHeight = (int) g2d.getFontMetrics().getStringBounds(string, g2d).getHeight();

        int horizontalCenter = width / 2 - stringWidth / 2;
        int verticalCenter = height / 2 - stringHeight / 2;
        g2d.drawString(string, horizontalCenter, verticalCenter);
    }

    public Integer getStart() {
        return this.start;
    }

    public Integer getEnd() {
        return this.end;
    }

    public Integer getRange() {
        return this.size;
    }

    public ArrayList<Gene> getGenes() {
        return this.genesInRange;
    }

    public void highlightGene(Gene gene) {
        System.out.println(gene.getName() + " highlighted");
        for (Gene g : this.genesInRange) {
            if (g.getName().equals(gene.getName())) {
                g.setSelected(true);
            }
        }
        repaint();
    }

    public void clearHighlightedGenes() {
        for (Gene g : this.genesInRange) {
            g.setSelected(false);
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawQuery(g);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        geneDialog = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        geneName = new javax.swing.JLabel();
        geneSize = new javax.swing.JLabel();
        geneSource = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        queryDBButton = new javax.swing.JButton();
        clinVarButton = new javax.swing.JRadioButton();
        dbVarButton = new javax.swing.JRadioButton();
        buttonGroup1 = new javax.swing.ButtonGroup();
        databaseResult = new javax.swing.JDialog();
        jScrollPane1 = new javax.swing.JScrollPane();
        databaseTable = new javax.swing.JTable();
        exportDBResult = new javax.swing.JButton();

        geneDialog.setBounds(new java.awt.Rectangle(300, 300, 283, 120));
        geneDialog.setLocation(new java.awt.Point(300, 300));
        geneDialog.setMinimumSize(new java.awt.Dimension(249, 160));
        geneDialog.setPreferredSize(new java.awt.Dimension(249, 165));

        jLabel1.setText("Gene Name:");

        jLabel2.setText("Gene Size:");

        jLabel3.setText("Source:");

        geneName.setText("jLabel4");

        geneSize.setText("jLabel5");

        geneSource.setText("jLabel6");

        jLabel7.setText("bp");

        queryDBButton.setText("Query DB");
        queryDBButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queryDBButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(clinVarButton);
        clinVarButton.setSelected(true);
        clinVarButton.setText("ClinVar");

        buttonGroup1.add(dbVarButton);
        dbVarButton.setText("DBVar");

        javax.swing.GroupLayout geneDialogLayout = new javax.swing.GroupLayout(geneDialog.getContentPane());
        geneDialog.getContentPane().setLayout(geneDialogLayout);
        geneDialogLayout.setHorizontalGroup(
            geneDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(geneDialogLayout.createSequentialGroup()
                .addGroup(geneDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(geneDialogLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(geneDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)))
                    .addGroup(geneDialogLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(clinVarButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dbVarButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(geneDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(geneName)
                    .addGroup(geneDialogLayout.createSequentialGroup()
                        .addComponent(geneSize)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7))
                    .addComponent(geneSource)
                    .addGroup(geneDialogLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(queryDBButton))))
        );
        geneDialogLayout.setVerticalGroup(
            geneDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(geneDialogLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(geneDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(geneName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(geneDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(geneSize, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(geneDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(geneDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(geneSource))
                .addGap(18, 18, 18)
                .addGroup(geneDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(queryDBButton)
                    .addComponent(clinVarButton)
                    .addComponent(dbVarButton))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        databaseResult.setTitle("DB Result");
        databaseResult.setBounds(new java.awt.Rectangle(400, 300, 500, 500));

        databaseTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(databaseTable);

        exportDBResult.setText("Export");

        javax.swing.GroupLayout databaseResultLayout = new javax.swing.GroupLayout(databaseResult.getContentPane());
        databaseResult.getContentPane().setLayout(databaseResultLayout);
        databaseResultLayout.setHorizontalGroup(
            databaseResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, databaseResultLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(exportDBResult)
                .addContainerGap())
        );
        databaseResultLayout.setVerticalGroup(
            databaseResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databaseResultLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exportDBResult)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 918, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 302, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void queryDBButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queryDBButtonActionPerformed
        ArrayList<ArrayList<String>> result = new ArrayList();
        if (this.clinVarButton.isSelected()) {
            result = this.selectedGene.queryDB("clinvar", this.refOrganism);
        }
        if (this.dbVarButton.isSelected()) {
            result = this.selectedGene.queryDB("dbvar", this.refOrganism);
        }
        DefaultTableModel dbTableModel = (DefaultTableModel) this.databaseTable.getModel();
        this.databaseTable.setAutoCreateRowSorter(true);
//         First clear the chromosomes JTable of any previous data
        dbTableModel.setRowCount(0);
        dbTableModel.setColumnCount(0);

        String[] columnNames = {"ID", "SV ID", "Clinical Significance"};
        dbTableModel.setColumnIdentifiers(columnNames); //Set the column names of this table

        for (ArrayList<String> res : result) {
            String[] array = res.toArray(new String[res.size()]);
            System.out.println(array.toString());
            dbTableModel.addRow(array);
        }
        if (this.clinVarButton.isSelected()) {
            databaseResult.setTitle("Results for " + this.selectedGene.getName() + " ClinVar");
        }
        if (this.dbVarButton.isSelected()) {
            databaseResult.setTitle("Results for " + this.selectedGene.getName() + " dbVar");
        }

        databaseResult.setVisible(true);

    }//GEN-LAST:event_queryDBButtonActionPerformed
    private void reset() {

        this.alignShapes = new ArrayList();
        this.xmapPositions = new HashMap();
        this.refSitesRelPos = new HashMap();
        this.geneRects = new ArrayList();
        this.indels = new ArrayList();
        this.genesInRange = new ArrayList();

        this.pressed = false;
        this.relSize = 0.0;
        this.draggedShape = null;
        this.selectedGene = null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        pressed = true;
        this.point = e.getPoint();
        for (MapOpticsRectangle r : geneRects) {
            if (r.contains(point) && pressed) {
                if (r.contains(point) && pressed) {
                    this.selectedGene = r.getGene();
                    geneName.setText(selectedGene.getName());
                    geneSource.setText(selectedGene.getSource());
                    geneSize.setText(String.valueOf(selectedGene.getEnd() - selectedGene.getStart()));
                    geneDialog.setVisible(true);
                }
            }
        }
        for (QueryShape s : alignShapes) {
            if (s.getRect().contains(point) && pressed) {

            }

        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
// depending on where the mouse is in the screen search for the shape
        pressed = true;
        this.point = e.getPoint();
        if (this.point.getY() >= 35 && this.point.getY() <= 60) {
            for (MapOpticsRectangle r : geneRects) {
                if (r.contains(point) && pressed) {
                    this.selectedGene = r.getGene();
                    geneName.setText(selectedGene.getName());
                    geneSource.setText(selectedGene.getSource());
                    geneSize.setText(String.valueOf(selectedGene.getEnd() - selectedGene.getStart()));
                    repaint();
                    geneDialog.setVisible(true);

                }
            }

        }
        if (alignment) {
            draggedShape = null;
            pressed = true;
            this.point = e.getPoint();
            Integer i = 0;
            for (QueryShape s : alignShapes) {
                if (s.getRect().contains(point)) {

                    draggedShape = i;
                    this.alignShapes.get(this.draggedShape).setSelected(true);
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    i++;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("released");
        pressed = false;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (point != null && pressed) {
            this.dx = (e.getX() - point.x);
            this.start += this.dx;
            this.end += this.dx;
//            refSitesInRange = new ArrayList();
            genesInRange = new ArrayList();
            geneRects = new ArrayList();
            alignShapes = new ArrayList();
            findSites();

        }

        if (point != null && pressed && this.draggedShape != null) {
            this.dx = (e.getX() - point.x);
            this.dy = (e.getY() - point.y);
            this.alignShapes.get(this.draggedShape).setDeltaX(Double.valueOf(this.dx));
            this.alignShapes.get(this.draggedShape).setDeltaY(Double.valueOf(this.dy));
            this.alignShapes.get(this.draggedShape).setSelected(true);
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void saveImage(String name, String type, String location) {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        printAll(g2d);
        try {
            ImageIO.write(image, type, new File(location + name + "." + type));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton clinVarButton;
    private javax.swing.JDialog databaseResult;
    private javax.swing.JTable databaseTable;
    private javax.swing.JRadioButton dbVarButton;
    private javax.swing.JButton exportDBResult;
    private javax.swing.JDialog geneDialog;
    private javax.swing.JLabel geneName;
    private javax.swing.JLabel geneSize;
    private javax.swing.JLabel geneSource;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton queryDBButton;
    // End of variables declaration//GEN-END:variables
}
