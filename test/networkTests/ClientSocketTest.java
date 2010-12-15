
package networkTests;

import blokus.*;
import network.*;
import java.io.*;

/**
 * This tests the client side of passing a move over the network. It reads a move
 * from the server, prints its values, and writes the same move back to the server.
 * The printed values should match those in the output of the server.
 * 
 * @author guyoung
 */
public class ClientSocketTest {

    public ClientSocketTest(){
        Move move = null;
        Player player = new TestPlayer("Test");
        player.setColor(Color.GREEN);
        try{
            BlokusSocket sock = new BlokusSocket(NetworkTest.IP, NetworkTest.SERVERPORT);
            //test move
            move = sock.readMove();
            NetworkTest.printMove(move);
            sock.writeMove(move);
            //test name
            String name = sock.readName();
            System.out.println(name+"\n");
            sock.writeName(name);
            //test ip
            String ip = sock.readIP();
            System.out.println(ip+"\n");
            sock.writeIP(ip);
            //test color
            Color color = sock.readColor();
            System.out.println(color);
            sock.writeColor(color);
            sock.close();
        }
        catch(NetworkException e){
            System.out.println(e.getMessage());
        }
        catch(IOException e){
            System.out.println("Error connecting to the socket.");
        }
    }

    public static void main(String[] args){
        new ClientSocketTest();
    }

}
