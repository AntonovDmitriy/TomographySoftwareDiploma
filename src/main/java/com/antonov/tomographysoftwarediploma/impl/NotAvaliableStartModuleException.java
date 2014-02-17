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
public class NotAvaliableStartModuleException extends Exception {

    NotAvaliableStartModuleException(String string, Throwable ex) {
        super(string, ex);
    }

    NotAvaliableStartModuleException(String string) {
        super(string);
    }
}
