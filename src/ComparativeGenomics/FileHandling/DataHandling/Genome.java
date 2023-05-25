package ComparativeGenomics.FileHandling.DataHandling;

import ComparativeGenomics.FileHandling.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
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
    private HashMap<Integer,Chromosome> chromosomes = new HashMap();
   
   private Integer numChrs=0;
   private Integer i;
   
   public Genome(String name,Cmap cmap,Karyotype kary, Fasta fasta, Annot annot){
       this.genomeName = name;
       this.cmap = cmap;
       this.karyotype = kary;
       this.numChrs = karyotype.getNumChrs();
       this.genomeSize= this.cmap.getGenomeSize();
       this.annot=annot;
       this.fasta=fasta;
       setChromosomes();
   }
   /**
    * 
    * @return Return the cmap ids corresponding to this genome
    */
   public ArrayList<Integer> getCmapIDs(){
       return this.cmap.getCmapIDs();
   }
   private void setChromosomes(){
              
       HashMap<Double, String> chrs = this.karyotype.getInfo();
//       iterate through every chr in the karyotype
        Iterator<Map.Entry<Double, String>> entries = chrs.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry<Double, String> entry = entries.next();
//            this accesses each chr size and value. the chrSize can be used to access the map in cmap
            Double chrSize = entry.getKey();
            String chrName = entry.getValue();
 
            Double relSize = chrSize/this.genomeSize;
            CmapData map = this.cmap.getCmapBySize(chrSize);
            Sequence sequence = this.fasta.getSequence(chrSize);
            ArrayList<Gene> chrGenes = this.annot.getFeatureByChr(chrName);
//            System.out.println(chrSize);
            if (map!=null){
                Chromosome chr = new Chromosome(chrName,map,relSize,sequence,chrGenes);
                chromosomes.put(map.getID(), chr);
            }
            
        }
   }
   
   public HashMap<Integer,Chromosome> getChromosomes(){
        return this.chromosomes;
    }

   public String getName(){
       return this.genomeName;
   }
   public Integer getNumChrs(){
       return this.numChrs;
   }
   public Cmap getCmap(){
       return this.cmap;
   }
   public Karyotype getKaryotype(){
       return this.karyotype;
   }
   public Annot getAnnot(){
       return this.annot;
   }
}
