package blokusTests;

import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author vjsingh
 */
public class MoveTest {

    Move _move1; // top/back edge 4Line vertical
    Move _move2; // bottom/back edge Z - for corner test
    Move _move3; // bottom/left/front corner 5T
    Move _move4; // top/right edge 4line horizontal
    Move _move5; // top/front edge 4line vertical
    Move _move6; // top/left edge 4line horizontal
    Move _move7; // bottom/right edge 4line horizontal
    Move _move8; // bottom/frontedge 4line vertical
    Move _move9; // same as move1 but made a different way
    Move _move10; // same as move4 but made in a different way
    
    public MoveTest() {
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
        piece4.rotateCW();
//        Piece piece10 = new Piece(Shape.fourLine);
//        piece10.rotateCW();
//        piece10.flipHorizontal();

        Square position1 = new Square(Face.TOP, 0, 2);
        Square position2 = new Square(Face.BACK, 7, 6);
        Square position3 = new Square(Face.LEFT, 8, 8);
        Square position4 = new Square(Face.TOP, 2, 8);
        Square position5 = new Square(Face.TOP, 8, 2);
        Square position6 = new Square(Face.TOP, 2, 0);
        Square position7 = new Square(Face.BOTTOM, 2, 8);
        Square position8 = new Square(Face.BOTTOM, 0, 2);
        Square position9 = new Square(Face.BACK, 0, 6);
        Square position10 = new Square(Face.RIGHT, 0, 6);

        _move1 = new Move(position1, piece1, Color.BLUE);
        _move2 = new Move(position2, piece2, Color.BLUE);
        _move3 = new Move(position3, piece3, Color.BLUE);
        _move4 = new Move(position4, piece4, Color.BLUE);
        _move5 = new Move(position5, piece1, Color.BLUE);
        _move6 = new Move(position6, piece4, Color.BLUE);
        _move7 = new Move(position7, piece4, Color.BLUE);
        _move8 = new Move(position8, piece1, Color.BLUE);
        _move9 = new Move(position9, piece1, Color.BLUE);
        _move10 = new Move(position10, piece1, Color.BLUE);
    }

    @After
    public void tearDown() {
    }

    /**
     * Tests that the constructor correctly identifies a piece that has a square
     * on the corner piece as invalid.
     */
    @Test
    public void testConstructor() {
        assert(_move1.isValid());
        assert(_move2.isValid());
        assert(!_move3.isValid());
        assert(_move4.isValid());
    }
    /**
     * Test of getBuildSquares method, of class Move.
     */
    @Test
    public void testGetBuildSquares() {
        LinkedList<Square> squares = _move1.getBuildSquares();
        
        assert(squares.size() == 4);
        
        assert(squares.get(0).getFace() == Face.BACK);
        assert(squares.get(0).getRow() == 2);
        assert(squares.get(0).getColumn() == 5);
        assert(squares.get(1).getFace() == Face.BACK);
        assert(squares.get(1).getRow() == 2);
        assert(squares.get(1).getColumn() == 7);
        assert(squares.get(2).getFace() == Face.TOP);
        assert(squares.get(2).getRow() == 2);
        assert(squares.get(2).getColumn() == 1);
        assert(squares.get(3).getFace() == Face.TOP);
        assert(squares.get(3).getRow() == 2);
        assert(squares.get(3).getColumn() == 3);


        squares = _move2.getBuildSquares();
        
        assert(squares.size() == 6);
        assert(squares.get(0).getFace() == Face.BACK);
        assert(squares.get(0).getRow() == 5);
        assert(squares.get(0).getColumn() == 4);
        assert(squares.get(1).getFace() == Face.BACK);
        assert(squares.get(1).getRow() == 7);
        assert(squares.get(1).getColumn() == 4);
        assert(squares.get(2).getFace() == Face.BACK);
        assert(squares.get(2).getRow() == 5);
        assert(squares.get(2).getColumn() == 7);
        assert(squares.get(3).getFace() == Face.BOTTOM);
        assert(squares.get(3).getRow() == 8);
        assert(squares.get(3).getColumn() == 3);
        assert(squares.get(4).getFace() == Face.BACK);
        assert(squares.get(4).getRow() == 7);
        assert(squares.get(4).getColumn() == 8);
        assert(squares.get(5).getFace() == Face.BOTTOM);
        assert(squares.get(5).getRow() == 8);
        assert(squares.get(5).getColumn() == 0);

        
    }

    /**
     * Test of getSquares method, of class Move.
     */
    @Test
    public void testGetSquares() {
        System.out.println("getSquares");
        LinkedList<Square> squares = _move1.getSquares();

        assert(squares.size() == 4);
        assert(squares.get(0).getFace() == Face.BACK);
        assert(squares.get(0).getRow() == 1);
        assert(squares.get(0).getColumn() == 6);
        assert(squares.get(1).getFace() == Face.BACK);
        assert(squares.get(1).getRow() == 0);
        assert(squares.get(1).getColumn() == 6);
        assert(squares.get(2).getFace() == Face.TOP);
        assert(squares.get(2).getRow() == 0);
        assert(squares.get(2).getColumn() == 2);
        assert(squares.get(3).getFace() == Face.TOP);
        assert(squares.get(3).getRow() == 1);
        assert(squares.get(3).getColumn() == 2);

        squares = _move3.getSquares();
        assert(squares.size() == 5);
        assert(squares.get(0).getFace() == Face.FRONT);
        assert(squares.get(0).getRow() == 7);
        assert(squares.get(0).getColumn() == 0);
        assert(squares.get(1).getFace() == Face.LEFT);
        assert(squares.get(1).getRow() == 8);
        assert(squares.get(1).getColumn() == 7);
        assert(squares.get(2).getFace() == Face.LEFT);
        assert(squares.get(2).getRow() == 8);
        assert(squares.get(2).getColumn() == 8);
        assert(squares.get(3).getFace() == Face.FRONT);
        assert(squares.get(3).getRow() == 8);
        assert(squares.get(3).getColumn() == 0);
        //This square is invalid because it is a corner square
//        assert(squares.get(4).getFace() == Face.BOTTOM);
//        assert(squares.get(4).getRow() == 0);
//        assert(squares.get(4).getColumn() == 0);

        squares = _move4.getSquares();
        assert(squares.size() == 4);
        assert(squares.get(2).getFace() == Face.RIGHT);
        assert(squares.get(2).getRow() == 0);
        assert(squares.get(2).getColumn() == 6);

        squares = _move5.getSquares();
        assert(squares.size() == 4);
        assert(squares.get(3).getFace() == Face.FRONT);
        assert(squares.get(3).getRow() == 0);
        assert(squares.get(3).getColumn() == 2);

        squares = _move6.getSquares();
        assert(squares.size() == 4);
        assert(squares.get(0).getFace() == Face.LEFT);
        assert(squares.get(0).getRow() == 0);
        assert(squares.get(0).getColumn() == 2);

        squares = _move7.getSquares();
        assert(squares.size() == 4);
        assert(squares.get(2).getFace() == Face.RIGHT);
        assert(squares.get(2).getRow() == 8);
        assert(squares.get(2).getColumn() == 2);

        squares = _move8.getSquares();
        assert(squares.size() == 4);
        assert(squares.get(1).getFace() == Face.FRONT);
        assert(squares.get(1).getRow() == 8);
        assert(squares.get(1).getColumn() == 2);
    }

    /**
     * Test of toString method, of class Move.
     */
    @Test
    public void testToString() {
        String string = "[BLUE: (TOP 0, 2), (TOP 1, 2), (BACK 0, 6), " +
                "(BACK 1, 6), ]";
        //System.out.println(_move1);
        assert(_move1.toString().equals(string));
    }

    /**
     * Test of equals method, of class Move.
     */
    @Test
    public void testEquals() {
        assert(_move1.equals(_move1));
        assert(!_move1.equals(_move2));
//        System.out.println(_move4.toString());
//        System.out.println(_move10.toString());
//        for (Square s : _move1.getSquares()) {
//            System.out.println(s.getFace().toString() + s.getRow() + s.getColumn());
//        }
        assert(_move1.equals(_move9));
        assert(_move10.equals(_move4));
    }
}