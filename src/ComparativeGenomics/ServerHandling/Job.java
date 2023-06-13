package ComparativeGenomics.ServerHandling;

/**
 *
 * @author franpeters The purpose of this class is to save all of the
 * information of a submitted job allowing the data to be downloaded from the
 * server it was run on and display the results for the user.
 *
 */
public class Job {

    private String name;
    private ExternalServer server;
    private String refFile;
    private String qryFile;
    private Enzyme enzyme;
    private String pipeline;
    private String refOrganism;
    private String qryOrganism;
    private String status = "Not Started";
    private String refAnnot;
    private String qryAnnot;

    /**
     *
     * @param server the ExternalServer used to run this alignment job
     * @param name name of the job
     * @param ref reference genome file path
     * @param qry query genome file path
     * @param enzyme Enzyme object of this job
     * @param pipeline Alignment algorithm for this job
     * @param status Latest status of the submitted job
     * @param qOrg query organism species (genus and species required i.e. Homo
     * sapiens)
     * @param rOrg reference organism species (genus and species required i.e.
     * Homo sapiens)
     * @param rAnnot file path to the GTF or GFF3 annotation file for reference
     * genome
     * @param qAnnot file path to the GTF or GFF3 annotation file for query
     * genome
     */
    public Job(ExternalServer server, String name, String ref, String qry, Enzyme enzyme, String pipeline, String status, String qOrg, String rOrg, String rAnnot, String qAnnot) {
        this.server = server;
        this.name = name;
        this.refFile = ref;
        this.qryFile = qry;
        this.enzyme = enzyme;
        this.pipeline = pipeline;
        this.status = status;
        this.refOrganism = rOrg;
        this.qryOrganism = qOrg;
        this.refAnnot = rAnnot;
        this.qryAnnot = qAnnot;
    }

    /**
     *
     */
    public Job() {
    }

    /**
     *
     * @param name set the name of the job
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param server set the ExternalServer of that the job is running on. To be
     * used when querying the status of the job
     */
    public void setServer(ExternalServer server) {
        this.server = server;
    }

    /**
     *
     * @param file set the reference file path
     */
    public void setRefFile(String file) {
        this.refFile = file;
    }

    /**
     *
     * @param file set the query file path
     */
    public void setQryFile(String file) {
        this.qryFile = file;
    }

    /**
     *
     * @param enzyme set the Enzyme object used to digest the reference and
     * query fasta files
     */
    public void setEnzyme(Enzyme enzyme) {
        this.enzyme = enzyme;
    }

    /**
     *
     * @param pipeline set which alignment algorithm is to be used with this job
     */
    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    /**
     *
     * @param status set the latest status of the job from querying the log file
     * associated with the job
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @param org set the reference species, including the genus name i.e. Homo
     * sapiens. For use when querying databases
     */
    public void setRefOrg(String org) {
        this.refOrganism = org;
    }

    /**
     *
     * @param org Set the query species, including the genus name i.e. Homo
     * sapiens. For use when querying databases
     */
    public void setQryOrg(String org) {
        this.qryOrganism = org;
    }

    /**
     *
     * @param annot Set the filepath of the reference GTF or GFF file for the
     * reference genome
     */
    public void setRefAnnot(String annot) {
        this.refAnnot = annot;
    }

    /**
     *
     * @param annot Set the filepath of the reference GTF or GFF file for the
     * query genome
     */
    public void setQryAnnot(String annot) {
        this.qryAnnot = annot;
    }

    /**
     *
     * @return get status of the job
     */
    public String getStatus() {
        return this.status;
    }

    /**
     *
     * @return get the name of the job
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return get the ExternalServer of the job
     */
    public ExternalServer getServer() {
        return this.server;
    }

    /**
     *
     * @return get the alignment algorithm used by the job
     */
    public String getPipeline() {
        return this.pipeline;
    }

    /**
     *
     * @return get the reference genome file path
     */
    public String getRef() {
        return this.refFile;
    }

    /**
     *
     * @return get the query genome file path
     */
    public String getQry() {
        return this.qryFile;
    }

    /**
     *
     * @return get the Enzyme used by the job
     */
    public Enzyme getEnz() {
        return this.enzyme;
    }

    /**
     *
     * @return get the reference species
     */
    public String getRefOrg() {
        return this.refOrganism;
    }

    /**
     *
     * @return get the query species
     */
    public String getQryOrg() {
        return this.qryOrganism;
    }

    /**
     *
     * @return get the reference annotation file path
     */
    public String getRefAnnot() {
        return this.refAnnot;
    }

    /**
     *
     * @return get the query annotation file path
     */
    public String getQryAnnot() {
        return this.qryAnnot;
    }
}
