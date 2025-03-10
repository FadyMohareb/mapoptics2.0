package ComparativeGenomics.FileHandling.DataHandling;

import ComparativeGenomics.FileHandling.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Stores all the metadata for example species of the genome,
 * amd group the files related to the genome. For instance Fasta and Karyotype files.
 *
 * @author franpeters
 */
public class Genome {

    private final String genomeName;
    private final Double genomeSize;
    private final Fasta fasta;
    private final Annot annot;
    private final Karyotype karyotype;
    private final Cmap cmap;
//    where the key is the cmap id
    private HashMap<Integer, Chromosome> chromosomes = new HashMap();

    private Integer numChrs = 0;
    private Integer i;

    /**
     * Constructor with name, cmap, karyotype, fasta and annotation file information
     * @param name genome name
     * @param cmap reference cmap file
     * @param kary karyotype file content
     * @param fasta fasta file content
     * @param annot annottaion file content
     */
    public Genome(String name, Cmap cmap, Karyotype kary, Fasta fasta, Annot annot) {
        this.genomeName = name;
        this.cmap = cmap;
        this.karyotype = kary;
        this.numChrs = karyotype.getNumChrs();
        this.genomeSize = this.cmap.getGenomeSize();
        this.annot = annot;
        this.fasta = fasta;
        setChromosomes();
    }

    /**
     * Gets the IDs of the cmaps of this genome
     * 
     * @return  cmap ids corresponding to this genome
     */
    public ArrayList<Integer> getCmapIDs() {
        return this.cmap.getCmapIDs();
    }

    /**
     * Sets all the chromosomes of this genome. Chromosome names are extracted from the karyotype file.
     * Chromosome size, saved in the karyotype, are used to map the chromosome name to its contig id in the cmap.
     */
    private void setChromosomes() {
        HashMap<Double, String> chrs = this.karyotype.getInfo();
//       iterate through every chr in the karyotype
        Iterator<Map.Entry<Double, String>> entries = chrs.entrySet().iterator();

        while(entries.hasNext()) {
            Map.Entry<Double, String> entry = entries.next();
//            this accesses each chr size and value. the chrSize can be used to access the map in cmap
            Double chrSize = entry.getKey();
            String chrName = entry.getValue();

            Double relSize = chrSize / this.genomeSize;
            CmapData map = this.cmap.getCmapBySize(chrSize);
            Sequence sequence = this.fasta.getSequence(chrSize);
            ArrayList<Gene> chrGenes = this.annot.getFeatureByChr(chrName.toLowerCase());
            if (map != null) {
                Chromosome chr = new Chromosome(chrName, map, relSize, sequence, chrGenes);
                chromosomes.put(map.getID(), chr);
            }
        }
    }

    /**
     * Gets all the chromosomes of this genome
     * @return list of chromosomes
     */
    public HashMap<Integer, Chromosome> getChromosomes() {
        return this.chromosomes;
    }

    /**
     * Gets this genome name
     * @return genome name
     */
    public String getName() {
        return this.genomeName;
    }

    /**
     * Gets the number of chromosomes in this genome
     * @return number of chromosomes
     */
    public Integer getNumChrs() {
        return this.numChrs;
    }

    /**
     * Gets the cmap corresponding to this genome
     * @return cmap
     */
    public Cmap getCmap() {
        return this.cmap;
    }

    /**
     * Gets the karyotype corresponding to this genome
     * @return karyotype
     */
    public Karyotype getKaryotype() {
        return this.karyotype;
    }

    /**
     * Gets annotations for this genome
     * @return annotation data
     */
    public Annot getAnnot() {
        return this.annot;
    }
}
