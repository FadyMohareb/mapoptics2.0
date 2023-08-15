package ComparativeGenomics.Drawing;

import ComparativeGenomics.FileHandling.DataHandling.Gene;
import java.awt.geom.Rectangle2D;

/**
 * Draws gene shapes on <code>QueryPanel</code>. Extends methods in
 * <code>java.awt.Rectangke2D</code>.
 *
 * @author franpeters
 */
public class MapOpticsRectangle extends Rectangle2D.Double {

    private Gene gene;

    /**
     * Constructor
     */
    public MapOpticsRectangle() {

    }

    /**
     * Constructor with rectangle size and position
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param width width
     * @param height height
     */
    public MapOpticsRectangle(double x, double y, double width, double height) {
        setRect(x, y, width, height);
    }

    /**
     * Sets the gene associated with this rectangle
     *
     * @param gene gene
     */
    public void setGene(Gene gene) {
        this.gene = gene;

    }

    /**
     * Returns the gene associated with this rectangle
     * @return gene
     */
    public Gene getGene() {
        return this.gene;
    }

    /**
     * Sets a rectangle
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param width width
     * @param height height
     */
    public void setRectangle(double x, double y, double width, double height) {
        setRect(x, y, width, height);
    }
}
