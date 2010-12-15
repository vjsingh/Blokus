/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aiTests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import aiTests.*;
import ai.GameTree;

/**
 * Tests out the GameTree class by using SimpleGameState. Specifically, this is
 * good for testing to see that alpha-beta pruning is working correctly and
 * that the logic behind the expansion of trees is correct.
 * @author dkimmel
 */
public class GameTreeTest {
    private GameTree<Integer,SimpleGameState> _tree;

    public GameTreeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        _tree = new
                GameTree<Integer,SimpleGameState>(new SimpleGameState(4), 4, 0);
    }

    @After
    public void tearDown() {
    }

    // TESTS

    @Test
    public void treeTest() {
        for (int i = 0; i < 5; i++) {
            Integer bestChoice = _tree.getBestMove(3);

            assertTrue(bestChoice.intValue() == 4);
            System.out.println(_tree);

            _tree = _tree.get(bestChoice);
        }
        
    }

    @Test
    public void testNormalize() {
        double[] values = new double[3];
        values[0] = 2.0;
        values[1] = 4.0;
        values[2] = 7.0;
        double[] newValues = _tree.normalize(values);
        assert (newValues[0] == (2.0 / 13.0));
        assert (newValues[1] == (4.0 / 13.0));
        assert (newValues[2] == (7.0 / 13.0));

    }
}