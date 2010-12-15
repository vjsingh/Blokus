package network;

import blokus.Color;
import blokus.Face;
import blokus.Move;
import blokus.Piece;
import blokus.Shape;
import blokus.Square;
import java.io.*;
import java.net.*;
import java.util.LinkedList;


/**
 * The BlokusSocket class. This class is a wrapper around a socket that abstracts
 * reading and writing moves, IP addresses, Colors, and names from and to the socket.
 *
 * @author guyoung
 */
public class BlokusSocket{

    private Socket _sock;
    private InputStream _in;
    private OutputStream _out;
    private BufferedReader _reader;
    private PrintWriter _writer;

    /**
     * Constructs a BlokusSocket with a given IP address and port. This creates
     * and connects a socket to the ip address and port passed in, then gets
     * the input and output streams.
     *
     * @param ip - ip address to connect to
     * @param port - port to connect to
     * @throws IOException - if connection fails
     */
    public BlokusSocket(String ip, int port) throws IOException{
        _sock = new Socket(ip, port);
        _sock.setSoTimeout(0);
        _in = _sock.getInputStream();
        _out = _sock.getOutputStream();
        _reader = new BufferedReader(new InputStreamReader(_in));
        _writer = new PrintWriter(_out, true);
    }

    /**
     * Constructs a BlokusSocket from an existing connected socket. This gets
     * the input and output streams from the socket.
     *
     * @param sock - connected socket
     * @throws IOException - if stream getting fails
     */
    public BlokusSocket(Socket sock) throws IOException{
        _sock = sock;
        _sock.setSoTimeout(0);
        _in = _sock.getInputStream();
        _out = _sock.getOutputStream();
        _reader = new BufferedReader(new InputStreamReader(_in));
        _writer = new PrintWriter(_out, true);
    }

    /**
     * This method reads a move from the socket with the following protocol:
     *
     * Pass /n (T/F)
     * Face /n
     * Row /n
     * Col /n
     * Shape /n
     *
     * True /n 1
     * False /n 2
     * ... (25, 5x5)
     *
     * Color /n
     *
     * @return - move constructed from reading the socket
     */
    public Move readMove() throws NetworkException{
        Move move = null;
        try{
            String first = _reader.readLine();
            if(first==null){
                throw new NetworkException("The current player's network has crashed.");
            }
            boolean pass = Boolean.parseBoolean(first);
            if(pass){
                Color color = Color.valueOf(_reader.readLine());
                move = new Move(color);
                return move;
            }
            Face face = Face.valueOf(_reader.readLine());
            int row = Integer.parseInt(_reader.readLine());
            int col = Integer.parseInt(_reader.readLine());
            Square position = new Square(face, row, col);
            Shape shape = Shape.valueOf(_reader.readLine());
            boolean[][] piecesquares = new boolean[5][5];
            for(int i = 0; i< piecesquares.length; i++){
                for(int j = 0; j < piecesquares[i].length; j++){
                    piecesquares[i][j] = Boolean.parseBoolean(_reader.readLine());
                }
            }
            Color color = Color.valueOf(_reader.readLine());
            move = new Move(position, new Piece(shape, piecesquares), 
                    color);
        }
        catch(IOException e){
            throw new NetworkException("The network timed out while reading the move.");
        }
        return move;
    }

    /**
     * This method writes a move to the socket with the following protocol:
     *
     * Pass /n (True/False)
     * Face /n
     * Row /n
     * Col /n
     * Shape /n
     *
     * True /n 1
     * False /n 2
     * ... (25, 5x5)
     * 
     * Color /n
     *
     * @param move - move to be written to the socket
     */
    public void writeMove(Move move){
        boolean pass = move.isPass();
        _writer.println(Boolean.toString(pass));
        if(!pass){
            Square position = move.getPosition();
            _writer.println(position.getFace().toString());
            _writer.println(position.getRow());
            _writer.println(position.getColumn());
            Piece piece = move.getPiece();
            _writer.println(piece.getShape().toString());
            boolean[][] piecesquares = piece.getPiece();
            for(int i = 0; i<piecesquares.length; i++){
                for(int j = 0; j<piecesquares[i].length; j++){
                    _writer.println(Boolean.toString(piecesquares[i][j]));
                }
            }            
            _writer.println(move.getColor().toString());
        }
        else{
            _writer.println(move.getColor().toString());
        }
    }

    /**
     * This method reads an IP address from the socket with the following protocol:
     *
     * IP
     *
     * @return - string representation of IP address
     */
    public String readIP() throws NetworkException{
        String ip = null;
        try{
            ip = _reader.readLine();
        }
        catch(IOException e){
            throw new NetworkException("Unable to read IP address.");
        }
        return ip;
    }

    /**
     * This method writes an IP address to the socket with the following protocol:
     *
     * IP
     *
     * @param ip - string representation of IP address
     */
    public void writeIP(String ip){
        _writer.println(ip);
    }

    /**
     * This method reads a player's color from the socket.
     *
     * @return - player Color
     */
    public Color readColor() throws NetworkException{
        Color color = null;
        try{
            color = Color.valueOf(_reader.readLine());
        }
        catch(IOException e){
            throw new NetworkException("Unable to read player's color.");
        }
        return color;
    }

    /**
     * This method writes a player's Color to the socket.
     *
     * @param color - color of the player
     */
    public void writeColor(Color color){
        _writer.println(color.toString());
    }

    /**
     * This method reads a player's name from the socket.
     *
     * @return - player name
     */
    public String readName() throws NetworkException{
        String name = null;
        try{
            name = _reader.readLine();
        }
        catch(IOException e){
            throw new NetworkException("Unable to read player's name.");
        }
        return name;
    }

    /**
     * This method writes a player's name to the socket.
     *
     * @param name - player's name
     */
    public void writeName(String name){
        _writer.println(name);
    }
    
    public int readInt() throws NetworkException{
        int i = 0;
        try{
            i = Integer.parseInt(_reader.readLine());
        }
        catch(IOException e){
            throw new NetworkException("Unable to read integer.");
        }
        return i;
    }

    public void writeInt(int i){
        _writer.println(i);
    }

    /**
     * This method closes the input/output streams and then closes the socket.
     *
     * @throws IOException - if something fails to close
     */
    public void close() throws IOException{
        _writer.close();
        _reader.close();
        _in.close();
        _out.flush();
        _out.close();
        _sock.close();
    }
}
