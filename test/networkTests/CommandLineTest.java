/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package networkTests;

import blokus.*;
import java.util.LinkedList;
import network.NetworkException;
import network.NetworkHelper;
import network.NetworkThread;

/**
 * This class tests a full game with use of the TestingPlayer, which reads
 * command line input.
 *
 * @author guyoung
 */
public class CommandLineTest {

    public CommandLineTest(String myName, boolean host, String serverip){
        if(host){
            LinkedList<Player> hosts = new LinkedList<Player>();
            hosts.add(new TestingPlayer(myName));
            Game game = new Game();
            try{
                NetworkHelper.populateServer(game, hosts);
                NetworkThread thread = new NetworkThread(game, true, serverip);
                thread.start();
            }
            catch(NetworkException e){
                System.out.println(e.getMessage());
            }
        }
        else{
            TestingPlayer human = new TestingPlayer(myName);
            Game game = new Game();
            try{
                NetworkHelper.populateClient(game, human, serverip);
                NetworkThread thread = new NetworkThread(game, false, serverip);
                thread.start();
            }
            catch(NetworkException e){
                System.out.println(e.getMessage());
            }
        }

    }

    public static void main(String[] args){
        if(args.length!=3){
            System.out.println("Usage: <name> <host> <server ip>");
        }
        else{
            String name = args[0];
            boolean host = Boolean.parseBoolean(args[1]);
            String ip = args[2];
            new CommandLineTest(name, host, ip);
        }
    }

}
