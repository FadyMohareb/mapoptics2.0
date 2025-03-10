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
 * Stores the gene entries from a GTF or GFFF file via the <code>Annot</code>
 * object. Handles querying the ClinVar and dbVar databases. The Entrez E-Utils
 * API is sued to query the database using the external <code>Gson</code> and
 * <code>Json</code> packages.
 *
 * Information on the E-Utils API can be found here:
 * https://www.ncbi.nlm.nih.gov/books/NBK25500
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

    /**
     * Constructor
     */
    public Gene() {

    }

    /**
     * Constructor with chromosome, type, source, start and end, information
     *
     * @param chr chromosome name
     * @param type type
     * @param source source
     * @param start start position
     * @param end end position
     * @param info supplementary information
     */
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
     * Splits the information from the last column of the annotation file. Saves
     * the gene name
     */
    private void splitInfo() {
        String[] infoSplit = this.info.split(";");
        int size = infoSplit.length;
        for (int i = 0; i < size; i++) {
            // Consider each information from the last column
            String str = infoSplit[i];
            Matcher checkChr = Pattern.compile("gene_name|gene_id|ID").matcher(str);
            if (checkChr.find()) {
                // gff information are separated by "="
                if (Pattern.compile("=").matcher(str).find()) {
                    String[] geneNameArr = str.split("=");
                    // gff files are separated by "=" but gtf by "   "
                    this.name = geneNameArr[geneNameArr.length - 1];
                } else if (Pattern.compile("\t").matcher(str).find()) {
                    // gtf information are separated by "  "
                    String[] geneNameArr = str.split("\t");
                    // gff files are separated by "=" but gtf by "   "
                    this.name = geneNameArr[geneNameArr.length - 1];
                }
            }
        }
    }

    /**
     * Sets selection to indicate if this gene was selected or nots
     * @param bool selection value (true or false)
     */
    public void setSelected(boolean bool) {
        this.selected = bool;
    }

    /**
     * Gets the name of the chromosome corresponding to this gene
     * @return chromosome name
     */
    public String getChr() {
        return this.chr;
    }

    /**
     * Gets the name of this gene
     * @return gene name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the start position of this gene
     * @return start
     */
    public Double getStart() {
        return this.start;
    }

    /**
     * Gets the end position of this gene
     * @return end
     */
    public Double getEnd() {
        return this.end;
    }

    /**
     * Gets supplementary information on this gene
     * @return information
     */
    public String getInfo() {
        return this.info;
    }

    /**
     * Gets this gene type
     * @return gene type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Gets this gene source
     * @return source
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Gets this gene selection information
     * @return boolean, is selected (true or false)
     */
    public boolean getSelected() {
        return this.selected;
    }

    /**
     * Queries either clinvar or dbVar database using the Entrez E-utils API. First the 
     * <code>esearch</code> command is used to find entry IDs associated with this gene in the 
     * chosen database. Then using the response from this request, a new query 
     * using the <code>esummary</code> command is used to access any associated structural 
     * variant IDs and clinical significance. if the server response with error code 414 
     * the entry IDs are split into chunks of 10 .
     * 
     * @param database database to query, either clinVar or DBvar
     * @param organism organism
     * @return database results
     */
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

    /**
     * Sends request to an API using <code>HTTPURLCOnnection</code> and <code>BufferedReader</code>
     * to read the response code
     * 
     * @param query query to database
     * @return response code
     */
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

    /**
     * Parses the result of the <code>esummary</code> API request for a dbVar query using the 
     * <code>Json</code> package

     * @param res result of API
     * @return parsed result
     */
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

    /**
     * Parses the result of the <code>esummary</code> API request for a ClinVar query, 
     * using the <code>Json</code> package
     * 
     * @param res result of API
     * @return parsed result
     */
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
