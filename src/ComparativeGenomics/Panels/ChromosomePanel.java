package ComparativeGenomics.Panels;

import ComparativeGenomics.FileHandling.DataHandling.Chromosome;
import ComparativeGenomics.Drawing.XmapShape;
import ComparativeGenomics.Drawing.MapOpticsRectangle;
import ComparativeGenomics.FileHandling.DataHandling.XmapData;
import ComparativeGenomics.FileHandling.DataHandling.CmapData;
import ComparativeGenomics.FileHandling.DataHandling.Pair;
import ComparativeGenomics.FileHandling.DataHandling.Site;
import ComparativeGenomics.FileHandling.Cmap;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author franpeters Extends javax.swing.JPanel which draws all of the XmapData
 * objects corresponding to a chosen Chromosome. Each alignment is drawn using
 * an alternating colour.
 */
public class ChromosomePanel extends javax.swing.JPanel implements MouseListener, MouseMotionListener {
//  for saving required data to visualise

    private Chromosome chr;
    private Cmap qryCmap;
    private boolean chrAdded = false;
//    private Alignment comp = null;

//    for saving mouse values
    int dx;
    int dy;
    private Point point;
    public boolean pressed = false;

//    for drawing
    XmapShape alignShape;
    private Integer w;
    private Integer h;
    private Double relSize;
    private final Integer startX = 10;
    private final Integer startY = 10;
    private boolean alignment = false;
    private boolean released = false;
    private Font chrFont = new Font("Arial", 1, 8);
    Double lastStart = Double.valueOf(startX);

//    list of shapes to draw
    private ArrayList<XmapShape> alignShapes = new ArrayList();

    /**
     * Creates new ChromosomePanel
     */
    public ChromosomePanel() {
        this.setBackground(Color.WHITE);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        w = this.getWidth() - startX * 2;
        h = this.getHeight();
        initComponents();
    }

    /**
     *
     * @param cmap Set the query Cmap data
     */
    public void setQueryCmap(Cmap cmap) {
        this.qryCmap = cmap;
    }

    /**
     * o Sets the chromosome of the reference genome which has been selected by
     * the user.
     *
     * @param chr set the Chromosome to view
     */
    public void setChr(Chromosome chr) {

        if (this.alignShapes != null) {
            this.alignShapes.clear();
        }
        if (chr != null) {
            System.out.println("Chromosome panel chr set");
            chrAdded = true;
            w = this.getWidth() - startX * 2;
            h = this.getHeight();
            this.chr = chr;
            this.relSize = w / this.chr.getSize();
            for (XmapData map : this.chr.getAlignments()) {
                addAlignment(map);
            }
            System.out.println("All alignments read");
            this.repaint();
            System.out.println("Chromosome panel repainted");
        } else {
            System.out.println("Chromosome panel chr is NULL");
        }
    }

    /**
     *
     * @param map Add in alignment information
     */
    public void addAlignment(XmapData map) {
        alignment = true;
//                    get all the matched sites
        ArrayList<Pair> matches = map.returnAlignments();
//                    get the first and last matched sites on the reference for this query map
        Integer firstRefSiteID = matches.get(0).getRef();
        Integer lastRefSiteID = matches.get(matches.size() - 1).getRef();
//                    determine the position on the panel to draw these sites
        Site firstSiteRef = this.chr.getRefSites().get(firstRefSiteID);
        Site lastSiteRef = this.chr.getRefSites().get(lastRefSiteID);
        // Scaling of first and last chromosome positions
        Double relFirstPosRef = firstSiteRef.getPosition() * relSize;
        Double relLastPosRef = lastSiteRef.getPosition() * relSize;
        // Get CMAP having the same ID as current xmap
        CmapData qryCmapMap = this.qryCmap.getCmapByID(map.getQryID());
        // Draw lines of alignement of the maps
        XmapShape shape = new XmapShape(map, qryCmapMap, relFirstPosRef + 10, relLastPosRef + 10);
        // Add to list of shapes to draw
        alignShapes.add(shape);
        repaint();
    }

    /**
     * Draw the chromosome
     *
     * @param g To set the Graphics device
     */
    private void drawChromosome(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(chrFont);
//        draw with sub pixel precision
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        if (!chrAdded) {
            drawTextCentre(g2d, "No Chromosome has been selected, please select a chromosome.", w, h);
        } else {
            System.out.println("chr being drawn");
            g2d.setColor(Color.LIGHT_GRAY);
            Integer y = startY + 20 + 5;
            Double yPos = y.doubleValue();
            MapOpticsRectangle rect = new MapOpticsRectangle(startX.doubleValue(), yPos, w.doubleValue(), 60);
            Shape refRect = rect;
            g2d.draw(refRect);
            g2d.fill(refRect);

            g2d.setColor(Color.BLACK);
            drawScaleBar(g2d, rect);

        }
        if (!alignShapes.isEmpty()) {
            drawAlignments(g2d);
        }
    }

    /**
     * Draw all the alignements
     *
     * @param g2d To set the Graphics2D device
     */
    private void drawAlignments(Graphics2D g2d) {
        Integer count = 0;
        for (XmapShape s : alignShapes) {
//          Set the colour
            Color colour = setAlignmentColour(count);
            s.setAlignColour(colour);
            s.drawAlignment(g2d);
            count += 1;
        }
    }

    /**
     * Sets the colour of a rectangle to indicate presence of an alignment to
     * the query genome.
     *
     * @param Int Number of the alignment, to determine which colour is accessed
     * from the array
     * @return Colour of the alignment at that index
     */
    private Color setAlignmentColour(Integer Int) {
        Color[] colours = {Color.decode("#3792ff"), Color.decode("#20cdf5"), Color.decode("#8ef2f4"), Color.decode("#f179a7"), Color.decode("#fcc5f1")};
        Integer number = colours.length;

        if (Int > number) {
            if (Int % number == 0 && ((Int / number) < 5)) {
                return colours[Int / number];
            } else {
                Double newInt = (Int - 5 * Math.floor((Int / number)));
                return colours[newInt.intValue()];
            }
        }
        if (Int < number) {
            return colours[Int];
        } else {
            System.out.println(Int);
            return Color.MAGENTA;
        }
    }

    /**
     *
     * @param g2d To set the Graphics2D device
     * @param string String to draw in the centre
     * @param width Width of area to draw in the centre of
     * @param height Height of area to draw in the centre of
     */
    private static void drawTextCentre(Graphics2D g2d, String string,
            Integer width, Integer height) {
        int stringWidth = (int) g2d.getFontMetrics().getStringBounds(string, g2d).getWidth();
        int stringHeight = (int) g2d.getFontMetrics().getStringBounds(string, g2d).getHeight();

        int horizontalCenter = width / 2 - stringWidth / 2;
        int verticalCenter = height / 2 - stringHeight / 2;
        g2d.drawString(string, horizontalCenter, verticalCenter);
    }

    private void drawScaleBar(Graphics2D g2d, MapOpticsRectangle refRect) {

        g2d.drawLine((int) refRect.getMinX(), startY, (int) (refRect.getMinX() + refRect.getWidth()), startY);
        int count = 0;
        int numScales = (int) refRect.getWidth() / 100;
        double length = chr.getSize();
        g2d.setFont(chrFont);
        if (numScales != 0) {
            for (int i = 0; i < numScales + 1; i++) {
                g2d.drawLine((int) (refRect.getMinX() + (refRect.getWidth() / numScales) * i), startY, (int) (refRect.getMinX() + (refRect.getWidth() / numScales) * i), startY + 5);
                g2d.drawString(String.format("%.2f", ((double) count) / 100000) + " Mb", (int) (refRect.getMinX() + ((refRect.getWidth() / numScales) * i) - g2d.getFontMetrics().stringWidth(String.format("%.2f", ((double) count) / 100000) + " Mb") / 2), startY + 15);
                count = (int) (count + (length / numScales));
            }
        } else {
            g2d.drawLine((int) (refRect.getMinX()), startY, (int) (refRect.getMinX()), startY + 5);
            g2d.drawString(String.format("%.2f", 0.0) + " Mb", (int) (refRect.getMinX() - g2d.getFontMetrics().stringWidth(String.format("%.2f", 0.0) + " kb") / 2), startY + 15);
            g2d.drawLine((int) (refRect.getMinX() + refRect.getWidth()), startY, (int) (refRect.getMinX() + refRect.getWidth()), startY + 5);
            g2d.drawString(String.format("%.2f", length / 100000) + " Mb", (int) (refRect.getMinX() + refRect.getWidth() - g2d.getFontMetrics().stringWidth(String.format("%.2f", length / 100000) + " Mb") / 2), startY + 15);

        }
    }

    /**
     *
     * @param g To access the Graphics device
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawChromosome(g);
    }

    public void drawScaleBar(Graphics g) {

    }

    public void selectAlignment(Integer xmapID) {
        for (XmapShape shape : alignShapes) {
            if (shape.getXmapID() == xmapID) {
                shape.setSelected(true);
            }
        }
        repaint();
    }

    public void clearSelection() {
        for (XmapShape shape : alignShapes) {
            shape.setSelected(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jColorChooser1 = new javax.swing.JColorChooser();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        jButton1.setText("Set Colour");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addComponent(jColorChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jColorChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 812, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 345, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JColorChooser jColorChooser1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked(MouseEvent e) {
//        System.out.println("clicked");
//       if (alignment) {
//            draggedShape = null;
//            pressed = true;
//            this.point = e.getPoint();
//            Integer i=0;
//            for (XmapShape s : alignShapes) {
//                if(s.getRect().contains(point)){
//                    draggedShape=i;
//                    if (e.getButton() == MouseEvent.BUTTON3) {
//                       this.alignShapes.get(this.draggedShape).drawSites(true);
//                    }else{
//                    this.alignShapes.get(this.draggedShape).setSelected(true);
//                    setCursor(new Cursor(Cursor.HAND_CURSOR));
//                    i++;
//                    }
//                   
//                    
//                }
//            }
//        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        System.out.println("pressed");
//        if (alignment) {
//            draggedShape = null;
//            pressed = true;
//            this.point = e.getPoint();
//            Integer i=0;
//            for (XmapShape s : alignShapes) {
//                if(s.getRect().contains(point)){
//                    
//                    System.out.println("contains");
//                    draggedShape=i;
//                    this.alignShapes.get(this.draggedShape).setSelected(true);
//                    setCursor(new Cursor(Cursor.HAND_CURSOR));
//                    i++;
//                }
//            }
//        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        System.out.println("released");
//        pressed = false;
//        this.alignShapes.get(this.draggedShape).drawSites(false);
//        this.alignShapes.get(this.draggedShape).setSelected(false);
//        repaint();
//        this.draggedShape=null;
//        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
//        System.out.println("dragged");
//        if (point != null && pressed && this.draggedShape!=null) {
//            this.dx = (e.getX() - point.x);
//            this.dy = (e.getY() - point.y);
//            System.out.println("change coords" + this.dx + " " +this.dy);
//            this.alignShapes.get(this.draggedShape).setDeltaX(Double.valueOf(this.dx));
//            this.alignShapes.get(this.draggedShape).setDeltaY(Double.valueOf(this.dy));
//            this.alignShapes.get(this.draggedShape).setSelected(true);
//            repaint();
//        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    /**
     * To allow the user to export the JPanel image as a file
     *
     * @param name File name to save the image as
     * @param type Format to save the image
     * @param location Location to save the image to
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

}
