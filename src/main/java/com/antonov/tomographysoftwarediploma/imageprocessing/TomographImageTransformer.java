/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.imageprocessing;

import com.antonov.tomographysoftwarediploma.controllers.ModellingModuleController;
import java.awt.image.BufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class TomographImageTransformer {

    // ----------Parameters of Scanning-------------------------
    public int scans; // Amount of Detectors or Point of Sourses
    public int stepSize; // Step of rotating in degree
    int views;

    //---------------------Images-------------------------------
    BufferedImage sourceImage;
    BufferedImage resultImage;

    //---------------------Other--------------------------------
    boolean isDataModellingValid = false;
    private static Logger logger = LoggerFactory.getLogger(TomographImageTransformer.class);

    public static final int AREA_SCANNING_IN_DEGREES = 180;

    public void setDataModelling(BufferedImage sourceImage, int scans, int stepSize) throws NumberWrongValueException, ImageWrongValueException {
        checkScans(scans);
        logger.trace("scans value is correct " + scans);
        this.scans = scans;
        
        checkStepSize(stepSize);
        logger.trace("stepSize value is correct " + stepSize);
        this.stepSize = stepSize;
        this.views = AREA_SCANNING_IN_DEGREES / stepSize;
        
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

    private void checkInt(int number) throws NumberFormatException {
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

    private void checkSourceImage(BufferedImage sourceImage) throws ImageWrongValueException {
        if(sourceImage == null){
            logger.error("sourceImage is null");
            throw new ImageWrongValueException("Source image is null");
        }
    }

}