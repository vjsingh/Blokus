
package networkTests;

import blokus.*;

/**
 * This class holds helpful constants and methods for testing the network portion
 * of Blokus.
 * 
 * @author guyoung
 */
public class NetworkTest {

    public static final int SERVERPORT = 2010;
    public static final int CLIENT1PORT = 3010;
    public static final int CLIENT2PORT = 4010;
    public static final int CLIENT3PORT = 5010;
    public static final String IP = "localhost";

    /**
     * A helper method for printing out all information stored in a move.
     *
     * @param move - move object to be printed
     */
    public static void printMove(Move move){
        boolean pass = move.isPass();
        if(pass){
            System.out.println("pass");
            return;
        }
        Square square = move.getPosition();
        System.out.println(square.getFace().toString());
        System.out.println(square.getRow());
        System.out.println(square.getColumn());
        Piece piece = move.getPiece();
        System.out.println(piece.getShape().toString());
        boolean[][] positions = piece.getPiece();
        for(int i = 0; i < positions.length; i++){
            for(int j = 0; j < positions[i].length; j++){
                System.out.print(positions[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static int[] getClientPorts(){
        int[] ports = new int[3];
        ports[0] = NetworkTest.CLIENT1PORT;
        ports[1] = NetworkTest.CLIENT2PORT;
        ports[2] = NetworkTest.CLIENT3PORT;
        return ports;
    }

}
