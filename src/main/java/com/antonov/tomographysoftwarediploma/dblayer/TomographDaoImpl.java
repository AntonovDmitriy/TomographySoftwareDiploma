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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Антоновы
 */
public class TomographDaoImpl implements ITomographDao {

    private static String SQL_INSERT_SET_PROJECTION_DATA = "INSERT INTO set_projection_data (PDNAME,PDDESCR) VALUES (?,?)";
    private static String SQL_INSERT_PROJECTION_DATA = "INSERT INTO projection_data (DATA,ID_SET) VALUES (?,?)";
    private static final String SQL_SELECT_ALL_SET_PROJECTION_DATA = "SELECT * FROM set_projection_data";

    private static final String READ_OBJECT_SQL = "SELECT PROJECT_ARRAY FROM project_data WHERE NAME = ?";

    private static final String SSH_CONNECTION = "528d18a4e0b8cdb068000071@app-helloweb.rhcloud.com";
    private static final String DB_URL = "jdbc:mysql://localhost:1234/Tomo?"
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
        Session session = getSession();
        insertProjectionDataToDb(fileName, description, projectionDataList, session);

    }

    private Session getSession() throws JSchException, EmptyOrNullParameterException {
        logger.info("Trying to connect database through ssh........");
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
        session.setPortForwardingL(localPort, dbHost, remotePort);
        long finish = System.currentTimeMillis();
        long result = finish - start;
        logger.info("Connection to database successfully created. " + result + " sec.");
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
        ResultSet rs = null;
        try (Connection connect = DriverManager.getConnection(DB_URL);
                PreparedStatement psSet = connect.prepareStatement(SQL_INSERT_SET_PROJECTION_DATA, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement psProjectData = connect.prepareStatement(SQL_INSERT_PROJECTION_DATA)) {

            psSet.setString(1, fileName);
            psSet.setString(2, description);
            psSet.executeUpdate();

            logger.trace("Entry of set have been inserted " + fileName + " " + description);

            int setGeneratedIndex = 8;
            rs = psSet.getGeneratedKeys();

            if (rs.next()) {
                setGeneratedIndex = rs.getInt(1);
            }
            logger.trace("Primary key of set is " + setGeneratedIndex);

            for (Object entry : projectionDataList) {
                psProjectData.setObject(1, entry);
                psProjectData.setInt(2, setGeneratedIndex);
                psProjectData.addBatch();
            }

            psProjectData.executeBatch();
//            connect.commit();
            logger.info("All projection data have been inserted");
        } finally {

            if (rs != null) {
                rs.close();
            }
            session.disconnect();
        }
    }

    @Override
    public List<PSetProjectionData> selectAllSetProjectionData() throws JSchException, SQLException, EmptyOrNullParameterException {
        Session session = getSession();
        return selectAllSetProjectionDataFromDb(session);
    }

    private List<PSetProjectionData> selectAllSetProjectionDataFromDb(Session session) throws SQLException {

        logger.info("Querying sets projection data from db......");
        long start = System.currentTimeMillis();
        List<PSetProjectionData> result = new ArrayList<PSetProjectionData>();

        ResultSet rs = null;
        try (Connection connect = DriverManager.getConnection(DB_URL);
                Statement st = connect.createStatement()) {

            rs = st.executeQuery(SQL_SELECT_ALL_SET_PROJECTION_DATA);

            while (rs.next()) {
                PSetProjectionData entry = new PSetProjectionData();
                entry.setID(rs.getInt("ID"));
                entry.setPDNAME(rs.getString("PDNAME"));
                entry.setPDDESCR(rs.getString("PDDESCR"));
                result.add(entry);
            }
        } finally {

            if (rs != null) {
                rs.close();
            }
            session.disconnect();
        }
        long finish = System.currentTimeMillis();
        long resultTime = finish - start;
        logger.info("Projection data sets have been loaded. " + resultTime + " sec");
        return result;
    }
}
