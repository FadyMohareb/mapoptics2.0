package ComparativeGenomics.FileHandling.DataHandling;

/**
 *
 * @author franpeters
 * Class to store the aligned site id's of the reference and query cmaps.
 */
public class Pair {
    private final Integer refSiteID;
    private final Integer qrySiteID;
    private Site refSite=null;
    private Site qrySite=null;
    /**
     * 
     * @param r
     * @param q 
     */
    public Pair(Integer r, Integer q){
        refSiteID=r;
        qrySiteID=q;
    }
    /**
     * 
     * @param rS
     * @param qS 
     */
    public void setSite(Site rS, Site qS){
        this.refSite = rS;
        this.qrySite = qS;

    }
     /**
     *
     * @return Integer Reference site ID
     */
    public Integer getRef(){
        return this.refSiteID;
    }
       /**
     *
     * 
     * @return Integer Query cmapsite ID
     */
    public Integer getQry(){
        return this.qrySiteID;
    }
    /**
     * 
     * @return 
     */
    public Site getRefSite(){
        return this.refSite;
    }
    
    /**
     * 
     * @return 
     */
    public Site getQrySite(){
        return this.qrySite;
    }
}
