package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;
import blokus.*;
import javax.swing.JFrame;

/**
 * This class is for displaying a players moves and allowing them to select and
 * rotate pieces.
 * @author dkimmel
 */
public class OpponentView extends PieceDisplay{
    private List<Position> _topLeftCorners;
    private final int PIECES_IN_WIDTH = 5;

    public OpponentView(PiecesView piecesView) {
        this(piecesView.getColor(), piecesView.getPieces());
    }

    public OpponentView(java.awt.Color color) {
        this(color, new ArrayList<Piece>(21));
    }
    /**
     * This constructor takes the color of the player. To tell this pane that
     * the player has used a piece (ie to remove it from the view, call
     * remove(Piece).
     * @param color
     */
    public OpponentView(java.awt.Color color, List<Piece> pieces) {
        super(color, pieces);

        boolean isEmpty = false;
        if (pieces.isEmpty())
            isEmpty = true;
        
        _topLeftCorners = new ArrayList<Position>(21);
        for (int i = 0; i < Shape.values().length; i++) {
            Shape s = Shape.values()[i];
            if (isEmpty) {
                _pieces.add(new Piece(s));
            }
            _topLeftCorners.add(new Position(1 + 6*(i%PIECES_IN_WIDTH), 1 + 6*(i/PIECES_IN_WIDTH)));
        }
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
                _pieces.remove(i);
                _topLeftCorners.remove(i);
            }
        }
        repaint();
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

        int boxWidth = this.getWidth() / (1 + 6*PIECES_IN_WIDTH);
        int boxHeight = this.getHeight() / (1 + 6*((int)(21.0/PIECES_IN_WIDTH + 0.9)));
        int boxSide = Math.min(boxWidth, boxHeight);

        // draw the pieces
        for (int i = 0; i < _pieces.size(); i++) {
            Piece piece = _pieces.get(i);
            boolean[][] p = piece.getPiece();
            Position pos = _topLeftCorners.get(i);

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
