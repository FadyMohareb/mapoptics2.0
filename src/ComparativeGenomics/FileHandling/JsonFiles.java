/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComparativeGenomics.FileHandling;

import ComparativeGenomics.ServerHandling.Enzyme;
import ComparativeGenomics.ServerHandling.ExternalServer;
import ComparativeGenomics.ServerHandling.Job;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import startScreen.runMapOptics;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author marie Manage creation and retrieval of information from server Json
 * files. Encryption or decryption of the Json, getting or saving jobs or
 * servers in those files.
 */
public class JsonFiles {

    private String jobsPath;
    private String serverPath;
    private String authenticatePath;
    private List<Job> jobsRunning = new ArrayList();
    private List<ExternalServer> servers = new ArrayList();
    //private byte[] publicKey = "nVqQMdZYHsiBwKxm".getBytes();
    private String strKey = new String();
    private String currentUser = new String();

    public JsonFiles() {
    }

    public void accessInitialisation(String user, String password) {
        this.strKey = password;
        this.currentUser = user;

        // Get current job json path
        Path path = Paths.get("");
        String pathDirectory = path.toAbsolutePath().toString();
        this.jobsPath = pathDirectory + "\\serverInfo\\jobs_" + user + ".json";
        // Get current server json path
        this.serverPath = pathDirectory + "\\serverInfo\\servers_" + user + ".json";
        this.authenticatePath = pathDirectory + "\\serverInfo\\" + user + ".txt";

        try {
//            Make sure the json files for the jobs and server objects to be saved between sessions still exist
            File jobsJson = new File(this.jobsPath);
            if (!jobsJson.exists()) {
                Files.createDirectories(Paths.get(pathDirectory + "\\serverInfo"));
                jobsJson.createNewFile();
                System.out.println("New file created: " + this.jobsPath);
            }
            File servJson = new File(this.serverPath);
            if (!servJson.exists()) {
                Files.createDirectories(Paths.get(pathDirectory + "\\serverInfo"));
                servJson.createNewFile();
                System.out.println("New file created: " + this.serverPath);
            }
        } catch (IOException ex) {
            Logger.getLogger(runMapOptics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Set the key that is used for the encryption / decryption of JSON file set
     * the user name to access the right file
     *
     * @param password
     */
    public void setAccess(String user, String password) {
        this.accessInitialisation(user, password);
    }

//Constructor creates and encrypt the json files
// Add saveJobJson
// saveServerJson
//serversFromJson
//JobsFromJson
    /**
     * Save jobs information from a list of Job objects to the json file
     * "jobs.json" saved in the folder "serverInfo"
     *
     * @param jobs List of Job objects
     */
    public void saveJobJson(List<Job> jobs) {
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(this.jobsPath));
            writer.beginObject();
            writer.name("data");
            writer.beginArray();
            for (Job j : jobs) {
                try {
                    ExternalServer s = j.getServer();
                    Enzyme e = j.getEnz();
                    writer.beginObject();
                    writer.name("Job name").value(j.getName());
                    writer.name("Server name").value(s.name);
                    writer.name("Server user").value(s.getUser());
                    writer.name("Server host").value(s.getHost());
                    writer.name("Server pass").value(encrypt(s.getPassword(), strKey.getBytes()));
                    writer.name("Server dir").value(s.getWorkingDir());
                    writer.name("qry").value(j.getQry());
                    writer.name("ref").value(j.getRef());
                    writer.name("Enzyme name").value(e.getName());
                    writer.name("Enzyme site").value(e.getSite());
                    writer.name("pipeline").value(j.getPipeline());
                    writer.name("Status").value(j.getStatus());
                    writer.name("Ref Organism").value(j.getRefOrg());
                    writer.name("Qry Organism").value(j.getQryOrg());
                    writer.name("Ref Annotation").value(j.getRefAnnot());
                    writer.name("Qry Annotation").value(j.getQryAnnot());
                    writer.endObject();
                } catch (InvalidKeyException e) {
                    System.out.println("Invalid key for encryption)");
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
            }
            writer.endArray();
            writer.endObject();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save jobs information from a list of Job objects to the json file
     * "jobs.json" saved in the folder "serverInfo"
     *
     * @param jobs List of Job objects
     */
    public void saveServerJson(List<ExternalServer> serversList) {
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(serverPath));
            writer.beginObject();
            writer.name("data");
            writer.beginArray();
            for (ExternalServer s : serversList) {
                String serverUser = encrypt(s.getUser(), strKey.getBytes());
                String serverHost = encrypt(s.getHost(), strKey.getBytes());
                String serverPwd = encrypt(s.getPassword(), strKey.getBytes());
                String serverDir = encrypt(s.getWorkingDir(), strKey.getBytes());

                System.out.println("Json 156 user encrypt " + serverUser);
                // Save encrypted information in JSON server file
                writer.beginObject();
                writer.name("name").value(s.name);
                // Before writing encoded message, replace padding characters = by * for better json reading pruposes
                // * are not used in base64 encoding.
                writer.name("user").value(serverUser.replace("=", "*"));
                writer.name("host").value(serverHost.replace("=", "*"));
                writer.name("password").value(serverPwd.replace("=", "*"));
                writer.name("dir").value(serverDir.replace("=", "*"));
                writer.endObject();

            }
            writer.endArray();
            writer.endObject();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            System.out.println("Invalid key for encryption)");
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the server information saved in the file servers.json
     *
     * @param array list
     * @return List of servers from json file
     */
    private void serversFromJson(List<ExternalServer> serversList) {
        this.servers = serversList;
        try {
            // create Gson instance
            Gson gson = new Gson();
            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get(serverPath));
            // convert JSON file to map
            Map<?, ?> map = gson.fromJson(reader, Map.class
            );
//    ArrayList array = new ArrayList();
            // print map entries
            if (map == null) {
            } else {
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    String[] value;
                    value = entry.getValue().toString().split("=");
                    // Once strings are split on "=", 
                    // temporary padding characters "*" can be replaced by the normal ones "="

//        Work out how many servers are present
                    int numServers = (value.length) / 5;
                    try {
                        for (int s = 1; s <= numServers; s++) {
//            create server object
                            String servUser = decrypt(value[2 + 5 * (s - 1)].replace("*", "=").split(",")[0], strKey.getBytes());
                            String servHost = decrypt(value[3 + 5 * (s - 1)].replace("*", "=").split(",")[0], strKey.getBytes());
                            String servPassword = decrypt(value[4 + 5 * (s - 1)].replace("*", "=").split(",")[0], strKey.getBytes());
                            String servDirectory = decrypt(value[5 + 5 * (s - 1)].replace("*", "=").split(",")[0].replace("}", "").replaceAll("]", ""), strKey.getBytes());

                            ExternalServer serv = new ExternalServer(
                                    value[1 + 5 * (s - 1)].replace("*", "=").split(",")[0],
                                    servUser,
                                    servHost,
                                    servPassword,
                                    servDirectory);
//           Add server object to array
                            servers.add(serv);
                            System.out.println("JSONFiles 229 - Server added " + serv.name);
                        }
                    } catch (InvalidKeyException e) {
                        System.out.println("Invalid key for encryption)");
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    }
                }
                // close reader
                reader.close();
            }
        } catch (Exception ex) {
            ex.getCause();
        }
    }

    /**
     * @param serversList list of servers
     * @return List of servers saved in the json file
     */
    public List<ExternalServer> getServersFromJson(List<ExternalServer> serversList) {
        serversFromJson(serversList);
        return this.servers;
    }

    /**
     * @param jobsRunningList List of Job object, jobs that are running
     * @return List of servers saved in the json file
     */
    public List<Job> getJobsFromJson(List<Job> jobsRunningList) {
        jobsFromJson(jobsRunningList);
        return this.jobsRunning;
    }

    /**
     * Read the jobs from the jobs.json file
     *
     * @param jobsRunningList
     */
    private void jobsFromJson(List<Job> jobsRunningList) {
        this.jobsRunning = jobsRunningList;
        //this.jobsRunning.clear();
        try {
            // create Gson instance
            Gson gson = new Gson();
            // Get path
            Path path = Paths.get("");
            String pathDirectory = path.toAbsolutePath().toString();
            // convert JSON file to map

            try (
                    // create a reader
                    Reader reader = Files.newBufferedReader(Paths.get(pathDirectory + "\\serverInfo\\jobs.json"))) {
                // convert JSON file to map
                Map<?, ?> map = gson.fromJson(reader, Map.class
                );
                // print map entries
                if (map == null) {
                } else {
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        //System.out.println(entry.getValue().toString());
                        String[] value = entry.getValue().toString().split("=");
                        //        Work out how many jobs are present
                        int numJobs = (value.length - 1) / 16;

                        for (int j = 1; j <= numJobs; j++) {
                            //            messy but it works
                            //            create enyzme object

                            // Create enzyme, get site and name from json file
                            Enzyme enz = new Enzyme(value[9 + 16 * (j - 1)].split(",")[0],
                                    value[10 + 16 * (j - 1)].split(",")[0]);

                            //            create server object
                            // Populate with name, user, host, pass, dir
                            try {
                                ExternalServer serv = new ExternalServer(
                                        value[2 + 16 * (j - 1)].split(",")[0],
                                        value[3 + 16 * (j - 1)].split(",")[0],
                                        value[4 + 16 * (j - 1)].split(",")[0],
                                        decrypt(value[5 + 16 * (j - 1)].replace("*", "=").split(",")[0], strKey.getBytes()),
                                        value[6 + 16 * (j - 1)].split(",")[0]);
                                //            create job object
                                // Populate with name, query and reference fasta, status, ref and qry organism
                                // Ref and query annotations
                                Job job = new Job(serv,
                                        value[1 + 16 * (j - 1)].split(",")[0],
                                        value[8 + 16 * (j - 1)].split(",")[0],
                                        value[7 + 16 * (j - 1)].split(",")[0],
                                        enz,
                                        value[11 + 16 * (j - 1)].split(",")[0],
                                        value[12 + 16 * (j - 1)].split(",")[0],
                                        value[14 + 16 * (j - 1)].split(",")[0],
                                        value[13 + 16 * (j - 1)].split(",")[0],
                                        value[15 + 16 * (j - 1)].split(",")[0],
                                        value[16 + 16 * (j - 1)].split(",")[0].replace("}", "").replaceAll("]", ""));
                                //           Add job object to array
                                jobsRunning.add(job);
                            } catch (InvalidKeyException e) {
                                System.out.println("Invalid key for encryption)");
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                // close reader
            }
            //jobTableAdd(this.jobsRunning);
        } catch (JsonIOException | JsonSyntaxException | IOException ex) {
            ex.getCause();
        }
    }

    /**
     * Encrypt a message.
     *
     * @param message
     * @param keyBytes
     * @return encryptedMessage
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private String encrypt(String message, byte[] publicKey)
            throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException,
            BadPaddingException, IllegalBlockSizeException {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKey secretKey = new SecretKeySpec(publicKey, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedMessage = cipher.doFinal(message.getBytes());
            System.out.println("JSON line 4400 - byte msg encryption " + new String(encryptedMessage));
            return Base64.getEncoder().encodeToString(encryptedMessage);
        } catch (Exception IllegalArgumentException) {
            return null;
        }
    }

    /**
     * Decrypt a crypted message.
     *
     * @param cryptedMessage
     * @param publicKey
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAligorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private String decrypt(String cryptedMessage, byte[] publicKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKey secretKey = new SecretKeySpec(publicKey, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // Decode crypted message from base 64 to normal string
            byte[] message = Base64.getDecoder().decode(cryptedMessage);
            System.out.println("JSON line 405 - byte msg " + new String(message) + " end decrypt " + new String(cipher.doFinal(message)));
            // Decypher message and convert it to string
            return new String(cipher.doFinal(message));
        } catch (Exception IllegalArgumentException) {
            return null;
        }
    }
}
