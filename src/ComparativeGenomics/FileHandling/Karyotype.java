package ComparativeGenomics.FileHandling;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 * To parse the karyotype file generated from the reference genome produced by
 * the run_job.sh script using Samtools faidx into a space separated file with
 * the first column being the chromosome. Size and the second column being the
 * chromosome name, which has been extracted from the fasta file headers (see
 * 3.1.4 for more information on the Fasta file).
 *
 * @author franpeters
 */
public class Karyotype {

    private Integer numChrs;
    private ArrayList<Double> chrSizes = new ArrayList();
    private ArrayList<String> chrNames = new ArrayList();
    private Double sizeGenome = 0.0;
    private String filename;
    private HashMap<Double, String> info = new HashMap();
    private ArrayList<Double> relativeStarts = new ArrayList();
    private ArrayList<Double> relativeSizes = new ArrayList();

    public Karyotype(String filepath) {
        readKaryotypeFile(filepath);
    }

    /**
     *
     * @return number of chromosomes
     */
    public Integer getNumChrs() {
        return this.numChrs;
    }

    /**
     *
     * @return the size of each chromosome
     */
    public ArrayList<Double> getChrSizes() {
        return this.chrSizes;
    }

    /**
     *
     * @return return the name of each chromosome
     */
    public ArrayList<String> getChrNames() {
        return this.chrNames;
    }

    /**
     *
     * @return the HashMap of the chromosome sizes, names
     */
    public HashMap<Double, String> getInfo() {
        return this.info;
    }

    /**
     *
     * @return the relative size of each chromosome
     */
    public ArrayList<Double> getChrRelativeStarts() {
        return this.relativeStarts;
    }

    /**
     *
     * @return return the sizes of the chromosomes for drawing the circos plot
     */
    public ArrayList<Double> getChrSizesCircos() {
        ArrayList<Double> list = new ArrayList();
        for (int i = 0; i < chrSizes.size(); i++) {
            Double size = ((chrSizes.get(i) / 1000)) / sizeGenome;
            list.add(size);
        }
        return list;
    }

    /**
     *
     * @param g2d Graphics2D device to use
     * @param w width of JPanel
     * @param h height of JPanel
     */
    public void drawChromosomes(Graphics2D g2d, Integer w, Integer h) {

        Font labelFont = new Font("Arial", 1, 10);
        Font chrFont = new Font("Arial", 1, 8);
        Double sizeCounter = 0.0;
        //get size of drawing area
        w -= 10;
        Integer startX = 5;
        Integer startY = 70;
        Integer height = h / 5;
        Double lastStart = Double.valueOf(startX);
        relativeStarts.add(lastStart);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(startX, startY, w, height);
        g2d.setColor(Color.BLACK);
        g2d.drawLine(startX, (startY + height + 5), w, (startY + height + 5));
        g2d.setFont(chrFont);
        g2d.drawString(String.valueOf("0"), lastStart.intValue(), (startY + height + 30));
        Integer x;
        for (x = 0; x < numChrs; x++) {
//            System.out.println(lastStart+" start positions");
            String chr = String.valueOf(x + 1);
            if (x < 22) {
                chr = String.valueOf(x + 1);
            }
            if (x == 22) {
                chr = "X";
            }
            if (x == 23) {
                chr = "Y";
            }
            Double rawSize = chrSizes.get(x);
            Double length = (Double.valueOf(rawSize) / Double.valueOf(1000)) / Double.valueOf(sizeGenome);
            Double draw = w * length;
            relativeSizes.add(draw);
            Double drawMiddle = lastStart + (draw / 2);
            Rectangle2D.Double rect = new Rectangle2D.Double(lastStart, startY, draw, height);
            relativeStarts.add(lastStart);
            g2d.draw(rect);
//            Draw the scale bars
            Line2D.Double line = new Line2D.Double(lastStart, (startY + height + 5), lastStart, (startY + height + 20));
            g2d.draw(line);
//            Draw the scale numbers
            g2d.setFont(labelFont);
            drawRotate(g2d, drawMiddle, (startY - 5), 315, "Chr " + chr);
            lastStart += draw;
            sizeCounter += (chrSizes.get(x) / 1000) / 1000;
            g2d.setFont(chrFont);
            drawRotate(g2d, lastStart.floatValue(), (startY + height + 35), 315, String.format("%.0f", sizeCounter));
        }

    }

    /**
     * o	read the karyotype file using scanner to parse each line in the
     * Karyotype file in a memory efficient manner. Each line is split and the
     * data is saved into ArrayLists and a HashMap. The genome size is also
     * calculated as the file is parsed.
     *
     * @param filepath of the file to be parsed
     */
    private void readKaryotypeFile(String filepath) {

        String[] directory = filepath.split("/");
        this.filename = directory[directory.length - 1];
        String karyPattern = ".txt";
        Pattern k = Pattern.compile(karyPattern);
        Matcher checkFileTypeKary = k.matcher(this.filename);
        if (checkFileTypeKary.find() != true) {
            JOptionPane.showMessageDialog(null, "Karyotype File type must be .txt\nPlease choose a different file!",
                    "Karyotype file error", JOptionPane.ERROR_MESSAGE);
        }
        File file = new File(filepath); //Make a new fileobject for the user selected file
        try {
            Scanner scan = new Scanner(file);
            int count = 1;
            while (scan.hasNextLine()) {
                String row = scan.nextLine();
                String[] data = row.split(" ");
                Double chrSize = Double.valueOf(data[0]);
                String chrName = data[1];
                this.chrSizes.add(chrSize);
                this.chrNames.add(chrName);
                info.put(chrSize, chrName);
                this.sizeGenome += (chrSize / 1000);
                count += 1;
//                    }
            }
            this.numChrs = chrSizes.size();

        } catch (FileNotFoundException ex) {

        }
        this.getChrSizesCircos();
    }

    /**
     * Method to draw a Graphcis2D object at a given angle. Source:
     * https://stackoverflow.com/questions/10083913/how-to-rotate-text-with-graphics2d-in-java
     *
     * @param g2d
     * @param x
     * @param y
     * @param angle
     * @param text
     */
    private static void drawRotate(Graphics2D g2d, double x, double y, int angle, String text) {
        g2d.translate((float) x, (float) y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(text, 0, 0);
        g2d.rotate(-Math.toRadians(angle));
        g2d.translate(-(float) x, -(float) y);
    }

}
