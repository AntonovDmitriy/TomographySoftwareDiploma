/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl.imageprocessing;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

/**
 *
 * @author Antonov
 */
public class ColoredStation {

    public static BufferedImage doColorOnImage(BufferedImage image, ColorFunctionNamesEnum colorEnum) {
        if (colorEnum.equals(ColorFunctionNamesEnum.blue)) {
            return getColorLutImage(image, LUTFunctions.blue());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.blue_saw_2)) {
            return getColorLutImage(image, LUTFunctions.blue_saw_2());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.blue_saw_4)) {
            return getColorLutImage(image, LUTFunctions.blue_saw_4());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.blue_saw_8)) {
            return getColorLutImage(image, LUTFunctions.blue_saw_8());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.blue_yellow)) {
            return getColorLutImage(image, LUTFunctions.blue_yellow());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.cyan)) {
            return getColorLutImage(image, LUTFunctions.cyan());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.gray128levels)) {
            return getColorLutImage(image, LUTFunctions.gray128levels());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.gray16levels)) {
            return getColorLutImage(image, LUTFunctions.gray16levels());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.gray2levels)) {
            return getColorLutImage(image, LUTFunctions.gray2levels());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.gray32levels)) {
            return getColorLutImage(image, LUTFunctions.gray32levels());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.gray4levels)) {
            return getColorLutImage(image, LUTFunctions.gray4levels());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.gray64levels)) {
            return getColorLutImage(image, LUTFunctions.gray64levels());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.gray8levels)) {
            return getColorLutImage(image, LUTFunctions.gray8levels());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.green)) {
            return getColorLutImage(image, LUTFunctions.green());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.green_blue_saw_2)) {
            return getColorLutImage(image, LUTFunctions.green_blue_saw_2());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.green_blue_saw_4)) {
            return getColorLutImage(image, LUTFunctions.green_blue_saw_4());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.green_blue_saw_8)) {
            return getColorLutImage(image, LUTFunctions.green_blue_saw_8());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.green_magenta)) {
            return getColorLutImage(image, LUTFunctions.green_magenta());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.green_saw_2)) {
            return getColorLutImage(image, LUTFunctions.green_saw_2());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.green_saw_4)) {
            return getColorLutImage(image, LUTFunctions.green_saw_4());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.green_saw_8)) {
            return getColorLutImage(image, LUTFunctions.green_saw_8());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.hue_bgr)) {
            return getColorLutImage(image, LUTFunctions.hue_bgr());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.hue_bgr_0)) {
            return getColorLutImage(image, LUTFunctions.hue_bgr_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.hue_brg)) {
            return getColorLutImage(image, LUTFunctions.hue_brg());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.hue_brg_0)) {
            return getColorLutImage(image, LUTFunctions.hue_brg_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.hue_gbr)) {
            return getColorLutImage(image, LUTFunctions.hue_gbr());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.hue_gbr_0)) {
            return getColorLutImage(image, LUTFunctions.hue_grb_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.hue_grb)) {
            return getColorLutImage(image, LUTFunctions.hue_grb());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.hue_grb_0)) {
            return getColorLutImage(image, LUTFunctions.hue_grb_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.hue_rbg)) {
            return getColorLutImage(image, LUTFunctions.hue_rbg());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.hue_rbg_0)) {
            return getColorLutImage(image, LUTFunctions.hue_rbg_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.hue_rgb)) {
            return getColorLutImage(image, LUTFunctions.hue_rgb());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.hue_rgb_0)) {
            return getColorLutImage(image, LUTFunctions.hue_rbg_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.invGray)) {
            return getColorLutImage(image, LUTFunctions.invGray());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.gray)) {
            return getColorLutImage(image, LUTFunctions.gray());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.magenta)) {
            return getColorLutImage(image, LUTFunctions.magenta());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.random_256)) {
            return getColorLutImage(image, LUTFunctions.random_256());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.random_32)) {
            return getColorLutImage(image, LUTFunctions.random_32());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.random_8)) {
            return getColorLutImage(image, LUTFunctions.random_8());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.red)) {
            return getColorLutImage(image, LUTFunctions.red());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.red_blue_saw_2)) {
            return getColorLutImage(image, LUTFunctions.red_blue_saw_2());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.red_blue_saw_4)) {
            return getColorLutImage(image, LUTFunctions.red_blue_saw_4());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.red_blue_saw_8)) {
            return getColorLutImage(image, LUTFunctions.red_blue_saw_8());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.red_cyan)) {
            return getColorLutImage(image, LUTFunctions.red_cyan());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.red_green_saw_2)) {
            return getColorLutImage(image, LUTFunctions.red_green_saw_2());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.red_green_saw_4)) {
            return getColorLutImage(image, LUTFunctions.red_green_saw_4());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.red_green_saw_8)) {
            return getColorLutImage(image, LUTFunctions.red_green_saw_8());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.red_saw_2)) {
            return getColorLutImage(image, LUTFunctions.red_saw_2());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.red_saw_4)) {
            return getColorLutImage(image, LUTFunctions.red_saw_4());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.red_saw_8)) {
            return getColorLutImage(image, LUTFunctions.red_saw_8());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sin_bgr)) {
            return getColorLutImage(image, LUTFunctions.sin_bgr());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sin_bgr_0)) {
            return getColorLutImage(image, LUTFunctions.sin_bgr_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sin_brg)) {
            return getColorLutImage(image, LUTFunctions.sin_brg());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sin_brg_0)) {
            return getColorLutImage(image, LUTFunctions.sin_brg_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sin_gbr)) {
            return getColorLutImage(image, LUTFunctions.sin_gbr());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sin_gbr_0)) {
            return getColorLutImage(image, LUTFunctions.sin_gbr_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sin_grb)) {
            return getColorLutImage(image, LUTFunctions.sin_grb());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sin_grb_0)) {
            return getColorLutImage(image, LUTFunctions.sin_grb_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sin_rgb_0)) {
            return getColorLutImage(image, LUTFunctions.sin_rbg_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sin_rgb)) {
            return getColorLutImage(image, LUTFunctions.sin_rgb());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sin_rbg)) {
            return getColorLutImage(image, LUTFunctions.sin_rbg());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sin_rbg_0)) {
            return getColorLutImage(image, LUTFunctions.sin_rbg_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sqrt_bgr)) {
            return getColorLutImage(image, LUTFunctions.sqrt_bgr());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sqrt_bgr_0)) {
            return getColorLutImage(image, LUTFunctions.sqrt_bgr_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sqrt_brg)) {
            return getColorLutImage(image, LUTFunctions.sqrt_brg());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sqrt_brg_0)) {
            return getColorLutImage(image, LUTFunctions.sqrt_brg_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sqrt_gbr)) {
            return getColorLutImage(image, LUTFunctions.sqrt_gbr());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sqrt_gbr_0)) {
            return getColorLutImage(image, LUTFunctions.sqrt_gbr_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sqrt_grb)) {
            return getColorLutImage(image, LUTFunctions.sqrt_grb());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sqrt_grb_0)) {
            return getColorLutImage(image, LUTFunctions.sqrt_grb_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sqrt_rgb_0)) {
            return getColorLutImage(image, LUTFunctions.sqrt_rbg_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sqrt_rgb)) {
            return getColorLutImage(image, LUTFunctions.sqrt_rgb());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sqrt_rbg)) {
            return getColorLutImage(image, LUTFunctions.sqrt_rbg());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.sqrt_rbg_0)) {
            return getColorLutImage(image, LUTFunctions.sqrt_rbg_0());
        } else if (colorEnum.equals(ColorFunctionNamesEnum.yellow)) {
            return getColorLutImage(image, LUTFunctions.yellow());
        }
        return null;
    }

    private static BufferedImage getColorLutImage(BufferedImage image, short[][] colors) {

        byte[] reds = new byte[256];
        byte[] greens = new byte[256];
        byte[] blues = new byte[256];
        for (int i = 0; i < 256; i++) {
            reds[i] = (byte) colors[i][0];
            greens[i] = (byte) colors[i][1];
            blues[i] = (byte) colors[i][2];
        }

        ColorModel colorModel = new IndexColorModel(8, 256, reds, greens, blues);
        WritableRaster wraster = image.getRaster();

        BufferedImage img = new BufferedImage(colorModel, wraster, false, null);
        return img;
    }
}
