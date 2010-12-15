/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guiTests;

import blokus.Color;
import blokus.HumanPlayer;
import gui.MainWindow;

/**
 *
 * @author ahills
 */
public class Tester {

    MainWindow w1;

    public Tester(){
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                w1 = new MainWindow();
                w1.setVisible(true);
                HumanPlayer p1 = new HumanPlayer(Color.BLUE,"Player1");
                System.out.println(p1.getName());
                w1.setPlayer1(p1);
            }
        });
    }

}
