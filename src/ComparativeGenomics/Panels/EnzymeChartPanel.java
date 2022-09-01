
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
 *
 * @author franpeters
 */
public class EnzymeChartPanel extends JPanel {

HashMap<String,Double> siteDensities;
Integer bestEnzIndx;
String bestEnz;

HashMap<String,String> enzymeMap = new HashMap();
boolean drawChart = false;
 ChartTheme style = ChartTheme.GGPlot2;
    // Create Chart
    CategoryChart chart =
        new CategoryChartBuilder()
            .width(this.getWidth()).height(this.getHeight())
            .title("Comparing Enzymes for in Silico Digestion").xAxisTitle("Enzyme").yAxisTitle("Sites / 100kb")
            .theme(style).build();
public EnzymeChartPanel(){
    enzymeMap.put("GCTCTTC", "BspQI");
    enzymeMap.put("CCTCAGC", "BbvCI");
    enzymeMap.put("ATCGAT", "bseCI");
    enzymeMap.put("CACGAG", "BssSI");
    enzymeMap.put("GCAATG", "BsrDI");
    enzymeMap.put("CCTCAGC", "BbvCI");
    enzymeMap.put("CTTAAG", "DLE1");

}
public void setData(HashMap<String,Double> siteDensities){
    drawChart = true;
    this.siteDensities = siteDensities;

    List<Integer> xData = new ArrayList<Integer>();
    List<Double> yData = new ArrayList<Double>();
    HashMap<Double,String> enzymes = new HashMap();
    Color[] colourArray = new Color[siteDensities.size()]; //new paint array
    Double highestSize=0.0;
    
    Double count = 1.0;
    int idx = 0;
    for (HashMap.Entry<String, Double> entry : siteDensities.entrySet()) {
       colourArray[idx] = new Color(0, 172, 178); //Colour all bars intially blue
       xData.add(count.intValue());
       yData.add(entry.getValue());
       enzymes.put(count, entry.getKey());
       if(entry.getValue()>highestSize){
            highestSize=entry.getValue();
            bestEnzIndx=idx;
            bestEnz=entry.getKey();
//        }
       count++;
       idx++;
    }
    }
    colourArray[bestEnzIndx] = new Color(239, 70, 55); //Set the column containing n50 to red
    // Customize Chart
    chart.getStyler().setChartTitleVisible(true);
    chart.getStyler().setLegendVisible(false);
//    chart.getStyler().setSeriesColors(colourArray);
//    chart.getStyler().setXAxisLabelRotation(45);
    chart.getStyler().setLegendVisible(true);
    chart.getStyler().setLegendBorderColor(Color.lightGray);


    Function<Double,String> function = x -> enzymes.get(x);
    chart.setCustomXAxisTickLabelsFormatter(function);
    // Series
    chart.addSeries("Enzyme", xData, yData);
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
    public String getBestEnz(){
        return this.bestEnz;
    }
    public void setStyle(ChartTheme style){
        this.style=style;
        repaint();
    }
    public ChartTheme getStyle(){
        return this.style;
    }
}
    
//   public class EnzymeChartPanel extends JPanel {
//
//HashMap<String,Double> siteDensities;
//Integer bestEnzIndx;
//
//List<Double> bestEnzArrYData = new ArrayList<Double>();
//List<Integer> xDataBesEnz = new ArrayList<Integer>();
//List<Double> enzAbove25YData = new ArrayList<Double>();
//List<Integer> xDataAbove25 = new ArrayList<Integer>();
//List<Double> enzBelow25YData = new ArrayList<Double>();
//List<Integer> xDataBelow25 = new ArrayList<Integer>();
//String bestEnz;
//
//HashMap<String,String> enzymeMap = new HashMap();
//boolean drawChart = false;
//    // Create Chart
//    CategoryChart chart =
//        new CategoryChartBuilder()
//            .width(this.getWidth()).height(this.getHeight())
//            .title("Comparing Enzymes for in Silico Digestion").xAxisTitle("Enzyme").yAxisTitle("Sites / 100kb")
//            .theme(ChartTheme.GGPlot2).build();
//public EnzymeChartPanel(){
//    enzymeMap.put("GCTCTTC", "BspQI");
//    enzymeMap.put("CCTCAGC", "BbvCI");
//    enzymeMap.put("ATCGAT", "bseCI");
//    enzymeMap.put("CACGAG", "BssSI");
//    enzymeMap.put("GCAATG", "BsrDI");
//    enzymeMap.put("CCTCAGC", "BbvCI");
//    enzymeMap.put("CTTAAG", "DLE1");
//
//}
//public void setData(HashMap<String,Double> siteDensities){
//    drawChart = true;
//    this.siteDensities = siteDensities;
//
//    
//    HashMap<Double,String> enzymes = new HashMap();
//   
//    Double highestSize=0.0;
//    Double count = 1.0;
//    int idx = 0;
//    for (HashMap.Entry<String, Double> entry : siteDensities.entrySet()) {
//       int x = count.intValue();
//       double res = entry.getValue();
//       if (res>highestSize){
//            bestEnzArrYData.clear();
//            xDataBesEnz.clear();
//            
//            bestEnzArrYData.add(res);
//            xDataBesEnz.add(x);
//            
//            highestSize=res;
//            bestEnzIndx=idx;
//            bestEnz=entry.getKey();
//       }
//       if (res>=25.0){
//           enzAbove25YData.add(res);
//           xDataAbove25.add(x);
//       }else{
//           enzBelow25YData.add(res);
//           xDataBelow25.add(x);
//       }
//       enzymes.put(count, entry.getKey());
//       count++;
//       idx++;
//    }
//    
//    
//    // Customize Chart
//    chart.getStyler().setChartTitleVisible(true);
//    chart.getStyler().setLegendVisible(false);
//  
//    chart.getStyler().setXAxisLabelRotation(45);
//    chart.getStyler().setLegendVisible(true);
//    chart.getStyler().setLegendBorderColor(Color.lightGray);
////    Function<Double,String> function = x -> enzymes.get(x);
////    chart.setCustomXAxisTickLabelsFormatter(function);
//    // Series
////    chart.addSeries("Best Enzyme", this.xDataBesEnz, this.bestEnzArrYData);
//    chart.addSeries("Sites > 25 /100kb",this.xDataAbove25, this.enzAbove25YData);
////    chart.addSeries("Sites < 25 /100kb",this.xDataBelow25, this.enzBelow25YData);
//    repaint();
//}
// @Override
//    public void paint(Graphics g) {
//        super.paint(g);
//        if(drawChart){
//        Graphics2D g2d = (Graphics2D) g;
//        chart.paint(g2d,this.getWidth(),this.getHeight());
//        }
//    }
//    public String getBestEnz(){
//        return this.bestEnz;
//    }
//}
