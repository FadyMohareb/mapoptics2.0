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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author marie Manage creation and retrieval of information from server Json
 * files. Encryption or decryption of the Json, getting or saving jobs or
 * servers in those files.
 */
public class JsonFiles {

    private String jobsPath;
    private String serverPath;
    private List<Job> jobsRunning = new ArrayList();
    private List<ExternalServer> servers = new ArrayList();

    public JsonFiles() {
        // Get current job json path
        Path path = Paths.get("");
        String pathDirectory = path.toAbsolutePath().toString();
        this.jobsPath = pathDirectory + "\\serverInfo\\jobs.json";
        // Get current server json path
        this.serverPath = pathDirectory + "\\serverInfo\\servers.json";
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
                ExternalServer s = j.getServer();
                Enzyme e = j.getEnz();
                writer.beginObject();
                writer.name("Job name").value(j.getName());
                writer.name("Server name").value(s.name);
                writer.name("Server user").value(s.getUser());
                writer.name("Server host").value(s.getHost());
                writer.name("Server pass").value(s.getPassword());
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
                writer.beginObject();
                writer.name("name").value(s.name);
                writer.name("user").value(s.getUser());
                writer.name("host").value(s.getHost());
                writer.name("password").value(s.getPassword());
                writer.name("dir").value(s.getWorkingDir());
                writer.endObject();
            }
            writer.endArray();
            writer.endObject();
            writer.close();
        } catch (IOException e) {
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
            Map<?, ?> map = gson.fromJson(reader, Map.class);
//    ArrayList array = new ArrayList();
            // print map entries
            if (map == null) {
            } else {
                for (Map.Entry<?, ?> entry : map.entrySet()) {

                    String[] value;
                    value = entry.getValue().toString().split("=");

//        Work out how many servers are present
                    int numServers = (value.length) / 5;

                    for (int s = 1; s <= numServers; s++) {

//            messy but it works
//            create server object
                        ExternalServer serv = new ExternalServer(value[1 + 5 * (s - 1)].split(",")[0],
                                value[2 + 5 * (s - 1)].split(",")[0],
                                value[3 + 5 * (s - 1)].split(",")[0],
                                value[4 + 5 * (s - 1)].split(",")[0],
                                value[5 + 5 * (s - 1)].split(",")[0].replace("}", "").replaceAll("]", ""));

//           Add job object to array
                        servers.add(serv);
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
    
    /*
    * Read the jobs from the jobs.json file
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
                Map<?, ?> map = gson.fromJson(reader, Map.class);
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
                            ExternalServer serv = new ExternalServer(value[2 + 16 * (j - 1)].split(",")[0],
                                    value[3 + 16 * (j - 1)].split(",")[0],
                                    value[4 + 16 * (j - 1)].split(",")[0],
                                    value[5 + 16 * (j - 1)].split(",")[0],
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
}
