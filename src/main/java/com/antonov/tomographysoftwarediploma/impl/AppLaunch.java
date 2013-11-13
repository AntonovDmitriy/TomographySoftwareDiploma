/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import com.antonov.tomographysoftwarediploma.forms.TomographPane;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class AppLaunch {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(AppLaunch.class);
//        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
//        ColorConvertOp op = new ColorConvertOp(cs, null);
//        System.out.println(System.getProperty("user.dir"));
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
        model.prepareViews();
//        initViewParameters((Frame) view);
        ((Frame) view).setVisible(true);

    }

    private static void initViewParameters(Frame frame) {

        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    private static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.out.println(ex);
        }
    }
}
