package gui;

import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;
import blokus.*;
import java.awt.event.MouseListener;

/**
 * This class is for displaying a players moves and allowing them to select and
 * rotate pieces.
 * @author dkimmel
 */
public class PiecesView extends PieceDisplay implements MouseListener {
    private List<Position> _topLeftCorners;
    private Piece _selected;
    private java.awt.Color _color;

    public PiecesView(OpponentView opponentView) {
        this(opponentView.getColor(), opponentView.getPieces());
    }

    public PiecesView(java.awt.Color color) {
        this(color, new ArrayList<Piece>(21));
        _color = color;
    }

    /**
     * This constructor takes the color of the player. To select a piece, click
     * on it. To rotate it clockwise, hit "D" or call rotateCW(). To rotate it
     * counterclockwise, hit "A" or call rotateCCW(). To flip it vertically, hit
     * "W" or call flipVertical(). To flip it horizontally, hit "S" or call
     * flipHorizontal(). To tell this pane that the player has used a piece (ie
     * to remove it from the view, call remove(Piece).
     * @param color
     */
    public PiecesView(java.awt.Color color, List<Piece> pieces) {

        super(color, pieces);
                        _color = color;

        boolean isEmpty = false;
        if (pieces.isEmpty())
            isEmpty = true;
        
        _topLeftCorners = new ArrayList<Position>(21);
        for (int i = 0; i < Shape.values().length; i++) {
            Shape s = Shape.values()[i];
            if (isEmpty) {
                _pieces.add(new Piece(s));
            }
            _topLeftCorners.add(new Position(1 + 6*i, 1));
        }

        //select the last piece because players generally want to play a piece
        //with more squares
        _selected = _pieces.get(pieces.size() - 1);
        
        // add a mouse listener so we can receive clicks
        this.addMouseListener(this);

/*        // add some key shortcuts
        this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released D"), "rotateCW");
        this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released A"), "rotateCCW");
        this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released S"), "flipHorizontal");
        this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released W"), "flipVertical");

        // create some actions to perform
        Action turnCW = new AbstractAction() {
            public void actionPerformed(ActionEvent e) { rotateCW(); }};
        Action turnCCW = new AbstractAction() {
            public void actionPerformed(ActionEvent e) { rotateCCW(); }};
        Action flipHorizontal = new AbstractAction() {
            public void actionPerformed(ActionEvent e) { flipHorizontal(); }};
        Action flipVertical = new AbstractAction() {
            public void actionPerformed(ActionEvent e) { flipVertical(); }};

        // register the actions with the names of the actions
        this.getActionMap().put("rotateCW", turnCW);
        this.getActionMap().put("rotateCCW", turnCCW);
        this.getActionMap().put("flipHorizontal", flipHorizontal);
        this.getActionMap().put("flipVertical", flipVertical);*/
    }

    /**
     * Removes the piece from the player's view (use when the player uses this
     * piece). Also, sets selected to the next unused piece.
     * @param piece The piece to remove from the view. It just has to have the
     * same shape to register correctly.
     */
    public void remove(Piece piece) {
        for (int i = 0; i < _pieces.size(); i++) {
            if (piece.getShape().equals(_pieces.get(i).getShape())) {
                if (_pieces.get(i) == _selected) {
                    if (_pieces.size() > 0) {
                        _selected = _pieces.get((i+1)%_pieces.size());
                    } else {
                        _selected = null;
                    }
                }
                _pieces.remove(i);
                _topLeftCorners.remove(i);
            }
        }
        repaint();
    }

    public java.awt.Color getColor()
    {
        return _color;
    }

    /**
     * A public method to rotate the selected piece clockwise.
     */
    public void rotateCW() {
        _selected.rotateCW();
        repaint();
    }

    /**
     * A public method to rotate the selected piece counterclockwise.
     */
    public void rotateCCW() {
        _selected.rotateCCW();
        repaint();
    }

    /**
     * A public method to flip the selected piece horizontally.
     */
    public void flipHorizontal() {
        _selected.flipHorizontal();
        repaint();
    }

    /**
     * A public method to flip the selected piece vertically.
     */
    public void flipVertical() {
        _selected.flipVertical();
        repaint();
    }

    /**
     * Returns the piece that is selected in this panel.
     * @return The piece which is selected, with all rotation/flipping data.
     * This method only returns null when all the pieces are removed because
     * there is always a selected piece (including at initialization). It's okay
     * to modify the piece that this returns because it is a copy of the one
     * stored internally by the display.
     */
    public Piece getSelected() {
        return new Piece(_selected.getShape(), _selected.getPiece());
    }

    /**
     * The bulk of this class. Displays the pieces on the panel by drawing each
     * of them block-by-block.
     * @param graphicsContext The graphics context with which this panel will be
     * drawn. Provided to us by some Java deus ex machina. Should never be null.
     */
    @Override
    public void paint(Graphics graphicsContext) {
        Graphics2D g = (Graphics2D) graphicsContext;
        
        int boxWidth = this.getWidth() / (21*6+1);
        int boxHeight = this.getHeight() / 7;
        int boxSide = Math.min(boxWidth, boxHeight);

        // draw the pieces
        for (int i = 0; i < _pieces.size(); i++) {
            Piece piece = _pieces.get(i);
            boolean[][] p = piece.getPiece();
            Position pos = _topLeftCorners.get(i);

            // draw selected background
            g.setColor(java.awt.Color.white);
            if (_selected == piece) {
                int actualTopLeftX = _topLeftCorners.get(i).getX() * boxSide - (boxSide/2);
                int actualTopLeftY = _topLeftCorners.get(i).getY() * boxSide - (boxSide/2);
                g.fillRect(actualTopLeftX, actualTopLeftY, 6*boxSide, 6*boxSide);
            }

            // draw pieces block by block
            g.setColor(_color);
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < 5; c++) {
                    if (p[r][c]) {
                        g.fillRect((pos.getX()+c)*boxSide, (pos.getY()+r)*boxSide, boxSide, boxSide);
                    }
                }
            }

            // draw piece outlines
            g.setColor(java.awt.Color.black);
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < 5; c++) {
                    if (p[r][c]) {
                        g.drawRect((pos.getX()+c)*boxSide, (pos.getY()+r)*boxSide, boxSide, boxSide);
                    }
                }
            }
        }
    }

    /**
     * These don't do anything because the operations aren't necessary for this
     * GUI element. We only care about mouseClicked().
     */
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    /**
     * Selects a piece if the click fell inside a piece's area. If it didn't,
     * this method just ignores the click.
     * @param e The event that describes the click. We use this to get the
     * position of the click relative to the upper-left corner of this area of
     * the screen.
     */
    public void mouseClicked(MouseEvent e) {
        this.grabFocus();
        int x = e.getX();
        int y = e.getY();

        int boxWidth = this.getWidth() / (21*6+1);
        int boxHeight = this.getHeight() / 7;
        int boxSide = Math.min(boxWidth, boxHeight);

        for (int i = 0; i < _topLeftCorners.size(); i++) {
            int actualTopLeftX = _topLeftCorners.get(i).getX() * boxSide - (boxSide/2);
            int actualTopLeftY = _topLeftCorners.get(i).getY() * boxSide - (boxSide/2);
            if (x > actualTopLeftX && x <= actualTopLeftX + 6*boxSide &&
                y > actualTopLeftY && y <= actualTopLeftY + 6*boxSide) {
                _selected = _pieces.get(i);
                repaint();
                return;
            }
        }
    }

    /**
     * A simple private class for storing the top-left square of each piece.
     */
    private class Position {
        private int _x, _y;
        public Position(int x, int y) {
            _x = x;
            _y = y;
        }
        public int getX() { return _x; }
        public int getY() { return _y; }
    }
}
