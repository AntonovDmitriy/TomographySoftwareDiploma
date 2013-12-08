/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.imageprocessing;

import static com.antonov.tomographysoftwarediploma.ImageTransformator.create12bitImage;
import com.antonov.tomographysoftwarediploma.Utils;
import static com.antonov.tomographysoftwarediploma.imageprocessing.ImageTransformerFacade.PerformWindowing;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class SinogramCreator extends ModellingImageCalculator {

    public static final int REGIME_NEAREST_NEIGHBOUR_ITERPOLATION = 1;
    public static final int REGIME_LINEAR_ITERPOLATION = 2;

    private static final Logger logger = LoggerFactory.getLogger(SinogramCreator.class);

    private double[][] generateProjectionData(double[][] pixInitialImage, int regime) {

        double[][] projectionData = new double[rotates][scans];
        Utils.fillZeroMatrix(projectionData);
        double scaleImageToSinogramRatio = calculateImageToSinogramScaleRatio(pixInitialImage, this.scans);

        double[] minusCosTab = Utils.getRowOfFunctionIncrementalValues("-cos", START_ROTATION_ANGLE, FINISH_ROTATION_ANGLE, this.stepSize);
        double[] sinTab = Utils.getRowOfFunctionIncrementalValues("sin", START_ROTATION_ANGLE, FINISH_ROTATION_ANGLE, this.stepSize);

        int rotate = 0;
        int heightInitialImage = pixInitialImage[0].length;
        int Xcenter = heightInitialImage / 2;
        int Ycenter = heightInitialImage / 2;

        for (int angle = START_ROTATION_ANGLE; angle < FINISH_ROTATION_ANGLE; angle = (int) (angle + stepSize)) {

            double a = -sinTab[rotate] / minusCosTab[rotate];
            double aa = 1 / a;
            if (isAngleInFirstOrFourthSection(rotate, minusCosTab)) {
                for (int scan = 0; scan < scans; scan++) {
                    double valueOfGray = 0;
                    int scanInGrid;
                    scanInGrid = scan - scans / 2;
                    double b = (scanInGrid - sinTab[rotate] - minusCosTab[rotate]) / minusCosTab[rotate];
                    b = b * scaleImageToSinogramRatio;

                    for (int x = -Xcenter; x < Xcenter; x++) {
                        if (regime == REGIME_NEAREST_NEIGHBOUR_ITERPOLATION) {
                            valueOfGray = inteprolationNearestNeighbour(a, x, b, Xcenter, Ycenter, pixInitialImage, valueOfGray);
                        } else if (regime == REGIME_LINEAR_ITERPOLATION) {
                            valueOfGray = inteprolationLinear(a, x, b, Xcenter, Ycenter, pixInitialImage, valueOfGray);
                        }
                    }
                    projectionData[rotate][scan] = valueOfGray / Math.abs(minusCosTab[rotate]);
                }
            } else {
                for (int scan = 0; scan < scans; scan++) {
                    double valueOfGray = 0;
                    int scanInGrid;
                    scanInGrid = scan - scans / 2;
                    double b = (scanInGrid - sinTab[rotate] - minusCosTab[rotate]) / sinTab[rotate];
                    b = b * scaleImageToSinogramRatio;
                    for (int y = -Ycenter; y < Ycenter; y++) {
                        if (regime == REGIME_NEAREST_NEIGHBOUR_ITERPOLATION) {
                            valueOfGray = inteprolationNearestNeighbour(aa, y, b, Xcenter, Ycenter, pixInitialImage, valueOfGray);
                        } else if (regime == REGIME_LINEAR_ITERPOLATION) {
                            valueOfGray = inteprolationLinear(aa, y, b, Xcenter, Ycenter, pixInitialImage, valueOfGray);
                        }
                    }
                    projectionData[rotate][scan] = valueOfGray / Math.abs(sinTab[rotate]);
                }
            }
            rotate++;
        }
        return projectionData;
    }

    private double inteprolationNearestNeighbour(double a, int x, double b, int Xcenter, int Ycenter, double[][] pixInitialImage, double val) {

        int y = (int) Math.round(a * x + b);
        if (y >= -Xcenter && y < Xcenter) {
            val += pixInitialImage[(y + Ycenter)][(x + Xcenter)];
        }
        return val;
    }

    private double inteprolationLinear(double a, int x, double b, int Xcenter, int Ycenter, double[][] pixInitialImage, double val) {
        int y = (int) Math.round(a * x + b);
        double weight = Math.abs((a * x + b)
                - Math.ceil(a * x + b));

        if (y >= -Xcenter && y + 1 < Xcenter) {
            val += (1 - weight)
                    * pixInitialImage[(y + Ycenter)][(x + Xcenter)]
                    + weight
                    * pixInitialImage[(y + Ycenter)][(x + Xcenter)];
        }
        return val;
    }

    boolean isAngleInFirstOrFourthSection(int view, double[] minusCosTab) {
        double cosOf45Degrees = Math.sqrt(2) / 2;
        return Math.abs(minusCosTab[view]) > cosOf45Degrees;
    }

    private double calculateImageToSinogramScaleRatio(double[][] imagePixArray, int scans) {

        int heightInitialImage = imagePixArray[0].length;
        double result = heightInitialImage * Math.sqrt(2) / scans;
        return result;
    }

    public BufferedImage createSinogram() {
        BufferedImage sinogram;

        double[][] pixInitialImage = Utils.getDoubleRevertedArrayPixelsFromBufImg(sourceImage);
        logger.trace("Array of pixel values of source image has been created");
        double[][] projectionData = generateProjectionData(pixInitialImage, REGIME_NEAREST_NEIGHBOUR_ITERPOLATION);
        logger.trace("Projection data has been created");
        projectionData = Utils.normalize2DArray(projectionData, 0, 1);
        short[] pixelshortArray = Utils.getShortRowFromProjectionData(projectionData);
        logger.trace("Short row projection data has been created");
        sinogram = Utils.create12bitImageFromShortProjectionData(projectionData[0].length, projectionData.length,
                pixelshortArray);
        logger.trace("Sinogram image is created");
        sinogram = PerformWindowing(sinogram);
        logger.trace("Sinogram inage has been performed for screaning");
        return sinogram;
    }
}
