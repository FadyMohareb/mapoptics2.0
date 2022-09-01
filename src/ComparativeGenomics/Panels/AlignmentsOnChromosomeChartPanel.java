/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComparativeGenomics.Panels;

import ComparativeGenomics.FileHandling.DataHandling.Chromosome;
import ComparativeGenomics.FileHandling.DataHandling.Genome;
import ComparativeGenomics.FileHandling.DataHandling.XmapData;
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
 * @author franpeters
 */
public class AlignmentsOnChromosomeChartPanel extends JPanel {
 boolean drawChart = false;
    HashMap<String,Double> siteDensities;
    Styler.ChartTheme style = Styler.ChartTheme.GGPlot2;
    Genome genome;
    // Create Chart
    CategoryChart chart = new CategoryChartBuilder()
            .width(this.getWidth()).height(this.getHeight())
            .title("Number of Alignments per Cmap ID to Chromosome").xAxisTitle("Chromosome").yAxisTitle("Alignment Count")
            .theme(this.style).build();
    
    public AlignmentsOnChromosomeChartPanel(){
        
        
    }
    
    public void plotCounts(Chromosome chr){
        drawChart=true;
//        this.genome=genome;
        ArrayList<Number> qryIDs = new ArrayList();
        ArrayList<Number> counts = new ArrayList();
        HashMap<Integer,Number> qryIDCount = new HashMap();
        for (XmapData map: chr.getAlignments()){
            if (qryIDCount.containsKey(map.getID())){
                Number n = qryIDCount.get(map.getID());
                n = Number.class.cast(n.intValue() + 1) ;
                qryIDCount.replace(map.getID(), n);
            }else{
                qryIDs.add(map.getID());
                qryIDCount.put(map.getID(), 1);
            }
        }
        for (Number num: qryIDCount.values()){
            counts.add(num);
        }
        chart.addSeries("Query Cmap ID", qryIDs, counts);
        chart.getStyler().setXAxisLabelRotation(45);
        chart.getStyler().setChartTitleVisible(true);
        repaint();
    }
@Override
    public void paint(Graphics g) {
        super.paint(g);
        if(drawChart){
        Graphics2D g2d = (Graphics2D) g;
        chart.paint(g2d,this.getWidth(),this.getHeight());
        }
    }
    public void setStyle(Styler.ChartTheme style){
        this.style=style;
        repaint();
    }
}