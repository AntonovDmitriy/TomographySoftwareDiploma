package com.antonov.tomographysoftwarediploma.controllers;

import com.antonov.tomographysoftwarediploma.impl.ITomographView;
import com.antonov.tomographysoftwarediploma.impl.PInterpolation;
import com.antonov.tomographysoftwarediploma.impl.Tomograph;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class ModellingModuleController extends Controller {

    public static final int AREA_SCANNING_IN_DEGREES = 180;
    
    private static Logger logger = LoggerFactory.getLogger(ModellingModuleController.class);
    private final ResourceBundle bundle = ResourceBundle.getBundle(
            "bundle_Rus");

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue,
                newValue);
    }

    public ModellingModuleController(Tomograph tomograph, ITomographView view) {
        super(tomograph, view);
    }

    public void setModellingImages(Map<String, BufferedImage> imageSamplesMapWithNames) {
        super.view.setModellingImages(imageSamplesMapWithNames);
    }

    public void setModelCurrentModellingImageByName(String image) {

        super.tomograph.modellingModule.setCurrentModellingImageByName(image);
    }

    public void getAndSetFileModellingImage(File file) {
        super.tomograph.modellingModule.getAndSetFileModellingImage(file);
    }

    public void setScans(String scansString, Component comp) {
        try {
            int scans = checkAndGetInt(scansString);
            super.tomograph.modellingModule.setScans(scans);
        } catch (NumberFormatException ex) {
            logger.warn("Error parsing value of scans", ex);
            firePropertyChange("PARAMETER_VALUE_WARNING", null, bundle.getString("ERROR_PARSE_SCANS") + " " + scansString + " " + ex.getMessage());
        }

    }

    public void setStepSize(String stepSizeString, Component comp) {
        try {
            int stepSize = checkAndGetInt(stepSizeString);
            checkRestOfDevision(stepSize,AREA_SCANNING_IN_DEGREES);
            super.tomograph.modellingModule.setStepSize(stepSize);
        } catch (NumberFormatException ex) {
            logger.warn("Error parsing value of scans", ex);
            firePropertyChange("PARAMETER_VALUE_WARNING", null, bundle.getString("ERROR_PARSE_STEPSIZE") + " " + stepSizeString + " " + ex.getMessage());
        }
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener p) {
        super.tomograph.modellingModule.addPropertyChangeListener(p);
    }

    private int checkAndGetInt(String scansString) throws NumberFormatException {
        int scans = Integer.parseInt(scansString);
        if (scans < 0) {
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

    public void createSinogram(String scans, String stepSize) {

        super.tomograph.modellingModule.createSinogram();
    }

    public void setInterpolation(Object selectedItem) {
        try{
            
        PInterpolation pojoInterpolation = (PInterpolation) selectedItem;
        super.tomograph.modellingModule.setRegimeInterpolation(pojoInterpolation);
        } catch (ClassCastException ex){
            firePropertyChange("PARAMETER_VALUE_WARNING", null, " " + ex.getMessage());
        }
    }
}
