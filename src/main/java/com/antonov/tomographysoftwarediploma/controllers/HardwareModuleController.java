/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.antonov.tomographysoftwarediploma.controllers;

import com.antonov.tomographysoftwarediploma.impl.ITomographView;
import com.antonov.tomographysoftwarediploma.impl.Tomograph;
import java.beans.PropertyChangeListener;

/**
 *
 * @author Antonov
 */
public class HardwareModuleController extends Controller{

    public HardwareModuleController(Tomograph tomograph, ITomographView view) {
        super(tomograph, view);
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
