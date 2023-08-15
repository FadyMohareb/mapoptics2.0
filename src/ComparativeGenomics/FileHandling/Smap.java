/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComparativeGenomics.FileHandling;

import ComparativeGenomics.FileHandling.DataHandling.SVFandom;
import ComparativeGenomics.FileHandling.DataHandling.SVRefAligner;
import ComparativeGenomics.StructuralVariant.Translocation;

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
    private ArrayList<SVFandom> SVList = new ArrayList<>();
    private ArrayList<SVRefAligner> smapSVList = new ArrayList<>();
    private ArrayList<SVFandom> possibleTxtTranslocations = new ArrayList<>();
    private ArrayList<SVRefAligner> possibleSmapTranslocations = new ArrayList<>();

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
            // Read and parse file
            readSVtxt(filepath);
            // Detect and sort SV
            sortTxtSV();
        } else if (this.filepath.endsWith(".smap")) {
            this.smapFormat = true;
            // Read and parse SMAP
            readSmap(filepath);
            // Detect and sort SV
            sortSmapSV();
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
                        int refPos1 = (int) Double.parseDouble(rowData[1]);
                        String direction1 = rowData[2];
                        int Chrom2 = Integer.valueOf(rowData[3]);
                        int refPos2 = (int) Double.parseDouble(rowData[4]);
                        String direction2 = rowData[5];
                        String type = rowData[6];
                        int[] id = new int[rowData[7].split(",").length];
                        for (int i = 0; i < rowData[7].split(",").length; i++){
                            id[i] = Integer.valueOf(rowData[7].split(",")[i]);
                        }
                        int numSupports = Integer.valueOf(rowData[8]);
                        boolean geneInterrupt = true;
                        if (rowData[9].equals("False")) {
                            geneInterrupt = false;
                        }
                        String geneFusion = rowData[10];

                        // Create SVFandom instance to store resulting data
                        SVFandom svObject = new SVFandom(Chrom1, Chrom2, refPos1, refPos2, direction1, direction2, type, id, numSupports, geneInterrupt, geneFusion);
                        SVList.add(svObject);
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
                    if (line.toLowerCase().contains("#header")) {
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

    /**
     * Detect SV if input file is a text file following the format of those
     * produced by FaNDOM
     */
    private void sortTxtSV() {
        for (int i = 0; i < this.SVList.size(); i++) {
            SVFandom currentSV = this.SVList.get(i);
            // Detect translocations
            if (currentSV.getType().equals("Unknown") && currentSV.getChr1() != currentSV.getChr2()) {
                possibleTxtTranslocations.add(currentSV);
            }
        }
    }

    /**
     * Read and parse SMAP input file.
     * @param filepath Path to SMAP file
     */
    private void readSmap(String filepath) {
        if (validateSmap(filepath)) {
            // Read the file
            FileInputStream inputStream = null;
            Scanner sc = null;
            try {
                inputStream = new FileInputStream(this.filepath);
                sc = new Scanner(inputStream, "UTF-8");
                while (sc.hasNextLine()) {
                    String row = sc.nextLine();

                    // Analys the lines that are not headers
                    String chrPattern = "#";
                    Pattern c = Pattern.compile(chrPattern);
                    Matcher checkHeader = c.matcher(row);
                    
                    if (!checkHeader.find()) {
                        // Parse data
                        String[] rowData = row.split("\t");
                        int smapID = Integer.valueOf(rowData[0]);
                        int qryID = Integer.valueOf(rowData[1]);
                        int refID1 = Integer.valueOf(rowData[2]);
                        int refID2 = Integer.valueOf(rowData[3]);
                        int qryStart = (int) Double.parseDouble(rowData[4]);
                        int qryEnd = (int) Double.parseDouble(rowData[5]);
                        int refStart = (int) Double.parseDouble(rowData[6]); 
                        int refEnd = (int) Double.parseDouble(rowData[7]);
                        int confidence = (int) Double.parseDouble(rowData[8]);
                        String type = rowData[9];
                        int xmapID1 = (int) Double.parseDouble(rowData[10]);
                        int xmapID2 = (int) Double.parseDouble(rowData[11]);
                        int linkID = (int) Double.parseDouble(rowData[12]);
                        int qryStartIdx = (int) Double.parseDouble(rowData[13]);
                        int qryEndIdx = (int) Double.parseDouble(rowData[14]);
                        int refStartIdx = (int) Double.parseDouble(rowData[15]);
                        int refEndIdx = (int) Double.parseDouble(rowData[16]);
                        String zygosity = rowData[17];
                        int genotype = Integer.valueOf(rowData[18]);
                        int genotypeGroup = Integer.valueOf(rowData[19]);
                        double rawConfidence = Double.parseDouble(rowData[20]);
                        double rawConfidenceLeft = Double.parseDouble(rowData[21]);
                        double rawConfidenceRight = Double.parseDouble(rowData[22]);
                        double rawConfidenceCenter = Double.parseDouble(rowData[23]);
                        double SVsize = Double.parseDouble(rowData[24]);
                        double SVfreq = Double.parseDouble(rowData[25]);
                        String orientation = rowData[26];
                        
                        System.out.println("SMAP 223 " + " " + smapID  + " " +  qryID  + " " +  refID1  + " " +  refID2 + " " + qryStart + " " + 
                                qryEnd + " " + refStart + " " + refEnd + " " + confidence + " " + type + " " + xmapID1 + " " + xmapID2 + " " + linkID + " " + 
                                qryStartIdx + " " + qryEndIdx + " " + refStartIdx + " " + refEndIdx + " " + zygosity + " " + genotype + " " +genotypeGroup + " " + 
                                rawConfidence + " " + rawConfidenceLeft + " " + rawConfidenceRight + " " + rawConfidenceCenter + " " + 
                                SVsize + " " + SVfreq + " " + orientation);

                        // Create SVRefAligner instance to store resulting data
                        SVRefAligner svOjbect = new SVRefAligner(smapID, qryID, refID1, refID2, qryStart, 
                                qryEnd, refStart, refEnd, confidence, type, xmapID1, xmapID2, linkID, 
                                qryStartIdx, qryEndIdx, refStartIdx, refEndIdx, zygosity, genotype,genotypeGroup, 
                                rawConfidence, rawConfidenceLeft, rawConfidenceRight, rawConfidenceCenter, 
                                SVsize, SVfreq, orientation);
                        smapSVList.add(svOjbect);
                    }
                }
            } catch (FileNotFoundException ex) {
                System.out.println("Smap file not found.");
            }
        }
    }

    /**
     * Validate file format (SMAP) and check that the file is not empty.
     *
     * @param filePath
     * @return Validation result (true if file is non empty SMAP)
     */
    private boolean validateSmap(String filePath) {
        if (Files.exists(Paths.get(filePath))) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(filePath));
                String line;

                // Check that a line in the file contains mandatory line
                while ((line = br.readLine()) != null && line.startsWith("#")) {
                    if (line.toLowerCase().contains("# smap file version")) {
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
                    "Error loading SMAP file."
                    + "\n\nFile does not exist!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return false;
    }
    
    /**
     * Detect SV if input file is a text file following the SMAP format
     */
    private void sortSmapSV() {
        for (int i = 0; i < this.smapSVList.size(); i++) {
            SVRefAligner currentSV = this.smapSVList.get(i);
            // Detect translocations
            if (currentSV.getType().equals("translocation_interchr")) {
                possibleSmapTranslocations.add(currentSV);
            }
        }
    }

    /**
     * @return File format: SMAP or txt
     */
    public boolean getSmapFormat() {
        return this.smapFormat;
    }

    /**
     * @return list of SVFandom objects having possible translocations
     */
    public ArrayList<SVFandom> getTxtTransloc() {
        return this.possibleTxtTranslocations;
    }
    
    /**
     * @return list of SVRefAlignerobjects having possible translocations
     */
    public ArrayList<SVRefAligner> getSmapTransloc() {
        return this.possibleSmapTranslocations;
    }
}
