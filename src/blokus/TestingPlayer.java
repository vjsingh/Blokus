package blokus;

import java.io.*;
import java.util.*;

/**
 * This player can either play a set group of moves (which are predetermined by
 * a list of moves) or
 * @author dkimmel
 */
public class TestingPlayer extends Player {
    private List<Move> _moveList;
    private int _position;

    public TestingPlayer(List<Move> moveList, String name) {
        super(name);
        _moveList = moveList;
        _position = 0;
    }

    public TestingPlayer(String name) {
        super(name);
        _moveList = null;
        _position = 0;
    }

    @Override
    public Move getMove() throws Exception {
        // if the moves are predetermined, return them
        if (_moveList != null) {
            _position++;
            return _moveList.get(_position-1);
        }

        // otherwise, read them from the command line
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Face face;
        int row;
        int column;
        Shape shape;

        System.out.println("Make your move. Enter the face (as the name of the face), " +
                "row (as an int between 0 and 8), column (as an int between 0 and 8), " +
                "and shape (as the name of the shape), one on each line.");

        face = Face.valueOf(br.readLine());
        row = Integer.parseInt(br.readLine());
        column = Integer.parseInt(br.readLine());
        shape = Shape.valueOf(br.readLine());

        return new Move(new Square(face, row, column), new Piece(shape), super.getColor());
    }
}
