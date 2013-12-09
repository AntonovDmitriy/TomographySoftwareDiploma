/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import com.antonov.tomographysoftwarediploma.imageprocessing.ImageTransformerFacade;
import com.antonov.tomographysoftwarediploma.controllers.ModellingModuleController;
import com.antonov.tomographysoftwarediploma.imageprocessing.SinogramCreator;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class ModellingModule {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("bundle_Rus");
    ModellingModuleController controller;
    private static final Logger logger = LoggerFactory.getLogger(ModellingModule.class);
    private Properties tomographProperty;
    private Map<String, BufferedImage> imageSamplesMapWithNames = new HashMap<>(); //Map for storage images for modelling
    private BufferedImage currentModellingImage;
    private BufferedImage sinogramImage;

    public PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    //Parameters of modelling
    private Integer scans;
    private Integer stepSize;
    private PInterpolation regimeInterpolation;
    private Set<PInterpolation> setInterpolation;

    public void setController(ModellingModuleController controller) {
        this.controller = controller;

    }

    public void setImageSamplesMap(Map<String, BufferedImage> map) {
        this.imageSamplesMapWithNames = map;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue,
                newValue);
    }

    public Map<String, BufferedImage> getImageSampleMap() {
        return this.imageSamplesMapWithNames;
    }

    public ModellingModule(Properties p) {

        if (p != null) {
            this.tomographProperty = p;
            initInterpolations();
            initSamplesMapImage();
            initParamModelling();

        } else {
            logger.warn("Properties file is null");
        }

    }

    private void initInterpolations() {

        setInterpolation = new HashSet<>();
        PInterpolation pojo = new PInterpolation();
        pojo.setValue(SinogramCreator.REGIME_LINEAR_ITERPOLATION);
        pojo.setNameInteprolation(bundle.getString("LINEAR_INTERPOLATION"));
        setInterpolation.add(pojo);

        pojo = new PInterpolation();
        pojo.setValue(SinogramCreator.REGIME_NEAREST_NEIGHBOUR_INTERPOLATION);
        pojo.setNameInteprolation(bundle.getString("NEAREST_NEIGHBOUR_INTERPOLATION"));
        setInterpolation.add(pojo);
    }

    private void initSamplesMapImage() {

        if (tomographProperty.getProperty("PATH_MODELLING_IMAGES") != null && !tomographProperty.getProperty("PATH_MODELLING_IMAGES").isEmpty()) {
            try {
                String pathToImages = tomographProperty.getProperty("PATH_MODELLING_IMAGES");
                logger.info("Path to the internal images is " + pathToImages);

                for (File imageFile : new File(pathToImages).listFiles()) {

                    if (imageFile.exists() && imageFile.isFile()) {
                        logger.trace("Reading image file " + imageFile.getAbsolutePath());
                        BufferedImage image = ReaderWriterData.getImageFromFileSystem(imageFile);
                        logger.trace("File successfully has been read ");
                        String imageNameWithoutExt = (imageFile.getName().split("\\."))[0];
                        image = ImageTransformerFacade.prepareImage(image);
                        imageSamplesMapWithNames.put(imageNameWithoutExt, image);
                        logger.trace("Image file " + imageFile.getAbsolutePath() + " was been successufully added");
                    } else {
                        logger.info(imageFile.getAbsolutePath() + " is not file ");
                    }
                }
            } catch (IOException ex) {
                logger.warn("Error during downloading internal images ", ex);
            }
        } else {
            logger.warn("Path for internal images for modelling is empty or null");
        }
    }

    public void prepareView() {

        controller.setModellingImages(imageSamplesMapWithNames);
        firePropertyChange("scans", null, scans);
        firePropertyChange("stepsize", null, stepSize);
        firePropertyChange("regimeInterpolationModel", null, setInterpolation);
        firePropertyChange("regimeInterpolation", null, regimeInterpolation);
        logger.info("Views are prepared");
    }

    public void setCurrentModellingImageByName(String image) {
        currentModellingImage = imageSamplesMapWithNames.get(image);
        logger.info("Current modelling image changes on " + image);
        setCurrentModellingImage();
    }

    public void setCurrentModellingImage() {
        firePropertyChange("currentImageModelling", null, currentModellingImage);
        logger.info("Current modelling image changes on display");
        firePropertyChange("clearResultModelling", null, null);
        logger.info("Result modelling is clear");
        firePropertyChange("disableModellingControls", null, null);
        logger.info("Modelling controls are disabled");
    }

    public void setSinogramImage(BufferedImage image) {
        BufferedImage oldSinogramImage = this.sinogramImage;
        this.sinogramImage = image;
        firePropertyChange("setSinogramImage", oldSinogramImage, sinogramImage);
        logger.trace("Sinogram image is changed");
    }

    public void getAndSetFileModellingImage(File file) {
        try {
            BufferedImage image = ReaderWriterData.getImageFromFileSystem(file);
            logger.info(file.getAbsolutePath() + " is successfully opened");
            BufferedImage imagePrepared = ImageTransformerFacade.prepareImage(image);
            logger.info(file.getAbsolutePath() + " is prepared");
            currentModellingImage = imagePrepared;
            logger.info("Current modelling image changes on " + file.getName());
            setCurrentModellingImage();
        } catch (IOException ex) {
            logger.error("Error during openinig image " + ex);
        }
    }

    private void initParamModelling() {
        logger.info("Reading initial modelling parameters");
        try {
            scans = Integer.parseInt(tomographProperty.getProperty("SCANS"));
            logger.info("scans = " + scans);
        } catch (NumberFormatException ex) {
            logger.warn("Error reading initial parameter SCANS", ex);
        }

        try {
            stepSize = Integer.parseInt(tomographProperty.getProperty("STEPSIZE"));
            logger.info("stepsize = " + stepSize);
        } catch (NumberFormatException ex) {
            logger.warn("Error reading initial parameter STEPSIZE", ex);
        }

        try {
            String regimeInterpolationFromProperty = tomographProperty.getProperty("REGIME_INTERPOLATION");
            if (!regimeInterpolationFromProperty.equals(SinogramCreator.REGIME_LINEAR_ITERPOLATION)
                    && !regimeInterpolationFromProperty.equals(SinogramCreator.REGIME_LINEAR_ITERPOLATION)) {
                throw new NumberFormatException("Regime of interpolation has invalid value " + regimeInterpolationFromProperty);
            } else {
                for (PInterpolation pojoInterpolation : setInterpolation) {
                    if (pojoInterpolation.getValue().equals(regimeInterpolationFromProperty)) {
                        regimeInterpolation = pojoInterpolation;
                        break;
                    }
                }

            }
            logger.info("regimeInterpolation = " + regimeInterpolation);
        } catch (NumberFormatException ex) {
            logger.warn("Error reading initial parameter REGIME_INTERPOLATION", ex);
        }
        logger.info("Initial modelling parameters have been read");
    }

    public void setScans(int scans) {
        Integer oldScans = this.scans;
        this.scans = scans;
        logger.trace("Value of scans now is " + scans + ". Old value was " + oldScans);
    }

    public void setStepSize(int stepSize) {
        Integer oldStepSize = this.stepSize;
        this.stepSize = stepSize;
        logger.trace("Value of stepSize now is " + stepSize + ". Old value was " + oldStepSize);
        firePropertyChange("clearResultModelling", null, null);
        logger.info("Result modelling is clear");
    }

    public void setRegimeInterpolation(PInterpolation regimeInterpolation) {

        PInterpolation oldRegimeInterpolation = this.regimeInterpolation;
        this.regimeInterpolation = regimeInterpolation;

        if (oldRegimeInterpolation != null) {
            logger.trace("Value of regimeInterpolation now is " + regimeInterpolation.getValue()
                    + ". Old value was " + oldRegimeInterpolation.getValue());
        } else {
            logger.trace("Value of regimeInterpolation now is " + regimeInterpolation.getValue()
                    + ". Old value was " + oldRegimeInterpolation);
        }

    }

    public void createSinogram() {
        try {
            logger.trace("Sinogram creating is starting");
            firePropertyChange("startSinogramm", null, null);
            BufferedImage sinogram = ImageTransformerFacade.createSinogram(currentModellingImage, scans, stepSize, regimeInterpolation.getValue());
            setSinogramImage(sinogram);
            firePropertyChange("enableReconControls", null, null);
            logger.trace("Recon controls are enabled");
            firePropertyChange("stopSinogramm", null, null);
            logger.trace("Sinogram createing is finishing");
        } catch (Throwable ex) {
            logger.error("Internal error during calculating sinogram ", ex);
            firePropertyChange("INTERNAL_ERROR", null, "Internal error during calculating sinogram");
        }
    }
}
