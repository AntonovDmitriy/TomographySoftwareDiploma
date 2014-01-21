/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author Antonov
 */
public class ReaderWriterData {

    public static BufferedImage getImageFromFileSystem(File file) throws IOException {

        return ImageIO.read(file);
    }

    public static List<BufferedImage> getAllImagesFromFileSystem(File pathTo) throws IOException {

        List<BufferedImage> result = new ArrayList<>();
        File[] listOfFiles = pathTo.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                BufferedImage img = ImageIO.read(file);
                result.add(img);
            }
        }
        return result;
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

    public static void writeDoubleArrayToTextFile(double[][] array, String nameFile) {

        BufferedWriter writer = null;
        try {
            File file = new File(nameFile);

            writer = new BufferedWriter(new FileWriter(file));
            for (double[] row : array) {
                writer.write("\n");
                for (double entry : row) {
                    writer.write(entry + " ");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    static void isFolderExists(String javaSecurityFolder) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
