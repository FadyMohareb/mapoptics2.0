/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComparativeGenomics.FileHandling;

import ComparativeGenomics.FileHandling.DataHandling.SVFandom;
import ComparativeGenomics.FileHandling.DataHandling.SVRefAligner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author marie Read and parse SV detection outputs from FaNDOM or RefAligner.
 * The parsed file can either be .txt format, or SMAP format. The format of SMAP
 * is described here:
 * https://bionanogenomics.com/wp-content/uploads/2017/03/30041-SMAP-File-Format-Specification-Sheet.pdf
 * The expected format of txt input is described here:
 * https://github.com/jluebeck/FaNDOM/tree/master
 */
public class Smap {

    private String filepath;
    private boolean smapFormat; // Indicates if the file is a txt from FaNDOM or a SMAP from runBNG
    private boolean isValid = true; // Indicates file is valid (not empty, in proper format)
    private ArrayList<SVFandom> SVList;

    /**
     * Passes file to read and parse it if it is valid (proper format, non
     * empty)
     *
     * @param filepath Path of the file to parse
     */
    public Smap(String filepath) {
        this.filepath = filepath;
        if (this.filepath.endsWith(".txt")) {
            this.smapFormat = false;
        } else if (this.filepath.endsWith(".smap")) {
            this.smapFormat = false;
        } else {
            this.isValid = false;
            //Show error message if wrong file type
            JOptionPane.showMessageDialog(null,
                    "Error loading SV file."
                    + "\n\nInvalid file type, the type should either be txt or smap.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Read and parse files in .txt format, They are output from FaNDOM. The
     * result of this parsing is saved in the class SVFandom.
     *
     * @param filepath
     */
    private void readSVtxt(String filepath) {
        if (validateSVtxt(filepath)) {
            // Read the file
            FileInputStream inputStream = null;
            Scanner sc = null;
            try {
                inputStream = new FileInputStream(this.filepath);
                sc = new Scanner(inputStream, "UTF-8");
                while (sc.hasNextLine()) {
                    String row = sc.nextLine();

                    String chrPattern = "#";
                    Pattern c = Pattern.compile(chrPattern);
                    Matcher checkHeader = c.matcher(row);
                    if (!checkHeader.find()) {
                        // Parse data
                        String[] rowData = row.split("\t");
                        int Chrom1 = Integer.valueOf(rowData[0]);
                        int refPos1 = Integer.valueOf(rowData[1]);
                        String direction1 = rowData[2];
                        int Chrom2 = Integer.valueOf(rowData[3]);
                        int refPos2 = Integer.valueOf(rowData[4]);
                        String direction2 = rowData[5];
                        String type = rowData[6];
                        int id = Integer.valueOf(rowData[7]);
                        int numSupports = Integer.valueOf(rowData[8]);
                        boolean geneInterrupt = true ;
                        if(Integer.valueOf(rowData[9]).equals("False")){
                            geneInterrupt = false;
                        }
                        String geneFusion = rowData[10];
                        
                        // Create SVFandom instance to store resulting data
                        SVFandom svObject = new SVFandom( Chrom1, Chrom2, refPos1, refPos2, direction1, direction2, type, id, numSupports, geneInterrupt, geneFusion);
                        
                    }
                }
            } catch (FileNotFoundException ex) {
            }
        }
    }

    /**
     * Validate input file which is in text format, FaNDOM output.
     *
     * @param filepath Path to the file to validate
     * @return boolean indicating if the file is valid
     */
    private boolean validateSVtxt(String filePath) {
        if (Files.exists(Paths.get(filePath))) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(filePath));
                String line;

                // Check that a line in the file contains the standard header
                while ((line = br.readLine()) != null && line.startsWith("#")) {
                    if (line.toLowerCase().contains("Header\tChrom1")) {
                        return true;
                    }
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            isValid = false;
            //Show error message if no file found
            JOptionPane.showMessageDialog(null,
                    "Error loading SV file."
                    + "\n\nFile does not exist!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return false;
    }

}
