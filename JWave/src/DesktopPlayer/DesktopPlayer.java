package DesktopPlayer;

import javax.swing.*;
import java.awt.*;

public class DesktopPlayer {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Desktop Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new PlayerPanel().mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

}
