package blokusTests;

import gui.MainWindow;
import java.util.LinkedList;

import main.BlokusMain;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static blokus.Shape.*;
import static blokus.Face.*;
import static blokus.Color.*;
import blokus.*;

/**
 *
 * @author vjsingh
 */
public class BlokusMainTest {
    BlokusMain blokus_main;

    public BlokusMainTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        blokus_main = new BlokusMain();
        Game game = blokus_main.getGame();
        Player player1 = new TestingPlayer("1");
        Player player2 = new TestingPlayer("2");
        Player player3 = new TestingPlayer("3");
        Player player4 = new TestingPlayer("4");

        game.addPlayer(player1);
        assert(game.getNumOfPlayers() == 1);

        //check that initial positions are set up correctly
        //_game.getBoard().display();

        game.addPlayer(player2);
        game.addPlayer(player3);
        game.addPlayer(player4);
    }

    @After
    public void tearDown() {
    }
}