package ComparativeGenomics.ServerHandling;

/**
 *
 * @author franpeters
 * ExternalServer’s purpose is to save all of the information about an external server to enable mapoptics to connect to it.
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
     * 
     * @param name name of server
     * @param user login user name
     * @param host host IP address
     * @param password password of login
     * @param workingDir the directory whereby run_job.sh, calc_best_enz.sh and enzymes.txt as been saved to 
     */
    public ExternalServer(String name, String user, String host, String password, String workingDir ){
        this.name = name;
        this.user = user;
        this.host = host;
        this.password = password;
        this.workingDir = workingDir;
    }
    /**
     * 
     * @param name set name of server
     */
    public void setName(String name){
        this.name = name;
    }
    /**
     * 
     * @param user set username of server
     */
    public void setUser(String user){
        this.user = user;
    }
    /**
     * 
     * @param host set host IP address
     */
    public void setHost(String host){
        this.host = host;
    }
    /**
     * 
     * @param password set server password
     */
    public void setPassword(String password){
        this.password = password;
    }
    /**
     * 
     * @param workingDir set the directory whereby run_job.sh, calc_best_enz.sh and enzymes.txt as been saved to 
     */
    public void setUpDir(String workingDir){
        this.workingDir = workingDir;
    }
    /**
     * 
     * @return name of server
     */
    public String getName(){
        return this.name;
    }
    /**
     * 
     * @return username of server
     */
    public String getUser(){
        return this.user;
    }
    /**
     * 
     * @return host address of server
     */
    public String getHost(){
        return this.host;
    } 
    /**
     * 
     * @return password of server
     */
    public String getPassword(){
        return this.password;
    }
    /**
     * 
     * @return working directory of server
     */
    public String getWorkingDir(){
        return this.workingDir;
    }
  
}
