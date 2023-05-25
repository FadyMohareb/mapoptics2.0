package ComparativeGenomics.FileHandling;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Read in the file generated from the calc_best_enz.sh script 
 * to determine the frequency of different digestion enzymes 
 * for a given fasta file. The file has been downloaded from 
 * the ExternalServer which is associated with a job chosen by the user. 
 * See the calc_best_enz.sh User Guide for information on the format 
 * of the file outputted by the script.
 * @author franpeters
 */
public class BestEnzyme {
    String filepath;
    HashMap<String,Double> result = new HashMap();
    
    /**
     * 
     * @param filepath of the file to be read
     */
    public BestEnzyme(String filepath){
        this.filepath=filepath;
        readFile();
    }
    /**
     * read the best enzyme file using Scanner to parse each line, 
     * saving each enzyme as the key in the result HashMap and 
     * the density as the value
     */
    private void readFile(){
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(this.filepath);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String row = sc.nextLine();
                String[] rowData = row.split(":"); 
                result.put(rowData[0], Double.valueOf(rowData[1].replaceAll(" ", "")));
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
            }   finally {
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
     * 
     * @return result HashMap
     */
    public HashMap<String,Double> getResult(){
        return this.result;
    }
}    

