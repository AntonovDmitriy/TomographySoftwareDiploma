/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.viewSwing;

import com.antonov.tomographysoftwarediploma.impl.imageprocessing.Utils;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author Antonov
 */
public class DensityAnalizator {
    

    
    public static ChartPanel generateDensityGraph(BufferedImage densitySourse, int lineOfSlise) {
        ResourceBundle bundle = ResourceBundle.getBundle(
            "bundle_Rus");
        //      BufferedImage inverseReconstructImage = ImageTransformator.getColorLutImage(densitySourse, LUTFunctions.invGray());
        double[][] densitySourseArray = Utils.getDoubleRevertedArrayPixelsFromBufImg(densitySourse);
        double[][] densityInverseArray = new double[densitySourseArray.length][densitySourseArray.length];
        for (int x = 0; x < densitySourseArray.length; x++) {
            for (int y = 0; y < densitySourseArray.length; y++) {
                densityInverseArray[x][y] = Math.abs(densitySourseArray[x][y] - 255);
            }
        }
        
        double[][] normdensitySourseArray = Utils.normalize2DArray(densityInverseArray, 0, 1);
        DefaultXYDataset ds = new DefaultXYDataset();
        
        double[][] arrayDataSetDensitySource = new double[2][];
        double[] xDensityGraph = new double[densitySourseArray.length];
        double[] yDensityGraph = new double[densitySourseArray.length];
        
        for (int i = 0; i < densitySourseArray.length; i++) {
            yDensityGraph[i] = normdensitySourseArray[i][lineOfSlise];
            xDensityGraph[i] = i;
        }
        arrayDataSetDensitySource[0] = xDensityGraph;
        arrayDataSetDensitySource[1] = yDensityGraph;
        
        ds.addSeries("density1", arrayDataSetDensitySource);
        
        JFreeChart chart
                = ChartFactory.createXYLineChart(bundle.getString("GRAPH_TITLE"),
                        bundle.getString("GRAPH_X_TITLE"),
                        bundle.getString("GRAPH_Y_TITLE"), ds, PlotOrientation.VERTICAL, false, false,
                        false);
        
        ChartPanel cp = new ChartPanel(chart);
        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.yellow);
        chart.setBackgroundPaint(Color.black);
        chart.getXYPlot().setBackgroundPaint(Color.black);
        
        chart.getXYPlot().getDomainAxis().setTickLabelPaint(Color.white);
        chart.getXYPlot().getRangeAxis().setTickLabelPaint(Color.white);
         
        chart.getTitle().setPaint(Color.white);
        
        chart.getXYPlot().setOutlinePaint(Color.white);
        chart.getXYPlot().setRangeMinorGridlinePaint(Color.white);
        cp.setVerticalAxisTrace(false);
        cp.setMouseWheelEnabled(true);
        
        return cp;
    }
    
    public static BufferedImage generateCursorOnImage(BufferedImage initialImage, int line) {
        
        BufferedImage outputImage = initialImage;
        
        Graphics2D g2d = outputImage.createGraphics();
        g2d.setColor(Color.GREEN);
        g2d.drawLine(0, line, outputImage.getWidth(), line);
        // BasicStroke bs = new BasicStroke(2);
        g2d.dispose();
        return outputImage;
    }
    
    public static BufferedImage scaleImage(BufferedImage img, int width, int height,
            Color background) {
        
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        if (imgWidth * height < imgHeight * width) {
            width = imgWidth * height / imgHeight;
        } else {
            height = imgHeight * width / imgWidth;
        }
        BufferedImage newImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImage.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setBackground(background);
            g.clearRect(0, 0, width, height);
            g.drawImage(img, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return newImage;
    }
}
