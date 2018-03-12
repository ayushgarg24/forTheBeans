import jwave.JWave;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        AudioInputStream audio = AudioSystem.getAudioInputStream(new File("src/files/inputs/sa.wav"));
        AudioFormat format = new AudioFormat(44100/10, 16, 2, true, false);
        AudioInputStream audio2 = AudioSystem.getAudioInputStream(format, audio);
        AudioSystem.write(audio2, AudioFileFormat.Type.WAVE, new File("src/files/outputs/downsampled.wav"));
    }
}
