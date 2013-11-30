/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import com.antonov.tomographysoftwarediploma.controllers.HardwareModuleController;
import com.antonov.tomographysoftwarediploma.controllers.ModellingModuleController;
import com.antonov.tomographysoftwarediploma.viewSwing.TomographPane;
import java.awt.Frame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class AppLaunch {

    private static Logger logger = LoggerFactory.getLogger(AppLaunch.class);

    public static void main(String[] args) {
        
        initLAF();
        Tomograph model = new Tomograph();
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

//    private static void initViewParameters(Frame frame) {
//
//        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
//    }

    private static void initLAF() {
        logger.info("=======Start TomographySoftware 1.0.0 application=======");
        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
////                    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceRavenLookAndFeel");
////                    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel");
////                    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceTwilightLookAndFeel");
////                    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel");
//                    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel");
//                    SwingUtilities.updateComponentTreeUI((Frame) view);
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            logger.error("Error occured while setting look and feel");
        }

    }
}
