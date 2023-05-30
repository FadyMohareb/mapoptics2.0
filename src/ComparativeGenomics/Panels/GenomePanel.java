package ComparativeGenomics.Panels;

import java.awt.Graphics;
import java.awt.Graphics2D;
import ComparativeGenomics.FileHandling.DataHandling.Alignment;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
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

    public void setAlignment(Alignment align, String type) {
        this.alignment = align;
        this.type = type;
    }

    void drawRectangles(Graphics g) {
        if (alignment == null) {
        } else {
            Graphics2D g2d = (Graphics2D) g;
            Integer w = (this.getWidth() - 10);
            Integer h = (this.getHeight() - 100);
            if ("reference".equals(this.type)) {
                this.alignment.getRefGenome().getKaryotype().drawChromosomes(g2d, w, h);
            }
//         if("query".equals(this.type)){
//              this.alignment.getQryGenome().getKaryotype().drawChromosomes(g2d, w, h);
//         }

        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawRectangles(g);
    }

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
