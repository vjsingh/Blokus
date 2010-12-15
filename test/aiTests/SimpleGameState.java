package aiTests;

import ai.EvaluableGameState;
import java.util.*;

/**
 * This is a very simple game that allows us to test out the Tree and AIEngine
 * classes more easily than with Blokus boards. There are n players, and at each
 * turn, the player whose turn it is picks a number between 1 and 4. A board is
 * rated for a particular player by adding up the sum of all the numbers they've
 * requested so far. I said it was simple!
 * @author dkimmel
 */
public class SimpleGameState implements EvaluableGameState<Integer> {
    double[] _sums;
    int _whoseMove;

    public SimpleGameState(int numPlayers) {
        _sums = new double[numPlayers];
        _whoseMove = 0;
    }

    private SimpleGameState(SimpleGameState other) {
        _sums = new double[other._sums.length];

        for (int i = 0; i < _sums.length; i++) {
            _sums[i] = other._sums[i];
        }

        _whoseMove = other._whoseMove;
    }

    public SimpleGameState applyMove(Integer move) {
        SimpleGameState newState = new SimpleGameState(this);
        
        newState._sums[_whoseMove] += move.intValue();
        newState._whoseMove = (_whoseMove + 1) % _sums.length;

        return newState;
    }

    public double[] evaluate() {
        return _sums;
    }

    public Collection<Integer> getMoves() {
        Collection<Integer> rn = new ArrayList<Integer>(4);

        rn.add(new Integer(3));
        rn.add(new Integer(2));
        rn.add(new Integer(1));
        rn.add(new Integer(4));

        return rn;
    }

    @Override
    public String toString() {
        String rn = "[SimpleGameState ";

        for (int i = 0; i < _sums.length; i++) {
            rn += _sums[i] + ",";
        }

        rn += " value:" + evaluate() + "]";

        return rn;
    }
}
