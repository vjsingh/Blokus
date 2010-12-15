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
import ai.AIEngine;

/**
 *
 * @author dkimmel
 */
public class AIEngineTest {

    public AIEngineTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TESTS

    @Test
    public void aiEngineTest() {
        AIEngine<Integer,SimpleGameState> engine = new
                AIEngine<Integer,SimpleGameState>(new SimpleGameState(5), 5);

        for (int i = 0; i < 5; i++) {
            Integer bestChoice = engine.getBestMove(i, 3);

            assertTrue(bestChoice.intValue() == 4);

            if (i == 0) {
                engine.registerMove(bestChoice);
            } else {
                engine.registerMove(new Integer((int)(Math.random() * 4 + 1)));
            }
        }
    }
}