package files;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;

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


        return panel;


    }
}
