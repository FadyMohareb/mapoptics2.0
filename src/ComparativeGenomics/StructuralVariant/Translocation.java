package ComparativeGenomics.StructuralVariant;

import ComparativeGenomics.FileHandling.DataHandling.Chromosome;
import ComparativeGenomics.FileHandling.DataHandling.CmapData;
import ComparativeGenomics.FileHandling.DataHandling.Gene;

import ComparativeGenomics.FileHandling.DataHandling.XmapData;
import java.util.ArrayList;

/**
 * Stores translocations data.
 *
 * @author franpeters
 * @author Marie Schmit
 */
public class Translocation {

    private Chromosome refChr1;
    private Chromosome refChr2;
    private CmapData qrytoChr1;
    private CmapData qrytoChr2;
    private XmapData xmap1;
    private XmapData xmap2;
    private Integer length;
    private ArrayList<Gene> features;

    private Integer refID1;
    private Integer refID2;

    private Integer qryID;

    /**
     * Constructor with query ID, xmap data and chromosomes data
     *
     * @param qryID id of the query contig having a translocation
     * @param xmap1 xmap data of the first contig involved in the translocation
     * @param xmap2 xmap data of the second contig involved in the translocation
     * @param refChr1 first chromosome involved in the translocation
     * @param refChr2 second chromosome involved in the translocation
     */
    public Translocation(Integer qryID, XmapData xmap1, XmapData xmap2,
            Chromosome refChr1, Chromosome refChr2) {
        this.qryID = qryID;
        this.xmap1 = xmap1;
        this.xmap2 = xmap2;
        this.refChr1 = refChr1;
        this.refChr2 = refChr2;
    }

    /**
     * Constructor with query id and chromosomes data
     *
     * @param qryID query contig id
     * @param refChr1 first chromosome involved in the translocation
     * @param refChr2 second chromosome involved in the translocation
     */
    public Translocation(Integer qryID, Chromosome refChr1, Chromosome refChr2) {
        this.qryID = qryID;
        this.refChr1 = refChr1;
        this.refChr2 = refChr2;
    }

    /**
     * Gets first chromosome name
     *
     * @return first chromosome name
     */
    public String getRefChr1Name() {
        if (this.refChr1 != null) {
            return this.refChr1.getName();
        } else {
            return null;
        }
    }

    /**
     * Gets second chromosome name
     *
     * @return second chromosome name
     */
    public String getRefChr2Name() {
        if (this.refChr2 != null) {
            return this.refChr2.getName();
        } else {
            return null;
        }
    }
}
