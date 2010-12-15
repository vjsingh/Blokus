package ai;

import blokus.*;

/**
 * This class provides a backend to the AIPlayer interface. It facilitates the
 * AIPlayer's moves by storing the move tree in a central location.
 * @author dkimmel
 */
public class AIEngine<M, S extends EvaluableGameState<M>> implements java.io.Serializable {
    private static AIEngine<Move,GameState> _singleton = new AIEngine<Move,GameState>(new GameState(), 4);
    private GameTree<M,S> _currentState;
    
    /**
     * Constructor. It should be private because this class follows the singleton
     * design pattern. We want this behavior because it allows us to store only
     * one copy of the game tree, which saves space and computation time.
     * It's only public for testing purposes.
     */
    public AIEngine(S state, int numPlayers) {
        _currentState = new GameTree<M,S>(state, numPlayers, 0);
    }

    /**
     * Returns the singleton instance of this class. The reason a singleton is
     * used for this class is that we only want to compute one moves tree (b/c
     * it's very space and time expensive), so we can just use this one.
     * @return The singleton instance of this class.
     */
    public static AIEngine<Move,GameState> getSingleton() {
        return _singleton;
    }

    /**
     * This returns the move that an AI player should play. It can also be used
     * to get the best possible move for a non-AI player (like in a tutorial).
     * @param minDepth The minimum depth of the search for the best move for
     * this player. Each level of player will use a different minDepth value
     * depending where they are in the game, but the basic idea is that their
     * minDepth will increase as their number of moves decreases.
     * @return The best move (as judged by this AI) that the selected player
     * could make at this point in the game.
     */
    public M getBestMove(int minDepth) {
        M move = _currentState.getBestMove(minDepth);
        return move;
    }

    /**
     * Registers the most recent move with this object. This must be called for
     * every move that is performed on the board (including moves for AI
     * players). Don't give it null as an argument.
     * @param move The move that is being registered.
     */
    public void registerMove(M move) {
        // ensure that the new node has been added to the tree, then go to it
        _currentState.add(move);
        _currentState = _currentState.get(move);
    }

    public double[] getBoardRating() {
        return _currentState.getBoardRating();
    }

    public boolean hasNoMoves() {
        return _currentState.hasNoMoves();
    }

    public void reset() {
        _currentState = new GameTree<M,S>((S) new GameState(), 4, 0);
    }
}
