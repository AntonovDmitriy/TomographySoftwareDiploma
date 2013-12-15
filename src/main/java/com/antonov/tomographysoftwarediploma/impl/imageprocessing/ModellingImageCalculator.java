/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl.imageprocessing;

import java.awt.image.BufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class ModellingImageCalculator {

    // ----------Parameters of Scanning-------------------------
    public int scans; // Amount of Detectors or Point of Sourses
    public int stepSize; // Step of rotating in degree
    int rotates;
    public static final int START_ROTATION_ANGLE = 0;
    public static final int FINISH_ROTATION_ANGLE = 180;
    public static final String REGIME_NEAREST_NEIGHBOUR_INTERPOLATION = "nearest";
    public static final String REGIME_LINEAR_ITERPOLATION = "linear";
    //---------------------Images-------------------------------
    BufferedImage sourceImage;
    BufferedImage resultImage;

    //---------------------Other--------------------------------
    boolean isDataModellingValid = false;
    private static final Logger logger = LoggerFactory.getLogger(ModellingImageCalculator.class);

    public static final int AREA_SCANNING_IN_DEGREES = 180;

    public void setDataModelling(int scans, int stepSize) throws NumberWrongValueException, ImageWrongValueException {

        checkScans(scans);
        logger.trace("scans value is correct " + scans);
        this.scans = scans;

        checkStepSize(stepSize);
        logger.trace("stepSize value is correct " + stepSize);
        this.stepSize = stepSize;
        this.rotates = AREA_SCANNING_IN_DEGREES / stepSize;

    }

    protected void setInitialImage(BufferedImage sourceImage) throws ImageWrongValueException {

        checkSourceImage(sourceImage);
        this.sourceImage = sourceImage;
        logger.trace("sourceImage is correct ");
    }

    private void checkScans(int scans) throws NumberWrongValueException {
        try {
            checkInt(scans);
        } catch (NumberFormatException ex) {
            logger.error("Error value of scans " + scans + " ", ex);
            throw new NumberWrongValueException("Error value of scans " + scans + " ", ex);
        }
    }

    private void checkStepSize(int stepSize) throws NumberWrongValueException {

        try {
            checkInt(stepSize);
            checkRestOfDevision(stepSize, AREA_SCANNING_IN_DEGREES);
        } catch (NumberFormatException ex) {
            logger.error("Error value of stepSize " + stepSize + " ", ex);
            throw new NumberWrongValueException("Error value of stepSize " + stepSize + " ", ex);
        }
    }

    protected void checkInt(int number) throws NumberFormatException {
        if (number == 0) {
            throw new NumberFormatException("Number is zero");
        }
        if (number < 0) {
            throw new NumberFormatException("Number is down zero");
        }
    }

    private void checkRestOfDevision(int divisor, int dividend) {

        int restOf = dividend % divisor;
        if (restOf != 0) {
            throw new NumberFormatException("Rest of devision " + dividend + " on " + divisor + " is not 0");
        }
    }

    protected void checkSourceImage(BufferedImage sourceImage) throws ImageWrongValueException {
        if (sourceImage == null) {
            logger.error("sourceImage is null");
            throw new ImageWrongValueException("Source image is null");
        }
    }

    protected double calculateImageScaleRatio(double[][] imagePixArray, int scans) {

        int heightInitialImage = imagePixArray[0].length;
        double result = heightInitialImage * Math.sqrt(2) / scans;
        return result;
    }
}
