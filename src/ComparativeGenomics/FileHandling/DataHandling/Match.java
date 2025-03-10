package ComparativeGenomics.FileHandling.DataHandling;

/**
 * Stores the information on the match, so that only the matches related to the
 * displayed reference cmap are drawn.
 *
 * @author franpeters
 */
public class Match {

    private boolean match = false;
    private Integer qryCmapID;
    private Site qrySite;
    private Integer cmapID;

    /**
     * Constructor with cmap id, match and query
     * 
     * @param cmapId reference cmap id
     * @param m indicates if a match occured or not
     * @param q query site
     */
    public Match(Integer cmapId, boolean m, Site q) {
        this.cmapID = cmapId;
        this.match = m;
        this.qrySite = q;
    }

    /**
     * Gets match
     * @return match
     */
    public boolean getMatch() {
        return this.match;
    }

    /**
     * Gets cmap id
     * @return reference cmap id
     */
    public Integer getCmapID() {
        return this.cmapID;
    }

    /**
     * Gets query cmap id
     * @return query cmap id
     */
    public Integer getQryCmapID() {
        return this.qryCmapID;
    }

    /**
     * Gets query cmap site
     * @return query cmap site
     */
    public Site getQryCmapSite() {
        return this.qrySite;
    }

}
