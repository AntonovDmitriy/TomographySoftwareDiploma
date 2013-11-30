package com.antonov.tomographysoftwarediploma;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;


public class Utils {
	
	/**creates array of pixels from BufferedImage*/
	
	public static int[][] getIntArrayPixelsFromBufImg(BufferedImage img) {

		final int width = img.getWidth(null);
		final int height = img.getHeight(null);
		final int[][] pix = new int[width][height];

		if (img.getType() == 11) {
			// TYPE_USHORT_GRAY Represents an unsigned short grayscale image,
			// non-indexed).

			DataBufferUShort db = (DataBufferUShort) img.getRaster()
					.getDataBuffer();
			short[] pixelarray = db.getData();

			for (int x = 0; x < pix.length; x++) {
				for (int y = 0; y < pix[0].length; y++) {
					pix[x][y] = pixelarray[x + y * pix.length] & 0xFFFF;
				}
			}

		} else if (img.getType() == 1) {
			// TYPE_INT_RGB Represents an image with 8-bit RGB color components
			// packed into integer pixels.
			DataBufferInt db = (DataBufferInt) img.getRaster().getDataBuffer();
			int[] pixelarray = db.getData();
			for (int x = 0; x < pix.length; x++) {
				for (int y = 0; y < pix[0].length; y++) {
					pix[x][y] = pixelarray[x + y * pix.length];
				}
			}
		} // else assume 8 bit
		else {
			DataBufferByte db = (DataBufferByte) img.getRaster()
					.getDataBuffer();
			byte[] pixelarray = db.getData();
			for (int x = 0; x < pix.length; x++) {
				for (int y = 0; y < pix[0].length; y++) {
					pix[x][y] = pixelarray[x + y * pix.length] & 0xFF;
				}
			}
		}
		return pix;
	}

	public static double[][] getDoubleArrayPixelsFromBufImg(BufferedImage img) {

		final int width = img.getWidth(null);
		final int height = img.getHeight(null);
		final double[][] result = new double[width][height];

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
	
    /**method to find the minimum value in a 2D array of doubles */
    public static double getMin(double data[][]) {
        
        double min = data[0][0];
        for ( int i = 0; i < data.length; i++ ) {
            for ( int j = 0; j < data[0].length; j++ ) {
                if (data[i][j] < min) min = data[i][j] ;
            }//System.out.println(min);
        }
        return min;
    }

    public static double getMax(double data[][]) {
        
        double max = data[0][0];
        for ( int i = 0; i < data.length; i++ ) {
            for ( int j = 0; j < data[0].length; j++ ) {
                if (data[i][j] > max) max = data[i][j] ;
            } //System.out.println(max);
        }
        return max;
    }
    
    public static double[][] normalize2DArray(double data[][], double min, double max) {
        
        double datamax = getMax(data);
        
        double datamin = getMin(data);
        //zero matrix
        if (datamin < 0 ){
            for ( int i = 0; i < data.length; i++ ) {
                for ( int j = 0; j < data[0].length; j++ ) {
                    if (data[i][j] < 0 ){
                        data[i][j] = 0;    
                    }
                }
            }
        }
        
        datamin = 0;
        
        for ( int i = 0; i < data.length; i++ ) {
            for ( int j = 0; j < data[0].length; j++ ) {
                data[i][j] = (double) (((data[i][j]-datamin) * (max))/datamax);
                
            }
        }
        return data;
    }

    public static boolean isPow2(int value) {
        return (value == (int)roundPow2(value));
    }
    
    public static double roundPow2(double value) {
        double power = (double)(Math.log(value) / Math.log(2));
        int intPower = (int)Math.round(power);
        return (double)(pow2(intPower));
    }
    
    public static int pow2(int power) {
        return (1 << power);
    }
    
    public static double[] filter1(String filtname, int scans, double cutoff){
        int i=0;
        int Width = scans/2;
        double tau = Width*cutoff;
        double[] filter = new double[scans];
        double PI = Math.PI;
        
        filter[0] = 0;
        if (filtname == "ramp"){
            for (i = 1; i <= Width; i++) {
                filter[scans - i] = filter[i] = (double) PI*i ;
            }
        } else if (filtname == "shepplogan"){
            for (i = 1; i <= Width; i++) {
                filter[scans - i] = filter[i] =  PI*i * ((Math.sin(PI*i/Width/2))/(PI*i/Width/2));
                
            }
        } else if (filtname == "hamming"){
            
            for (i = 1; i <= Width; i++) {
                if (i <= tau){
                    filter[scans - i] = filter[i] =  PI*i * (.54 + (.46 * Math.cos(PI*i/tau)));
                } else filter[scans - i] = filter[i] = 0;
            }
        } else if (filtname == "hann"){
            for (i = 1; i <= Width; i++) {
                if (i <= tau){
                    filter[scans - i] = filter[i] =  PI*i * (1 + (Math.cos(PI*i/tau)));
                } else filter[scans - i] = filter[i] = 0;
            }
        } else if (filtname == "cosine"){
            for (i = 1; i <= Width; i++) {
                if (i <= tau){
                    filter[scans - i] = filter[i] =  PI*i * (Math.cos(PI*i/tau/2));
                } else filter[scans - i] = filter[i] = 0;
            }
        } else if (filtname == "blackman"){
            for (i = 1; i <= Width; i++) {
                if (i <= tau){
                    filter[scans - i] = filter[i] = PI*i * (0.42 + (0.5 * Math.cos(PI*i/tau-1))
                                                      + (0.08 * Math.cos(2*PI*i/tau-1)));
                } else filter[scans - i] = filter[i] = 0;
            }
        }

        setRange1DArray(filter,0,1);
        return filter;
    }
    
    public static double[] setRange1DArray(double data[], int min, int max) {
        
        double[] result = new double [data.length];
        for ( int i = 0; i < result.length; i++ ) {
            if (data[i] > max){
                result[i] = max ;
            } else if (data[i] < min){
                result[i] = min ;
            } else { result [i] = data[i];}
        }
        return result;
    }
    
    public static void FFT(int dir, int s, double[] x,double[] y) {
        int n, i, i1, j, k, i2, l, l1, l2;
        double c1, c2, tx, ty, t1, t2, u1, u2, z;
        int m = (int) (Math.log(s)/Math.log(2));
        
        /* Calculate the number of points */
        n = 1;
        for (i=0;i<m;i++)
            n *= 2;
        
        /* Do the bit reversal */
        i2 = n >> 1;
        j = 0;
        for (i=0;i<n-1;i++) {
            if (i < j) {
                tx = x[i];
                ty = y[i];
                x[i] = x[j];
                y[i] = y[j];
                x[j] = tx;
                y[j] = ty;
            }
            k = i2;
            while (k <= j) {
                j -= k;
                k >>= 1;
            }
            j += k;
        }
        
        /* Compute the FFT */
        c1 = -1.0;
        c2 = 0.0;
        l2 = 1;
        for (l=0;l<m;l++) {
            l1 = l2;
            l2 <<= 1;
            u1 = 1.0;
            u2 = 0.0;
            for (j=0;j<l1;j++) {
                for (i=j;i<n;i+=l2) {
                    i1 = i + l1;
                    t1 = u1 * x[i1] - u2 * y[i1];
                    t2 = u1 * y[i1] + u2 * x[i1];
                    x[i1] = x[i] - t1;
                    y[i1] = y[i] - t2;
                    x[i] += t1;
                    y[i] += t2;
                }
                z =  u1 * c1 - u2 * c2;
                u2 = u1 * c2 + u2 * c1;
                u1 = z;
            }
            c2 = Math.sqrt((1.0 - c1) / 2.0);
            if (dir == 1)
                c2 = -c2;
            c1 = Math.sqrt((1.0 + c1) / 2.0);
        }
        
        /* Scaling for forward transform */
        if (dir == 1) {
            for (i=0;i<n;i++) {
                x[i] /= n;
                y[i] /= n;
                
            }
            
        }
    }
}
