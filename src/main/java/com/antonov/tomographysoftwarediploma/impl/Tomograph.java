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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Antonov
 */
public class Tomograph {
    private static final String TOMOGRAPH_CONF_PATH = "conf/tomograph.conf";
    private Properties tomographProperty = new Properties();
    public ModellingModule modellingModule = new ModellingModule();

    public Tomograph() {
        initTomographProperty();
    }

    private void initTomographProperty() {
        try (InputStream is = new FileInputStream(TOMOGRAPH_CONF_PATH)) {
            tomographProperty.load(is);
            System.out.println(tomographProperty.getProperty("PATH_MODELLING_IMAGES"));
        } catch (IOException ex) {
            Logger.getLogger(Tomograph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
