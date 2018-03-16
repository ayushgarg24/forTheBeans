import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Frontend {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(createMainPanel(), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public static JPanel createMainPanel() {
        JPanel panel = new JPanel();

        JTextArea a = new JTextArea();
        panel.add(a);

        JButton b = new JButton("Play");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playFile(a.getText());
            }
        });
        panel.add(b);

        return panel;
    }

    public static void playFile(String path) {
        //path = "src/files/outputs/ran.zip";
        path = "http://storage.googleapis.com/stuffandthingsforstuff/ran.zip";
        Wave song = null;
        try {
            song = new Wave(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        song.decompress();
        /*try {
            song.playWave();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        song.streamWave();
        for (int i = 1; i < 18; i++) {
            try {
                song.addToWave("https://storage.googleapis.com/rd-site-resources/wavelets/first20/first20_" + i + ".wav");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }
    }
}
