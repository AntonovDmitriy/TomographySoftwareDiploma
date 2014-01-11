/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.dblayer;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class ConnectionManagerImpl implements IConnectionManager {

    private Properties properties;
    private Session sessionSSH;
    private Connection connectionDb;

    private String pathToPrivateKey;
    private String dbHost;
    private int localPort;
    private int remotePort;
    public static Logger logger = LoggerFactory.getLogger(ConnectionManagerImpl.class);

    private static final String SSH_CONNECTION = "528d18a4e0b8cdb068000071@app-helloweb.rhcloud.com";
    private static final String DB_URL = "jdbc:mysql://localhost:1234/Tomo?"
            + "user=adminR8QufFz&password=a3ZixG3aDcJG";

    public ConnectionManagerImpl(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Connection getConnection() {

        return this.connectionDb;
    }

    @Override
    public Session getSSHSession() {
        return this.sessionSSH;
    }

    @Override
    public void disconnect() {

        this.sessionSSH.disconnect();
        logger.info("ConnectionManager state : connected");
    }

    @Override
    public void connect() throws JSchException, EmptyOrNullParameterException, Exception {

        createSSHSession();
        createDbConnection();
        logger.info("ConnectionManager state : connected");
    }

    private void createSSHSession() throws JSchException, EmptyOrNullParameterException, Exception {
        logger.info("Trying to create ssh connection........");
        long start = System.currentTimeMillis();

        readInitialDbParameters();

        JSch jsch = new JSch();
        jsch.addIdentity((new File(pathToPrivateKey)).getAbsolutePath());

        String host = SSH_CONNECTION;

        String user = host.substring(0, host.indexOf('@'));
        host = host.substring(host.indexOf('@') + 1);

        Session session = jsch.getSession(user, host, 22);

        UserInfo ui = new TomoUserInfo();

        session.setUserInfo(ui);
        session.connect();
        session.sendKeepAliveMsg();

        session.setPortForwardingL(localPort, dbHost, remotePort);
        long finish = System.currentTimeMillis();
        long result = finish - start;
        logger.info("SSH Connection successfully created. " + result + " sec.");
        this.sessionSSH = session;
    }

    private void readInitialDbParameters() throws EmptyOrNullParameterException {

        String pathToPrivateKey = properties.getProperty("SSH_PRIVATE_KEY_PATH");
        if (pathToPrivateKey != null && !pathToPrivateKey.isEmpty()) {
            logger.trace("SSH_PRIVATE_KEY_PATH " + pathToPrivateKey);
            this.pathToPrivateKey = pathToPrivateKey;
        } else {
            logger.warn("Error while reading SSH_PRIVATE_KEY_PATH. It is null or empty");
            throw new EmptyOrNullParameterException("Error while reading SSH_PRIVATE_KEY_PATH. It is null or empty");
        }

        String dbHost = properties.getProperty("DB_HOST");
        if (dbHost != null && !dbHost.isEmpty()) {
            logger.trace("DB_HOST " + dbHost);
            this.dbHost = dbHost;
        } else {
            logger.warn("Error while reading DB_HOST. It is null or empty");
            throw new EmptyOrNullParameterException("Error while reading DB_HOST. It is null or empty");
        }

        String localPortString = properties.getProperty("PORT_LOCAL");
        if (localPortString != null && !localPortString.isEmpty()) {
            int localPort = Integer.parseInt(localPortString);

            logger.trace("PORT_LOCAL " + localPort);
            this.localPort = localPort;
        } else {
            logger.warn("Error while reading PORT_LOCAL. It is null or empty");
            throw new EmptyOrNullParameterException("Error while reading PORT_LOCAL. It is null or empty");
        }

        String remotePortString = properties.getProperty("PORT_REMOTE");
        if (remotePortString != null && !remotePortString.isEmpty()) {
            int remotePort = Integer.parseInt(remotePortString);

            logger.trace("PORT_REMOTE " + remotePort);
            this.remotePort = remotePort;
        } else {
            logger.warn("Error while reading PORT_REMOTE. It is null or empty");
            throw new EmptyOrNullParameterException("Error while reading PORT_REMOTE. It is null or empty");
        }
    }

    private void createDbConnection() throws SQLException {

        Connection connect = DriverManager.getConnection(DB_URL, generatePropsForJdbcDriver());
        this.connectionDb = connect;
    }

    private Properties generatePropsForJdbcDriver() {
        Properties props = new Properties();
        props.put("useUnicode", "true");
        props.put("characterEncoding", "Cp1251");
        return props;
    }
}
