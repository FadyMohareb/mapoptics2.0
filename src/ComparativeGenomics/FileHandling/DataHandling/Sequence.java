package ComparativeGenomics.FileHandling.DataHandling;

/**
 * Contains a nucleotide sequence and calculates useful statistics such as
 * number of Ns and GC content
 *
 * @author franpeters
 */
public class Sequence {

    private final String sequence;
    private float gcContent;
    private Integer gcNum;
    private Integer length;
    private Integer numberNs;
    private float percentNs;

    /**
     * Constructor with sequence
     *
     * @param seq sequence
     */
    public Sequence(String seq) {
        this.sequence = seq;
        this.length = this.sequence.length();
        calculateSeqStats(this.sequence);

    }

    /**
     * Calculates GC content
     *
     * @param str sequence string
     * @return GC count
     */
    private Integer calculateGC(String str) {
        Integer gcCount = 0;
        Integer nCount = 0;
        char[] seq = str.toCharArray();
        for (char c : seq) {
            if (c == 'G' | c == 'C' | c == 'c' | c == 'g') {
                gcCount++;
            }
        }
        return gcCount;
    }

    /**
     * Calculate gaps (N characters)
     * 
     * @param str sequence string
     * @return number of Ns
     */
    private Integer calculateNs(String str) {

        Integer nCount = 0;
        char[] seq = str.toCharArray();
        for (char c : seq) {
            if (c == 'N' | c == 'n') {
                nCount++;
            }
        }
        return nCount;
    }

    /**
     * Calculates sequence statistiques: gc content, number of Ns, gc number, percentage of Ns
     * 
     * @param str sequence
     */
    private void calculateSeqStats(String str) {
        this.gcNum = calculateGC(str);
        this.numberNs = calculateNs(str);
        this.gcContent = ((float) this.gcNum / (float) this.length) * 100;
        this.percentNs = ((float) this.numberNs / (float) this.length) * 100;
    }

    /**
     * Gets GC content
     * 
     * @return gc content
     */
    public float getGCContent() {
        return this.gcContent;
    }

    /**
     * Gets size
     * 
     * @return length of the sequence
     */
    public Integer getSize() {
        return this.length;
    }

    /**
     * Gets sequence
     * 
     * @return sequence
     */
    public String getSeq() {
        return this.sequence;
    }

    /**
     * Gets gaps
     * 
     * @return number of Ns
     */
    public Integer getNs() {
        return this.numberNs;
    }

    /**
     * Gets percentage of Ns
     * 
     * @return percentage of Ns
     */
    public float getPercentNs() {
        return this.percentNs;
    }
}
