package blokus;

import java.io.Serializable;
import java.util.LinkedList;
import static blokus.Constants.BOARD_SIZE;

/**
 *
 * @author ahills, dkimmel, guyoung, vjsingh
 */
public class Square implements Comparable, Serializable{
    private final Face _face;
    private final int _row;
    private final int _column;

    public Square(Face face, int row, int column) {
        if ( row < 0 || row >= BOARD_SIZE || column < 0 || column >= BOARD_SIZE) {
        }
        _face = face;
        _row = row;
        _column = column;
    }

    /**
     * @return The four squares that are adjacent to this square (not
     * diagonally)
     */
    public LinkedList<Square> getAdjacentSquares() {
        LinkedList<Square> adjacentSquares = new LinkedList<Square>();

        adjacentSquares.add(getAbove());
        adjacentSquares.add(getBelow());
        adjacentSquares.add(getLeft());
        adjacentSquares.add(getRight());

        return adjacentSquares;
    }

    /**
     * @return All the squares that are diagonally adjacent to this one.
     */
    public LinkedList<Square> getCorners() {
        LinkedList<Square> squares = new LinkedList<Square>();
        squares.add(this.getRelativeSquare(-1, -1));
        squares.add(this.getRelativeSquare(-1,  1));
        squares.add(this.getRelativeSquare( 1, -1));
        squares.add(this.getRelativeSquare( 1,  1));
        return squares;
    }

    /**
     * Returns a square that is rowOffSet rows away and columnOffSet columns
     * away from this square.
     * @param rowOffSet The number of rows away from this square, where positive
     * numbers represent the down (positive) direction and negative numbers
     * represent the up direction.
     * @param columnOffSet The number of columns away from this square, where
     * positive numbers represent right, and negative numbers represent left.
     * @return the specified square
     */
    public Square getRelativeSquare(int rowOffSet, int columnOffSet) {
        if (rowOffSet == 0 && columnOffSet == 0) {
            return this;
        }
        
        Square square = this;

        //The way this code works is you have to change the "direction" you're
        //going in if you change.

        //First make one change at a time...
        //adjust rows
        if (rowOffSet > 0) {
            square = square.getBelow();
            rowOffSet--;
        }
        else if (rowOffSet < 0) {
            square = square.getAbove();
            rowOffSet++;
        }
        //adjust columns
        else if (columnOffSet > 0) {
            square = square.getRight();
            columnOffSet--;
        }
        else if (columnOffSet < 0) {
            square = square.getLeft();
            columnOffSet++;
        }

        //...then make a recursive call with the updated "direction"
        Face newFace = square.getFace();
        int temp;

        if (_face != newFace) { //Do nothing if the face hasn't changed
            switch (_face) {
                case TOP:
                    if (newFace == Face.BACK) {
                        rowOffSet = -rowOffSet;
                        columnOffSet = -columnOffSet;
                    }
                    else if (newFace == Face.LEFT) {
                        temp = -columnOffSet;
                        columnOffSet = rowOffSet;
                        rowOffSet = columnOffSet;
                    }
                    else if (newFace == Face.RIGHT) {
                        temp = -rowOffSet;
                        rowOffSet = columnOffSet;
                        columnOffSet = temp;
                    }
                    //if you go to the front, do nothing
                    break;
                //do nothing for front
                case LEFT:
                    if (newFace == Face.TOP) {
                        temp = -rowOffSet;
                        rowOffSet = columnOffSet;
                        columnOffSet = temp;
                    }
                    else if (newFace == Face.BOTTOM) {
                        temp = -columnOffSet;
                        columnOffSet = rowOffSet;
                        rowOffSet = temp;
                    }
                    break;
                case BACK:
                    if (newFace == Face.TOP || newFace == Face.BOTTOM) {
                        rowOffSet = -rowOffSet;
                        columnOffSet = -columnOffSet;
                    }
                    break;
                case RIGHT:
                    if (newFace == Face.TOP) {
                        temp = -columnOffSet;
                        columnOffSet = rowOffSet;
                        rowOffSet = temp;
                    }
                    else if (newFace == Face.BOTTOM) {
                        temp = -rowOffSet;
                        rowOffSet = columnOffSet;
                        columnOffSet = temp;
                    }
                    break;
                case BOTTOM:
                    if (newFace == Face.LEFT) {
                        temp = -rowOffSet;
                        rowOffSet = columnOffSet;
                        columnOffSet = temp;
                    }
                    else if (newFace == Face.BACK) {
                        columnOffSet = -columnOffSet;
                        rowOffSet = -rowOffSet;
                    }
                    else if (newFace == Face.RIGHT) {
                        temp = -columnOffSet;
                        columnOffSet = rowOffSet;
                        rowOffSet = temp;
                    }
                    break;
            }
        }
        return square.getRelativeSquare(rowOffSet, columnOffSet);
    }

    /**
     * @return The Square "above" this square
     */
    private Square getAbove() {
        int newRow = _row - 1;
        int newColumn = _column;
        Face newFace = _face;

        int tempRow;

        if (newRow < 0) {
            switch (_face) {
                case TOP: newFace = Face.BACK; 
                    newRow = -1 - newRow;
                    newColumn = BOARD_SIZE - 1 - newColumn;
                    break;
                case LEFT: newFace = Face.TOP;
                    tempRow = newColumn;
                    newColumn = -1 - newRow;;
                    newRow = tempRow;
                    break;
                case BACK: newFace = Face.TOP;
                    newRow = -1 - newRow;
                    newColumn = BOARD_SIZE - 1 - newColumn;
                    break;
                case RIGHT: newFace = Face.TOP;
                    tempRow = newColumn;
                    newColumn = BOARD_SIZE + newRow;
                    newRow = BOARD_SIZE - 1 - tempRow;
                    break;
                case FRONT: newFace = Face.TOP;
                    newRow = BOARD_SIZE + newRow;
                    break;
                case BOTTOM: newFace = Face.FRONT;
                    newRow = BOARD_SIZE + newRow;
                    break;
            }
        }

        return new Square(newFace, newRow, newColumn);
    }

    /**
     * @return The square "below" this square
     */
    private Square getBelow() {
        int newRow = _row + 1;
        int newColumn = _column;
        Face newFace = _face;

        int tempRow;

        if (newRow >= BOARD_SIZE) {
            switch (_face) {
                case TOP: newFace = Face.FRONT;
                    newRow = newRow - BOARD_SIZE;
                    break;
                case LEFT: newFace = Face.BOTTOM;
                    tempRow = BOARD_SIZE - 1 - newColumn; 
                    newColumn = newRow - BOARD_SIZE;
                    newRow = tempRow;
                    break;
                case BACK: newFace = Face.BOTTOM;
                    newRow = (2*BOARD_SIZE - 1) - newRow;
                    newColumn = BOARD_SIZE - 1 - newColumn;
                    break;
                case RIGHT: newFace = Face.BOTTOM;
                    tempRow = newColumn;
                    newColumn = (2*BOARD_SIZE - 1) - newRow;
                    newRow = tempRow;
                    break;
                case FRONT: newFace = Face.BOTTOM;
                    newRow = newRow - BOARD_SIZE;
                    break;
                case BOTTOM: newFace = Face.BACK;
                    newRow = (2*BOARD_SIZE - 1) - newRow;
                    newColumn = BOARD_SIZE - 1 - newColumn;
                    break;
            }
        }

        return new Square(newFace, newRow, newColumn);
    }

    /**
     * @return The square to the "right" of this thread
     */
    private Square getRight() {
        int newRow = _row;
        int newColumn = _column + 1;
        Face newFace = _face;

        int tempRow;
        
        if (newColumn >= BOARD_SIZE) {
            switch (_face) {
                case TOP: newFace = Face.RIGHT;
                    tempRow = newColumn - BOARD_SIZE;
                    newColumn = BOARD_SIZE - 1 - newRow;
                    newRow = tempRow;
                    break;
                case LEFT: newFace = Face.FRONT;
                    newColumn = newColumn - BOARD_SIZE;
                    break;
                case BACK: newFace = Face.LEFT;
                    newColumn = newColumn - BOARD_SIZE;
                    break;
                case RIGHT: newFace = Face.BACK;
                    newColumn = newColumn - BOARD_SIZE;
                    break;
                case FRONT: newFace = Face.RIGHT;
                    newColumn = newColumn - BOARD_SIZE;
                    break;
                case BOTTOM: newFace = Face.RIGHT;
                    tempRow = (2*BOARD_SIZE - 1) - newColumn;
                    newColumn = newRow;
                    newRow = tempRow;
                    break;
            }
        }

        return new Square(newFace, newRow, newColumn);
    }

    /**
     * @return The square to the "left" of this square
     */
    private Square getLeft() {
        int newRow = _row;
        int newColumn = _column - 1;
        Face newFace = _face;

        int tempColumn;
        
        if (newColumn < 0) {
            switch (_face) {
                case TOP: newFace = Face.LEFT;
                    tempColumn = newRow;
                    newRow = -1 - newColumn;
                    newColumn = tempColumn;
                    break;
                case LEFT: newFace = Face.BACK;
                    newColumn = BOARD_SIZE + newColumn;
                    break;
                case BACK: newFace = Face.RIGHT;
                    newColumn = BOARD_SIZE + newColumn;
                    break;
                case RIGHT: newFace = Face.FRONT;
                    newColumn = BOARD_SIZE + newColumn;
                    break;
                case FRONT: newFace = Face.LEFT;
                    newColumn = BOARD_SIZE + newColumn;
                    break;
                case BOTTOM: newFace = Face.LEFT;
                    tempColumn = BOARD_SIZE - 1 - newRow;
                    newRow = BOARD_SIZE + newColumn;
                    newColumn = tempColumn;
                    break;
            }
        }

        return new Square(newFace, newRow, newColumn);
    }

    /**
     * Returns whether this square is 'greater than' otherSquare. A square is
     * greater than another square if its face, row, or column is higher, in
     * that order.
     * @param otherSquare Second square to compare
     * @return Whether this square is 'greater than' otherSquare
     */
    @Override
    public int compareTo(Object otherSquareObj) {
        Square otherSquare = (Square) otherSquareObj;
        if (this.getFace().ordinal() > otherSquare.getFace().ordinal())
            return 1;
        else if (this.getFace().ordinal() < otherSquare.getFace().ordinal())
            return -1;
        if (this.getRow() > otherSquare.getRow())
            return 1;
        else if (this.getRow() < otherSquare.getRow())
            return -1;
        if (this.getColumn() > otherSquare.getColumn())
            return 1;
        else if (this.getColumn() < otherSquare.getColumn())
            return -1;
        return 0; //same square
    }

    @Override
    public boolean equals(Object other) {
        Square otherSquare = (Square) other;
        return (_face == otherSquare.getFace() &&
                _row == otherSquare.getRow() &&
                _column == otherSquare.getColumn());
    }

    @Override
    public String toString() {
        String s = "";
        s += _face.toString() + " ";
        s += "row: " + _row;
        s += "column: " + _column;
        return s;
    }

    /**
     * @return the _face
     */
    public Face getFace() {
        return _face;
    }

    /**
     * @return the _x
     */
    public int getRow() {
        return _row;
    }

    /**
     * @return the _y
     */
    public int getColumn() {
        return _column;
    }

    public boolean equals(Square square) {
        return (square.getFace() == _face &&
                square.getRow() == _row &&
                square.getColumn() == _column);
    }

}
