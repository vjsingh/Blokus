package ai;

import blokus.Color;
import blokus.Move;
import java.util.*;

/**
 * This tree allows you to reference each branch by a key and store a value at
 * each node. It's useful for storing game states, where moves are the labels
 * for the edges in between states.
 * @author dkimmel
 */
public class GameTree<M, S extends EvaluableGameState<M>> implements java.io.Serializable  {
    private int _numPlayers;
    private int _currentPlayer;
    private S _state;
    private Map<M,GameTree<M,S>> _moveMap;
    
    // remember these things so you don't need to recalculate (just in case)
    private boolean _expanded;
    private int _maxDepthEvaluated;
    private SearchPair _bestSearchPair;

    /**
     * Creates a tree with the given value stored at the root.
     * @param state The value to initialize this tree with. It's not ok if this
     * value is null.
     * @param numPlayers The number of players in this game.
     * @param currentPlayer The player who is currently making a move.
     */
    public GameTree(S state, int numPlayers, int currentPlayer) {
        _state = state;
        _numPlayers = numPlayers;
        _currentPlayer = currentPlayer;
        _moveMap = new HashMap<M,GameTree<M,S>>();

        _expanded = false;
        _maxDepthEvaluated = -1;
        _bestSearchPair = null;
    }

    /**
     * Adds a new child node to this tree. The child node will be accessible
     * from this tree by traversing the edge with the specified key. If the user
     * of this class tries to add something that is already present, their
     * request will be ignored to save time.
     * @param move The move used to reference this state later. The key cannot
     * be null or there will be problems when you try to get a branch later on.
     */
    public void add(M move) {
        // don't ever replace old subtrees!
        if (!_moveMap.containsKey(move)) {
            _moveMap.put(move, new GameTree(_state.applyMove(move), _numPlayers, (_currentPlayer + 1) % _numPlayers));
        }
    }

    /**
     * Gets a child node by edge key. The key you give it must not be null or
     * you will likely get lots of errors.
     * @param move The key of the edge which you would like to traverse.
     * @return The tree under the edge you would like to traverse. If no edge
     * with this key is found, this will return null.
     */
    public GameTree<M,S> get(M move) {
        return _moveMap.get(move);
    }

    /**
     * Performs a search on the tree with at least the specified depth and
     * returns what the best choice is at the top level.
     * @return The best move for the current player to make.
     */
    public M getBestMove(int minDepth) {
        return expand(minDepth, 1).getMove();
    }

    /**
     * Expands this node by finding all the possible moves and returning the
     * one that had the best set of possible results based on a search of the
     * specified depth. Uses Alpha-Beta pruning.
     * @param minDepth The depth of the search.
     * @param upperBound - The upper bound of the score for the player to move.
     * This means if the current player can make a move that results in a higher
     * score for him, then this entire branch of the tree can be discarded,
     * because the player before him will never choose this branch.
     * @return A pair containing the move used to get to the best result and the
     * actual ratings for each player of the best result. This function will
     * return null if there are no moves off of this tree node. It will put a
     * null move value in the SearchPair it returns if minDepth is 0, because it
     * can't know what move was used to get here.
     */





    private SearchPair expand(int minDepth, double upperBound) {
        if (minDepth == 0 ) {//or end of game
            double[] scores = this.normalize(_state.evaluate());
            _bestSearchPair = new SearchPair(null, scores);
        }

        else if (_expanded) {
            SearchPair maxPair = null;
            double maxScore = 0;
            int count = 1; //DELETE
            for (Map.Entry<M,GameTree<M,S>> mapPair : _moveMap.entrySet()) {
                
                //This is what it was before: GameTree childTree = this.get(mapPair.getKey());
            	GameTree<M, S> childTree = this.get(mapPair.getKey());
                SearchPair movePair = childTree.expand(minDepth - 1, 1 - maxScore);
                double moveScore = movePair.getScores()[_currentPlayer];

                if (moveScore >= upperBound) {
                    movePair = new SearchPair(mapPair.getKey(), movePair.getScores());
                    _bestSearchPair = movePair;
                    return movePair;
                }

                else if (moveScore >= maxScore) {
                    maxPair = new SearchPair(mapPair.getKey(), movePair.getScores());
                    maxScore = moveScore;
                }
                count++;
            }
            _bestSearchPair = maxPair;
            
        }

        // this node's children haven't yet been found, so find them
        // then recall this function as if that was the normal way to do things
        else {
            _expanded = true;
            for (M move : _state.getMoves()) {
                add(move);
            }

            _bestSearchPair = expand(minDepth, upperBound);
        }

        return _bestSearchPair;

    }

    //public for testing
    /**
     * Normalizes the values in the array. Note that this function does no error
     * checking whatsoever, so pass in a valid array.
     * @param values array of values to be normalized
     * @return normalized array
     */
    public double[] normalize(double[] values) {
        int size = values.length;
        double[] newValues = new double[size];

        double sum = 0.0;
        for (double d : values) {
            sum += d;
        }
        for (int i = 0; i < size; i++) {
            newValues[i] = values[i] / sum;
        }

        return newValues;
    }

    /**
     * A convenience class that allows the searching algorithm to remember what
     * argument was used to get the best score as well as the best score itself.
     * Because it's just a storage class, it can hold nulls. This is useful for
     * when we're expand(0)-ing because we don't know what move got us here.
     */
    private class SearchPair implements java.io.Serializable {
        M _move;
        double[] _scores;

        /**
         * Basic constructor.
         * @param move The move to store.
         * @param scores The scores to store.
         */
        public SearchPair(M move, double[] scores) {
            _move = move;
            _scores = scores;
        }

        /**
         * Gives the move from this pair.
         * @return The move from this pair.
         */
        public M getMove() {
            return _move;
        }

        /**
         * Gives the scores from this pair.
         * @return The scores from this pair.
         */
        public double[] getScores() {
            return _scores;
        }
    }

    /**
     * The normal toString() method for printing this game tree to standard
     * out. Helpful for testing and debugging.
     * @return The string representation of this tree.
     */
    @Override
    public String toString() {
        String rn = "[Tree ";

        if (_moveMap.size() == 0) {
            rn += _state;
        }
        
        for (Map.Entry<M,GameTree<M,S>> movePair : _moveMap.entrySet()) {
            rn += movePair.getKey() + " >> " + movePair.getValue() + ", ";
        }
        rn += "]\n";
        
        return rn;
    }

    protected Color getCurrColor() {
        return Color.values()[this._currentPlayer];
    }

    public double[] getBoardRating() {
        return _state.evaluate();
    }

    public boolean hasNoMoves() {
        //if the only move is a pass, return true
        Collection<M> moves = _state.getMoves();
        if (moves.size() == 1) {
            if ( ( (Move)moves.iterator().next()).isPass()) {
                return true;
            }
        }
        //otherwise return false
        return false;
    }
}
