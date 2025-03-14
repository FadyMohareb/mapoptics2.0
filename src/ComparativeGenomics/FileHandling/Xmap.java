package ComparativeGenomics.FileHandling;

import ComparativeGenomics.FileHandling.DataHandling.CmapData;
import ComparativeGenomics.FileHandling.DataHandling.XmapData;
import ComparativeGenomics.StructuralVariant.Duplication;
import ComparativeGenomics.FileHandling.DataHandling.Pair;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 * Verifies file format, parse and save the contents of an XMAP file. Scanner is
 * used to read through the contents of the file in a memory efficient manner.
 * The data is stored into HashMap data structures using XmapData (see 3.2.10
 * for more information on this class) and Pair (see 3.2.7) objects. XmapData
 * stores information regarding each Xmap ID within the file. This class assumes
 * the file follows the criteria laid out here
 * https://bionanogenomics.com/wp-content/uploads/2017/03/30040-XMAP-FileFormat-Specification-Sheet.pdf
 *
 * @author franpeters
 */
public class Xmap {

    private HashMap<Integer, ArrayList<XmapData>> xmap = new HashMap(); //where key is ref cmap ID and value is the xmapmap
    private String version;
    private String labelChannels;
    private Integer numberConsensus;
    private String filepath;

//   this is to check if there have been any translocation events. 
    //i.e. one query map mapping to more than one ref cmap i.e. chromosome!
    private HashMap<Integer, ArrayList<XmapData>> queryMapsToRef = new HashMap();
    //where key is the query map ID and values are the corresponding xmap map
    private Cmap refCmap;
    private Cmap qryCmap;
    private Integer indelTotal = 0;
    private ArrayList<Duplication> duplications = new ArrayList();
    private HashMap<Integer, ArrayList<XmapData>> potentialTranslocations = new HashMap();
    private HashMap<Integer, XmapData> allXmaps = new HashMap();

    private boolean isValid = true; //Check if the file is valid, ie in xmap format and not empty

    /**
     * Constructor with xmap file path
     *
     * @param filepath path to xmap file
     */
    public Xmap(String filepath) {
        this.filepath = filepath;
        readXmap();
        Iterator<Map.Entry<Integer, ArrayList<XmapData>>> entries = this.xmap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, ArrayList<XmapData>> entry = entries.next();
            Integer chrSize = entry.getKey();
            ArrayList<XmapData> chrName = entry.getValue();
        }
    }

    /**
     *
     * @param x Xmap ID
     * @return all the XmapData from the xmap matching the given ID
     */
    public ArrayList<XmapData> getXmap(Integer x) {
        return this.xmap.get(x);
    }

    /**
     * Parses the Xmap file using Scanner to read each line in a memory
     * efficient manner. Each line that is not a header line is read into an
     * XmapData object except the Alignment field of the file which is split
     * into an ArrayList and each site ‘pair’ (i.e. (1,2) where the first
     * integer is the reference cmap site id and the second integer is the query
     * cmap site id) is read into a Pair object (see 3.2.7) which is also added
     * to the XmapData object.
     */
    private void readXmap() {
        String[] directory = this.filepath.split("/");

        boolean checkXmap = ValidateXMAP(this.filepath);
//       Validate XMAP
        if (!checkXmap) {
//            do not read file if it is not valid 
            this.isValid = false;
        } else {
            FileInputStream inputStream = null;
            Scanner sc = null;
            try {
                inputStream = new FileInputStream(this.filepath);
                sc = new Scanner(inputStream, "UTF-8");
                while (sc.hasNextLine()) {
                    ArrayList<Pair> align = new ArrayList();
                    String row = sc.nextLine();
                    // Parse only rows that are not headers
                    String chrPattern = "#";
                    Pattern c = Pattern.compile(chrPattern);
                    Matcher checkHeader = c.matcher(row);
                    if (checkHeader.find() == true) {

                    } else {
                        String[] rowData = row.split("\t");
                        Integer xmapID = Integer.valueOf(rowData[0]);
                        Integer qid = Integer.valueOf(rowData[1]);
                        Integer rid = Integer.valueOf(rowData[2]);
                        Double qst = Double.parseDouble(rowData[3]);
                        Double qen = Double.parseDouble(rowData[4]);
                        Double rst = Double.parseDouble(rowData[5]);
                        Double ren = Double.parseDouble(rowData[6]);
                        // Orientation: + is true, - is false
                        Boolean ori = true;
                        if ("-".equals(rowData[7])) {
                            ori = false;
                        }
                        Double con = Double.parseDouble(rowData[8]); // Confidence
                        String hit = rowData[9]; //Pseudo cigar string
                        Double qle = Double.parseDouble(rowData[10]); //Length of query map
                        Double rle = Double.parseDouble(rowData[11]); //Length of ref map
                        String alignment = rowData[13].replace(")(", ";").replace("(", "").replace(")", "");
                        String[] alin = alignment.split(";");
                        for (String a : alin) {
                            String[] s = a.split(",");
                            Pair pair = new Pair(Integer.valueOf(s[0]), Integer.valueOf(s[1]));
                            align.add(pair);
                        }
                        // Save parsed information into a XmapData object
                        XmapData map = new XmapData(xmapID, qid, rid, qst, qen,
                                rst, ren, ori, con,
                                hit, qle, rle, align);
                        // Save all maps (ie line of xmap file) into and ArrayList object
                        this.allXmaps.put(xmapID, map);
                        // Reference / query id and their values (list of map) are saved into a HashMap
                        if (this.xmap.containsKey(rid)) {
                            this.xmap.get(rid).add(map);
                        } else {
                            ArrayList<XmapData> list = new ArrayList();
                            list.add(map);
                            this.xmap.put(rid, list);
                        }
                        if (this.queryMapsToRef.containsKey(qid)) {
                            this.queryMapsToRef.get(qid).add(map);
                        } else {
                            ArrayList<XmapData> list = new ArrayList();
                            list.add(map);
                            this.queryMapsToRef.put(qid, list);
                        }
                    }
                }
                if (sc.ioException() != null) {
                    try {
                        throw sc.ioException();
                    } catch (IOException ex) {
                        Logger.getLogger(Cmap.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                        //            check the possible translocation events
                        for (Map.Entry<Integer, ArrayList<XmapData>> entry : this.queryMapsToRef.entrySet()) {
                            Integer key = entry.getKey();
                            ArrayList<XmapData> value = entry.getValue();
                            if (value.size() > 1) {
                                boolean allEqual = value.stream().distinct().count() <= 1;
                                if (!allEqual) {
                                    // Possible translocation found
                                    this.potentialTranslocations.put(key, value);
                                }
                            }
                        }
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
     * Verifies xmap file format
     * 
     * @param filepath path of file to be validated
     * @return true if valid and false if not
     */
    private boolean ValidateXMAP(String filePath) {
        if (filePath.endsWith(".xmap")) {
            if (Files.exists(Paths.get(filePath))) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(filePath));
                    String line;

                    while ((line = br.readLine()) != null && line.startsWith("#")) {
                        if (line.toLowerCase().contains("xmap file version")) {
                            return true;
                        }
                    }
                    br.close();

                    // Show error message if wrong format
                    JOptionPane.showMessageDialog(null,
                            "Error loading XMAP file."
                            + "\n\nInvalid format!",
                            "XMAP input error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //Show error message if no file found
                JOptionPane.showMessageDialog(null,
                        "Error loading XMAP file."
                        + "\n\nFile does not exist!",
                        "XMAP input error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            //Show error message if wrong file type
            JOptionPane.showMessageDialog(null,
                    "Error loading XMAP file."
                    + "\n\nInvalid file type!",
                    "XMAP input error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /**
     * Gets xmap
     * 
     * @return hashmap of xmap. The key is the xmap id, the value is an array llist of xmap data corresponding
     * to the id
     */
    public HashMap<Integer, ArrayList<XmapData>> getXmap() {
        return this.xmap;
    }

    /**
     * Gets queries mapped to reference
     * 
     * @return a hashmap, the key is the reference cmap ID and the value
     * is all the alignments related to that reference ID
     */
    public HashMap<Integer, ArrayList<XmapData>> getQueriesMappedToRef() {
        return this.queryMapsToRef;
    }

    /**
     * Sets the query cmap object
     * 
     * @param map query cmap
     */
    public void setQryCmap(Cmap map) {
        this.qryCmap = map;
    }

    /**
     * Sets the reference cmap object
     * 
     * @param map reference cmap
     */
    public void setRefCmap(Cmap map) {
        this.refCmap = map;
    }

    /**
     * Gets query cmap
     * 
     * @return Cmap object corresponding to this query genome
     */
    public Cmap getQryCmap() {
        return this.qryCmap;
    }

    /**
     * Gets reference cmap
     * 
     * @return Cmap object corresponding to this query genome
     */
    public Cmap getRefCmap() {
        return this.refCmap;
    }

    /**
     * Labels channels of this xmap file
     * 
     * @return label channels
     */
    public String getLabelChannels() {
        return this.labelChannels;
    }

    /**
     * Gets version of this xmap file
     * 
     * @return this version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Returns the validity of the file
     *
     * @return boolean true if it is valid, false if not
     */
    public boolean getValidity() {
        return this.isValid;
    }

    /**
     * Returns a Hashmap of all maps (values) corresponding to a reference contig ID (key) in the XMAP
     * 
     * @return maps of a reference contig id
     */
    public HashMap<Integer, ArrayList<XmapData>> getXmapByRef() {
        return xmap;
    }

    /**
     * Returns a Hashmap of all maps (values) corresponding to a query contig ID (key) in the XMAP
     * 
     * @return maps of a query contig ID
     */
    public HashMap<Integer, ArrayList<XmapData>> getXmapByQry() {
        return queryMapsToRef;
    }

    /**
     * Gets all xmap from this file
     * 
     * @return all xmaps
     */
    public HashMap<Integer, XmapData> getAllXmaps() {
        return this.allXmaps;
    }

    /**
     * Gets xmap by their IDs
     * 
     * @param xmapID ID of the fetched xmap
     * @return xmap
     */
    public XmapData getXmapByXmapID(Integer xmapID) {
        return this.allXmaps.get(xmapID);
    }

    /**
     * Detects any potential insertion or deletions by comparing the size of
     * the ref and query lengths for each XmapData and checking if the
     * difference is above the minimum size of the indel SV
     *
     * @param minIndelSize minimum detected insertion or deletion size
     */
    public void detectSVs(Integer minIndelSize) {
        int count = 0;

        Set<Entry<Integer, ArrayList<XmapData>>> alignments = this.xmap.entrySet();
        for (HashMap.Entry<Integer, ArrayList<XmapData>> entry1 : alignments) {

            Integer cmapID1 = entry1.getKey();
            CmapData refCmapMap = refCmap.getCmapByID(cmapID1);

            ArrayList<XmapData> map1 = entry1.getValue();
//            next iterate through all the alignments in the xmap to determine SVs
            for (XmapData m1 : map1) {
                //            first detect indels
                CmapData qryCmapMap = qryCmap.getCmapByID(m1.getQryID());
                m1.setCmap(refCmapMap, qryCmapMap, minIndelSize);
                indelTotal += m1.numIndels();
            }
        }
    }

    /**
     * Gets potential translocations
     * 
     * @return list of translocations
     */
    public HashMap<Integer, ArrayList<XmapData>> getPotentialTranslocations() {
        return this.potentialTranslocations;
    }

}
