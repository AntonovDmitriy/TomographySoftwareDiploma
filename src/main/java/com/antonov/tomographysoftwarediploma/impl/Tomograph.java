/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
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
    private Properties tomographProperty = new Properties();

    public ModellingModule modellingModule;

    public Tomograph() {
        initTomographProperty();
        this.modellingModule = new ModellingModule(tomographProperty);
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
            logger.warn("Can't load configuration file " + TOMOGRAPH_CONF_PATH, ex);
        }
    }


    public void prepareViews() {
        modellingModule.prepareView();
    }
    
}
