package ComparativeGenomics.ServerHandling;

/**
 * Saves all of the information about an external server to enable mapoptics to
 * connect to it
 *
 * @author franpeters
 */
public class ExternalServer {

    public String name;
//    set the properties of the server required to access it
    private String user;
    private String host;
    private String password;
//    where the files will be saved within the server, bash script is and where results will be
    private String workingDir;

    /**
     * Constructor with server name and user name, ip address, password, working
     * directory
     *
     * @param name name of server
     * @param user login user name
     * @param host host IP address
     * @param password password of login
     * @param workingDir the directory whereby run_job.sh, calc_best_enz.sh and
     * enzymes.txt as been saved to
     */
    public ExternalServer(String name, String user, String host, String password, String workingDir) {
        this.name = name;
        this.user = user;
        this.host = host;
        this.password = password;
        this.workingDir = workingDir;
    }

    /**
     * Sets this servers name
     *
     * @param name server name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets this servers username
     *
     * @param user server username
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Sets this servers host IP address
     * @param host host IP address
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets this servers password
     * 
     * @param password server password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets this servers working directory
     * 
     * @param workingDir directory whereby run_job.sh, calc_best_enz.sh and enzymes.txt as been saved to
     */
    public void setUpDir(String workingDir) {
        this.workingDir = workingDir;
    }

    /**
     * Sets this servers name
     * 
     * @return name of server
     */
    public String getName() {
        return this.name;
    }

    /**
     * sets this servers username
     * 
     * @return username of server
     */
    public String getUser() {
        return this.user;
    }

    /**
     *Sets this servs host address
     * 
     * @return host address of server
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Sets this servers password
     * 
     * @return password of server
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets this servers working directory
     * 
     * @return working directory of server
     */
    public String getWorkingDir() {
        return this.workingDir;
    }

}
