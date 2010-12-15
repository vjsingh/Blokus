/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import blokus.MoveMessage;
import blokus.Move;
import blokus.Player;
import blokus.Color;
import blokus.Game;
import java.io.*;
import java.net.*;
import java.util.*;
import gui.MainWindow;

/**
 * The Client class. This class keeps track of four players, one of which is a
 * HumanPlayer and the other three are ServerPlayers. It uses a server socket to
 * accept incoming connections and write to those connections from the Server.
 * All server players must connect to the SAME port, corresponding to which
 * player is using this machine (not the player number of the server players!)
 *
 * @author guyoung
 */
public class Client implements NetworkComponent{
    
    private LinkedList<Player> _players;
    private int _port;
    private String _serverIP;
    private ServerSocket _servsock;
    private Game _game;
    private MainWindow _gui;

    /**
     * Creates a client with the given players and the server's ip address.
     * players[0] must be a server player (on server's host machine)
     * one player is the human player for this machine
     * all other players are server players
     *
     * The client needs to know which port it belongs to on the Server, this is
     * determined by the color of the non-server player.
     *
     * @param players - array of Players
     */
    public Client(Game game, String serverip, MainWindow gui) throws NetworkException{
        _game = game;
        _gui = gui;
        _players = _game.getPlayers();
        Color color = Color.RED;
        for(int i = 0; i<_players.size(); i++){
            if(!(_players.get(i) instanceof ServerPlayer)){ //only one non-server player
                color = _players.get(i).getColor();
            }
        }
        switch(color){
            case RED: _port = NetworkHelper.REDPORT; break;
            case BLUE: _port = NetworkHelper.BLUEPORT; break;
            case GREEN: _port = NetworkHelper.GREENPORT; break;
            case YELLOW: _port = NetworkHelper.YELLOWPORT; break;
        }
        _serverIP = serverip;
    }

    /**
     * Returns a move made by a specific player. If it is the human player,
     * it gets the move from the player which waits for a response from the GUI.
     * Once it has the response, it sends the move to the server.
     * If it is a server player, it gets the move from the server.
     * 
     * @param playernum
     * @return
     */
    public Move takeTurn(Color color) throws NetworkException{
        Move move = null;
        int j = 0;
        for(int i = 0; i <_players.size(); i++){
            if(_players.get(i).getColor()==color){
                j = i;
            }
        }
        try{
            MoveMessage message;
            do{ 
//                if there are no available moves for the player, make a pass
//                move and return it
                if (_game.hasNoMoves()) {
                    move = new Move(color); //this is a pass move
                }
                else {
                    move = _players.get(j).getMove();
                }
                if (move==null)
                    return null;
                
                message = _game.getBoard().isValidMessage(move);
                if(message != MoveMessage.SUCCESS){
                    //display error message in a window
                    _gui.setStatus(message.getMessage(), color);
                }
            }
            while (message != MoveMessage.SUCCESS);
            if(!(_players.get(j) instanceof ServerPlayer)){
                //human player
                this.sendMove(move);
            }
        }
        catch(Exception e){
            throw new NetworkException(e.getMessage());
        }
        return move;
    }

    /**
     * This move sends a move to the server after it is made by the human
     * player on this client's machine. It accepts a BlokusSocket
     * connection using its server socket then writes a move to it.
     *
     * @param move - move made on client side to be sent to the server
     */
    private void sendMove(Move move) throws NetworkException{
        try{
            BlokusSocket server = new BlokusSocket(_serverIP, _port);
            server.writeMove(move);
            server.close();
        }
        catch(IOException e){
           // throw new NetworkException("Failed to connect to the server.");
        }
    }

    public void close(){
        
    }

}
