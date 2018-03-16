import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Frontend {
    protected static Wave song;
    protected static boolean playing = false;
    protected static Thread playSong;
    protected static WavePlayer wp;
    protected static Thread playback;
    public static void main(String[] args) {
        wp = new WavePlayer();
        try {
            wp.setSource("https://storage.googleapis.com/rd-site-resources/wavelets/first20/first20_1.wav");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }

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
        JButton stop = new JButton("Stop");

        JSlider s = new JSlider(0, wp.maxFrame);
       /* s.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                wp.seek(s.getValue());
            }
        });*/

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wp.stop();
            }
        });
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playing = true;
                 playSong = new Thread() {
                    public void run() {
                        stream();
                    }
                };
                playSong.start();
            }
        });
        panel.add(b);
        panel.add(stop);
        panel.add(s);

        playback = new Thread() {
            public void run() {
                s.setValue(wp.getPosition());
            }
        };

        return panel;
    }

    public static void playFile(String path) {
        //path = "src/files/outputs/ran.zip";
        path = "http://storage.googleapis.com/stuffandthingsforstuff/ran.zip";
        song = null;
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

    public static void stream() {
        wp.play();
        playback.start();
    }
}
