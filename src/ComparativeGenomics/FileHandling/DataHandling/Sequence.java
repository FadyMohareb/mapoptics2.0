package ComparativeGenomics.FileHandling.DataHandling;

/**
 *
 * @author franpeters
 * Contains a nucleotide sequence and calculates useful statistics 
 * such as number of Ns and GC content 
 */
public class Sequence {
    private final String sequence;
    private float gcContent;
    private Integer gcNum;
    private Integer length;
    private Integer numberNs;
    private float percentNs;

    
    public Sequence(String seq){
        this.sequence=seq;
        this.length=this.sequence.length();
        calculateSeqStats(this.sequence);
        
    }
    /**
     * 
     * @param str
     * @return 
     */
     private Integer calculateGC(String str){
        Integer gcCount = 0;
        Integer nCount = 0;
        char[] seq = str.toCharArray();
        for (char c : seq){
            if (c == 'G' | c == 'C'| c == 'c'| c=='g'){
                gcCount ++;
            }   
        }
        return gcCount;
     }
        private Integer calculateNs(String str){

        Integer nCount = 0;
        char[] seq = str.toCharArray();
        for (char c : seq){
            if (c == 'N' | c == 'n'){
            nCount ++;
                }  
            }
        return nCount;
        }
    private void calculateSeqStats(String str){
            this.gcNum=calculateGC(str);
            this.numberNs=calculateNs(str);
            this.gcContent = ((float)this.gcNum/(float)this.length)*100;
            this.percentNs = ((float)this.numberNs/(float)this.length)*100;
        }

    public float getGCContent(){
        return this.gcContent;
    }
        
    public Integer getSize(){
        return this.length;
    }
    public String getSeq(){
        return this.sequence;
    }
    
    public Integer getNs(){
        return this.numberNs;
    }
    
    public float getPercentNs(){
        return this.percentNs;
    }
}


