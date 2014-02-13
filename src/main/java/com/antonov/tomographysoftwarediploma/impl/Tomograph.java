/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import com.antonov.tomographysoftwarediploma.controllers.HardwareModuleController;
import com.antonov.tomographysoftwarediploma.controllers.ModellingModuleController;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.ColorFunctionNamesEnum;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.SinogramCreator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class Tomograph {
    
    private static Logger logger = LoggerFactory.getLogger(Tomograph.class);
    ModellingModuleController modellingModuleController;
    HardwareModuleController hardwareModuleController;
    
    private static final String TOMOGRAPH_CONF_PATH = "conf/tomograph.conf";
    private static ResourceBundle bundle;
    private Properties tomographProperty = new Properties();
    
    public ModellingModule modellingModule;
    public HardwareModule hardwareModule;
    
    public Set<String> setFilterName;
    public Set<PInterpolation> setInterpolation;
    
    public Tomograph() {
        
        initTomographProperty();
        downloadMainJar();
        initRequiredSshConnectionFilesToSystem();
        initBundle();
        initInterpolations();
        initFilterNames();
        this.modellingModule = new ModellingModule(this, tomographProperty);
        this.hardwareModule = new HardwareModule(this, tomographProperty);
    }
    
    public void setModellingController(ModellingModuleController controller) {
        this.modellingModuleController = controller;
        this.modellingModule.setController(controller);
    }
    
    public void setHardwareController(HardwareModuleController controller) {
        this.hardwareModuleController = controller;
    }
    
    private void initTomographProperty() {
        try (InputStream is = new FileInputStream(TOMOGRAPH_CONF_PATH)) {
            tomographProperty.load(is);
            logger.info("Successfully loading config file " + TOMOGRAPH_CONF_PATH);
            
        } catch (IOException ex) {
            try (InputStream ist = getClass().getClassLoader().getResourceAsStream(TOMOGRAPH_CONF_PATH)) {
                tomographProperty.load(ist);
            } catch (IOException e) {
                logger.warn("Can't load configuration file " + TOMOGRAPH_CONF_PATH, ex);
            }
            
        }
    }
    
    public void prepareViews() {
        modellingModule.prepareView();
        hardwareModule.prepareView();
    }
    
    public void exitApplication() {
        hardwareModule.getConnectionManager().disconnect();
        logger.info("=======Stop TomographySoftware 1.0.0 application=======");
        System.exit(0);//cierra aplicacion
    }
    
    void showViewer(BufferedImage image) {
        AppLaunch.showImageViewer(image);
    }
    
    void showDensityAnalizator(BufferedImage image) {
        
        AppLaunch.showDensityAnalizator(image);
    }
    
    public void initFilterNames() {
        setFilterName = new TreeSet<>();
        setFilterName.add("ramp");
        setFilterName.add("shepplogan");
        setFilterName.add("hamming");
        setFilterName.add("hann");
        setFilterName.add("blackman");
        setFilterName.add("none");
    }
    
    public void initInterpolations() {
        
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
    
    public boolean isColoringEnumContainsColorName(String coloringFromProperty) {
        
        for (ColorFunctionNamesEnum color : ColorFunctionNamesEnum.values()) {
            if (color.toString().equals(coloringFromProperty)) {
                return true;
            }
        }
        return false;
    }
    
    private void initRequiredSshConnectionFilesToSystem() {
        try {
            String javaHome = System.getProperty("java.home");
            logger.info("Java home: " + javaHome);
            String javaSecurityFolder = javaHome + "/lib/security";
            logger.info("security folder: " + javaSecurityFolder);
            
            if (ReaderWriterData.isFolderExists(javaSecurityFolder)) {
                String javaVersion = System.getProperty("java.version");
                logger.info("java version: " + javaVersion);
                if (javaVersion.contains("1.7")) {
                    
                    try {
                        ReaderWriterData.copyFilesFromTo(tomographProperty.getProperty("PATH_CONNECTION_POLICY_7"),
                                javaSecurityFolder);
                    } catch (IOException | NullPointerException ex) {
                        try {
                            copySecurityFilesFromJar("jce7", javaSecurityFolder);
                        } catch (IOException ex3) {
                            logger.error("Error when copy security files from jar", ex3);
                            copySecurityFilesFromTargetDirectory("target/classes/connectionFiles/jce7", javaSecurityFolder);
                        }
                    }
                } else if (javaVersion.contains("1.6")) {
                    try {
                        ReaderWriterData.copyFilesFromTo(tomographProperty.getProperty("PATH_CONNECTION_POLICY_6"),
                                javaSecurityFolder);
                    } catch (IOException ex) {
                        copySecurityFilesFromJar("jce6", javaSecurityFolder);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error while configuring ssh connection required files", ex);
        }
    }
    
    private void copySecurityFilesFromJar(String nameOfFolder, String folderDestination) throws UnsupportedEncodingException, IOException {
        ReaderWriterData reader = new ReaderWriterData();
        String pathOfJar = reader.getNameOfCurrentJar(tomographProperty);
        reader.copyJarFolder(pathOfJar, nameOfFolder, folderDestination);
    }
    
    private void copySecurityFilesFromTargetDirectory(String nameOfFolder, String folderDestination) throws IOException {
        
        ReaderWriterData.copyFilesFromTo(nameOfFolder, folderDestination);
    }
    
    private void initBundle() {
        try {
            bundle = ResourceBundle.getBundle("conf/bundle_Rus");
        } catch (Exception ex1) {
            try {
                ReaderWriterData reader = new ReaderWriterData();
                reader.getStringResource("conf/bundle_Rus");
            } catch (Exception ex2) {
                logger.error("Error while getting bundle", ex2);
            }
        }
    }
    
    private void downloadMainJar() {
        try {
            ReaderWriterData.downloadFileAndWriteToTempFolder(tomographProperty.getProperty("URL_TO_MAIN_JAR"), tomographProperty.getProperty("PATH_TO_MAIN_JAR"));
        } catch (IOException ex) {
            logger.error("Error while downloading main jar in tmp folder", ex);
        }
    }
}
