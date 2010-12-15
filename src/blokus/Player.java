package blokus;

import java.util.LinkedList;
import gui.PieceDisplay;
import java.io.Serializable;

/**
 *
 * @author ahills, dkimmel, guyoung, vjsingh
 */

public abstract class Player implements Serializable {
    private Color _color;
    private String _name;
    private LinkedList<Piece> _pieces;
    private transient PieceDisplay _display;
    private int _score;

    public Player(String name) {
        _name = name;
        _pieces = new LinkedList<Piece>();
        for (Shape shape : Shape.values()) {
            _pieces.add(new Piece(shape));
        }
        _score = 0;
    }

    public void removePiece(Shape shape) {
        for (Piece piece : _pieces) {
            if (piece.getShape() == shape) {
                _pieces.remove(piece);
                _display.remove(piece);
                //add to score
                for (int i = 0; i < shape.getnumOfSquares(); i++) {
                    _score++;
                }
                return;
            }
        }
    }

    /**
     * Used when loading in a game. We don't store the piece display, so this
     * function removes all pieces from the display that the player has already
     * made.
     */
    public void updatePieceDisplay() {
        boolean foundPiece;

        //Slow, but only called when loading in a game
        for (Shape shape : Shape.values()) {
            foundPiece = false;
            for (Piece piece : _pieces) {
                if (piece.getShape() == shape) {
                    foundPiece = true;
                }
            }
            if (!foundPiece) {
                _display.remove(new Piece(shape));
            }
        }
    }

    public Color getColor() {
        return _color;
    }

    public void setColor(Color color) {
        _color = color;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name){
        _name = name;
    }

    public int getScore() {
        if (_score==91)
            return 106;
        else
        return _score;
    }

    /**
     * Should call makeMove() before returning the move.
     * @return Move that was made by the player
     * @throws Exception Used for network timeouts
     */
    public abstract Move getMove() throws Exception;

    public LinkedList<Piece> getPieces() {
        return _pieces;
    }

    public void setPieceDisplay(PieceDisplay display){
        _display = display;
    }
}
