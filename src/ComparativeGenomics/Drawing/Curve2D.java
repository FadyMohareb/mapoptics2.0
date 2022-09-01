package ComparativeGenomics.Drawing;

import java.awt.geom.QuadCurve2D;

/**
 *
 * @author franpeters
 */
public class Curve2D extends QuadCurve2D.Double {
    
    private String translocationInfo;
    
    public Curve2D(float x1,float y1, float x2, float y2, float x3,float y3, String info ){
        this.setCurve(x1,y1,x2,y2,x3,y3);
        this.translocationInfo=info;
    }
    
    public String getInfo(){
        return this.translocationInfo;
    }

}
