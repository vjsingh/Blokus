/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package networkTests;

import network.*;
import blokus.*;
import java.util.LinkedList;

/**
 * This class tests the functionality of the name transfer in the NetworkHelper
 * class. It creates three separate clients with different names and attempts to
 * synchronize these names with player colors on the server. The test succeeds if
 * each client and server name has the same color for all clients in the output.
 * 
 * @author guyoung
 */
public class NameClientTest {

    public NameClientTest(){
        new NameClientThread(3).start();
        new NameClientThread(2).start();
        new NameClientThread(1).start();
    }

    public NameClientTest(int n){
        Game game = new Game();
        Player human = new TestPlayer("Client"+n);
        try{
            NetworkHelper.populateClient(game, human, NetworkTest.IP);
        }
        catch(NetworkException e){
            System.out.println(e.getMessage());
        }
        LinkedList<Player> players = game.getPlayers();
        for(int i = 0; i < players.size(); i++){
            System.out.println(players.get(i).getName()+": "+players.get(i).getColor()+"\n");
        }
    }

    public static void main(String[] args){
        new NameClientTest();
    }

    private class NameClientThread extends Thread{

        private int _n;

        public NameClientThread(int n){
            _n = n;
        }

        public void run(){
            new NameClientTest(_n);
        }
    }

}
