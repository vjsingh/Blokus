package network;

import java.io.*;
import java.util.*;
import java.net.*;

/**********************************************************
 *
 * @author guyoung
 *
 *The entry point of your server back end. Takes in the
 *port # the server will listen on as a command line param.
 *Then listens on that port for connecting clients.
 *
 * THIS SHOULD BE CREATED IN ADDITION TO THE CHATCLIENT ON
 * THE HOST MACHINE ONLY
 **********************************************************/
public class ChatServer extends Thread{
    private int _localport;               //The local port the server will listen on.
    private ServerSocket _serverSocket;   //The socket the server will accept connections on.

    private List<ChatClientHandler> _clients; //The list of currently connected clients.

    private boolean _done;

    /**************************************************************************
     * The constructor must initialize 'serverSocket' and 'clients'.
     * @param localport - the port the server will listen on
     * @throws IOException
     **************************************************************************/
    public ChatServer() throws IOException {
        _localport = NetworkHelper.CHATPORT;
        _serverSocket = new ServerSocket(_localport);
        _clients = new LinkedList<ChatClientHandler>();
        _done = false;
    }

    /***************************************************************************************
     * Accept connections and add them to the clients queue.
     * This function should, in a while loop:
     * - Accept a connection using ServerSocket.accept()
     * - Create a new ClientHandler, giving it the Socket that accept() returns.
     * - Add the ClientHandler to the clients list. This is so that
     * broadcastMessage() function will be able to access it.
     * - Start the ClientHandler thread so that it can begin reading any incoming messages.
      **************************************************************************************/
    public void run() {
        Socket clientSocket;
        ChatClientHandler handler;
        while(!_done){
            try{
                clientSocket = _serverSocket.accept();
                handler = new ChatClientHandler(this, clientSocket);
                _clients.add(handler);
                handler.start();
            }
            catch(IOException e){
                //System.out.println("Unable to establish client connection.");
            }
        }
    }

    /***************************************************************************
     * Broadcast a message to everybody currently connected to the server by 
     * @param message - message to broadcast
     * looping through the clients list.
     * Warning: More than one ClientHandler can call this function at a time. 
    ***************************************************************************/
    public synchronized void broadcastMessage(String message) {
        for(int i = 0; i<_clients.size(); i++){
            _clients.get(i).send(message);
        }
    }

    /**************************************************************************
     * Remove the given client from the clients list.
     * Warning: More than one ClientHandler can call this function at a time. 
     * Return whether the client was found or not.
     **************************************************************************/
    public synchronized boolean removeClient(ChatClientHandler client) {
        if(_clients.contains(client)){
            _clients.remove(client);
            return true;
        }
        else return false;
    }

    public synchronized void end(){
        _done = true;
        for(int i = 0; i < _clients.size(); i++){
            this.removeClient(_clients.get(i));
        }
        try{
            _serverSocket.close();
        }
        catch(IOException e){
            //server socket failed to close
        }
    }
}
