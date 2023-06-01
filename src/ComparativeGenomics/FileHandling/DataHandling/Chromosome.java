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
    private LinkedHashMap<Integer, CmapData> qryCmapMaps = new LinkedHashMap(); //to link the XmapData obj to its corresponding CmapData obj. 
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

    public Chromosome(String name, CmapData cmap, Double relsize, Sequence seq, ArrayList<Gene> genes) {
        this.name = name;
        this.refCmapMap = cmap;
        this.size = this.refCmapMap.getLength();
        this.refSites = this.refCmapMap.getSites();
        this.sequence = seq;
        this.features = genes;
    }

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
////            save to hashmap
            qryCmapMaps.put(align.getQryID(), qryCmapMap);
        }
//        
    }

    public Integer getNumIndels() {
        return this.indels.size();
    }

    public Integer getNumInversions() {
        return this.inversions.size();
    }

    public Integer getNumTranslocations() {
        return 0;
    }

    public Integer getNumDuplications() {
        return this.duplications.size();
    }

    public Double getSize() {
        return this.size;
    }

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

    public Sequence getSequnce() {
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
//                                    System.out.println(feature.getName());
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

    public void addDuplication(Duplication dup) {
        this.duplications.add(dup);
    }

    public void addInversion(Inversion inversion) {
        this.inversions.add(inversion);
    }

    public void addTranslocation(Translocation trans) {
        this.translocations.add(trans);
    }

    public void addIndel(Indel indel) {
        this.indels.add(indel);
    }
}
