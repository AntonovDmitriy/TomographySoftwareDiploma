/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Antonov
 */
class ModellingModule {

    //Map for storage images for modelling
    private Map<String, BufferedImage> imageSamplesMapWithNames = new HashMap<>();

    public void setImageSamplesMap(Map<String, BufferedImage> map) {
        this.imageSamplesMapWithNames = map;
    }

    public Map<String, BufferedImage> getImageSampleMap() {
        return this.imageSamplesMapWithNames;
    }

    public ModellingModule() {

        initSamplesMapImage();
    }

    private void initSamplesMapImage() {

        ColorSpace grayColorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(grayColorSpace, null);
        
    }

}
