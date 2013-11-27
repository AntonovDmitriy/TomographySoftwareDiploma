/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.controllers;

import com.antonov.tomographysoftwarediploma.impl.ITomographView;
import com.antonov.tomographysoftwarediploma.impl.Tomograph;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Map;

/**
 *
 * @author Antonov
 */
public class ModellingModuleController extends Controller {

    public ModellingModuleController(Tomograph tomograph, ITomographView view) {
        super(tomograph, view);
    }

    public void setModellingImages(Map<String, BufferedImage> imageSamplesMapWithNames) {
        super.view.setModellingImages(imageSamplesMapWithNames);
    }

    public void setModelCurrentModellingImageByName(String image) {
        
        super.tomograph.modellingModule.setCurrentModellingImageByName(image);
    }

    public void getAndSetFileModellingImage(File file){
        super.tomograph.modellingModule.getAndSetFileModellingImage(file);
    }
    
    @Override
    public void setPropertyChangeListener(PropertyChangeListener p) {
        super.tomograph.modellingModule.addPropertyChangeListener(p);
    }
}
