/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.controllers;

import com.antonov.tomographysoftwarediploma.impl.ITomographView;
import com.antonov.tomographysoftwarediploma.impl.PInterpolation;
import com.antonov.tomographysoftwarediploma.impl.Tomograph;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.ColorFunctionNamesEnum;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Map;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class HardwareModuleController extends Controller {

    public static final int AREA_SCANNING_IN_DEGREES = 180;
    private static Logger logger = LoggerFactory.getLogger(ModellingModuleController.class);
    private final ResourceBundle bundle = ResourceBundle.getBundle(
            "bundle_Rus");

    public HardwareModuleController(Tomograph tomograph, ITomographView view) {
        super(tomograph, view);
    }

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue,
                newValue);
    }

    public void setScans(String scansString, Component comp) {
        try {
            int scans = checkAndGetInt(scansString);
            super.tomograph.hardwareModule.setScans(scans);
        } catch (NumberFormatException ex) {
            logger.warn("Error parsing value of tomograph_scans", ex);
            firePropertyChange("PARAMETER_VALUE_WARNING", null, bundle.getString("ERROR_PARSE_SCANS") + " " + scansString + " " + ex.getMessage());
        }

    }

        public void setMoving(String movingString, Component comp) {
        try {
            int moving = checkAndGetInt(movingString);
            super.tomograph.hardwareModule.setMoving(moving);
        } catch (NumberFormatException ex) {
            logger.warn("Error parsing value of tomograph_moving", ex);
            firePropertyChange("PARAMETER_VALUE_WARNING", null, bundle.getString("ERROR_PARSE_MOVING") + " " + movingString + " " + ex.getMessage());
        }

    }
    
    public void setStepSize(String stepSizeString) {
        try {
            int stepSize = checkAndGetInt(stepSizeString);
            checkRestOfDevision(stepSize, AREA_SCANNING_IN_DEGREES);
            super.tomograph.hardwareModule.setStepSize(stepSize);
        } catch (NumberFormatException ex) {
            logger.warn("Error parsing value of tomograph_scans", ex);
            firePropertyChange("PARAMETER_VALUE_WARNING", null, bundle.getString("ERROR_PARSE_STEPSIZE") + " " + stepSizeString + " " + ex.getMessage());
        }
    }

    public void setSizeReconstruction(String sizeReconstructionString) {
        try {
            int sizeReconstruction = checkAndGetInt(sizeReconstructionString);
            super.tomograph.hardwareModule.setSizeReconstruction(sizeReconstruction);
        } catch (NumberFormatException ex) {
            logger.warn("Error parsing value of tomograph_sizeReconstruction", ex);
            firePropertyChange("PARAMETER_VALUE_WARNING", null, bundle.getString("ERROR_PARSE_SIZERECONSTRUCTION") + " " + sizeReconstructionString + " " + ex.getMessage());
        }
    }

    public void addPropertyChangeListenerToModel(PropertyChangeListener p) {
        super.tomograph.hardwareModule.addPropertyChangeListener(p);
    }

    private int checkAndGetInt(String scansString) throws NumberFormatException {
        int scans = Integer.parseInt(scansString);
        if (scans <= 0) {
            throw new NumberFormatException("Number is down zero");
        }
        return scans;
    }

    private void checkRestOfDevision(int divisor, int dividend) {

        int restOf = dividend % divisor;
        if (restOf != 0) {
            throw new NumberFormatException("Rest of devision " + dividend + " on " + divisor + " is not 0");
        }
    }

    public void setReconstructionInterpolation(Object selectedItem) {
        try {

            PInterpolation pojoInterpolation = (PInterpolation) selectedItem;
            super.tomograph.hardwareModule.setReconstructionRegimeInterpolation(pojoInterpolation);
        } catch (ClassCastException ex) {
            firePropertyChange("PARAMETER_VALUE_WARNING", null, " " + ex.getMessage());
        }
    }

    public void setFilterModel(Object selectedItem) {
        super.tomograph.hardwareModule.setFilterModel((String) selectedItem);
    }

    public void setColoringName(ColorFunctionNamesEnum name) {
        super.tomograph.hardwareModule.setCurrentColorOfModellingImage(name);
    }

    public void saveModellingReconstruction(File file, String desc) {

    }

    public void showReconstructionModelling() {
    }

    public void showDensityAnalizator() {
    }
}
