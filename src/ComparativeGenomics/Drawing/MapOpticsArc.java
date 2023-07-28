package ComparativeGenomics.Drawing;

import java.awt.geom.Arc2D;

/**
 *
 * @author franpeters
 */
public class MapOpticsArc extends Arc2D.Double {

    String chrName;

    public MapOpticsArc(double x, double y, double size1, double size2, double start, double var, String name) {
        setArc(x, y, size1, size2, start, var, Arc2D.PIE);
        this.chrName = name;
    }

    public String returnChrName() {
        return this.chrName;
    }
}
