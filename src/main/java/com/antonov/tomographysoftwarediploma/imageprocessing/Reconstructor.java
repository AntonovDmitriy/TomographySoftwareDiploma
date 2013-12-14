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

    // this method is too long cause better understanding of sequence of actions with data
    public double[][] getReconstructedDate(double[][] filteredProjectionData, String regimeInterpolation) {

        //for better production tabs of angles are created at the rest of cycle
        double[] sinTab = Utils.getRowOfFunctionIncrementalValues("sin", START_ROTATION_ANGLE, FINISH_ROTATION_ANGLE, this.stepSize);
        double[] cosTab = Utils.getRowOfFunctionIncrementalValues("cos", START_ROTATION_ANGLE, FINISH_ROTATION_ANGLE, this.stepSize);

        double[][] reconstructedData = new double[sizeOfReconstruction][sizeOfReconstruction];
        reconstructedData = Utils.fillZeroMatrix(reconstructedData);

        double scaleArrayRecostructedDataToImageRatio = calculateImageScaleRatio(reconstructedData, this.scans);
        int Xcenter = sizeOfReconstruction / 2;
        int Ycenter = sizeOfReconstruction / 2;

        for (int x = -Xcenter; x < Xcenter; x++) {
            for (int y = -Ycenter; y < Ycenter; y++) {
                int rotate = 0;
                double valueOfGray = 0;
                for (int angle = START_ROTATION_ANGLE; angle < FINISH_ROTATION_ANGLE; angle = angle + stepSize) {

                    double position = (-x * cosTab[rotate] + y * sinTab[rotate]);
                    switch (regimeInterpolation) {
                        case REGIME_NEAREST_NEIGHBOUR_INTERPOLATION:
                            valueOfGray = revertedInteprolationNearestNeighbous(filteredProjectionData, position, scaleArrayRecostructedDataToImageRatio, valueOfGray, rotate);
                            break;
                        case REGIME_LINEAR_ITERPOLATION:
                            valueOfGray = revertedInterpolationLinear(filteredProjectionData, rotate, position, scaleArrayRecostructedDataToImageRatio, valueOfGray);
                            break;
                    }
                    rotate++;
                }
                reconstructedData[x + Xcenter][y + Ycenter] = valueOfGray / rotates;
            }
        }

        return reconstructedData;
    }

    private double[][] filteringBackProjection(double[][] projectionData) {

        double[][] filteredProjectionData = new double[rotates][scans];
        
        int scansPow2 = getScansPow2();
        double[] rawdata = new double[scansPow2 * 2];
        double[] idata = new double[scansPow2 * 2];

        for (int i = 0; i < scansPow2; i++) {
            idata[i] = 0;
        }
        
        // Initialize the filter
        double[] filter = Utils.filter1(filterName, scansPow2 * 2, 1.0);

        int i = 0;
        // Filter each projection
        for (int phi = 0; phi < 180; phi += stepSize, i++) {
            for (int S = 0; S < scans; S++) {
                rawdata[S] = projectionData[i][S];
            }
            //zero pad projections
            for (int S = scans; S < scansPow2 * 2; S++) {
                rawdata[S] = 0;
            }
            Utils.FFT(1, scansPow2 * 2, rawdata, idata);
            for (int S = 0; S < scans * 2; S++) {
                rawdata[S] *= filter[S];
            }
            //perform inverse fourier transform of filtered product
            Utils.FFT(0, scansPow2 * 2, rawdata, idata);
            for (int S = 0; S < scans; S++) {
                filteredProjectionData[i][S] = rawdata[S];
            }
            for (int S = 0; S < scansPow2 * 2; S++) {
                idata[S] = 0;
            }
        }
        return filteredProjectionData;
    }

    public BufferedImage reconstructModellingSinogram(BufferedImage sinogram) throws ImageWrongValueException {
        return null;
    }

    public BufferedImage reconstructProjectionData(double[][] projectionData, String regimeInterpolation) {

        if (scans != 0 && stepSize != 0 && sizeOfReconstruction != 0 && filterName != null) {

            double[][] filteredProjectionData = filteringBackProjection(projectionData);
            logger.trace("Projection data is filtered");
            double[][] reconstructedData = getReconstructedDate(filteredProjectionData, regimeInterpolation);
            logger.trace("Reconstructed data is created");
            BufferedImage reconstructionImage = Utils.createImagefromArray(reconstructedData);
            logger.trace("Reconstructed image is created");
            reconstructionImage = PerformWindowing(reconstructionImage);
            logger.trace("Reconstructed inage has been performed for screaning");
            return reconstructionImage;
        } else {
            throw new MissingFormatArgumentException("Parameters of modelling are emptry or incorrect");
        }
    }

    private double revertedInteprolationNearestNeighbous(double[][] filteredProjectionData, double position, double scaleArrayRecostructedDataToImageRatio, double valueOfGray, int rotate) {

        int positionInGrid = (int) Math.round(position / scaleArrayRecostructedDataToImageRatio);
        positionInGrid = positionInGrid + scans / 2;
        if (positionInGrid < scans && positionInGrid > 0) {
            valueOfGray += filteredProjectionData[rotate][positionInGrid];
        }
        return valueOfGray;
    }

    private double revertedInterpolationLinear(double[][] filteredProjectionData, int rotate, double position, double scaleArrayRecostructedDataToImageRatio, double valueOfGray) {
        int a = (int) Math.floor(position / scaleArrayRecostructedDataToImageRatio);
        int b = a + scans / 2;
        if (b < scans - 1 && b > 0) {
            if (position >= 0) {
                valueOfGray = valueOfGray
                        + filteredProjectionData[rotate][b]
                        + (filteredProjectionData[rotate][b + 1] - filteredProjectionData[rotate][b])
                        * (position / scaleArrayRecostructedDataToImageRatio - (double) a);
            } else {
                valueOfGray = valueOfGray
                        + filteredProjectionData[rotate][b]
                        + (filteredProjectionData[rotate][b] - filteredProjectionData[rotate][b + 1])
                        * (Math.abs(position / scaleArrayRecostructedDataToImageRatio) - Math
                        .abs(a));
            }
        }
        return valueOfGray;
    }

    private int getScansPow2() {

        if (Utils.isPow2(scans) == true) {
            return scans;
        } else {
            int power = (int) ((Math.log(scans) / Math.log(2))) + 1; //closest power of 2 rounded up
            return (int) Math.pow(2, power);
        }
    }
}
