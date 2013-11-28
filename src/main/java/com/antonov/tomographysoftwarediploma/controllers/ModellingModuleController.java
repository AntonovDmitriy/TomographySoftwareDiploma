package com.antonov.tomographysoftwarediploma.controllers;

import com.antonov.tomographysoftwarediploma.impl.ITomographView;
import com.antonov.tomographysoftwarediploma.impl.Tomograph;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
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

    private static Logger logger = LoggerFactory.getLogger(ModellingModuleController.class);
    private final ResourceBundle bundle = ResourceBundle.getBundle(
            "bundle_Rus");

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
            JOptionPane.showMessageDialog(comp, bundle.getString("ERROR_PARSE_SCANS") + " " + scansString + " " + ex.getMessage(), bundle.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
        }

    }

    public void setStepSize(String stepSizeString, Component comp) {
        try {
            int stepSize = checkAndGetInt(stepSizeString);
            super.tomograph.modellingModule.setStepSize(stepSize);
        } catch (NumberFormatException ex) {
            logger.warn("Error parsing value of scans", ex);
            JOptionPane.showMessageDialog(comp, bundle.getString("ERROR_PARSE_STEPSIZE") + " " + stepSizeString + " " + ex.getMessage(), bundle.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
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

    public void createSinogram(String scans, String stepSize) {

        //!!!!!!!!!!!!!!!!!!!CHECKING PARAMETERS MODELLING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        super.tomograph.modellingModule.createSinogram();
    }
}
