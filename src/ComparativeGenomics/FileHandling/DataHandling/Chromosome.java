package ComparativeGenomics.FileHandling.DataHandling;

import ComparativeGenomics.StructuralVariant.*;
import ComparativeGenomics.FileHandling.Cmap;
import ComparativeGenomics.StructuralVariant.Duplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
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
     * Set new alignment
     * @param map ArrayList of XmapData (corresponding to one line of an XMAP file)
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
     * Add a translocation to the list of translocations in the chromosomes
     * @param transloc New translocation
     */
    public void addTranslocation(Translocation transloc){
        this.translocations.add(transloc);
    }

    /**
     * Get number of indels in chromosome
     * @return Number of indels
     */
    public Integer getNumIndels() {
        return this.indels.size();
    }

    /**
     * Get number of inversions in chromosome
     * @return Number of inversions
     */
    public Integer getNumInversions() {
        return this.inversions.size();
    }

    /**
     * Get number of translocations in chromosome
     * @return  Translocations number
     */
    public Integer getNumTranslocations() {
        return this.translocations.size();
    }

    /**
     * Get number of duplications in chromosome
     * @return duplication number
     */
    public Integer getNumDuplications() {
        return this.duplications.size();
    }

    /**
     * Get chromosome size
     * @return size chromosome size
     */
    public Double getSize() {
        return this.size;
    }

    /** 
     * Get cmap ID associated with the chromosome
     * @return refCmapMap CMAP ID associated with the chromosome
     */
    public Integer getCmapID() {
        return this.refCmapMap.getID();
    }

    public CmapData getRefCmapData() {
        return this.refCmapMap;
    }

    public HashMap<Integer, Site> getRefSites() {
        return this.refSites;
    }

    public CmapData getQryCmapsByID(Integer ID) {
        return this.qryCmapMaps.get(ID);
    }

    public LinkedHashMap<Integer, CmapData> getQryCmaps() {
        return this.qryCmapMaps;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Indel> getIndels() {
        return this.indels;
    }

    public Sequence getSequence() {
        return this.sequence;
    }

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

    public ArrayList<XmapData> getAlignments() {
        return this.xmapMaps;
    }

    public ArrayList<Gene> getAnnotations() {
        return this.features;
    }

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
     * Add duplication object to the arrayList
     * @param dup 
     */
    public void addDuplication(Duplication dup) {
        this.duplications.add(dup);
    }

    /**
     * Add inversion object to the arrayList
     * @param inversion 
     */
    public void addInversion(Inversion inversion) {
        this.inversions.add(inversion);
    }


    /**
     * Add Indel object to the arrayList
     * @param indel 
     */
    public void addIndel(Indel indel) {
        this.indels.add(indel);
    }
}
