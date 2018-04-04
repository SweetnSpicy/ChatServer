/** 
  * UDP Based Chat Client
   
  * Purpose: This is the Client program that interacts with the UDP Chat Server. When the user starts the program, they will need to supply the
  * UDP Chat Server's IP Address in the command line. This address can be found when the UDP Server is started. The IP Address of the Server
  * will be displayed in both the Server's GUI and in the console. Make sure the Server is properly running and enter the IP Address in the command 
  * line as the first argument in order to successfully run the Client. 
  
  * Course: NSSA.290.01 - Networking Essentials For Dev
  * Professor: Ann Warren
  * Date: May 4th, 2017
  * @author Cauldierre Mckay, Curtis Nelson, Matthew Turczmanovicz
  * @version 1.0 
  */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class ChatGUIUDP extends JFrame implements ActionListener
{
   //Attributes
   //GUI Objects
   //Panels
   private JPanel convoPanel = new JPanel();
   private JPanel msgPanel = new JPanel();
   private JPanel btnPanel = new JPanel();
   //Labels
   private JLabel convoLabel = new JLabel("Conversation");
   private JLabel msgLabel = new JLabel("Message");
   //Text Areas
   private JTextArea convoArea = new JTextArea(10, 1);
   private JTextArea msgArea = new JTextArea(5, 1);
   //Buttons
   private JButton sendBtn = new JButton("Send");
   private JButton disconBtn = new JButton("Disconnect");
   //Networking Objects
   Socket socket;
   DatagramSocket dataSocket;
   OutputStream output;
   InputStream input;
   String IP_ADDRESS;
   
   //Constructor
   public ChatGUIUDP(String ipValue)
   {
      //Frame Settings
      this.setTitle("UDP Chat Client");
      this.setLayout(new BorderLayout());
      this.setSize(400, 400);
      this.setLocationRelativeTo(null);
      this.setVisible(true);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Eventually change this to the method that disconnects
      
      //Panel Settings
      convoPanel.setLayout(new BorderLayout());
      msgPanel.setLayout(new BorderLayout());
      btnPanel.setLayout(new BorderLayout());
      
      //Text Area Settings
      msgArea.setEditable(true);
      convoArea.setLineWrap(true);
      msgArea.setLineWrap(true);
      convoArea.setWrapStyleWord(true);
      msgArea.setWrapStyleWord(true);
      
      //Add components to the GUI
      convoPanel.add(convoLabel, BorderLayout.NORTH);
      convoPanel.add(convoArea, BorderLayout.SOUTH);
      msgPanel.add(msgLabel, BorderLayout.NORTH);
      msgPanel.add(msgArea, BorderLayout.CENTER);
      msgPanel.add(btnPanel, BorderLayout.EAST);
      btnPanel.add(sendBtn, BorderLayout.CENTER);
      this.add(convoPanel, BorderLayout.NORTH);
      this.add(msgPanel, BorderLayout.SOUTH);
      
      try
      {
         //Set IP ADDRESS
         IP_ADDRESS = ipValue;
      
         //Datagram Socket
         dataSocket = new DatagramSocket(12345);
      
         //Set up action listener
         sendBtn.addActionListener(this);
      
         //Separate thread for receiving messages.
         new ReceiveMessage().start();
      }
      catch (Exception ex)
      {
         System.out.println("Error, please try again.");
         ex.printStackTrace();
      }
   }
   
   //Action Performed Listener - Send Button
   public void actionPerformed(ActionEvent ae)
   {
      if (ae.getActionCommand().equals("Send"))
      {
         System.out.println("Message Sent");
         sendMessage();
      }
   }
   
   //Method for sending messages from the text area to the server
   public void sendMessage()
   {
      try
      {
         //Get the bytes of the message
         byte[] messageData = msgArea.getText().getBytes();
         //Turn this message to a packet
         DatagramPacket packet = new DatagramPacket(messageData, messageData.length, InetAddress.getByName(IP_ADDRESS), 12345);
         //Send the packet
         dataSocket.send(packet);
      }
      catch (IOException ioe)
      {
         convoArea.append("Error: Message Cannot Be Sent");
      }
      catch (NullPointerException npe)
      {
         convoArea.append("Error: Host Server Cannot Be Found");
      }
      catch (Exception ex)
      {
         System.out.println("Error, please try again.");
         ex.printStackTrace();
      }
   }
   
   //Inner thread based class to focus on receiving messages
   class ReceiveMessage extends Thread
   {
      //Thread run method
      public void run()
      {  
         try
         {
            while (true)
            {
               //Create a packet 
               DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
               //Receive the message
               dataSocket.receive(packet);
               //Convert this packet into a String, which is the text that was sent to the Server by a Client
               String message = new String(packet.getData());
               //Add the received message to the conversation area
               convoArea.append(message + "\n");
            }
         }
         catch (Exception ex)
         {
            System.out.println("Error, please try again.");
            ex.printStackTrace();
         }
      }
   }
   
   //Main
   public static void main(String[] args)
   {
      //Make sure the user is supplying an IP Address in order to connect to the Server
      if (args.length <= 0)
      {
         System.out.println("Please enter the Server's IP Address in the command line. Thank you.");
         System.exit(0);
      }
      else 
      {
         //Start the Client and pass in the Server's IP Address
         String IP_ADDRESS = args[0];
         new ChatGUIUDP(IP_ADDRESS);
      }
   } 
}