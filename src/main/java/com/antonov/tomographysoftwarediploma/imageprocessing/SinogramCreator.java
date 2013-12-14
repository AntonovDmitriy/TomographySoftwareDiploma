/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.imageprocessing;

import com.antonov.tomographysoftwarediploma.Utils;
import static com.antonov.tomographysoftwarediploma.imageprocessing.ImageTransformerFacade.PerformWindowing;
import java.awt.image.BufferedImage;
import java.util.MissingFormatArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class SinogramCreator extends ModellingImageCalculator {

    private static final Logger logger = LoggerFactory.getLogger(SinogramCreator.class);

    // I decided to remain this long method for better understanding
    private double[][] generateProjectionData(double[][] pixInitialImage, String regime) {

        double[][] projectionData = new double[rotates][scans];
        Utils.fillZeroMatrix(projectionData);
        
        double scaleImageToSinogramRatio = calculateImageScaleRatio(pixInitialImage, this.scans);

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
                        switch (regime) {
                            case REGIME_NEAREST_NEIGHBOUR_INTERPOLATION:
                                valueOfGray = inteprolationNearestNeighbour(a, x, b, Xcenter, Ycenter, pixInitialImage, valueOfGray, true);

                                break;
                            case REGIME_LINEAR_ITERPOLATION:
                                valueOfGray = inteprolationLinear(a, x, b, Xcenter, Ycenter, pixInitialImage, valueOfGray, true);
                                break;
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
                        switch (regime) {
                            case REGIME_NEAREST_NEIGHBOUR_INTERPOLATION:
                                valueOfGray = inteprolationNearestNeighbour(aa, y, b, Xcenter, Ycenter, pixInitialImage, valueOfGray, false);
                                break;
                            case REGIME_LINEAR_ITERPOLATION:
                                valueOfGray = inteprolationLinear(aa, y, b, Xcenter, Ycenter, pixInitialImage, valueOfGray, false);
                                break;
                        }
                    }
                    projectionData[rotate][scan] = valueOfGray / Math.abs(sinTab[rotate]);
                }
            }
            rotate++;
        }

        return projectionData;
    }

    private double inteprolationNearestNeighbour(double a, int x, double b, int Xcenter, int Ycenter, double[][] pixInitialImage, double val, boolean isAngleInFirstOrFourthSection) {

        int y = (int) Math.round(a * x + b);
        if (y >= -Xcenter && y < Xcenter) {
            if (isAngleInFirstOrFourthSection) {
                val += pixInitialImage[(y + Ycenter)][(x + Xcenter)];
            } else {
                val += pixInitialImage[(x + Ycenter)][(y + Xcenter)];
            }
        }
        return val;
    }

    private double inteprolationLinear(double a, int x, double b, int Xcenter, int Ycenter, double[][] pixInitialImage, double val, boolean isAngleInFirstOrFourthSection) {
        int y = (int) Math.round(a * x + b);
        double weight = Math.abs((a * x + b)
                - Math.ceil(a * x + b));

        if (y >= -Xcenter && y + 1 < Xcenter) {
            if(isAngleInFirstOrFourthSection)
            val += (1 - weight)
                    * pixInitialImage[(y + Ycenter)][(x + Xcenter)]
                    + weight
                    * pixInitialImage[(y + Ycenter)][(x + Xcenter)];
            else{
                            val += (1 - weight)
                    * pixInitialImage[(x + Ycenter)][(y + Xcenter)]
                    + weight
                    * pixInitialImage[(x + Ycenter)][(y + Xcenter)];
            }
        }
        return val;
    }

    boolean isAngleInFirstOrFourthSection(int view, double[] minusCosTab) {
        double cosOf45Degrees = Math.sqrt(2) / 2;
        return Math.abs(minusCosTab[view]) > cosOf45Degrees;
    }


    public BufferedImage createSinogram(IProjDataSaver model, BufferedImage sourceImage, String regimeInteprolation) throws NumberWrongValueException, ImageWrongValueException {
        super.setInitialImage(sourceImage);
        if (sourceImage != null && scans != 0 && stepSize != 0) {
            BufferedImage sinogram;
            checkRegimeInterpolation(regimeInteprolation);

            double[][] pixInitialImage = Utils.getDoubleRevertedArrayPixelsFromBufImg(sourceImage);
            logger.trace("Array of pixel values of source image has been created");
            double[][] projectionData = generateProjectionData(pixInitialImage, regimeInteprolation);

            logger.trace("Projection data has been created and saved to model");
            projectionData = Utils.normalize2DArray(projectionData, 0, 1);

            model.setProjectionData(projectionData);
            short[] pixelshortArray = Utils.getShortRowFromProjectionData(projectionData);
            logger.trace("Short row projection data has been created");

            sinogram = Utils.create12bitImageFromShortProjectionData(projectionData[0].length, projectionData.length,
                    pixelshortArray);
            logger.trace("Sinogram image is created");
            sinogram = PerformWindowing(sinogram);
            logger.trace("Sinogram inage has been performed for screaning");

            return sinogram;
        } else {
            throw new MissingFormatArgumentException("Parameters of modelling are emptry or incorrect");
        }
    }

    private void checkRegimeInterpolation(String regimeInteprolation) throws NumberWrongValueException {
        if (regimeInteprolation == null || (!regimeInteprolation.equals(REGIME_LINEAR_ITERPOLATION)
                && !regimeInteprolation.equals(REGIME_NEAREST_NEIGHBOUR_INTERPOLATION))) {
            logger.error("Error value of regimeInteprolation " + regimeInteprolation + " ");
            throw new NumberWrongValueException("Error value regimeInteprolation " + regimeInteprolation + " ");
        }
    }
}
