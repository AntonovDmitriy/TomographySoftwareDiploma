/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl.imageprocessing;

import static com.antonov.tomographysoftwarediploma.impl.imageprocessing.Utils.setRange1DArray;

/**
 *
 * @author Antonov
 */
public class Filterer {

    public static double[] getFilterMatrix(String filtname, int scans, double cutoff) {
        int i = 0;
        int width = scans / 2;
        double tau = width * cutoff;
        double[] filterMatrix = new double[scans];
        double PI = Math.PI;

        filterMatrix[0] = 0;
        switch (filtname) {
            case "ramp":
                for (i = 1; i <= width; i++) {
                    filterMatrix[scans - i] = filterMatrix[i] = (double) PI * i;
                }
                break;
            case "shepplogan":
                for (i = 1; i <= width; i++) {
                    filterMatrix[scans - i] = filterMatrix[i] = PI * i * ((Math.sin(PI * i / width / 2)) / (PI * i / width / 2));

                }
                break;
            case "hamming":
                for (i = 1; i <= width; i++) {
                    if (i <= tau) {
                        filterMatrix[scans - i] = filterMatrix[i] = PI * i * (.54 + (.46 * Math.cos(PI * i / tau)));
                    } else {
                        filterMatrix[scans - i] = filterMatrix[i] = 0;
                    }
                }
                break;
            case "hann":
                for (i = 1; i <= width; i++) {
                    if (i <= tau) {
                        filterMatrix[scans - i] = filterMatrix[i] = PI * i * (1 + (Math.cos(PI * i / tau)));
                    } else {
                        filterMatrix[scans - i] = filterMatrix[i] = 0;
                    }
                }
                break;
            case "cosine":
                for (i = 1; i <= width; i++) {
                    if (i <= tau) {
                        filterMatrix[scans - i] = filterMatrix[i] = PI * i * (Math.cos(PI * i / tau / 2));
                    } else {
                        filterMatrix[scans - i] = filterMatrix[i] = 0;
                    }
                }
                break;
            case "blackman":
                for (i = 1; i <= width; i++) {
                    if (i <= tau) {
                        filterMatrix[scans - i] = filterMatrix[i] = PI * i * (0.42 + (0.5 * Math.cos(PI * i / tau - 1))
                                + (0.08 * Math.cos(2 * PI * i / tau - 1)));
                    } else {
                        filterMatrix[scans - i] = filterMatrix[i] = 0;
                    }
                }
                break;
        }

        setRange1DArray(filterMatrix, 0, 1);
        return filterMatrix;
    }

    public static void rightFFT(int s, double[] x, double[] y) {
        FFT(1, s, x, y);
    }

    public static void inverseFFT(int s, double[] x, double[] y) {

        FFT(0, s, x, y);
    }

    public static void FFT(int dir, int s, double[] x, double[] y) {
        int n, i, i1, j, k, i2, l, l1, l2;
        double c1, c2, tx, ty, t1, t2, u1, u2, z;
        int m = (int) (Math.log(s) / Math.log(2));

        /* Calculate the number of points */
        n = 1;
        for (i = 0; i < m; i++) {
            n *= 2;
        }

        /* Do the bit reversal */
        i2 = n >> 1;
        j = 0;
        for (i = 0; i < n - 1; i++) {
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
        for (l = 0; l < m; l++) {
            l1 = l2;
            l2 <<= 1;
            u1 = 1.0;
            u2 = 0.0;
            for (j = 0; j < l1; j++) {
                for (i = j; i < n; i += l2) {
                    i1 = i + l1;
                    t1 = u1 * x[i1] - u2 * y[i1];
                    t2 = u1 * y[i1] + u2 * x[i1];
                    x[i1] = x[i] - t1;
                    y[i1] = y[i] - t2;
                    x[i] += t1;
                    y[i] += t2;
                }
                z = u1 * c1 - u2 * c2;
                u2 = u1 * c2 + u2 * c1;
                u1 = z;
            }
            c2 = Math.sqrt((1.0 - c1) / 2.0);
            if (dir == 1) {
                c2 = -c2;
            }
            c1 = Math.sqrt((1.0 + c1) / 2.0);
        }

        /* Scaling for forward transform */
        if (dir == 1) {
            for (i = 0; i < n; i++) {
                x[i] /= n;
                y[i] /= n;

            }

        }
    }
}
