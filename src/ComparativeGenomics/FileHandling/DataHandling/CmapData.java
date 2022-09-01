package ComparativeGenomics.FileHandling.DataHandling;
import ComparativeGenomics.FileHandling.Cmap;
import ComparativeGenomics.FileHandling.DataHandling.XmapData;
import ComparativeGenomics.FileHandling.DataHandling.Site;
import ComparativeGenomics.FileHandling.DataHandling.Match;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 *
 * @author franpeters
 * Stores the data corresponding to each cmap ID within a CMAP file.
 */
public class CmapData {
//    this ID refers to the cmap id
    private final Integer ID;
    private final Integer numSites;
    private final Double contigLen;
    private LinkedHashMap<Integer,Site> sites = new LinkedHashMap(); //where the site id is the key!
    
    /**
     * 
     * @param ID Cmap ID
     * @param numSites Number of site corresponding to this ID
     * @param contig Length of the cmap
     */
    public CmapData(Integer ID, Integer numSites,Double contig){
        this.ID = ID;
        this.numSites=numSites;
        this.contigLen=contig;    
    }

    /**
     * 
     * @param id Site id
     * @param site Site object, containing further information about the site
     */
    public void addSite(Integer id,  Site site){
        sites.put(id, site);
    }
    /**
     * 
     * @return All of the sites corresponding to this cmap map id
     */
    public LinkedHashMap<Integer,Site> getSites(){
        return this.sites;
    }
    /**
     * 
     * @param x ID of site to access
     * @return Site object corresponding to that ID
     */
    public Site getSite(Integer x){
        return this.sites.get(x);
    }
    /**
     * 
     * @return Length of the cmap map 
     */
    public Double getLength(){
        return this.contigLen;
    }
    /**
     * 
     * @return Cmap ID
     */
   public Integer getID(){
       return this.ID;
   }
 
}