

package network;

import blokus.*;
import gui.MainWindow;
import ai.*;

/**
 *
 * @author guyoung
 */
public class NetworkThread extends Thread{

    private NetworkComponent _network;
    private boolean _done;
    private Game _game;
    private MainWindow _gui;


    public NetworkThread(Game game, boolean isHost, String serverip, MainWindow gui) throws NetworkException{
        _game = game;
        _gui = gui;
        _done = false;
        
        if(isHost){
            _network = new Server(_game, _gui);
        }
        else{
            _network = new Client(_game, serverip, _gui);
        }
    }

    @Override
    public synchronized void run(){
        while(!_done){
            try{
                //assumes only valid moves are sent across the network
                Move move = _network.takeTurn(_game.getCurrentTurn());
                if (move==null) //new game was made in the middle of a game
                {
                    this.endGame();
                    return;
                }

                //update GUI/Main here

                //Don't do anything with the move if this thread is interrupted
                //This will happen when the user creates a new game while in
                //a game
                if (!NetworkThread.interrupted()) {
                boolean stillGoing = _game.nextMove(move);
                
                
                    _gui.drawMove(move);
                    _gui.updateScores();

                    if(!stillGoing) {
                        _done = true;
                        _gui.endGame();
                    }
                }
                else {
                    endGame();
                    return;
                }

            }
            catch(NetworkException e) {
                //do something with e's message; i.e. send it to the GUI to display
                _gui.displayError(e.getMessage());
            }
        }
    }

    public void endGame(){
        System.out.println("NetworkThread:endGame()");
        _done = true;
        _network.close();
    }
}
