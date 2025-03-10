package ComparativeGenomics.ServerHandling;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 * Enables SSH connection to an external server, commands execution, creation of Docker container,
 * running of scripts run_job.sh and calc_best_enz.sh as well as file uploading and downloading using STFP. 
 * The externalJAR <code>Jsch</code> is used to add in external server connection.
 * All files which are downloaded from an external server are saved to a directory called "download"
 * within the MapOptics directory. The <code>Jsch</code> session is used to log into the external server.
 * The session object is used to create a channel which enables functionalities such as file transfer or 
 * commands execution.
 * 
 * @author franpeters
 * @author Marie Schmit
 */
public class SSH {

    JSch jsch = new JSch();
    ExternalServer server;
//    For connection to an ssh server
    Session session;
//    for remote command execution
    Channel channel;
    ChannelExec execChannel;

//    for remote file transfer
    ChannelSftp sftpChannel;

    SftpProgressMonitor fileProgress;
    String command;

    Job job;
    ProgressMonitor monitor; // Class to monitor the progress of some operation
//    Has a connection been established? Need to confirm before perfoming file transfers etc.
    Boolean connection = false;
    InputStream in;

    /**
     * Constructor
     */
    public SSH() {
        this.monitor = new ProgressMonitor();
    }

    /**
     * Constructor with job data
     * 
     * @param job job 
     */
    public SSH(Job job) {
        this.job = job;
        this.job.getServer();
        this.monitor = new ProgressMonitor();
    }

    /**
     * Constructor with external server and progress bar
     * 
     * @param server external server of the session
     * @param bar progress bar indicating the percentage of files uploaded to the server
     */
    public SSH(ExternalServer server, JProgressBar bar) {
        this.monitor = new ProgressMonitor();
        this.server = server;
        this.monitor = new ProgressMonitor(bar);
    }

    /**
     * Constructor with external server data
     * 
     * @param server external server
     */
    public SSH(ExternalServer server) {
        this.monitor = new ProgressMonitor();
        this.server = server;
    }

    /**
     * Sets the <code>JProgressBar</code> to add the ProgressMonitor object tracking the file
     * upload progress to
     * 
     * @param bar <code>JProgressBar</code> tracking file upload
     */
    public void setProgressBar(JProgressBar bar) {
        this.monitor = new ProgressMonitor(bar);
    }

    /**
     * Sets external server to which to connect to and execute remote commands
     * 
     * @param server external server to which connection will be established
     */
    public void setServer(ExternalServer server) {
        this.server = server;
    }

    /**
     * Connects to the external server by opening an ssh session
     * 
     * @return boolean indicating if the connection to the external server has been successful
     */
    public boolean connectServer() {
        try {
            session = jsch.getSession(this.server.getUser(), this.server.getHost());
            session.setPassword(this.server.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(); // Opens a channel with no timeout
            this.channel = session.openChannel("shell");
            this.channel.setInputStream(System.in);
            this.channel.setOutputStream(System.out); // print the output of the connection to the terminal
            this.channel.connect(3 * 10000); //need to confirm this number is correct
            this.connection = true;
        } catch (JSchException ex) {
            JOptionPane.showMessageDialog(null, "Connection to server has not been established!",
                    "Connection error!", JOptionPane.ERROR_MESSAGE);
            System.out.println(ex);
            this.connection = false;
        }
        return this.connection;
    }

    /**
     * Gets the sftp channel.
     *
     * @return <code>ChannelSftp</code> object
     */
    public ChannelSftp getChannel() {
        return this.sftpChannel;
    }

    /**
     * Gets the boolean indicating if the server is connected via SSH or not.
     *
     * @return boolean describing the connection (true for successfull connection)
     */
    public Boolean getConnection() {
        return this.connection;
    }

    /**
     * Sets username of this external server
     *
     * @param user username of this external server
     */
    public void setUser(String user) {
        this.server.setUser(user);
    }

    /**
     * Sets password of this external server
     * 
     * @param password password of this external server
     */
    public void setPass(String password) {
        this.server.setPassword(password);
    }

    /**
     * Sets host of the external server
     * 
     * @param host host of this ExternalServer
     */
    public void setHost(String host) {
        this.server.setHost(host);
    }

    /**
     * Disconnects server ssh and sftp channels
     * 
     * @return true if the disconnection has been successful, false if not
     */
    public Boolean disconnectServer() {
        if (!this.execChannel.isClosed() && !this.execChannel.isConnected()) {
            this.execChannel.disconnect();
        }
        if (!this.sftpChannel.isClosed() && !this.sftpChannel.isConnected()) {
            this.sftpChannel.exit();
            this.sftpChannel.disconnect();
        }
        if (!this.channel.isClosed() && !this.channel.isConnected()) {
            this.channel.disconnect();
        }
        if (!this.session.isConnected()) {
            this.session.disconnect();
        }
        return true;
    }

    /**
     * Executes a given command on the external server
     * 
     * @param cmd command to execute
     * @return external server’s console output as an ArrayList
     */
    public ArrayList executeCmd(String cmd) {
        String line;
        ArrayList lines = new ArrayList();
        try {
            connectServer();
            this.execChannel = (ChannelExec) session.openChannel("exec");
            this.execChannel.setPty(true);
            this.execChannel.setCommand(cmd);
            this.execChannel.connect();
            in = execChannel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(" ");
                System.out.println(line);
                lines.add(line);
            }

            this.execChannel.disconnect();
            return lines;
        } catch (JSchException | IOException ex) {
            System.out.println(ex.getCause());
            return lines;
        }
    }

    /**
     *
     * Creates a new directory on the external server.
     * This is used when creating the directories associated with a 
     * new alignment job within MapOptics
     * 
     * @param dir name of the directory to create
     * @return true if successful and false if not
     */
    public Boolean mkDir(String dir) {
        try {
            connectServer();
            this.sftpChannel = (ChannelSftp) this.session.openChannel("sftp");
            this.sftpChannel.connect();
            this.sftpChannel.mkdir(this.server.getWorkingDir() + dir);
            return true;
        } catch (JSchException | SftpException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(null, "Could not create folder " + dir + ", or folder already exists.",
                    "Creation failed!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Runs a container using the Docker image marieschmit/mapoptics_docker_server:ubuntuv16. 
     * The name of the container is the name of the job. The job folder is a volume
     * mounted in the container.
     *
     * @param jobname name of the job
     */
    public void runContainer(String jobname) {
        String dir = this.server.getWorkingDir();
        // Run new container for the job
        String cmd = "cd " + dir
                + " && docker run -it -d --name mapopticsDock_" + jobname
                + " -v ~/" + dir + jobname + ":/mapoptics/jobs/" + jobname + " marieschmit/mapoptics_docker_server:ubuntu16v6";
        System.out.println(cmd);
        executeCmd(cmd);
    }

    /**
     * Uploads a file to the server and indicates its upload progress
     * on a progression bar (which can take the values 0 or 100)
     * 
     * @param file file to upload to ExternalServer
     * @param dir directory on the external servre to which the file will be uploaded
     * @param bar JProgressBar to monitor the file transfer progress
     * @return true if successful and false if not
     */
    public Boolean uploadFile(String file, String dir, JProgressBar bar) {
        this.monitor.setBar(bar); // Set progress bar

        try {
            connectServer();

            this.sftpChannel = (ChannelSftp) this.session.openChannel("sftp");
            this.sftpChannel.connect();

            if (this.connection == true) {

                try {
                    System.out.println("File to upload " + this.server.getWorkingDir() + dir);
                    this.sftpChannel.put(file, this.server.getWorkingDir() + dir, this.monitor);
                    this.sftpChannel.exit();
                    return true;
                } catch (SftpException ex) {
                    System.out.println(ex.getCause());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "File transfert failed",
                            "Transfert failed!", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Connection to server has not been established!",
                        "Connection error!", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (JSchException ex) {
            return false;
        }
    }

    /**
     * Queries log file to get the progresses of a job launched in the external server.
     * The log.txt file is generated while a job is running, it indicates which steps were run
     * (for instance "Complete" or "Aligning data"_
     * 
     * @param job job from which log report is extracted
     * @return contents of the log file, with each line as an element in an ArrayList
     */
    public ArrayList queryLogFile(Job job) {
        connectServer();
        String jobname = job.getName();
        String directory = job.getServer().getWorkingDir();
        String cmd = "cd " + directory + jobname + "; cat log.txt";
//        also uses the queryCmd method
        ArrayList result = executeCmd(cmd);
        return result;
    }

    /**
     * Runs a job, which includes reference and query fasta files digestion with restriction enzyme,
     * producing cmap outputs. Then, alignment of both cmap outputs in an xmap file with either FaNDOM or RefALigner (two aligners).
     * Then, identification of SVs in the xmap output with one of the aligners. 
     * A docker container is started with the Docker image previously ran.
     * The script "run_job.sh" is executed inside this container.
     * 
     * @param job job containing the data necessary to the execution of the process
     */
    public void runJob(Job job) {
        connectServer();
        String jobname = job.getName();
        String ref = job.getRef();
        String qry = job.getQry();
        String dir = job.getServer().getWorkingDir();
        String enz = job.getEnz().getSite();
        String align = job.getPipeline();
        
        String cmd = "cd " + dir
                //+ " ; docker run -it -d --name mapopticsDock_" + jobname
                //+ " -v ~/" + dir + jobname + ":/mapoptics/jobs/" + jobname + " marieschmit/mapoptics_docker_server:ubuntu16 "
                + "; docker start " + jobname
                + "; docker exec -it mapopticsDock_" + jobname + " sh -c \"cd /mapoptics/jobs && "
                + "./run_job.sh -j " + jobname + " -r " + ref + " -q " + qry + " -e " + enz + " -a " + align
                + " > " + jobname + "/output.log 2>&1 \"";

        System.out.println(cmd + " Command send to server.");
        executeCmd(cmd);
    }

    /**
     * Calculates restriction enzymes density scores.
     * Starts the Docker container already ran for the job. Inside this container,
     * execute the script "calc_best_enz.sh", which digest either the reference or query file
     * with all the existing restriction enzyme and calculate their resulting densities,
     * stored in an output file.
     * 
     * @param job job containing data necessary to run the process
     * @param query boolean, indicates if the calculation must be ran on query (true)
     * or on reference reference file (false) of the given job
     */
    public void runCalcBestEnz(Job job, boolean query) {
        connectServer();
        String jobname = job.getName();
        String ref = job.getRef();
        String qry = job.getQry();
        String dir = job.getServer().getWorkingDir();
        String align = job.getPipeline();
        String cmd = new String();
        
        // Run the script either on query or reference file
        if (query) {
            cmd = "cd " + dir
                    //+ " ; docker run -it -d --name mapopticsDock_" + jobname
                    //+ " -v ~/" + dir + jobname + ":/mapoptics/jobs/" + jobname + " marieschmit/mapoptics_docker_server:ubuntu16 "
                    + " docker start " + jobname
                    + "; docker exec -it mapopticsDock_" + jobname + " sh -c \"cd /mapoptics/jobs "
                    + "; ./calc_best_enz.sh " + jobname + "/Files/Query/" + qry
                    + "\"";
        } else {
            cmd = "cd " + dir
                    //+ " ; docker run -it -d --name mapopticsDock_" + jobname
                    //+ " -v ~/" + dir + jobname + ":/mapoptics/jobs/" + jobname + " marieschmit/mapoptics_docker_server:ubuntu16 "
                    + "&& docker start " + jobname
                    + "; docker exec -it mapopticsDock_" + jobname + " sh -c \"cd /mapoptics/jobs "
                    + "; ./calc_best_enz.sh " + jobname + "/Files/Reference/" + ref
                    + "\"";
        }
        System.out.println(cmd);
        executeCmd(cmd);
    }

    /**
     * Downloads job results and display them on <code>CompGenView</code> panel.
     * Results folder is downloaded in a local "download" folder. It contains the produced cmap, xmap and
     * karyotype file, as well as the output of SV detection. Before downloading the results,
     * the state of the job (saved in log.txt file in the external server) is verify: it must be complete.
     * 
     * @param job job for which results are downloaded and displayed
     * @throws SftpException
     */
    public void downloadJobResults(Job job) throws SftpException {
        String jobName = job.getName();

        try {
            this.sftpChannel = (ChannelSftp) session.openChannel("sftp");
            this.sftpChannel.connect();

            Vector<ChannelSftp.LsEntry> entries = this.sftpChannel.ls(job.getServer().getWorkingDir() + jobName + "/Files/Results/");

            new File("download" + File.separator + jobName).mkdir();
//            download all files (except the ., .. and folders) from the jobs's Results folder

            for (ChannelSftp.LsEntry en : entries) {
                if (en.getFilename().equals(".") || en.getFilename().equals("..") || en.getAttrs().isDir()) {
                    continue;
                }
                this.sftpChannel.get(job.getServer().getWorkingDir() + jobName + "/Files/Results/" + en.getFilename(), "download" + File.separator + jobName + File.separator + en.getFilename());
            }
        } catch (JSchException ex) {
            Logger.getLogger(SSH.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Download results of best enzyme calculation and visualise them using COmpGenView,
     * on a dedicated panel.
     * 
     * @param job job data on which the density scores are calculated
     */
    public void downloadEnzResults(Job job) throws SftpException {
        String jobName = job.getName();
        try {
            this.sftpChannel = (ChannelSftp) session.openChannel("sftp");
            this.sftpChannel.connect();

            Vector<ChannelSftp.LsEntry> entries = this.sftpChannel.ls(job.getServer().getWorkingDir() + jobName + "/Files/Results/");

            new File("download" + File.separator + jobName).mkdir();
//            download the results from the run_calc_best_enz.sh script
            for (ChannelSftp.LsEntry en : entries) {
                if (en.getFilename().equals(".") || en.getFilename().equals("..") || en.getAttrs().isDir()) {
                    continue;
                }
                if (en.getFilename().equals("compare_enzymes.txt")) {
                    this.sftpChannel.get(job.getServer().getWorkingDir() + jobName + "/Files/Results/" + en.getFilename(), "download" + File.separator + jobName + File.separator + en.getFilename());
                }
            }
        } catch (JSchException ex) {
            Logger.getLogger(SSH.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Indicates if the channel is still connected
     *
     * @return true if it is connected, false if not
     */
    public boolean isChannelConnected() {
        return this.channel.isConnected();
    }

    /**
     * Indicates if the sftp chanel is still connected
     *
     * @return true if it is connected, false if not
     */
    public boolean isSftpChannelConnected() {
        return this.sftpChannel.isConnected();
    }
}
