package ComparativeGenomics.ServerHandling;

/**
 * Saves all information related to a submitted job, allowing the data to be downloaded from 
 * the server it was run on, and displaying the results.
 * 
 * @author franpeters
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
     * Constructor with server, job name, reference and query file names, 
     * enzyme, pipeline, status, organisms and annotations files
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
    public Job(ExternalServer server, String name, String ref, String qry, Enzyme enzyme, 
            String pipeline, String status, String qOrg, String rOrg, String rAnnot, String qAnnot) {
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
     * Constructor
     */
    public Job() {
    }

    /**
     * Sets name of this job
     * 
     * @param name job name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets external server on which this job is running. 
     * This external server information is used when the status or results of this job are queried.
     * 
     * @param server external server on which this job is run
     */
    public void setServer(ExternalServer server) {
        this.server = server;
    }

    /**
     * Sets this job reference fasta file
     * 
     * @param file set the reference file path
     */
    public void setRefFile(String file) {
        this.refFile = file;
    }

    /**
     * Sets this job reference query file
     * 
     * @param file set the query file path
     */
    public void setQryFile(String file) {
        this.qryFile = file;
    }

    /**
     * Sets this job restriction enzyme, which digests either the reference or query
     * 
     * @param enzyme restriction enzyme for this job
     */
    public void setEnzyme(Enzyme enzyme) {
        this.enzyme = enzyme;
    }

    /**
     * Sets the pipeline, indicating which alignment algorithm (FaNDOM or RefAligner)
     * is used to run this job
     * 
     * @param pipeline alignment algorithm
     */
    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    /**
     * Sets the status of this job, saved in the external server in a log file.
     * 
     * @param status latest status of this job
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the reference organism of this job.
     * The organism name is used when querying the database of SVs.
     * 
     * @param org reference species, including the genus name i.e. Homo sapiens
     */
    public void setRefOrg(String org) {
        this.refOrganism = org;
    }

    /**
     *
     * Sets the query organism name.
     * The organism name is used when querying the database of SVs.

     * @param org query species, including the genus name i.e. Homo sapiens
     */
    public void setQryOrg(String org) {
        this.qryOrganism = org;
    }

    /**
     * Sets the reference annotation file for this job
     * 
     * @param annot filepath of the reference GTF or GFF file for the reference genome
     */
    public void setRefAnnot(String annot) {
        this.refAnnot = annot;
    }

    /**
     * Sets query annotation file for this job
     * 
     * @param annot filepath of the reference GTF or GFF file for the query genome
     */
    public void setQryAnnot(String annot) {
        this.qryAnnot = annot;
    }

    /**
     * Gets job status
     * @return get status of the job
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Gets this jobs name
     * 
     * @return name of the job
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the information of the server on which this job is run
     * 
     * @return external server
     */
    public ExternalServer getServer() {
        return this.server;
    }

    /**
     * Gets the alignment algorithm used to execute this job
     * 
     * @return alignment algorithm
     */
    public String getPipeline() {
        return this.pipeline;
    }

    /**
     * Gets the reference genome file of this job
     * 
     * @return reference genome file path
     */
    public String getRef() {
        return this.refFile;
    }

    /**
     * Gets the query genome file path of this job
     * 
     * @return query genome file path
     */
    public String getQry() {
        return this.qryFile;
    }

    /**
     * Gets the restriction enzyme used in this job
     * 
     * @return restriction enzyme
     */
    public Enzyme getEnz() {
        return this.enzyme;
    }

    /**
     * Gets the reference species
     * 
     * @return reference species
     */
    public String getRefOrg() {
        return this.refOrganism;
    }

    /**
     * Gets the query species
     * 
     * @return query species
     */
    public String getQryOrg() {
        return this.qryOrganism;
    }

    /**
     * Gets the reference annotation file path
     * 
     * @return reference annotation file path
     */
    public String getRefAnnot() {
        return this.refAnnot;
    }

    /**
     * Gets the query annotation file path
     * 
     * @return query annotation file path
     */
    public String getQryAnnot() {
        return this.qryAnnot;
    }
}
