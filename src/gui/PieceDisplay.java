package gui;
import blokus.Color;
import blokus.Piece;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author guyoung
 */
public abstract class PieceDisplay extends JComponent {
    List<Piece> _pieces;
    java.awt.Color _color;

    public PieceDisplay(java.awt.Color color, List<Piece> pieces) {
        _pieces = pieces;
        _color = color;
    }

    public abstract void remove(Piece piece);

    /**
     * Used when switching opponentView and PiecesView
     * @return List of pieces the player has
     */
    protected List<Piece> getPieces() {
        return _pieces;
    }

    protected java.awt.Color getColor() {
        return _color;
    }

    public void setColor(Color color){
        switch(color){
            case BLUE: _color = java.awt.Color.blue; break;
            case YELLOW: _color = java.awt.Color.yellow; break;
            case RED: _color = java.awt.Color.red; break;
            case GREEN: _color = java.awt.Color.green; break;
        }

        this.repaint();
    }

}



