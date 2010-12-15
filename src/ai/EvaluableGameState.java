package ai;

import java.util.*;

/**
 * This interface is used to allow the Tree class to take care of alpha-beta
 * pruning on its own even though the Tree is generic.
 * @author dkimmel
 */
public interface EvaluableGameState<M> {
    /**
     * This function evaluates the board from each player's point of view and
     * returns the resulting vector of values in an array of doubles.
     * @return The ratings for this board as described above.
     */
    public double[] evaluate();

    /**
     * This function returns a collection of all the possible moves that could
     * be made next.
     * @return The collection of moves. This value will never be null.
     */
    public Collection<M> getMoves();

    /**
     * Creates a copy of this class and modifies it so that the given move is
     * applied to the board.
     * @param move The move to apply to the board.
     * @return The state of the game after the move is applied.
     */
    public EvaluableGameState<M> applyMove(M move);
}
