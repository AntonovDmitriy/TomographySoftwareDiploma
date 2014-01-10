/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.dblayer;

import com.antonov.tomographysoftwarediploma.impl.ScanningEmulator;
import com.antonov.tomographysoftwarediploma.viewSwing.TomographPane;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Антоновы
 */
public class TomographDaoImpl implements ITomographDao {

    private static String SQL_INSERT_PROJECTION_DATA = "INSERT INTO project_data (DATE, NAME,DESCRIPTION,PROJECT_ARRAY) VALUES(?, ?,?,?)";
    
    private static final String READ_OBJECT_SQL = "SELECT PROJECT_ARRAY FROM project_data WHERE NAME = ?";

    private static final String SSH_CONNECTION = "528d18a4e0b8cdb068000071@app-helloweb.rhcloud.com";
    private static final String DB_URL = "jdbc:mysql://localhost:1234/mysql?"
                + "user=adminR8QufFz&password=a3ZixG3aDcJG";
    
    private Properties properties;
    public static Logger logger = LoggerFactory.getLogger(TomographDaoImpl.class);

    private String pathToPrivateKey;
    private String dbHost;
    private int localPort;
    private int remotePort;

    public TomographDaoImpl(Properties p) {
        this.properties = p;
    }

    @Override
    public void insertProjectionData(String fileName, String description, List<Object> projectionDataList) throws JSchException, SQLException, EmptyOrNullParameterException {
        logger.trace("Reading dbParameters");
        readInitialDbParameters();
//        try {
//            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/testing?"
//                    + "user=root&password=ProL1ant");
//
//            Calendar calendar = Calendar.getInstance();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//
//            PreparedStatement pstmt = connect.prepareStatement(SQL_INSERT_PROJECTION_DATA);
//            pstmt.setString(1, dateFormat.format(calendar.getTime()).toString());
//            pstmt.setString(2, name);
//            pstmt.setString(3, description);
//            pstmt.setObject(4, projArrayList);
//            pstmt.executeUpdate();
//
//        } catch (SQLException ex) {
//            Logger.getLogger(TomographPane.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        

        Session session = getSession(pathToPrivateKey, dbHost, localPort, remotePort);
        insertProjectionDataToDb(fileName, description, projectionDataList, session);

    }

//    public static void main(String[] args) throws JSchException, SQLException {
//
//        TomographDaoImpl t = new TomographDaoImpl();
//        t.insertProjectionData(null);
//    }
    private Session getSession(String pathToPrivateKey, String dbHost, int localPort, int remotePort) throws JSchException {

        JSch jsch = new JSch();
        jsch.addIdentity((new File(pathToPrivateKey)).getAbsolutePath());

        String host = SSH_CONNECTION;

        String user = host.substring(0, host.indexOf('@'));
        host = host.substring(host.indexOf('@') + 1);

        Session session = jsch.getSession(user, host, 22);

        UserInfo ui = new TomoUserInfo();

        session.setUserInfo(ui);
        session.connect();
        session.setPortForwardingL(localPort, dbHost, remotePort);
        return session;
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

    private void insertProjectionDataToDb(String fileName, String description, List<Object> projectionDataList, Session session) throws SQLException {

        Connection connect = DriverManager.getConnection(DB_URL);

        Statement st = connect.createStatement();
        st.execute("INSERT INTO  `Tomo`.`test` (\n"
                + "`id` ,\n"
                + "`name`\n"
                + ")\n"
                + "VALUES (\n"
                + "'2',  'second'\n"
                + ");");
    }
}
