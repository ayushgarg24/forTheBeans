package DesktopPlayer;

import com.sun.xml.internal.ws.transport.http.client.HttpClientTransport;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
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
    }

    private void createUIComponents() {

    }
}
