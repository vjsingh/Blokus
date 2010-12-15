package gui;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.*;
import blokus.*;

/**
 *
 * @author guyoung
 */
public class PreviousMovePanel extends JPanel {

    private MainWindow _gui;
    private Move _move;
    private String _name;
    private boolean _selected;
    private java.awt.Color _c;
    private PreviousMove _pm;
    private int _moveNumber;
    private Board _board;

    public PreviousMovePanel(Move move, Board board, String name, int movenum,
            MainWindow gui){
        super();
        _board=board;
        _move = move;
        _name = name;
        _gui = gui;
        _selected = false;
        _pm = new PreviousMove(_move);
        _moveNumber = movenum;

        switch(_move.getColor()){
            case BLUE: _c = java.awt.Color.blue; break;
            case YELLOW: _c = java.awt.Color.yellow; break;
            case RED: _c = java.awt.Color.red; break;
            case GREEN: _c = java.awt.Color.green; break;
        }

        this.setLayout(new GridLayout(1, 0));

        JLabel label = new JLabel(name);
        label.setForeground(_c);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.CENTER);
        this.add(label);
        if(!_move.isPass()){
            _pm.setSize(new Dimension(this.getWidth()/2, this.getHeight()));
            _pm.setPreferredSize(new Dimension(this.getWidth()/2, this.getHeight()));
            this.add(_pm);
            _pm.repaint();
        }
        else{
            JLabel pass = new JLabel("Pass");
            pass.setForeground(_c);
            pass.setHorizontalTextPosition(JLabel.CENTER);
            pass.setVerticalTextPosition(JLabel.CENTER);
            pass.setHorizontalAlignment(JLabel.CENTER);
            this.add(pass);
        }

        this.setSize(new Dimension(200, 90));
        this.setPreferredSize(new Dimension(200, 90));
        this.setMaximumSize(new Dimension(200, 90));
        this.setBackground(new java.awt.Color(0,0,0));

        this.setVisible(true);
    }

    public Board getBoard()
    {
        return _board;
    }

    public void setSelected(boolean selected) {
        if(selected){
            _selected = true;
            this.setBackground(java.awt.Color.white);
            _pm.setBackground(java.awt.Color.white);
            _pm.repaint();
            this.repaint();
            //revert to natural board state in mainwindow
        }
        else {
            _selected = false;
            this.setBackground(java.awt.Color.black);
            _pm.setBackground(java.awt.Color.black);
            _pm.repaint();
            this.repaint();
            //revert to this board state in mainwindow
        }
    }

    //for testing
    protected Move getMove() {return _move;}
}
