/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.viewSwing;

import java.io.File;

/**
 *
 * @author Antonov
 */
public class ImageFilter extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String name = f.getName();
        return name.matches(".*((.jpg)|(.gif)|(.png)|(.bmp))");
    }

    @Override
    public String getDescription() {
        return "Image files (*.jpg, *.gif, *.png, *.bmp)";
    }
}
