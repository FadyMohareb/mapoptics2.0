/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComparativeGenomics.FileHandling.DataHandling;

/**
 * Saves all the information contained in the file SMAP, output
 * from runBNG SV, that contains data about SVs detected during the alignment.
 * 
 * @author Marie Schmit
 */
public class SVRefAligner {

    private int smapID;
    private int qryContigID; // Map ID of query map (contig ID from .cmap)
    private int refContigID1; // Map ID of the reference map from the .cmap reference file
    private int refContigID2; // Same as refContigID1 for insertions, deletions, duplications, inversions
    private int qryStartPos; //Start of SV on query map
    private int qryEndPos;
    private int refStartPos; // Coordinate of reference contig1 aligned position which borders the SV
    private int refEndPos; // Coordinate of reference contig2 which borders the SV
    private int confidence;
    private String type;
    private int xmapID1;
    private int xmapID2;
    private int linkID;
    private int qryStartIdx;
    private int qryEndIdx;
    private int refStartIdx;
    private int refEndIdx;
    private String zygosity;
    private int genotype;
    private int genotypeGroup;
    private double rawConfidence;
    private double rawConfidenceLeft;
    private double rawConfidenceRight;
    private double rawConfidenceCenter;
    private double SVsize;
    private double SVfreq;
    private String orientation;

    /**
     * Constructor with all the data from a line of this parsed file
     * 
     * @param smapid unique number for an entry in the SMAP file
     * @param qrycontigid map ID of query map
     * @param refcontigid1 reference contig ID from the cmap reference file (xmapID1)
     * @param refcontigid2 contig ID from the cmap reference file (xmapID2)
     * @param qrystartpos start of SV on the query map
     * @param qryendpos end of SV on the query map
     * @param refstartpos coordinate of reference contig 1 aligned position which borders the considered SV
     * @param refendpos of reference contig 2 aligned position which borders the considered SV
     * @param confidence estimate of probability of being correct for SVs
     * @param type Type of SV
     * @param xmapid1 xmap entry ID in the xmap file of the first alignment from which this SV is derived
     * @param xmapid2 entry ID in the xmap file of the second alignment from which this SV is derived
     * @param linkid link between two SMAP entries
     * @param qrystartid index in query map of site nearest to QryStartPos
     * @param qryendid in query map of site nearest to QryEndPos
     * @param refstartid index in query map of site nearest to RefStartPos
     * @param refendid index in query map of site nearest to RefEndPos
     * @param zygosity "homozygous" or "heterozygous" or "unknown"
     * @param genotype "1" for homozygous SV, "2" for heterozygous, "-1" for unknown
     * @param genotypegroup indels overlapping one another and belong to the same size cluster
     * @param rawconfidence minimum of next confidences for Indels, "-1" for other SVs
     * @param rawconfidenceleft confidence of alignment to the left
     * @param rawconfidenceright of alignment to the right
     * @param rawconfidencecenter outlier confidence for indels only
     * @param svsize estimated size of SV
     * @param svfreq SV frequency
     * @param orientation orientation, only for translocations
     */
    public SVRefAligner(int smapid, int qrycontigid, int refcontigid1, int refcontigid2, int qrystartpos, int qryendpos, int refstartpos,
            int refendpos, int confidence, String type, int xmapid1, int xmapid2, int linkid, int qrystartid, int qryendid, int refstartid, int refendid, String zygosity,
            int genotype, int genotypegroup, double rawconfidence, double rawconfidenceleft, double rawconfidenceright, double rawconfidencecenter, double svsize, double svfreq, String orientation) {

        this.smapID = smapid;
        this.qryContigID = qrycontigid;
        this.refContigID1 = refcontigid1;
        this.refContigID2 = refcontigid2;
        this.qryStartPos = qrystartpos;
        this.qryEndPos = qryendpos;
        this.refStartPos = refstartpos;
        this.refEndPos = refendpos;
        this.confidence = confidence;
        this.type = type;
        this.xmapID1 = xmapid1;
        this.xmapID2 = xmapid2;
        this.linkID = linkid;
        this.qryStartIdx = qrystartid;
        this.qryEndIdx = qryendid;
        this.refStartIdx = refstartid;
        this.refEndIdx = refendid;
        this.zygosity = zygosity;
        this.genotype = genotype;
        this.genotypeGroup = genotypegroup;
        this.rawConfidence = rawconfidence;
        this.rawConfidenceLeft = rawconfidenceleft;
        this.rawConfidenceRight = rawconfidenceright;
        this.rawConfidenceCenter = rawconfidencecenter;
        this.SVsize = svsize;
        this.SVfreq = svfreq;
        this.orientation = orientation;
    }
    
    /**
     * Gets this smap id
     * 
     * @return smap id
     */
    public int getSmapid(){
        return this.smapID;
    }
    
    /**
     * Gets this query contig id
     * @return query contig id 
     */
    public int getQryContigID(){
        return this.qryContigID;
    }
    
    /**
     * Gets this reference contig id
     * @return reference id
     */
    public int[] getRefContigID(){
        int[] refContigs = {this.refContigID1, this.refContigID2};
        return refContigs;
    }
    
    /**
     * Gets this query position
     * @return query position
     */
    public int[] getQryPos(){
        int[] qryPos = {this.qryStartPos, this.qryEndPos};
        return qryPos;
    }
      
    /**
     * Gets this reference position
     * @return reference position
     */
    public int[] getRefPos(){
        int[] refPos = {this.qryStartPos, this.qryEndPos};
        return refPos;
    }
    
    /**
     * Gets this type
     * @return SV type
     */
    public String getType(){
        return this.type;
    }
    
    /**
     * Gets this smap link id
     * @return link id
     */
    public int getLinkID(){
        return this.linkID;
    }
    
    /**
     * Gets this xmap 1 id
     * @return xmap id
     */
    public int getXmapID1(){
        return xmapID1;
    }
    
    /**
     * Gets this xmap 2 id
     * @return xmap id
     */
    public int getXmapID2(){
        return xmapID2;
    }
}
