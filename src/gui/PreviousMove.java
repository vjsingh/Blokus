/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;
import blokus.*;
import java.awt.event.MouseListener;
import java.awt.Dimension;

/**
 *
 * @author guyoung
 */
public class PreviousMove extends JComponent{

    private Move _move;
    private java.awt.Color _c;

    public PreviousMove(Move move){
        super();
        _move = move;

        switch(_move.getColor()){
            case BLUE: _c = java.awt.Color.blue; break;
            case YELLOW: _c = java.awt.Color.yellow; break;
            case RED: _c = java.awt.Color.red; break;
            case GREEN: _c = java.awt.Color.green; break;
        }

        this.setSize(new Dimension(100, 90));
        this.setPreferredSize(new Dimension(100, 90));
        this.setVisible(true);
        this.setBackground(new java.awt.Color(0, 0, 0));
        this.setForeground(new java.awt.Color(0,0,0));
    }

    @Override
    public void paint(Graphics g){
        Graphics2D brush = (Graphics2D) g;

        int boxWidth = this.getWidth() / 5;
        int boxHeight = this.getHeight() / 5;
        int boxSide = Math.min(boxWidth, boxHeight);
        Position pos = new Position(0, 0);

        boolean[][] p = _move.getPiece().getPiece();
        
        // draw piece block by block
        brush.setColor(_c);
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (p[r][c]) {
                    brush.fillRect((pos.getX()+c)*boxSide, (pos.getY()+r)*boxSide, boxSide, boxSide);
                }
            }
        }

        // draw piece outline
        g.setColor(java.awt.Color.black);
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (p[r][c]) {
                    brush.drawRect((pos.getX()+c)*boxSide, (pos.getY()+r)*boxSide, boxSide, boxSide);
                }
            }
        }
    }

    /**
     * A simple private class for storing the top-left square of each piece.
     */
    private class Position {
        private int _x, _y;
        public Position(int x, int y) {
            _x = x;
            _y = y;
        }
        public int getX() { return _x; }
        public int getY() { return _y; }
    }

}
