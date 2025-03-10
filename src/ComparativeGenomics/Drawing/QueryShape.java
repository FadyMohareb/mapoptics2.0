package ComparativeGenomics.Drawing;

import ComparativeGenomics.FileHandling.DataHandling.Match;
import ComparativeGenomics.FileHandling.DataHandling.Pair;
import ComparativeGenomics.FileHandling.DataHandling.Site;
import ComparativeGenomics.Drawing.MapOpticsRectangle;
import ComparativeGenomics.FileHandling.DataHandling.XmapData;
import ComparativeGenomics.FileHandling.DataHandling.CmapData;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.HashMap;

/**
 * Draws the query chromosome that has been aligned to the reference chromosome
 * in <code>QueryPanel</code>
 *
 * @author franpeters
 */
public class QueryShape {

    private CmapData cmap;
    private XmapData xmap;
    private MapOpticsRectangle queryRect = new MapOpticsRectangle();
    private Double deltaX = 0.0;
    private Double deltaY = 0.0;
    private Double scale; //how many pixels per base pair
    private boolean selected = false;

//      position of the query start
    Double queryStart;

//    coordinates to start drawing the rectangle
    private Double startX;
//    private Double endX;
    private Double startY;

    //    position of the first and last matched sites to be drawn
    private Double firstMatchPos;

//    coordinates of the first and last matched sites to be drawn
    private Double firstMatchX;
//    private Double lastMatchX;

    private Color matchedSiteColour = Color.green;
    private Color unmatchedSiteColour = Color.black;
    private Color rectColour = Color.lightGray;

    private Double queryLength;
    private Double queryLengthScaled;

    private Line2D.Double qrySiteLine;
    private Line2D.Double refSiteLine;
    HashMap<Integer, Double> refSitesRelPos = new HashMap();

    /**
     * Constructor with positins, scale, start y, cmap, xmap, start query
     *
     * @param refSitesRelPos relative position of reference sites
     * @param xPosFirstMatchedSite x coordinate of first matched site
     * @param startY y coordinate
     * @param scale scale
     * @param xmap xmap data
     * @param cmap cmap data
     * @param queryStart position of the query start
     */
    public QueryShape(HashMap<Integer, Double> refSitesRelPos, Double xPosFirstMatchedSite, double startY, Double scale, XmapData xmap, CmapData cmap, Double queryStart) {
        this.startY = startY;
        this.cmap = cmap;
        this.xmap = xmap;
        this.scale = scale;
        if (xmap.getOri()) {
            this.queryLength = xmap.getQryEnd() - xmap.getQryStart();
        } else {
            this.queryLength = xmap.getQryStart() - xmap.getQryEnd();
        }
        this.queryLengthScaled = this.queryLength * scale;
        this.queryStart = queryStart;
        this.refSitesRelPos = refSitesRelPos;
        if (xmap.getOri()) {
            this.firstMatchPos = xmap.returnFirstMatch().getQrySite().getPosition();
            this.firstMatchX = this.firstMatchPos * scale;
            this.startX = xPosFirstMatchedSite - firstMatchX;
        }
        if (!xmap.getOri()) {
            this.firstMatchPos = xmap.returnLastMatch().getQrySite().getPosition();
            this.firstMatchX = this.firstMatchPos * scale;
            this.startX = xPosFirstMatchedSite - firstMatchX;
        }

    }

    /**
     * Draws the query rectangle on the <code>JPanel</code>
     *
     * @param g2d graphical device
     */
    public void drawRect(Graphics2D g2d) {
        g2d.setColor(rectColour);
        queryRect = new MapOpticsRectangle(startX + this.deltaX, startY + this.deltaY, this.queryLengthScaled, 50);
        Shape rectShape = queryRect;
        g2d.draw(rectShape);
        g2d.fill(rectShape);
        if (selected) {
            g2d.setColor(Color.CYAN);
            g2d.setStroke(new BasicStroke(2));
            Shape borderShape = rectShape;
            g2d.draw(borderShape);
            g2d.setStroke(new BasicStroke(1));
        }
        for (Pair p : this.xmap.returnAlignments()) {
            g2d.setColor(Color.BLACK);
            if (p.getRefSite() != null) {
                if (this.refSitesRelPos.get(p.getRefSite().getSiteID()) != null) {
                    Line2D.Double line = new Line2D.Double(this.refSitesRelPos.get(p.getRefSite().getSiteID()),
                            120,
                            (p.getQrySite().getPosition() * scale) + startX + deltaX,
                            startY + deltaY);
                    Shape s = line;
                    g2d.draw(s);
                    qrySiteLine = new Line2D.Double((p.getQrySite().getPosition() * scale) + startX + deltaX, 220 + deltaY, (p.getQrySite().getPosition() * scale) + startX + deltaX, 220 + deltaY + 50);
                    refSiteLine = new Line2D.Double(this.refSitesRelPos.get(p.getRefSite().getSiteID()), 70, this.refSitesRelPos.get(p.getRefSite().getSiteID()), 120);
                    if (!p.getQrySite().isMatch()) {
                        if (p.getQrySite().getMatchesByXmapID(xmap.getID()) != null) {
                            g2d.setColor(matchedSiteColour);
                        }
                    } else {
                        g2d.setColor(unmatchedSiteColour);
                    }
                    Shape qry = qrySiteLine;
                    g2d.draw(qry);
                    g2d.setColor(matchedSiteColour);
                    Shape ref = refSiteLine;
                    g2d.draw(ref);
                }
            }
        }
    }

    /**
     * Gets rectangle
     * @return rectangle
     */
    public MapOpticsRectangle getRect() {
        return this.queryRect;
    }

    /**
     * Sets if the rectangle has been clicked on
     * @param bool selected
     */
    public void setSelected(boolean bool) {
        this.selected = bool;
    }

    /**
     * Records the change in position of the shape if it is dragged
     * @param dx x coordinate
     * @param dy y coordinate
     */
    public void moveRect(Double dx, Double dy) {
        this.deltaX = dx;
        this.deltaY = dy;
    }

    /**
     * Sets the colour of matched sites. Default is green
     * @param c colour
     */
    public void setMatchedColour(Color c) {
        this.matchedSiteColour = c;
    }

    /**
     * Sets the colour of unmatched sites. Default is black
     * @param c colour
     */
    public void setUnmatchedColour(Color c) {
        this.unmatchedSiteColour = c;
    }

    /**
     * Set the colour of query rectangle. Default is light grey
     * @param c colour
     */
    public void setRectColour(Color c) {
        this.rectColour = c;
    }

    /**
     * Changes the position of the shape in x axis
     * @param dX shape x coordinate
     */
    public void setDeltaX(Double dX) {
        this.deltaX = dX;
    }

    /**
     * Changes the position of the shape in y axis
     * @param dY shape y coordinate
     */
    public void setDeltaY(Double dY) {
        this.deltaY = dY;
    }
}
