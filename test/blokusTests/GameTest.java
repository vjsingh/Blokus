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

import static blokus.Shape.*;
import static blokus.Face.*;
import static blokus.Color.*;

/**
 *
 * @author vjsingh
 */
public class GameTest {
    Game _game;

    public GameTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        _game = new Game();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addHumanPlayer method, of class Game.
     * Also tests initial state of the game (in board and player)
     */
    @Test
    public void testAddHumanPlayer() {
        System.out.println("addHumanPlayer");
        Player player1 = new TestingPlayer("1");
        Player player2 = new TestingPlayer("2");
        Player player3 = new TestingPlayer("3");
        Player player4 = new TestingPlayer("4");
        
        _game.addPlayer(player1);
        System.out.println(_game.getNumOfPlayers());
        assert(_game.getNumOfPlayers() == 1);

        //check that initial positions are set up correctly
        //_game.getBoard().display();

        _game.addPlayer(player2);
        _game.addPlayer(player3);
        _game.addPlayer(player4);

        //_game.getBoard().display();
        
    }

    /**
     * Test of addClientPlayer method, of class Game.
     */
    @Test
    public void testAddClientPlayer() {
        System.out.println("addClientPlayer");
    }

    /**
     * Test of addServerPlayer method, of class Game.
     */
    @Test
    public void testAddServerPlayer() {
        System.out.println("addServerPlayer");
    }

    /**
     * Test of addAIPlayer method, of class Game.
     */
    @Test
    public void testAddAIPlayer() {
        System.out.println("addAIPlayer");
    }

    /**
     * Test of nextMove method, of class Game.
     */
    @Test
    public void testNextMove() {
        System.out.println("nextMove");
    }

    /**
     * Test of saveGame method, of class BlokusMain.
     */
    @Test
    public void testSaveAndLoadGame() {
        System.out.println("save/loadGame");
        BlokusMain main = new BlokusMain();
        Game game = main.getGame();

        //set 3 moves
        Move move1 = new Move(new Square(FRONT, 2, 1), new Piece(cross), BLUE);
        Move move2 = new Move(new Square(BACK , 2, 1), new Piece(cross), YELLOW);
        Move move3 = new Move(new Square(RIGHT, 2, 4), new Piece(U), RED);
        game.nextMove(move1);
        game.nextMove(move2);
        game.nextMove(move3);

        //save, load in a different blokus and assert the moves are the same
        String filePath = game.saveGame();
        BlokusMain main2  = new BlokusMain();
        Game game2 = main2.getGame();

        //assert they are not the same initially
        for (Square s : move1.getSquares()) {
            assert(game.getBoard().getSquare(s) !=
                    game2.getBoard().getSquare(s));
        }

        //load in the game
        game2 = game.loadGame(filePath);

        for (Square s : move1.getSquares()) {
            assert(game.getBoard().getSquare(s) ==
                    game2.getBoard().getSquare(s));
        }
        for (Square s : move2.getSquares()) {
            assert(game.getBoard().getSquare(s) ==
                    game2.getBoard().getSquare(s));
        }
        for (Square s : move3.getSquares()) {
            assert(game.getBoard().getSquare(s) ==
                    game2.getBoard().getSquare(s));
        }
        System.out.println(new Move(new Square(Face.BOTTOM, 2, 0), new Piece(Shape.Z), Color.BLUE));
    }
    

}