/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import com.antonov.tomographysoftwarediploma.forms.TomographPane;
import java.awt.Frame;
import javax.swing.UIManager;
import org.apache.log4j.Logger;

/**
 *
 * @author Antonov
 */
public class AppLaunch {

    public static void main(String[] args) {

        Logger logger = Logger.getLogger(AppLaunch.class.getName());
//        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
//        ColorConvertOp op = new ColorConvertOp(cs, null);
//        System.out.println(System.getProperty("user.dir"));
        logger.info("=======Start TomographySoftware 1.0.0 application=======");
        Tomograph model = new Tomograph();
//        TomographPane view = new TomographPane();
//        initViewParameters(view);
//        view.setVisible(true);
        logger.info("=======Stop TomographySoftware 1.0.0 application=======");

    }

    private static void initViewParameters(Frame frame) {

        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.out.println(ex);
        }
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    }
}
