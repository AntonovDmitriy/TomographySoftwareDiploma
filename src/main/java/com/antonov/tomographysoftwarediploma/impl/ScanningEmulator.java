/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import com.antonov.tomographysoftwarediploma.dblayer.EmptyOrNullParameterException;
import com.antonov.tomographysoftwarediploma.dblayer.ITomographDao;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.ImageTransformator;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.ImageTransformerFacade;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.ImageWrongValueException;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.NumberWrongValueException;
import com.antonov.tomographysoftwarediploma.viewSwing.TomographPane;
import com.jcraft.jsch.JSchException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Антоновы
 */
public class ScanningEmulator {

    public static Logger logger = LoggerFactory.getLogger(ScanningEmulator.class);

    public static void emulateScanning(String name, String description, int scans, int stepSize, Properties tomographProperties, ITomographDao dao) throws IOException, NumberWrongValueException, ImageWrongValueException, JSchException, SQLException, EmptyOrNullParameterException {

        logger.trace("Scanning is in emulator regime");
        String path = tomographProperties.getProperty("PATH_SCANNING_EMULATOR_IMAGES_PATH");
        List<Object> projArrayList = new ArrayList<>();

        File folder = new File(path);
        logger.trace("Trying to read images from folder");

            for (BufferedImage image : ReaderWriterData.getAllImagesFromFileSystem(folder)) {

                logger.trace("File is readed");
                logger.trace("Trying to create projection data");
                double[][] projectionData = ImageTransformerFacade.createProjectionDataFromImage(image, scans, stepSize);
                logger.trace("Projection data has been created");
                projArrayList.add(projectionData);

            }
            
            dao.insertProjectionData(name, description, projArrayList);
        }
    }

