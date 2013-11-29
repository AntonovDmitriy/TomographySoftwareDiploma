/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.imageprocessing;

import com.antonov.tomographysoftwarediploma.impl.Tomograph;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class ImageTransformer {

    private static Logger logger = LoggerFactory.getLogger(Tomograph.class);

    public static BufferedImage prepareImage(BufferedImage image) {

        if (image.getType() != 13) {
            logger.trace("Type of image is TYPE_BYTE_INDEXED. Trying to make it gray");
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            image = op.filter(image, null);
        }

        return image;
    }

    public static BufferedImage createSinogram(BufferedImage initialImage, int views, int stepSize) {
        
        SinogramCreator sinogramCreator = new SinogramCreator();
        
    }

}
