/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

/**
 *
 * @author Antonov
 */
public class ImageTransformer {

//    public static BufferedImage makeImageGray(BufferedImage image){
//        //                Image gray = new BufferedImage(imgBuf.getWidth(), imgBuf.getHeight(),
////                        BufferedImage.TYPE_BYTE_GRAY);
//    }
    public static BufferedImage prepareImage(BufferedImage image) {

        if (image.getType() != 13) {
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            image = op.filter(image, null);
        }

        return image;
    }
}
