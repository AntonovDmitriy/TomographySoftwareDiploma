/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl.imageprocessing;

/**
 *
 * @author Antonov
 */
public class ImageWrongValueException extends Exception {

    ImageWrongValueException(String string, Throwable ex) {
        super(string, ex);
    }

    ImageWrongValueException(String object) {
        super(object);
    }
}
