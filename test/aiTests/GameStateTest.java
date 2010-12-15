package aiTests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import ai.*;
import blokus.*;
import static blokus.Shape.*;
import static blokus.Face.*;
import static blokus.Color.*;
import java.util.*;

/**
 *
 * @author dkimmel
 */
public class GameStateTest {

    private GameState state;
    private GameState stateForEvaluate;

    public GameStateTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        state = new GameState();
        state = state.applyMove(new Move(new Square(FRONT, 2, 1), new Piece(cross), BLUE));
        state = state.applyMove(new Move(new Square(BACK , 2, 1), new Piece(cross), YELLOW));
        state = state.applyMove(new Move(new Square(RIGHT, 2, 1), new Piece(cross), RED));
        state = state.applyMove(new Move(new Square(LEFT , 2, 1), new Piece(cross), GREEN));

        state = state.applyMove(new Move(new Square(FRONT, 2, 4), new Piece(U), BLUE));
        state = state.applyMove(new Move(new Square(BACK , 2, 4), new Piece(U), YELLOW));
        state = state.applyMove(new Move(new Square(RIGHT, 2, 4), new Piece(U), RED));
        state = state.applyMove(new Move(new Square(LEFT , 2, 4), new Piece(U), GREEN));

        state = state.applyMove(new Move(new Square(FRONT, 1, 6), new Piece(F), BLUE));
        state = state.applyMove(new Move(new Square(BACK , 1, 6), new Piece(F), YELLOW));
        state = state.applyMove(new Move(new Square(RIGHT, 1, 6), new Piece(F), RED));
        state = state.applyMove(new Move(new Square(LEFT , 1, 6), new Piece(F), GREEN));

        state = state.applyMove(new Move(new Square(FRONT, 5, 3), new Piece(W), BLUE));
        state = state.applyMove(new Move(new Square(BACK , 5, 3), new Piece(W), YELLOW));
        state = state.applyMove(new Move(new Square(RIGHT, 5, 3), new Piece(W), RED));
        state = state.applyMove(new Move(new Square(LEFT , 5, 3), new Piece(W), GREEN));

        state = state.applyMove(new Move(new Square(FRONT, 4, 8), new Piece(Y), BLUE));
        state = state.applyMove(new Move(new Square(BACK , 4, 8), new Piece(Y), YELLOW));
        state = state.applyMove(new Move(new Square(RIGHT, 4, 8), new Piece(Y), RED));
        state = state.applyMove(new Move(new Square(LEFT , 4, 8), new Piece(Y), GREEN));

        state = state.applyMove(new Move(new Square(FRONT, 7, 1), new Piece(P), BLUE));
        state = state.applyMove(new Move(new Square(BACK , 7, 1), new Piece(P), YELLOW));
        state = state.applyMove(new Move(new Square(RIGHT, 7, 1), new Piece(P), RED));
        state = state.applyMove(new Move(new Square(LEFT , 7, 1), new Piece(P), GREEN));

        state = state.applyMove(new Move(new Square(FRONT, 5, 6), new Piece(fourSquare), BLUE));
        state = state.applyMove(new Move(new Square(BACK , 5, 6), new Piece(fourSquare), YELLOW));
        state = state.applyMove(new Move(new Square(RIGHT, 5, 6), new Piece(fourSquare), RED));
        state = state.applyMove(new Move(new Square(LEFT , 5, 6), new Piece(fourSquare), GREEN));

        state = state.applyMove(new Move(new Square(FRONT, 8, 6), new Piece(zigZag), BLUE));
        state = state.applyMove(new Move(new Square(BACK , 8, 6), new Piece(zigZag), YELLOW));
        state = state.applyMove(new Move(new Square(RIGHT, 8, 6), new Piece(zigZag), RED));
        state = state.applyMove(new Move(new Square(LEFT , 8, 6), new Piece(zigZag), GREEN));

        state = state.applyMove(new Move(new Square(FRONT, 6, 7), new Piece(oneSquare), BLUE));
        state = state.applyMove(new Move(new Square(BACK , 6, 7), new Piece(oneSquare), YELLOW));
        state = state.applyMove(new Move(new Square(RIGHT, 6, 7), new Piece(oneSquare), RED));
        state = state.applyMove(new Move(new Square(LEFT , 6, 7), new Piece(oneSquare), GREEN));

        Piece p1 = new Piece(fiveLine);
        p1.rotateCW();
        state = state.applyMove(new Move(new Square(BOTTOM, 5, 7), new Piece(fiveLine), BLUE));
        state = state.applyMove(new Move(new Square(BOTTOM, 3, 1), new Piece(fiveLine), YELLOW));
        state = state.applyMove(new Move(new Square(BOTTOM, 7, 3), p1, RED));
        state = state.applyMove(new Move(new Square(BOTTOM, 1, 5), p1, GREEN));

        p1 = new Piece(fiveT);
        state = state.applyMove(new Move(new Square(BOTTOM, 3, 5), p1, BLUE));
        p1.flipVertical();
        state = state.applyMove(new Move(new Square(BOTTOM, 5, 3), p1, YELLOW));
        p1.rotateCCW();
        state = state.applyMove(new Move(new Square(BOTTOM, 5, 5), p1, RED));
        p1.flipHorizontal();
        state = state.applyMove(new Move(new Square(BOTTOM, 3, 3), p1, GREEN));

        p1 = new Piece(fiveJ);
        p1.flipVertical();
        state = state.applyMove(new Move(new Square(TOP  , 5, 1), p1, BLUE));
        p1.flipVertical();
        p1.flipHorizontal();
        state = state.applyMove(new Move(new Square(TOP  , 3, 7), p1, YELLOW));
        p1.rotateCW();
        state = state.applyMove(new Move(new Square(TOP  , 7, 5), p1, RED));
        p1.flipHorizontal();
        p1.flipVertical();
        state = state.applyMove(new Move(new Square(TOP  , 1, 3), p1, GREEN));

        p1 = new Piece(fourL);
        p1.flipVertical();

        state = state.applyMove(new Move(new Square(TOP  , 6, 3), p1, BLUE));
        p1.flipVertical();
        p1.flipHorizontal();
        state = state.applyMove(new Move(new Square(TOP  , 2, 5), p1, YELLOW));
        p1.rotateCW();
        state = state.applyMove(new Move(new Square(TOP  , 5, 6), p1, RED));
        p1.flipHorizontal();
        p1.flipVertical();
        state = state.applyMove(new Move(new Square(TOP  , 3, 2), p1, GREEN));

        // now everyone still has the following pieces:
        // domino, smallL, threeLine, T, fiveL, fiveZigZag, Z
        // and the TOP and BOTTOM faces are still totally empty

        stateForEvaluate = new GameState();
        stateForEvaluate = stateForEvaluate.applyMove(
                new Move(new Square(FRONT, 2, 1), new Piece(cross), BLUE));
    }

    @After
    public void tearDown() {
    }

    // TESTS


    @Test
    public void getMovesTest() {
        state.getBoard().display();
        Collection<Move> moves = state.getMoves();
        //System.out.println("Moves: " + moves.size());
        for (Move m : moves) {
            assert( m.getShape() != Shape.domino ||
                    m.getShape() != Shape.smallL ||
                    m.getShape() != Shape.threeLine ||
                    m.getShape() != Shape.T ||
                    m.getShape() != Shape.fiveL ||
                    m.getShape() != Shape.fiveZigZag ||
                    m.getShape() != Shape.Z);
            assert(state.getBoard().isValidBoolean(m));
            System.out.println(m);
        }

        for (int i = 0; i < 21; i++) {
            GameState s = new GameState(i);
            moves = s.getMoves();
            System.out.println("Moves(" + i + "): " + moves.size() + " -> " + moves.size()/6);
            for (Move m : moves) {
                System.out.println(m);
            }
        }
    }

    @Test
    public void evaluateTest() {
        //11 / (sqrt(13)  * 25
        System.out.println("AA " + this.stateForEvaluate.evaluate()[0]);
        assert(this.stateForEvaluate.evaluate()[0] == 76.271276980969);

        stateForEvaluate = stateForEvaluate.applyMove(
                new Move(new Square(TOP , 5, 1), new Piece(fiveLine), BLUE));

        //13 / (sqrt(9.25)  * 100
        assert(this.stateForEvaluate.evaluate()[0] == 427.43736699392895);
    }

    @Test
    public void testCopyConstructor() {
        GameState state1 = new GameState();
        state1 = state1.applyMove(new Move(new Square(FRONT, 2, 1), new Piece(cross), BLUE));
        //System.out.println(state1._averageRow[0]);
        GameState state2 = new GameState(state1);
    }
}
