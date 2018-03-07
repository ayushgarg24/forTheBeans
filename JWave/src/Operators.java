import javafx.beans.binding.IntegerBinding;

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

    public static double[] getDoubleArrayTruncated(double[] d, double[] o) {
        double[] result = new double[o.length];

        for (int i = 0; i < o.length; i++) {
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
}
