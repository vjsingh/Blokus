package network;

import java.io.*;
import java.net.*;

/**************************************************************************
 * 
 * @author guyoung
 *
 *  The ClientHandler class is a Thread which handles all I/O with the
 *  clients. Each client will be associated with a different ClientHandler.
 *  The first line the ClientHandler reads is the client's username.
 *  Any subsequent lines are what the user is typing to the chatroom.
 *  When the client exits, you must call ChatServer.removeClient() to inform
 *  the server that the client is no longer connected.  
 **************************************************************************/
public class ChatClientHandler extends Thread {
    private ChatServer _server;
    private Socket _clientSocket;

    private PrintWriter _out;
    private BufferedReader _in;

    private String _username;

    /***************************************************************************
     * Initialize 'server' and 'clientSocket', and create the input and output
     * streams using the socket's getInputStream() and getOutputStream() functions.
    ****************************************************************************/
    public ChatClientHandler(ChatServer serv, Socket clientSock) throws IOException {
        _server = serv;
        _clientSocket = clientSock;
        _out = new PrintWriter(_clientSocket.getOutputStream(), true);
        _in = new BufferedReader(new InputStreamReader(_clientSocket.getInputStream()));
    }

    /**************************************************************************
     * Send a message to the client.
     * @param message
     **************************************************************************/
    public void send(String message) {
        _out.println(message);
    }

    /**************************************************************************
     * @return the users name
     **************************************************************************/
    public String getUsername() {
        return _username;
    }

    /********************************************************************
     * Sign the client off.
     * Here you have to:
     * - Send a sign off message
     * - Close the input and output streams.
     * - Remove the client from the server.
      *******************************************************************/
    public void signOff() {
        _server.broadcastMessage(_username+" signing off...");
        _out.close();
        try{
            _in.close();
        }
        catch(IOException e){
            System.out.println("Error closing input stream.");
        }
        _server.removeClient(this);
    }

    /***********************************************************************************************
     * Read messages and broadcast them to everybody else.
     *  In a while loop:
     *  - Read a line of input using readLine().
     *  -Use the ChatServer to broadcast it.
     *  -Remember the first message is always the username
     *  Note: If readLine() throws an exception or returns null, this means that either something
     *   went wrong or the client signed off. In either case, you want to sign off and stop the
     *   thread.  
     **********************************************************************************************/  
    public void run() {
        String message;
        try{
            _username = _in.readLine();
            _server.broadcastMessage(_username+" has joined the chat.");
        }
        catch(IOException e){
            this.signOff();
        }
        while(true){
            try{
                message = _in.readLine();
                if(message==null||message.equals("null")||message.equals(null)){
                    this.signOff();
                    return;
                }
                _server.broadcastMessage(_username+": "+message);
            }
            catch(IOException e){
                this.signOff();
            }

        }
    }
}
