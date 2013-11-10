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
import org.apache.log4j.Logger;

/**
 *
 * @author Antonov
 */
public class Tomograph {

    private Logger logger = Logger.getLogger(Tomograph.class.getName());
    private static final String TOMOGRAPH_CONF_PATH = "conf/tomograph.conf";
    private Properties tomographProperty = new Properties();

    public ModellingModule modellingModule;

    public Tomograph() {
        initTomographProperty();
        this.modellingModule = new ModellingModule(tomographProperty);
    }

    private void initTomographProperty() {
        try (InputStream is = new FileInputStream(TOMOGRAPH_CONF_PATH)) {
            tomographProperty.load(is);
            logger.trace("Successfully loading config file " + TOMOGRAPH_CONF_PATH);

        } catch (IOException ex) {
            logger.error("Can't load configuration file " + TOMOGRAPH_CONF_PATH, ex);
        }
    }

}
