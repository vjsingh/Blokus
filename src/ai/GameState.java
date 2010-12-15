package ai;

import java.util.*;
import blokus.*;

/**
 * This class stores some data that the AIEngine will need to know about a given
 * board. This information includes some data about where moves can be placed
 * for each player, the current board state, and the pieces each player still
 * has.
 * @author dkimmel
 */
public class GameState implements EvaluableGameState<Move>, java.io.Serializable {
    private Board _board;
    private List<Collection<Square>> _buildingPoints;
    private boolean[][] _remainingPieces;
    private int _whoseTurn;

    //to calculate avg distance from center
    //each variable holds information for all 4 players
    private int[] _counts; //total squares played
    private double[] _averageRow; //average distance in rows
    private double[] _averageColumn; //average distance in columns

    /**
     * Creates a new game state where nobody has made any moves yet.
     */
    public GameState() {
        _board = new Board();
        _buildingPoints = new ArrayList<Collection<Square>>(4);
        for (int i = 0; i < 4; i++) {
            _buildingPoints.add(new HashSet<Square>());
        }
        for (Move m : _board.getInitialMoves()) {
            _buildingPoints.get(m.getColor().ordinal()).addAll(m.getBuildSquares());
        }
        
        _remainingPieces = new boolean[4][Shape.values().length];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 21; j++) {
                _remainingPieces[i][j] = true;
            }
        }

        _whoseTurn = 0;

        _counts = new int[4];
        _averageRow = new double[4];
        _averageColumn = new double[4];
    }

    /**
     * for testing. initializes a gamestate where each player only has one type
     * of piece.
     * @param piece The number of the piece that this player has.
     */
    public GameState(int piece) {
        this();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 21; j++) {
                if (j != piece) {
                    _remainingPieces[i][j] = false;
                }
            }
        }
    }

    /**
     * The copy constructor does a deep copy of another game state so that it
     * can be modified through applyMove(). This method is private so that you
     * can only get its functionality through applyMove().
     * @param other The other game state that we wish to copy.
     */
    public GameState(GameState other) {
        _board = new Board(other._board);
        _buildingPoints = new ArrayList<Collection<Square>>(4);
        for (int i = 0; i < 4; i++) {
            _buildingPoints.add(new HashSet<Square>());
            _buildingPoints.get(i).addAll(other._buildingPoints.get(i));
        }

        _remainingPieces = new boolean[4][21];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 21; j++) {
                _remainingPieces[i][j] = other._remainingPieces[i][j];
            }
        }
        
        _whoseTurn = (other._whoseTurn + 1) % 4;

        _counts = new int[4];
        _averageRow = new double[4];
        _averageColumn = new double[4];

        for (int i = 0; i < 4; i++) {
            _counts[i] = other._counts[i];
            _averageRow[i] = other._averageRow[i];
            _averageColumn[i] = other._averageColumn[i];
        }
        
    }

    /**
     * Makes a copy of an existing board state and modifies it so that it will
     * have the specified move inserted onto it. Do not pass this an illegal
     * or null move or there will be trouble. Note that the game state returned
     * is not the same game state that this function was called on, but a new
     * one.
     * @param move The move to add to a copy of the current board state.
     * @return The new game state with the supplied move added to it.
     */
    public GameState applyMove(Move move) {
        GameState newState = new GameState(this);
        newState._board.setMove(move);

        // remove any build points that don't work anymore
        for (int i = 0; i < 4; i++) {
            for (Iterator<Square> iter = newState._buildingPoints.get(i).iterator(); iter.hasNext(); ) {
                Square maybe = iter.next();
                if (!newState._board.isValidBuildPoint(maybe, Color.values()[i])) {
                    iter.remove();
                }
            }
        }
        
        // add any new legal squares
        List<Square> newPlaces = move.getBuildSquares();
        for (Square s : newPlaces) {
            // fill in this move to make sure the point is valid
            if (newState._board.isValidBuildPoint(s, move.getColor())) {
                if (!newState._buildingPoints.get(move.getColor().ordinal()).contains(s)) {
                    newState._buildingPoints.get(move.getColor().ordinal()).add(s);
                }
            }
        }

        // set this piece's usage to false
        if (!move.isPass()) {
            newState._remainingPieces[move.getColor().ordinal()][move.getShape().ordinal()] = false;
        }

        //calculate new average distances from center for this player
        LinkedList<Square> squares = move.getSquares();

        int playerNum = move.getColor().ordinal();
        double totalRow = newState._averageRow[playerNum] * newState._counts[playerNum];
        double totalColumn = newState._averageColumn[playerNum] * newState._counts[playerNum];
        
        for (Square square : squares) {
            totalRow += square.getRow();
            totalColumn += square.getColumn();
            newState._counts[playerNum]++;
        }
        
        newState._averageRow[playerNum] = totalRow / newState._counts[playerNum];
        newState._averageColumn[playerNum] = totalColumn / newState._counts[playerNum];

        return newState;
    }

    /**
     * Gives the board evaluation values for each of the four players.
     * @return The "goodness" value of this board for each player.
     */
    public double[] evaluate() {
        double[] rn = new double[4];

        // multiply by the # of building points
        for (int i = 0; i < 4; i++) {
            rn[i] = _buildingPoints.get(i).size();
        }

        // divide by the average distance of each square played to the center
        // of the face that it is on

        double[] _averageDistanceRow  = new double[4];
        double[] _averageDistanceColumn  = new double[4];

        for (int i = 0; i < 4; i++) {
            _averageDistanceRow[i] = _averageRow[i] - (int) ((Constants.BOARD_SIZE / 2));
            _averageDistanceColumn[i] = _averageColumn[i] - (int) ((Constants.BOARD_SIZE / 2));

            //this should only hurt the player, so if it is less than 1, don't
            //do anything
            double avgDistance = Math.sqrt((
                    _averageDistanceRow[i] * _averageDistanceRow[i]) +
                    (_averageDistanceColumn[i] * _averageDistanceColumn[i]));
            if (avgDistance >= 1) {
                rn[i] /= avgDistance;
            }
        }

        // multiply by the # of squares played so far squared
        for (int i = 0; i < 4; i++) {
            rn[i] *= _counts[i] * _counts[i];
        }

        return rn;
    }

    /**
     * Gives a list of all the valid moves that could be made at this point in
     * the game. Necessary for the GameTree to know about all the choices it
     * could make.
     * @return A list of the valid moves on the board. If there are no valid
     * moves, returns a list containing only the "pass" move.
     */
    public Collection<Move> getMoves() {
        Collection<Move> moves = new HashSet<Move>();
        Collection<Move> invalidMoves = new HashSet<Move>();
        Color currColor = Color.values()[_whoseTurn];
        int numOfPieces = _remainingPieces[0].length;
        
        // for each building point
        for (Square s : _buildingPoints.get(_whoseTurn)) {
            // for each remaining piece
            for (int p = 0; p < numOfPieces; p++) {
                if (_remainingPieces[_whoseTurn][p] == true) {
                    Piece piece = new Piece(Shape.values()[p]);
                    // for each flip
                    for (int f = 0; f < 2; f++) {
                        // for each rotation
                        for (int r = 0; r < 4; r++) {
                            // for starting position x,y
                            for (int x = -2; x <= 2; x++) {
                                for (int y = -2; y <= 2; y++) {
                                    // TODO: if !corners[2-x][2-y] then we don't need to check this center point
                                    /*if (!piece.isCorner(2+y, 2-x)) {
                                        break;
                                    }*/
                                    Square center = s.getRelativeSquare(x, y);
                                    Move move = new Move(center, new Piece(piece.getShape(), piece.getPiece()), currColor);
                                    if (moves.contains(move) || invalidMoves.contains(move)) {
                                        // do nothing
                                    } else if (_board.isValidBoolean(move)) {
                                        moves.add(move);
                                    } else {
                                        invalidMoves.add(move);
                                    }
                                }
                            }
                            if (r != 3) piece.rotateCW();
                        }
                        if (f != 1) piece.flipHorizontal();
                    }
                }
            }
        }

        if (moves.isEmpty()) {
            moves.add(new Move(Color.values()[_whoseTurn]));
        }

        return moves;
    }

    public Board getBoard() {
        return _board;
    }
}
