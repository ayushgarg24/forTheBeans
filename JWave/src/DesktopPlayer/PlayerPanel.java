package DesktopPlayer;

import com.sun.xml.internal.ws.transport.http.client.HttpClientTransport;
import org.apache.commons.io.IOUtils;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PlayerPanel {
    private JButton playButton;
    private JButton stopButton;
    private JSlider slider1;
    protected JPanel mainPanel;
    private JButton chooseButton;
    private JButton uploadButton;
    private JTextField textField1;
    private JTabbedPane tabbedPane1;
    private JTextField linkTextField;
    private JComboBox filePicker;
    protected JFileChooser fileChooser;
    protected File file;

    public PlayerPanel() {
        fileChooser = new JFileChooser();
        textField1.setText("No File Selected");
        textField1.setEditable(false);
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(mainPanel);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    textField1.setText(file.getName());
                }
            }
        });
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (file != null && file.getName().contains(".wav")) {
                    URL url = null;
                    try {
                        url = new URL("https://wavelets.radovandesign.com/upload/?name=" + file.getName());
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        if (file.length() > 30000000) {

                        }
                        URLConnection c = url.openConnection();
                        c.setDoOutput(true);
                        c.setRequestProperty("Content-Type", "audio/wav");
                        //c.setRequestMethod("POST");
                        OutputStream out = c.getOutputStream();
                        out.write(IOUtils.toByteArray(fis));
                        //Files.copy(Paths.get(file.toURI()), c.getOutputStream());
                        //IOUtils.copy(fis, c.getOutputStream());
                        //OutputStreamWriter w = new OutputStreamWriter(c.getOutputStream());
                        //w.write("This is a test!");
                        //w.close();
                        InputStream response = c.getInputStream();
                        System.out.println(response);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = filePicker.getSelectedItem().toString() + ".zip";
                try {
<<<<<<< HEAD
                    Wave w = new Wave("https://storage.googleapis.com/audiowavelet.appspot.com/the_file.zip");
=======
                    Wave w = new Wave("https://storage.googleapis.com/rd-site-resources/wavelets/" + name);
>>>>>>> c49603fe299114a99ac1ef80185156a747e8e766
                    System.out.println("Wave In");
                    w.decompress();
                    System.out.println("Wave Decomp");
                    w.playWave();
                    System.out.println("Wave Stream");
                    slider1.setMaximum(6);
                    slider1.setMinimum(-80);
                    slider1.setValue((int)w.f.getValue());
                    slider1.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            int v = slider1.getValue();
                            float dB = (float) v/100*46 - 40;
                            w.f.setValue(v);
                            System.out.println(v);
                        }
                    });
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedAudioFileException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void createUIComponents() {

    }
}
