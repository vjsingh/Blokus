package networkTests;

import blokus.*;

/**
 * This class is used to test Network functionality. It allows for specification
 * of a move by the tester instead of using the GUI, AI, or Network to get the
 * move. The only methods it has are mutators and accessors for that move.
 *
 * @author guyoung
 */
public class TestPlayer extends HumanPlayer{

    private Move _move;

    public TestPlayer(String name, Move move){
        super(name);
        _move = move;
    }

    public TestPlayer(String name){
        super(name);
    }

    public Move getMove(){
        return _move;
    }

    public void setMove(Move move){
        _move = move;
    }

    public void setMove(){
        switch(this.getColor()){
            case RED: _move = new Move(new Square(Face.TOP, 7, 7), new Piece(Shape.oneSquare),
                    Color.RED); break;
            case BLUE: _move = new Move(new Square(Face.TOP, 7, 1), new Piece(Shape.oneSquare),
                    Color.BLUE); break;
            case GREEN: _move = new Move(new Square(Face.TOP, 1, 1), new Piece(Shape.oneSquare),
                    Color.GREEN); break;
            case YELLOW: _move = new Move(new Square(Face.TOP, 1, 7), new Piece(Shape.oneSquare),
                    Color.YELLOW); break;
        }
    }

}
