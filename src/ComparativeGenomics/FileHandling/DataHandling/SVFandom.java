package ComparativeGenomics.FileHandling.DataHandling;

/**
 *
 * @author marie 
 * Save all the information contained in the file SV.txt, output
 * of FaNDOM alignments in which SV information are saved. Breakpoints between
 * 15kbp to each other are clustered together. Breakpoint 1 is the breakpoint
 * having the lowest position.
 */
public class SVFandom {

    private int chr1;
    private int chr2;
    private int refPos1; // Position of breakpoint 1
    private String direction1; // Direction of breakpoint 1
    private int refPos2; //Position of breakpoint 2
    private String direction2; // Direction of breakpoint 2
    private String type; // Type of SV
    private int[] ids; // ID of contig supporting the breakpoint
    private int numSupports; // Number of contigs supporting the breakpoint
    private boolean geneInterrupt;
    private String geneFusion;

    public SVFandom(int chr1, int chr2, int refpos1, int refpos2, String direction1, 
            String direction2, String type, int[] ids, int numsupports, 
            boolean geneinterrupt, String genefusion){
        this.chr1 = chr1;
        this.chr2 = chr2;
        this.refPos1 = refpos1;
        this.direction1 = direction1;
        this.direction2 = direction2;
        this.type = type;
        this.ids = ids;
        this.numSupports = numsupports;
        this.geneInterrupt = geneinterrupt;
        this.geneFusion = genefusion;
    }
    
    public int getChr1(){
        return this.chr1;
    }
    
    public int getChr2(){
        return this.chr2;
    }
    
    public int getRefPos1(){
        return this.refPos1;
    }
    
    public int getRefPos2(){
        return this.refPos2;
    }
    
    public String getDirection1(){
        return this.direction1;
    }
    
    public String getDirection2(){
        return this.direction2;
    }
    
    public String getType(){
        return this.type;
    }
    
    public int[] getIds(){
        return this.ids;
    }
    
    public int getNumSupports(){
        return this.numSupports;
    }
    
    public String getGeneFusion(){
        return this.geneFusion;
    }
}
