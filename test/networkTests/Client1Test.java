

package networkTests;

import blokus.*;
import network.*;
import java.io.*;
import java.util.*;

/**
 * This class tests the Client on a simple move exchange between four players.
 * One player is a test player representing the human on the client side. All others
 * are server players that connect to the server. The test prints all moves
 * using a helper function in NetworkTest. If all works correctly, the output
 * should exactly match the output of ServerTest and the other ClientTests.
 *
 * @author guyoung
 */
public class Client1Test {

    public Client1Test(){
        TestPlayer testPlayer = new TestPlayer("Client1");
        Game game = new Game();
        try{
            NetworkHelper.populateClient(game, testPlayer, NetworkTest.IP);
            LinkedList<Player> players = game.getPlayers();
            for(int i = 0; i < players.size(); i++){
                System.out.println(players.get(i).getName()+": "+players.get(i).getColor()+"\n");
            }
            testPlayer.setMove();
            NetworkThread thread = new NetworkThread(game, false, NetworkTest.IP);
            thread.start();
            thread.join();
        }
        catch(NetworkException e){
            System.out.println(e.getMessage());
        }
        catch(InterruptedException e){
            System.out.println("Thread interrupted");
        }
    }

    public static void main(String[] args){
        new Client1Test();
    }

}
