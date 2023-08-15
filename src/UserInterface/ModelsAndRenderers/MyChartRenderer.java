package UserInterface.ModelsAndRenderers;

import java.awt.Color;
import java.awt.Paint;
import org.jfree.chart.renderer.category.BarRenderer;

/**
 * Displays bar chart.
 * 
 * @author Josie
 */
public class MyChartRenderer extends BarRenderer {

    public int selectedBar;

    /**
     * Sets selected bar
     * 
     * @param selectedBar id of selected bar
     */
    public void setSelectedBar(int selectedBar) {
        this.selectedBar = selectedBar;
    }

    /**
     * Gets item to paint
     * 
     * @param row row of item of interest
     * @param col column of item of interest
     * @return color of item or item to paint
     */
    @Override
    public Paint getItemPaint(int row, int col) {
        if (col == this.selectedBar) {
            return new Color(170, 255, 128);
        } else {
            return super.getItemPaint(row, col);
        }
    }
}
