package ComparativeGenomics.Panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import javax.swing.JPanel;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Styler.ChartTheme;

/**
 * Plots the results of calc_best_enzymes.sh script of an uploaded fasta file
 * using <code>XChart</code>
 *
 * @author franpeters
 */
public class EnzymeChartPanel extends JPanel {

    HashMap<String, Double> siteDensities;
    Integer bestEnzIndx;
    String bestEnz;

    HashMap<String, String> enzymeMap = new HashMap();
    boolean drawChart = false;
    ChartTheme style = ChartTheme.GGPlot2;
    // Create Chart
    CategoryChart chart
            = new CategoryChartBuilder()
                    .width(this.getWidth()).height(this.getHeight())
                    .title("Comparing Enzymes for in Silico Digestion").xAxisTitle("Enzyme").yAxisTitle("Sites / 100kb")
                    .theme(style).build();

    /**
     * Constructor
     */
    public EnzymeChartPanel() {
        enzymeMap.put("GCTCTTC", "BspQI");
        enzymeMap.put("CCTCAGC", "BbvCI");
        enzymeMap.put("ATCGAT", "bseCI");
        enzymeMap.put("CACGAG", "BssSI");
        enzymeMap.put("GCAATG", "BsrDI");
        enzymeMap.put("CCTCAGC", "BbvCI");
        enzymeMap.put("CTTAAG", "DLE1");

    }

    /**
     * Sets enzymes sites density values
     * 
     * @param siteDensities hashmap of sites density, the value is the density score, the key is the enzyme 
     */
    public void setData(HashMap<String, Double> siteDensities) {
        drawChart = true;
        this.siteDensities = siteDensities;

        List<Integer> xData = new ArrayList<Integer>();
        List<Double> yData = new ArrayList<Double>();
        HashMap<Double, String> enzymes = new HashMap();
        Color[] colourArray = new Color[siteDensities.size()]; //new paint array
        Double highestSize = 0.0;

        Double count = 1.0;
        int idx = 0;
        for (HashMap.Entry<String, Double> entry : siteDensities.entrySet()) {
            colourArray[idx] = new Color(0, 172, 178); //Colour all bars intially blue
            xData.add(count.intValue());
            yData.add(entry.getValue());
            enzymes.put(count, entry.getKey());
            if (entry.getValue() > highestSize) {
                highestSize = entry.getValue();
                bestEnzIndx = idx;
                bestEnz = entry.getKey();
                count++;
                idx++;
            }
        }
        colourArray[bestEnzIndx] = new Color(239, 70, 55); //Set the column containing n50 to red
        // Customize Chart
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setLegendBorderColor(Color.lightGray);

        Function<Double, String> function = x -> enzymes.get(x);
        chart.setCustomXAxisTickLabelsFormatter(function);
        // Series
        chart.addSeries("Enzyme", xData, yData);
        repaint();
    }

    /**
     * Repaints this panel
     * 
     * @param g graphical device
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (drawChart) {
            Graphics2D g2d = (Graphics2D) g;
            chart.paint(g2d, this.getWidth(), this.getHeight());
        }
    }

    /**
     * Gets best enzyme
     * 
     * @return enzyme with highest density score
     */
    public String getBestEnz() {
        return this.bestEnz;
    }

    /**
    * Sets this plots style
    * 
    * @param style plot style
    */
    public void setStyle(ChartTheme style) {
        this.style = style;
        repaint();
    }

    /**
     * Gets this plots style
     * @return style
     */
    public ChartTheme getStyle() {
        return this.style;
    }
}
