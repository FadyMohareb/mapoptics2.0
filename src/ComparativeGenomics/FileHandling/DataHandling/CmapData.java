package ComparativeGenomics.FileHandling.DataHandling;

import ComparativeGenomics.FileHandling.Cmap;
import ComparativeGenomics.FileHandling.DataHandling.XmapData;
import ComparativeGenomics.FileHandling.DataHandling.Site;
import ComparativeGenomics.FileHandling.DataHandling.Match;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Stores the data corresponding to each cmap ID within a CMAP file.
 *
 * @author franpeters
 */
public class CmapData {

    // This ID refers to the cmap id
    private final Integer ID;
    private final Integer numSites;
    private final Double contigLen;
    private LinkedHashMap<Integer, Site> sites = new LinkedHashMap(); //where the site id is the key!

    /**
     * Constructor with ID, number of sites and length of cmap maps
     *
     * @param ID Cmap ID
     * @param numSites Number of site corresponding to this ID
     * @param contig Length of the cmap
     */
    public CmapData(Integer ID, Integer numSites, Double contig) {
        this.ID = ID;
        this.numSites = numSites;
        this.contigLen = contig;
    }

    /**
     * Add a <code>Site</code> object to a <code>LinkedHashMap</code> where the key is the site ID
     * and the value is a <code>Site</code> object.
     * 
     * @param id site id
     * @param site <code>Site</code> object, containing further information about the site
     */
    public void addSite(Integer id, Site site) {
        sites.put(id, site);
    }

    /**
     *Gets all the sites associated with a cmap ID
     * 
     * @return all of the sites corresponding to this cmap map id
     */
    public LinkedHashMap<Integer, Site> getSites() {
        return this.sites;
    }

    /**
     * Gets a <code>Site</code> object associated with a cmap ID
     * 
     * @param x ID of site to access
     * @return site corresponding to that ID
     */
    public Site getSite(Integer x) {
        return this.sites.get(x);
    }

    /**
     * Gets the length of the reference covered by this cmap ID
     * 
     * @return length of the cmap map
     */
    public Double getLength() {
        return this.contigLen;
    }

    /**
     * Gets this cmap ID
     * 
     * @return cmap ID
     */
    public Integer getID() {
        return this.ID;
    }

}
