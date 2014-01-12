/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.dblayer;

import com.jcraft.jsch.JSchException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
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
    private static String SQL_SELECT_PROJECTION_DATA_BY_ID = "SELECT DATA FROM projection_data WHERE ID=?";
    private static String SQL_SELECT_PROJECTION_DATA_ID_BY_ID_SET = "SELECT ID FROM projection_data WHERE ID_SET=?";

    private Properties properties;
    private IConnectionManager connectionManager;

    public static Logger logger = LoggerFactory.getLogger(TomographDaoImpl.class);

    public TomographDaoImpl(Properties p, IConnectionManager connectionManager) {
        this.properties = p;
        this.connectionManager = connectionManager;
    }

    @Override
    public void insertProjectionData(String fileName, String description, List<Object> projectionDataList) throws JSchException, SQLException, EmptyOrNullParameterException {

        insertProjectionDataToDb(fileName, description, projectionDataList);
    }

    private void insertProjectionDataToDb(String fileName, String description, List<Object> projectionDataList) throws SQLException {
        ResultSet rs = null;
        Connection connect = this.connectionManager.getConnection();

        try (
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
        }
    }

    @Override
    public List<PSetProjectionData> selectAllSetProjectionData() throws JSchException, SQLException, EmptyOrNullParameterException {
        return selectAllSetProjectionDataFromDb();
    }

    private List<PSetProjectionData> selectAllSetProjectionDataFromDb() throws SQLException {

        logger.info("Querying sets projection data from db......");
        long start = System.currentTimeMillis();
        List<PSetProjectionData> result = new ArrayList<PSetProjectionData>();

        ResultSet rs = null;
        Connection connect = this.connectionManager.getConnection();
        try (
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
        }
        long finish = System.currentTimeMillis();
        long resultTime = finish - start;
        logger.info("Projection data sets have been loaded. " + resultTime + " sec");
        return result;
    }

    @Override
    public List<Object> getSetProjectionData(PSetProjectionData pojo) throws JSchException, SQLException, EmptyOrNullParameterException, IOException, ClassNotFoundException {

        List<Object> result = new ArrayList<>();
        logger.info("Querying set projection data from db......");
        long start = System.currentTimeMillis();

        List<Integer> listIdOfProjectionData = getAllIdOfSetProjectionData(pojo);
        Connection connect = this.connectionManager.getConnection();
        
        int countSets = 1;
        for (Integer id : listIdOfProjectionData) {
            ResultSet rs = null;
            try (
                    PreparedStatement ps = connect.prepareStatement(SQL_SELECT_PROJECTION_DATA_BY_ID)) {
                ps.setInt(1, id);
                rs = ps.executeQuery();

                
                if (rs.next()) {
                    byte[] arrayBytes = rs.getBytes(1);
//                    rs.getBlob(1);
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(arrayBytes));
                    Object entry = ois.readObject();
                    ois.close();
                    result.add(entry);
                    logger.trace("Amount of downloaded project data sets " + countSets);
                    countSets++;
                }
            } finally {

                if (rs != null) {
                    rs.close();
                }
            }
        }
        long finish = System.currentTimeMillis();
        long resultTime = finish - start;
        logger.info("Projection data set have been loaded. " + resultTime + " sec");
        return result;
    }

    private List<Integer> getAllIdOfSetProjectionData(PSetProjectionData pojo) throws SQLException {

        List<Integer> result = new ArrayList<>();
        ResultSet rs = null;
        Connection connect = this.connectionManager.getConnection();
        try (
                PreparedStatement ps = connect.prepareStatement(SQL_SELECT_PROJECTION_DATA_ID_BY_ID_SET)) {
            ps.setInt(1, pojo.getID());
            rs = ps.executeQuery();

            while (rs.next()) {
                Integer entry = rs.getInt(1);
                result.add(entry);
            }
        } finally {

            if (rs != null) {
                rs.close();
            }
        }
        return result;
    }
}
