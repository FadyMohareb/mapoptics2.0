/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComparativeGenomics.Panels;

import ComparativeGenomics.FileHandling.DataHandling.Chromosome;
import ComparativeGenomics.FileHandling.DataHandling.Genome;
import ComparativeGenomics.FileHandling.DataHandling.XmapData;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.JPanel;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.Styler;

/**
 *
 * @author franpeters Plot the number of alignments found per each cmap ID of
 * the query genome accross a chromosome from the reference genome using XChart.
 * The chart style can be changed by the user.
 */
public class AlignmentsOnChromosomeChartPanel extends JPanel {

    private boolean drawChart = false;
    HashMap<String, Double> siteDensities;
    Styler.ChartTheme style = Styler.ChartTheme.GGPlot2;
    Genome genome;
    // Create Chart
    CategoryChart chart = new CategoryChartBuilder()
            .width(this.getWidth()).height(this.getHeight())
            .title("Number of Alignments per Cmap ID to Chromosome").xAxisTitle("Query Cmap ID").yAxisTitle("Alignment Count")
            .theme(this.style).build();

    ArrayList<String> qryIDs = new ArrayList();
    ArrayList<Number> counts = new ArrayList();
    HashMap<Integer, Number> qryIDCount = new HashMap();

    /**
     * Constructor of the class
     */
    public AlignmentsOnChromosomeChartPanel() {
    }

    /**
     * o Plot the counts of each query map ID on a chromosome.
     *
     * @param chr Chromosome to analyse and for which maps are displayed
     */
    public void plotCounts(Chromosome chr) {
        drawChart = true;

        qryIDs = new ArrayList();
        counts = new ArrayList();
        qryIDCount = new HashMap();
        chart = new CategoryChartBuilder()
                .width(this.getWidth()).height(this.getHeight())
                .title("Number of Alignments per Cmap ID to Chromosome").xAxisTitle("Query cmap ID").yAxisTitle("Alignment Count")
                .theme(this.style).build();

        for (XmapData map : chr.getAlignments()) {
            if (qryIDCount.containsKey(map.getID())) {
                // If at least one map was already associated with the considered query,
                // update the counts
                Number n = qryIDCount.get(map.getID());
                n = Number.class.cast(n.intValue() + 1);
                qryIDCount.replace(map.getID(), n);
            } else {
                // Update list of query map IDs and count of maps 
                // associated with that ID in the reference
                qryIDs.add(String.valueOf(map.getID()));
                qryIDCount.put(map.getID(), 1);
            }
        }
        // List the counts of alignment for each query map
        for (Number num : qryIDCount.values()) {
            counts.add(num);
        }
        try {
            chart.addSeries("Query Cmap ID", qryIDs, counts);
            chart.getStyler().setXAxisLabelRotation(45);
            chart.getStyler().setChartTitleVisible(true);
            repaint();
        } catch (Exception e) {
            System.out.println("Error in AlignmentsOnChromosomeChartPanel " + e);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (this.drawChart) {
            Graphics2D g2d = (Graphics2D) g;
            try {
                chart.paint(g2d, this.getWidth(), this.getHeight());
            } catch (Exception e) {

            }
        }
    }

    /**
     * Set style of the plots
     * @param style Style chosen by the user
     */
    public void setStyle(Styler.ChartTheme style) {
        this.style = style;
        repaint();
    }
}
