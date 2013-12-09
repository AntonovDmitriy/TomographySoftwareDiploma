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
public class PInterpolation {

    private String nameInteprolation;
    private String Value;

    public String getValue() {
        return Value;
    }

    public void setValue(String Value) {
        this.Value = Value;
    }

    public String getNameInteprolation() {
        return nameInteprolation;
    }

    public void setNameInteprolation(String nameInteprolation) {
        this.nameInteprolation = nameInteprolation;
    }

    @Override
    public String toString() {
        return getNameInteprolation();
    }

    
}
