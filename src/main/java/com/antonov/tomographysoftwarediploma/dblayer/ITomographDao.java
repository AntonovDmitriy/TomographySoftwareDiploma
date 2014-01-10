/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.dblayer;

import com.jcraft.jsch.JSchException;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Антоновы
 */
public interface ITomographDao {

    public void insertProjectionData(String name, String description,List<Object> projectionDataList) throws JSchException, SQLException,EmptyOrNullParameterException;

}
