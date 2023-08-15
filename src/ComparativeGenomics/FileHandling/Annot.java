package ComparativeGenomics.FileHandling;

import ComparativeGenomics.FileHandling.DataHandling.Gene;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author franpeters Class verify file format, parse and save the contents of a
 * GFF3 (General Feature File) or GTF (General Transfer Format) file. The data
 * is stored into a hashmap data structure using Gene objects.This class assumes
 * the file follows the criteria laid out here:
 * https://www.ensembl.org/info/website/upload/gff.html
 *
 */
public class Annot {

    private HashMap<String, ArrayList<Gene>> chrAnnotations = new HashMap(); //where key is chromosome name and value are all the genes associated with that chr
    private String filepath;
    private boolean gff3 = false;
    private boolean gtf = false;
    private boolean isValid = false;
    private ArrayList<Gene> genes = new ArrayList();

    /**
     * Class constructor
     * 
     * @param filepath Path of annotation file
     */
    public Annot(String filepath) {
        this.filepath = filepath;
        readAnnotation();

    }

    /**
     * Class constructor with empty annotations
     */
    public Annot() {

    }

    /**
     * Read the annotation file using the file path of the file. Scanner is used
     * to parse each line in the file and a Gene object is made for each entry
     * that matches the type ‘gene’. See 3.2.4 for more information on the Gene
     * object.
     */
    private void readAnnotation() {
//        first need to check the file version as it will change slightly what information is contained
        String[] directory = this.filepath.split("/");

        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            Integer count = 0;
            inputStream = new FileInputStream(this.filepath);
            sc = new Scanner(inputStream, "UTF-8");
            
            if(sc != null){
                this.isValid = true;
            }
            // Read the whole file and save lines corresponding to chromosomes as Gene objects
            while (sc.hasNextLine()) {
                String row = sc.nextLine();
                
                // Headers are the lines starting by "#"
                String chrPattern = "#";
                Pattern c = Pattern.compile(chrPattern);
                Matcher checkHeader = c.matcher(row);
                if (checkHeader.find()) {
                    // Check the header to have the format of the file (gff or gtf)
                    // gff matcher
                    String gffPattern = ".gff|.gff3";
                    Pattern f = Pattern.compile(gffPattern);
                    Matcher checkGff = f.matcher(row);
                    
                    // gtf matcher
                    String gtfPattern = ".gtf";
                    Pattern t = Pattern.compile(gtfPattern);
                    Matcher checkGtf = t.matcher(row);
                    if (checkGff.find()) {
                        gff3 = true;
                    }
                    if (checkGtf.find()) {
                        gtf = true;
                    }
                } else { // Not a header
                    if (gtf || gff3) {
                        String[] rowData = row.split("\t");
                        // First element of a gff line is the chromosome / scaffold id
                        String chr = rowData[0];
                        Matcher checkChr = Pattern.compile("chr|ch").matcher(chr);
                        // The row corresponds to a chromosome
                        if (checkChr.find()) {
                        //if (true){
                            String type = rowData[2]; // Type of feature (term / accession from SPFA sequence ontology)
                            // Feature is a gene
                            if ("gene".equals(type.replace(" ", ""))) {
                                // Check that the chr is nuclear and not mitochondrial
                                Matcher checkM = Pattern.compile("M").matcher(chr);
                                if (!checkM.find()) {
                                    // Create a new gene with source, start and end, attributes
                                    Gene newGene = new Gene(chr, type, rowData[1],
                                            Double.parseDouble(rowData[3]),
                                            Double.parseDouble(rowData[4]),
                                            rowData[8]);
                                    genes.add(newGene);
                                }
                            }
                        }
                    }
                }
            }
//                chrAnnotations.put(chr,list);

            if (sc.ioException() != null) {
                try {
                    throw sc.ioException();
                } catch (IOException ex) {

                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }

//            Save the identified gene objects to arraylist to be accessed via chr name in the hashmap.
        for (Gene g : genes) {
            // Get the chromosome of each gene
            String c = g.getChr().toLowerCase();
            // Check if hashmap of chromosomes (key = chr name, value = list of genes for the chr)
            // contains the chr
            if (chrAnnotations.containsKey(c)) {
                // Add the chromosome to the hasmap
                chrAnnotations.get(c).add(g);

            } else {
                // Create a new entry for the chromosome in the hashmap
                ArrayList<Gene> newChrArray = new ArrayList();
                newChrArray.add(g);
                chrAnnotations.put(c, newChrArray);
            }
        }
    }

    /**
     * Validate the file is in the correct format
     *
     * @author marie
     * @param filepath the filepath of the file to be validated
     * @return true if file is valid and false if not
     */
    private boolean validateFile(String filePath) {
        // Extension corresponds to gff or gtf
        if (filePath.endsWith(".gff;.gff3;.gtf")) {
                if (!Files.exists(Paths.get(filePath))) {
                    //Show error message if no file found
                    JOptionPane.showMessageDialog(null,
                            "Error loading gff or gtf file." +
                                    "\n\nFile does not exist!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                //Show error message if wrong file type
                JOptionPane.showMessageDialog(null,
                        "Error loading gtf or gff file." +
                                "\n\nInvalid file type!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        return true;
    }

    /**
     * Set the filepath of the annotation file to be read
     * 
     * @param filepath File path of the annotation file to be read
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
        readAnnotation();
    }

    /**
     * Access all of the Gene objects associated with that chromosome from the chrAnnotations HashMap.
     * <p>
     * Return a HashMap that stores the contents of the annotation file whereby the key is the
     * chromosome name and the contents an ArrayList of all the genes.
     * Please note the chromosome name on the header of the Fasta file should
     * be the same as the chromosome name format used here. For example, either chr1 or 1 or chr_1.
     * The specific format does not matter if it is consistent between the annotation and fasta
     * files for a genome. The query and reference genomes can use different formats.
     * 
     * @param chr the chromosome name of the annotations to be accessed
     * @return Access all of the Gene objects associated with that chromosome
     * from the chrAnnotations HashMap
     */
    public ArrayList<Gene> getFeatureByChr(String chr) {
        return chrAnnotations.get(chr.toLowerCase());
    }

    /**
     * Return the number of genes for each chromosome
     * 
     * @param chr the chromosome name to count the number of annotations of
     * @return the Integer value of the number of annotations
     */
    public Integer getNumFeaturesByChr(String chr) {
        return chrAnnotations.get(chr).size();
    }

    /**
     *Get genes of the annotation file
     * 
     * @return all genes from the file regardless of the chromosome
     */
    public ArrayList<Gene> getGenes() {
        return this.genes;
    }
    
    /**
     * Indicate if A valid file exists, is in proper format and is not empty
     * 
     * @return isValid Boolean indicating file validity
     */
    public boolean getValidity(){
        return this.isValid;
    }
}
