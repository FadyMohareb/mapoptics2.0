package ComparativeGenomics.FileHandling.DataHandling;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author franpeters
 */
public class Gene {

    private String chr;
    private String name;
    private String source;
    private String type;
    private Double start;
    private Double end;
    private String info;

    private String clinVarResult;
    private String dbVarResult;
    private boolean selected = false;

    public Gene() {

    }

    public Gene(String chr, String type, String source, Double start, Double end, String info) {
        this.chr = chr;
        this.type = type;
        this.source = source;
        this.start = start;
        this.end = end;
        this.info = info;
        splitInfo();
    }

    /**
     * Split the information from the last column of the gff Save the gene name
     */
    private void splitInfo() {
        String[] infoSplit = this.info.split(";");
        int size = infoSplit.length;
        for (int i = 0; i < size; i++) {
            // Consider each information from the last column
            String str = infoSplit[i];
            Matcher checkChr = Pattern.compile("gene_name|gene_id|ID").matcher(str);
            if (checkChr.find()) {
                //System.out.println("Gene line 63: " + str);
                // gff information are separated by "="
                if (Pattern.compile("=").matcher(str).find()){
                    String[] geneNameArr = str.split("=");
                    // gff files are separated by "=" but gtf by "   "
                    this.name = geneNameArr[geneNameArr.length - 1];
                }
                else if (Pattern.compile("\t").matcher(str).find()){
                    // gtf information are separated by "  "
                    String[] geneNameArr = str.split("\t");
                    // gff files are separated by "=" but gtf by "   "
                    this.name = geneNameArr[geneNameArr.length - 1];
                }
                else{
                    System.out.println("Chromosome ID is not found.");
                }
            } else {
                /*
                Matcher checkID = Pattern.compile("gene_id").matcher(str);
                if (checkID.find() == true) {
                    String[] geneNameArr = str.split("=");
                    this.name = geneNameArr[geneNameArr.length - 1];
                 */
                System.out.println("No gene ID found. "
                        + "\n Name is neither indicated by gene_name, gene_id nor ID in the input annotation file.");
            }

        }
    }

    public void setSelected(boolean bool) {
        this.selected = bool;
    }

    public String getChr() {
        return this.chr;
    }

    public String getName() {
        return this.name;
    }

    public Double getStart() {
        return this.start;
    }

    public Double getEnd() {
        return this.end;
    }

    public String getInfo() {
        return this.info;
    }

    public String getType() {
        return this.type;
    }

    public String getSource() {
        return this.source;
    }

    public boolean getSelected() {
        return this.selected;
    }
//    need to make a method to split the info column into useable data.

    public ArrayList<ArrayList<String>> queryDB(String database, String organism) {
        ArrayList<ArrayList<String>> results = new ArrayList();
        ArrayList<String> uids = new ArrayList();

        organism = organism.replaceAll(" ", "%20"); //remember strings are immutable!
//        first query the chosen database to find all entries concerning this gene
        String baseURL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=";
        String jsonQuery = baseURL + database + "&term=" + this.name + "[gene]&"
                + organism + "[organism]&retmax=500&retmode=json";
        String response = sendRequest(jsonQuery);
        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
        JsonArray idListJson = jsonObject.get("esearchresult")
                .getAsJsonObject()
                .get("idlist")
                .getAsJsonArray();

        for (int i = 0; i < idListJson.size(); i++) {
            String uid = idListJson.get(i).getAsString();
            uids.add("%20" + uid);

        }
//        Then using the uids from the previous requests' response, access the esummary data
        String[] array = uids.toArray(String[]::new);
        String queryURL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db="
                + database + "&id="
                + Arrays.toString(array) + "&retmax=500&retmode=json";
        queryURL = queryURL.replace(" ", "");
        String response2 = sendRequest(queryURL);
//        if the request is too long divide the uids into smaller chunks so not to cause error when requesting from server
        if ("414".equals(response2)) {
            int chunk = 10;
            for (int i = 0; i < array.length; i += chunk) {
                String queryURL2 = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db="
                        + database + "&id="
                        + Arrays.toString(Arrays.copyOfRange(array, i, Math.min(array.length, i + chunk)))
                        + "&retmax=500&retmode=json";
                queryURL2 = queryURL2.replace(" ", "");
                response2 = sendRequest(queryURL2);
                if (!response2.isEmpty()) {
//                    System.out.println(jsonObject2.get("result"));
                    if ("dbvar".equals(database)) {
                        results.add(parseDBVarResult(response2));
                    }
                    if ("clinvar".equals(database)) {
                        results.add(parseClinVarResult(response2));
                    }
                }
//               
            }
        } else {
            if (!response2.isEmpty()) {
                if ("dbvar".equals(database)) {
                    results.add(parseDBVarResult(response2));
                }
                if ("clinvar".equals(database)) {
                    results.add(parseClinVarResult(response2));
                }

            }

        }
        return results;
    }

    private String sendRequest(String query) {
        String json = "";
        try {

            URL url = new URL(query);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("Accept", "application/json");
            if (http.getResponseCode() == 200) {
                String line;
                StringBuilder responseContent = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
                json = responseContent.toString();

            }
            if (http.getResponseCode() == 414) {
                return "414";
            }
        } catch (MalformedURLException ex) {

        } catch (IOException ex) {

        }
        return json;

    }

    private ArrayList<String> parseDBVarResult(String res) {
        ArrayList<String> r = new ArrayList();
        JSONObject jsonObject2 = new JSONObject(res);
        JSONObject result = jsonObject2.getJSONObject("result");
        JSONArray uidsObj = result.getJSONArray("uids");
        List<Object> uidsList = uidsObj.toList();
        for (Object o : uidsList) {

            JSONObject uidsRes = result.getJSONObject(o.toString());
            r.add(o.toString());
            r.add(uidsRes.get("sv").toString());
            r.add(uidsRes.get("dbvarclinicalsignificancelist").toString());

        }
        return r;
    }

    private ArrayList<String> parseClinVarResult(String res) {
        ArrayList<String> r = new ArrayList();
        JSONObject jsonObject2 = new JSONObject(res);
        JSONObject result = jsonObject2.getJSONObject("result");
        JSONArray uidsObj = result.getJSONArray("uids");
        List<Object> uidsList = uidsObj.toList();
        for (Object o : uidsList) {
            JSONObject uidsRes = result.getJSONObject(o.toString());
            JSONObject clinSig = uidsRes.getJSONObject("clinical_significance");
            r.add(o.toString());
            r.add(uidsRes.get("accession").toString());
            r.add(clinSig.get("description").toString());

        }
        return r;
    }
}
