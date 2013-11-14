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
}
