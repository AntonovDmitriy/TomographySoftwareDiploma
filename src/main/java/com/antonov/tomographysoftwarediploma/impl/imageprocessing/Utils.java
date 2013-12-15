package com.antonov.tomographysoftwarediploma.impl.imageprocessing;

import static com.antonov.tomographysoftwarediploma.impl.imageprocessing.ImageTransformator.create12bitImage;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public class Utils {

    public static int[][] getIntArrayPixelsFromBufImg(BufferedImage img) {

        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int[][] pix;

        if (img.getType() == 11) {
            // TYPE_USHORT_GRAY Represents an unsigned short grayscale image,
            // non-indexed).
            pix = getIntArrayPixelsFromGrayScaleImage(img, width, height);
        } else if (img.getType() == 1) {
            // TYPE_INT_RGB Represents an image with 8-bit RGB color components
            // packed into integer pixels.
            pix = getIntArrayPixelsFromRGB8bitImage(img, width, height);
        } else {
            // else assume 8 bit
            pix = getIntArrayPixelsFrom8bitImage(img, width, height);
        }
        return pix;
    }

    private static int[][] getIntArrayPixelsFromGrayScaleImage(BufferedImage img, int width, int height) {

        final int[][] result = new int[width][height];
        DataBufferUShort db = (DataBufferUShort) img.getRaster().getDataBuffer();
        short[] pixelarray = db.getData();

        for (int x = 0; x < result.length; x++) {
            for (int y = 0; y < result[0].length; y++) {
                result[x][y] = pixelarray[x + y * result.length] & 0xFFFF;
            }
        }
        return result;
    }

    private static int[][] getIntArrayPixelsFromRGB8bitImage(BufferedImage img, int width, int height) {

        final int[][] result = new int[width][height];
        DataBufferInt db = (DataBufferInt) img.getRaster().getDataBuffer();
        int[] pixelarray = db.getData();
        for (int x = 0; x < result.length; x++) {
            for (int y = 0; y < result[0].length; y++) {
                result[x][y] = pixelarray[x + y * result.length];
            }
        }
        return result;
    }

    private static int[][] getIntArrayPixelsFrom8bitImage(BufferedImage img, int width, int height) {

        final int[][] result = new int[width][height];
        DataBufferByte db = (DataBufferByte) img.getRaster()
                .getDataBuffer();
        byte[] pixelarray = db.getData();
        for (int x = 0; x < result.length; x++) {
            for (int y = 0; y < result[0].length; y++) {
                result[x][y] = pixelarray[x + y * result.length] & 0xFF;
            }
        }
        return result;
    }

    public static double[][] getDoubleRevertedArrayPixelsFromBufImg(BufferedImage img) {

        int width = img.getWidth(null);
        int height = img.getHeight(null);
        double[][] result = new double[width][height];

        if (img.getType() == 11) {
            // TYPE_USHORT_GRAY Represents an unsigned short grayscale image,
            // non-indexed).

            DataBufferUShort db = (DataBufferUShort) img.getRaster().getDataBuffer();
            short[] shortPixelArray = db.getData();
            for (int x = 0; x < result.length; x++) {
                for (int y = 0; y < result[0].length; y++) {
                    result[x][y] = shortPixelArray[x + y * result.length] & 0xFFFF;
                }
            }
        } else if (img.getType() == 1) {
            // TYPE_INT_RGB Represents an image with 8-bit RGB color components
            // packed into integer pixels

            DataBufferInt db = (DataBufferInt) img.getRaster().getDataBuffer();
            int[] intPixelArray = db.getData();
            for (int x = 0; x < result.length; x++) {
                for (int y = 0; y < result[0].length; y++) {
                    result[x][y] = intPixelArray[x + y * result.length] & 0xFF;
                }
            }
        } // else assume 8 bit
        else {
            DataBufferByte db = (DataBufferByte) img.getRaster()
                    .getDataBuffer();
            byte[] bytePixelArray = db.getData();
            for (int x = 0; x < result.length; x++) {
                for (int y = 0; y < result[0].length; y++) {
                    result[x][y] = bytePixelArray[x + y * result.length] & 0xFF;
                }
            }
        }
        return result;
    }

    public static double[][] getDoubleArrayPixelsFromBufImg(BufferedImage img) {

        int width = img.getWidth(null);
        int height = img.getHeight(null);
        double[][] result = new double[height][width];

        if (img.getType() == 11) {
            // TYPE_USHORT_GRAY Represents an unsigned short grayscale image,
            // non-indexed).

            DataBufferUShort db = (DataBufferUShort) img.getRaster().getDataBuffer();
            short[] shortPixelArray = db.getData();
            for (int x = 0; x < result[0].length; x++) {
                for (int y = 0; y < result.length; y++) {
                    result[y][x] = shortPixelArray[x + y * result.length] & 0xFFFF;
                }
            }
        } else if (img.getType() == 1) {
            // TYPE_INT_RGB Represents an image with 8-bit RGB color components
            // packed into integer pixels

            DataBufferInt db = (DataBufferInt) img.getRaster().getDataBuffer();
            int[] intPixelArray = db.getData();
            for (int x = 0; x < result[0].length; x++) {
                for (int y = 0; y < result.length; y++) {
                    result[x][y] = intPixelArray[x + y * result.length] & 0xFF;
                }
            }
        } // else assume 8 bit
        else {

            DataBufferByte db = (DataBufferByte) img.getRaster()
                    .getDataBuffer();
            byte[] bytePixelArray = db.getData();
            for (int x = 0; x < result[0].length; x++) {
                for (int y = 0; y < result.length; y++) {
                    result[y][x] = bytePixelArray[x + y * result.length] & 0xFF;
                }
            }
        }
        return result;
    }

    public static double[][] fillZeroMatrix(double[][] matrix) {
        for (double[] matrix1 : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix1[j] = 0.0;
            }
        }
        return matrix;
    }

    public static void fillZeroMatrix(double[] matrix, int from, int to) {
        for (int i = from; i < to; i++) {
            matrix[i] = 0;
        }
    }

    public static void fillZeroMatrix(double[] matrix) {
        
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = 0;
        }
    }

    public static double[] getRowOfFunctionIncrementalValues(String function, int startFunctionPositiveArgument, int endFunctionPositiveArgument, int stepSize) {
        int amountElements = (int) (endFunctionPositiveArgument - startFunctionPositiveArgument) / stepSize;
        double[] result = new double[amountElements];
        int i = 0;

        for (int argument = startFunctionPositiveArgument; argument < endFunctionPositiveArgument; argument = (int) (argument + stepSize), i++) {
            switch (function) {
                case "-cos":
                    result[i] = (-1) * Math.cos((double) argument * Math.PI / 180);
                    break;
                case "sin":
                    result[i] = Math.sin((double) argument * Math.PI / 180);
                    break;
                case "cos":
                    result[i] = Math.cos((double) argument * Math.PI / 180);
                    break;
            }
        }
        return result;
    }

    public static double getMin(double data[][]) {

        double min = data[0][0];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                if (data[i][j] < min) {
                    min = data[i][j];
                }
            }//System.out.println(min);
        }
        return min;
    }

    public static double getMax(double data[][]) {

        double max = data[0][0];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                if (data[i][j] > max) {
                    max = data[i][j];
                }
            } //System.out.println(max);
        }
        return max;
    }

    public static double[][] normalize2DArray(double data[][], double min, double max) {

        double datamax = getMax(data);
        double datamin = getMin(data);

        if (datamin < 0) {
            fillZeroMatrix(data);
        }

        datamin = 0;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                data[i][j] = (double) (((data[i][j] - datamin) * (max)) / datamax);

            }
        }
        return data;
    }

    public static short[] getShortRowFromProjectionData(double[][] projectionData) {

        double min = Utils.getMin(projectionData);
        double max = Utils.getMax(projectionData);
        int gray;

        short[] pixelshortArray = new short[projectionData.length
                * projectionData[0].length];

        for (int x = 0; x < projectionData.length; x++) {
            for (int y = 0; y < projectionData[0].length; y++) {
                // rescale pixel values for 12-bit grayscale image??
                if (max > min) {
                    gray = (int) ((projectionData[x][y]) * 2000 / (max));
                } else {
                    gray = (int) ((projectionData[x][y] - min) * 2000 / (max));
                }
                pixelshortArray[y + x * projectionData[0].length] = (short) gray;

            }
        }
        return pixelshortArray;
    }

    public static BufferedImage create12bitImageFromShortProjectionData(int imageWidth,
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

    public static boolean isPow2(int value) {
        return (value == (int) roundPow2(value));
    }

    public static double roundPow2(double value) {
        double power = (double) (Math.log(value) / Math.log(2));
        int intPower = (int) Math.round(power);
        return (double) (pow2(intPower));
    }

    public static int pow2(int power) {
        return (1 << power);
    }

    

    public static double[] setRange1DArray(double data[], int min, int max) {

        double[] result = new double[data.length];
        for (int i = 0; i < result.length; i++) {
            if (data[i] > max) {
                result[i] = max;
            } else if (data[i] < min) {
                result[i] = min;
            } else {
                result[i] = data[i];
            }
        }
        return result;
    }

    public static BufferedImage createImagefromArray(double[][] pix) {

        double max = getMax(pix);
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

        img = create12bitImage(pix.length, pix.length, pixelshortArray);

        return img;

    }
}
