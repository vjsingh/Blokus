package blokus;

import java.io.Serializable;

/**
 *
 * @author ahills, dkimmel, guyoung, vjsingh
 */
public class Piece implements Serializable {
    boolean[][] _positions;
    boolean[][] _corners;
    private Shape _shape;

    /**
     * Creates a piece based on shape and positions
     * @param shape The Shape of the piece
     * @param positions A 5x5 array of all the squares in the piece
     */
    public Piece (Shape shape, boolean[][] positions) {
        _shape = shape;
        _positions = positions;
    }

    /**
     * Creates a piece of Shape shape. Every piece has a square in [2][2].
     * @param shape The Shape of the piece
     */
    public Piece(Shape shape) {
        _positions = new boolean[5][5];
        _corners = new boolean[5][5];
        _shape = shape;
        
        //initialize all to false
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                _positions[i][j] = false;
                _corners[i][j] = false;
            }
        }

        switch (shape) {
            case oneSquare:
                _positions[2][2] = true;
                _corners[2][2] = true;
                break;
            case domino: 
                _positions[2][1] = true;
                _positions[2][2] = true;
                _corners[2][1] = true;
                _corners[2][2] = true;
                break;
            case smallL: 
                _positions[2][1] = true;
                _positions[2][2] = true;
                _positions[3][2] = true;
                _corners[2][1] = true;
                _corners[2][2] = true;
                _corners[3][2] = true;
                break;
            case threeLine:
                _positions[1][2] = true;
                _positions[2][2] = true;
                _positions[3][2] = true;
                _corners[1][2] = true;
                _corners[3][2] = true;
                break;
            case fourLine:
                _positions[0][2] = true;
                _positions[1][2] = true;
                _positions[2][2] = true;
                _positions[3][2] = true;
                _corners[0][2] = true;
                _corners[3][2] = true;
                break;
            case fourL:
                _positions[1][2] = true;
                _positions[2][2] = true;
                _positions[3][2] = true;
                _positions[3][3] = true;
                _corners[1][2] = true;
                _corners[3][2] = true;
                _corners[3][3] = true;
                break;
            case fourSquare:
                _positions[1][1] = true;
                _positions[1][2] = true;
                _positions[2][1] = true;
                _positions[2][2] = true;
                _corners[1][1] = true;
                _corners[1][2] = true;
                _corners[2][1] = true;
                _corners[2][2] = true;
                break;
            case zigZag:
                _positions[1][1] = true;
                _positions[1][2] = true;
                _positions[2][2] = true;
                _positions[2][3] = true;
                _corners[1][1] = true;
                _corners[1][2] = true;
                _corners[2][2] = true;
                _corners[2][3] = true;
                break;
            case T:
                _positions[2][1] = true;
                _positions[2][2] = true;
                _positions[2][3] = true;
                _positions[3][2] = true;
                _corners[2][1] = true;
                _corners[2][3] = true;
                _corners[3][2] = true;
                break;
            case fiveJ:
                _positions[0][2] = true;
                _positions[1][2] = true;
                _positions[2][2] = true;
                _positions[3][2] = true;
                _positions[3][3] = true;
                _corners[0][2] = true;
                _corners[3][2] = true;
                _corners[3][3] = true;
                break;
            case fiveT:
                _positions[1][1] = true;
                _positions[1][2] = true;
                _positions[1][3] = true;
                _positions[2][2] = true;
                _positions[3][2] = true;
                _corners[1][1] = true;
                _corners[1][3] = true;
                _corners[3][2] = true;
                break;
            case fiveL:
                _positions[1][2] = true;
                _positions[2][2] = true;
                _positions[3][2] = true;
                _positions[3][3] = true;
                _positions[3][4] = true;
                _corners[1][2] = true;
                _corners[3][2] = true;
                _corners[3][4] = true;
                break;
            case fiveZigZag:
                _positions[0][1] = true;
                _positions[1][1] = true;
                _positions[1][2] = true;
                _positions[2][2] = true;
                _positions[3][2] = true;
                _corners[0][1] = true;
                _corners[1][1] = true;
                _corners[1][2] = true;
                _corners[3][2] = true;
                break;
            case Z:
                _positions[1][1] = true;
                _positions[1][2] = true;
                _positions[2][2] = true;
                _positions[3][2] = true;
                _positions[3][3] = true;
                _corners[1][1] = true;
                _corners[1][2] = true;
                _corners[3][2] = true;
                _corners[3][3] = true;
                break;
            case fiveLine:
                _positions[0][2] = true;
                _positions[1][2] = true;
                _positions[2][2] = true;
                _positions[3][2] = true;
                _positions[4][2] = true;
                _corners[0][2] = true;
                _corners[4][2] = true;
                break;
            case P:
                _positions[1][2] = true;
                _positions[2][2] = true;
                _positions[3][2] = true;
                _positions[2][3] = true;
                _positions[3][3] = true;
                _corners[1][2] = true;
                _corners[3][2] = true;
                _corners[2][3] = true;
                _corners[3][3] = true;
                break;
            case W:
                _positions[1][1] = true;
                _positions[2][1] = true;
                _positions[2][2] = true;
                _positions[3][2] = true;
                _positions[3][3] = true;
                _corners[1][1] = true;
                _corners[2][1] = true;
                _corners[2][2] = true;
                _corners[3][2] = true;
                _corners[3][3] = true;
                break;
            case U:
                _positions[1][1] = true;
                _positions[1][2] = true;
                _positions[2][2] = true;
                _positions[3][2] = true;
                _positions[3][1] = true;
                _corners[1][1] = true;
                _corners[1][2] = true;
                _corners[3][2] = true;
                _corners[3][1] = true;
                break;
            case F:
                _positions[1][1] = true;
                _positions[1][2] = true;
                _positions[2][2] = true;
                _positions[2][3] = true;
                _positions[3][2] = true;
                _corners[1][1] = true;
                _corners[1][2] = true;
                _corners[2][3] = true;
                _corners[3][2] = true;
                break;
            case cross:
                _positions[1][2] = true;
                _positions[2][1] = true;
                _positions[2][2] = true;
                _positions[2][3] = true;
                _positions[3][2] = true;
                _corners[1][2] = true;
                _corners[2][1] = true;
                _corners[2][3] = true;
                _corners[3][2] = true;
                break;
            case Y:
                _positions[0][2] = true;
                _positions[1][1] = true;
                _positions[1][2] = true;
                _positions[2][2] = true;
                _positions[3][2] = true;
                _corners[0][2] = true;
                _corners[1][1] = true;
                _corners[3][2] = true;
                break;
        }
    }

    /**
     * Rotates the piece counter-clockwise around the middle of the 5x5 array.
     */
    public void rotateCCW() {
        boolean[][] newPositions =  new boolean[5][5];
        boolean[][] newCorners = new boolean[5][5];
        for (int j = 4; j >= 0; --j) {
            for (int i = 0; i < 5; ++i) {
                newPositions[4-j][i] = _positions[i][j];
                newCorners[4-j][i] = _corners[i][j];
            }
        }
        _positions = newPositions;
        _corners = newCorners;
    }

    /**
     * Rotates the piece clock-wise around the middle of the 5x5 array.
     */
    public void rotateCW() {
        boolean[][] newPositions =  new boolean[5][5];
        boolean[][] newCorners = new boolean[5][5];
        for (int i = 4; i >= 0; --i) {
            for (int j = 0; j < 5; ++j) {
                newPositions[j][4-i] = _positions[i][j];
                newCorners[j][4-i] = _corners[i][j];
            }
        }
        _positions = newPositions;
        _corners = newCorners;
    }

    /**
     * Flips the piece vertically
     */
    public void flipVertical() {
        boolean[][] newPositions =  new boolean[5][5];
        boolean[][] newCorners = new boolean[5][5];
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                newPositions[4-i][j] = _positions[i][j];
                newCorners[4-i][j] = _corners[i][j];
            }
        }
        _positions = newPositions;
        _corners = newCorners;
    }

    /**
     * Flips the piece horizontally
     */
    public void flipHorizontal() {
        boolean[][] newPositions =  new boolean[5][5];
        boolean[][] newCorners = new boolean[5][5];
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                newPositions[i][4-j] = _positions[i][j];
                newCorners[i][4-j] = _corners[i][j];
            }
        }
        _positions = newPositions;
        _corners = newCorners;
    }

    public boolean[][] getPiece() {
        return _positions;
    }

    public boolean isCorner(int i, int j) {
        return _corners[i][j];//[4-(2+i)][4-(2+j)];
    }

    public Shape getShape() {
        return _shape;
    }

    //for testing
    public void print() {
        System.out.println("piece");
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (this._positions[i][j] == true) {
                    System.out.print("1");
                }
                else {
                    System.out.print("0");
                }
            }
            System.out.println();
        }
        System.out.println("corners");
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (this._corners[i][j] == true) {
                    System.out.print("1");
                }
                else {
                    System.out.print("0");
                }
            }
            System.out.println();
        }
    }
}
