/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.dblayer;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.sql.Connection;

/**
 *
 * @author Antonov
 */
public interface IConnectionManager {

    Connection getConnection();

    Session getSSHSession();

    void disconnect();
    
    void connect() throws JSchException, EmptyOrNullParameterException, Exception;
}
