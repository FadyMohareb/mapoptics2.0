package ComparativeGenomics.Panels;

import ComparativeGenomics.FileHandling.DataHandling.Alignment;
import ComparativeGenomics.FileHandling.Karyotype;
import ComparativeGenomics.StructuralVariant.Translocation;
import ComparativeGenomics.Drawing.MapOpticsArc;
import ComparativeGenomics.FileHandling.DataHandling.Genome;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Draws the chromosome making up the genome in a circle.
 * Indicates any detected translocation events using the MapOpticsArcs and
 * Curve2D objects.
 *
 * @author franpeters
 * @author Marie Schmit 
 */
public class CircosPanel extends javax.swing.JPanel implements MouseListener, MouseMotionListener {

    Karyotype karyotype = null;
    boolean trans = false;
    Genome refGenome;
    ArrayList<Double> chrSizes = new ArrayList();
    ArrayList<String> chrNames = new ArrayList();
    ArrayList<Translocation> translocations = new ArrayList();
    HashMap<String, MapOpticsArc> arcs = new HashMap();

    /**
     * Creates new CircosPanel
     */
    public CircosPanel() {
        initComponents();
    }

    /**
     * Sets chromosomes that make up the reference genome displayed on the circos plot
     * 
     * @param kary chromosomes of the reference genome karyotype
     * @param comp alignment
     */
    public void setKaryotype(Karyotype kary, Alignment comp) {
        this.karyotype = kary;
        this.chrSizes = this.karyotype.getChrRelativeStarts();
        this.chrNames = this.karyotype.getChrNames();
        this.refGenome = comp.getRefGenome();
        this.translocations = comp.getTranslocations();
        trans = true;
        repaint();
    }
    
    /**
     * Sets karyotype and translocations from given indexes.
     * Extract a sublist of translocations from the alignments translocations,
     * to display only a part of the translocations
     * on the circos plot and translocations table.
     * 
     * @param kary Karyotype
     * @param comp Alignment object
     * @param fromIndex Index of the beginning of the translocatione extracted sublist
     * @param toIndex Index of the end of the translocations extracted sublist
     */
    public void setKaryotype(Karyotype kary, Alignment comp, int fromIndex, int toIndex) {
        this.karyotype = kary;
        this.chrSizes = this.karyotype.getChrRelativeStarts();
        this.chrNames = this.karyotype.getChrNames();
        this.refGenome = comp.getRefGenome();
        this.translocations = comp.getLocalisedTranslocations(fromIndex, toIndex);
        trans = true;
        repaint();
    }

    /**
     * Draw circos plot
     * 
     * @param g graphic on which the circos plot should be drawn
     */
    private void drawCircos(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        //get size of drawing panel
        this.setBackground(Color.WHITE);
        Integer w = this.getWidth() / 10;
        Integer h = this.getHeight() / 10;
        Integer W = this.getWidth() / 2;
        Integer H = this.getHeight() / 2;
        List<Double> chrAngles = new ArrayList<>();

        if (this.karyotype != null) {
            int numChrs = this.karyotype.getNumChrs();
//            ArrayList<Double> chrs = this.karyotype.getChrSizesCircos();
            for (Integer i = 0; i < numChrs; i++) {
                Double angle = Double.valueOf((double) 360 / numChrs);
                Double chrAngle = angle;
                chrAngles.add(chrAngle);
            }

            // Determine the center of the panel
            int cntrX = getWidth() / 2;
            int cntrY = getHeight() / 2;

            // Calculate the radius
            int outer = getWidth() / 4;
            int inner = getWidth() / 5;

            drawChrs(chrAngles, chrNames, g2d, 90, outer * 2, cntrX - outer, cntrY - outer);

            if (trans) {
                g2d.setColor(Color.white);
                g2d.fillOval(cntrX - inner, cntrY - inner, inner * 2, inner * 2);
                g2d.setColor(Color.black);
            }

            Double angle = chrAngles.get(0); //this is the angle of the first chromosome
        }
    }

    /**
     * Calculates the sum of the angles between the elements making up the circos plot
     * 
     * @param list list of angles
     * @param lower first angle of the list, for which the sum is calculated
     * @param upper last angle of the list for which the sum is calculated
     * @return sum sum of angles
     */
    public Double sumAngles(List<Double> list, Integer lower, Integer upper) {
        Double sum = 0.0;
        for (int i = lower; i < upper; i++) {
            sum += list.get(i);
        }
        return sum;
    }

    /**
     * Draw chromosomes from the karyotype of this circos panel.
     * 
     * @param listAngles angles between the different chromosomes drawn on this circos plot
     * @param x x-coordinate of the upper-left corner of this arc
     * @param y y-coordinate of the upper-left corner of this arc
     * @param start starting angle of this arc in degrees
     * @param size angular extend of this arc in degrees
     */
    public void drawChrs(List<Double> listAngles, List<String> names, Graphics2D g2d, Integer start, Integer size, Integer x, Integer y) {

        // Draw the chromosomes in descending order
        for (int i = listAngles.size() - 1; i >= 0; i--) {
            // Alternate the colours
            if (i % 2 == 0) {
                g2d.setColor(Color.gray);
            } else {
                g2d.setColor(Color.LIGHT_GRAY);
            }
            MapOpticsArc arc = new MapOpticsArc(x, y, size, size, start, -(sumAngles(listAngles, 0, i + 1)), names.get(i));
            Shape arcShape = arc;
            // Centre pount of arc
            double cx = x + (size * 0.5);
            double cy = y + (size * 0.5);
//                    middle angle of arc
            double mAngle = start + (-(sumAngles(listAngles, 0, i + 1)) * 0.5);
//                    radius of arc at angle mAngle
            double radius = 0.5 * size * size / Math.sqrt(Math.pow(size * Math.cos(mAngle), 2) + Math.pow(size * Math.sin(mAngle), 2));
//                    middle point of arc
            double mx = cx + radius * Math.cos(mAngle);
            double my = cy + radius * Math.sin(mAngle);
//                    g2d.draw(arcShape);
//                    g2d.fill(arcShape);
            arcs.put(names.get(i), arc);
            double xP = arc.getEndPoint().getX();
            double yP = arc.getEndPoint().getY();
            float X = (float) xP;
            float Y = (float) yP;
            String chrName = this.chrNames.get(i);
            if (X < 286.0) {
                int stringWidth = (int) g2d.getFontMetrics().getStringBounds(chrName, g2d).getWidth();
                X -= stringWidth;
            }
            if (Y > 230.0) {
                int stringHeight = (int) g2d.getFontMetrics().getStringBounds(chrName, g2d).getHeight();
                Y += stringHeight;
            }
            g2d.setColor(Color.black);
            g2d.drawString(chrName, X, Y);

        }
    }

    /**
     * Draws arcs on the circos plot between two chromosomes sharing a translocation.
     * 
     * @param g graphical device
     */
    private void drawTranslocations(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (Translocation t : translocations) {
            try {
                Double x1 = arcs.get(t.getRefChr1Name()).getEndPoint().getX();
                Double y1 = arcs.get(t.getRefChr1Name()).getEndPoint().getY();

                Double x3 = arcs.get(t.getRefChr2Name()).getEndPoint().getX();
                Double y3 = arcs.get(t.getRefChr2Name()).getEndPoint().getY();
                
                QuadCurve2D.Double quad = new QuadCurve2D.Double(x1, y1, arcs.get(t.getRefChr2Name()).getCenterX(), arcs.get(t.getRefChr2Name()).getCenterY(), x3, y3);
                Shape shape3 = quad;
                g2d.setColor(Color.red);
                g2d.draw(shape3);
            } catch (Exception e) {
                System.out.println(e);
            }
        }

    }

    @Override
    /**
     * Repaint this circos panel
     * 
     * @param g graphic device
     */
    public void paint(Graphics g) {
        super.paint(g);
        drawCircos(g);
        if (trans) {
            drawTranslocations(g);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
