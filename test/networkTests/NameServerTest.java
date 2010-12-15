/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package networkTests;

import network.*;
import blokus.*;
import java.util.LinkedList;

/**
 *
 * @author guyoung
 */
public class NameServerTest {

    public NameServerTest(){
        Game game = new Game();
        Player host = new TestPlayer("Server");
        LinkedList<Player> hostPlayers = new LinkedList<Player>();
        hostPlayers.add(host);
        try{
            NetworkHelper.populateServer(game, hostPlayers);
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
        new NameServerTest();
    }

}
