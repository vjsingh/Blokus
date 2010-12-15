package ai;

import blokus.*;

/**
 * This provides the interface necessary for an AIPlayer to return moves. The
 * AIPlayer depends on the AIEngine to do most of its internal functions. This
 * is a good thing because it allows several AI players to use the same move
 * tree, which is totally fine and saves a lot of space and time.
 * @author dkimmel
 */
public class AIPlayer extends Player {
    private int _difficulty;
    private AIEngine<Move,GameState> _engine;

    public AIPlayer(String name, int difficulty) {
        super(name);
        _difficulty = difficulty;
        _engine = AIEngine.getSingleton();
    }

    public Move getMove() {
        // instead of just passing _difficulty as the minDepth
        Move move = _engine.getBestMove(_difficulty);
        return move;
    }

    public AIEngine<Move, GameState> getEngine() {
        return _engine;
    }
}
