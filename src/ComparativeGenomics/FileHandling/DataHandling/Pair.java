package ComparativeGenomics.FileHandling.DataHandling;

/**
 * Stores the aligned site id's of the reference and query cmaps.
 *
 * @author franpeters
 */
public class Pair {

    private final Integer refSiteID;
    private final Integer qrySiteID;
    private Site refSite = null;
    private Site qrySite = null;

    /**
     * Constructor with two integer
     *
     * @param r reference id
     * @param q query id
     */
    public Pair(Integer r, Integer q) {
        refSiteID = r;
        qrySiteID = q;
    }

    /**
     * Sets pair sites
     * @param rS reference site
     * @param qS query site
     */
    public void setSite(Site rS, Site qS) {
        this.refSite = rS;
        this.qrySite = qS;

    }

    /**
     * Gets reference
     * 
     * @return Integer Reference site ID
     */
    public Integer getRef() {
        return this.refSiteID;
    }

    /**
     * Gets query
     *
     * @return Integer Query cmapsite ID
     */
    public Integer getQry() {
        return this.qrySiteID;
    }

    /**
     * Gets reference site
     * 
     * @return reference site
     */
    public Site getRefSite() {
        return this.refSite;
    }

    /**
     * Gets query site
     * 
     * @return query site
     */
    public Site getQrySite() {
        return this.qrySite;
    }
}
