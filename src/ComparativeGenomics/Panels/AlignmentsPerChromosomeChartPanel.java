/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComparativeGenomics.Panels;

import ComparativeGenomics.FileHandling.DataHandling.Chromosome;
import ComparativeGenomics.FileHandling.DataHandling.Genome;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JPanel;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.Styler;

/**
 *
 * @author franpeters
 * Plot the number of alignments per each chromosome within the reference genome using XChart.
 * The chart style can be changed by the user.
 */
public class AlignmentsPerChromosomeChartPanel extends JPanel {
 boolean drawChart = false;
    HashMap<String,Double> siteDensities;
    Styler.ChartTheme style = Styler.ChartTheme.GGPlot2;
    Genome genome;
    // Create Chart
    CategoryChart chart = new CategoryChartBuilder()
            .width(this.getWidth()).height(this.getHeight())
            .title("Number of Alignments per Chromosome").xAxisTitle("Chromosome").yAxisTitle("Alignment Count")
            .theme(this.style).build();
    
    public AlignmentsPerChromosomeChartPanel(){
        
        
    }
    
    public void plotGenome(Genome genome){
        drawChart=true;
//        this.genome=genome;
        ArrayList<Number> numAlignments = new ArrayList();
        ArrayList<String> chrNames = new ArrayList();
        for (Chromosome chr: genome.getChromosomes().values()){
            numAlignments.add(chr.getAlignments().size());
            chrNames.add(chr.getName());
            System.out.println(chr.getName());
            System.out.println(chr.getAlignments().size());
        }
        chart.addSeries("Alignments", chrNames, numAlignments);
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
