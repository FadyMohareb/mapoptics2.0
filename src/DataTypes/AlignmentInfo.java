package DataTypes;

/**
 * Stores information on alignments
 * @author Josie
 */
public class AlignmentInfo {
    private final String qryAlignStart;
    private final String qryAlignEnd ;
    private final String refAlignStart ;
    private final String refAlignEnd ;
    private String orientation ;
    private String confidence ;
    private String hitEnum ;
    private String labelChannel ;
    private String alignment ;

    /**
     * Constructor
     * @param qryAlignStart Start of query alignment
     * @param qryAlignEnd End of query alignment
     * @param refAlignStart Start of reference alignment
     * @param refAlignEnd End of reference alignment
     * @param orientation Oriantation
     * @param confidence Confidence score
     * @param hitEnum Pseudo-CIGAR string
     * @param labelChannel Color channel of alignment from cmap files
     * @param alignment Indices of the aligned site ID pairs. Count begins at the leftmost anchor label of that color.
     */
    public AlignmentInfo(String qryAlignStart, String qryAlignEnd, String refAlignStart, String refAlignEnd, String orientation, String confidence, String hitEnum, String labelChannel, String alignment) {
        this.qryAlignStart = qryAlignStart;
        this.qryAlignEnd = qryAlignEnd;
        this.refAlignStart = refAlignStart;
        this.refAlignEnd = refAlignEnd;
        this.orientation = orientation;
        this.confidence = confidence;
        this.hitEnum = hitEnum;
        this.labelChannel = labelChannel;
        this.alignment = alignment;
    }
    
    /**
     * Gets query alignment start
     * 
     * @return qryAlignStart
     */
    public String getQryAlignStart() {
        return qryAlignStart;
    }

    /**
     * Gets query alignment end
     * 
     * @return qryAlignEnd
     */
    public String getQryAlignEnd() {
        return qryAlignEnd;
    }

    /**
     * Gets reference alignment start
     * @return refAlignStart
     */
    public String getRefAlignStart() {
        return refAlignStart;
    }

    /**
     * Gets reference alignment end
     * @return refAlignEnd
     */
    public String getRefAlignEnd() {
        return refAlignEnd;
    }

    /**
     * Gets contig orientation
     * @return orientation
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Sets orientation of alignments
     * @param orientation Orientation of alignment
     */
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    /**
     * Gets confidence score
     * @return confidence
     */
    public String getConfidence() {
        return confidence;
    }

    /**
     * Sets confidence score
     * @param confidence confidence score
     */
    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    /**
     * Gets hit enum
     * @return hitEnum pseudo-cigar string representing matches (M), insertions (I) or deletions (D)
     * or label sites with respect to the reference or anchor map.
     */
    public String getHitEnum() {
        return hitEnum;
    }

    /**
     * Sets hit enumeration
     * @param hitEnum pseudo-cigar string representing matches (M), insertions (I) or deletions (D) 
     * or label sites with respect to the reference or anchor map
     */
    public void setHitEnum(String hitEnum) {
        this.hitEnum = hitEnum;
    }

    /**
     * Gets channel label
     * @return labelChannel Color channel of alignment from cmap files
     */
    public String getLabelChannel() {
        return labelChannel;
    }

    /**
     * Sets label channel
     * @param labelChannel Color channel of alignment from cmap files
     */
    public void setLabelChannel(String labelChannel) {
        this.labelChannel = labelChannel;
    }

    /**
     * Gets alignment
     * @return alignment
     */
    public String getAlignment() {
        return alignment;
    }

    /**
     * Sets alignment
     * @param alignment alignment object
     */
    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }
}
