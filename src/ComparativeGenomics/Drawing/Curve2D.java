package ComparativeGenomics.Drawing;

import java.awt.geom.QuadCurve2D;

/**
 * Draws curves between two chromosomes in circos view to graphically indicate a
 * translocation event. Extends the methods in
 * <code>java.awt.geom.QuadCurve2D</code>.
 *
 * @author franpeters
 */
public class Curve2D extends QuadCurve2D.Double {

    private String translocationInfo;

    /**
     * Constructor with curve size and positions
     *
     * @param x1 x coordinate of start point
     * @param y1 y coordinate of start point
     * @param x2 x coordinate of control point
     * @param y2 y coordinate of control point
     * @param x3 x coordinate of end point
     * @param y3 y coordinate of end point
     * @param info translocation
     */
    public Curve2D(float x1, float y1, float x2, float y2, float x3, float y3, String info) {
        this.setCurve(x1, y1, x2, y2, x3, y3);
        this.translocationInfo = info;
    }

    /**
     * Gets translocations information for future user interactivity
     *
     * @return translocations information
     */
    public String getInfo() {
        return this.translocationInfo;
    }

}
