/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.dblayer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Antonov
 */
public class PSetProjectionData {

    private Integer ID;
    private String PDNAME;
    private String PDDESCR;

    public String getPDDESCR() {
        return PDDESCR;
    }

    public void setPDDESCR(String PDDESCR) {

        String oldPDDESCR = this.PDDESCR;
        this.PDDESCR = PDDESCR;
        changeSupport.firePropertyChange("PDDESCR", oldPDDESCR, PDDESCR);
    }

    public String getPDNAME() {
        return PDNAME;
    }

    public void setPDNAME(String PDNAME) {
        String oldPDNAME = this.PDNAME;
        this.PDNAME = PDNAME;
        changeSupport.firePropertyChange("PDNAME", oldPDNAME, PDNAME);
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        Integer oldID = this.ID;
        this.ID = ID;
        changeSupport.firePropertyChange("ID", oldID, ID);
    }

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
