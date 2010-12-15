package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * A class for tutorial slideshows of screenshots and text.
 * @author dkimmel
 */
public class SlideshowPanel extends JPanel implements Serializable, ActionListener {
    private ArrayList<BufferedImage> _images;
    private ArrayList<String> _captions;
    private int _current;

    private JLabel _captionLabel;
    private PicturePanel _picturePanel;
    private JButton _nextButton;
    private JButton _previousButton;

    public SlideshowPanel() {
        super();
        _images = new ArrayList<BufferedImage>();
        _captions = new ArrayList<String>();
        _current = -1;

        _captionLabel = new JLabel("");
        _picturePanel = new PicturePanel();
        _nextButton = new JButton(">");
        _previousButton = new JButton("<");

        _nextButton.setActionCommand("next");
        _nextButton.addActionListener(this);
        _previousButton.setActionCommand("previous");
        _previousButton.addActionListener(this);

        this.setLayout(new BorderLayout());
        this.add(BorderLayout.CENTER, _picturePanel);
        this.add(BorderLayout.SOUTH, _captionLabel);
        this.add(BorderLayout.EAST, _nextButton);
        this.add(BorderLayout.WEST, _previousButton);
    }

    public void next() {
        if(_current < _images.size()-1) {
            _current++;
        }
        _captionLabel.setText(_captions.get(_current));
        _picturePanel.setPicture(_current);
    }

    public void previous() {
        if(_current > 0) {
            _current--;
        }
        _captionLabel.setText(_captions.get(_current));
        _picturePanel.setPicture(_current);
    }

    public void add(File imageFile, String text) {
        try {
            _images.add(javax.imageio.ImageIO.read(imageFile));
        } catch (IOException ex) {
            System.err.println("Error reading image: " + imageFile.getPath());
        }
        _captions.add(text);
    }

    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals("next")) {
            next();
        }
        else if (action.equals("previous")) {
            previous();
        }
    }

    private class PicturePanel extends JPanel {
        private BufferedImage _image;

        public PicturePanel() {}

        public void setPicture(int index) {
            _image = _images.get(index);
            this.setPreferredSize(new Dimension(_image.getWidth(), _image.getHeight()));
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            g.drawImage(_image, 0, 0, null);
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Slideshow Test");
        SlideshowPanel sp = new SlideshowPanel();
        String[] text = {"This is how the game begins. Each player occupies 2 radially opposite corners.",
                "To make your first move, simply select a piece in the bottom panel with your left mouse button.",
                "You can then rotate and flip the piece with the WASD keys, or through the \"Piece\" menu.",
                "You can then make your move with the left mouse button. Note that your piece must be adjacent diagonally, " +
                    "but not by sides, to one of your already placed squares.",
                "This is a good move because it uses a 5 square piece (getting you on the way to placing all of your pieces!), " +
                    "and begins to encroach on others\' territory (preventing them from placing their pieces).",
                "Note that your piece will be grayed out if it is an invalid move (see status at the bottom to understand why).",
                "Now, the game has progressed a bit. Let's examine the Recent Moves panel on the left.",
                "I clicked the first blue state, so the game has reverted to that state on the board.",
                "(Doesn't that look valid? but Yellow has a piece there!)",
                "You can revert the board to the current state by clicking the most recent (top-most) move, but you can also " +
                    "just make a move, and the board will automatically revert."};
        for (int i = 1; i <= 10; i++) {
            //sp.add(new File("/home/dkimmel/course/blokus/cs032student/Blokus/tutorialImages/" + i + ".png"), text[i-1]);
            sp.add(new File("." + File.separator + i + ".png"), text[i-1]);
        }
        sp.next();
        f.setLayout(new BorderLayout());
        f.add(BorderLayout.CENTER, sp);
        //f.setSize(400,400);
        f.pack();
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setVisible(true);
    }
}
