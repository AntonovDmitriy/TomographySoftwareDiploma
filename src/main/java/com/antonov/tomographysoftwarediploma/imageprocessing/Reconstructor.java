/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.imageprocessing;

import static com.antonov.tomographysoftwarediploma.ImageTransformator.create12bitImage;
import com.antonov.tomographysoftwarediploma.Utils;
import static com.antonov.tomographysoftwarediploma.imageprocessing.ImageTransformerFacade.PerformWindowing;
import static com.antonov.tomographysoftwarediploma.imageprocessing.ImageTransformerFacade.prepareImage;
import java.awt.image.BufferedImage;
import java.util.MissingFormatArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class Reconstructor extends ModellingImageCalculator {

    private int sizeOfReconstruction;
    private String filterName;

    private static final Logger logger = LoggerFactory.getLogger(Reconstructor.class);

    public void setDataReconstruction(int sizeOfReconstruction, String filterName) throws NumberWrongValueException {
        try {
            checkInt(sizeOfReconstruction);
        } catch (NumberFormatException ex) {
            logger.error("Error value of sizeOfReconstruction " + sizeOfReconstruction + " ", ex);
            throw new NumberWrongValueException("Error value of sizeOfReconstruction " + sizeOfReconstruction + " ", ex);
        }

        this.sizeOfReconstruction = sizeOfReconstruction;
        logger.trace("sizeOfReconstruction value is correct " + sizeOfReconstruction);
        this.filterName = filterName;
    }

    public BufferedImage createReconstructedImage(double[][] projectionDat) {

        int i, a;
        i = 0;
        double val = 0, pos, Aleft, Aright;
        double[][] bpimage = new double[sizeOfReconstruction][sizeOfReconstruction]; // массив с данными реконструкции
        int x, y, Xcenter, Ycenter, Ileft, Iright;
        double[][] fprojection;
        int S = 0;
        String interp = "nearest";
        fprojection = filteringBackProjection(projectionDat);
        double[] sinTab = Utils.getRowOfFunctionIncrementalValues("sin", START_ROTATION_ANGLE, FINISH_ROTATION_ANGLE, this.stepSize);
        double[] cosTab = Utils.getRowOfFunctionIncrementalValues("cos", START_ROTATION_ANGLE, FINISH_ROTATION_ANGLE, this.stepSize);

        // Заполнение матрицы для реконструкции нулями
        for (x = 0; x < sizeOfReconstruction; x++) {
            for (y = 0; y < sizeOfReconstruction; y++) {
                bpimage[x][y] = 0;
            }
        }

        // Back Project each pixel in the image
        Xcenter = sizeOfReconstruction / 2;
        Ycenter = sizeOfReconstruction / 2;
        i = 0;
        double scale = sizeOfReconstruction * Math.sqrt(2) / scans;
        System.out.println("Performing back projection.. ");

        boolean interrupt = false;

        for (x = -Xcenter; x < Xcenter; x++) {
            if (interrupt == true) {
                break;
            }
            for (y = -Ycenter; y < Ycenter; y++) {
                int x1 = x;
                int y1 = y;

                for (int phi = 0; phi < 180; phi = phi + stepSize) {
                    // pos = (x1 * sintab[i] - y1 * costab[i]);
                    pos = (-x1 * cosTab[i] + y1 * sinTab[i]);
                    // System.out.print("pos= "+pos+" ");

                    if (interp == "nearest") {

                        S = (int) Math.round(pos / scale);
                        S = S + scans / 2;
                        if (S < scans && S > 0) {
                            val = val + fprojection[i][S];
                        }
                    } // perform linear interpolation
                    else if (interp == "linear") {
                        if (pos >= 0) {
                            a = (int) Math.floor(pos / scale);
                            int b = a + scans / 2;
                            if (b < scans - 1 && b > 0) {
                                val = val
                                        + fprojection[i][b]
                                        + (fprojection[i][b + 1] - fprojection[i][b])
                                        * (pos / scale - (double) a);
                            }
                        } else if (pos < 0) {
                            a = (int) Math.floor(pos / scale);
                            int b = a + scans / 2;
                            if (b < scans - 1 && b > 0) {
                                val = val
                                        + fprojection[i][b]
                                        + (fprojection[i][b] - fprojection[i][b + 1])
                                        * (Math.abs(pos / scale) - Math
                                        .abs(a));
                            }
                        }
                    }
                    i++;
                }
                S = 0;
                i = 0;

                bpimage[x + Xcenter][y + Ycenter] = val / rotates;
                // bpimage[x + Xcenter][y + Ycenter] = val*Math.PI/2*views;
                // img = img*pi/(2*length(theta));
                val = 0;
            }
        }

        double maxval = Utils.getMax(bpimage);
        BufferedImage reconstruction = CreateImagefromArray(bpimage, maxval, 1);
        BufferedImage reconstuctionImage;

        reconstuctionImage = PerformWindowing(reconstruction);
        return reconstuctionImage;
    }

    private BufferedImage CreateImagefromArray(double[][] pix, double max,
            int type) {

        int i, j;
        i = 0;
        j = 0;

        short[] pixelshortArray = new short[pix.length * pix[0].length];
        double min = Utils.getMin(pix);

        double datamin = Utils.getMin(pix);

        // zero matrix
        if (datamin < 0) {
            for (i = 0; i < pix.length; i++) {
                for (j = 0; j < pix[0].length; j++) {
                    if (pix[i][j] < 0) {
                        pix[i][j] = 0;
                    }
                }
            }
        }

        int gray;
        // System.out.println("rescaling output image: ");

        for (int y = 0; y < pix[0].length; y++) {
            for (int x = 0; x < pix.length; x++) {
                if (min < 0) {
                    gray = (int) ((pix[x][y]) * 2000 / (max));
                } else {

                    gray = (int) ((pix[x][y]) * 2000 / (max));
                }
                pixelshortArray[x + y * pix.length] = (short) gray;
            }
        }

        BufferedImage img;
        // returns an 8-bit buffered image for display purposes

        img = create12bitImage(sizeOfReconstruction, sizeOfReconstruction, pixelshortArray);

        return img;

    }

    private double[][] filteringBackProjection(double[][] projectionData) {

        int i, pscans;
        double filter[], pfilter[];   //array to store filter and padded filter

        double[] rawdata;
        double[] idata;

        double[][] fproj = new double[rotates][scans];

        //length of array - no of 'scans', must be a power of 2
        //if scans is a power of 2 then just allocated twice this value for arrays and then pad
        //the projection (and filter data?) with zeroes before applying FFT
        if (Utils.isPow2(scans) == true) {
            pscans = scans;   //System.out.println("power of 2");
        } //if scans is not a power of 2, then round pscans up to nearest power and double
        else {
            int power = (int) ((Math.log(scans) / Math.log(2))) + 1; //closest power of 2 rounded up
            pscans = (int) Math.pow(2, power);
            System.out.println("PSCANS: " + pscans);
        }
        rawdata = new double[pscans * 2];
        idata = new double[pscans * 2];
        pfilter = new double[pscans * 2];

        for (int S = 0; S < pscans; S++) {
            idata[S] = 0;
        }

        // Initialize the filter
        filter = Utils.filter1(filterName, pscans * 2, 1.0);

        i = 0;

        // Filter each projection
        for (int phi = 0; phi < 180; phi += stepSize) {
            for (int S = 0; S < scans; S++) {
                rawdata[S] = projectionData[i][S];
            }
            //zero pad projections
            for (int S = scans; S < pscans * 2; S++) {
                rawdata[S] = 0;
            }
            Utils.FFT(1, pscans * 2, rawdata, idata);
            for (int S = 0; S < scans * 2; S++) {
                rawdata[S] *= filter[S];
            }
            //perform inverse fourier transform of filtered product
            Utils.FFT(0, pscans * 2, rawdata, idata);
            for (int S = 0; S < scans; S++) {
                fproj[i][S] = rawdata[S];
            }
            for (int S = 0; S < pscans * 2; S++) {
                idata[S] = 0;
            }
            i++;
        }
        return fproj;
    }

    public BufferedImage reconstructModellingSinogram(BufferedImage sinogram) throws ImageWrongValueException {
        return null;
    }

    public BufferedImage reconstructProjectionData(double[][] projData) {

        if (scans != 0 && stepSize != 0 && sizeOfReconstruction != 0 && filterName != null) {
            BufferedImage reconstruction = createReconstructedImage(projData);
            return reconstruction;
        } else {
            throw new MissingFormatArgumentException("Parameters of modelling are emptry or incorrect");
        }
    }
}
