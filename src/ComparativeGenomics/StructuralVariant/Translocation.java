package ComparativeGenomics.StructuralVariant;

import ComparativeGenomics.FileHandling.DataHandling.Chromosome;
import ComparativeGenomics.FileHandling.DataHandling.CmapData;
import ComparativeGenomics.FileHandling.DataHandling.Gene;

import ComparativeGenomics.FileHandling.DataHandling.XmapData;
import java.util.ArrayList;

/**
 *
 * @author franpeters
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
    //private Integer refPos1;
    //private Integer refPos2;

    private Integer refID1;
    private Integer refID2;

    private Integer qryID;

    public Translocation(Integer qryID, XmapData xmap1, XmapData xmap2,
            Chromosome refChr1, Chromosome refChr2) {
        this.qryID = qryID;
        this.xmap1 = xmap1;
        this.xmap2 = xmap2;
        this.refChr1 = refChr1;
        this.refChr2 = refChr2;
    }

    public Translocation(Integer qryID, Chromosome refChr1, Chromosome refChr2) {
        this.qryID = qryID;
        this.refChr1 = refChr1;
        this.refChr2 = refChr2;
    }

    /**
     * @param values from FaNDOM .txt output file
     * @return
     */
    /*
    public Translocation(Chromosome refChr1, Chromosome refChr2) {
        this.refChr1 = refChr1;
        this.refChr2 = refChr2;
//        compareMatchingSites();
    }
     */
    public String getRefChr1Name() {
        if (this.refChr1 != null) {
            return this.refChr1.getName();
        } else {
            return null;
        }
    }

    public String getRefChr2Name() {
        if (this.refChr2 != null) {
            return this.refChr2.getName();
        } else {
            return null;
        }
    }

}
