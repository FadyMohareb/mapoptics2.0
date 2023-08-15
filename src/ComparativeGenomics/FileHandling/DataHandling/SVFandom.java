package ComparativeGenomics.FileHandling.DataHandling;

/**
 * Saves all the information contained in the file SV.txt, output
 * of FaNDOM alignments in which SV information are saved. Breakpoints between
 * 15kbp to each other are clustered together. Breakpoint 1 is the breakpoint
 * having the lowest position.
 * 
 * @author Marie Schmit
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

    /**
     * Constructor with all the information provided in one line of the SV file
     * 
     * @param chr1 id of chromosome 1
     * @param chr2 id of chromosome 2
     * @param refpos1 position of breakpoint 1
     * @param refpos2 position of breakpoint 2
     * @param direction1 direction of breakpoint 1
     * @param direction2 direction of breakpoint 2
     * @param type type of SV
     * @param ids ID of contig supporting the breakpoint
     * @param numsupports number of contigs supporting the breakpoint
     * @param geneinterrupt interrupted genes
     * @param genefusion merged genes
     */
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
    
    /**
     * Gets chromosome 1
     * @return chromosome 1
     */
    public int getChr1(){
        return this.chr1;
    }
    
    /**
     * Gets chromosome 2 data
     * @return chromosome 2
     */
    public int getChr2(){
        return this.chr2;
    }
    
    /**
     * Gets reference breakpoint position 1
     * @return reference position
     */
    public int getRefPos1(){
        return this.refPos1;
    }
    
    /**
     * Gets reference breakpoint position 2
     * @return reference position
     */
    public int getRefPos2(){
        return this.refPos2;
    }
    
    /**
     * Gets reference direction 1
     * @return reference position
     */
    public String getDirection1(){
        return this.direction1;
    }
    
    /**
     * Gets reference direction 2
     * @return reference position
     */
    public String getDirection2(){
        return this.direction2;
    }
    
    /**
     * Gest SV type
     * @return type
     */
    public String getType(){
        return this.type;
    }
    
    /**
     * Gets ID of contigs supporting this breakpoint
     * @return IDs
     */
    public int[] getIds(){
        return this.ids;
    }
    
    /**
     * Gets number of genes supporting this breakpoint
     * @return number of supports
     */
    public int getNumSupports(){
        return this.numSupports;
    }
    
    /**
     * Gets number of genes merged by this breakpoint
     * @return gene fusion
     */
    public String getGeneFusion(){
        return this.geneFusion;
    }
}
