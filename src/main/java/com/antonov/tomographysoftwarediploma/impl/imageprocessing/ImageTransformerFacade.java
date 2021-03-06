/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl.imageprocessing;

import static com.antonov.tomographysoftwarediploma.dblayer.TomographDaoImpl.logger;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class ImageTransformerFacade {

    private static Logger logger = LoggerFactory.getLogger(ImageTransformerFacade.class);

    public static BufferedImage prepareImage(BufferedImage image) {

        if (image.getType() != 13) {
            logger.trace("Type of image is TYPE_BYTE_INDEXED. Trying to make it gray");
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            image = op.filter(image, null);
        }

        return image;
    }

    public static BufferedImage createSinogram(IProjDataSaver model, BufferedImage initialImage, int scans, int stepSize, String regimeInterpolation) throws NumberWrongValueException, ImageWrongValueException {

        SinogramCreator sinogramCreator = new SinogramCreator();
        sinogramCreator.setDataModelling(scans, stepSize);
        BufferedImage sinogram = sinogramCreator.createSinogram(model, initialImage, regimeInterpolation);
        return sinogram;
    }

    public static double[][] createProjectionDataFromImage(BufferedImage initialImage, int scans, int stepSize) throws NumberWrongValueException, ImageWrongValueException {

        SinogramCreator sinogramCreator = new SinogramCreator();
        sinogramCreator.setDataModelling(scans, stepSize);
        return sinogramCreator.createProjectionData(initialImage);
    }

    public static BufferedImage reconstructModellingSinogram(BufferedImage sinogram, int scans, int stepSize, int sizeOfReconstruction, String filterName) throws NumberWrongValueException, ImageWrongValueException {
        return null;
    }

    public static BufferedImage reconstructProjectionData(double[][] projectionData, int scans, int stepSize, int sizeOfReconstruction, String filterName, String regimeInterpolation) throws NumberWrongValueException, ImageWrongValueException {

        Reconstructor reconstructor = new Reconstructor();
        reconstructor.setDataModelling(scans, stepSize);
        reconstructor.setDataReconstruction(sizeOfReconstruction, filterName);
        BufferedImage reconstruction = reconstructor.reconstructProjectionData(projectionData, regimeInterpolation);
        return reconstruction;
    }

    public static BufferedImage PerformWindowing(BufferedImage image) {

        BufferedImage result;
        int width = image.getWidth();
        int height = image.getHeight();

        if ((image.getType() == 11) || (image.getType() == 10)) {
            result = performGrayImage(image);
        } else {
            result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            result.createGraphics().drawImage(image, 0, 0, null);
        }
        return result;
    }

    private static int getUpperLevel(BufferedImage image) {

        int result;
        if (image.getType() == 11) {
            result = 2000;
        } else {
            result = 255;
        }
        return result;
    }

    private static BufferedImage performGrayImage(BufferedImage image) {

        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        int upperwinlvl = getUpperLevel(image);
        int lowerwinlvl = 0;
        int winwidth = upperwinlvl - lowerwinlvl;

        int[][] pixels = Utils.getIntArrayPixelsFromBufImg(image);

        WritableRaster wraster = result.getRaster();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
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
        return result;
    }

    public static BufferedImage doColorOnImage(BufferedImage image, ColorFunctionNamesEnum nameColor) {
        if (nameColor.equals(ColorFunctionNamesEnum.none)) {
            return image;
        } else {
            return ColoredStation.doColorOnImage(image, nameColor);
        }
    }

    public static List<BufferedImage> recontructProjectionDataSet(List<Object> setProjectionData, int scans, int stepSize, int sizeOfReconstruction, String filterName, String regimeInterpolation) throws NumberWrongValueException, ImageWrongValueException {

        List<BufferedImage> result = new ArrayList<>();
        int countImages = 1;
        int amountImages = setProjectionData.size();
        for (Object entry : setProjectionData) {
            double[][] projectionData = (double[][]) entry;
            BufferedImage image = reconstructProjectionData(projectionData, scans, stepSize, sizeOfReconstruction, filterName, regimeInterpolation);
            result.add(image);
            logger.trace("Amount of reconstructed image " + countImages + ". All is " + amountImages);
            countImages++;
        }
        return result;
    }
}
