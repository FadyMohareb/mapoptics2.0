/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComparativeGenomics.FileHandling.DataHandling;

/**
 *
 * @author marie Save all the information contained in the file SMAP, output
 * from runBNG SV, that contains data about SVs detected during the alignment.
 */
public class SVRefAligner {

    private int smapID;
    private int qryContigID;
    private int refContigID1;
    private int refContigID2;
    private int qryStartPos;
    private int qryEndPos;
    private int refStartPos;
    private int refEndPos;
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
    
    public int getSmapid(){
        return this.smapID;
    }
    
    public int getQryContigID(){
        return this.qryContigID;
    }
    
    public int[] getRefContigID(){
        int[] refContigs = {this.refContigID1, this.refContigID2};
        return refContigs;
    }
    
    public int[] getQryPos(){
        int[] qryPos = {this.qryStartPos, this.qryEndPos};
        return qryPos;
    }
        
    public int[] getRefPos(){
        int[] refPos = {this.qryStartPos, this.qryEndPos};
        return refPos;
    }
    
    public String getType(){
        return this.type;
    }
    
    public int getLinkID(){
        return this.linkID;
    }
    
    public int getXmapID1(){
        return xmapID1;
    }
    
    public int getXmapID2(){
        return xmapID2;
    }
}
