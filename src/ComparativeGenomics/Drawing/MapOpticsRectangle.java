package ComparativeGenomics.Drawing;

import ComparativeGenomics.FileHandling.DataHandling.Gene;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author franpeters
 */
public class MapOpticsRectangle extends Rectangle2D.Double{

    private Gene gene;

    
    public MapOpticsRectangle(){
        
    }
    public MapOpticsRectangle(double x, double y, double width, double height) {  
            setRect(x, y, width, height);
    }
    public void setGene(Gene gene){
        this.gene=gene;
       
    }

    
    public Gene getGene(){
        return this.gene;
    }
    
    public void setRectangle(double x, double y, double width, double height) {  
            setRect(x, y, width, height);
    }
        
}

