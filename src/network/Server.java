/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import blokus.Move;
import blokus.Player;
import blokus.Color;
import blokus.Game;
import blokus.MoveMessage;
import java.net.*;
import java.io.*;
import java.util.*;
import gui.MainWindow;
/**
 * The Server class. This class keeps track of four players (human, AI on host
 * machine and client players for networked players). It uses a serversocket to
 * get connected client sockets and read/write to them.
 * 
 * @author guyoung
 */
public class Server implements NetworkComponent{

    private LinkedList<Player> _players;
    private ServerSocket[] _servsocks;
    private Game _game;
    private MainWindow _gui;
    private BlokusSocket[] _clients;

    /**
     * Constructs a Server with the given players:
     * players[0] must be a human player
     * players[1,2,3] can be human, AI, or client players
     *
     * This constructs a different server socket for each remote player and passes
     * it to the corresponding client player. This is based on the remote player's
     * color.
     *
     * @param players - player array specified by GUI and main
     */
    public Server(Game game, MainWindow gui) throws NetworkException{
        _game = game;
        _gui = gui;
        _players = _game.getPlayers();
        _servsocks = new ServerSocket[_players.size()];
        int port = 0;
        _clients = new BlokusSocket[4];
        try{
            for(int i = 0; i<_players.size(); i++){
                if(_players.get(i) instanceof ClientPlayer){
                    Color color = _players.get(i).getColor();
                    switch(color){
                        case RED: port = NetworkHelper.REDPORT; break;
                        case BLUE: port = NetworkHelper.BLUEPORT; break;
                        case GREEN: port = NetworkHelper.GREENPORT; break;
                        case YELLOW: port = NetworkHelper.YELLOWPORT; break;
                    }
                    _servsocks[i] = new ServerSocket(port);
                    ((ClientPlayer) _players.get(i)).setServerSocket(_servsocks[i]);
                }
            }
        }
        catch(IOException e){
            throw new NetworkException("Failed to initialize server.");
        }
    }

    /**
     * This method waits for a move from the player with the given number then
     * sends that same move to all other players.
     * 
     * @param color - the color of the current player
     * @return - move made by the player
     */
    public Move takeTurn(Color color) throws NetworkException{
        Move move = null;
        int j = 0;
        for(int i = 0; i <_players.size(); i++){
            if(_players.get(i).getColor()==color){
                j = i;
            }
        }
        for(int i = 0; i < _players.size(); i++){
            if((_players.get(i) instanceof ClientPlayer)&&(i!=j)){
                try{
                    _clients[i] = new BlokusSocket(_servsocks[i].accept());
                }
                catch(IOException e){
                    throw new NetworkException("Failed to connect to player "+i+".");
                }
            }
        }
        try{
            MoveMessage message;
            do{
                //if there are no available moves for the player, make a pass
                //move and return it
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
                    //display move error message
                    _gui.setStatus(message.getMessage(), color);
                }
            } while (message != MoveMessage.SUCCESS);
            this.sendMove(move, j);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new NetworkException(e.getMessage());
        }
        return move;
    }

    /**
     * This method sends a move to all networked players except for the player
     * that made the move. It does this by accepting sockets with a server socket
     * and then creating BlokusSockets with those.
     *
     * @param move - move to send to all connected players
     * @param playernum - number of player whose turn it is
     */
    private void sendMove(Move move, int playernum) throws NetworkException{
        for(int i = 0; i < _players.size(); i++){
            if((_players.get(i) instanceof ClientPlayer)&&(i!=playernum)){
                try{
                    _clients[i].writeMove(move);
                    _clients[i].close();
                    _clients[i] = null;
                }
                catch(IOException e){
                    throw new NetworkException("Failed to send move to player "+i+".");
                }
            }
        }
    }

    public void close(){
        try{
            for(int i = 0; i < 4; i++){
                if(_servsocks[i]!=null){
                    _servsocks[i].close();
                }
                if(_clients[i]!=null){
                    _clients[i].close();
                }
            }
        }
        catch(IOException e){

        }
    }

}
