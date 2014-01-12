/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import com.antonov.tomographysoftwarediploma.controllers.HardwareModuleController;
import com.antonov.tomographysoftwarediploma.dblayer.ConnectionManagerImpl;
import com.antonov.tomographysoftwarediploma.dblayer.DbModule;
import com.antonov.tomographysoftwarediploma.dblayer.EmptyOrNullParameterException;
import com.antonov.tomographysoftwarediploma.dblayer.IConnectionManager;
import com.antonov.tomographysoftwarediploma.dblayer.ITomographDao;
import com.antonov.tomographysoftwarediploma.dblayer.PSetProjectionData;
import com.antonov.tomographysoftwarediploma.dblayer.TomographDaoImpl;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.ColorFunctionNamesEnum;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.ImageTransformerFacade;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.ImageWrongValueException;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.NumberWrongValueException;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.SinogramCreator;
import com.jcraft.jsch.JSchException;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class HardwareModule {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("bundle_Rus");
    public PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private static final Logger logger = LoggerFactory.getLogger(HardwareModule.class);
    private HardwareModuleController controller;
    private Properties tomographProperty;
    private Tomograph tomograph;

    //Parameters of modelling
    private Integer scans;
    private Integer stepSize;
    private Integer moving;
    private PInterpolation regimeSinogramInterpolation;

    //Parameters of reconstruction
    private Integer sizeReconstruction;
    private String currentFilter;
    private PInterpolation regimeReconstructionInterpolation;
    private ColorFunctionNamesEnum currentColorName;

    private BufferedImage currentReconstructionImage;
    private BufferedImage coloredReconstructionImage;

    private List<PSetProjectionData> listProjectionData = new ArrayList<>();
    private IConnectionManager connectionManager;
    private List<BufferedImage> listReconstructedImages;

    public HardwareModule(Tomograph tomograph, Properties p) {
        this.tomograph = tomograph;
        init(p);
    }

    private void init(Properties p) {

        if (p != null) {
            this.tomographProperty = p;
            initConnectionManager();
            initParamScanning();
            initProjectionDataList();
        } else {
            logger.warn("Properties file is null");
        }
    }

    private void initParamScanning() {

        logger.info("Reading initial hardware module parameters");
        initScans();
        initStepSize();
        initReconstructionInterpolation();
        initSizeReconstruction();
        initFiltering();
        initColoring();
        initMoving();
        logger.info("Initial hardware module parameters have been read");
    }

    private void initScans() {

        try {
            scans = Integer.parseInt(tomographProperty.getProperty("TOMOGRAPH_SCANS"));
            logger.info("tomograph_scans = " + scans);
        } catch (NumberFormatException ex) {
            logger.warn("Error reading initial parameter TOMOGRAPH_SCANS", ex);
        }
    }

    private void initStepSize() {

        try {
            stepSize = Integer.parseInt(tomographProperty.getProperty("TOMOGRAPH_STEPSIZE"));
            logger.info("tomograph_stepsize = " + stepSize);
        } catch (NumberFormatException ex) {
            logger.warn("Error reading initial parameter TOMOGRAPH_STEPSIZE", ex);
        }
    }

    private void initReconstructionInterpolation() {

        try {
            String regimeInterpolationFromProperty = tomographProperty.getProperty("TOMOGRAPH_REGIME_RECONSTRUCTION_INTERPOLATION");
            if (!regimeInterpolationFromProperty.equals(SinogramCreator.REGIME_LINEAR_ITERPOLATION)
                    && !regimeInterpolationFromProperty.equals(SinogramCreator.REGIME_NEAREST_NEIGHBOUR_INTERPOLATION)) {
                throw new NumberFormatException("Regime of reconstruction interpolation has invalid value " + regimeInterpolationFromProperty);
            } else {
                for (PInterpolation pojoInterpolation : tomograph.setInterpolation) {
                    if (pojoInterpolation.getValue().equals(regimeInterpolationFromProperty)) {
                        regimeReconstructionInterpolation = pojoInterpolation;
                        break;
                    }
                }
            }
            logger.info("regimeReconstructionInterpolation = " + regimeReconstructionInterpolation);
        } catch (NumberFormatException ex) {
            logger.warn("Error reading initial parameter REGIME_RECONSTRUCTION_INTERPOLATION", ex);
        }
    }

    private void initSizeReconstruction() {

        try {
            sizeReconstruction = Integer.parseInt(tomographProperty.getProperty("TOMOGRAPH_SIZE_RECONSTRUCTION"));
            logger.info("tomograph_sizeReconstruction = " + sizeReconstruction);
        } catch (NumberFormatException ex) {
            logger.warn("Error reading initial parameter TOMOGRAPH_SIZE_RECONSTRUCTION", ex);
        }
    }

    private void initFiltering() {

        try {
            String filteringFromProperty = tomographProperty.getProperty("FILTERING_TOMOGRAPH");
            if (!tomograph.setFilterName.contains(filteringFromProperty)) {
                throw new NumberFormatException("Filter name has invalid value " + filteringFromProperty);
            } else {
                currentFilter = filteringFromProperty;
            }
            logger.info("current filtering tomograph = " + currentFilter);
        } catch (NumberFormatException ex) {
            logger.warn("Error reading initial parameter FILTERING_TOMOGRAPH", ex);
        }
    }

    private void initColoring() {

        String coloringFromProperty = tomographProperty.getProperty("COLORING_TOMOGRAPH");
        if (tomograph.isColoringEnumContainsColorName(coloringFromProperty)) {
            currentColorName = ColorFunctionNamesEnum.valueOf(coloringFromProperty);
            logger.info("coloring name tomograph = " + coloringFromProperty);
        } else {
            logger.warn("Coloring name tomograph has invalid value " + coloringFromProperty);
        }
    }

    private void initMoving() {
        try {
            moving = Integer.parseInt(tomographProperty.getProperty("TOMOGRAPH_MOVING"));
            logger.info("TOMOGRAPH_MOVING = " + moving);
        } catch (NumberFormatException ex) {
            logger.warn("Error reading initial parameter TOMOGRAPH_MOVING", ex);
        }
    }

    public void setController(HardwareModuleController controller) {
        this.controller = controller;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue,
                newValue);
    }

    public void prepareView() {

        firePropertyChange("hardware_scans", null, scans);
        firePropertyChange("hardware_stepsize", null, stepSize);
        firePropertyChange("hardware_moving", null, stepSize);
        firePropertyChange("hardware_sizeReconstruction", null, sizeReconstruction);
        firePropertyChange("hardware_regimeInterpolationModel", null, tomograph.setInterpolation);
        firePropertyChange("hardware_regimeReconstructionInterpolation", null, regimeReconstructionInterpolation);
        firePropertyChange("hardware_filterSet", null, tomograph.setFilterName);
        firePropertyChange("hardware_filter", null, currentFilter);
        firePropertyChange("hardware_colorModel", null, ColorFunctionNamesEnum.class);
        firePropertyChange("hardware_currentColorModelling", null, currentColorName);
        if (this.listProjectionData != null && !this.listProjectionData.isEmpty()) {
            firePropertyChange("hardware_setProjectionData", null, listProjectionData);
        } else {
//            firePropertyChange("hardware_disableAllTomographControls", null, null);
        }
        logger.info("Views are prepared");
    }

    public void setScans(int scans) {
        Integer oldScans = this.scans;
        this.scans = scans;
        firePropertyChange("hardware_clearResultReconstruction", null, null);
        logger.trace("Value of tomograph_scans now is " + scans + ". Old value was " + oldScans);
        firePropertyChange("hardware_disableReconControls", null, null);
        logger.info("Hardware recon controls are disabled");
    }

    public void setStepSize(int stepSize) {
        Integer oldStepSize = this.stepSize;
        this.stepSize = stepSize;
        logger.trace("Value of hardware_stepSize now is " + stepSize + ". Old value was " + oldStepSize);
        firePropertyChange("hardware_clearResultReconstruction", null, null);
        logger.info("Result hardware_reconstruction is clear");
        firePropertyChange("hardware_disableReconControls", null, null);
        logger.info("Hardware recon controls are disabled");
    }

    public void setMoving(int moving) {
        Integer oldMoving = this.moving;
        this.moving = moving;
        firePropertyChange("hardware_clearResultReconstruction", null, null);
        logger.trace("Value of tomograph_moving now is " + moving + ". Old value was " + oldMoving);
        firePropertyChange("hardware_disableReconControls", null, null);
        logger.info("Hardware recon controls are disabled");
    }

    public void setSizeReconstruction(int sizeReconstruction) {
        Integer oldSizeReconstruction = this.sizeReconstruction;
        this.sizeReconstruction = sizeReconstruction;
        logger.trace("Value of tomograph_sizeReconstruction now is " + sizeReconstruction + ". Old value was " + oldSizeReconstruction);
        firePropertyChange("hardware_clearResultReconstruction", null, null);
        logger.info("Result hardware_reconstruction is clear");
        firePropertyChange("hardware_disableReconControls", null, null);
        logger.info("Hardware recon controls are disabled");
    }

    public void setFilterModel(String filterModel) {

        String oldFilterModel = this.currentFilter;
        this.currentFilter = filterModel;

        logger.trace("Value of tomograph_currentFilter now is " + currentFilter
                + ". Old value was " + oldFilterModel);
    }

    public void setReconstructionRegimeInterpolation(PInterpolation regimeInterpolation) {

        PInterpolation oldRegimeInterpolation = this.regimeReconstructionInterpolation;
        this.regimeReconstructionInterpolation = regimeInterpolation;

        if (oldRegimeInterpolation != null) {
            logger.trace("Value of tomograph_regimeReconstructionInterpolation now is " + regimeInterpolation.getValue()
                    + ". Old value was " + oldRegimeInterpolation.getValue());
        } else {
            logger.trace("Value of tomograph_regimeReconstructionInterpolation now is " + regimeInterpolation.getValue()
                    + ". Old value was " + oldRegimeInterpolation);
        }

    }

    public void setCurrentColorOfModellingImage(ColorFunctionNamesEnum colorName) {
        ColorFunctionNamesEnum oldCurrentColorName = this.currentColorName;
        this.currentColorName = colorName;

        logger.trace("Value of tomograph_currentColorName now is " + currentColorName
                + ". Old value was " + oldCurrentColorName);

        if (currentReconstructionImage != null) {
            doColoringToReconstructedImage();
        }
    }

    private void doColoringToReconstructedImage() {

        BufferedImage colorImage = ImageTransformerFacade.doColorOnImage(currentReconstructionImage, currentColorName);
//        logger.trace("Colored image has been created");
        this.coloredReconstructionImage = colorImage;
//        logger.trace("current tomograph colored image has been changed");
        firePropertyChange("hardware_currentReconstructedImageTomograph", null, coloredReconstructionImage);
    }

    public void startScanning(String fileName, String fileDesctiption) {

        if (scans == null || stepSize == null || moving == null) {
            firePropertyChange("ERROR", null, bundle.getString("ERROR_SCAN_PARAMETERS"));
        } else {
            try {
                firePropertyChange("hardware_disableTomographControls", null, null);
                firePropertyChange("hardware_startScanning", null, null);

                List<Object> listProjectionData = getProjectionData();
                insertProjectionDataToDb(fileName, fileDesctiption, listProjectionData);
                this.listProjectionData = selectAllProjectData();
                firePropertyChange("hardware_setProjectionData", null, this.listProjectionData);
                firePropertyChange("hardware_enableTomographControls", null, null);
                firePropertyChange("hardware_stopScanning", null, null);
            } catch (IOException ex) {
                logger.error("Error while reading image file ", ex);
                firePropertyChange("ERROR", null, bundle.getString("ERROR_IO_READING"));
            } catch (NumberWrongValueException ex) {
                logger.error("Error while generating projection data ", ex);
                firePropertyChange("ERROR", null, bundle.getString("ERROR_SCAN_PARAMETERS"));
            } catch (ImageWrongValueException ex) {
                logger.error("Error while generating projection data ", ex);
                firePropertyChange("ERROR", null, bundle.getString("ERROR_IO_READING"));
            } catch (JSchException ex) {
                logger.error("Error while creating ssh connection ", ex);
                firePropertyChange("ERROR", null, bundle.getString("ERROR_SSH_CONNECTION"));
            } catch (SQLException ex) {
                logger.error("Data base error ", ex);
                firePropertyChange("ERROR", null, bundle.getString("ERROR_DB"));
            } catch (EmptyOrNullParameterException ex) {
                firePropertyChange("ERROR", null, bundle.getString("ERROR_DB_CONFIGURATION"));
            }
        }

    }

    private List<Object> getProjectionData() throws IOException, NumberWrongValueException, ImageWrongValueException {
        ;

        logger.trace("Scanning is starting");
        long start = System.currentTimeMillis();
        //This methos emulates scanning. In future there will be method starting real scanning
        List<Object> listProjectionData = ScanningEmulator.emulateScanning(scans, stepSize, tomographProperty);
        long finish = System.currentTimeMillis();
        long result = finish - start;
        logger.trace("Scanning is finishing " + result + " sec");
        return listProjectionData;
    }

    private void insertProjectionDataToDb(String fileName, String fileDescr, List<Object> listProjectionData) throws JSchException, SQLException, EmptyOrNullParameterException {

        logger.trace("Start db insert of projection data set");
        long start = System.currentTimeMillis();
        ITomographDao dao = new TomographDaoImpl(tomographProperty, connectionManager);
        dao.insertProjectionData(fileName, fileDescr, listProjectionData);
        long finish = System.currentTimeMillis();
        long result = finish - start;
        logger.trace("Finish db insert of projection data set " + result + " sec");
    }

    private void initProjectionDataList() {
        try {
            TomographDaoImpl dao = new TomographDaoImpl(tomographProperty, connectionManager);
            this.listProjectionData = dao.selectAllSetProjectionData();
        } catch (Throwable ex) {
            logger.error("Error getting set projection data from db", ex);
        }
    }

    private List<PSetProjectionData> selectAllProjectData() throws JSchException, SQLException, EmptyOrNullParameterException {

        TomographDaoImpl dao = new TomographDaoImpl(tomographProperty, connectionManager);
        return dao.selectAllSetProjectionData();
    }

    private void initConnectionManager() {
        this.connectionManager = new ConnectionManagerImpl(tomographProperty);
        try {
            connectionManager.connect();
        } catch (Exception ex) {
            logger.error("Error while creating ssh or db connection ", ex);
        }
    }

    public IConnectionManager getConnectionManager() {
        return this.connectionManager;
    }

    public void reconstructProjectionData(PSetProjectionData selectedSet) {
        try {
            logger.trace("Reconstruction of selected set of projection dataa is starting");
            firePropertyChange("hardware_startReconstruction", null, null);

            ITomographDao dao = new TomographDaoImpl(tomographProperty, connectionManager);

            List<Object> setProjectionData = dao.getSetProjectionData(selectedSet);
            List<BufferedImage> listReconstructedImages = ImageTransformerFacade.recontructProjectionDataSet(setProjectionData, scans, stepSize, sizeReconstruction, currentFilter, regimeReconstructionInterpolation.getValue());
            this.listReconstructedImages = listReconstructedImages;

            setCurrentReconstructionImage(this.listReconstructedImages.get(0));
            firePropertyChange("hardware_amountReconstructedImages", null, this.listReconstructedImages.size() - 1);
            firePropertyChange("hardware_enableAfterReconstructControls", null, null);
            firePropertyChange("hardware_stopReconstruction", null, null);
            logger.trace("Reconstruction of selected set of projection data is finishied");

        } catch (Throwable ex) {
            logger.error("Internal error during reconstruction of set projection data ", ex);
            firePropertyChange("INTERNAL_ERROR", null, "Internal error during reconstruction sinogram");
        }
    }

    public void setCurrentReconstructionImage(BufferedImage image) {
        this.currentReconstructionImage = image;
        firePropertyChange("hardware_currentReconstructedImage", null, null);
//        logger.trace("Current reconstructed image is changed"); there was too big

        if (currentColorName != null) {
            doColoringToReconstructedImage();
        }
    }

    public void setCurrentReconstructedImage(int value) {
        
        BufferedImage image = listReconstructedImages.get(value);
        setCurrentReconstructionImage(image);
    }
}
