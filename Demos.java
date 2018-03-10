import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class Demos {

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException
    {
        String path = "/users/samNelson/Documents/JainCome_15sSection.wav";
        Wave wave = new Wave(path);
        double[] fakeSignal = randomList(-20, 20, 32);

        HaarTransform waveHT = new HaarTransform(wave.waveAsDoubles);//HaarTransform object
        HaarTransform fakeSignalHT = new HaarTransform(fakeSignal);  //HaarTransform object

        double[] waveTransform = waveHT.transform(//double array containing elements of signal post-transform
                waveHT.doubleSignal,              //double[] specific to the wave signal
                1);                         //THIS WILL ALWAYS BE ONE, it's necessary for recursion tho

        double[] fakeSignalTranform = fakeSignalHT.transform(//double array containing elements of signal post-transform
                fakeSignalHT.doubleSignal,                   //double[] specific to the fake signal
                1);                                    //THIS WILL ALWAYS BE ONE, it's necessary for recursion tho

        double[] thresholdedWaveTransform = hardThreshold(waveTransform, 300);         //thresholds wave transform by 300
        double[] thresholdFakeSignalTransform = hardThreshold(fakeSignalTranform, 10);//thresholds fake transform by 10

        double[] newWaveSignal = waveHT.inverseTransform(thresholdedWaveTransform, waveHT.levels);                //new double[] signal post-thresholding
        double[] newFakeSignal = fakeSignalHT.inverseTransform(thresholdFakeSignalTransform, fakeSignalHT.levels);//new double[] signal post-thresholding
        //for the two calls above
        //the levels parameter (2nd parameter) is equivelent to log base 2 of the length of each transform

        Wave newWave = new Wave(newWaveSignal, wave); //the new wave file created from the transformed and thresholded wave file
    }


    //generates double[] with random doubles
    private static double[] randomList(
            int min,
            int max,
            int amount)
    {
        double[] answer = new double[amount];
        for(int i = 0; i<amount; i++)
        {
            answer[i] = (Math.random()*(max-min)+min);
        }
        return answer;
    }

    //sets every element below value to 0
    private static double[] hardThreshold(
            double[] array,
            int value)
    {
        for(int i = 0; i<array.length; i++)
        {
            if(Math.abs(array[i])<=value)
                array[i]=0;
        }
        return array;
    }

}
