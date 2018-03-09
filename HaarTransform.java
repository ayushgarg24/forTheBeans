import java.util.Arrays;

/* -----------------------------------------------------
@Purpose:

@Date: dd/mm/yyyy

@Modified:
    -
@Notes:
*///----------------------------------------------------

public class HaarTransform {

    public double[] doubleSignal;
    public int originalLength;
    public int levels;



    /* -----------------------------------------------------
    @Purpose: Constructor, instantiates doubleSignal, originalLength, and levels

    @Date: 09/03/2018 10:25am dd/mm/yyyy

    @Modified:
        -
    @Notes:
        - takes integer array
    *///----------------------------------------------------
    public HaarTransform(
            int[] signal)
    {
        this.doubleSignal = copyToDoubleArray(signal);
        this.levels = (int)(Math.log(signal.length)/Math.log(2));
        this.originalLength = signal.length;
    }

    /* -----------------------------------------------------
    @Purpose: Constructor, instantiates doubleSignal, originalLength, and levels

    @Date: 09/03/2018 10:25am dd/mm/yyyy

    @Modified:
        -
    @Notes:
        - takes double array
    *///----------------------------------------------------
    public HaarTransform(
            double[] signal)
    {
        this.doubleSignal = signal;
        this.levels = (int)(Math.log(signal.length)/Math.log(2));
        this.originalLength = signal.length;
    }



    /* -----------------------------------------------------
    @Purpose: Method, haar transform, takes signal as double array, when calling the method, level should always be 1

    @Date: 09/03/2018 11:04am dd/mm/yyyy

    @Modified:
        -
    @Notes:
    *///----------------------------------------------------
    public double[] transform(
            double[] array,
            int level)
    {
        if(level == this.levels+1)
            return array;
        int len = array.length;
        array = addZeros(array);
        double[] buffer = transformForward(array, level);
        return transform(buffer, level + 1);
    }

    public double[] transform(
            int[] array,
            int level)
    {
        double[] newSignal = copyToDoubleArray(array);
        return transform(newSignal, 1);
    }


    /* -----------------------------------------------------
    @Purpose: adds zeros so signal length is of the order 2^n

    @Date: 09/03/2018 11:04am dd/mm/yyyy

    @Modified:
        -
    @Notes:
    *///----------------------------------------------------
    private double[] addZeros(
            double[] array)
    {
        int len = array.length;
        if(Math.log(len)/Math.log(2)%1==0)
            return array;
        int multipleOfTwo = ((int)(Math.log(len)/Math.log(2)))+1;
        int add = (int)(Math.pow(2,multipleOfTwo)-len);
        double[] newArray = new double[len+add];
        for(int i = 0; i<len; i++)
        {
            newArray[i] = array[i];
        }
        for(int i = len; i<(len+add); i++)
        {
            newArray[i] = 0;
        }
        return newArray;
    }



    /* -----------------------------------------------------
    @Purpose: Method, rebuilds signal from the signal's transformed state

    @Date: 09/03/2018 11:06am dd/mm/yyyy

    @Modified:
        -
    @Notes:
        - level should be the number of levels it took to fully decompose the signal
        - e.g. <HaarTranformInstance>.inverseTransform(transformedSignal, <HaarTransformInstance>.levels);
    *///----------------------------------------------------
    public double[] inverseTransform(
            double[] transform,
            int level)
    {
        if(level == 0)
            return transform;
        double[] buffer = transform;
        buffer = transformBackward(transform, level);
        return inverseTransform(buffer, level-1);
    }


    /* -----------------------------------------------------
    @Purpose: helper method for inverse transform

    @Date: 09/03/2018 11:12am dd/mm/yyyy

    @Modified:
        -
    @Notes:
    *///----------------------------------------------------
    private double[] transformBackward(
            double[] array,
            int level)
    {
        int sIndex = (int)(array.length/Math.pow(2,level));
        double[] buffer = Arrays.copyOf(array, array.length);
        for(int i = 0; i<sIndex; i++)
        {
            buffer[2*i] = array[sIndex+i] + array[i];
            buffer[(2*i)+1] = array[i] - array[sIndex+i];
        }
        return buffer;
    }

    /* -----------------------------------------------------
    @Purpose: helper method for transform

    @Date: 09/03/2018 11:13am dd/mm/yyyy

    @Modified:
        -
    @Notes:
    *///----------------------------------------------------
    private double[] transformForward(
            double[] array,
            int level)
    {
        int sIndex = (int)(array.length/Math.pow(2, level));
        double[] buffer = Arrays.copyOf(array, array.length);
        for(int i = 0; i<sIndex; i++)
        {
            buffer[i] = (array[i*2]+array[(i*2)+1])/2;
            buffer[sIndex+i] = array[(i*2)]-buffer[i];
        }
        return buffer;
    }


    //very self-explanatory
    private double[] copyToDoubleArray(
            int[] a)
    {
        double[] answer = new double[a.length];
        for(int i = 0; i<a.length; i++)
        {
            answer[i] = a[i];
        }
        return answer;
    }
    private int[] copyToIntArray(
            double[] a)
    {
        int[] answer = new int[a.length];
        for(int i = 0; i<a.length; i++)
            answer[i] = (int)a[i];
        return answer;
    }
}
