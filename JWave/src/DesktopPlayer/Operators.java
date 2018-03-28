package DesktopPlayer;

import jwave.transforms.wavelets.Wavelet;
import jwave.transforms.wavelets.biorthogonal.*;
import jwave.transforms.wavelets.coiflet.*;
import jwave.transforms.wavelets.daubechies.*;
import jwave.transforms.wavelets.haar.Haar1;
import jwave.transforms.wavelets.haar.Haar1Orthogonal;
import jwave.transforms.wavelets.legendre.Legendre1;
import jwave.transforms.wavelets.legendre.Legendre2;
import jwave.transforms.wavelets.legendre.Legendre3;
import jwave.transforms.wavelets.symlets.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Operators
{
    public static double[] getDoubleArrayOfCorrectLength(double[] d) {
        int i = 0;
        int fLength = 0;
        while (Math.pow(2, i) < d.length) {
            i++;
        }
        fLength = (int) Math.pow(2, i);
        double[] result = new double[fLength];
        for (int j = 0; j < fLength; j++) {
            if (j < d.length) {
                result[j] = d[j];
            }
            else {
                result[j] = 0;
            }
        }

        return result;
    }

    public static double[] getDoubleArrayTruncated(double[] d, int o) {
        double[] result = new double[o];

        for (int i = 0; i < o; i++) {
            result[i] = d[i];
        }

        return result;
    }

    public static double[] removeZeroesFromDoubles(double[] d) {
        Double[] move = new Double[d.length];
        for (int i = 0; i < d.length; i++) {
            move[i] = d[i];
        }
        ArrayList<Double> list = new ArrayList<Double>(Arrays.asList(move));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == 0) {
                int j = 0;
                for (j = i; j < list.size(); j++) {
                    if (list.get(j) != 0) {
                        break;
                    }
                }
                if (j == i+1) {
                    list.add(i+1, 0.0);
                    i++;
                }
                else {
                    if((j-i) > 100) {
                        System.out.println("100+");
                    }
                    list.subList(i, j).clear();
                    list.add(i, 0.0);
                    list.add(i+1, (double)(j - i - 1));
                }
            }
        }

        move = new Double[list.size()];
        move = list.toArray(move);
        double[] move2 = new double[list.size()];
        for (int i = 0; i < move.length; i++) {
            move2[i] = move[i];
        }

        return move2;
    }

    public static double[] addZeroesToDoubles(double[] d) {
        Double[] D = new Double[d.length];
        for (int i = 0; i < d.length; i++) {
            D[i] = d[i];
        }
        ArrayList<Double> list = new ArrayList<Double>(Arrays.asList(D));
        for (int i = 0; i < list.size(); i++) {
            if (i == 1048576) {
                System.out.println("hit");
            }
            if (list.get(i) == 0) {
                if (list.get(i+1) == 0) {
                    list.remove(i+1);
                }
                else {
                    int max = (int)(double)list.get(i+1);
                    if(max < 0) {
                        System.out.println("hit");
                    }
                    list.remove(i+1);
                    for (int j = 0; j < max; j++) {
                        list.add(i+1, 0.0);
                    }
                    i = i + max;
                }
            }
        }

        D = new Double[list.size()];
        D = list.toArray(D);
        double[] result = new double[list.size()];
        for (int i = 0; i < D.length; i++) {
            result[i] = D[i];
        }

        return result;
    }

    public static double getAvg(double[] d) {
        double min = 0;
        double max = 0;
        for (int i = 0; i < d.length; i++) {
            if (d[i] > max) {
                max = d[i];
            }
            if (d[i] < min) {
                min = d[i];
            }
        }
        return (min + max) / 2;
    }

    public static Wavelet getWaveletFromIndex(int index) {
        if (index >= 0 && index <= 15) {
            if (index == 0) {
                return new BiOrthogonal();
            }
            else if (index == 1) {
                return new BiOrthogonal11();
            }
            else if (index == 2) {
                return new BiOrthogonal13();
            }
            else if (index == 3) {
                return new BiOrthogonal15();
            }
            else if (index == 4) {
                return new BiOrthogonal22();
            }
            else if (index == 5) {
                return new BiOrthogonal24();
            }
            else if (index == 6) {
                return new BiOrthogonal26();
            }
            else if (index == 7) {
                return new BiOrthogonal28();
            }
            else if (index == 8) {
                return new BiOrthogonal31();
            }
            else if (index == 9) {
                return new BiOrthogonal33();
            }
            else if (index == 10) {
                return new BiOrthogonal35();
            }
            else if (index == 11) {
                return new BiOrthogonal37();
            }
            else if (index == 12) {
                return new BiOrthogonal39();
            }
            else if (index == 13) {
                return new BiOrthogonal44();
            }
            else if (index == 14) {
                return new BiOrthogonal55();
            }
            else {
                return new BiOrthogonal68();
            }
        }
        if (index >= 16 && index <= 20) {
            if (index == 16) {
                return new Coiflet1();
            }
            else if (index == 17) {
                return new Coiflet2();
            }
            else if (index == 18) {
                return new Coiflet3();
            }
            else if (index == 19) {
                return new Coiflet4();
            }
            else {
                return new Coiflet5();
            }
        }
        if (index >= 21 && index <= 39) {
            if (index == 21) {
                return new Daubechies2();
            }
            else if (index == 22) {
                return new Daubechies3();
            }
            else if (index == 23) {
                return new Daubechies4();
            }
            else if (index == 24) {
                return new Daubechies5();
            }
            else if (index == 25) {
                return new Daubechies6();
            }
            else if (index == 26) {
                return new Daubechies7();
            }
            else if (index == 27) {
                return new Daubechies8();
            }
            else if (index == 28) {
                return new Daubechies9();
            }
            else if (index == 29) {
                return new Daubechies10();
            }
            else if (index == 30) {
                return new Daubechies11();
            }
            else if (index == 31) {
                return new Daubechies12();
            }
            else if (index == 32) {
                return new Daubechies13();
            }
            else if (index == 33) {
                return new Daubechies14();
            }
            else if (index == 34) {
                return new Daubechies15();
            }
            else if (index == 35) {
                return new Daubechies16();
            }
            else if (index == 36) {
                return new Daubechies17();
            }
            else if (index == 37) {
                return new Daubechies18();
            }
            else if (index == 38) {
                return new Daubechies19();
            }
            else {
                return new Daubechies20();
            }
        }
        if (index >= 40 && index <= 41) {
            if (index == 40) {
                return new Haar1();
            }
            else {
                return new Haar1Orthogonal();
            }
        }
        if (index >= 42 && index <= 44) {
            if (index == 42) {
                return new Legendre1();
            }
            else if (index == 43) {
                return new Legendre2();
            }
            else {
                return new Legendre3();
            }
        }
        if (index >= 45 && index <= 63) {
            if (index == 45) {
                return new Symlet2();
            }
            else if (index == 46) {
                return new Symlet3();
            }
            else if (index == 47) {
                return new Symlet4();
            }
            else if (index == 48) {
                return new Symlet5();
            }
            else if (index == 49) {
                return new Symlet6();
            }
            else if (index == 50) {
                return new Symlet7();
            }
            else if (index == 51) {
                return new Symlet8();
            }
            else if (index == 52) {
                return new Symlet9();
            }
            else if (index == 53) {
                return new Symlet10();
            }
            else if (index == 54) {
                return new Symlet11();
            }
            else if (index == 55) {
                return new Symlet12();
            }
            else if (index == 56) {
                return new Symlet13();
            }
            else if (index == 57) {
                return new Symlet14();
            }
            else if (index == 58) {
                return new Symlet15();
            }
            else if (index == 59) {
                return new Symlet16();
            }
            else if (index == 60) {
                return new Symlet17();
            }
            else if (index == 61) {
                return new Symlet18();
            }
            else if (index == 62) {
                return new Symlet19();
            }
            else {
                return new Symlet20();
            }
        }
        return null;
    }
}
