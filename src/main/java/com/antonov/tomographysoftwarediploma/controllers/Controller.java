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
public abstract class Controller {

    protected Tomograph tomograph;
    protected ITomographView view;

    public Controller(Tomograph tomograph, ITomographView view) {
        this.tomograph = tomograph;
        this.view = view;
    }

    public void exitApplication() {
        tomograph.exitApplication();
    }

    public abstract void addPropertyChangeListenerToModel(PropertyChangeListener p);

    
}
