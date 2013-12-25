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
public class FileNameExtensionFilter extends javax.swing.filechooser.FileFilter {

    private String extenName;
    private String exten;

    FileNameExtensionFilter(String extenName, String exten) {
        this.extenName = extenName;
        this.exten = exten;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String name = f.getName();
        if (name.matches(".*(" + exten + ")")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getDescription() {
        return extenName;
    }
}
