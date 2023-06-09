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
    
    private Integer refID1;
    private Integer refID2;
    
    private Integer qryID;

    
    public Translocation(Integer qryID, XmapData xmap1, XmapData xmap2, 
                            Chromosome refChr1, Chromosome refChr2){
        this.qryID=qryID;
        this.xmap1=xmap1;
        this.xmap2=xmap2;
        this.refChr1=refChr1;
        this.refChr2=refChr2;
//        compareMatchingSites();
    }
        
    public String getRefChr1Name(){
        return this.refChr1.getName();
    }
   
    public String getRefChr2Name(){
        return this.refChr2.getName();
    }
    
}
