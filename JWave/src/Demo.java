import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class Demo {
    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        String path = "src/files/inputs/first20.wav";

        Wave wave = new Wave(path);

        wave.wavelet = 39;

        wave.compress(1500);

        wave.toZipFile("src/files/outputs/ran.zip");

        path = "src/files/outputs/ran.zip";
        Wave song = null;
        try {
            song = new Wave(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        song.decompress();
        song.playWave();
    }
}
