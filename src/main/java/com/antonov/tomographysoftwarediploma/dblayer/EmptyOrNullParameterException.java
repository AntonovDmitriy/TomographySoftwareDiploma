/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.dblayer;

/**
 *
 * @author Antonov
 */
public class EmptyOrNullParameterException extends Exception {

    EmptyOrNullParameterException(String string, Throwable ex) {
        super(string, ex);
    }

    EmptyOrNullParameterException(String object) {
        super(object);
    }
}
