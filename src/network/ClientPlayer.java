/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import blokus.Color;
import blokus.Move;
import java.io.*;
import java.net.*;

/**
 * The ClientPlayer class. This class creates a BlokusSocket to connect to the
 * server and then read from that connection. It overrides
 * getMove to read and write moves from the socket. THIS PLAYER IS USED BY THE
 * SERVER, NOT THE CLIENT.
 * 
 * @author guyoung
 */
public class ClientPlayer extends NetworkPlayer{

    private ServerSocket _serv;

    /**
     * Creates a ClientPlayer with the given color, name, IP address, and port.
     *
     * @param name - name of player
     * @param ip - ip address of client
     */
    public ClientPlayer(String name){
        super(name);
    }

    /**
     * This method returns a move by accepting a socket connection and reading from
     * it.
     *
     * @return - move read from socket
     */
    public Move getMove() throws NetworkException{
        //gets move from client by connecting to its ip and port
        Move move = null;
        try{
            BlokusSocket client = new BlokusSocket(_serv.accept());
            move = client.readMove();
            client.close();
        }
        catch(IOException e){
            throw new NetworkException("Failed to get move from networked player.");
        }
        return move;
    }

    /**
     * This allows the server and the client player to share the same server sockets
     * to listen to the same ports.
     *
     * @param serv - server socket corresponding to this client player's color
     */
    public void setServerSocket(ServerSocket serv) throws NetworkException{
        _serv = serv;
    }

}
