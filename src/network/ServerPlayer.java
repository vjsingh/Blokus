/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import blokus.Color;
import blokus.Move;
import java.io.*;

/**
 * The ServerPlayer class. This class defines the abstract getMove class to
 * open a connection with the server using the given IP address and port and
 * reads and reads the move from the server. THIS CLASS IS USED IN THE CLIENT,
 * NOT THE SERVER.
 * 
 * @author guyoung
 */
public class ServerPlayer extends NetworkPlayer{

    private String _serverIP;
    private int _serverPort;

    public ServerPlayer(String name, String serverip){
        super(name);
        _serverIP = serverip;
    }

    /**
     * This method connects to the server, reads a move from the socket, then
     * closes the connection and returns the move.
     *
     * @return - move read from socket
     */
    public Move getMove() throws NetworkException{
        //gets move from server by connecting to server ip, port
        Move move = null;
        try{
            BlokusSocket sock = new BlokusSocket(_serverIP, _serverPort);
            move = sock.readMove();
            sock.close();
        }
        catch(IOException e){
            //throw new NetworkException("Failed to connect to the server.");
        }
        return move;
    }

    /**
     * This is used to set the port the ServerPlayer should connect to based on
     * the color of the Human player on the client's end. This is necessary
     * because each client has its own port to connect to.
     *
     * @param color - color of human player on client's side
     */
    public void setPort(Color color){
        switch(color){
            case RED: _serverPort = NetworkHelper.REDPORT; break;
            case BLUE: _serverPort = NetworkHelper.BLUEPORT; break;
            case GREEN: _serverPort = NetworkHelper.GREENPORT; break;
            case YELLOW: _serverPort = NetworkHelper.YELLOWPORT; break;
        }
    }
}
