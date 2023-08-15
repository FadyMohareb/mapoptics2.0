package FileHandling;

import DataTypes.ContigInfo;
import DataTypes.Reference;
import UserInterface.ModelsAndRenderers.MapOpticsModel;

import java.awt.geom.Rectangle2D;
import UserInterface.ModelsAndRenderers.MapOpticsModel;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Reads and parse a FASTA file
 *
 * @author s276817
 */
public class FastaReader {

    /**
     * Read and pasre fasat data in a hashmap
     *
     * @param filename fasta file name
     * @param contigNames hashmap of contigs names
     * @return hasmap of fasta file data
     */
    public static LinkedHashMap<String, ArrayList<Integer>> readFasta(String filename, LinkedHashMap<String, String> contigNames) {
        ArrayList<String> sequenceArray = new ArrayList();
        LinkedHashMap<String, ArrayList<Integer>> sequences = new LinkedHashMap();
        String name = "";

        // Try reading file
        try {
            BufferedReader read = new BufferedReader(new FileReader(filename));
            String line;
            String sequence = "";
            // If line starts with > add sequence to array
            while ((line = read.readLine()) != null) {
                if (line.startsWith(">")) {
                    if (sequenceArray.size() > 0) {
                        if (contigNames.keySet().contains(name)) {
                            sequence = concatenateStrings(sequenceArray);
                            //save the gap info
                            ArrayList<Integer> gaps = new ArrayList<>(findGaps(sequence));
                            if (!gaps.isEmpty()) {
                                sequences.put(contigNames.get(name), gaps);

                            }

                        }
                        sequenceArray.clear();
                    }
                    name = line.substring(1);
                } else {
                    sequenceArray.add(line);
                }
            }
            if (sequenceArray.size() > 0) {
                sequence = concatenateStrings(sequenceArray);
                //save the gap info
                ArrayList<Integer> gaps = new ArrayList<>(findGaps(sequence));
                if (!gaps.isEmpty()) {
                    sequences.put(contigNames.get(name), gaps);
                    System.out.println(gaps + " 1 ");
                }
                sequenceArray.clear();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error loading FASTA file\n\nError: " + ex,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return sequences;
    }

    /**
     * Fins gaps in the fasta sequence
     *
     * @param seq DNA sequence
     * @return array list of gaps positions
     */
    public static ArrayList<Integer> findGaps(String seq) {
        ArrayList<Integer> gaps = new ArrayList();

        String match = "N";
        int index1 = seq.indexOf(match);////the start position of the first "N" is index1
        int index2;
        int count = 0;

        while (index1 >= 0) {
            index2 = seq.indexOf(match, index1 + 1);//the start position of the second "N" is index1+1

            if (index2 - index1 == 1) {
                count++;
            } else {
                gaps.add(index1 - count);
                gaps.add(index1);
                count = 0;
            }
            index1 = index2;
        }

        return gaps;
    }

    /**
     * Concatenates array list of strings
     *
     * @param items array list of string
     * @return concatenated string
     */
    public static String concatenateStrings(ArrayList<String> items) {
        int expectedSize = 0;
        for (String item : items) {
            expectedSize += item.length();
        }
        StringBuffer result = new StringBuffer(expectedSize);
        for (String item : items) {
            result.append(item);
        }
        return result.toString();
    }

    /**
     * Read and parse file containing keys
     *
     * @param filename path of the key file
     * @param contigIds list of contigs IDs
     * @param refqry string indicating if a reference or a query is used
     * @param model model containing query and reference data
     * @return hashmap of keys
     * the key is the name of the key (second column in key file), the value is its ID (first column of the file)
     */
    public static LinkedHashMap<String, String> readKeyFile(String filename, java.util.List<String> contigIds, String refqry, MapOpticsModel model) {
        LinkedHashMap<String, String> contigNames = new LinkedHashMap();

        // Try reading file
        try {
            BufferedReader read = new BufferedReader(new FileReader(filename));
            String line;
            String[] fields;
            String id;
            String name;
            String length;
            while ((line = read.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("CompntId")) {
                    continue;
                }
                fields = line.split("\t");
                id = fields[0];
                name = fields[1];
                length = fields[2];
                if (refqry == "ref") {
                    if (contigIds.contains(id)) {

                        if (Double.parseDouble(MapOpticsModel.getRefLen().get(id)) == Double.parseDouble(length)) {
                            contigNames.put(name, id);
                        }
                    }
                } else if (refqry == "qry") {
                    if (contigIds.contains(id)) {
                        if (Double.parseDouble(MapOpticsModel.getQueryLen().get(id)) == Double.parseDouble(length)) {
                            contigNames.put(name, id);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error loading KEY file\n\nError: " + ex,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return contigNames;
    }
}
