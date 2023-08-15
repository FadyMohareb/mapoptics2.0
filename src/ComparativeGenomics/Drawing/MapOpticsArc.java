package ComparativeGenomics.Drawing;

import java.awt.geom.Arc2D;

/**
 * Extends <code>Arc2D</code>. Arc drawn between two chromosome on the circos plots
 * 
 * @author franpeters
 */
public class MapOpticsArc extends Arc2D.Double {

    String chrName;

    /**
     * Constructor, sets the location, size, angular extents and closure type
     * of this arc to the specified values
     * 
     * @param x x-coordinate of the upper-left corner of this arc
     * @param y y-coordinate of the upper-left corner of this arc
     * @param size1 overall width of the full ellipse of which this arc is a partial section
     * @param size2 overall height of the full ellipse of which this arc is a partial section
     * @param start starting angle of this arc in degrees
     * @param var angular extend of this arc in degrees
     * @param name closure type for this arc (OPEN, CHORD, PIE)
     */
    public MapOpticsArc(double x, double y, double size1, double size2, double start, double var, String name) {
        setArc(x, y, size1, size2, start, var, Arc2D.PIE);
        this.chrName = name;
    }

    /**
     * Gets chromosome name of this arc
     * 
     * @return chrName chromosome name
     */
    public String returnChrName() {
        return this.chrName;
    }
}
