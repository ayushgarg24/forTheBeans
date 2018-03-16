import files.InvalidParamException;
import jwave.Transform;
import jwave.transforms.FastWaveletTransform;

import javax.sound.sampled.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        String path = "src/files/inputs/first20.wav";

        Wave wave = new Wave(path);

        wave.wavelet = 39;

        Transform set = new Transform(new FastWaveletTransform(Operators.getWaveletFromIndex(wave.wavelet)));

        /*double[] rightLength = Operators.getDoubleArrayOfCorrectLength(wave.waveAsDoubles);
        double[] coefs = set.forward(rightLength, 11);

        int count = 0;
        System.out.println(coefs.length);
        for (int i = 0; i < coefs.length; i++) {
            if (coefs[i] < 1000 && coefs[i] > -1000) {
                count++;
                coefs[i] = 0;
            }
        }

        System.out.println(count - (rightLength.length - wave.waveAsDoubles.length));

        Wave comp = new Wave(coefs, wave);*/
/*
        System.out.println(wave.waveAsDoubles.length);

        double[] rightLength = Operators.getDoubleArrayOfCorrectLength(wave.waveAsDoubles);

        double[] outr = set.forward(rightLength, 11);
        int count = 0;
        System.out.println(outr.length);
        for (int i = 0; i < outr.length; i++) {
            if (outr[i] < 1000 && outr[i] > -1000) {
                count++;
                outr[i] = 0;
            }
        }

        System.out.println(count - (rightLength.length - wave.waveAsDoubles.length));
        double[] revr = set.reverse(outr, 11);
        double[] output = Operators.getDoubleArrayTruncated(revr, wave.waveAsDoubles);

        Wave outWave = new Wave(output, wave);

        double[] move2 = Operators.removeZeroesFromDoubles(outr);
        Wave comp = new Wave(move2, wave);

        Wave thing = new Wave(outr, wave);

        RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
        // create a Renjin engine:
        ScriptEngine engine = factory.getScriptEngine();

        engine.put("input", wave.waveAsDoubles);
        engine.put("java", output);
        engine.put("coefs", outr);
        engine.put("shorts", outWave.waveAsShorts);
        engine.put("huff", move2);

        try {
            engine.eval("dput(input, file = \"src/files/rObjects/in.txt\")");
            engine.eval("dput(java, file = \"src/files/rObjects/javaRev.txt\")");
            engine.eval("dput(shorts, file = \"src/files/rObjects/shorts.txt\")");
            engine.eval("dput(coefs, file = \"src/files/rObjects/coefs.txt\")");
            engine.eval("dput(huff, file = \"src/files/rObjects/huff.txt\")");

        } catch (ScriptException e) {
            e.printStackTrace();
        }

        outWave.toFile("src/files/outputs/out.wav");

        thing.toBinaryFile("src/files/outputs/withzeroes.bin", "ints");*/

        //comp.toFile("src/files/outputs/comp.wav");
        //comp.toBinaryFile("src/files/outputs/binary.bin", "ints");
        /*comp.toZipFile("src/files/outputs/Z.zip");

        File f = new File("src/files/outputs/Z.zip");
        Wave player = new Wave(f);
        player.fileAudioFormat = wave.fileAudioFormat;

        //player.waveAsDoubles = Operators.getDoubleArrayOfCorrectLength(player.waveAsDoubles);

        player.waveAsDoubles = set.reverse(player.waveAsDoubles, 11);
        player.waveAsBytes = player.toBytesFromDoubles(player.waveAsDoubles);

        wave.waveAsBytes = player.toBytesFromDoubles(wave.waveAsDoubles);
        System.out.println("Playing!");
        try {
            player.playWave();
            //wave.playWave();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }*/

        /*Wave compIn = new Wave("src/files/outputs/comp.wav");
        double[] ints = new double[move2.length];
        for (int i = 0; i < move2.length; i++) {
            ints[i] = (double)(int)move2[i];
        }
        double[] in = Operators.addZeroesToDoubles(ints);
        double[] rl = Operators.getDoubleArrayOfCorrectLength(in);
        double[] rlO = set.reverse(in, 11);
        double[] compOutput = Operators.getDoubleArrayTruncated(rlO, wave.waveAsDoubles);

        Wave compOut = new Wave(compOutput, wave);

        compOut.toFile("src/files/outputs/compOut.wav");*/

        /*double[] rans = new double[100];
        for (int i = 0; i < rans.length; i++) {
            rans[i] = (int)(Math.random()*100);
        }*/
        double[] rans = wave.waveAsDoubles;
        wave.ogLength = wave.waveAsDoubles.length;
        double[] rightLength = Operators.getDoubleArrayOfCorrectLength(rans);
        wave.tLevel = (int) Math.log((double)rightLength.length);
        double[] ranResult = set.forward(rightLength, wave.tLevel);
        int count = 0;
        System.out.println(wave.waveAsDoubles.length);
        System.out.println(ranResult.length);
        for (int i = 0; i < ranResult.length; i++) {
            if (ranResult[i] < 1500 && ranResult[i] > -1500) {
                count++;
                ranResult[i] = 0;
            }
            else {
                if (ranResult[i] > 0) {
                    ranResult[i] = ranResult[i] - 1500;
                }
                else {
                    ranResult[i] = ranResult[i] + 1500;
                }
            }
        }
        System.out.println(count - (rightLength.length - wave.waveAsDoubles.length));
        Wave ranWave = new Wave(Operators.getDoubleArrayTruncated(ranResult, wave.ogLength), wave);
        ranWave.fileAudioFormat = wave.fileAudioFormat;
        ranWave.tLevel = wave.tLevel;
        ranWave.wavelet = wave.wavelet;
        ranWave.ogLength = wave.ogLength;
        ranWave.toZipFile("src/files/outputs/ran.zip");
        wave.toZipFile("src/files/outputs/wave.zip");

        String p = "src/files/outputs/ran.zip";
        Wave ranIn = new Wave(p);

        Transform set2 = new Transform(new FastWaveletTransform(Operators.getWaveletFromIndex(ranIn.wavelet)));

        ranIn.waveAsDoubles = Operators.getDoubleArrayTruncated(set2.reverse(Operators.getDoubleArrayOfCorrectLength(ranIn.waveAsDoubles), ranIn.tLevel), rans.length);

        ranIn.waveAsBytes = ranIn.toBytesFromDoubles(ranIn.waveAsDoubles);

        wave.waveAsBytes = wave.toBytesFromDoubles(wave.waveAsDoubles);

        try {
            wave.simpleDownSample(2);
        } catch (InvalidParamException e) {
            e.printStackTrace();
        }

        wave.toSimpleZip("src/files/outputs/waveDown.zip");

        System.out.println("Playing!");
            ranIn.playWave();
            //wave.playWave();

        System.out.println("Done");

        //downsampling
        //accustioc model


    }
}
