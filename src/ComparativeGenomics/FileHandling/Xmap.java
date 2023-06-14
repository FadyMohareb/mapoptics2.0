package ComparativeGenomics.FileHandling;

import ComparativeGenomics.FileHandling.DataHandling.CmapData;
import ComparativeGenomics.FileHandling.DataHandling.XmapData;
import ComparativeGenomics.StructuralVariant.Duplication;
import ComparativeGenomics.FileHandling.DataHandling.Pair;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

/**
 *
 * @author franpeters
 * Verifies file format, parse and save the contents of an XMAP file. 
 * Scanner is used to read through the contents of the file in a memory efficient manner. 
 * The data is stored into HashMap data structures using XmapData 
 * (see 3.2.10 for more information on this class) and Pair (see 3.2.7) objects. 
 * XmapData stores information regarding each Xmap ID within the file.
 * This class assumes the file follows the criteria laid out here 
 * https://bionanogenomics.com/wp-content/uploads/2017/03/30040-XMAP-FileFormat-Specification-Sheet.pdf
 */
public class Xmap {
    private HashMap<Integer,ArrayList<XmapData>> xmap = new HashMap(); //where key is ref cmap ID and value is the xmapmap
    private String version;
    private String labelChannels;
    private Integer numberConsensus;
    private String filepath;

//   this is to check if there have been any translocation events. 
    //i.e. one query map mapping to more than one ref cmap i.e. chromosome!
    private HashMap<Integer,ArrayList<XmapData>> queryMapsToRef = new HashMap(); 
    //where key is the query map ID and values are the refmap IDs
    private Cmap refCmap;
    private Cmap qryCmap;
    private Integer indelTotal = 0;
    private ArrayList<Duplication> duplications = new ArrayList();
    private HashMap<Integer,ArrayList<XmapData>> potentialTranslocations = new HashMap();
    private HashMap<Integer,XmapData> allXmaps = new HashMap();
   
    public Xmap(String filepath){
        this.filepath = filepath;
        readXmap();
        Iterator<Map.Entry<Integer, ArrayList<XmapData>>> entries = this.xmap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, ArrayList<XmapData>> entry = entries.next();
            Integer chrSize = entry.getKey();
            ArrayList<XmapData> chrName = entry.getValue();
//            System.out.println(chrSize + " ");
//            for(int i=0;i<chrName.size();i++){
//                System.out.print(chrName.get(i).refStart +"   START    ");
//            }
        }
    }
    
    /**
     * 
     * @param x Xmap ID
     * @return all the XmapData from the xmap  matching the given ID
     */
    public ArrayList<XmapData> getXmap(Integer x){
        return this.xmap.get(x);
    }
    /**
     * o	parse the Xmap file using Scanner to read each line in a memory efficient manner. 
     *          Each line that is not a header line is read into an XmapData object 
     *          except the Alignment field of the file which is split into an ArrayList 
     *          and each site ‘pair’ (i.e. (1,2) where the first integer is the reference 
     *          cmap site id and the second integer is the query cmap site id) 
     *          is read into a Pair object (see 3.2.7) which is also added to the XmapData object.
     */
    private void readXmap(){
        String[] directory = this.filepath.split("/");
        
        boolean checkXmap = ValidateXMAP(this.filepath);
//       Validate XMAP
        if (!checkXmap) {
//            do not read file if it is not valid 
        }else{
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
                    if (checkHeader.find() == true){

                    }else{
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
                        if ("-".equals(rowData[7])){
                            ori = false;
                        }
                        Double con = Double.parseDouble(rowData[8]); // Confidence
                        String hit = rowData[9]; //Pseudo cigar string
                        Double qle = Double.parseDouble(rowData[10]); //Length of query map
                        Double rle = Double.parseDouble(rowData[11]); //Length of ref map
                        String alignment = rowData[13].replace(")(",";").replace("(","").replace(")","");
                        String[] alin = alignment.split(";");
                        for (String a : alin) {
                            String[] s = a.split(",");
                            Pair pair = new Pair(Integer.valueOf(s[0]), Integer.valueOf(s[1]));
                            align.add(pair);
                        }
                        // Save parsed information into a XmapData object
                        XmapData map = new XmapData(xmapID,qid,rid,qst,qen,
                                          rst,ren,ori,con,
                                          hit,qle,rle,align);
                        // Save all maps (ie line of xmap file) into and ArrayList object
                        this.allXmaps.put(xmapID, map);
                        // Reference / query id and their values (list of map) are saved into a HashMap
                        if (this.xmap.containsKey(rid)) {
                                this.xmap.get(rid).add(map);
                        }else{
                            ArrayList<XmapData> list = new ArrayList();
                            list.add(map);
                            this.xmap.put(rid, list);
                            }
                        if (this.queryMapsToRef.containsKey(qid)){
                            this.queryMapsToRef.get(qid).add(map);
                        }else{
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

        }   finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                                    //            check the possible translocation events
                        for (Map.Entry<Integer, ArrayList<XmapData>> entry : this.queryMapsToRef.entrySet()) {
                                Integer key = entry.getKey();
                                ArrayList<XmapData> value = entry.getValue();
                                if (value.size()>1){
                                    boolean allEqual = value.stream().distinct().count() <= 1;
                                    if (!allEqual){
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
     * 
     * @param filepath of file to be validated
     * @return true if valid and false if not
     */
    private boolean ValidateXMAP(String filepath){
//        need to add in code here to check the xmap file!
        return true;
    }
    /**
     * 
     * @return 
     */
    public HashMap<Integer,ArrayList<XmapData>> getXmap(){
        return this.xmap;
    }
    
    /**
     * 
     * @return 
     */
    public HashMap<Integer,ArrayList<XmapData>> getQueriesMappedToRef(){
        return this.queryMapsToRef;
    }

    /**
     * 
     * @param map 
     */
    public void setQryCmap(Cmap map){
        this.qryCmap=map;
    }
    
    /**
     * 
     * @param map 
     */
    public void setRefCmap(Cmap map){
        this.refCmap=map;
    }
    /**
     * 
     * @return Cmap object corresponding to the query genome
     */
    public Cmap getQryCmap(){
        return this.qryCmap;
    }
    
    /**
     * 
     * @return 
     */
    public Cmap getRefCmap(){
        return this.refCmap;
    }

    /**
     * 
     * @return 
     */
    public String getLabelChannels(){
        return this.labelChannels;
    }
    
    /**
     * 
     * @return 
     */
    public String getVersion(){
        return this.version;
    }
    
    /**
     * 
     * @return 
     */
    public HashMap<Integer,XmapData> getAllXmaps(){
        return this.allXmaps;
    }
    
    /**
     * 
     * @param xmapID
     * @return 
     */
    public XmapData getXmapByXmapID(Integer xmapID){
        return this.allXmaps.get(xmapID);
    }
    
    /**
     * 
     * o    Detect any potential insertion or deletions by comparing the size of the ref and 
     *      query lengths for each XmapData and checking if the difference is above the 
     *      minimum size of the indel SV
     * @param minIndelSize 
     */
    public void detectSVs(Integer minIndelSize){
        int count =0;

        Set<Entry<Integer,ArrayList<XmapData>>> alignments = this.xmap.entrySet();
        for(HashMap.Entry<Integer,ArrayList<XmapData>> entry1:alignments) {
            
            Integer cmapID1 = entry1.getKey();
            CmapData refCmapMap = refCmap.getCmapByID(cmapID1);

            ArrayList<XmapData> map1 = entry1.getValue();
//            next iterate through all the alignments in the xmap to determine SVs
            for (XmapData m1 : map1){
                //            first detect indels
                CmapData qryCmapMap = qryCmap.getCmapByID(m1.getQryID());
                m1.setCmap(refCmapMap, qryCmapMap, minIndelSize);
                indelTotal += m1.numIndels();
                    }
                }
            }   
   
    /**
     * 
     * @return 
     */
   public HashMap<Integer,ArrayList<XmapData>> getPotentialTranslocations(){
        return this.potentialTranslocations;
    }
   
}