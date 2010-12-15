package network;

import java.io.*;
import java.net.*;
import blokus.Color;
import gui.MainWindow;

/**********************************************************
 *
 * @author guyoung
 *
 *This class handles the clients communication with the server. It receives and
 *sends information over input and output streams continuously.
 * THIS SHOULD BE CREATED ON EACH PLAYER'S MACHINE
 *\*********************************************************/
public class ChatClient {
    private Socket _socket;
    private String _username;
    private ChatClientReadThread _readThread;
    private int _port;
    private String _addrName;
    private MainWindow _gui;
    private Color _color;
    private PrintWriter _writer;

    /*
    * These constants are the port number and host name for your server. For
    * now, the server name is set to localhost. If you wish to connect to
    * another computer, change the name to the name of that computer or IP.
    *
    * NOTE: you may need to change the port # periodically, especially if your
    * exited the application on error and did not close the socket cleanly. The
    * socket may still be in use when you try to run again and this will cause
    * otherwise working code to fail due to a busy port.
    */

    //this needs to take in either the GUI or the chat window so it can update it
    public ChatClient(String username, Color color, String ip, MainWindow gui) {
        //init private i-vars here
        _username = username;
        _port = NetworkHelper.CHATPORT;
        _addrName = ip;
        _gui = gui;
        _color=color;
        _socket = createSocket();

        if (_socket == null) {
            System.out.println("Error creating socket");
            System.exit(0);
        }

        //make printwriter and thread here

        try{
            _writer = new PrintWriter(_socket.getOutputStream(), true);
        }
        catch(IOException e){
            System.out.println("Unable to establish writing connection.");
        }

        _writer.println(_username);

        _readThread = new ChatClientReadThread();
        _readThread.start();
    }

    /******************************************************************
    * This function should use the server port and name information to create a
    * Socket and return it.
    *
    * NOTE:Make sure to catch and report exceptions!!!
    *******************************************************************/
    public Socket createSocket() {
        InetAddress addr = null;
        try{
            addr = InetAddress.getByName(_addrName);
        }
        catch(UnknownHostException e){
            System.out.println("Unknown host.");
        }
        Socket sock = null;

        try{
            sock = new Socket(addr, _port);
        }
        catch(IOException e){
            System.out.println("Unable to create socket.");
        }

        return sock;
    }

    //CALL THIS METHOD ON THE CHATCLIENT WHEN YOU WANT TO SEND TEXT THROUGH THE CHAT
    public void sendMessage(String message){
        _writer.println(message);
    }

    public void end(){
        _readThread.end();
    }


    /************************************************************************ 
     * This thread is used to read from a socket
     * It has access to ChatClient's <socket> variable
     * This blocks!
     ************************************************************************/
    private class ChatClientReadThread extends Thread {
        private BufferedReader _reader;
        private boolean _end;

        /*****************************************************************
         * This should initialize the buffered Reader
         *****************************************************************/
        public ChatClientReadThread() {
            _end = false;
            try{
                _reader = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
            }
            catch(IOException e){
                System.out.println("Unable to read from socket.");
            }
        }

        /***********************************************************************
         * This should keep trying to read from the BufferedReader
         * The message read should be printed out with a simple System.out.println()
         * This method blocks
         * NOTE: If the server dies, reader.readLine() will be returning null!
         ************************************************************************/

        //THIS METHOD NEEDS TO ADD TEXT TO THE CHAT WINDOW
        //probably should pass the window as a parameter, or have a method in GUI
        //to update the chat window and have this reference the GUI
        @Override
        public void run() {
            String message = null;

            while(!_end){
                try{
                    message = _reader.readLine();
                }
                catch(IOException e){
                    System.out.println("Unable to read from socket.");
                }
                if(message!=null&&!message.equals("null")){
                    _gui.appendChat(_color, message+"\n");
                    if((!message.contains(":"))&&message.endsWith("signing off...")){
                        _gui.displayError(message.substring(0, message.lastIndexOf("s"))+"has disconnected. Please start a new game.");
                    }
                }
                else{
                    _gui.displayError("The host has disconnected. Please start a new game.");
                }
            }
        }

        public void end(){
            _end = true;
        }
    }
}
