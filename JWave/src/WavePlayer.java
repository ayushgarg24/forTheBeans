import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Arrays;

public class WavePlayer {
    protected Wave completeWave;
    protected SourceDataLine sLine;
    protected int frameLocation;
    protected int maxFrame;

    public WavePlayer() {

    }

    /**
     * This method will query the server for the given file name, and if found, will split file into temporary file chunks and return an array of their names.
     *
     * To-Do: Instead of file chunks, have player dynamically request variable length chunks of the audio data.
     * To-Do: Chase down exceptions, try/catch's in sub methods.
     *
     * @param path
     */
    public void setSource(String path) throws IOException, UnsupportedAudioFileException {
        completeWave = new Wave(path);
        maxFrame = completeWave.waveAsBytes.length/completeWave.fileAudioFormat.getFrameSize();
    }

    public void play() {
        try {
            sLine = AudioSystem.getSourceDataLine(completeWave.fileAudioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        try {
            sLine.open(completeWave.fileAudioFormat, 88224);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        sLine.start();
        sLine.write(completeWave.waveAsBytes, 0, completeWave.waveAsBytes.length);
        for (int i = 2; i < 18; i++) {
            byte[] b = new byte[0];
            try {
                b = completeWave.addToWave("https://storage.googleapis.com/rd-site-resources/wavelets/first20/first20_" + i + ".wav");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
            sLine.write(b, 0, b.length);
        }
    }

    public void pause() {

    }

    public void stop() {
        if (sLine.isRunning()) {
            sLine.stop();
            sLine.flush();
            sLine.close();
        }
    }

    public void seek(int fP) {
        sLine.flush();
        byte[] b = Arrays.copyOfRange(completeWave.waveAsBytes, fP * completeWave.fileAudioFormat.getFrameSize(), completeWave.waveAsBytes.length);
        sLine.write(b, 0, b.length);
    }

    public int getPosition() {
        return sLine.getFramePosition();
    }
}
