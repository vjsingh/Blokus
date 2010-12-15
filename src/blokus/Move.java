package blokus;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import static blokus.Constants.BOARD_SIZE;

/**
 * Holds a piece on the board (and a list of squares in that piece), and the
 * player who played the piece. The _position member variable holds the Square
 * of the piece that is in the center of the 5x5 array.
 * @author ahills, dkimmel, guyoung, vjsingh
 */
public class Move implements Serializable {
    private Square _position;
    private Piece _piece;
    //private Player _player;
    private Color _color;
    private LinkedList<Square> square_list;
    private boolean is_valid;
    private int hash_code; //store to speed up
    private boolean _pass;

    /**
     * This creates a "pass" move.
     */
    public Move(Color color) {
        _pass = true;
        _color = color;
        is_valid = true;
        square_list = new LinkedList<Square>();
        hash_code = this.toString().hashCode();
        _pass = true;
    }

    /**
     * For constructing initial moves ONLY.
     * @param squares
     * @param player
     */
    public Move(LinkedList<Square> squares, Color color) {
        square_list = squares;
        _color = color;
        is_valid = true;
        hash_code = this.toString().hashCode();
        _pass = false;
    }

    /**
     * May not construct a valid move if the piece includes a square that is
     * touching a corner of the cube. This is because all of these squares are
     * initially taken up and thus no piece can be placed there. To check
     * whether this is the case, call this.isValid() after the constructor.
     * Note that if isValid() returns false, the move is not valid, but if
     * isValid returns true, the move may or may not be valid.
     * @param position The 'position' square of the piece. Should be the place
     * of the 'middle piece' (in the (2,2) square of the 5x5 array) on the
     * board.
     * @param piece Piece to place
     * @param player Player who is placing the piece
     */
    public  Move(Square position, Piece piece, Color color) {
        _position = position;
        _piece = piece;
        _color = color;
        square_list = new LinkedList<Square>();
        boolean isValid = true; //used to set is_valid at the end

        //Calculate a list of all the squares in the move
        //i.e. Create all squares based on _position and the piece
        boolean[][] pieceArray = _piece.getPiece();

        //position square is [2][2] in piece array
        int positionI = 2;
        int positionJ = 2;
        
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (pieceArray[i][j] == true) {
                    int rowDiff = i - positionI;
                    int columnDiff = j - positionJ;

                    Square square = position.getRelativeSquare(
                            rowDiff, columnDiff);
                    
                    //Return false if the square is touching a corner
                    //and the piece is not the starting block (one square)
                    //i.e. if the square is (0,0)(0,8)(8,0) or (8,8)
                    if ( ( (square.getRow() == 0 ||
                            square.getRow() == BOARD_SIZE - 1) &&
                            (square.getColumn() == 0 ||
                            square.getColumn() == BOARD_SIZE - 1)) ) {
                        if (piece.getShape() != Shape.oneSquare) {
                            isValid = false;
                        }
                    }
                    
                    square_list.add(square);
                }
            }
        }
        is_valid = isValid;

        hash_code = this.toString().hashCode();

        _pass = false;
    }

    /**
     * @return A list containing all the squares that could be "played off" this
     * piece. Specifically, returns a list of squares that are diagonally
     * adjacent to a square in the piece, but are not adjacent by sides to any
     * other square in the piece. Note that these squares could, however,
     * already be occupied.
     */
    public LinkedList<Square> getBuildSquares() {
        LinkedList<Square> squaresToReturn = new LinkedList<Square>();

        boolean shouldAdd;
        for (Square square : square_list) { //every square in the piece

            //All possible corners of the square in the piece
            for (Square tempBuildSquare : square.getCorners()) {
                shouldAdd = true; //default to true unless proven otherwise

                //loop through all squares in the piece again, and check that
                //none of their adjacent squares are equal to the corner piece
                //we're checking
                for (Square square2 : square_list) {
                    for (Square adjSquare : square2.getAdjacentSquares()) {
                        if (adjSquare.equals(tempBuildSquare)) {
                            shouldAdd = false;
                        }
                    }
                }
                if (shouldAdd == true) {
                    squaresToReturn.add(tempBuildSquare);
                }
            }
        }

        return squaresToReturn;
    }

    /**
     * @return A string which represents the positions this piece fills on the
     * board and what player made the move.
     */
    @Override
    public String toString() {
        String string = "[";
        string += _color.name() + ": ";

        //perform deep copy of square_list, then sort
        LinkedList<Square> sortedSquares = new LinkedList<Square>();
        for (Square square : square_list) {
            sortedSquares.add(square);
        }

        Collections.sort((java.util.List) sortedSquares);

        for (Square square : sortedSquares) {
            string += "(" + square.getFace().name() + " ";
            string += square.getRow() + ", ";
            string += square.getColumn() + "), ";
        }

        string += "]";
        
        return string;
    }

    /**
     * Tests whether two moves are the same by comparing the player who made
     * them and the space they occupy on the board.
     * @param other The other move that we want to compare ourselves to.
     * @return Whether or not these two moves are the same.
     */
    @Override
    public boolean equals(Object other) {
        if (this.hash_code != other.hashCode()) {
            return false;
        }

        if (other.getClass() != this.getClass()) {
            return false;
        }

        Move move = (Move) other;

        if (move.getColor() != _color) {
            return false;
        }

        if (move.getSquares().size() != getSquares().size()) {
            return false;
        }

        for (Square s : move.getSquares()) {
            if (!getSquares().contains(s)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        // don't worry - it's precalculated in the constructor
        return hash_code;
    }
    
    public LinkedList<Square> getSquares() {
        return square_list;
    }

    /**
     * @return the _position
     */
    public Square getPosition() {
        return _position;
    }

    /**
     * @return Color of the player who played this move
     */
    public Color getColor() {
        return _color;
    }

    /**
     * @return the piece
     */
    public Piece getPiece() {
        return _piece;
    }

    /**
     * @return the shape
     */
    public Shape getShape() {
        try {
            return _piece.getShape();
        } catch (java.lang.NullPointerException e) {

            _piece.print();
            return null;
        }
    }

    /**
     * Even if this function returns true, the piece may still not be valid.
     * @return
     * if false: the piece is not valid
     * if true: the piece may or may not be valid
     */
    public boolean isValid() {
        return is_valid;
    }

    public boolean isPass(){
        return _pass;
    }

    public void printMove(){
        boolean pass = this.isPass();
        if(pass){
            System.out.println("pass");
            return;
        }
        Square square = this.getPosition();
        System.out.println(square.getFace().toString()+ " " + square.getRow() +
                " " + square.getColumn());
        Piece piece = this.getPiece();
        System.out.println(piece.getShape().toString());
        boolean[][] positions = piece.getPiece();
        for(int i = 0; i < positions.length; i++){
            for(int j = 0; j < positions[i].length; j++){
                System.out.print(positions[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
