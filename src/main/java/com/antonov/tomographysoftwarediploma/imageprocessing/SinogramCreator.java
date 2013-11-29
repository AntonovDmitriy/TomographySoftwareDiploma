/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.imageprocessing;

import static com.antonov.tomographysoftwarediploma.ImageTransformator.create12bitImage;
import com.antonov.tomographysoftwarediploma.Utils;
import java.awt.image.BufferedImage;

/**
 *
 * @author Antonov
 */
public class SinogramCreator {

    // ----------Parameters of Scanning-------------------------
    public int scans; // Amount of Detectors or Point of Sourses
    public int stepSize; // Step of rotating in degree
    int views;

    //---------------------Images-------------------------------
    BufferedImage sourceImage;
    BufferedImage sinogram;

    //---------------------Other--------------------------------
    boolean isDataModellingValid = false;
    
    public void setDataModelling(BufferedImage sourceImage, int scans, int stepSize) {
        this.scans = scans;
        this.stepSize = stepSize;
        views = 180 / stepSize;

        this.sourceImage = sourceImage;
    }

    private void simulateProjectionData(double[][] pixInitialImage) {

        int ang1 = 0, ang2 = 180; // start and stop angles for projections

        double val;
        int x, y, Xcenter, Ycenter, S = 0;

        int inputimgsize = pixInitialImage[0].length;
        projection = new double[views][scans];
        double[] sintab = new double[views];
        double[] costab = new double[views];
        // Zero matrix
        for (int i = 0; i < projection.length; i++) {
            for (int j = 0; j < projection[0].length; j++) {
                projection[i][j] = 0.0;
            }
        }

        int i = 0, phi;

        for (phi = ang1; phi < ang2; phi = (int) (phi + stepsize), i++) {
            sintab[i] = Math.sin((double) phi * Math.PI / 180 - Math.PI / 2);
            costab[i] = Math.cos((double) phi * Math.PI / 180 - Math.PI / 2);
        }

        // Project each pixel in the image
        Xcenter = inputimgsize / 2;
        Ycenter = inputimgsize / 2;
        i = 0;

        double scale = inputimgsize * Math.sqrt(2) / scans;

        int N = 0;
        val = 0;
        double weight = 0;
        double sang = Math.sqrt(2) / 2;
        boolean interrupt = false, fast = false;

        for (phi = ang1; phi < ang2; phi = (int) (phi + stepsize)) {
            if (interrupt) {
                break;
            }
            double a = -costab[i] / sintab[i];
            double aa = 1 / a;
            if (Math.abs(sintab[i]) > sang) {
                for (S = 0; S < scans; S++) {
                    N = S - scans / 2; // System.out.print("N="+N+" ");
                    double b = (N - costab[i] - sintab[i]) / sintab[i];
                    b = b * scale;

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
                    projection[i][S] = val / Math.abs(sintab[i]);
                    val = 0;

                }
            } else if (Math.abs(sintab[i]) <= sang) {
                for (S = 0; S < scans; S++) {
                    N = S - scans / 2;
                    double bb = (N - costab[i] - sintab[i]) / costab[i];
                    bb = bb * scale;
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
                    projection[i][S] = val / Math.abs(costab[i]);
                    val = 0;

                }
            }
            i++;
        }
    }

    public BufferedImage createSinogram() {
        BufferedImage sinogram;
        BufferedImage sinogramImage;

        int gray;

        double[][] pixInitialImage = Utils
                .getDoubleArrayPixelsFromBufImg(initialImage);

        simulateProjectionData(pixInitialImage);

        projection = Utils.normalize2DArray(projection, 0, 1);

        double min = Utils.getMin(projection);

        double max = Utils.getMax(projection);

        short[] pixelshortArray = new short[projection.length
                * projection[0].length];

        for (int x = 0; x < projection.length; x++) {
            for (int y = 0; y < projection[0].length; y++) {
                // rescale pixel values for 12-bit grayscale image??
                if (max > min) {
                    gray = (int) ((projection[x][y]) * 2000 / (max));
                } else {
                    gray = (int) ((projection[x][y] - min) * 2000 / (max));
                }
                pixelshortArray[y + x * projection[0].length] = (short) gray;

            }
        }

        sinogram = create12bitImage(projection[0].length, projection.length,
                pixelshortArray);

        sinogramImage = PerformWindowing(sinogram);

        return sinogramImage;
    }
}
