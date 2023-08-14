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
 *
 * @author franpeters Class to allow SSH connection to an external server. SSH
 * allows execution of commands, running of scripts run_job.sh and
 * calc_best_enz.sh as well as file uploading and downloading using STFP. The
 * externalJAR Jsch is used to add in external server connection.
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
     *
     */
    public SSH() {
        this.monitor = new ProgressMonitor();
    }

    /**
     *
     * @param job
     */
    public SSH(Job job) {
        this.job = job;
        this.job.getServer();
        this.monitor = new ProgressMonitor();
    }

    /**
     *
     * @param server
     * @param bar
     */
    public SSH(ExternalServer server, JProgressBar bar) {
        this.monitor = new ProgressMonitor();
        this.server = server;
        this.monitor = new ProgressMonitor(bar);
    }

    /**
     *
     * @param server
     */
    public SSH(ExternalServer server) {
        this.monitor = new ProgressMonitor();
        this.server = server;
    }

    /**
     *
     * @param bar the JProgressBar to add the ProgressMonitor object tracking
     * the file upload progress to
     */
    public void setProgressBar(JProgressBar bar) {
        this.monitor = new ProgressMonitor(bar);
    }

    /**
     *
     * @param server the ExternalServer object by which to connect to and
     * execute remote commands to
     */
    public void setServer(ExternalServer server) {
        this.server = server;

    }

    /**
     *
     * @return he ExternalServer object and return a true response if this has
     * been successful
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
     * Get the sftp channel.
     *
     * @return the ChannelSftp object
     */
    public ChannelSftp getChannel() {
        return this.sftpChannel;
    }

    /**
     * Get the boolean indicating if the server is connected via SSH or not.
     *
     * @return the connection boolean
     */
    public Boolean getConnection() {
        return this.connection;
    }

    /**
     * o
     *
     * @param user username of the ExternalServer
     */
    public void setUser(String user) {
        this.server.setUser(user);
    }

    /**
     *
     * @param password password of the ExternalServer
     */
    public void setPass(String password) {
        this.server.setPassword(password);
    }

    /**
     *
     * @param host host of the ExternalServer
     */
    public void setHost(String host) {
        this.server.setHost(host);
    }

    /**
     *
     * @return disconnect from the ExternalServer session and return true if
     * this has been successful and false if not
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
     *
     * @param cmd execute a given command on the ExternalServer
     * @return the ExternalServer’s console output as an ArrayList
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
     * @param dir make a directory on the ExternalServer, This is used when
     * creating the directories associated with a new alignment job within
     * MapOptics
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
     * Run a container using the Docker image mapoptics_docker_server. The name
     * of the container is the name of the job. The job folder is a volume
     * mounted in the container.
     *
     * @param job name
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
     *
     * @param file File to upload to ExternalServer
     * @param dir Directory on the ExternalServer to upload the file to
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
     *
     * @param job Job to query the log file of
     * @return the contents of the file with each line an element in an
     * ArrayList
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
     *
     * @param job begin an alignment job on the ExternalServer using the
     * run_job.sh script
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
     *
     * @param job run the calc_best_enz.sh script on the ExternalServer with the
     * @param query (boolean) indicates if the calculation must be ran on query
     * or on reference reference file of the given job
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
     *
     * @param job download all the results of an alignment job to analyse and
     * visualise using CompGenView
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
     *
     * @param job download all the results of an alignment job to analyse and
     * visualise using CompGenStart
     * @throws SftpException
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
     * Indicates if the chanel is still connected
     *
     * @return boolean
     */
    public boolean isChannelConnected() {
        return this.channel.isConnected();
    }

    /**
     * Indicates if the sftp chanel is still connected
     *
     * @return boolean
     */
    public boolean isSftpChannelConnected() {
        return this.sftpChannel.isConnected();
    }
}
