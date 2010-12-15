
package networkTests;

import blokus.*;
import network.*;
import java.net.*;
import java.io.*;

/**
 * This class tests the Server's side of passing a move over the network. It creates
 * a move, prints it, writes the move to the client, reads the same move from the
 * client, then prints that copy. If everything works, the moves should be identical.
 * 
 * @author guyoung
 */
public class ServerSocketTest {

    public ServerSocketTest(){
        Player player = new TestPlayer("Test");
        player.setColor(Color.GREEN);
        Move move = new Move(new Square(Face.LEFT, 3, 6), new Piece(Shape.cross), player.getColor());
        NetworkTest.printMove(move);
        try{
            ServerSocket ssock = new ServerSocket(NetworkTest.SERVERPORT);
            Socket client = ssock.accept();
            BlokusSocket sock = new BlokusSocket(client);
            //test move
            sock.writeMove(move);
            Move copy = sock.readMove();
            NetworkTest.printMove(copy);
            //test name
            System.out.println(player.getName());
            sock.writeName(player.getName());
            String name = sock.readName();
            System.out.println(name+"\n");
            //test ip
            System.out.println(NetworkTest.IP);
            sock.writeIP(NetworkTest.IP);
            String ip = sock.readIP();
            System.out.println(ip+"\n");
            //test color
            System.out.println(player.getColor());
            sock.writeColor(player.getColor());
            Color color = sock.readColor();
            System.out.println(color);
            sock.close();
        }
        catch(IOException e){
            System.out.println("Failed to create server socket.");
        }
        catch(NetworkException e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args){
        new ServerSocketTest();
    }

}
