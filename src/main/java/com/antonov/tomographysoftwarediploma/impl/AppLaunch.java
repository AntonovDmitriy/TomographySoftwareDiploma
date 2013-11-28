/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import com.antonov.tomographysoftwarediploma.controllers.HardwareModuleController;
import com.antonov.tomographysoftwarediploma.controllers.ModellingModuleController;
import com.antonov.tomographysoftwarediploma.forms.TomographPane;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class AppLaunch {

    private static Logger logger = LoggerFactory.getLogger(AppLaunch.class);

    public static void main(String[] args) {

        logger.info("=======Start TomographySoftware 1.0.0 application=======");
        Tomograph model = new Tomograph();
        initLookAndFeel();
        ITomographView view = new TomographPane();
        ModellingModuleController mc = new ModellingModuleController(model, view);
        HardwareModuleController hc = new HardwareModuleController(model, view);

        model.setModellingController(mc);
        model.setHardwareController(hc);
        view.setModellingController(mc);
        view.setHardwareController(hc);
        view.initListeners();
        model.prepareViews();
        ((Frame) view).setVisible(true);

    }

    private static void initViewParameters(Frame frame) {

        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    private static void initLookAndFeel() {
        try {
//            UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");

            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.error("Error occured when starting LAF", ex);
        }

    }
}
