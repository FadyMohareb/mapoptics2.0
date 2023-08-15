package ComparativeGenomics.StructuralVariant;

import ComparativeGenomics.FileHandling.DataHandling.Gene;
import java.util.ArrayList;

/**
 *
 * @author franpeters Class to store insertions and deletions alongside any
 * associated genes located within the region of the ref genome
 *
 */
public class Indel {

    private String type = ""; //where type can be an insertion or a deletion
    private Double start;
    private Double end;
    private Integer refCmapID;
    private Integer qryCmapID;
    private ArrayList<Gene> genes = new ArrayList();

    /**
     *
     * @param type Is the variant an insertion or deletion
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
     *
     * @param gene Add a gene to the SV
     */
    public void addFeature(Gene gene) {
        this.genes.add(gene);
    }

    public Integer numberGenes() {
        return this.genes.size();
    }

    public String getType() {
        return this.type;
    }

    public Double getStart() {
        return this.start;
    }

    public Double getEnd() {
        return this.end;
    }

    public Double getSize() {
        return (this.end - this.start);
    }

    public Integer getQryID() {
        return this.qryCmapID;
    }

    public Integer getRefID() {
        return this.refCmapID;
    }
}
