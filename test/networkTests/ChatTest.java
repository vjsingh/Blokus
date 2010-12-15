/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package networkTests;

import network.*;
import blokus.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 *
 * @author guyoung
 */
public class ChatTest extends JFrame{

    private ChatClient _client;
    private JTextField _chatDialog;

    public ChatTest(String name, boolean isHost){
        super("ChatTest");
        Dimension size = new Dimension(300, 300);
        this.setSize(size);
        this.setPreferredSize(size);
        this.setVisible(true);

        JPanel panel = new JPanel(new BorderLayout());
        JTextPane chatPane = new JTextPane();
        chatPane.setSize(new Dimension(300, 250));
        chatPane.setPreferredSize(new Dimension(300, 250));
        chatPane.setVisible(true);
        panel.add(chatPane, BorderLayout.CENTER);

        _chatDialog = new JTextField();
        _chatDialog.setSize(new Dimension(300, 50));
        _chatDialog.setPreferredSize(new Dimension(300, 50));
        _chatDialog.addActionListener(new ChatListener());
        _chatDialog.setVisible(true);
        panel.add(_chatDialog, BorderLayout.SOUTH);
        panel.setVisible(true);

        this.add(panel);
        this.pack();

        Player human = new TestPlayer(name);
        
        if(isHost){
            try{
                ChatServer serverThread = new ChatServer();
                serverThread.start();
            }
            catch(IOException e){
                System.out.println(e.getMessage());
            }
        }

        //_client = new ChatClient(human.getName(), NetworkTest.IP, chatPane);

    }

    private class ChatListener implements ActionListener{

        public void actionPerformed(ActionEvent e){
            _client.sendMessage(_chatDialog.getText());
            _chatDialog.setText(null);
        }

    }

}
