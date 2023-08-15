package ComparativeGenomics.StructuralVariant;

import ComparativeGenomics.FileHandling.DataHandling.Site;
import ComparativeGenomics.FileHandling.DataHandling.Gene;
import java.util.ArrayList;

/**
 * Stores duplication data
 *
 * @author franpeters
 */
public class Duplication {

    private Integer refID;
    private Double size;
    private ArrayList<Site> refSites;
    private String orientation;
    private ArrayList<Gene> genes = new ArrayList();

    /**
     * Constructor with reference ID and sites
     *
     * @param refID reference ID
     * @param sites reference cmap sites
     */
    public Duplication(Integer refID, ArrayList<Site> sites) {
        this.refID = refID;
        this.refSites = sites;
    }

    /**
     * Adds gene to the list of genes involved in the duplication
     *
     * @param genes list of genes to add
     */
    public void addGenes(ArrayList<Gene> genes) {
        this.genes = genes;
    }

}
