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
 * @author ahills, dkimmel, guyoung, vjsingh
 */
public class PieceTest {

    Piece _piece;

    public PieceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        _piece = new Piece(Shape.P);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of rotateCW method, of class Piece.
     */
    @Test
    public void testRotateCW() {
        System.out.println("rotateCW");

        boolean[][] original = new boolean[5][5];

        //initialize all to false
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                original[i][j] = false;
            }
        }

        original[1][2] = true;
        original[2][2] = true;
        original[3][2] = true;
        original [2][3] = true;
        original [3][3] = true;

        boolean[][] originalResult = _piece.getPiece();
        
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                assert(original[i][j] == originalResult[i][j]);
            }
        }
        boolean[][] rotated = new boolean[5][5];

        //initialize all to false
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                rotated[i][j] = false;
            }
        }

        rotated[2][1] = true;
        rotated[2][2] = true;
        rotated[2][3] = true;
        rotated[3][1] = true;
        rotated[3][2] = true;

        _piece.rotateCW();

        boolean[][] rotatedResult = _piece.getPiece();
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                assert(rotated[i][j] == rotatedResult[i][j]);
            }
        }
    }

    /**
     * Test of rotateCCW method, of class Piece.
     */
    @Test
    public void testRotateCCW() {
        boolean[][] rotated = new boolean[5][5];

        //initialize all to false
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                rotated[i][j] = false;
            }
        }

        rotated[2][1] = true;
        rotated[2][2] = true;
        rotated[2][3] = true;
        rotated[1][2] = true;
        rotated[1][3] = true;

        _piece.rotateCCW();

        boolean[][] rotatedResult = _piece.getPiece();
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                assert(rotated[i][j] == rotatedResult[i][j]);
            }
        }
    }

    /**
     * Test of flipVertical method, of class Piece.
     */
    @Test
    public void testFlipVertical() {
        boolean[][] rotated = new boolean[5][5];

        //initialize all to false
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                rotated[i][j] = false;
            }
        }

        rotated[1][2] = true;
        rotated[2][2] = true;
        rotated[3][2] = true;
        rotated[1][3] = true;
        rotated[2][3] = true;

        _piece.flipVertical();

        boolean[][] rotatedResult = _piece.getPiece();
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                assert(rotated[i][j] == rotatedResult[i][j]);
            }
        }
    }

    /**
     * Test of flipHorizontal method, of class Piece.
     */
    @Test
    public void testFlipHorizontal() {
        boolean[][] rotated = new boolean[5][5];

        //initialize all to false
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                rotated[i][j] = false;
            }
        }

        rotated[1][2] = true;
        rotated[2][2] = true;
        rotated[3][2] = true;
        rotated[2][1] = true;
        rotated[3][1] = true;

        _piece.flipHorizontal();

        boolean[][] rotatedResult = _piece.getPiece();
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                assert(rotated[i][j] == rotatedResult[i][j]);
            }
        }
    }

}