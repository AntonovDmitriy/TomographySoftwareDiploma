/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.imageprocessing;

import com.antonov.tomographysoftwarediploma.Utils;
import com.antonov.tomographysoftwarediploma.impl.Tomograph;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.WritableRaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class ImageTransformerFacade {

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

    public static BufferedImage createSinogram(BufferedImage initialImage, int views, int stepSize) throws NumberWrongValueException, ImageWrongValueException {

        SinogramCreator sinogramCreator = new SinogramCreator();
        sinogramCreator.setDataModelling(initialImage, views, stepSize);
        BufferedImage sinogram = sinogramCreator.createSinogram();
        return sinogram;
    }

    public static BufferedImage PerformWindowing(BufferedImage mBufferedImage) {

        int[][] pixels = Utils.getIntArrayPixelsFromBufImg(mBufferedImage);
        int iw = mBufferedImage.getWidth();
        int ih = mBufferedImage.getHeight();
        BufferedImage windowedImage;

        int upperwinlvl;
        int lowerwinlvl = 0;

        if (mBufferedImage.getType() == 11) {
            upperwinlvl = 2000;
        } else {
            upperwinlvl = 255;
        }
        int winwidth = upperwinlvl - lowerwinlvl;

        if ((mBufferedImage.getType() == 11)
                || (mBufferedImage.getType() == 10)) {
            windowedImage = new BufferedImage(iw, ih,
                    BufferedImage.TYPE_BYTE_GRAY);

            WritableRaster wraster = windowedImage.getRaster();
            for (int x = 0; x < iw; x++) {
                for (int y = 0; y < ih; y++) {
                    // int val = wraster.getSample(x, y, 0);
                    int val = pixels[x][y];
                    if (val <= lowerwinlvl) {
                        wraster.setSample(x, y, 0, 0);
                    } else if (val >= upperwinlvl) {
                        wraster.setSample(x, y, 0, 255);
                    } else {
                        int newval = (val - lowerwinlvl) * 256 / winwidth;
                        wraster.setSample(x, y, 0, newval);
                    }
                }
            }
        } else {
            windowedImage = new BufferedImage(iw, ih,
                    BufferedImage.TYPE_INT_RGB);
            windowedImage.createGraphics()
                    .drawImage(mBufferedImage, 0, 0, null);

        }
        return windowedImage;
    }
}
