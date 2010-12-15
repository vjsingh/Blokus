package blokus;

import gui.MainWindow;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ahills, dkimmel, guyoung, vjsingh
 */
public class HumanPlayer extends Player {

    transient MainWindow _gui;

    public HumanPlayer(String name, MainWindow gui) {
        super(name);
        _gui=gui;
    }

    /**
     * Used when loading in a game from file. If a game is loaded in from a
     * file, this method MUST be called to set the gui, since we don't save
     * the gui. Otherwise, should not be used.
     * @param gui GUI to use
     */
    public void setGUI(MainWindow gui) {
        _gui = gui;
    }

    public Move getMove() {

        _gui.getMove();
        //wait until the move is set by the GUI
        while (_gui._getMove)
            try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            return null;
            //Logger.getLogger(HumanPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }

       // _gui.swapOutDisplay(true);
        return _gui._sentMove;
    }
    
}
