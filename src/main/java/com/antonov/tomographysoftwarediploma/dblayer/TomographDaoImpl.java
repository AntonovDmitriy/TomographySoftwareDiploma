/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.dblayer;

import com.antonov.tomographysoftwarediploma.viewSwing.TomographPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import org.slf4j.Logger;

/**
 *
 * @author Антоновы
 */
public class TomographDaoImpl implements ITomographDao {

    private static String SQL_INSERT_PROJECTION_DATA = "INSERT INTO project_data (DATE, NAME,DESCRIPTION,PROJECT_ARRAY) VALUES(?, ?,?,?)";
    private static final String READ_OBJECT_SQL = "SELECT PROJECT_ARRAY FROM project_data WHERE NAME = ?";

    @Override
    public void insertProjectionData(List<Object> projectionDataList) {

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
    }

}
