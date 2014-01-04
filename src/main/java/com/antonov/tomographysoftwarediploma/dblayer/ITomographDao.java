/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.dblayer;

import java.util.List;

/**
 *
 * @author Антоновы
 */
public interface ITomographDao {

    public void insertProjectionData(List<Object> projectionDataList);

}
