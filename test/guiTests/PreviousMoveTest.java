/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guiTests;

import gui.PreviousMovePanel;
import blokus.*;

import javax.swing.JFrame;
import java.awt.Dimension;
import gui.*;

/**
 *
 * @author guyoung
 */
public class PreviousMoveTest extends JFrame{

    public PreviousMoveTest(){
        super("Previous Move Test");

        this.setSize(new Dimension(220, 100));
        this.setPreferredSize(new Dimension(220,100));

        this.add(new PreviousMovePanel(new Move(new Square(Face.TOP, 0, 0), new Piece(Shape.fiveT), Color.BLUE),
                new Board(), "Greg", 1, null));

        //this.add(new PreviousMove(new Move(new Square(Face.TOP, 0, 0), new Piece(Shape.fourSquare), Color.BLUE)));


        this.setVisible(true);
        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args){
        new PreviousMoveTest();
    }

}
