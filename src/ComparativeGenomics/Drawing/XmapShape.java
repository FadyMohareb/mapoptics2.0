package ComparativeGenomics.Drawing;

import ComparativeGenomics.FileHandling.DataHandling.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 *
 * @author franpeters To draw the alignment to the panel like this: _________ |
 * | | | | | |-------| |-------| Where the lines to join the alignment are
 * anchored to the site positions on the reference and the query can be moved
 * about, with the alignment lines following
 */
public class XmapShape {

    private Color alignColour = Color.BLACK;
    private Color queryColour = Color.magenta;
    private Color matchedSiteColour = Color.GREEN;
    private Color unmatchedSiteColour = Color.BLACK;
    private final Double relRefStart;
    private Double relRefEnd;
    private Double deltaX = 0.0;
    private Double deltaY = 0.0;
    private MapOpticsRectangle rectangle;
    private MapOpticsRectangle border;
    private Line2D.Double l1;
    private Line2D.Double l2;
    private Line2D.Double l3;
    private final CmapData cmap;
    private final XmapData xmap;

    private Double scale;
    private Double startX;
    private boolean selected = false;
    private boolean sites = false;
    private Integer xmapID;
    private Graphics2D g2d;

    public XmapShape(XmapData xmap, CmapData cmap, Double relStart, Double relEnd) {
        this.cmap = cmap;
        this.xmap = xmap;
        this.xmapID = xmap.getID();
        this.relRefStart = relStart;
        this.relRefEnd = relEnd;
    }

    public void drawAlignment(Graphics2D g2d) {
        this.g2d = g2d;
        try {
            if (xmap.getOri()) {
                // Get site of the query of the first aligned site ID (?)
                cmap.getSite(xmap.returnAlignments().get(0).getQry()).getPosition();
                /* 
            Scale the positions of the sites depending of the 
            real first and last sites, and the relative first and last sites
                 */
                Double firstSite = cmap.getSite(1).getPosition();
                Double lastSite = cmap.getSite(cmap.getSites().size()).getPosition();
                Double sitesDist = lastSite - firstSite;
                scale = (relRefEnd - relRefStart) / sitesDist;
                Double firstRelPos = scale * firstSite;
                Double queryWidth = cmap.getLength() * scale;
                // Calculate X coord of the first position, related to the start of the drawn alignment
                startX = relRefStart - firstRelPos;
                l1 = new Line2D.Double(relRefStart, 95, relRefStart + deltaX, 230 + deltaY);
                l2 = new Line2D.Double(relRefEnd, 95, relRefEnd + deltaX, 230 + deltaY);
                rectangle = new MapOpticsRectangle(startX + deltaX, 230 + deltaY, queryWidth, 35);
                if (selected) {
                    border = new MapOpticsRectangle(startX + deltaX, 230 + deltaY, queryWidth, 35);
                }
            } else {
                cmap.getSite(xmap.returnAlignments().get(0).getQry()).getPosition();
                Double firstSite = cmap.getSite(1).getPosition();
                Double lastSite = cmap.getSite(cmap.getSites().size()).getPosition();
                Double sitesDist = lastSite - firstSite;
                scale = (relRefEnd - relRefStart) / sitesDist;
                Double firstRelPos = scale * firstSite;
                Double queryWidth = cmap.getLength() * scale;
                startX = relRefStart - firstRelPos;
                l1 = new Line2D.Double(relRefStart, 95, relRefEnd + deltaX, 230 + deltaY);
                l2 = new Line2D.Double(relRefEnd, 95, relRefStart + deltaX, 230 + deltaY);
                rectangle = new MapOpticsRectangle(startX + deltaX, 230 + deltaY, queryWidth, 35);
                if (selected) {
                    border = new MapOpticsRectangle(startX + deltaX, 230 + deltaY, queryWidth, 35);
                }

            }
            // Draw the rectangles and lines
            Shape rect = rectangle;
            Shape shape1 = l1;
            Shape shape2 = l2;
            g2d.setColor(alignColour);
            g2d.draw(shape1);
            g2d.draw(shape2);
            g2d.setColor(queryColour);
            g2d.draw(rect);
            g2d.fill(rect);
            if (selected) {
                g2d.setColor(Color.black);
                g2d.setStroke(new BasicStroke(2));
                Shape borderShape = border;
                g2d.draw(borderShape);
            }
            if (sites) {
                for (HashMap.Entry<Integer, Site> entry : cmap.getSites().entrySet()) {
                    Integer i = entry.getKey();
                    Site site = entry.getValue();
                    Double relPos = site.getPosition() * scale;
                    l3 = new Line2D.Double(startX + deltaX + relPos, 300 + deltaY, startX + deltaX + relPos, 300 + deltaY + 35);
                    if (site.isMatch()) {
                        g2d.setColor(matchedSiteColour);
                    } else {
                        g2d.setColor(unmatchedSiteColour);
                    }
                    Shape shape3 = l3;
                    g2d.draw(shape3);
                }
            }
        } catch (Exception e) {
            System.out.println("Data could not be extracted from CMAP, or other problem occured. Impossible to draw chart.");
        }
    }

    public MapOpticsRectangle getRect() {
        return this.rectangle;
    }

    public void setAlignColour(Color colour) {
        this.queryColour = colour;
    }

    public void setQueryColour(Color colour) {
        this.queryColour = colour;
    }

    public void setMatchedSiteColour(Color colour) {
        this.matchedSiteColour = colour;
    }

    public void setUnmatchedSiteColour(Color colour) {
        this.unmatchedSiteColour = colour;
    }

    public void setDeltaX(Double dX) {
        this.deltaX = dX;
    }

    public void setDeltaY(Double dY) {
        this.deltaY = dY;
    }

    public void setSelected(boolean bool) {
        this.selected = bool;
    }

    public void drawSites(boolean bool) {
        this.sites = bool;
    }

    public Integer getXmapID() {
        return this.xmapID;
    }
}
