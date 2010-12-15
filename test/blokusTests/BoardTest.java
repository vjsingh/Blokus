/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package blokusTests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vjsingh
 */
public class BoardTest {

    Move _move1; // top/back edge 4Line vertical
    Move _move2; // bottom/back edge 5Z - for corner test
    Move _move3; // bottom/left/front corner 5T
    Move _move4; // top 4line vertical, adjacent to move1
    Move _move5; // top 4line vertical, adjacent to move1, different player
    Board _board;
    
    public BoardTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {

        Piece piece1 = new Piece(Shape.fourLine);
        Piece piece2 = new Piece(Shape.Z);
        Piece piece3 = new Piece(Shape.fiveT);
        piece3.rotateCW();
        Piece piece4 = new Piece(Shape.fourLine);

        Square position1 = new Square(Face.TOP, 0, 4);
        Square position2 = new Square(Face.BACK, 7, 6);
        Square position3 = new Square(Face.LEFT, 8, 8);
        Square position4 = new Square(Face.TOP, 2, 5);

        _move1 = new Move(position1, piece1, Color.BLUE);
        _move2 = new Move(position2, piece2, Color.BLUE);
        _move3 = new Move(position3, piece3, Color.BLUE);
        _move4 = new Move(position4, piece4, Color.BLUE);
        _move5 = new Move(position4, piece4, Color.GREEN);
        
        _board = new Board();

        //place squares so these moves are valid (diagonally adjacent)
        //this doesn't update previous moves
        Move move1 = new Move(new Square(Face.TOP, 2, 5),
                new Piece(Shape.oneSquare), Color.BLUE);
        _board.setMoveTesting(move1);
        Move move2 = new Move(new Square(Face.BACK, 5, 4),
                new Piece(Shape.oneSquare), Color.BLUE);
        _board.setMoveTesting(move2);
    }

    @After
    public void tearDown() {
    }

    /**
     * Tests that the initial squares are set up correctly
     * Player 1: Blue - top/front/left & bottom/back/right
     * Player 2: Yellow - top/back/right & bottom/front/left
     * Player 3: Red - top/front/right & bottom/back/left
     * Player 4: Green - top/back/left & bottom/front/right
     */
    @Test
    public void testConstructor() {
        assert (_board.getInitialMoves().size() == 8);
        System.out.println(_board.getSquare(new Square(Face.TOP, 8, 0)).toString());
        assert (_board.getSquare(new Square(Face.TOP, 8, 0)) == Color.BLUE);
        assert (_board.getSquare(new Square(Face.FRONT, 8, 0)) == Color.YELLOW);
        assert (_board.getSquare(new Square(Face.LEFT, 8, 0)) == Color.RED);
        assert (_board.getSquare(new Square(Face.BACK, 0, 8)) == Color.GREEN);
    }
    /**
     * Test of isValidMessage method, of class Board.
     */
    @Test
    public void testIsValid() {
        System.out.println("isValid");
        
        assert(_board.isValidMessage(_move1) == MoveMessage.SUCCESS);
        assert(_board.isValidMessage(_move2) == MoveMessage.SUCCESS);
        assert(!_move3.isValid());
        assert(_board.isValidMessage(_move3) == MoveMessage.OVERLAP);

        _board.setMove(_move1);
        assert(_board.isValidMessage(_move1) == MoveMessage.OVERLAP);
        assert(_board.isValidMessage(_move4) == MoveMessage.ADJACENT);
        
        
    }


    /**
     * Test of rollBack and rollForward method, of class Board.
     */
    @Test
    public void testRollBackForward() {
        //all the numOfSquares are +2 because of initial squares placed
        System.out.println("rollBack");
        assert(_board.numOfSquares() - 2 == 0);

        _board.setMove(_move1);
        System.out.println(_board.numOfSquares());
        assert(_board.numOfSquares() - 2 == 4);

        _board.setMove(_move2);
        assert(_board.numOfSquares() - 2 == 9);
        
        _board.setMove(_move3); //invalid move
        assert(_board.numOfSquares() - 2 == 9);

        _board.rollBack(1);
        assert(_board.numOfSquares() - 2 == 4);
        _board.rollBack(1);
        assert(_board.numOfSquares() - 2 == 0);
        assert(!_board.rollBack((1))); //can't go any further

        _board.rollForward(1);
        assert (_board.numOfSquares() - 2 == 4);
        _board.rollForward(1);
        assert (_board.numOfSquares() - 2 == 9);
        assert(!_board.rollForward(1)); //can't go any further

        _board.rollBack(2);
        assert(_board.numOfSquares() - 2 == 0);
        _board.rollForward(2);
        assert(_board.numOfSquares() - 2 == 9);

    }

    @Test
    public void testIsValidBuildPoint() {
       assert(_board.isValidBuildPoint(new Square(Face.FRONT, 1, 1), Color.BLUE));
       Move move1 = new Move(new Square(Face.LEFT, 1, 6), 
               new Piece(Shape.F), Color.BLUE);
       _board.setMove(move1);
       assert(_board.isValidBuildPoint(new Square(Face.RIGHT, 7, 7), Color.BLUE));

//       ADDING MOVE[BLUE: (LEFT 0, 5), (LEFT 0, 6), (LEFT 1, 6), (LEFT 1, 7), (LEFT 2, 6), ]
//REMOVING, with i 0 RIGHT row: 7column: 7
//REMOVING, with i 0 LEFT row: 1column: 7
       //1 top/back edge 4Line vertical
       //2 bottom/back edge 5Z - for corner test
    }

}