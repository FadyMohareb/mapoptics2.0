package ComparativeGenomics.FileHandling.DataHandling;

import ComparativeGenomics.FileHandling.DataHandling.Match;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * Store the information of a digestion site on a cmap contig
 * Any match event associated with this site is stored in a HashMap
 * The key is the xmap id, the value is an ArrayList of match objects
 * @author franpeters
 */
public class Site {
    private final Integer cmapID;
    private final Integer siteID;
    private final Double position;
    private final Double labelChannel;
    private final Double stdDev;
    private final Double coverage;
    private final Double occurrence;
    private HashMap<Integer,ArrayList<Match>> matches = new HashMap();
    private Integer matchCount = 0;
    private boolean duplicated = false;
    /**
     * 
     * @param ci cmapID site is assigned to
     * @param si Site id
     * @param p Position
     * @param l Label channel
     * @param s Standard deviation
     * @param c Coverage
     * @param o Occurrence
     */
    public Site (Integer ci, Integer si,Double p,Double l, Double s, Double c, Double o){ //,Double pi, Double g, Double n, ){
        this.cmapID=ci;
        this.siteID=si;
        this.position= p;
        this.labelChannel=l;
        this.stdDev=s;
        this.coverage=c;
        this.occurrence=o;
    }
    /**
     * 
     * @param xmapID
     * @param match Has the site been aligned to?
     */
    public void addMatch(Integer xmapID, Match match){
        if(this.matches.containsKey(xmapID)){
            this.matches.get(xmapID).add(match);
        }else{
            ArrayList<Match> list = new ArrayList();
            list.add(match);
            this.matches.put(xmapID, list);
        }
        matchCount += 1;
    }
    /**
     * 
     * @param xmapID
     * @return The matches
     */
    public ArrayList<Match> getMatchesByXmapID(Integer xmapID){
        return this.matches.get(xmapID);
    }
    
    public HashMap<Integer,ArrayList<Match>> getMatches(){
        return this.matches;
    }

    
    /**
     * Have there been any sites matched to this site, return true if so and false if not
     * @return boolean
     */
    public boolean isMatch(){
        if (this.matches.size()>1){
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * 
     * @return 
     */
    public Double getPosition(){
        return this.position;
    }
    /**
     * 
     * @return 
     */
    public Double getLabelChannel(){
        return this.labelChannel;
    }
    /**
     * 
     * @return 
     */
    public Double getStdDev(){
        return this.stdDev;
    }
    /**
     * 
     * @return 
     */
    public Double getCoverage(){
        return this.coverage;
    }
    /**
     * 
     * @return 
     */
    public Double getOccurance(){
        return this.occurrence;
    }
    /**
     * 
     * @return 
     */
    public Integer getCmapID(){
        return this.cmapID;
    }
    /**
     * 
     * @return 
     */
    public Integer getSiteID(){
        return this.siteID;
    }
    /**
     * 
     * @return 
     */
    public Integer getMatchCount(){
        return this.matchCount;
    }
    /**
     * 
     * @param bool 
     */
    public void setDuplicated(boolean bool){
        this.duplicated=bool;
    }
}
