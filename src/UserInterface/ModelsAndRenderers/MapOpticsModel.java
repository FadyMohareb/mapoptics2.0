package UserInterface.ModelsAndRenderers;

import DataTypes.Reference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains data of reference and query, and their alignments.
 *
 * @author James
 */
public class MapOpticsModel {

    private File refFile, qryFile, xmapFile;
    private static String selectedRefID;
    private Reference selectedRef;
    private boolean isReversed;
    private List<Double> lengths;
    private List<Double> densities;
    private List<Reference> references;
    private double rectangleTotalWidth;
    private double rectangleTotalHeight;

    /**
     * Constructor
     */
    public MapOpticsModel() {
        isReversed = false;
        selectedRefID = "";
        lengths = new ArrayList<>();
        densities = new ArrayList<>();
        references = new ArrayList<>();
    }

    /**
     * Swap the reference and the query
     */
    public void swapRefQry() {
        File oldRef = refFile;
        refFile = qryFile;
        qryFile = oldRef;

        setReversed(!isReversed);
    }

    /**
     * Indicates if reference and query were swapped
     *
     * @return boolean is reversed
     */
    public boolean isReversed() {
        return isReversed;
    }

    /**
     * Sets reference and query as reversed
     *
     * @param reversed boolean indicating if reference and query are reversed
     */
    public void setReversed(boolean reversed) {
        isReversed = reversed;
    }

    /**
     * Gest reference file
     *
     * @return refFile reference file
     */
    public File getRefFile() {
        return refFile;
    }

    /**
     * Sets reference file
     *
     * @param refFile reference file
     */
    public void setRefFile(File refFile) {
        this.refFile = refFile;
    }

    /**
     * Gets query file
     *
     * @return qryFile query file
     */
    public File getQryFile() {
        return qryFile;
    }

    /**
     * Sets query file
     *
     * @param qryFIle query file
     */
    public void setQryFile(File qryFIle) {
        this.qryFile = qryFIle;
    }

    /**
     * Gets xmap file
     *
     * @return xmap file
     */
    public File getXmapFile() {
        return xmapFile;
    }

    /**
     * Sets xmap file
     *
     * @param xmapFile xmap file
     */
    public void setXmapFile(File xmapFile) {
        this.xmapFile = xmapFile;
    }

    /**
     * Sets lengths of query and reference
     *
     * @param lengths list of lengths
     */
    public void setLengths(List<Double> lengths) {
        this.lengths = lengths;
    }

    /**
     * Sets density of query and reference
     *
     * @param densities list of densities
     */
    public void setDensities(List<Double> densities) {
        this.densities = densities;
    }

    /**
     * Gets selected reference id
     *
     * @return selected reference ID
     */
    public String getSelectedRefID() {
        return selectedRefID;
    }

    /**
     * Gets lengths
     *
     * @return list of lengths
     */
    public List<Double> getLengths() {
        return lengths;
    }

    /**
     * Sets selected reference id
     *
     * @param selectedRow row of reference of interest
     */
    public void setSelectedRefID(String selectedRow) {
        selectedRefID = selectedRow;
        setSelectedRef(selectedRow);
    }

    /**
     * Gets densities
     *
     * @return list of densities
     */
    public List<Double> getDensities() {
        return densities;
    }

    /**
     * Set references
     *
     * @param references list of references
     */
    public void setReferences(List<Reference> references) {
        this.references = references;
    }

    /**
     * Gets references
     *
     * @return list of references
     */
    public List<Reference> getReferences() {
        return references;
    }

    /**
     * Sets selected reference
     *
     * @param refID id of selected reference
     */
    private void setSelectedRef(String refID) {

        if (refID.isEmpty()) {
            selectedRef = null;
        }
        for (Reference reference : references) {
            if (reference.getRefID().equals(refID)) {
                selectedRef = reference;
            }
        }
    }

    /**
     * Gets selected reference
     *
     * @return selected reference
     */
    public Reference getSelectedRef() {
        return selectedRef;
    }

    /**
     * Sets rectangle width
     *
     * @param rectangleTotalWidth total width of rectangle
     */
    public void setRectangleTotalWidth(double rectangleTotalWidth) {
        this.rectangleTotalWidth = rectangleTotalWidth;
    }

    /**
     * Gets rectangle width
     *
     * @return total width of rectangle
     */
    public double getRectangleTotalWidth() {
        return rectangleTotalWidth;
    }

    /**
     * Sets rectangle height
     *
     * @param rectangleTotalHeight total height of rectangle
     */
    public void setRectangleTotalHeight(double rectangleTotalHeight) {
        this.rectangleTotalHeight = rectangleTotalHeight;
    }

    /**
     * Gets rectangle total height
     *
     * @return total height of rectangle
     */
    public double getRectangleTotalHeight() {
        return rectangleTotalHeight;
    }

    private static ArrayList<String> qryIds;
    private static Map<String, String> qrylen;
    private static Map<String, String> reflen;

    /**
     * Sets list of queries
     *
     * @param qryIDs hashmap of queries, containing their IDs (the key) and
     * their value
     */
    public static void setQueryList(Map<String, String> qryIDs) {
        qryIds = new ArrayList<>(qryIDs.keySet());
        qrylen = new HashMap<>(qryIDs);
    }

    /**
     * Gets list of queries
     * @return array list of queries IDs
     */
    public ArrayList<String> getQueryList() {
        return qryIds;
    }

    /**
     * Gets length of queries
     * 
     * @return length of queries
     */
    public static Map<String, String> getQueryLen() {
        return qrylen;
    }

    /**
     * Sets list of references
     * 
     * @param refIDs Hashmap of references, the key is their ID
     */
    public static void setRefList(Map<String, String> refIDs) {
        reflen = new HashMap<>(refIDs);
    }

    /**
     * Gets length of referecnes
     * 
     * @return Hashmap of references lengths, the key is the reference ID
     */
    public static Map<String, String> getRefLen() {
        return reflen;
    }
}
