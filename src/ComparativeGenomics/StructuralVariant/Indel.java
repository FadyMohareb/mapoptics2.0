package ComparativeGenomics.StructuralVariant;

import ComparativeGenomics.FileHandling.DataHandling.Gene;
import java.util.ArrayList;

/**
 * Stores insertions and deletions alongside any
 * associated genes located within the region of the ref genome
 * @author franpeters
 */
public class Indel {
    private String type = ""; //where type can be an insertion or a deletion
    private Double start;
    private Double end;
    private Integer refCmapID;
    private Integer qryCmapID;
    private ArrayList<Gene> genes = new ArrayList();

    /**
     * Constructor with type, start, end and associated reference and query ID
     *
     * @param type variant: an insertion or deletion
     * @param start Start position on reference
     * @param end End position on reference
     * @param rID associated reference cmap ID
     * @param qID associated query cmap ID
     */
    public Indel(String type, Double start, Double end, Integer rID, Integer qID) {
        this.type = type;
        this.start = start;
        this.end = end;
        this.refCmapID = rID;
        this.qryCmapID = qID;
    }

    /**
     * Adds a gene to the SV
     * 
     * @param gene gene added to the SV
     */
    public void addFeature(Gene gene) {
        this.genes.add(gene);
    }

    /**
     * Gets number of genes
     * 
     * @return number of genes
     */
    public Integer numberGenes() {
        return this.genes.size();
    }

    /**
     * Gets type
     * 
     * @return type of SV
     */
    public String getType() {
        return this.type;
    }

    /**
     * Gets starts
     * 
     * @return indel start position
     */
    public Double getStart() {
        return this.start;
    }

    /**
     * Gets end
     * 
     * @return indel end position
     */
    public Double getEnd() {
        return this.end;
    }

    /**
    * Gets size
    * 
    * @return size of indel
    */
    public Double getSize() {
        return (this.end - this.start);
    }

    /**
     * Gets query ID
     * 
     * @return query cmap id
     */
    public Integer getQryID() {
        return this.qryCmapID;
    }

    /**
     * Gets reference ID
     * 
     * @return reference cmap id
     */
    public Integer getRefID() {
        return this.refCmapID;
    }
}
