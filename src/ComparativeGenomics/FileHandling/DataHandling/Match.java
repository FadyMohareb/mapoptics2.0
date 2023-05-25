package ComparativeGenomics.FileHandling.DataHandling;

/**
 *
 * @author franpeters
 * To store the information on the match, so that only the matches 
 * related to the displayed reference cmap are drawn.
 */
public class Match {
    private boolean match = false;
//    private Integer refCmapID;
    private Integer qryCmapID;
    private Site qrySite;
    private Integer cmapID;
    
    public Match(Integer cmapId, boolean m, Site q){
        this.cmapID=cmapId;
        this.match=m;
//        this.refCmapID=r;
        this.qrySite =q;
    }

    public boolean getMatch(){
        return this.match;
    }
    public Integer getCmapID(){
        return this.cmapID;
    }
    public Integer getQryCmapID(){
        return this.qryCmapID;
    }
    
    public Site getQryCmapSite(){
        return this.qrySite;
    }
    
}
