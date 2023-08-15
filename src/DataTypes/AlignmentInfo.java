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
     * Get query alignment start
     * 
     * @return qryAlignStart
     */
    public String getQryAlignStart() {
        return qryAlignStart;
    }

    /**
     * Get query alignment end
     * 
     * @return qryAlignEnd
     */
    public String getQryAlignEnd() {
        return qryAlignEnd;
    }

    /**
     * Get reference alignment start
     * @return refAlignStart
     */
    public String getRefAlignStart() {
        return refAlignStart;
    }

    /**
     * Gete reference alignment end
     * @return refAlignEnd
     */
    public String getRefAlignEnd() {
        return refAlignEnd;
    }

    /**
     * Get contig orientation
     * @return orientation
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Set orientation of alignments
     * @param orientation Orientation of alignment
     */
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    /**
     * Get confidence score
     * @return confidence
     */
    public String getConfidence() {
        return confidence;
    }

    /**
     * Set confidence score
     * @param confidence confidence score
     */
    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    /**
     * Get hit enum
     * @return hitEnum
     */
    public String getHitEnum() {
        return hitEnum;
    }

    /**
     * Set hit enumeration
     * @param hitEnum Enumeration of hits
     * @return hitEnum
     */
    public void setHitEnum(String hitEnum) {
        this.hitEnum = hitEnum;
    }

    /**
     * Get channel label
     * @return labelChannel
     */
    public String getLabelChannel() {
        return labelChannel;
    }

    /**
     * Set label of the channel
     * @param labelChannel Label of the channel
     */
    public void setLabelChannel(String labelChannel) {
        this.labelChannel = labelChannel;
    }

    /**
     * Get alignment
     * @return alignment
     */
    public String getAlignment() {
        return alignment;
    }

    /**
     * Set alignment
     * @param alignment alignment object
     */
    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }
    /*
     public void setQryAlignStart(String qryAlignStart) {
         this.qryAlignStart = qryAlignStart;
     }

     public void setQryAlignEnd(String qryAlignEnd) {
         this.qryAlignEnd = qryAlignEnd;
     }

     public void setRefAlignEnd(String refAlignEnd) {
        this.refAlignEnd = refAlignEnd;
     }

     public void setRefAlignStart(String refAlignStart) {
        this.refAlignStart = refAlignStart;
    }

    */


}
