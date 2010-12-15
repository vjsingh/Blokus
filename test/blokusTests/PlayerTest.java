package blokusTests;

import java.util.LinkedList;
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
public class PlayerTest {
    private HumanPlayer human_player;
    private Move _move1;

    public PlayerTest() {
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
        Square position1 = new Square(Face.TOP, 4, 4);
        _move1 = new Move(position1, piece1, Color.BLUE);
    }

    @After
    public void tearDown() {
    }
}