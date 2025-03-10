package ComparativeGenomics.Panels;

import java.awt.Graphics;
import java.awt.Graphics2D;
import ComparativeGenomics.FileHandling.DataHandling.Alignment;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Displays all the chromosomes of the reference.
 * 
 * @author franpeters
 */
public class GenomePanel extends javax.swing.JPanel {

    Alignment alignment = null;
    String type;

    /**
     * Creates new form GenomePanel
     */
    public GenomePanel() {
        initComponents();
    }

    /**
     * Sets alignments of this genome 
     * 
     * @param align alignment
     * @param type alignment type
     */
    public void setAlignment(Alignment align, String type) {
        this.alignment = align;
        this.type = type;
    }

    /**
     * Draws one rectangle per chromosome
     * 
     * @param g graphical device
     */
    void drawRectangles(Graphics g) {
        if (alignment == null) {
        } else {
            Graphics2D g2d = (Graphics2D) g;
            Integer w = (this.getWidth() - 10);
            Integer h = (this.getHeight() - 100);
            if ("reference".equals(this.type)) {
                this.alignment.getRefGenome().getKaryotype().drawChromosomes(g2d, w, h);
            }
        }
    }

    /**
     * Repaint this panel
     * 
     * @param g graphical device
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawRectangles(g);
    }

    /**
     * Saves an image of this panel
     * 
     * @param name image name
     * @param type image type
     * @param location file directory
     */
    public void saveImage(String name, String type, String location) {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        printAll(g2d);
        try {
            ImageIO.write(image, type, new File(location + name + "." + type));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setForeground(new java.awt.Color(255, 255, 255));
        setToolTipText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 401, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 195, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
