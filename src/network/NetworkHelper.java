
package network;

import blokus.*;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * This class defines which ports to use for connections. Each different player
 * uses a different port to connect to the server. It also has static methods
 * to be used for name transfer at the start of the game.
 * 
 * @author guyoung
 */
public class NetworkHelper {

    //these are used on server and server player
    public static final int REDPORT = 8105;
    public static final int BLUEPORT = 8106;
    public static final int GREENPORT = 8107;
    public static final int YELLOWPORT = 8108;

    //ports to use for chat
    public static final int CHATPORT = 8113;

    //used for name transfer at the beginning of the game
    public static final int NAMEPORT = 8114;

    /**
     * This method fills the game with players based on the names of remotely connected
     * players. This method is for the server and it uses server sockets to accept
     * connections and share information.
     *
     * Protocol:
     *
     * Accept all sockets
     * For each socket:
     * Read name
     * Write number of players
     * Write player number
     * Write player name for each player
     * Close
     * Close server sockets
     * 
     * @param game
     * @param hostPlayers - human and AI players on host machine
     * @throws NetworkException
     */
    public static void populateServer(Game game, LinkedList<Player> hostPlayers) throws NetworkException{
        ServerSocket serv;
        for(int i = 0; i < hostPlayers.size(); i++){
            game.addPlayer(hostPlayers.get(i));
        }
        try{
            serv = new ServerSocket(NAMEPORT);
            BlokusSocket[] socks = new BlokusSocket[Color.values().length-hostPlayers.size()];
            for(int i = 0; i< socks.length; i++){
                socks[i] = new BlokusSocket(serv.accept());
            }
            for(int i = 0; i< socks.length; i++){
                String name = socks[i].readName();
                game.addPlayer(new ClientPlayer(name));
            }
            for(int i = 0; i<socks.length; i++){
                socks[i].writeInt(game.getPlayers().size());
                socks[i].writeInt(hostPlayers.size() + i);
                for(int j = 0; j < game.getPlayers().size(); j++){
                    socks[i].writeName(game.getPlayers().get(j).getName());
                }
                socks[i].close();
            }
            serv.close();
        }
        catch(IOException e){
            throw new NetworkException("Failed to connect to all clients.");
        }
    }

    /**
     * This method creates the players in game based on names read from the server.
     * This is for the client side and it creates a BlokusSocket to first write
     * its own name information then read all name information from the server.
     *
     * Protocol:
     *
     * Write my name
     * Read number of players
     * Read my player number
     * Read each player's name
     * Close
     * 
     * @param game
     * @param human - human player on client side
     * @param serverIP - ip address of server
     * @throws NetworkException
     */
    public static void populateClient(Game game, Player human, String serverIP) throws NetworkException{
        try{
            BlokusSocket sock = new BlokusSocket(serverIP, NAMEPORT);
            sock.writeName(human.getName());
            int numplayers = sock.readInt();
            int mynum = sock.readInt();
            for(int i = 0; i<numplayers; i++){
                String name = sock.readName();
                if(i==mynum){
                    game.addPlayer(human);
                }
                else{
                    game.addPlayer(new ServerPlayer(name, serverIP));
                }
            }
            sock.close();
            for(int i = 0; i < game.getPlayers().size(); i++){
                if(game.getPlayers().get(i) instanceof ServerPlayer){
                    ((ServerPlayer) game.getPlayers().get(i)).setPort(human.getColor());
                }
            }
        }
        catch(IOException e){
            throw new NetworkException("Invalid IP address.");
        }
    }

}
