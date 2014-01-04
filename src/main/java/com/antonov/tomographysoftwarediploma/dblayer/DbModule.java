/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.dblayer;

import com.antonov.tomographysoftwarediploma.viewSwing.TomographPane;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Antonov
 */
public class DbModule {

    private static final Properties props = new Properties();

    static {
        props.put("useUnicode", "true");
        props.put("characterEncoding", "UTF-8");
    }
    private static String WRITE_OBJECT_SQL = "INSERT INTO project_data (DATE, NAME,DESCRIPTION,PROJECT_ARRAY) VALUES(?, ?,?,?)";
    private static final String READ_OBJECT_SQL = "SELECT PROJECT_ARRAY FROM project_data WHERE NAME = ?";

    public static List getProjDataSet(String name) {
        ObjectInputStream ois = null;


        try {
            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/testing?"
                    + "user=root&password=ProL1ant", props);

            PreparedStatement pstmt = connect.prepareStatement(READ_OBJECT_SQL);
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            byte[] arrayBytes = rs.getBytes(1);
            ois = new ObjectInputStream(new ByteArrayInputStream(arrayBytes));
            rs.close();
            pstmt.close();
            List projDataSet = (List) ois.readObject();

            return projDataSet;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbModule.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DbModule.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DbModule.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(DbModule.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;

    }

    public static void main(String[] args) {

        //       String SQL = "INSERT INTO project_data (DATE, NAME,DESCRIPTION) VALUES('2.05.2013', 'proj1','Проекционные данные 2')";


        try {


            // Statement statement = connect.createStatement();
            /*
             statement.executeUpdate(SQL);
             statement.executeUpdate("INSERT INTO project_data (DATE, NAME,DESCRIPTION) VALUES('2.05.2013', 'proj2','Проекционные данные 2')");
             statement.executeUpdate("INSERT INTO project_data (DATE, NAME,DESCRIPTION) VALUES('3.05.2013', 'proj3','Проекционные данные 3')");
             statement.executeUpdate("INSERT INTO project_data (DATE, NAME,DESCRIPTION) VALUES('4.05.2013', 'proj4','Проекционные данные 4')");
             statement.executeUpdate("INSERT INTO project_data (DATE, NAME,DESCRIPTION) VALUES('5.05.2013', 'proj5','Проекционные данные 5')");
             statement.executeUpdate("INSERT INTO project_data (DATE, NAME,DESCRIPTION) VALUES('6.05.2013', 'proj6','Проекционные данные 6')");
             statement.executeUpdate("INSERT INTO project_data (DATE, NAME,DESCRIPTION) VALUES('7.05.2013', 'proj7','Проекционные данные 7')");
             */
            //----Create ArrayList witch consist of project dates creating from images in folder
            List<Object> projArrayList = new ArrayList<Object>();
            String path = "C:\\Users\\Antonov\\Desktop\\Images";
//            int countFiles = new File(path).listFiles().length;
            File folder = new File(path);
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try {
                        BufferedImage img = ImageIO.read(file);
                        double[][] entry = com.antonov.tomographysoftwarediploma.impl.imageprocessing.ImageTransformator.createProjectionData(img,180,700,1);
                        projArrayList.add(entry);
                    } catch (IOException e) {
                    }
                }
            }

            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/testing?"
                    + "user=root&password=ProL1ant", props);

            PreparedStatement pstmt = connect.prepareStatement(WRITE_OBJECT_SQL);
            pstmt.setString(1, "3.05.2013");
            pstmt.setString(2, "proj2");
            pstmt.setString(3, "Тестовые проекционные данные 2");
            pstmt.setObject(4, projArrayList);
            pstmt.executeUpdate();





        } catch (SQLException ex) {
            Logger.getLogger(TomographPane.class
                    .getName()).log(Level.SEVERE, null, ex);
        }


    }
}
