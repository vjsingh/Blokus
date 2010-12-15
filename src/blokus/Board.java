package blokus;

import java.io.Serializable;
import java.util.*;
import static blokus.Constants.BOARD_SIZE;

/**
 * 
 * @author ahills, dkimmel, guyoung, vjsingh
 */
public class Board implements Serializable {
    private Color[][][] _board;
    private Stack<Move> previous_moves;
    private Stack<Move> future_moves;
    private LinkedList<Move> initial_moves;

    public Board() {
        this(new Color[6][BOARD_SIZE][BOARD_SIZE]);
    }

    /**
     * Creates the board based on the passed in color array. The format is
     * [face][row][column]. Also sets the initial moves of the board, where
     * each player has these corners to start:
     * Player 1: Blue - top/front/left & bottom/back/right
     * Player 2: Yellow - top/back/right & bottom/front/left
     * Player 3: Red - top/front/right & bottom/back/left
     * Player 4: Green - top/back/left & bottom/front/right
     * @param board A 3-dimensional array of colors representing the board state
     */
    public Board(Color[][][] board) {
        _board = board;
        previous_moves = new Stack<Move>();
        future_moves = new Stack<Move>();

        //Set initial moves for all 4 players
        initial_moves = new LinkedList<Move>();

        //blue
        LinkedList<Square> initialSquares1 = new LinkedList<Square>();
        LinkedList<Square> initialSquares2 = new LinkedList<Square>();

        //yellow
        LinkedList<Square> initialSquares3 = new LinkedList<Square>();
        LinkedList<Square> initialSquares4 = new LinkedList<Square>();

        //red
        LinkedList<Square> initialSquares5 = new LinkedList<Square>();
        LinkedList<Square> initialSquares6 = new LinkedList<Square>();

        //green
        LinkedList<Square> initialSquares7 = new LinkedList<Square>();
        LinkedList<Square> initialSquares8 = new LinkedList<Square>();
        
        initial_moves = new LinkedList<Move>();
        int edgeCoordinate = Constants.BOARD_SIZE - 1;

        //blue
        initialSquares1.add(new Square(Face.TOP, edgeCoordinate, 0));
        initialSquares1.add(new Square(Face.FRONT, 0, 0));
        initialSquares1.add(new Square(Face.LEFT, 0, edgeCoordinate));
        initialSquares2.add(new Square(Face.BACK, edgeCoordinate, 0));
        initialSquares2.add(new Square(Face.RIGHT, edgeCoordinate, edgeCoordinate));
        initialSquares2.add(new Square(Face.BOTTOM, edgeCoordinate, edgeCoordinate));

        //yellow
        initialSquares3.add(new Square(Face.TOP, 0, edgeCoordinate));
        initialSquares3.add(new Square(Face.BACK, 0, 0));
        initialSquares3.add(new Square(Face.RIGHT, 0, edgeCoordinate));
        initialSquares4.add(new Square(Face.BOTTOM, 0, 0));
        initialSquares4.add(new Square(Face.FRONT, edgeCoordinate, 0));
        initialSquares4.add(new Square(Face.LEFT, edgeCoordinate, edgeCoordinate));

        //red
        initialSquares5.add(new Square(Face.TOP, edgeCoordinate, edgeCoordinate));
        initialSquares5.add(new Square(Face.FRONT, 0, edgeCoordinate));
        initialSquares5.add(new Square(Face.RIGHT, 0, 0));
        initialSquares6.add(new Square(Face.BOTTOM, edgeCoordinate, 0));
        initialSquares6.add(new Square(Face.LEFT, edgeCoordinate, 0));
        initialSquares6.add(new Square(Face.BACK, edgeCoordinate, edgeCoordinate));

        //green
        initialSquares7.add(new Square(Face.TOP, 0, 0));
        initialSquares7.add(new Square(Face.LEFT, 0, 0));
        initialSquares7.add(new Square(Face.BACK, 0, edgeCoordinate));
        initialSquares8.add(new Square(Face.FRONT, edgeCoordinate, edgeCoordinate));
        initialSquares8.add(new Square(Face.RIGHT, edgeCoordinate, 0));
        initialSquares8.add(new Square(Face.BOTTOM, 0, edgeCoordinate));

        Move move1 = new Move(initialSquares1, Color.BLUE);
        Move move2 = new Move(initialSquares2, Color.BLUE);
        Move move3 = new Move(initialSquares3, Color.YELLOW);
        Move move4 = new Move(initialSquares4, Color.YELLOW);
        Move move5 = new Move(initialSquares5, Color.RED);
        Move move6 = new Move(initialSquares6, Color.RED);
        Move move7 = new Move(initialSquares7, Color.GREEN);
        Move moveedgeCoordinate = new Move(initialSquares8, Color.GREEN);

        setInitialMove(move1);
        setInitialMove(move2);
        setInitialMove(move3);
        setInitialMove(move4);
        setInitialMove(move5);
        setInitialMove(move6);
        setInitialMove(move7);
        setInitialMove(moveedgeCoordinate);
    }

     /**
     * @return A copy of this board
     */
    public Board(Board other) {
        previous_moves = new Stack<Move>();
        future_moves = new Stack<Move>();
        initial_moves = new LinkedList<Move>();

        this._board = new Color[6][Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                for (int k = 0; k < Constants.BOARD_SIZE; k++) {
                    _board[i][j][k] =
                            other.getSquare(new Square(Face.values()[i], j, k));
                }
            }
        }

        for (Move move : other.future_moves) {
            future_moves.add(move);
        }
        for (Move move : other.previous_moves) {
            previous_moves.add(move);
        }
        for (Move move : other.initial_moves) {
            initial_moves.add(move);
        }

    }

    /**
     * Same as isValidMessage but returns a boolean instead of a string
     */
    public boolean isValidBoolean(Move move) {
        return (isValidMessage(move) == MoveMessage.SUCCESS);
    }

    /**
     * Checks whether a given move is valid
     * @param move Move to check
     * @return A status message detailing the result of the check. Can be:
     * "Success" or
     * "Pieces cannot overlap" or
     * "Your new piece cannot be adjacent to another one of your pieces"
     */
    public MoveMessage isValidMessage(Move move) {
        return isValidMessage(move, move.getColor());
    }

    /**
     * Same as isValidMessage(Move move) but takes a color instead of a move
     */
    public MoveMessage isValidMessage(Move move, Color color) {
        //return false if the move is invalid
        //this only happens when one of the squares of the move is on a square
        //adjacent to a corner, which means it is overlapping a starting square
        if (!move.isValid()) {
            return MoveMessage.OVERLAP;
        }
        
        //return success if the move is a pass
        if (move.isPass())
            return MoveMessage.SUCCESS;

        int face;
        int row;
        int column;

        boolean hasValidBuildPoint = false;

        for (Square square : move.getSquares()) {
            face = square.getFace().ordinal();
            row = square.getRow();
            column = square.getColumn();
            if (_board[face][row][column] != null) {
                return MoveMessage.OVERLAP;
            }
            for (Square square2 : square.getAdjacentSquares()) {
                face = square2.getFace().ordinal();
                row = square2.getRow();
                column = square2.getColumn();
                if (_board[face][row][column] == color ) {
                    return MoveMessage.ADJACENT;
                }
            }
            if (this.isValidBuildPoint(square, color))
                hasValidBuildPoint = true;
        }

        if (!hasValidBuildPoint)
            return MoveMessage.NOCORNER;
        return MoveMessage.SUCCESS;
    }

    /**
     * Returns whether the player with specified color could play their one
     * square in the location given.
     * @param s The location on the board where we want to see if the one-pice
     * could go.
     * @param c The color of the player who would be playing the piece.
     * @return Whether or not this would be a legal move.
     */
    public boolean isValidBuildPoint(Square s, Color c) {
        // check if the square is filled
        if (_board[s.getFace().ordinal()][s.getRow()][s.getColumn()] != null) {
            return false;
        }

        // check if the square is at a corner to the right color
        boolean cornerExists = false;
        for (Square corner : s.getCorners()) {
            int face = corner.getFace().ordinal();
            int row = corner.getRow();
            int col = corner.getColumn();
            if (_board[face][row][col] == c) {
                cornerExists = true;
                break;
            }
        }
        if (!cornerExists) {
            return false;
        }

        // check if the square is not adjacent by side to the same color
        for (Square side : s.getAdjacentSquares()) {
            int face = side.getFace().ordinal();
            int row = side.getRow();
            int col = side.getColumn();
            if (_board[face][row][col] == c) {
                return false;
            }
        }

        return true;
    }

    /**
     * Attempts to set a move. Will only make the move if it is valid. Returns
     * the result of Board.isValidMessage.
     * @param move Move to make
     * @return Board.isValidMessage(move)
     */
    public String setMove(Move move) {
        MoveMessage returnMessage = this.isValidMessage(move);

        //set the move if it is valid
        if (returnMessage == MoveMessage.SUCCESS) {
            int face;
            int row;
            int column;
            for (Square square : move.getSquares()) {
                face = square.getFace().ordinal();
                row = square.getRow();
                column = square.getColumn();
                _board[face][row][column] = move.getColor();
            }
            previous_moves.push(move);
        }
        return returnMessage.getMessage();
    }

    public void setInitialMove(Move move) {
        initial_moves.add(move);

        int face;
        int row;
        int column;
        for (Square square : move.getSquares()) {
            face = square.getFace().ordinal();
            row = square.getRow();
            column = square.getColumn();
            _board[face][row][column] = move.getColor();
        }
    }

    public List<Move> getInitialMoves() {
        return initial_moves;
    }

    public Color getSquare(Square square) {
        return _board[square.getFace().ordinal()]
                [square.getRow()]
                [square.getColumn()];
    }

    /**
     * Rolls the board back numOfMoves.
     * @param numOfMoves The number of moves to roll back
     * @return If you try and roll back more moves than there have been in the
     * game, will roll the game back to the initial state and return false.
     * Otherwise, returns true;
     */
    public boolean rollBack(int numOfMoves) {
        for (int i = 0; i < numOfMoves; i++) {
            if (previous_moves.isEmpty())
                return false;
            Move move = previous_moves.pop();
            for (Square square : move.getSquares()) {
                _board[square.getFace().ordinal()]
                      [square.getRow()]
                      [square.getColumn()] = null;
            }
            future_moves.push(move);
        }
        return true;
    }
    
    /**
     * Rolls the board forward numOfMoves.
     * @param numOfMoves The number of moves to roll forward
     * @return If you try and roll forward more moves than there are available
     * in the game, will roll forward to the furthest possible (current) state
     * of the game, and return false.
     * Otherwise, returns true;
     */
    public boolean rollForward(int numOfMoves) {
        for (int i = 0; i < numOfMoves; i++) {
            if (future_moves.isEmpty())
                return false;
            Move move = future_moves.pop();
            for (Square square : move.getSquares()) {
                _board[square.getFace().ordinal()]
                      [square.getRow()]
                      [square.getColumn()] = move.getColor();
            }
            previous_moves.push(move);
        }
        return true;
    }

    //for testing
    /*
     * return the number of squares played on the board NOT COUNTING THE INITIAL
     * SQUARES.
     */
    protected int numOfSquares() {
        int num = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                for (int k = 0; k < BOARD_SIZE; k++) {
                   if (_board[i][j][k] != null)
                       num++;
                }
            }
        }
        return num - 24; //minus the initial squares
    }

    public void display() {
        for (int i = 0; i < 6; i++) {
            System.out.println(Face.values()[i]);
            for (int j = 0; j < BOARD_SIZE; j++) {
                for (int k = 0; k < BOARD_SIZE; k++) {
                    if (_board[i][j][k] != null) {
                        String s = "";
                        switch (_board[i][j][k]) {
                            case BLUE:
                                s = "B"; break;
                            case YELLOW:
                                s = "Y"; break;
                            case RED:
                                s = "R"; break;
                            case GREEN:
                                s = "G"; break;
                        }
                        System.out.print(s);
                    } else {
                        System.out.print(".");
                    }
                }
                System.out.println();
            }
            System.out.print("\n\n");
        }
    }

    public List<Move> getAllMoves() {
        List<Move> rn = new LinkedList<Move>();
        
        for (int i = 0; i < previous_moves.size(); i++) {
            rn.add(i, previous_moves.get(i));
        }
        for (int i = 0; i < future_moves.size(); i++) {
            rn.add(0, future_moves.get(i));
        }

        return rn;
    }

    /**
     * ONLY USE FOR TESTING DOES WEIRD THINGS
     */
    public void setMoveTesting(Move move) {
        int face;
        int row;
        int column;
        for (Square square : move.getSquares()) {
            face = square.getFace().ordinal();
            row = square.getRow();
            column = square.getColumn();
            _board[face][row][column] = move.getColor();
        }
    }
}
