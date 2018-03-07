import org.apache.commons.vfs2.util.FileObjectUtils;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Wave {
    protected double[] waveAsDoubles;
    protected short[] waveAsShorts;
    protected byte[] waveAsBytes;
    protected int[] shiftedInts;

    protected AudioFileFormat fileFormat;
    protected AudioFileFormat.Type fileType;
    protected AudioFormat fileAudioFormat;

    protected File file;

    public Wave(String path) throws IOException, UnsupportedAudioFileException {
        file = new File(path);

        fileFormat = AudioSystem.getAudioFileFormat(file);
        fileType = fileFormat.getType();
        fileAudioFormat = fileFormat.getFormat();
        waveAsDoubles = readWaveAsDoubles(file);
    }

    public Wave(double[] d, Wave r) {
        waveAsDoubles = d;
        waveAsShorts = shortsFromDoubles(d);
        waveAsBytes = bytesFromShorts(waveAsShorts);

        fileFormat = r.fileFormat;
        fileType = r.fileType;
        fileAudioFormat = r.fileAudioFormat;
    }

    public Wave(File f) throws IOException {
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

        byte[] r = new byte[result.length - 1];
        for (int i = 1; i < result.length; i++) {
            r[i - 1] = result[i];
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
    }

    public double[] readWaveAsDoubles(File f) throws IOException, UnsupportedAudioFileException {
        short[] shorts = readWaveAsShorts(f);

        double[] result = new double[shorts.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = shorts[i];
        }

        return result;
    }

    public byte[] readWaveAsBytes(File f) throws IOException, UnsupportedAudioFileException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = AudioSystem.getAudioInputStream(f);

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

    public short[] readWaveAsShorts(File f) throws IOException, UnsupportedAudioFileException {
        byte[] bytes = readWaveAsBytes(f);

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

        byte[] header = {(byte)toShiftedInts(waveAsDoubles)};

        byte[] bytes = new byte[shiftedInts.length*4];

        for (int i = 0; i < shiftedInts.length; i++) {
            byte a = bytes[((i+1)*4)-4] = (byte)(shiftedInts[i] & 0xff);
            byte b = bytes[((i+1)*4)-3] = (byte)((shiftedInts[i] >> 8) & 0xff);
            byte c = bytes[((i+1)*4)-2] = (byte)((shiftedInts[i] >> 16) & 0xff);
            byte d = bytes[((i+1)*4)-1] = (byte)((shiftedInts[i] >> 24) & 0xff);
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

    public void playWave() throws LineUnavailableException {
        Clip c = AudioSystem.getClip();
        c.open(fileAudioFormat, waveAsBytes, 0, waveAsBytes.length);
        c.start();
        JOptionPane.showMessageDialog(null, "Click OK to stop music");
    }

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

    public void downSample() {
        byte[] newBytes = new byte[waveAsBytes.length/10];
        int j = 0;
        for (int i = 0; i < newBytes.length; i++) {
            if ((j % 4) == 0 && j != 0) {
                j = j + 36;
            }
            try {
                newBytes[i] = waveAsBytes[j];
            }
            catch (ArrayIndexOutOfBoundsException e)  {
                e.printStackTrace();
            }
            j++;
        }
        AudioFormat format = new AudioFormat(fileAudioFormat.getSampleRate()/10, fileAudioFormat.getSampleSizeInBits(), fileAudioFormat.getChannels(), true, fileAudioFormat.isBigEndian());
        fileAudioFormat = format;

        waveAsBytes = newBytes;
    }
}
