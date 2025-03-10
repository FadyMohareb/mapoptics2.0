package ComparativeGenomics.FileHandling.DataHandling;

import ComparativeGenomics.StructuralVariant.*;
import ComparativeGenomics.FileHandling.Cmap;
import ComparativeGenomics.StructuralVariant.Duplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Combines the CmapData, XmapData, annotations (gene), structural variants and
 * context of the chromosome within a genom
 *
 * @author franpeters
 */
public class Chromosome {

    private String name = "";
    private final CmapData refCmapMap; //this contains all of the site information related to that chromosome
    private ArrayList<XmapData> xmapMaps = new ArrayList(); //this contains all of the alignments that have been matched to this CmapData

    //to link the XmapData obj to its corresponding CmapData obj.
    // The key is the cmap ID, the value is the corresponding cmap object
    private LinkedHashMap<Integer, CmapData> qryCmapMaps = new LinkedHashMap();
    private Sequence sequence;
    private ArrayList<Indel> indels = new ArrayList();
    private Double size;
    private ArrayList<Double> gcAcrossChr = new ArrayList();
    private LinkedHashMap<Integer, Site> refSites = new LinkedHashMap(); //where the key is the site id
    private ArrayList<String> genesOnIndels = new ArrayList();
    private ArrayList<Duplication> duplications = new ArrayList();
    private ArrayList<Inversion> inversions = new ArrayList();
    private ArrayList<Translocation> translocations = new ArrayList();
    private ArrayList<Gene> features = new ArrayList();

    /**
     * Constructor of class Chromosome
     *
     * @param name Chromosome name
     * @param cmap Cmap data for the considered chromosome
     * @param relsize Relative size of the chromosome
     * @param seq Sequence of the chromosome
     * @param genes List of genes on the chromosome
     */
    public Chromosome(String name, CmapData cmap, Double relsize, Sequence seq, ArrayList<Gene> genes) {
        this.name = name;
        this.refCmapMap = cmap;
        this.size = this.refCmapMap.getLength();
        this.refSites = this.refCmapMap.getSites();
        this.sequence = seq;
        this.features = genes;
    }

    /**
     * Sets new alignment: adds the alignment file to the chromosome object
     *
     * @param map ArrayList of XmapData (corresponding to one line of an XMAP
     * file)
     * @param qryCmap CMAP data of the query
     */
    public void setAlignment(ArrayList<XmapData> map, Cmap qryCmap) {
        Integer s = map.size();
        for (Integer i = 0; i < s; i++) {
//            for each alignment object find the sites that match
            XmapData align = map.get(i);
//            add indels
            indels.addAll(align.getIndels());
//            add the XmapData to the arrayList
            this.xmapMaps.add(align);
            CmapData qryCmapMap = qryCmap.getCmapByID(align.getQryID());
//            save to hashmap
            qryCmapMaps.put(align.getQryID(), qryCmapMap);
        }
    }

    /**
     * Adds identified translocation to the array list of translocations in the
     * chromosomes
     *
     * @param transloc New translocation
     */
    public void addTranslocation(Translocation transloc) {
        this.translocations.add(transloc);
    }

    /**
     * Gets number of indels in chromosome
     *
     * @return Number of indels
     */
    public Integer getNumIndels() {
        return this.indels.size();
    }

    /**
     * Gets number of inversions in chromosome
     *
     * @return Number of inversions
     */
    public Integer getNumInversions() {
        return this.inversions.size();
    }

    /**
     * Gets the number of translocations in chromosome
     *
     * @return Translocations number
     */
    public Integer getNumTranslocations() {
        return this.translocations.size();
    }

    /**
     * Gets number of duplications in chromosome
     *
     * @return duplication number
     */
    public Integer getNumDuplications() {
        return this.duplications.size();
    }

    /**
     * Gets chromosome size
     *
     * @return size chromosome size
     */
    public Double getSize() {
        return this.size;
    }

    /**
     * Gets cmap ID associated with the chromosome
     *
     * @return refCmapMap CMAP ID associated with the chromosome
     */
    public Integer getCmapID() {
        return this.refCmapMap.getID();
    }

    /**
     * Gets the CmapData associated with this chromosome
     *
     * @return refCmapMap reference cmap map
     */
    public CmapData getRefCmapData() {
        return this.refCmapMap;
    }

    /**
     * Gets the hashmap containing all the sites within this chromosome, with the
     * key being the site ID and the value being the site object
     *
     * @return refSites hashmap of sites
     */
    public HashMap<Integer, Site> getRefSites() {
        return this.refSites;
    }

    /**
     * Gets a query CmapData object by ID
     *
     * @param ID id of query Cmap
     *
     * @return qryCmapMaps corresponding query cmap object
     */
    public CmapData getQryCmapsByID(Integer ID) {
        return this.qryCmapMaps.get(ID);
    }

    /**
     * Gets all of the CmapData objects associated with this query Cmap
     *
     * @return qryCmapsMaps query cmap maps
     */
    public LinkedHashMap<Integer, CmapData> getQryCmaps() {
        return this.qryCmapMaps;
    }

    /**
     * Get the names of the chromosome
     *
     * @return name chromosome name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get all the indels of this chromosome
     *
     * @return chromosome's indels
     */
    public ArrayList<Indel> getIndels() {
        return this.indels;
    }

    /**
     * Access a specific sequence from the sequences HashMap by sequence size
     *
     * @return sequence sequence of chromosomes
     */
    public Sequence getSequence() {
        return this.sequence;
    }

    /**
     * GC% in windows across this chromosome
     *
     * @return array array of GC%
     */
    public ArrayList<Double> getGCAcrossSequence() {
        ArrayList<Double> array = new ArrayList();
        String str = this.sequence.getSeq();
        for (int i = 0; i < size; i += 1000) {
            if (i < size - (1000 + 1)) {
                String subset = str.substring(i, i + 1000);
                Double gc = calculateGC(subset).doubleValue() / (double) 1000.0 * 100;
                array.add(gc);
            }
        }
        return array;
    }

    /**
     * Calculates GC% of the sequence associated with this chromosome
     *
     * @param str sequence associated with this chromosome
     * @return gcCount GC%
     */
    private Integer calculateGC(String str) {
        Integer gcCount = 0;
        char[] seq = str.toCharArray();
        for (char c : seq) {
            if (c == 'G' | c == 'C' | c == 'c' | c == 'g') {
                gcCount++;
            }
        }
        return gcCount;

    }

    /**
     * Gets all the alignments to this chromosome
     *
     * @return xmapMaps alignments
     */
    public ArrayList<XmapData> getAlignments() {
        return this.xmapMaps;
    }

    /**
     * Gets all <code>Gene</code> objects added to this <code>Chromosome</code>
     *
     * @return features genes
     */
    public ArrayList<Gene> getAnnotations() {
        return this.features;
    }

    /**
     * Annotate <code>Indel</code> objects with <code>Gene</code>
     */
    public void annotateIndels() {
        if (!this.indels.isEmpty()) {
            for (Indel indel : this.indels) {
                Double start = indel.getStart();
                Double end = indel.getEnd();
                if (features != null) {
                    Integer size3 = features.size();
                    for (Gene feature : features) {
                        Double fStart = feature.getStart();
                        Double fEnd = feature.getStart();
                        if (((fEnd < start) == true) || ((fStart > end) == true)) {
                            continue;
                        } else {
                            if (fStart > start && fEnd < end) {
                                indel.addFeature(feature);
                                if (!genesOnIndels.contains(feature.getName())) {
                                    genesOnIndels.add(feature.getName());
                                    indel.addFeature(feature);
                                }
                            }
                            if (fStart < start && fEnd < end) {
                                indel.addFeature(feature);
                                if (!genesOnIndels.contains(feature.getName())) {
                                    genesOnIndels.add(feature.getName());
                                    indel.addFeature(feature);

                                }
                            }
                            if (fStart > start && fStart < end && fEnd > end) {
                                indel.addFeature(feature);
                                if (!genesOnIndels.contains(feature.getName())) {
                                    genesOnIndels.add(feature.getName());
                                    indel.addFeature(feature);

                                }
                            }
                            if (fStart < end && fEnd > end) {
                                indel.addFeature(feature);
                                if (!genesOnIndels.contains(feature.getName())) {
                                    genesOnIndels.add(feature.getName());
                                    indel.addFeature(feature);

                                }
                            }
                        }
                    }

                }

            }
        }
    }

    /**
     * Add duplication object to the <code>ArrayList</code>
     *
     * @param dup duplication to add to this chromosome
     */
    public void addDuplication(Duplication dup) {
        this.duplications.add(dup);
    }

    /**
     * Add inversion object to the <code>ArrayList</code>
     *
     * @param inversion inversion to add to this chromosome
     */
    public void addInversion(Inversion inversion) {
        this.inversions.add(inversion);
    }

    /**
     * Add <code>Indel</code> object to the arrayList
     *
     * @param indel to add to this chromosome
     */
    public void addIndel(Indel indel) {
        this.indels.add(indel);
    }
}
