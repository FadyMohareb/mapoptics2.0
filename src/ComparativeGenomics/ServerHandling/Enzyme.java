package ComparativeGenomics.ServerHandling;
//Created to store the information about the enzyme selected by the user
//to enable extension of functionality later on
/**
 *
 * @author franpeters
 */
public class Enzyme {
    private String name;
    private String site;
    
    public Enzyme(String name, String site){
        this.name = name;
        this.site = site;
    }
    
    public void setName(String name){this.name = name;}
    public void setSite(String site){this.site = site;}
    public String getName(){return this.name;}
    public String getSite(){return this.site;}
    
}
