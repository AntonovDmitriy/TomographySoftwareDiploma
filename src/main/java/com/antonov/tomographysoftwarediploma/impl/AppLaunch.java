/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import com.antonov.tomographysoftwarediploma.Tomograph_MEPHI;
import static com.antonov.tomographysoftwarediploma.Tomograph_MEPHI.models;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.UIManager;

/**
 *
 * @author Antonov
 */
public class AppLaunch {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");

        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (InstantiationException ex) {
            System.out.println(ex);
        } catch (IllegalAccessException ex) {
            System.out.println(ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            System.out.println(ex);
        }
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);

        Tomograph_MEPHI frame = new Tomograph_MEPHI();

        frame.setVisible(true);
        frame.setExtendedState(frame.MAXIMIZED_BOTH);

    }
}
