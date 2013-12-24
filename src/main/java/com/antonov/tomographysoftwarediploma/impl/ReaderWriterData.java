/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Antonov
 */
public class ReaderWriterData {

    public static BufferedImage getImageFromFileSystem(File file) throws IOException {
        
        return ImageIO.read(file);
    }

    public static void saveImageToFileSystem(BufferedImage image, File file, String filterImageDesc) throws IOException {

        String format = "";
        String name = file.getAbsolutePath();
        if (filterImageDesc.equals("JPEG File")) {
            String ext = ".jpeg";
            name = name + ext;
            format = "jpeg";
        } else if (filterImageDesc.equals("PNG File")) {
            String ext = ".png";
            name = name + ext;
            format = "PNG";
        } else if (filterImageDesc.equals("BMP File")) {
            String ext = ".bmp";
            name = name + ext;
            format = "BMP";
        } else if (filterImageDesc.equals("All Files")) {
            format = "";
        }
        
        ImageIO.write(image, format, new File(name));

    }
}
