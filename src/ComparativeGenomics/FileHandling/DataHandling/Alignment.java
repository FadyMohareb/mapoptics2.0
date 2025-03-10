package ComparativeGenomics.FileHandling.DataHandling;

import ComparativeGenomics.StructuralVariant.Translocation;
import ComparativeGenomics.FileHandling.Xmap;
import ComparativeGenomics.FileHandling.Cmap;
import ComparativeGenomics.FileHandling.Annot;
import ComparativeGenomics.FileHandling.Smap;
import ComparativeGenomics.FileHandling.DataHandling.SVFandom;
import ComparativeGenomics.FileHandling.DataHandling.SVRefAligner;

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
 * Stores the alignment files produced by the job.
 *
 * @author franpeters
 * @author Marie Schmit
 */
public final class Alignment {

    private Cmap cmapRef;
    private Cmap cmapQry;
    private Xmap xmap;
    private Genome refGenome;
    private Genome qryGenome;
    private ArrayList<Integer> refCmapIDs = new ArrayList();
    private ArrayList<Translocation> translocations = new ArrayList();

    // SV detection settings (Can be altered by user)
    Integer distance = 30000;
    Integer minIndelSize = 500;
    Integer duplicationMin = 2;

    /**
     * Sets alignment with its genome, query cmap and xmap
     *
     * @param refGenome reference genome
     * @param qryCmap query cmap
     * @param xmap xmap
     */
    public Alignment(Genome refGenome, Cmap qryCmap, Xmap xmap) {
        this.refGenome = refGenome;
        this.refCmapIDs = this.refGenome.getCmapIDs();
        this.cmapRef = this.refGenome.getCmap();
        this.cmapQry = qryCmap;
        this.xmap = xmap;

        for (Map.Entry<Integer, ArrayList<XmapData>> entry : this.xmap.getXmap().entrySet()) {
            Integer chrID = entry.getKey();
            ArrayList<XmapData> value = entry.getValue(); //Each line of the xmap file
            for (XmapData map : value) {
                // Get the maps from the reference and query data
                // that have the same ID as the considered xmap
                CmapData r = this.cmapRef.getCmapByID(map.getRefID());
                CmapData q = this.cmapQry.getCmapByID(map.getQryID());
                // Populate the Pair objects with Site objects
                if (q != null & r != null) {
                    for (Pair pair : map.returnAlignments()) {
                        Integer refID = pair.getRef();
                        Integer qryID = pair.getQry();
                        if (r.getSite(refID) != null & q.getSite(qryID) != null) {
                            r.getSite(refID).addMatch(map.getID(), new Match(map.getQryID(), true, q.getSite(qryID)));
                            q.getSite(qryID).addMatch(map.getID(), new Match(map.getRefID(), true, r.getSite(refID)));
                            pair.setSite(r.getSite(refID), q.getSite(qryID));
                        }
                    }
                }
            }
        }

        setAlignments();
        for (Chromosome chr : this.refGenome.getChromosomes().values()) {

            // detect inversions
            for (XmapData xmap1 : chr.getAlignments()) {
                Integer id = xmap1.getID();
                for (XmapData xmap2 : this.xmap.getAllXmaps().values()) {
                    //check not comparing to itself
                    if (!Objects.equals(xmap2.getID(), id) & Objects.equals(xmap1.getRefID(), xmap2.getRefID())) {
//  detect duplications
//  detectDuplications(xmap1,xmap2,duplicationMin);
                    }
                }
            }
        }
        detectTranslocations();
    }

    /**
     * Gets reference genome name
     * 
     * @return reference genome name
     */
    public String getRefGenomeName() {
        return this.refGenome.getName();
    }

    /**
     * Gets xmap
     * 
     * @return xmap
     */
    public Xmap getXmap() {
        return this.xmap;
    }

    /**
     * gets query cmap
     * 
     * @return query cmap
     */
    public Cmap getCmapQry() {
        return this.cmapQry;
    }

    /**
     * Gets reference cmap data
     * 
     * @return reference cmap
     */
    public Cmap getCmapRef() {
        return this.cmapRef;
    }

    /**
     * Assigns each chromosome in a genome to a cmap file with all the digestion
     * sites corresponding to that chromosome
     */
    private void setAlignments() {
        // Where a refcmapid refers to a chromosome as each cmap refers to one chromosome
        for (int i = 0; i < this.refCmapIDs.size(); i++) {
            Integer cmapID = this.refCmapIDs.get(i);
            // This accesses all of the alignments related to that cmap ID
            if (this.xmap.getXmap().containsKey(cmapID)) {
                // These are all the qry cmap matches to the ref cmap
                ArrayList<XmapData> chrAlignments = this.xmap.getXmap().get(cmapID);
                if (this.refGenome.getChromosomes().get(cmapID) != null) {
                    this.refGenome.getChromosomes().get(cmapID).setAlignment(chrAlignments, cmapQry);
                }
            }
        }
    }

    /**
     * Gets reference genome data
     * 
     * @return reference genome
     */
    public Genome getRefGenome() {
        return this.refGenome;
    }

    /** 
     * Gets translocations data
     * 
     * @return array list of translocations
     */
    public ArrayList<Translocation> getTranslocations() {
        return this.translocations;
    }

    /**
     * Get translocations according to their position's in the array list of
     * translocation
     *
     * @return ArrayList of translocations
     * @param fromIndex first index of translocations sublist
     * @param toIndex last index of trasnlocations sublist
     */
    public ArrayList<Translocation> getLocalisedTranslocations(int fromIndex, int toIndex) {
        ArrayList<Translocation> subListTransloc = new ArrayList<Translocation>(this.translocations.subList(fromIndex, toIndex));
        return subListTransloc;
    }

    /**
     * Detect translocations from list of possible translocations from XMAP, and save them in
     * this alignment list of translocations
     */
    public void detectTranslocations() {
        // Making translocation objects
        for (Map.Entry<Integer, ArrayList<XmapData>> entry : this.xmap.getPotentialTranslocations().entrySet()) {
            Integer key = entry.getKey();
            ArrayList<XmapData> value = entry.getValue();
            List<XmapData> distinctChrs = value.stream().distinct().collect(Collectors.toList());

            // Get all the xmap by their reference contig ID
            HashMap<Integer, ArrayList<XmapData>> xmapByRefID = this.xmap.getXmapByRef();

            // Get the reference ID corresponding to the possible translocation
            for (XmapData xmapByQryLine : value) {
                // Query contig that is mapped to the same reference ID as the considered possible translocation
                for (XmapData xmapByRefLine : xmapByRefID.get(xmapByQryLine.getRefID())) {

                    int firstChrQry = xmapByRefLine.getQryID();
                    int secondChrQry = xmapByQryLine.getQryID();

                    // Translocation if the query ID share a reference with another query ID
                    if (firstChrQry != secondChrQry) {
                        // add translocation
                        Translocation translocation = new Translocation(key,
                                distinctChrs.get(0),
                                distinctChrs.get(1),
                                this.refGenome.getChromosomes()
                                        .get(firstChrQry),
                                this.refGenome.getChromosomes()
                                        .get(secondChrQry));
                        translocations.add(translocation);

                        try {
                            // Add translocation to list of translocations of first chromosome
                            this.refGenome.getChromosomes().get(firstChrQry).addTranslocation(translocation);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Detect translocations among the ones detected in SV.txt file, that
     * results from FaNDOM SV detection.
     *
     * @param smap smap containing SVs data
     */
    public void detectTxtTranslocations(Smap smap) {
        for (int i = 0; i < smap.getTxtTransloc().size(); i++) {
            SVFandom currentInput = smap.getTxtTransloc().get(i);
            for (int j = 0; j < currentInput.getIds().length; j++) {
                Translocation translocation = new Translocation(currentInput.getIds()[j],
                        this.refGenome.getChromosomes().get(currentInput.getChr1()),
                        this.refGenome.getChromosomes().get(currentInput.getChr2()));
                translocations.add(translocation);

                try {
                    // Add tran slocation to list of translocations of the first chromosome
                    this.refGenome.getChromosomes().get(currentInput.getChr1()).addTranslocation(translocation);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    /**
     * Detect inter chromosomal translocations from SMAP file resulting from
     * RefAligner translocation detection
     *
     * @param smap smap containing SVs data
     */
    public void detectSmapTranslocations(Smap smap) {
        for (int i = 0; i < smap.getSmapTransloc().size(); i++) {
            SVRefAligner currentInput = smap.getSmapTransloc().get(i);

            Translocation translocation = new Translocation(currentInput.getQryContigID(),
                    xmap.getAllXmaps().get(currentInput.getXmapID1()),
                    xmap.getAllXmaps().get(currentInput.getXmapID1()),
                    this.refGenome.getChromosomes().get(currentInput.getRefContigID()[0]),
                    this.refGenome.getChromosomes().get(currentInput.getRefContigID()[1]));

            translocations.add(translocation);
            try {
                // Add translocation to list of translocations of first chromosome
                this.refGenome.getChromosomes().get(currentInput.getRefContigID()[0]).addTranslocation(translocation);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
