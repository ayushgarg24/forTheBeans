package DesktopPlayer;

import files.InvalidParamException;
import jwave.Transform;
import jwave.transforms.FastWaveletTransform;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Wave object class, used for the storing and manipulation of audio files, including the reading, writing, and compression of such files.
 *
 * To-Do: Add support for streaming audio in playback and compression. The ability of the servlet to chuck and audio file and compress it, and the ability to dynamically build
 * a Wave object as data arrives and playback from a buffer rather than a fixed array.
 *
 * Playback control utility for the frontend, either by direct access methods, or by returning the clip object for manipulation by another object.
 */
public class Wave {
    //<editor-fold desc="Wave Object Global Variables">
    protected double[] waveAsDoubles;
    protected short[] waveAsShorts;
    protected byte[] waveAsBytes;
    protected int[] shiftedInts;

    protected AudioFileFormat fileFormat;
    protected AudioFileFormat.Type fileType;
    protected AudioFormat fileAudioFormat;

    protected File file;
    protected URL waveURL;
    protected int tLevel;

    protected int wavelet;

    protected int ogLength;

    protected SourceDataLine s;

    protected FloatControl f;
    //</editor-fold>

    //<editor-fold desc="Obsolete">
    /**
     * Wave object constructor that takes a String path, loading a corresponding java.io.File and loading audio format info using AudioSystem.
     * The waveAsDoubles array is then populated by the readWaveAsDoubles method with the File object as a parameter.
     *
     * To-Do: Update constructor to receive String/URL, and load file local or over http, as well as determine if ZIP or WAV format. Combine.
     *
     * @date 3.11.2018 21:48:00
     * @author Alex Radovan (alexradocole@gmail.com)
     * @param path
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    /*public Wave(String path) throws IOException, UnsupportedAudioFileException {
        file = new File(path);

        fileFormat = AudioSystem.getAudioFileFormat(file);
        fileType = fileFormat.getType();
        fileAudioFormat = fileFormat.getFormat();
        waveAsDoubles = readWaveAsDoubles(file);
    }*/
    //</editor-fold>

    //<editor-fold desc="Obsolete">
    /**
     * Wave object constructor that takes a java.io.File and unzips data, loading header information into appropriate fields, and reading input stream into the Wave's waveAsDouble array.
     * Shifting is done during read-in according to the shift parameter, specified by the first byte of the header.
     * A byte representation of the array is stored in the waveAsBytes array, calculated from the waveAsDoubles array.
     *
     * To-Do: Update constructor to receive String/URL, and load file local or over http, as well as determine if ZIP or WAV format. Combine.
     *
     * @date 3.11.2018 21:31:00
     * @author Alex Radovan (alexradocole@gmail.com)
     * @param f
     * @throws IOException
     */
    /*public Wave(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int read;
        byte[] buff = new byte[1024];
        while (zis.getNextEntry() != null) {
            while ((read = zis.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        }
        out.flush();

        byte[] result = out.toByteArray();

        int shift = result[0];

        float sampleRate = (float) ((result[1] & 0xff) | ((result[2] << 8) & 0x0000FF00) | ((result[3] << 16) & 0x00ff0000) | ((result[4] << 24) & 0xff000000));
        int sampleSizeInBits = result[5];
        int channels = result[6];
        boolean signed = result[7] == 1;
        boolean bigEndian = result[8] == 1;
        fileAudioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

        tLevel = result[9];

        wavelet = result[10];

        ogLength = ((result[11] & 0xff) | ((result[12] << 8) & 0x0000FF00) | ((result[13] << 16) & 0x00ff0000) | ((result[14] << 24) & 0xff000000));

        byte[] r = new byte[result.length - 15];
        for (int i = 15; i < result.length; i++) {
            r[i - 15] = result[i];
        }

        double[] doubles = new double[r.length/4];

        for (int i = 0; i < doubles.length; i++) {
            byte byteA = r[((i+1)*4)-4];
            byte byteB = r[((i+1)*4)-3];
            byte byteC = r[((i+1)*4)-2];
            byte byteD = r[((i+1)*4)-1];
            int intA = byteA & 0xff;
            int intB = (byteB << 8) & 0x0000FF00;
            int intC = (byteC << 16) & 0x00ff0000;
            int intD = (byteD << 24) & 0xff000000;
            if (shift == 0) {
                doubles[i] = (double) (intA | intB | intC | intD);
            }
            else {
                doubles[i] = ((double) (intA | intB | intC | intD)) / (Math.pow(10, shift-1));
            }
        }

        waveAsDoubles = doubles;

        waveAsBytes = toBytesFromDoubles(doubles);
    }*/
    //</editor-fold>

    /**
     * Wave object constructor that takes a String path pointing to either a local file or URL. Path is parsed to determine whether to load the Wave object from ZIP or WAV.
     *
     * FOR ZIP:
     * Data is unzipped, loading header information into appropriate fields, and reading input stream into the Wave's waveAsDouble array.
     * Shifting is done during read-in according to the shift parameter, specified by the first byte of the header.
     * A byte representation of the array is stored in the waveAsBytes array, calculated from the waveAsDoubles array.
     *
     * FOR WAV:
     * Audio format info is loaded using AudioSystem.
     * The waveAsDoubles array is then populated by the readWaveAsDoubles method with the URL object as a parameter.
     *
     * Input path is stored in waveURL as a URL object regardless of type.
     *
     * @date 3.12.2018 20:26:00
     * @author Alex Radovan (alexradocole@gmail.com)
     * @param path
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public Wave(String path) throws IOException, UnsupportedAudioFileException {
        URL url = null;
        if (path.contains("zip")) {     //Determine if given path points to a compressed or uncompressed file, and load Wave.
            //<editor-fold desc="Read Wave from compressed ZIP.">
            if (path.contains("http")) {    //Determine if given path is a url, and load Wave.
                url = new URL(path);
            } else {
                url = new File(path).toURI().toURL();
            }
            //FileInputStream fis = new FileInputStream();
            InputStream in = url.openStream();

            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(in));

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int read;
            byte[] buff = new byte[1024];
            while (zis.getNextEntry() != null) {
                while ((read = zis.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            }
            out.flush();

            byte[] result = out.toByteArray();

            int shift = result[0];

            float sampleRate = (float) ((result[1] & 0xff) | ((result[2] << 8) & 0x0000FF00) | ((result[3] << 16) & 0x00ff0000) | ((result[4] << 24) & 0xff000000));
            int sampleSizeInBits = result[5];
            int channels = result[6];
            boolean signed = result[7] == 1;
            boolean bigEndian = result[8] == 1;
            fileAudioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

            tLevel = result[9];

            wavelet = result[10];

            ogLength = ((result[11] & 0xff) | ((result[12] << 8) & 0x0000FF00) | ((result[13] << 16) & 0x00ff0000) | ((result[14] << 24) & 0xff000000));

            byte[] r = new byte[result.length - 15];
            for (int i = 15; i < result.length; i++) {
                r[i - 15] = result[i];
            }

            double[] doubles = new double[r.length/4];

            for (int i = 0; i < doubles.length; i++) {
                byte byteA = r[((i+1)*4)-4];
                byte byteB = r[((i+1)*4)-3];
                byte byteC = r[((i+1)*4)-2];
                byte byteD = r[((i+1)*4)-1];
                int intA = byteA & 0xff;
                int intB = (byteB << 8) & 0x0000FF00;
                int intC = (byteC << 16) & 0x00ff0000;
                int intD = (byteD << 24) & 0xff000000;
                if (shift == 0) {
                    doubles[i] = (double) (intA | intB | intC | intD);
                }
                else {
                    doubles[i] = ((double) (intA | intB | intC | intD)) / (Math.pow(10, shift-1));
                }
            }

            waveAsDoubles = doubles;

            waveAsBytes = toBytesFromDoubles(doubles);
            //</editor-fold>
        }
        else {      //Path points to an uncompressed audio file, load Wave.
            //<editor-fold desc="Read Wave from .wav file.">
            if (path.contains("http")) {    //Determine if given path is a url, and load Wave.
                url = new URL(path);
            } else {
                url = new File(path).toURI().toURL();
            }

            fileFormat = AudioSystem.getAudioFileFormat(url);
            fileType = fileFormat.getType();
            fileAudioFormat = fileFormat.getFormat();
            waveAsDoubles = readWaveAsDoubles(url);
            //</editor-fold>
        }
        waveURL = url;
    }

    /**
     * Alternate Wave object constructor that takes a Double array and a previous Wave object, populating array based on the given Double array.
     * AudioFormat information is loaded from the previous Wave object.
     *
     * @date 3.11.2018 21:51:00
     * @author Alex Radovan (alexradocole@gmail.com)
     * @param d
     * @param r
     */
    public Wave(double[] d, Wave r) {
        waveAsDoubles = d;
        waveAsShorts = shortsFromDoubles(d);
        waveAsBytes = bytesFromShorts(waveAsShorts);

        fileFormat = r.fileFormat;
        fileType = r.fileType;
        fileAudioFormat = r.fileAudioFormat;
    }

    public double[] readWaveAsDoubles(URL u) throws IOException, UnsupportedAudioFileException {
        short[] shorts = readWaveAsShorts(u);

        double[] result = new double[shorts.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = shorts[i];
        }

        return result;
    }

    public byte[] readWaveAsBytes(URL u) throws IOException, UnsupportedAudioFileException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = AudioSystem.getAudioInputStream(u);

        int read;
        byte[] buff = new byte[1024];
        while ((read = in.read(buff)) > 0)
        {
            out.write(buff, 0, read);
        }
        out.flush();

        byte[] result = out.toByteArray();

        waveAsBytes = result;
        return result;
    }

    public short[] readWaveAsShorts(URL u) throws IOException, UnsupportedAudioFileException {
        byte[] bytes = readWaveAsBytes(u);

        short[] shorts = new short[bytes.length/2];

        for (int i = 0; i < shorts.length; i++) {
            /*ByteBuffer bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.put(bytes[((i+1)*2)-2]);
            bb.put(bytes[((i+1)*2)-1]);
            shorts[i] = bb.getShort(0);*/
            byte byteA = bytes[((i+1)*2)-2];
            byte byteB = bytes[((i+1)*2)-1];
            int intA = byteA & 0xff;
            int intB = (byteB << 8);
            shorts[i] = (short) (intA | intB);
        }

        waveAsShorts = shorts;
        return shorts;
    }

    public short[] shortsFromDoubles(double[] d) {
        short[] shorts = new short[d.length];

        for (int i = 0; i < shorts.length; i++) {
            shorts[i] = (short) d[i];
        }

        return shorts;
    }

    public byte[] bytesFromShorts(short[] s) {
        byte[] bytes = new byte[s.length*2];

        for (int i = 0; i < s.length; i++) {
            bytes[((i+1)*2)-2] = (byte)(s[i] & 0xff);
            bytes[((i+1)*2)-1] = (byte)((s[i] >> 8) & 0xff);
        }

        return bytes;
    }

    /**
     * Simple method to save Wave object as an audio file to the local system, mostly only for use in testing.
     *
     * To-Do: Possible frontend implementation to store streamed/downloaded audio to local machine.
     *
     * @date 3.12.2018 20:32:00
     * @author Alex Radovan (alexradocole@gmail.com)
     * @param path
     */
    public void toFile(String path) {
        File output = new File(path);

        ByteArrayInputStream bais = new ByteArrayInputStream(waveAsBytes);

        AudioInputStream outputAIS = new AudioInputStream(bais, fileAudioFormat, waveAsBytes.length/fileAudioFormat.getFrameSize());

        try {
            int nWrittenBytes = AudioSystem.write(outputAIS, fileType, output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        file = new File(path);
    }

    /**
     * Simple method to save Wave object as a binary file to the local system. Primarily for testing.
     *
     * To-Do: Possible frontend implementation to store streamed/downloaded audio to local machine.
     *
     * @date 3.12.2018 20:35:00
     * @author Alex Radovan (alexradocole@gmail.com)
     * @param path
     * @param type
     * @throws IOException
     */
    public void toBinaryFile(String path, String type) throws IOException {
        DataOutputStream os = new DataOutputStream(new FileOutputStream(path));
        if (type == "doubles") {
            for (int i = 0; i < waveAsDoubles.length; i++) {
                os.writeDouble(waveAsDoubles[i]);
            }
        }
        if (type == "ints") {
            for (int i = 0; i < waveAsDoubles.length; i++) {
                os.writeInt((int)waveAsDoubles[i]);
            }
        }
        if (type == "shorts") {
            for (int i = 0; i < waveAsShorts.length; i++) {
                os.writeShort(waveAsShorts[i]);
            }
        }
        os.close();
    }

    public void toZipFile(String path) throws IOException {
        FileOutputStream os = new FileOutputStream(path);
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(os));
        zos.setMethod(ZipOutputStream.DEFLATED);

        byte[] header = new byte[15];

        header[0] = (byte)toShiftedInts(waveAsDoubles);

        header[1] = (byte) (((int)fileAudioFormat.getSampleRate()) & 0xff);
        header[2] = (byte) ((((int)fileAudioFormat.getSampleRate()) >> 8) & 0xff);
        header[3] = (byte) ((((int)fileAudioFormat.getSampleRate()) >> 16) & 0xff);
        header[4] = (byte) ((((int)fileAudioFormat.getSampleRate()) >> 24) & 0xff);

        header[5] = (byte) fileAudioFormat.getSampleSizeInBits();

        header[6] = (byte) fileAudioFormat.getChannels();

        if (fileAudioFormat.getEncoding().toString().equals("PCM_SIGNED")) {
            header[7] = 1;
        }
        else {
            header[7] = 0;
        }
        if (fileAudioFormat.isBigEndian()) {
            header[8] = 1;
        }
        else {
            header[8] = 0;
        }

        header[9] = (byte) tLevel;

        header[10] = (byte) wavelet;

        header[1] = (byte) ((ogLength) & 0xff);
        header[12] = (byte) (((ogLength) >> 8) & 0xff);
        header[13] = (byte) (((ogLength) >> 16) & 0xff);
        header[14] = (byte) (((ogLength) >> 24) & 0xff);

        byte[] bytes = new byte[shiftedInts.length*4];

        for (int i = 0; i < shiftedInts.length; i++) {
            bytes[((i+1)*4)-4] = (byte)(shiftedInts[i] & 0xff);
            bytes[((i+1)*4)-3] = (byte)((shiftedInts[i] >> 8) & 0xff);
            bytes[((i+1)*4)-2] = (byte)((shiftedInts[i] >> 16) & 0xff);
            bytes[((i+1)*4)-1] = (byte)((shiftedInts[i] >> 24) & 0xff);
        }

        byte[] newBytes = new byte[bytes.length + header.length];
        for (int i = 0; i < header.length; i++) {
            newBytes[i] = header[i];
        }
        for (int i = header.length; i < bytes.length - header.length; i++) {
            newBytes[i] = bytes[i - header.length];
        }

        ZipEntry entry = new ZipEntry("file");
        zos.putNextEntry(entry);
        zos.write(newBytes);
        zos.close();
    }

    public void toZipStream(OutputStream os) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(os);
        zos.setMethod(ZipOutputStream.DEFLATED);

        byte[] header = new byte[15];

        header[0] = (byte)toShiftedInts(waveAsDoubles);

        header[1] = (byte) (((int)fileAudioFormat.getSampleRate()) & 0xff);
        header[2] = (byte) ((((int)fileAudioFormat.getSampleRate()) >> 8) & 0xff);
        header[3] = (byte) ((((int)fileAudioFormat.getSampleRate()) >> 16) & 0xff);
        header[4] = (byte) ((((int)fileAudioFormat.getSampleRate()) >> 24) & 0xff);

        header[5] = (byte) fileAudioFormat.getSampleSizeInBits();

        header[6] = (byte) fileAudioFormat.getChannels();

        if (fileAudioFormat.getEncoding().toString().equals("PCM_SIGNED")) {
            header[7] = 1;
        }
        else {
            header[7] = 0;
        }
        if (fileAudioFormat.isBigEndian()) {
            header[8] = 1;
        }
        else {
            header[8] = 0;
        }

        header[9] = (byte) tLevel;

        header[10] = (byte) wavelet;

        header[1] = (byte) ((ogLength) & 0xff);
        header[12] = (byte) (((ogLength) >> 8) & 0xff);
        header[13] = (byte) (((ogLength) >> 16) & 0xff);
        header[14] = (byte) (((ogLength) >> 24) & 0xff);

        byte[] bytes = new byte[shiftedInts.length*4];

        for (int i = 0; i < shiftedInts.length; i++) {
            bytes[((i+1)*4)-4] = (byte)(shiftedInts[i] & 0xff);
            bytes[((i+1)*4)-3] = (byte)((shiftedInts[i] >> 8) & 0xff);
            bytes[((i+1)*4)-2] = (byte)((shiftedInts[i] >> 16) & 0xff);
            bytes[((i+1)*4)-1] = (byte)((shiftedInts[i] >> 24) & 0xff);
        }

        byte[] newBytes = new byte[bytes.length + header.length];
        for (int i = 0; i < header.length; i++) {
            newBytes[i] = header[i];
        }
        for (int i = header.length; i < bytes.length - header.length; i++) {
            newBytes[i] = bytes[i - header.length];
        }

        ZipEntry entry = new ZipEntry("file");
        zos.putNextEntry(entry);
        zos.write(newBytes);
        zos.close();
    }

    public void toSimpleZip(String path) throws IOException {
        FileOutputStream os = new FileOutputStream(path);
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(os));
        zos.setMethod(ZipOutputStream.DEFLATED);

        ZipEntry entry = new ZipEntry("file");
        zos.putNextEntry(entry);
        zos.write(waveAsBytes);
        zos.close();
    }

    public byte[] getBytesFromDoubles(double[] d) {
        byte[] bytes = new byte[d.length*4];

        for (int i = 0; i < d.length; i++) {
            byte a = bytes[((i+1)*4)-4] = (byte)((int)d[i] & 0xff);
            byte b = bytes[((i+1)*4)-3] = (byte)(((int)d[i] >> 8) & 0xff);
            byte c = bytes[((i+1)*4)-2] = (byte)(((int)d[i] >> 16) & 0xff);
            byte e = bytes[((i+1)*4)-1] = (byte)((c >> 8) & 0xff);
        }

        return bytes;
    }

    public byte[] toBytesFromDoubles(double[] f) {
        /*byte[] bytes = new byte[f.length*4];

        for (int i = 0; i < f.length; i++) {
            byte a = bytes[((i+1)*4)-4] = (byte)((int)f[i] & 0xff);
            byte b = bytes[((i+1)*4)-3] = (byte)(((int)f[i] >> 8) & 0xff);
            byte c = bytes[((i+1)*4)-2] = (byte)(((int)f[i] >> 16) & 0xff);
            byte d = bytes[((i+1)*4)-1] = (byte)(((int)f[i] >> 24) & 0xff);
        }
        if (fileAudioFormat != null) {
            AudioFormat format = new AudioFormat(fileAudioFormat.getSampleRate(), 32, fileAudioFormat.getChannels(), true, fileAudioFormat.isBigEndian());
            fileAudioFormat = format;
        }*/
        byte[] bytes = new byte[f.length*2];
        for (int i = 0; i < f.length; i++) {
            short s = (short) f[i];
            byte a = bytes[((i+1)*2)-2] = (byte)(s & 0xff);
            byte b = bytes[((i+1)*2)-1] = (byte)((s >> 8) & 0xff);
        }

        return bytes;
    }

    /**
     * Utility method used for compressing Wavelet coefficients for ZIPing.
     * The bandwidth of a double array is calculated and used to shift the decimals of the array right.
     * The resulting doubles are then truncated at that decimal and stored in the shiftedInts array.
     *
     * The method returns the number of digits the resulting array has been shifted right.
     *
     * @param d
     * @return the number of digits the resulting shiftedInts array was shifted right.
     */
    public int toShiftedInts(double[] d) {
        int[] result = new int[d.length];

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

        double abs = 0;
        double sqrt = Math.pow(2, 32);

        min = Math.abs(min);
        max = Math.abs(max);

        if (min > max) {
            abs = min;
        }
        else {
            abs = max;
        }

        int dig = 0;
        int t = 10;
        while(sqrt/2 > Math.pow(10, dig)*abs) {
            dig++;
        }

        int times = (int)Math.pow(10, dig-1);

        for (int i = 0; i < d.length; i++) {
            result[i] = (int) (d[i] * times);
        }

        shiftedInts = result;

        return dig;
    }

    /**
     * ISSUES: Playback will continue with GUI, no need for blocking. CountDownLatch blocks all activity on Thread until playback completes.
     * Simple playback method for Wave objects, opens a javax.sound.sampled.Clip and opens using the waveAsBytes array and the Wave's AudioFormat given by fileAudioFormat.
     * Playback continues through the end of the clip with the thread being maintained by a CountDownLatch triggered by a LineEvent listener waiting for the Clip to a throw a STOP event.
     *
     * @date 3.11.2018 21:38:00
     * @author Alex Radovan (alexradocole@gmail.com)
     */
    public void playWave() {
        Clip c = null;
        try {
            c = AudioSystem.getClip();
            c.open(fileAudioFormat, waveAsBytes, 0, waveAsBytes.length);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        c.start();
        f = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
    }

    public void streamWave() {
        Thread stream = new Thread() {
            public void run() {
                try {
                    s = AudioSystem.getSourceDataLine(fileAudioFormat);
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }
                try {
                    s.open(fileAudioFormat, 88224);
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }
                s.start();
                for (int i = 0; i < waveAsBytes.length; i++) {
                    //byte[] b = Arrays.copyOfRange(waveAsBytes, i, i + 22056);
                    //int byteswrite = s.write(b, 0, b.length);
                    //i = i + b.length - 1;
                }
            }
        };
        stream.start();
        //s.close();
    }

    /**
     * UNTESTED: Variable Scale Factor
     * Simple downsample method which discards every 1 - (1/n) frames of data using Wave's waveAsBytes array.
     * Wave's fileAudioFormat is replaced with new AudioFormat with correct sample rate for playback.
     * waveAsBytes value's are replaced with new values.
     *
     * @date 3.11.2018 20:01:00
     * @author Alex Radovan (alexradocole@gmail.com)
     * @param s where s is a non-zero multiple of 2
     * @throws InvalidParamException
     */
    public void simpleDownSample(int s) throws InvalidParamException {
        if (s == 0) {
            throw new InvalidParamException("Param S must be non-zero!");
        }
        else if ((s % 2) == 1) {
            throw new InvalidParamException("Param S must be a multiple of 2!");
        }
        else {
            byte[] newBytes = new byte[waveAsBytes.length / s];

            int f = fileAudioFormat.getFrameSize();

            int j = 0;
            for (int i = 0; i < newBytes.length; i++) {
                if ((j % f) == 0 && j != 0) {
                    j = j + (f * (s - 1));
                }
                try {
                    newBytes[i] = waveAsBytes[j];
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                j++;
            }
            AudioFormat format = new AudioFormat(fileAudioFormat.getSampleRate() / s, fileAudioFormat.getSampleSizeInBits(), fileAudioFormat.getChannels(), true, fileAudioFormat.isBigEndian());
            fileAudioFormat = format;

            waveAsBytes = newBytes;
        }
    }

    /**
     * Given a threshold level, this method will perform a forward wavelet transform using the Wave's wavelet type.
     * Thresholding will be applied and resulting array will be stored in waveAsDoubles array.
     *
     * @date 3.16.2018 9:50:00
     * @author Alex Radovan (alexradocole@gmail.com)
     * @param thresh
     */
    public void compress(int thresh) {
        Transform set = new Transform(new FastWaveletTransform(Operators.getWaveletFromIndex(wavelet)));

        ogLength = waveAsDoubles.length;
        waveAsDoubles = Operators.getDoubleArrayOfCorrectLength(waveAsDoubles);
        tLevel = (int) Math.log((double)waveAsDoubles.length);
        waveAsDoubles = set.forward(waveAsDoubles, tLevel);
        for (int i = 0; i < waveAsDoubles.length; i++) {
            if (waveAsDoubles[i] < thresh && waveAsDoubles[i] > -thresh) {
                waveAsDoubles[i] = 0;
            }
            else {
                if (waveAsDoubles[i] > 0) {
                    waveAsDoubles[i] = waveAsDoubles[i] - thresh;
                }
                else {
                    waveAsDoubles[i] = waveAsDoubles[i] + thresh;
                }
            }
        }

        waveAsDoubles = Operators.getDoubleArrayTruncated(waveAsDoubles, ogLength);
    }

    /**
     * Decompresses a Wave object, expanding the Wave using a FWT given by the Wave's wavelet value.
     * The reverse transformation is applied to a length adjusted array created from the Wave's waveAsDoubles array, at a level specified by the Wave's tLevel value.
     * Padding of the array and the subsequent de-padding after transformation is achieved with the use of the ogLength value, present in the ZIP header.
     *
     * Decompressed values are stored back in the Wave's waveAsDoubles array, and translated into bytes and stored in the waveAsBytes array.
     *
     * @date 3.11.2018 19:22:00
     * @author Alex Radovan (alexradocole@gmail.com)
     */
    public void decompress() {
        Transform t = new Transform(new FastWaveletTransform(Operators.getWaveletFromIndex(wavelet)));

        waveAsDoubles = Operators.getDoubleArrayTruncated(t.reverse(Operators.getDoubleArrayOfCorrectLength(waveAsDoubles), tLevel), ogLength);

        waveAsBytes = toBytesFromDoubles(waveAsDoubles);
    }

    public byte[] addToWave(String path) throws IOException, UnsupportedAudioFileException {
        URL url = null;
        byte[] resultant = null;
        if (path.contains("zip")) {     //Determine if given path points to a compressed or uncompressed file, and load Wave.
            //<editor-fold desc="Read Wave from compressed ZIP.">
            if (path.contains("http")) {    //Determine if given path is a url, and load Wave.
                url = new URL(path);
            } else {
                url = new File(path).toURI().toURL();
            }
            //FileInputStream fis = new FileInputStream();
            InputStream in = url.openStream();

            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(in));

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int read;
            byte[] buff = new byte[1024];
            while (zis.getNextEntry() != null) {
                while ((read = zis.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            }
            out.flush();

            byte[] result = out.toByteArray();

            int shift = result[0];

            float sampleRate = (float) ((result[1] & 0xff) | ((result[2] << 8) & 0x0000FF00) | ((result[3] << 16) & 0x00ff0000) | ((result[4] << 24) & 0xff000000));
            int sampleSizeInBits = result[5];
            int channels = result[6];
            boolean signed = result[7] == 1;
            boolean bigEndian = result[8] == 1;
            fileAudioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

            tLevel = result[9];

            wavelet = result[10];

            ogLength = ((result[11] & 0xff) | ((result[12] << 8) & 0x0000FF00) | ((result[13] << 16) & 0x00ff0000) | ((result[14] << 24) & 0xff000000));

            byte[] r = new byte[result.length - 15];
            for (int i = 15; i < result.length; i++) {
                r[i - 15] = result[i];
            }

            double[] doubles = new double[r.length/4];

            for (int i = 0; i < doubles.length; i++) {
                byte byteA = r[((i+1)*4)-4];
                byte byteB = r[((i+1)*4)-3];
                byte byteC = r[((i+1)*4)-2];
                byte byteD = r[((i+1)*4)-1];
                int intA = byteA & 0xff;
                int intB = (byteB << 8) & 0x0000FF00;
                int intC = (byteC << 16) & 0x00ff0000;
                int intD = (byteD << 24) & 0xff000000;
                if (shift == 0) {
                    doubles[i] = (double) (intA | intB | intC | intD);
                }
                else {
                    doubles[i] = ((double) (intA | intB | intC | intD)) / (Math.pow(10, shift-1));
                }
            }

            waveAsDoubles = doubles;

            waveAsBytes = toBytesFromDoubles(doubles);
            //</editor-fold>
        }
        else {      //Path points to an uncompressed audio file, load Wave.
            //<editor-fold desc="Read Wave from .wav file.">
            if (path.contains("http")) {    //Determine if given path is a url, and load Wave.
                url = new URL(path);
            } else {
                url = new File(path).toURI().toURL();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = url.openStream();

            int read;
            byte[] buff = new byte[1024];
            while ((read = in.read(buff)) > 0)
            {
                out.write(buff, 0, read);
            }
            out.flush();

            resultant = out.toByteArray();

            byte[] temp = new byte[resultant.length + waveAsBytes.length];

            for (int i = 0; i < temp.length; i++) {
                if (i < waveAsBytes.length) {
                    temp[i] = waveAsBytes[i];
                }
                else {
                    temp[i] = resultant[i - waveAsBytes.length];
                }
            }

            waveAsBytes = temp;
            //</editor-fold>
        }
        return resultant;
    }
}
