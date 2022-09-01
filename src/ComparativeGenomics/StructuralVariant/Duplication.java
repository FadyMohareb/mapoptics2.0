package ComparativeGenomics.StructuralVariant;
import ComparativeGenomics.FileHandling.DataHandling.Site;
import ComparativeGenomics.FileHandling.DataHandling.Gene;
import java.util.ArrayList;

/**
 *
 * @author franpeters
 */
public class Duplication {
    private Integer refID;
    private Double size;
    private ArrayList<Site> refSites;
    private String orientation;
    private ArrayList<Gene> genes =new ArrayList();
    

    public Duplication(Integer refID, ArrayList<Site> sites){
        this.refID = refID;
        this.refSites = sites;
    }
    
    public void addGenes(ArrayList<Gene> genes){
        this.genes=genes;
    }
    
}
