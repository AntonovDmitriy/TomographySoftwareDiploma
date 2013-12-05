/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.imageprocessing;

import static com.antonov.tomographysoftwarediploma.ImageTransformator.create12bitImage;
import com.antonov.tomographysoftwarediploma.Utils;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class SinogramCreator extends ModellingImageCalculator {

    private static Logger logger = LoggerFactory.getLogger(SinogramCreator.class);

    private double[][] generateProjectionData(double[][] pixInitialImage) {

        double val;
        int x, y,  S = 0;
        int N = 0;
        val = 0;
        double weight = 0;
        double sang = Math.sqrt(2) / 2;
        boolean fast = false;


        double[][] projectionData = new double[views][scans];
        Utils.fillZeroMatrix(projectionData);
        double scaleImageToSinogramRatio = calculateImageToSinogramScaleRatio(pixInitialImage, this.scans);       
        
        double[] sintab = Utils.getRowOfFunctionIncrementalValues("sin", START_ROTATION_ANGLE, FINISH_ROTATION_ANGLE, this.stepSize);
        double[] costab = Utils.getRowOfFunctionIncrementalValues("cos", START_ROTATION_ANGLE, FINISH_ROTATION_ANGLE, this.stepSize);

        int view = 0;
        int heightInitialImage = pixInitialImage[0].length;
        int Xcenter = heightInitialImage / 2;
        int Ycenter = heightInitialImage / 2;
        for (int angle = START_ROTATION_ANGLE; angle < FINISH_ROTATION_ANGLE; angle = (int) (angle + stepSize)) {

            double a = -costab[view] / sintab[view];
            double aa = 1 / a;
            if (Math.abs(sintab[view]) > sang) {
                for (S = 0; S < scans; S++) {
                    N = S - scans / 2;
                    double b = (N - costab[view] - sintab[view]) / sintab[view];
                    b = b * scaleImageToSinogramRatio;

                    for (x = -Xcenter; x < Xcenter; x++) {
                        if (fast == true) {
                            // just use nearest neighbour interpolation
                            y = (int) Math.round(a * x + b);
                            if (y >= -Xcenter && y < Xcenter) {
                                val += pixInitialImage[(y + Ycenter)][(x + Xcenter)];
                            }

                        } else {
                            // linear interpolation
                            y = (int) Math.round(a * x + b);
                            weight = Math.abs((a * x + b)
                                    - Math.ceil(a * x + b));

                            if (y >= -Xcenter && y + 1 < Xcenter) {
                                val += (1 - weight)
                                        * pixInitialImage[(y + Ycenter)][(x + Xcenter)]
                                        + weight
                                        * pixInitialImage[(y + Ycenter)][(x + Xcenter)];
                            }
                        }
                    }
                    projectionData[view][S] = val / Math.abs(sintab[view]);
                    val = 0;

                }
            } else if (Math.abs(sintab[view]) <= sang) {
                for (S = 0; S < scans; S++) {
                    N = S - scans / 2;
                    double bb = (N - costab[view] - sintab[view]) / costab[view];
                    bb = bb * scaleImageToSinogramRatio;
                    for (y = -Ycenter; y < Ycenter; y++) {
                        if (fast == true) {
                            x = (int) Math.round(aa * y + bb);
                            if (x >= -Xcenter && x < Xcenter) {
                                val += pixInitialImage[(y + Ycenter)][(x + Xcenter)];
                            }
                        } else {

                            x = (int) Math.round(aa * y + bb);
                            weight = Math.abs((aa * y + bb)
                                    - Math.ceil(aa * y + bb));

                            if (x >= -Xcenter && x + 1 < Xcenter) {
                                val += (1 - weight)
                                        * pixInitialImage[(y + Ycenter)][(x + Xcenter)]
                                        + weight
                                        * pixInitialImage[(y + Ycenter)][(x + Xcenter)];
                            }
                        }
                    }
                    projectionData[view][S] = val / Math.abs(costab[view]);
                    val = 0;

                }
            }
            view++;
        }
        return projectionData;
    }

    private double calculateImageToSinogramScaleRatio(double[][] imagePixArray, int scans) {

        int heightInitialImage = imagePixArray[0].length;
        double result = heightInitialImage * Math.sqrt(2) / scans;
        return result;
    }

    public BufferedImage createSinogram() {
        BufferedImage sinogram;
        BufferedImage sinogramImage;

        int gray;

        double[][] pixInitialImage = Utils.getDoubleRevertedArrayPixelsFromBufImg(sourceImage);
        logger.trace("Array of pixel values of source image has been created");
        double[][] projectionData = generateProjectionData(pixInitialImage);

        projectionData = Utils.normalize2DArray(projectionData, 0, 1);

        double min = Utils.getMin(projectionData);

        double max = Utils.getMax(projectionData);

        short[] pixelshortArray = new short[projectionData.length
                * projectionData[0].length];

        for (int x = 0; x < projectionData.length; x++) {
            for (int y = 0; y < projectionData[0].length; y++) {
                // rescale pixel values for 12-bit grayscale image??
                if (max > min) {
                    gray = (int) ((projectionData[x][y]) * 2000 / (max));
                } else {
                    gray = (int) ((projectionData[x][y] - min) * 2000 / (max));
                }
                pixelshortArray[y + x * projectionData[0].length] = (short) gray;

            }
        }

        sinogram = create12bitImage(projectionData[0].length, projectionData.length,
                pixelshortArray);

        sinogramImage = PerformWindowing(sinogram);

        return sinogramImage;
    }

    private static BufferedImage PerformWindowing(BufferedImage mBufferedImage) {

        int[][] pixels = Utils.getIntArrayPixelsFromBufImg(mBufferedImage);
        int iw = mBufferedImage.getWidth();
        int ih = mBufferedImage.getHeight();
        BufferedImage windowedImage;

        int upperwinlvl;
        int lowerwinlvl = 0;

        if (mBufferedImage.getType() == 11) {
            upperwinlvl = 2000;
        } else {
            upperwinlvl = 255;
        }
        int winwidth = upperwinlvl - lowerwinlvl;

        if ((mBufferedImage.getType() == 11)
                || (mBufferedImage.getType() == 10)) {
            windowedImage = new BufferedImage(iw, ih,
                    BufferedImage.TYPE_BYTE_GRAY);

            WritableRaster wraster = windowedImage.getRaster();
            for (int x = 0; x < iw; x++) {
                for (int y = 0; y < ih; y++) {
                    // int val = wraster.getSample(x, y, 0);
                    int val = pixels[x][y];
                    if (val <= lowerwinlvl) {
                        wraster.setSample(x, y, 0, 0);
                    } else if (val >= upperwinlvl) {
                        wraster.setSample(x, y, 0, 255);
                    } else {
                        int newval = (val - lowerwinlvl) * 256 / winwidth;
                        wraster.setSample(x, y, 0, newval);
                    }
                }
            }
        } else {
            windowedImage = new BufferedImage(iw, ih,
                    BufferedImage.TYPE_INT_RGB);
            windowedImage.createGraphics()
                    .drawImage(mBufferedImage, 0, 0, null);

        }
        return windowedImage;
    }
}
