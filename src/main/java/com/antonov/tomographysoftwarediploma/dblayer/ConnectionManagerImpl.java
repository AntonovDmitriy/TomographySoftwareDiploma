/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.dblayer;

import com.antonov.tomographysoftwarediploma.impl.ReaderWriterData;
import com.antonov.tomographysoftwarediploma.impl.Tomograph;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
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

    private boolean isConnected = false;

    Tomograph.StartModeEnum mode;

    public ConnectionManagerImpl(Properties properties, Tomograph.StartModeEnum mode) throws IOException, EmptyOrNullParameterException {
        this.mode = mode;
        this.properties = properties;
        readInitialDbParameters();
        initConnectionFile();
    }

    @Override
    public Connection getConnection() {

        try {
            if (this.connectionDb.isClosed()) {
                connect();
            }
        } catch (Exception ex) {
        }
        return this.connectionDb;

    }

    @Override
    public Session getSSHSession() {
        return this.sessionSSH;
    }

    @Override
    public void disconnect() {

        try {
            this.connectionDb.close();
        } catch (SQLException ex) {
        }
        this.sessionSSH.disconnect();
        logger.info("ConnectionManager state : disconnected");
    }

    @Override
    public void connect() throws JSchException, EmptyOrNullParameterException, Exception {

        createSSHSession();
        createDbConnection();
        logger.info("ConnectionManager state : connected");
        this.isConnected = true;
    }

    private void createSSHSession() throws JSchException, EmptyOrNullParameterException, Exception {
        logger.info("Trying to create ssh connection........");
        long start = System.currentTimeMillis();

        JSch jsch = new JSch();
        logger.info((new File(pathToPrivateKey)).getAbsolutePath());

        jsch.addIdentity((new File(pathToPrivateKey)).getAbsolutePath());

        String host = properties.getProperty("SSH_CONNECTION");

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

        Connection connect = DriverManager.getConnection(properties.getProperty("DB_CONNECTION"), generatePropsForJdbcDriver());
        this.connectionDb = connect;
    }

    private Properties generatePropsForJdbcDriver() {
        Properties props = new Properties();
        props.put("useUnicode", "true");
        props.put("characterEncoding", "Cp1251");
        return props;
    }

    private void initConnectionFile() throws IOException {

        File privateKey = new File(pathToPrivateKey);
        if (!privateKey.exists()) {
            ReaderWriterData reader = new ReaderWriterData();
            if (mode.equals(Tomograph.StartModeEnum.MODE_WEBSTART)) {
                reader.extractResourceToFile(pathToPrivateKey, properties, true, false);
            } else {
                reader.extractResourceToFile(pathToPrivateKey, properties, false, false);
            }
        }
    }

    @Override
    public boolean isConnected() {

        return isConnected;
    }
}
