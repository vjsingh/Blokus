package gui;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.media.*;
import javax.swing.*;

/**
 *
 * @author dkimmel
 */
public class VideoPanel extends JPanel {
    public VideoPanel(File videoFile) {
        super();
        setLayout(new BorderLayout());
        
        Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, true);
        try {
            URL videoURL = videoFile.toURI().toURL();
            Player mediaPlayer = Manager.createRealizedPlayer(videoURL);
            Component video = mediaPlayer.getVisualComponent();
            Component controls = mediaPlayer.getControlPanelComponent();

            if (video != null) {
                add(video, BorderLayout.CENTER);
            }
            if (controls != null) {
                add(controls, BorderLayout.SOUTH);
            }

            mediaPlayer.start();
        } catch (Exception e) {
            System.err.println("An error occurred while loading the video");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * TODO: remove after done testing.
     * @param args
     */
    public static void main(String[] args) {
        JFrame testFrame = new JFrame("Testing the VideoPanel");
        testFrame.setLayout(new BorderLayout());
        testFrame.add(new VideoPanel(new File("/home/dkimmel/Desktop/test-0000.mpeg")), BorderLayout.CENTER);
        testFrame.pack();
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testFrame.setVisible(true);
    }
}
