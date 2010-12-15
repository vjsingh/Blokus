/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class SquareTest {
    Square _square1; //border line btwn TOP and BACK
    Square _square2; //usual
    Square _square3; //border line btwn FRONT and LEFT
    Square _square4; //FRONT/LEFT/BOTTOM corner
    Square _square5; //FRONT/RIGHT/BOTTOM corner
    Square _square6; //FRONT/RIGHT/TOP corner
    Square _square7; //FRONT/LEFT/TOP corner
    Square _square8; //BACK/LEFT/BOTTOM corner
    Square _square9; //BACK/RIGHT/BOTTOM corner
    Square _square10; //BACK/LEFT/TOP corner
    Square _square11; //BACK/RIGHT/TOP corner
    LinkedList<Square> _squares;

    public SquareTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        _square1 = new Square(Face.TOP, 0, 5); 
        _square2 = new Square(Face.RIGHT, 4, 4); 
        _square3 = new Square(Face.FRONT, 5, 0);
        _square4 = new Square(Face.BOTTOM, 0, 0);
        _square5 = new Square(Face.BOTTOM, 0, 8);
        _square6 = new Square(Face.TOP, 8, 8);
        _square7 = new Square(Face.TOP, 8, 0);
        _square8 = new Square(Face.BOTTOM, 8, 0);
        _square9 = new Square(Face.BOTTOM, 8, 8);
        _square10 = new Square(Face.TOP, 0, 0);
        _square11= new Square(Face.TOP, 0, 8);

        _squares = new LinkedList<Square>();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetCorners() {
        LinkedList<Square> squares = _square2.getCorners();
        
        //order is top-left, top-right, bottom-left, bottom-right
        assert(squares.get(0).getRow() == 3);
        assert(squares.get(0).getColumn() == 3);
        assert(squares.get(1).getRow() == 3);
        assert(squares.get(1).getColumn() == 5);
        assert(squares.get(2).getRow() == 5);
        assert(squares.get(2).getColumn() == 3);
        assert(squares.get(3).getRow() == 5);
        assert(squares.get(3).getColumn() == 5);

        Square s = new Square(Face.LEFT, 4, 7);
        for (Square sq : s.getCorners()) {
            System.out.println(sq.toString());
        }
        Square s1 = new Square(Face.LEFT, 4, 8);
        for (Square sq : s1.getCorners()) {
            System.out.println(sq.toString());
        }
        Square s2 = new Square(Face.LEFT, 5, 8);
        for (Square sq : s2.getCorners()) {
            System.out.println(sq.toString());
        }
        Square s3 = new Square(Face.FRONT, 5, 0);
        for (Square sq : s3.getCorners()) {
            System.out.println(sq.toString());
        }
        Square s4 = new Square(Face.FRONT, 6, 0);
        for (Square sq : s4.getCorners()) {
            System.out.println(sq.toString());
        }
    }
    /**
     * Tests that from every corner, if you go in 2 different directions that
     * span the other two faces on that corner, you end up in the correct
     * place.
     */
    @Test
    public void testGetRelativeSquare() {
        assert(_square4.getRelativeSquare(-2, -1).getFace() == Face.LEFT);
        assert(_square4.getRelativeSquare(-2, -1).getRow() == 7);
        assert(_square4.getRelativeSquare(-2, -1).getColumn() == 8);

        assert(_square5.getRelativeSquare(-2, 1).getFace() == Face.RIGHT);
        assert(_square5.getRelativeSquare(-2, 1).getRow() == 7);
        assert(_square5.getRelativeSquare(-2, 1).getColumn() == 0);

        assert(_square6.getRelativeSquare(1, 2).getFace() == Face.RIGHT);
        assert(_square6.getRelativeSquare(1, 2).getRow() == 0);
        assert(_square6.getRelativeSquare(1, 2).getColumn() == 1);

        assert(_square7.getRelativeSquare(1, -2).getFace() == Face.LEFT);
        assert(_square7.getRelativeSquare(1, -2).getRow() == 0);
        assert(_square7.getRelativeSquare(1, -2).getColumn() == 7);

        assert(_square8.getRelativeSquare(2, -1).getFace() == Face.LEFT);
        assert(_square8.getRelativeSquare(2, -1).getRow() == 7);
        assert(_square8.getRelativeSquare(2, -1).getColumn() == 0);

        assert(_square9.getRelativeSquare(2, 1).getFace() == Face.RIGHT);
        assert(_square9.getRelativeSquare(2, 1).getRow() == 7);
        assert(_square9.getRelativeSquare(2, 1).getColumn() == 8);

        assert(_square10.getRelativeSquare(-2, -1).getFace() == Face.LEFT);
        assert(_square10.getRelativeSquare(-2, -1).getRow() == 1);
        assert(_square10.getRelativeSquare(-2, -1).getColumn() == 0);

        assert(_square11.getRelativeSquare(-2, 1).getFace() == Face.RIGHT);
        assert(_square11.getRelativeSquare(-2, 1).getRow() == 1);
        assert(_square11.getRelativeSquare(-2, 1).getColumn() == 8);
        
    }
    /**
     * Test of getAdjacentSquares method, of class Square.
     */
    @Test
    public void testGetAdjacentSquares() {
        System.out.println("getAdjacentSquares");

        //return sequence: above below left right
        
        _squares = _square1.getAdjacentSquares();
        assert(_squares.size() == 4);
        assert(_squares.get(0).getFace() == Face.BACK);
        assert(_squares.get(0).getRow() == 0);
        assert(_squares.get(0).getColumn() == 3);
        //below left and right are trivial

        _squares = _square2.getAdjacentSquares();
        assert(_squares.get(0).getFace() == Face.RIGHT);
        assert(_squares.get(0).getRow() == 3);
        assert(_squares.get(0).getColumn() == 4);
        assert(_squares.get(1).getFace() == Face.RIGHT);
        assert(_squares.get(1).getRow() == 5);
        assert(_squares.get(1).getColumn() == 4);
        assert(_squares.get(2).getFace() == Face.RIGHT);
        assert(_squares.get(2).getRow() == 4);
        assert(_squares.get(2).getColumn() == 3);
        assert(_squares.get(3).getFace() == Face.RIGHT);
        assert(_squares.get(3).getRow() == 4);
        assert(_squares.get(3).getColumn() == 5);

        _squares = _square3.getAdjacentSquares();
        assert(_squares.get(2).getFace() == Face.LEFT);
        assert(_squares.get(2).getRow() == 5);
        assert(_squares.get(2).getColumn() == 8);

        _squares = _square4.getAdjacentSquares();
        assert(_squares.get(0).getFace() == Face.FRONT);
        assert(_squares.get(0).getRow() == 8);
        assert(_squares.get(0).getColumn() == 0);
        assert(_squares.get(2).getFace() == Face.LEFT);
        assert(_squares.get(2).getRow() == 8);
        assert(_squares.get(2).getColumn() == 8);

        
    }
}