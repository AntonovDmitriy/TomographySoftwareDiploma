/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import com.antonov.tomographysoftwarediploma.controllers.HardwareModuleController;
import com.antonov.tomographysoftwarediploma.controllers.ModellingModuleController;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 *
 * @author Antonov
 */
public interface ITomographView {

    public void initClosingOperations();
    
    public void fillModelNames(Map<String, BufferedImage> imageSamplesMapWithNames);
    
    public void initModelList();
    
    public void setModellingController(ModellingModuleController controller);

    public void setHardwareController(HardwareModuleController controller);

    public void setModellingImages(Map<String, BufferedImage> imageSamplesMapWithNames);

    public void setCurrentModellingImage(BufferedImage image);

    public void setSinogramImage(BufferedImage image);
    
    public void clearResultModelling();

    public void disableModellingControls();

    public void enableReconControls();
    
    public void initListeners();
    
    public void startCalculating();
    
    public void stopCalculating();
    
    public void showInternalErrorMessage(String messageError);
    
    public void showWarningMessage(String messageWarning);
}
