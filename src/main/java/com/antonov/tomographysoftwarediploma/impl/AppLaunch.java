/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import com.antonov.tomographysoftwarediploma.controllers.HardwareModuleController;
import com.antonov.tomographysoftwarediploma.controllers.ModellingModuleController;
import com.antonov.tomographysoftwarediploma.viewSwing.TomographPane;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
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

        logger.info("=======Start TomographySoftware 1.0.0 application=======");

        Tomograph model = new Tomograph();
        ITomographView view = new TomographPane();
//        initLookAndFeel(view);

        ModellingModuleController mc = new ModellingModuleController(model, view);
        HardwareModuleController hc = new HardwareModuleController(model, view);

        model.setModellingController(mc);
        model.setHardwareController(hc);
        view.setModellingController(mc);
        view.setHardwareController(hc);
        view.initListeners();
        model.prepareViews();
        startView(view);


    }

    private static void initViewParameters(Frame frame) {

        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    private static void startView(final ITomographView view) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
//                    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
//                    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceRavenLookAndFeel");
//                    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel");
//                    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceTwilightLookAndFeel");
//                    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel");
                    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel");
                    SwingUtilities.updateComponentTreeUI((Frame) view);
                    ((Frame) view).setVisible(true);
                } catch (UnsupportedLookAndFeelException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException ex) {
                    java.util.logging.Logger.getLogger(AppLaunch.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    java.util.logging.Logger.getLogger(AppLaunch.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    java.util.logging.Logger.getLogger(AppLaunch.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
