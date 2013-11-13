/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.antonov.tomographysoftwarediploma.impl;

/**
 *
 * @author Antonov
 */
public class Controller {

    protected Tomograph tomograph;
    protected ITomographView view;
    
    public Controller(Tomograph tomograph, ITomographView view) {
        this.tomograph = tomograph;
        this.view = view;
    }
    
}
