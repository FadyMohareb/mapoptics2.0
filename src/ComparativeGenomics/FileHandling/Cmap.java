package ComparativeGenomics.FileHandling;

import ComparativeGenomics.FileHandling.DataHandling.CmapData;
import ComparativeGenomics.FileHandling.DataHandling.Site;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author franpeters Stores all of the contiguous maps contained within the
 * cmap file the data are stored into hashmaps to allow for efficient access The
 * class constructor takes on only one argument, a string containing the
 * filepath of the cmap file to be loaded into mapoptics
 *
 */
public class Cmap {

    private HashMap<Double, CmapData> cmapSize = new HashMap(); //where key is cmap length and the value is CmapData object
    private HashMap<Integer, CmapData> cmapID = new HashMap();
    private ArrayList<Integer> cmapIDs = new ArrayList();

    private String version;
    private String nickase;
    private Integer numberConsensus;
    private String filepath;
    private String filename;

    private Double genomeSize;

    private boolean isValid = true;

    /**
     *
     * @param filepath Filepath Cmap file to be read
     */
    public Cmap(String filepath) {
        this.filepath = filepath;
        genomeSize = 0.0;
        readCmap();
    }

    /**
     * Returns the data of a contig of a given size from the Cmap, for use when
     * assigning a chromosome name using the Karyotype file.
     *
     * @param x size of cmap contig to access
     * @return the CmapData corresponding to that size
     */
    public CmapData getCmapBySize(Double x) {
        return this.cmapSize.get(x);
    }

    /**
     * o	Returns the data of a contig of a given ID from the Cmap, for use when
     * accessing CmapData for an Xmap file.
     *
     * @param x ID of cmap contig to access
     * @return the CmapData corresponding to that ID
     */
    public CmapData getCmapByID(Integer x) {
        return this.cmapID.get(x);
    }

    /**
     *
     * @return all Cmap contig ids within the CMAP file as an ArrayList
     */
    public ArrayList<Integer> getCmapIDs() {
        return this.cmapIDs;
    }

    /**
     *
     * @return File version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     *
     * @return Digestion enzyme used to make the cmap file
     */
    public String getNickase() {
        return this.nickase;
    }

    /**
     *
     * @return Number of contigs contained within the cmap, should correspond
     * with number of chromosomes
     */
    public Integer getNumConsensus() {
        return this.numberConsensus;
    }

    /**
     *
     * @return File name of the cmap
     */
    public String getFileName() {
        return this.filename;
    }

    /**
     *
     * @return
     */
    public Double getGenomeSize() {
        return this.genomeSize;
    }

    /**
     * @return boolean indicating if CMAP file read is vailid
     */
    public boolean getValidity() {
        return this.isValid;
    }

    /**
     * Method to read in the data within the cmap file into CmapData and Site
     * objects
     */
    private void readCmap() {
        String[] directory = this.filepath.split("/");
        filename = directory[directory.length - 1];
        boolean checkCmap = validateCmap(this.filepath);
        this.isValid = checkCmap;
//       Validate CMAP
        if (checkCmap) {
            FileInputStream inputStream = null;
            Scanner sc = null;
            try {
                Integer count = 0;
                inputStream = new FileInputStream(this.filepath);
                sc = new Scanner(inputStream, "UTF-8");
                while (sc.hasNextLine()) {
                    String row = sc.nextLine();

                    String chrPattern = "#";
                    Pattern c = Pattern.compile(chrPattern);
                    Matcher checkHeader = c.matcher(row);
                    if (checkHeader.find() == true) {

                    } else {
                        String[] rowData = row.split("\t");
                        Integer cmapid = Integer.valueOf(rowData[0]);
                        Double size = Double.parseDouble(rowData[1]);
                        Integer sites = Integer.valueOf(rowData[2]);
                        Integer siteid = Integer.valueOf(rowData[3]);
                        Double pos = Double.parseDouble(rowData[5]);
                        Double lab = Double.parseDouble(rowData[4]);
                        Double std = Double.parseDouble(rowData[6]);
                        Double cov = Double.parseDouble(rowData[7]);
                        Double occ = Double.parseDouble(rowData[8]);
                        Site site = new Site(cmapid, siteid, pos, lab, std, cov, occ);
                        if (cmapSize.containsKey(size)) {
                            CmapData map = cmapSize.get(size);
                            map.addSite(siteid, site);
                            cmapID.replace(cmapid, map);
                            cmapSize.replace(size, map);
                        } else {
                            CmapData map = new CmapData(cmapid, sites, size);
                            map.addSite(siteid, site);
                            cmapID.put(cmapid, map);
                            cmapIDs.add(cmapid);
                            cmapSize.put(size, map);
                            this.genomeSize += size;
                        }
                    }
                }
                // note that Scanner suppresses exceptions
                if (sc.ioException() != null) {
                    try {
                        throw sc.ioException();
                    } catch (IOException ex) {
                        Logger.getLogger(Cmap.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (FileNotFoundException ex) {
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Cmap.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (sc != null) {
                    sc.close();
                }
            }
        }
    }

    /**
     * Method to check file is in correct format
     *
     * @param filePath
     * @return
     */
    private boolean validateCmap(String filePath) {
        if (filePath.endsWith(".cmap")) {
            if (Files.exists(Paths.get(filePath))) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(filePath));

                    String line;

                    while ((line = br.readLine()) != null && line.startsWith("#")) {
                        if (line.toLowerCase().contains("cmap file version")) {
                            return true;
                        }
                    }

                    br.close();

                    //Show error message if wrong format
                    JOptionPane.showMessageDialog(null,
                            "Error loading CMAP file."
                            + "\n\nInvalid format!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //Show error message if no file found
                JOptionPane.showMessageDialog(null,
                        "Error loading CMAP file."
                        + "\n\nFile does not exist!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            //Show error message if wrong file type
            JOptionPane.showMessageDialog(null,
                    "Error loading CMAP file."
                    + "\n\nInvalid file type!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}
