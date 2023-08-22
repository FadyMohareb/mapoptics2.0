package DataTypes;

import Algorithms.DetectSV;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * @author Anisha
 *
 * Detects medium-sized inversions.
 * */
@Deprecated
public class Inversion extends SV {

    private static List<String> parsedCigar;
    private static List<String> revCompCigar;
    private Map<Integer, List<Integer>> mappedCigar;
    private String hitEnum;
    private static Cigar cigar;
    private static List<Integer> refLabels;
    private static List<Integer> qryLabels;

    public Inversion(DetectSV detectSV, String hitEnum) {
        super();
        this.hitEnum = hitEnum;
        cigar = new Cigar(hitEnum);
        mappedCigar = new HashMap<>();
        revCompCigar = new ArrayList<>();
        parsedCigar = new ArrayList<>();
        refLabels = new ArrayList<>();
        qryLabels = new ArrayList<>();

    }
    
    /**
     * For medium sized inversions, identifies reversed CIGAR
     * @param cigar CIGAR string
     */
    public static void setRevCompCigar(Cigar cigar) {
        parsedCigar = cigar.parseHitEnum();
        revCompCigar = cigar.reverseComplement(parsedCigar);
    }

    List<String> setRevPalindromes(Cigar cigar) {
        List<String> revPalindromes = cigar.getRevPalindromes(parsedCigar, revCompCigar);
        return revPalindromes;
    }

    /**
     * Matches the distance sites between adjacent label sites on ref and
     * those between adjacent labels on the reversed query
     * 
     * @param revPalindrome reversed palindrome
     * @param mappedCigar cigar
     * @param qryAlignments query alignments
     * @param refSites reference sites
     * @param qrySites query sites
     */
    public static void getSites(String revPalindrome, Map<Integer, List<Integer>> mappedCigar,
                                    Map<Integer, List<Integer>> qryAlignments,
                                    Map<Integer, Double> refSites, Map<Integer, Double> qrySites) {
        // get indices for the label sites of revPalindromes
        String parsedCig = String.join("", parsedCigar);

        int startInd = parsedCig.indexOf(revPalindrome) - 1;
        int endInd = parsedCig.indexOf(revPalindrome.length() - 1) - 1;
        // get the site corresponding to the beginning
        List<Integer> refLabels = new ArrayList<>();
        List<Integer> qryLabels = new ArrayList<>();
        // loop through index sites corresponding to the CIGAR palindrome sequence
        for (int i = startInd; i <= endInd; i++) {
            if (parsedCigar.get(i).equals("D")) {
                // its refsite label
                refLabels.add(mappedCigar.get(i).get(0));
            } else if (parsedCigar.get(i).equals("D")) {
                qryLabels.add(mappedCigar.get(i).get(0));
            } else {
                refLabels.add(mappedCigar.get(i).get(0));
                qryLabels.add(mappedCigar.get(i).get(1));
            }
        }

    }

    /**
     * Gets distances
     */
    public static void getDistances() {
        // get distances
        // traverse in the forward direction on the reference labels
        // traverse on the reverse direction on the query labels
    }

    /**
     * Sets type of indel to inversion
     */
    @Override
    public void setType() {
        this.type = "Inversion";
    }
}
