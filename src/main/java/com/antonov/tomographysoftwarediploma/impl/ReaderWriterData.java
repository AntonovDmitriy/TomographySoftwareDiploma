/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Antonov
 */
public class ReaderWriterData {

    public static BufferedImage getImageFromFileSystem(File file) throws IOException {

        return ImageIO.read(file);
    }

    public static List<BufferedImage> getAllImagesFromFileSystem(File pathTo) throws IOException {

        List<BufferedImage> result = new ArrayList<>();
        File[] listOfFiles = pathTo.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                BufferedImage img = ImageIO.read(file);
                result.add(img);
            }
        }
        return result;
    }

    public static void saveImageToFileSystem(BufferedImage image, File file, String filterImageDesc) throws IOException {

        String format = "";
        String name = file.getAbsolutePath();
        if (filterImageDesc.equals("JPEG File")) {
            String ext = ".jpeg";
            name = name + ext;
            format = "jpeg";
        } else if (filterImageDesc.equals("PNG File")) {
            String ext = ".png";
            name = name + ext;
            format = "PNG";
        } else if (filterImageDesc.equals("BMP File")) {
            String ext = ".bmp";
            name = name + ext;
            format = "BMP";
        } else if (filterImageDesc.equals("All Files")) {
            format = "";
        }

        ImageIO.write(image, format, new File(name));

    }

    public static void writeDoubleArrayToTextFile(double[][] array, String nameFile) {

        BufferedWriter writer = null;
        try {
            File file = new File(nameFile);

            writer = new BufferedWriter(new FileWriter(file));
            for (double[] row : array) {
                writer.write("\n");
                for (double entry : row) {
                    writer.write(entry + " ");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    static boolean isFolderExists(String javaSecurityFolder) {
        File file = new File(javaSecurityFolder);
        return file.isDirectory();

    }

    static void copyFilesFromTo(String fromFolder, String toFolder) throws IOException {
        File[] listFiles = (new File(fromFolder)).listFiles();
        for (File file : listFiles) {

            File newFile = new File(toFolder + "/" + file.getName());
            copyFile(file, newFile);
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    static void downloadFileAndWriteToTempFolder(String urlString, String fileName) throws IOException {

        File destFile = new File(fileName);
        destFile.getParentFile().mkdirs();
        destFile.createNewFile();
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(destFile);

            byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

    public void copyJarFolder(String jarName, String folderName, String destination) throws IOException {

        ZipFile z = new ZipFile(jarName);
        Enumeration entries = z.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            if (entry.getName().contains(folderName) && entry.isDirectory() == false) {
                String[] path = entry.getName().split("/");
                String nameOfFile = path[path.length - 1];
                String destFileName = destination + "/" + nameOfFile;
                File fileDest = new File(destFileName);
                copyFromJar(entry.getName(), fileDest);
            }
        }
    }

    private boolean copyFromJar(String sResource, File fDest) {
        if (sResource == null || fDest == null) {
            return false;
        }

        InputStream is = null;
        OutputStream os = null;

        try {
            int nLen = 0;
            is = getClass().getClassLoader().getResourceAsStream(sResource);
            if (is == null) {

                throw new IOException("Error copying from jar"
                        + "(" + sResource + " to " + fDest.getPath() + ")");
            }

            os = new FileOutputStream(fDest);
            byte[] bBuffer = new byte[1024];
            while ((nLen = is.read(bBuffer)) > 0) {
                os.write(bBuffer, 0, nLen);
            }
            os.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException eError) {
                eError.printStackTrace();
            }
        }
        return fDest.exists();
    }

    public String getStringResource(String name) throws IOException {
        InputStreamReader reader = null;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(name)) {

            reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }

            return stringBuffer.toString();
        } finally {
            reader.close();
        }

    }

    public Object getObjectResource(String name) throws IOException {
        System.out.println(name);
        Object result = null;
        URL urlFile = getClass().getClassLoader().getResource(name);

        Object resource = urlFile.getContent();
        if (resource != null) {
            result = resource;
        }
        return result;

    }

    public List<File> getListFilesFromJarFolder(String folderName, Properties p, boolean isWebStart) throws IOException {
        ZipFile z = null;
        if (isWebStart) {
            z = new ZipFile(getNameOfWebStartJar(p));
        } else {
            z = new ZipFile(getNameOfCurrentJar());
        }

        Enumeration entries = z.entries();
        List<File> resultList = new ArrayList<>();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            if (entry.getName().contains(folderName) && entry.isDirectory() == false) {
                resultList.add(new File(entry.getName()));
            }
        }
        return resultList;
    }

    public String getNameOfCurrentJar() throws UnsupportedEncodingException {
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        return decodedPath;
    }

    public String getNameOfWebStartJar(Properties p) throws UnsupportedEncodingException {
        String path = p.getProperty("PATH_TO_MAIN_JAR");
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        return decodedPath;
    }

    BufferedImage getImageResource(String path) throws IOException {
        System.out.println(path);
        path = path.replaceAll("\\\\", "/");
        System.out.println(path);
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        System.out.println(is.toString());
        return ImageIO.read(is);
    }

    public void extractResourceToFile(String pathToPrivateKey, Properties p, boolean isWebStart) throws IOException {
        ZipFile z = null;
        if (isWebStart) {
            z = new ZipFile(getNameOfCurrentJar());
        } else {
            z = new ZipFile(getNameOfWebStartJar(p));
        }

        Enumeration entries = z.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            if (entry.getName().contains(pathToPrivateKey) && entry.isDirectory() == false) {
                File fileDest = new File(pathToPrivateKey);
                fileDest.getParentFile().mkdirs();
                copyFromJar(entry.getName(), fileDest);
            }
        }

    }
}
