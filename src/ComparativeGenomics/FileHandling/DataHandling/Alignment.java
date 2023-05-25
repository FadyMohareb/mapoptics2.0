package ComparativeGenomics.FileHandling.DataHandling;

import ComparativeGenomics.StructuralVariant.Translocation;
import ComparativeGenomics.FileHandling.Xmap;
import ComparativeGenomics.FileHandling.Cmap;
import ComparativeGenomics.FileHandling.Annot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ComparativeGenomics.StructuralVariant.Duplication;
import ComparativeGenomics.StructuralVariant.Indel;
import ComparativeGenomics.StructuralVariant.Inversion;
import java.util.Objects;

/**
 *
 * @author franpeters 
 * Stores the alignment files produced by the job
 */
public final class Alignment {

    private Cmap cmapRef;
    private Cmap cmapQry;
    private Xmap xmap;
    private Genome refGenome;
    private Genome qryGenome;
    private ArrayList<Integer> refCmapIDs = new ArrayList();
    private ArrayList<Translocation> translocations = new ArrayList();


//    SV detection settings (Can be altered by user)
    Integer distance =  30000;
    Integer minIndelSize = 500;
    Integer duplicationMin = 2;

    /**
     * 
     * @param refGenome
     * @param qryGenome
     * @param xmap 
     */
    public Alignment(Genome refGenome, Cmap qryCmap, Xmap xmap) {
        this.refGenome = refGenome;
        this.refCmapIDs = this.refGenome.getCmapIDs();
        this.cmapRef = this.refGenome.getCmap();
        this.cmapQry = qryCmap;
        this.xmap = xmap;

        for (Map.Entry<Integer, ArrayList<XmapData>> entry : this.xmap.getXmap().entrySet()) {
            Integer chrID = entry.getKey();
            ArrayList<XmapData> value = entry.getValue();
            for (XmapData map : value) {
                CmapData r = this.cmapRef.getCmapByID(map.getRefID());
                CmapData q = this.cmapQry.getCmapByID(map.getQryID());
//              populate the Pair objects with Site objects
                if (q != null & r != null) {
                    for (Pair pair : map.returnAlignments()) {
                        Integer refID = pair.getRef();
                        Integer qryID = pair.getQry();
                        if(r.getSite(refID)!=null & q.getSite(qryID)!=null){
                            r.getSite(refID).addMatch(map.getID(),new Match(map.getQryID(),true,q.getSite(qryID)));
                            q.getSite(qryID).addMatch(map.getID(),new Match(map.getRefID(),true, r.getSite(refID)));
                            pair.setSite(r.getSite(refID), q.getSite(qryID));
                            
                    }
                       
                    }
                }
            }
        }


        setAlignments();
        for (Chromosome chr : this.refGenome.getChromosomes().values()) {
           
//            detect inversions
             for (XmapData xmap1: chr.getAlignments()){
                Integer id = xmap1.getID();
                for (XmapData xmap2: this.xmap.getAllXmaps().values() ){
//                    check not comparing to itself
                    if (!Objects.equals(xmap2.getID(), id)&Objects.equals(xmap1.getRefID(), xmap2.getRefID())){
//                        detect duplications
//                        detectDuplications(xmap1,xmap2,duplicationMin);
                    }
                }
            }
        }
        detectTranslocations();
        
    }

    public String getRefGenomeName() {
        return this.refGenome.getName();
    }

    public Xmap getXmap() {
        return this.xmap;
    }

    public Cmap getCmapQry() {
        return this.cmapQry;
    }

    public Cmap getCmapRef() {
        return this.cmapRef;
    }

    /**
     * Assigns each chromosome in a genome a cmap file with all the
     * digestion sites corresponding to that chromosome
     */
    private void setAlignments() {
//       where a refcmapid refers to a chromosome as each cmap refers to one chromosome
        for (int i = 0; i < this.refCmapIDs.size(); i++) {
//            cmapID = chromosome
            Integer cmapID = this.refCmapIDs.get(i);
//            this accesses all of the alignments related to that cmap ID
            if (this.xmap.getXmap().containsKey(cmapID)) {
//                these are all the qry cmap matches to the ref cmap
                ArrayList<XmapData> chrAlignments = this.xmap.getXmap().get(cmapID);

                if (this.refGenome.getChromosomes().get(cmapID) != null) {
                    this.refGenome.getChromosomes().get(cmapID).setAlignment(chrAlignments, cmapQry);

                }
            } 
        }
    }


    public Genome getRefGenome() {
        return this.refGenome;
    }

//    public Genome getQryGenome() {
//        return this.qryGenome;
//    }

    public ArrayList<Translocation> getTranslocations() {
        return this.translocations;
    }



    public void detectTranslocations() {
        
//        making translocation objects
         for (Map.Entry<Integer, ArrayList<XmapData>> entry : this.xmap.getPotentialTranslocations().entrySet()) {
                Integer key = entry.getKey();
                ArrayList<XmapData> value = entry.getValue();
                System.out.println(value.size() + " translocations?");
//        first only going to deal with scenarios where there are only two different chromosomes affected
                boolean twoChrs = value.stream().distinct().count() <= 2;
                if(twoChrs){
                  
//                    get the two chromosomes involved cmap id's
                    List<XmapData> distinctChrs = value.stream().distinct().collect(Collectors.toList());
                    Translocation translocation = new Translocation(key, 
                                                                    distinctChrs.get(0),
                                                                    distinctChrs.get(1),
                                                                    this.refGenome.getChromosomes()
                                                                                  .get(distinctChrs.get(0)),
                                                                    this.refGenome.getChromosomes()
                                                                                  .get(distinctChrs.get(1)));
                    translocations.add(translocation);
                    
            }
        }
    }
   
//   private ArrayList<Duplication> detectDuplications(XmapData xmap1, XmapData xmap2, int duplicationMin) {
//        ArrayList<Duplication> duplications =  new ArrayList();
//        ArrayList<Site> duplicatedSites = new ArrayList();
//        ArrayList<Pair> pairs1 = xmap1.returnAlignments();
//        ArrayList<Pair> pairs2 = xmap2.returnAlignments();
//        if (Objects.equals(xmap1.getRefID(), xmap2.getRefID())){
//            int pairsSize1 = pairs1.size();
//            int pairsSize2 = pairs2.size();
//            for (int x = 0; x < pairsSize1; x++) {
//                Pair pair1 = pairs1.get(x);
//                for (int y = 0; y < pairsSize2; y++) {
//                    Pair pair2 = pairs2.get(y);
//                    if (!Objects.equals(pair1.getRef(), pair2.getRef())) {
//                        pair1.getRefSite().setDuplicated(true);
//                        duplicatedSites.add(pair1.getRefSite());
//                    }
//                }
//            }
//            if (duplicatedSites.size() > duplicationMin) {
//                Duplication dup = new Duplication(xmap1.getRefID(), duplicatedSites);
//                duplications.add(dup);
//            }
//        }
//        if (duplications.isEmpty()){
//            return null;
//        }else{
//            return duplications; 
//        }
//    }
//
//    public void setDuplicationMin(int i) {
//        this.duplicationMin = i;
//    }
}
