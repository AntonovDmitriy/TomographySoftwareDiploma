package com.antonov.tomographysoftwarediploma;

import dblayer.DbModule;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class ImageTransformator {

    public BufferedImage initialImage;
    // ----------Parameters of Scanning-------------------------
    public int scans; // Amount of Detectors or Point of Sourses
    public int stepsize; // Step of rotating in degree
    int views;
    // -----------Parameters of Reconstruction
    public double[][] projection;
    public int outputImgSize;
    public boolean filtering;
    public String filterName;
    public double pixels[][]; // 2-D array store pixel values

    public void setImage(BufferedImage initialImage) {
        this.initialImage = initialImage;
    }

    public void setScanParameters(int scans, int stepsize) {
        this.scans = scans;
        this.stepsize = stepsize;
        views = 180 / stepsize;
    }

    public void setReconstructParameters(int outputImgSize, boolean filtering, String filterName) {
        this.outputImgSize = outputImgSize;
        this.filtering = filtering;
        this.filterName = filterName;
    }

    public void setReconstructParameters(int outputImgSize) {
        this.outputImgSize = outputImgSize;
        this.filtering = false;
        this.filterName = null;
    }

    public BufferedImage createSinogram() {

        BufferedImage sinogram;
        BufferedImage sinogramImage;

        int gray;
        double[][] pixInitialImage = Utils
                .getDoubleArrayPixelsFromBufImg(initialImage);

        simulateProjectionData(pixInitialImage);

        projection = Utils.normalize2DArray(projection, 0, 1);


        double min = Utils.getMin(projection);

        double max = Utils.getMax(projection);

        short[] pixelshortArray = new short[projection.length
                * projection[0].length];

        for (int x = 0; x < projection.length; x++) {
            for (int y = 0; y < projection[0].length; y++) {
                // rescale pixel values for 12-bit grayscale image??
                if (max > min) {
                    gray = (int) ((projection[x][y]) * 2000 / (max));
                } else {
                    gray = (int) ((projection[x][y] - min) * 2000 / (max));
                }
                pixelshortArray[y + x * projection[0].length] = (short) gray;

            }
        }

        sinogram = create12bitImage(projection[0].length, projection.length,
                pixelshortArray);

        sinogramImage = PerformWindowing(sinogram);

        return sinogramImage;

    }

    public static double[][] createProjectionData(BufferedImage imgBuf, int views, int scans, int stepSize) {

        double[][] projection;
        if (imgBuf.getType() != 13) {
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            imgBuf = op.filter(imgBuf, null);
        }

        double[][] pixInitialImage = Utils
                .getDoubleArrayPixelsFromBufImg(imgBuf);
        projection = simulateProjectionDataStatic(pixInitialImage, views, scans, stepSize);

        return projection;
    }

    public static BufferedImage create12bitImage(int imageWidth,
            int imageHeight, short data[]) {

        DataBuffer dbuff = new DataBufferUShort((short[]) data, imageWidth);
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorModel cm = new ComponentColorModel(cs, new int[]{16}, false,
                false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
        int[] band = {0};
        SampleModel sm = new PixelInterleavedSampleModel(dbuff.getDataType(),
                imageWidth, imageHeight, 1, imageWidth, band);
        WritableRaster raster = Raster.createWritableRaster(sm, dbuff,
                new Point(0, 0));

        return new BufferedImage(cm, raster, false, null);

    }

    public static ArrayList<BufferedImage> createArrayReconstructedImage(String name, int outputImgSize, boolean filtering, String filterName, int scans, int stepsize) {

        ArrayList<BufferedImage> reconstructedImages = new ArrayList<BufferedImage>();
        int views;
        if (180 % stepsize == 0) {
            views = 180 / stepsize;
        } else {
            views = 1;
        }
        List projDataSet = DbModule.getProjDataSet(name);

        for (int k = 0; k < projDataSet.size(); k++) {
            int i, a;
            int sxoffset = 0, syoffset = 0;
            i = 0;
            double val = 0, pos;
            double zoom = 1;
            double xoffset = 0;
            double yoffset = 0;
            double[][] bpimage = new double[outputImgSize][outputImgSize];
            int x, y, Xcenter, Ycenter;

            double[] sintab = new double[views];
            double[] costab = new double[views];
            double[][] fprojection;
            int S = 0;
            String interp = "linear";

            // Фильтрация проекционных данных
            if (filtering == true) {

                fprojection = filterStatic((double[][]) projDataSet.get(k), views, scans, stepsize, filterName);

            } else {
                fprojection = (double[][]) projDataSet.get(k); // без фильтрации
            }

            int ang1 = 0, ang2 = 180; // начальный и конечный угол поворота

            for (int phi = ang1; phi < ang2; phi = phi + stepsize) {
                sintab[i] = Math.sin((double) phi * Math.PI / 180);
                costab[i] = Math.cos((double) phi * Math.PI / 180);
                i++;
            }

            for (x = 0; x < outputImgSize; x++) {
                for (y = 0; y < outputImgSize; y++) {
                    bpimage[x][y] = 0;
                }
            }

            // Обратное проецирование
            Xcenter = outputImgSize / 2;
            Ycenter = outputImgSize / 2;
            i = 0;
            double scale = zoom * outputImgSize * Math.sqrt(2) / scans;
            System.out.println("Performing back projection.. ");
            if (xoffset > 0) {
                sxoffset = (int) Math.floor(xoffset * outputImgSize * zoom);
            }
            if (yoffset > 0) {
                syoffset = (int) Math.floor(yoffset * outputImgSize * zoom);
            }
            boolean interrupt = false;

            for (x = -Xcenter; x < Xcenter; x++) {
                if (interrupt == true) {
                    break;
                }
                for (y = -Ycenter; y < Ycenter; y++) {
                    int x1 = x - sxoffset;
                    int y1 = y - syoffset;
                    if (Math.abs(x1) <= Xcenter + Math.abs(sxoffset)
                            && Math.abs(y1) <= Ycenter + Math.abs(syoffset)) {

                        for (int phi = ang1; phi < ang2; phi = phi + stepsize) {
                            pos = (-x1 * costab[i] + y1 * sintab[i]);

                            if (interp == "nearest") {
                                S = (int) Math.round(pos / scale);
                                S = S + scans / 2;
                                if (S < scans && S > 0) {
                                    val = val + fprojection[i][S];
                                }
                            } // линейная интерполяция
                            else if (interp == "linear") {
                                if (pos >= 0) {
                                    a = (int) Math.floor(pos / scale);
                                    int b = a + scans / 2;
                                    if (b < scans - 1 && b > 0) {
                                        val = val
                                                + fprojection[i][b]
                                                + (fprojection[i][b + 1] - fprojection[i][b])
                                                * (pos / scale - (double) a);
                                    }
                                } else if (pos < 0) {
                                    a = (int) Math.floor(pos / scale);
                                    int b = a + scans / 2;
                                    if (b < scans - 1 && b > 0) {
                                        val = val
                                                + fprojection[i][b]
                                                + (fprojection[i][b] - fprojection[i][b + 1])
                                                * (Math.abs(pos / scale) - Math
                                                .abs(a));
                                    }
                                }
                            }
                            i++;
                        }
                        S = 0;
                        i = 0;

                        bpimage[x + Xcenter][y + Ycenter] = val / views;

                        val = 0;
                    }
                }
            }

            double maxval = Utils.getMax(bpimage);
            BufferedImage reconstruction = CreateImagefromArrayStatic(bpimage, maxval, 1, outputImgSize);
            BufferedImage reconstuctionImage;

            reconstuctionImage = PerformWindowing(reconstruction);
            reconstructedImages.add(reconstuctionImage);
        }
        return reconstructedImages;
    }

    public BufferedImage createReconstructedImage() {

        int i, a;
        i = 0;
        double val = 0, pos, Aleft, Aright;
        double[][] bpimage = new double[outputImgSize][outputImgSize]; // массив с данными реконструкции
        int x, y, Xcenter, Ycenter, Ileft, Iright;
        double[] sintab = new double[views];
        double[] costab = new double[views];
        double[][] fprojection;
        int S = 0;
        String interp = "nearest";
        int ang1 = 0, ang2 = 180; // стартовый и конечный угол поворота системы источник-детектор

        // Устанавливаем флаг фильтрации проекционных данных
        if (filtering == true) {

            fprojection = Filter(projection);

        } else {
            fprojection = projection; // без фильтрации
        }




        // Создадим таблицы sin и cos в радианах для каждого используемого угла от ang1 до ang2
        for (int phi = ang1; phi < ang2; phi = phi + stepsize) {
            sintab[i] = Math.sin((double) phi * Math.PI / 180);
            costab[i] = Math.cos((double) phi * Math.PI / 180);
            i++;
        }

        // Заполнение матрицы для реконструкции нулями
        for (x = 0; x < outputImgSize; x++) {
            for (y = 0; y < outputImgSize; y++) {
                bpimage[x][y] = 0;
            }
        }

        // Back Project each pixel in the image
        Xcenter = outputImgSize / 2;
        Ycenter = outputImgSize / 2;
        i = 0;
        double scale = outputImgSize * Math.sqrt(2) / scans;
        System.out.println("Performing back projection.. ");

        boolean interrupt = false;

        for (x = -Xcenter; x < Xcenter; x++) {
            if (interrupt == true) {
                break;
            }
            for (y = -Ycenter; y < Ycenter; y++) {
                int x1 = x;
                int y1 = y;


                for (int phi = ang1; phi < ang2; phi = phi + stepsize) {
                    // pos = (x1 * sintab[i] - y1 * costab[i]);
                    pos = (-x1 * costab[i] + y1 * sintab[i]);
                    // System.out.print("pos= "+pos+" ");


                    if (interp == "nearest") {

                        S = (int) Math.round(pos / scale);
                        S = S + scans / 2;
                        if (S < scans && S > 0) {
                            val = val + fprojection[i][S];
                        }
                    } // perform linear interpolation
                    else if (interp == "linear") {
                        if (pos >= 0) {
                            a = (int) Math.floor(pos / scale);
                            int b = a + scans / 2;
                            if (b < scans - 1 && b > 0) {
                                val = val
                                        + fprojection[i][b]
                                        + (fprojection[i][b + 1] - fprojection[i][b])
                                        * (pos / scale - (double) a);
                            }
                        } else if (pos < 0) {
                            a = (int) Math.floor(pos / scale);
                            int b = a + scans / 2;
                            if (b < scans - 1 && b > 0) {
                                val = val
                                        + fprojection[i][b]
                                        + (fprojection[i][b] - fprojection[i][b + 1])
                                        * (Math.abs(pos / scale) - Math
                                        .abs(a));
                            }
                        }
                    }
                    i++;
                }
                S = 0;
                i = 0;

                bpimage[x + Xcenter][y + Ycenter] = val / views;
                // bpimage[x + Xcenter][y + Ycenter] = val*Math.PI/2*views;
                // img = img*pi/(2*length(theta));
                val = 0;
            }
        }

        double maxval = Utils.getMax(bpimage);
        BufferedImage reconstruction = CreateImagefromArray(bpimage, maxval, 1);
        BufferedImage reconstuctionImage;

        reconstuctionImage = PerformWindowing(reconstruction);
        return reconstuctionImage;
    }

    // Необходимо уяснить каким методом создаются проекционные данные и найти
    // его математическое обоснование
    private void simulateProjectionData(double[][] pixInitialImage) {

        int ang1 = 0, ang2 = 180; // start and stop angles for projections

        double val;
        int x, y, Xcenter, Ycenter, S = 0;

        int inputimgsize = pixInitialImage[0].length;
        projection = new double[views][scans];
        double[] sintab = new double[views];
        double[] costab = new double[views];
        // Zero matrix
        for (int i = 0; i < projection.length; i++) {
            for (int j = 0; j < projection[0].length; j++) {
                projection[i][j] = 0.0;
            }
        }

        int i = 0, phi;

        for (phi = ang1; phi < ang2; phi = (int) (phi + stepsize), i++) {
            sintab[i] = Math.sin((double) phi * Math.PI / 180 - Math.PI / 2);
            costab[i] = Math.cos((double) phi * Math.PI / 180 - Math.PI / 2);
        }

        // Project each pixel in the image
        Xcenter = inputimgsize / 2;
        Ycenter = inputimgsize / 2;
        i = 0;

        double scale = inputimgsize * Math.sqrt(2) / scans;

        int N = 0;
        val = 0;
        double weight = 0;
        double sang = Math.sqrt(2) / 2;
        boolean interrupt = false, fast = false;

        for (phi = ang1; phi < ang2; phi = (int) (phi + stepsize)) {
            if (interrupt) {
                break;
            }
            double a = -costab[i] / sintab[i];
            double aa = 1 / a;
            if (Math.abs(sintab[i]) > sang) {
                for (S = 0; S < scans; S++) {
                    N = S - scans / 2; // System.out.print("N="+N+" ");
                    double b = (N - costab[i] - sintab[i]) / sintab[i];
                    b = b * scale;

                    for (x = -Xcenter; x < Xcenter; x++) {
                        if (fast == true) {
                            // just use nearest neighbour interpolation
                            y = (int) Math.round(a * x + b);
                            if (y >= -Xcenter && y < Xcenter) {
                                val += pixInitialImage[(y + Ycenter)][(x + Xcenter)];
                            }

                        } else {
                            // linear interpolation
                            y = (int) Math.round(a * x + b);
                            weight = Math.abs((a * x + b)
                                    - Math.ceil(a * x + b));

                            if (y >= -Xcenter && y + 1 < Xcenter) {
                                val += (1 - weight)
                                        * pixInitialImage[(y + Ycenter)][(x + Xcenter)]
                                        + weight
                                        * pixInitialImage[(y + Ycenter)][(x + Xcenter)];
                            }
                        }
                    }
                    projection[i][S] = val / Math.abs(sintab[i]);
                    val = 0;

                }
            } else if (Math.abs(sintab[i]) <= sang) {
                for (S = 0; S < scans; S++) {
                    N = S - scans / 2;
                    double bb = (N - costab[i] - sintab[i]) / costab[i];
                    bb = bb * scale;
                    for (y = -Ycenter; y < Ycenter; y++) {
                        if (fast == true) {
                            x = (int) Math.round(aa * y + bb);
                            if (x >= -Xcenter && x < Xcenter) {
                                val += pixInitialImage[(y + Ycenter)][(x + Xcenter)];
                            }
                        } else {

                            x = (int) Math.round(aa * y + bb);
                            weight = Math.abs((aa * y + bb)
                                    - Math.ceil(aa * y + bb));

                            if (x >= -Xcenter && x + 1 < Xcenter) {
                                val += (1 - weight)
                                        * pixInitialImage[(y + Ycenter)][(x + Xcenter)]
                                        + weight
                                        * pixInitialImage[(y + Ycenter)][(x + Xcenter)];
                            }
                        }
                    }
                    projection[i][S] = val / Math.abs(costab[i]);
                    val = 0;

                }
            }
            i++;
        }
//        for (int q = 0; q < 10; q++) {
//            System.out.println("");
//            for (int w = 0; w < 10; w++) {
//                System.out.print(projection[q][w]+" ");
//            }
//        }
//        for (int q = 0; q < 10; q++) {
//            System.out.println("");
//            for (int w = 0; w < 10; w++) {
//                System.out.print(new BigDecimal(projection[q][w]).setScale(2, RoundingMode.UP).doubleValue() + " ");
//            }
//        }

    }

    private static double[][] simulateProjectionDataStatic(double[][] pixInitialImage, int views, int scans, int stepsize) {

        int ang1 = 0, ang2 = 180; // start and stop angles for projections

        double val;
        int x, y, Xcenter, Ycenter, S = 0;

        int inputimgsize = pixInitialImage[0].length;
        double[][] projection = new double[views][scans];
        double[] sintab = new double[views];
        double[] costab = new double[views];
        // Zero matrix
        for (int i = 0; i < projection.length; i++) {
            for (int j = 0; j < projection[0].length; j++) {
                projection[i][j] = 0.0;
            }
        }

        int i = 0, phi;

        for (phi = ang1; phi < ang2; phi = (int) (phi + stepsize), i++) {
            sintab[i] = Math.sin((double) phi * Math.PI / 180 - Math.PI / 2);
            costab[i] = Math.cos((double) phi * Math.PI / 180 - Math.PI / 2);
        }

        // Project each pixel in the image
        Xcenter = inputimgsize / 2;
        Ycenter = inputimgsize / 2;
        i = 0;

        double scale = inputimgsize * Math.sqrt(2) / scans;

        int N = 0;
        val = 0;
        double weight = 0;
        double sang = Math.sqrt(2) / 2;
        boolean interrupt = false, fast = false;

        for (phi = ang1; phi < ang2; phi = (int) (phi + stepsize)) {
            if (interrupt) {
                break;
            }
            double a = -costab[i] / sintab[i];
            double aa = 1 / a;
            if (Math.abs(sintab[i]) > sang) {
                for (S = 0; S < scans; S++) {
                    N = S - scans / 2; 
                    double b = (N - costab[i] - sintab[i]) / sintab[i];
                    b = b * scale;

                    for (x = -Xcenter; x < Xcenter; x++) {
                        if (fast == true) {
                            // just use nearest neighbour interpolation
                            y = (int) Math.round(a * x + b);
                            if (y >= -Xcenter && y < Xcenter) {
                                val += pixInitialImage[(y + Ycenter)][(x + Xcenter)];
                            }

                        } else {
                            // linear interpolation
                            y = (int) Math.round(a * x + b);
                            weight = Math.abs((a * x + b)
                                    - Math.ceil(a * x + b));

                            if (y >= -Xcenter && y + 1 < Xcenter) {
                                val += (1 - weight)
                                        * pixInitialImage[(y + Ycenter)][(x + Xcenter)]
                                        + weight
                                        * pixInitialImage[(y + Ycenter)][(x + Xcenter)];
                            }
                        }
                    }
                    projection[i][S] = val / Math.abs(sintab[i]);
                    val = 0;

                }
            } else if (Math.abs(sintab[i]) <= sang) {
                for (S = 0; S < scans; S++) {
                    N = S - scans / 2;
                    double bb = (N - costab[i] - sintab[i]) / costab[i];
                    bb = bb * scale;
                    for (y = -Ycenter; y < Ycenter; y++) {
                        if (fast == true) {
                            x = (int) Math.round(aa * y + bb);
                            if (x >= -Xcenter && x < Xcenter) {
                                val += pixInitialImage[(y + Ycenter)][(x + Xcenter)];
                            }
                        } else {

                            x = (int) Math.round(aa * y + bb);
                            weight = Math.abs((aa * y + bb)
                                    - Math.ceil(aa * y + bb));

                            if (x >= -Xcenter && x + 1 < Xcenter) {
                                val += (1 - weight)
                                        * pixInitialImage[(y + Ycenter)][(x + Xcenter)]
                                        + weight
                                        * pixInitialImage[(y + Ycenter)][(x + Xcenter)];
                            }
                        }
                    }
                    projection[i][S] = val / Math.abs(costab[i]);
                    val = 0;

                }
            }
            i++;
        }
        return projection;
    }

    private static BufferedImage PerformWindowing(BufferedImage mBufferedImage) {

        int[][] pixels = Utils.getIntArrayPixelsFromBufImg(mBufferedImage);
        int iw = mBufferedImage.getWidth();
        int ih = mBufferedImage.getHeight();
        BufferedImage windowedImage;

        int upperwinlvl;
        int lowerwinlvl = 0;

        if (mBufferedImage.getType() == 11) {
            upperwinlvl = 2000;
        } else {
            upperwinlvl = 255;
        }
        int winwidth = upperwinlvl - lowerwinlvl;

        if ((mBufferedImage.getType() == 11)
                || (mBufferedImage.getType() == 10)) {
            windowedImage = new BufferedImage(iw, ih,
                    BufferedImage.TYPE_BYTE_GRAY);

            WritableRaster wraster = windowedImage.getRaster();
            for (int x = 0; x < iw; x++) {
                for (int y = 0; y < ih; y++) {
                    // int val = wraster.getSample(x, y, 0);
                    int val = pixels[x][y];
                    if (val <= lowerwinlvl) {
                        wraster.setSample(x, y, 0, 0);
                    } else if (val >= upperwinlvl) {
                        wraster.setSample(x, y, 0, 255);
                    } else {
                        int newval = (val - lowerwinlvl) * 256 / winwidth;
                        wraster.setSample(x, y, 0, newval);
                    }
                }
            }
        } else {
            windowedImage = new BufferedImage(iw, ih,
                    BufferedImage.TYPE_INT_RGB);
            windowedImage.createGraphics()
                    .drawImage(mBufferedImage, 0, 0, null);

        }
        return windowedImage;
    }

    private BufferedImage CreateImagefromArray(double[][] pix, double max,
            int type) {

        int i, j;
        i = 0;
        j = 0;

        short[] pixelshortArray = new short[pix.length * pix[0].length];
        double min = Utils.getMin(pix);

        double datamin = Utils.getMin(pix);

        // zero matrix
        if (datamin < 0) {
            for (i = 0; i < pix.length; i++) {
                for (j = 0; j < pix[0].length; j++) {
                    if (pix[i][j] < 0) {
                        pix[i][j] = 0;
                    }
                }
            }
        }

        int gray;
        // System.out.println("rescaling output image: ");

        for (int y = 0; y < pix[0].length; y++) {
            for (int x = 0; x < pix.length; x++) {
                if (min < 0) {
                    gray = (int) ((pix[x][y]) * 2000 / (max));
                } else {

                    gray = (int) ((pix[x][y]) * 2000 / (max));
                }
                pixelshortArray[x + y * pix.length] = (short) gray;
            }
        }

        BufferedImage img;
        // returns an 8-bit buffered image for display purposes

        img = create12bitImage(outputImgSize, outputImgSize, pixelshortArray);

        return img;

    }

    private static BufferedImage CreateImagefromArrayStatic(double[][] pix, double max,
            int type, int outputImgSize) {

        int i, j;
        i = 0;
        j = 0;

        short[] pixelshortArray = new short[pix.length * pix[0].length];
        double min = Utils.getMin(pix);

        double datamin = Utils.getMin(pix);

        // zero matrix
        if (datamin < 0) {
            for (i = 0; i < pix.length; i++) {
                for (j = 0; j < pix[0].length; j++) {
                    if (pix[i][j] < 0) {
                        pix[i][j] = 0;
                    }
                }
            }
        }

        int gray;
        // System.out.println("rescaling output image: ");

        for (int y = 0; y < pix[0].length; y++) {
            for (int x = 0; x < pix.length; x++) {
                if (min < 0) {
                    gray = (int) ((pix[x][y]) * 2000 / (max));
                } else {

                    gray = (int) ((pix[x][y]) * 2000 / (max));
                }
                pixelshortArray[x + y * pix.length] = (short) gray;
            }
        }

        BufferedImage img;
        // returns an 8-bit buffered image for display purposes

        img = create12bitImage(outputImgSize, outputImgSize, pixelshortArray);

        return img;

    }

    private double[][] Filter(double[][] proj) {

        int i, pscans;
        double filter[], pfilter[];   //array to store filter and padded filter

        double[] rawdata;
        double[] idata;

        double[][] fproj = new double[views][scans];

        //length of array - no of 'scans', must be a power of 2
        //if scans is a power of 2 then just allocated twice this value for arrays and then pad
        //the projection (and filter data?) with zeroes before applying FFT
        if (Utils.isPow2(scans) == true) {
            pscans = scans;   //System.out.println("power of 2");
        } //if scans is not a power of 2, then round pscans up to nearest power and double
        else {
            int power = (int) ((Math.log(scans) / Math.log(2))) + 1; //closest power of 2 rounded up
            pscans = (int) Math.pow(2, power);
            System.out.println("PSCANS: " + pscans);
        }
        rawdata = new double[pscans * 2];
        idata = new double[pscans * 2];
        pfilter = new double[pscans * 2];

        for (int S = 0; S < pscans; S++) {
            idata[S] = 0;
        }

        // Initialize the filter
        filter = Utils.filter1(filterName, pscans * 2, 1.0);

        i = 0;

        // Filter each projection
        for (int phi = 0; phi < 180; phi += stepsize) {
            for (int S = 0; S < scans; S++) {
                rawdata[S] = proj[i][S];
            }
            //zero pad projections
            for (int S = scans; S < pscans * 2; S++) {
                rawdata[S] = 0;
            }
            Utils.FFT(1, pscans * 2, rawdata, idata);
            for (int S = 0; S < scans * 2; S++) {
                rawdata[S] *= filter[S];
            }
            //perform inverse fourier transform of filtered product
            Utils.FFT(0, pscans * 2, rawdata, idata);
            for (int S = 0; S < scans; S++) {
                fproj[i][S] = rawdata[S];
            }
            for (int S = 0; S < pscans * 2; S++) {
                idata[S] = 0;
            }
            i++;
        }
        return fproj;
    }

    private static double[][] filterStatic(double[][] proj, int views, int scans, int stepsize, String filterName) {

        int i, pscans;
        double filter[];

        double[] rawdata;
        double[] idata;

        double[][] fproj = new double[views][scans];

        if (Utils.isPow2(scans) == true) {
            pscans = scans; 
        } 
        else {
            int power = (int) ((Math.log(scans) / Math.log(2))) + 1; 
            pscans = (int) Math.pow(2, power);
            System.out.println("PSCANS: " + pscans);
        }
        rawdata = new double[pscans * 2];
        idata = new double[pscans * 2];

        for (int S = 0; S < pscans; S++) {
            idata[S] = 0;
        }

        // Инициализация фильтра
        filter = Utils.filter1(filterName, pscans * 2, 1.0);

        i = 0;

        // Фильтруем каждую проекцию
        for (int phi = 0; phi < 180; phi += stepsize) {
            for (int S = 0; S < scans; S++) {
                rawdata[S] = proj[i][S];
            }

            for (int S = scans; S < pscans * 2; S++) {
                rawdata[S] = 0;
            }
            //Прямое преобразование фурье
            Utils.FFT(1, pscans * 2, rawdata, idata);
            for (int S = 0; S < scans * 2; S++) {
                rawdata[S] *= filter[S];
            }
            //Обратное преобразование Фурье
            Utils.FFT(0, pscans * 2, rawdata, idata);
            for (int S = 0; S < scans; S++) {
                fproj[i][S] = rawdata[S];
            }
            for (int S = 0; S < pscans * 2; S++) {
                idata[S] = 0;
            }
            i++;
        }
        return fproj;
    }

    public static BufferedImage getColorLutImage(BufferedImage image, short[][] colors) {


        byte[] reds = new byte[256];
        byte[] greens = new byte[256];
        byte[] blues = new byte[256];
        for (int i = 0; i < 256; i++) {
            reds[i] = (byte) colors[i][0];
            greens[i] = (byte) colors[i][1];
            blues[i] = (byte) colors[i][2];
        }

        ColorModel colorModel = new IndexColorModel(8, 256, reds, greens, blues);
        WritableRaster wraster = image.getRaster();

        BufferedImage img = new BufferedImage(colorModel, wraster, false, null);
        return img;
    }
}
