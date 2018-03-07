import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteTest {
    public static void main(String args[]) throws IOException, UnsupportedAudioFileException {
        File song = new File("src/pipe.wav");

        byte[] bytes = null;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = AudioSystem.getAudioInputStream(song);

        int read;
        byte[] buff = new byte[1024];
        while ((read = in.read(buff)) > 0)
        {
            out.write(buff, 0, read);
        }
        out.flush();

        byte[] audioBytes = out.toByteArray();

        byte byte1 = audioBytes[9998];
        byte byte2 = audioBytes[9999];

        //short result = (short)( ((byte1&0xFF)<<8) | (byte2&0xFF) );

        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(byte1);
        bb.put(byte2);
        short result = bb.getShort(0);

        short expectedValue = 19047;

        System.out.println("Byte 1: " + byte1);
        System.out.println("Byte 2: " + byte2);
        System.out.println("Result: " + result);

        System.out.println(audioBytes.length);

        byte result1 = (byte)(result & 0xff);
        byte result2 = (byte)((result >> 8) & 0xff);

        System.out.println("Result Byte 1: " + byte1);
        System.out.println("Result Byte 2: " + byte2);

        //1 = 0,1
        //2 = 2,3
        //3 = 4,5
        //4 = 6,7
        //5 = 8,9
        //6 = 10,11
    }
}
