package blokus;

import gui.MainWindow;
import ai.AIPlayer;
import network.*;
import java.util.LinkedList;
import java.io.*;
import javax.swing.SwingUtilities;

/**
 *
 * @author ahills, dkimmel, guyoung, vjsingh
 */
public class BlokusMain {
    private Game _game;
    private ChatClient _chatClient;
    private MainWindow _gui;
    private BlokusMain t;
    private ChatServer _chatServ;
    private NetworkThread _thread;

    public BlokusMain() {
        _game = new Game();
    }

    /**
     * Used when loading a saved game
     * @param game Loaded game
     */
    public BlokusMain(Game game) {
        _game = game;
        _thread = null;
    }
    /**
     * This method should be called in the MainWindow in the chat text field's
     * actionPerformed method. It sends a message through the chatClient to the
     * server and to all other connected chat clients.
     *
     * @param message - message to send through chat
     */
    public void sendMessage(String message){
        if (_chatClient!=null)
        _chatClient.sendMessage(message);
    }

    /**
     * This is called by MainWindow once appropriate information on the game is
     * obtained from the new game window. It populates a new Game with players
     * according to network communications. Each new player who connects takes
     * the next available seat. First player in each set MUST be a human player
     * in order to set up chat (and every connected machine needs 1 human player)
     *
     * @param isHost - boolean, whether or not this is the host machine
     * @param humanPlayers - linkedlist of players (human or AI). client side only has
     *                  1 human player!
     * @param serverip - ip address of server ('localhost' if this is the server)
     * @param gui - MainWindow for displaying messages
     *
     * @return _game - so MainWindow has access to players and board
     */
    public Game takeSeat(boolean isHost, LinkedList<Player> humanPlayers, String serverip, MainWindow gui) {
        //THIS IS FOR TESTING:
        if(isHost){
            try{
//                if (humanPlayers.size()<4){
//                    gui.displayWaiting();
//                }
                NetworkHelper.populateServer(_game, humanPlayers);
            }
            catch(NetworkException e){
                gui.displayError(e.getMessage());
            }
            if (humanPlayers.size()<4){
                //gui.removeWaiting();
            }
        }
        else{
            try{
                //gui.displayWaiting();
                NetworkHelper.populateClient(_game, humanPlayers.getFirst(), serverip);
            }
            catch(NetworkException e){
                gui.displayError(e.getMessage());
            }
            //gui.removeWaiting();
        }
        return _game;
    }

    /**
     * This method starts the chat server/client.
     *
     * @param isHost - whether or not to run the chat server
     * @param human - player who will be chatting
     * @param serverip - ip address of server
     * @param gui - MainWindow for updating chat pane
     */
    public void startChat(boolean isHost, Player human, String serverip, MainWindow gui){
        if(isHost){
            try{
                _chatServ = new ChatServer();
                _chatServ.start();
            }
            catch(IOException e){
                gui.displayError("Failed to initialize chat server.");
            }
        }
        _chatClient = new ChatClient(human.getName(), human.getColor(),
                        serverip, gui);
    }

    public void newGame(){
        _game = new Game();
        try{
            if(_chatClient!=null){
                _chatClient.end();
                _chatClient = null;
            }
            if(_chatServ!=null){
                    _chatServ.end();
                    _chatServ.join();
                    _chatServ = null;
            }
        }
        catch(InterruptedException e){
            //server interrupted
        }
    }

    public void endGame() {
        System.out.println("BlokusMain:endGame()");
        if (_thread != null) {
            System.out.println("thread!=null");
            _thread.interrupt();
            System.out.println("_thread.Interrupt()");
            _thread.endGame();
            System.out.println("_thread.endGame()");
            _thread = null;
        }
    }

    /**
     * Starts the game created and populated by takeSeat. This starts a Network
     * Thread to continuously get moves from players, this works even if the game
     * is not a networked game.
     * 
     * @param isHost - boolean whether or not this is the host machine
     * @param serverip - ip address of server ('localhost' if this is the server)
     * @param gui - MainWindow, for displaying errors
     */
    public void start(boolean isHost, String serverip, MainWindow gui) {

        try{
            _thread = new NetworkThread(_game, isHost, serverip, gui);
            _thread.start();
        }
        catch(NetworkException e){
            gui.displayError(e.getMessage());
        }
    }

    /**
     * Instantiates the GUI and runs the game until it is exited.
     */
    public void startGUI() {
        t = this;
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                _gui=new MainWindow(t);
                _gui.setVisible(true);
            }
        });
        _game = new Game(_gui);
        
    }

    //for testing, runs a command line version of the game
    public void startCommandLine() {
        _game = new Game();
        //_game.addPlayer(new TestingPlayer("auto"));
        for (int i = 0; i < 4; i++) {
            _game.addPlayer(new AIPlayer("AI" + i, 1));
            //_game.addPlayer(new TestingPlayer("auto" + i));
        }
        for (int i = 0; i < 4*22; i++) {
            if (!_game.nextMove()) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        //new BlokusMain().startCommandLine();
        new BlokusMain().startGUI();
    }

    //FOR TESTING ONLY
    protected Game getGame() {
        return _game;
    }
}
