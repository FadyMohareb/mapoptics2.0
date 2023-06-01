package ComparativeGenomics.FileHandling;

import ComparativeGenomics.FileHandling.DataHandling.Gene;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private ArrayList<Gene> genes = new ArrayList();

    public Annot(String filepath) {
        this.filepath = filepath;
        readAnnotation();

    }

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
            // Read the whole file and save lines corresponding to chromosomes as Gene objects
            while (sc.hasNextLine()) {
                String row = sc.nextLine();
                String chrPattern = "#";
                Pattern c = Pattern.compile(chrPattern);
                Matcher checkHeader = c.matcher(row);
                if (checkHeader.find() == true) {
                    // Check the header to have the format of the file ()gff or gtf)
                    String gffPattern = "gff";
                    Pattern f = Pattern.compile(gffPattern);
                    Matcher checkGff = f.matcher(row);
                    String gtfPattern = "gtf";
                    Pattern t = Pattern.compile(gtfPattern);
                    Matcher checkGtf = t.matcher(row);
                    if (checkGff.find() == true) {
                        gff3 = true;
                    }
                    if (checkGtf.find() == true) {
                        gtf = true;
                    }
                } else { // Not a header
                    //if ((gtf=true) || (gff3=true)){
                    if (gtf || gff3) {
                        String[] rowData = row.split("\t");
                        // First element of a gff line is the chromosome / scaffold id
                        String chr = rowData[0];
                        Matcher checkChr = Pattern.compile("chr").matcher(chr);
                        // The row corresponds to a chromosome
                        if (checkChr.find() == true) {
                            String type = rowData[2]; // Type of feature (term / accession from SPFA sequence ontology)
                            if ("gene".equals(type.replace(" ", ""))) {
                                Matcher checkM = Pattern.compile("M").matcher(chr);
                                if (checkM.find() == false) {
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

                    //if (gff3 && gtf == false){
                    // The file is neither a gff3 nor a gtf
                    if (!gff3 && !gtf) {
                        JOptionPane.showMessageDialog(null, "Error loading annotation file. Invalid file type!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {

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
        }

//            Save the identified gene objects to arraylist to be accessed via chr name in the hashmap.
        for (Gene g : genes) {
            String c = g.getChr();

            if (chrAnnotations.containsKey(c)) {
                chrAnnotations.get(c).add(g);

            } else {
                ArrayList<Gene> newChrArray = new ArrayList();
                newChrArray.add(g);
                chrAnnotations.put(c, newChrArray);
            }
        }
    }

    /**
     * Validate the file is in the correct format
     *
     * @param filepath the filepath of the file to be validated
     * @return true if file is valid and false if not
     */
    private boolean validateFile(String filepath) {
        return true;
    }

    /**
     *
     * @param filepath the filepath of the annotation file to be read
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
        readAnnotation();
    }

    /**
     *
     * @param chr the chromosome name of the annotations to be accessed
     * @return Access all of the Gene objects associated with that chromosome
     * from the chrAnnotations HashMap
     */
    public ArrayList<Gene> getFeatureByChr(String chr) {
        return chrAnnotations.get(chr);
    }

    /**
     *
     * @param chr the chromosome name to count the number of annotations of
     * @return the Integer value of the number of annotations
     */
    public Integer getNumFeaturesByChr(String chr) {
        return chrAnnotations.get(chr).size();
    }

    /**
     *
     * @return all genes from the file regardless of the chromosome
     */
    public ArrayList<Gene> getGenes() {
        return this.genes;
    }
}
