package ComparativeGenomics.FileHandling;

import ComparativeGenomics.ServerHandling.Enzyme;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Read in the file generated from the calc_best_enz.sh script to determine the
 * frequency of different digestion enzymes for a given fasta file. The file has
 * been downloaded from the ExternalServer which is associated with a job chosen
 * by the user. See the calc_best_enz.sh User Guide for information on the
 * format of the file outputted by the script.
 *
 * @author franpeters
 */
public class BestEnzyme {

    String filepath;
    Enzyme bestDensity;
    ArrayList<String[]> result = new ArrayList();
    HashMap<String, String> listEnzymes = new HashMap();

    /**
     *
     * @param filepath of the file to be read
     */
    public BestEnzyme(String filepath) {
        this.filepath = filepath;
        // Populate HashMap
        listEnzymes.put("GCTCTTC", "BspQI");
        listEnzymes.put("CCTCAGC", "BbvCI");
        listEnzymes.put("ATCGAT", "bseCI");
        listEnzymes.put("CACGAG", "BssSI");
        listEnzymes.put("GCAATG", "BsrDI");
        listEnzymes.put("CCTCAGC", "BbvCI");
        listEnzymes.put("CTTAAG", "DLE1");
        listEnzymes.put("GAATGC", "BsmI");
        listEnzymes.put("ATCGAT", "bseCI");
        
        readFile();
        calculateBest();
    }

    /**
     * read the best enzyme file using Scanner to parse each line, saving each
     * enzyme as the key in the result HashMap and the density as the value
     */
    private void readFile() {
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(this.filepath);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String row = sc.nextLine();
                String[] rowData = row.split(":");
                // Density value
                rowData[1] = rowData[1].replaceAll(" ", "");
                String[] rowEnzyme = {"", rowData[0], rowData[1]};
                //Add enzyme names
                if (listEnzymes.get(rowData[0]) != null){
                    rowEnzyme[0] = listEnzymes.get(rowData[0]);
                }
                result.add(rowEnzyme);
            }
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                try {
                    throw sc.ioException();
                } catch (IOException ex) {
                    Logger.getLogger(Cmap.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (FileNotFoundException ex) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(Cmap.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (sc != null) {
                sc.close();
            }
        }
    }
    
    /**
    * Calculate the best enzyme which corresponds to the higher density
    */
    private void calculateBest(){
        double max = 0;
        String bestName = "";
        String bestSite = "";
        // Get the maximal density enzyme among all calculated ones
        for (String[] enz : result){
            if (max < Double.parseDouble(enz[2])){
                bestName = enz[0];
                bestSite = enz[1];
                max = Double.parseDouble(enz[2]);
            }
        }
        bestDensity = new Enzyme(bestName, bestSite);
    }

    /**
     *
     * @return result HashMap
     */
    public ArrayList<String[]> getResult() {
        return this.result;
    }
    
    /**
     * @return Enzyme with highest density
     */
    public Enzyme getBestEnzyme(){
        return this.bestDensity;
    }
}
