package ComparativeGenomics.FileHandling.DataHandling;

import ComparativeGenomics.FileHandling.DataHandling.Site;
import ComparativeGenomics.FileHandling.Cmap;
import ComparativeGenomics.StructuralVariant.Indel;
import ComparativeGenomics.Drawing.MapOpticsRectangle;
import ComparativeGenomics.FileHandling.Cmap;
import ComparativeGenomics.FileHandling.DataHandling.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author franpeters Stores the data for each individual alignment identified
 * as the XMAP file. All of the aligned sites are stored as a Pair.
 */
public class XmapData {

    private final Integer ID;
    private final Integer refCmapID; //this is how you link the alignment to the chromosome
    private final Integer qryCmapID; //this is how you draw the relative position of the alignment to the screen

    private Double qryStart;
    private Double qryEnd;
    private Double refStart;
    private Double refEnd;

    private Boolean orientation;
    private Double confidence;
    private String hitEnum;

    private Double qryLen;
    private Double refLen;

    private ArrayList<Pair> align = new ArrayList();
    private ArrayList<Indel> indels = new ArrayList();

    private Double lastRefPos = 0.0;
    private Double lastQryPos = 0.0;

    /**
     *
     * @param id Xmap ID
     * @param qryid Query cmap ID
     * @param refid Reference cmap ID
     * @param qrys Start position on query
     * @param qrye End position on query
     * @param refs Start position on reference
     * @param refe End position on reference
     * @param ori Orientation
     * @param conf Confidence
     * @param hit HitEnum
     * @param qryl Query Length
     * @param refl Reference Length
     * @param align Sites matching
     */
    public XmapData(Integer id, Integer qryid, Integer refid, Double qrys,
            Double qrye, Double refs, Double refe,
            Boolean ori, Double conf, String hit,
            Double qryl, Double refl, ArrayList<Pair> align) {
        this.ID = id;
        this.qryCmapID = qryid;
        this.refCmapID = refid;
        this.qryStart = qrys;
        this.qryEnd = qrye;
        this.refStart = refs;
        this.refEnd = refe;
        this.orientation = ori;
        this.confidence = conf;
        this.hitEnum = hit;
        this.qryLen = qryl;
        this.refLen = refl;
        this.align = align;
    }

    public Integer getID() {
        return this.ID;
    }

    public Integer getQryID() {
        return this.qryCmapID;
    }

    public Integer getRefID() {
        return this.refCmapID;
    }

    public boolean getOri() {
        return this.orientation;
    }

    public Double getConfidence() {
        return this.confidence;
    }

    public String getHitEnum() {
        return this.hitEnum;
    }

    public Double getQryLen() {
        return this.qryLen;
    }

    public Double getRefLen() {
        return this.refLen;
    }

    public ArrayList<Pair> returnAlignments() {
        return this.align;
    }

    public Pair returnFirstMatch() {
        return this.align.get(0);
    }

    public Pair returnLastMatch() {
        return this.align.get(this.align.size() - 1);
    }

    public ArrayList<Indel> getIndels() {
        return this.indels;
    }

    public Integer numIndels() {
        return this.indels.size();
    }

    public Double getQryStart() {
        return this.qryStart;
    }

    public Double getQryEnd() {
        return this.qryEnd;
    }

    public Double getRefStart() {
        return this.refStart;
    }

    public Double getRefEnd() {
        return this.refEnd;
    }

    /**
     *
     * o Gets data of each site to detect indel.
     *
     * @param ref Data from Cmap reference
     * @param qry Data from Cmap query
     * @param minIndelSize Minimum size for indel
     */
    public void setCmap(CmapData ref, CmapData qry, Integer minIndelSize) {
        for (Pair pair : align) {
            Integer refSiteID = pair.getRef();
            Integer qrySiteID = pair.getQry();
            if (ref != null && qry != null) {
                this.indels = detectIndel(ref.getSite(refSiteID), qry.getSite(qrySiteID), minIndelSize, ref.getID(), qry.getID());
            }
        }
    }

    private ArrayList<Indel> detectIndel(Site refSite, Site qrySite, Integer minIndelSize, Integer refID, Integer qryID) {
        ArrayList<Indel> foundIndels = new ArrayList();

        if (refSite != null && qrySite != null) {
            Double refPos = refSite.getPosition();
            Double qryPos = qrySite.getPosition();
            Double refDiff = refPos - lastRefPos;
            Double qryDiff = qryPos - lastQryPos;
            // The length of the query and reference sites is not the same
            // ie their is insertion or deletion
            if (!Objects.equals(refDiff, qryDiff)) {
                // Check if length of insertion or deletion is higher than minimum indel size
                if (refDiff - qryDiff > minIndelSize || qryDiff - refDiff > minIndelSize) {
                    if (refDiff - qryDiff < 0) {
                        Indel i = new Indel("Deletion", lastRefPos, refPos, refID, qryID);
                        foundIndels.add(i);
                        // System.out.println("Indel added XmapData 181");
                    } else {
                        Indel i = new Indel("Insertion", lastRefPos, refPos, refID, qryID);
                        foundIndels.add(i);
                    }
                }

            }
            lastRefPos = refPos;
            lastQryPos = qryPos;
        }
        return foundIndels;
    }
}
