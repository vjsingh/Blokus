
package networkTests;

import blokus.*;
import network.*;
import java.util.*;

/**
 * This class tests the functionality of the Server using 1 TestPlayer and
 * 3 ClientPlayers. It goes through four moves of a game and prints out the move
 * values. If all works correctly, these should be identical to the output
 * of all ClientTests.
 * 
 * @author guyoung
 */
public class ServerTest {

    public ServerTest(boolean useThread){
        TestPlayer testPlayer = new TestPlayer("Server");
        try{
            Game game = new Game();
            LinkedList<Player> hosts = new LinkedList<Player>();
            hosts.add(testPlayer);
            NetworkHelper.populateServer(game, hosts);
            LinkedList<Player> players = game.getPlayers();
            for(int i = 0; i < players.size(); i++){
                System.out.println(players.get(i).getName()+": "+players.get(i).getColor()+"\n");
            }
            testPlayer.setMove();
            if(useThread){
                NetworkThread thread = new NetworkThread(game, true, NetworkTest.IP);
                thread.start();
                thread.join();
            }
            else{
                Server server = new Server(game);
                for(int i = 0; i< players.size(); i++){
                    Move next = server.takeTurn(players.get(i).getColor());
                    game.nextMove(next);
                    game.getBoard().display();
                }
            }
        }
        catch(NetworkException e){
            System.out.println(e.getMessage());
        }
        catch(InterruptedException e){
            System.out.println("Thread interrupted");
        }
    }

    public static void main(String[] args){
        new ServerTest(true);
    }

}
