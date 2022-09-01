
package ComparativeGenomics.Panels;

import ComparativeGenomics.FileHandling.DataHandling.Chromosome;
import ComparativeGenomics.FileHandling.DataHandling.Genome;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JPanel;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Styler.ChartTheme;

/**
 *
 * @author franpeters
 */
public class ChromosomeChartPanel extends JPanel{
    boolean drawChart = false;
    HashMap<String,Double> siteDensities;
    ChartTheme style = ChartTheme.GGPlot2;
    Genome genome;
    // Create Chart
    CategoryChart chart = new CategoryChartBuilder()
            .width(this.getWidth()).height(this.getHeight())
            .title("Structural Variants Detected By Chromosome").xAxisTitle("Chromosome").yAxisTitle("SV Count")
            .theme(this.style).build();
    
    public ChromosomeChartPanel(){
        
        
    }
    
    public void plotGenome(Genome genome){
        drawChart=true;
        this.genome=genome;
        ArrayList<Number> numIndels = new ArrayList();
        ArrayList<Number> numTrans = new ArrayList();
        ArrayList<Number> numDups = new ArrayList();
        ArrayList<Number> numInvs = new ArrayList();
        ArrayList<String> chrNames = new ArrayList();
        for (Chromosome chr: genome.getChromosomes().values()){
            numIndels.add(chr.getNumIndels());
            numTrans.add(chr.getNumTranslocations());
            numDups.add(chr.getNumDuplications());
            numInvs.add(chr.getNumInversions());
            chrNames.add(chr.getName());
        }
        chart.addSeries("Indels", chrNames, numIndels);
        chart.addSeries("Translocations", chrNames, numTrans);
        chart.addSeries("Duplications", chrNames, numDups);
        chart.addSeries("Inversions", chrNames, numInvs);
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
    public void setStyle(ChartTheme style){
        this.style=style;
        repaint();
    }
}
