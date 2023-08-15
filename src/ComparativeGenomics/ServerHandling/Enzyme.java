package ComparativeGenomics.ServerHandling;
//Created to store the information about the enzyme selected by the user
//to enable extension of functionality later on

import java.util.HashMap;

/**
 * Stores enzyme information such as name and restriction sites.
 * 
 * @author franpeters
 * @author Marie Schmit
 */
public class Enzyme {

    private String name;
    private String site;

    /**
     * Create an enzyme using only one string searched through hashmaps to get
     * the corresponding site or name
     *
     * @param motif pattern of the enzyme
     */
    public Enzyme(String motif) {
        motif = motif.toUpperCase();
        HashMap<String, String> enzymeMap = new HashMap();

        enzymeMap.put("GCTCTTC", "BSPQI");
        enzymeMap.put("CCTCAGC", "BBVCI");
        enzymeMap.put("ATCGAT", "BSECI");
        enzymeMap.put("CACGAG", "BSSSI");
        enzymeMap.put("GCAATG", "BSRDI");
        enzymeMap.put("CCTCAGC", "BBVCI");
        enzymeMap.put("CTTAAG", "DLE1");

        // Retrieve the name using the motif
        if (enzymeMap.get(motif) != null) {
            this.site = motif;
            this.name = enzymeMap.get(motif);
        } else {
            // Retrieve the motif using the name
            HashMap<String, String> sitesMap = new HashMap();
            sitesMap.put("BSPQI", "GCTCTTC");
            sitesMap.put("BBVCI", "CCTCAGC");
            sitesMap.put("BSECI", "ATCGAT");
            sitesMap.put("BSSSI", "CACGAG");
            sitesMap.put("BSRDI", "GCAATG");
            sitesMap.put("BBVCI", "CCTCAGC");
            sitesMap.put("DLE1", "CTTAAG");
            this.name = motif;
            this.site = sitesMap.get(motif);
        }
    }

    /**
     * Constructor with name and site
     * 
     * @param name enzyme name
     * @param site enzyme site
     */
    public Enzyme(String name, String site) {
        this.name = name;
        this.site = site;
    }

    /**
     * Sets this enzymes name
     * 
     * @param name enzyme name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets this enzymes site
     * 
     * @param site enzyme site
     */
    public void setSite(String site) {
        this.site = site;
    }

    /**
     * Gets this enzymes name
     * 
     * @return enzymes name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets this enzymes site
     * 
     * @return enzymes site
     */
    public String getSite() {
        return this.site;
    }

}
