package ComparativeGenomics.FileHandling;

import ComparativeGenomics.FileHandling.DataHandling.Sequence;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author franpeters
 * Class to verify file format, parse and save the contents of 
 * a multi-sequence fasta file. This file provides information 
 * about the sequence of each chromosome within a genome, 
 * with the chromosome name in the header. BufferedReader is used to read 
 * through the contents of the file in a memory efficient manner. 
 * The data is stored into a HashMap data structure using Sequence  
 * (see 3.2.8 for more information on this class) objects. 
 * Sequence stores information regarding each chromsomes’ nucleotide sequence. 
 * This class assumes the file follows the criteria laid out here: 
 * https://www.ncbi.nlm.nih.gov/blast/fasta.shtml with one difference, 
 * the header must contain no spaces and information about the chromosome name 
 * in a format such as chr1, 1 or chr_1. This format must be consistent 
 * with the format used in the annotation GFF or GTF file (see 3.1.1) to ensure 
 * creation of the Karyotype file is correct.
 * Currently not in use but available for future development.

 */
public class Fasta {

    private String filepath;
    HashMap<Double,Sequence> sequences = new HashMap();
    
    public Fasta(){}
    public Fasta(String filepath){
        this.filepath = filepath;
        readFasta();
    }
    
    /**
     * 	Use BufferedReader to parse the fasta file line by line splitting 
     * each sequence into separate Sequence objects by checking for the presence 
     * of the ‘>’, indicating a new header. The data is stored in the sequences HashMap 
     * with the sequence length as the key and the Sequence object as the value.
     */
    private void readFasta(){
//        Reads a multifasta file into Sequence objects, with each header having its own Sequence object
        String[] directory = this.filepath.split("/");
        
        Integer count=0;
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) 
        {
            String line;
            while ((line = br.readLine()) != null) 
            {
                Matcher checkHeader = Pattern.compile(">").matcher(line);
                if (checkHeader.find() == true){
                    if (count !=0){
                        String str = contentBuilder.toString();
                        Sequence seq = new Sequence(str);
                        Integer i = str.length();
                        sequences.put(i.doubleValue(), seq);
                        contentBuilder = new StringBuilder();
                    }
                }
                else{
                    contentBuilder.append(line);
                }
                count+=1;
            }
            if((line = br.readLine()) == null && count !=0){
                String str = contentBuilder.toString();
                Sequence seq = new Sequence(str);
                Integer i = str.length();
                sequences.put(i.doubleValue(), seq);
                contentBuilder = new StringBuilder();
            }

        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    /**
     *
     * @param Double size
     * @return Sequence object corresponding to that size
     */
    public Sequence getSequence(Double size){
        return sequences.get(size);
    }
}

