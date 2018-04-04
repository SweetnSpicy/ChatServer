/** 
  * TCP/IP Based Chat Client
   
  * Purpose: This is the Client program that interacts with the TCP/IP Chat Server. When the user starts the program, they will need to supply the
  * TCP/IP Chat Server's IP Address in the command line. This address can be found when the TCP/IP Server is started. The IP Address of the Server
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

public class ChatGUITCPIP extends JFrame implements ActionListener
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
   //Networking Objects
   Socket socket;
   BufferedReader br;
   PrintWriter pw;
   
   //Constructor
   public ChatGUITCPIP(String IP_ADDRESS)
   {
      //Frame Settings
      this.setTitle("TCP/IP Chat Client");
      this.setLayout(new BorderLayout());
      this.setSize(400, 400);
      this.setLocationRelativeTo(null);
      this.setVisible(true);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
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
         //Set up the socket
         socket = new Socket(IP_ADDRESS, 12345);
         System.out.println("Client is attempting to contact Server " + IP_ADDRESS + " with TCP/IP Connection from Client TCP/IP address: " + InetAddress.getLocalHost().getHostAddress().toString());
         //Set up the reader and writer
         pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
         br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         //Separate thread for receiving messages
         new ReceiveMessage().start();
         //Set up the send button
         sendBtn.addActionListener(this);
         System.out.println("Client has successfully connected to the Server.");
      }  
      catch (UnknownHostException uhe)
      {
         System.out.println("Error: Host Server Cannot Be Found.");
      }
      catch (IOException ioe)
      {
         System.out.println("Error: Messages From Host Server Cannot Be Retrieved or Read.");
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
         System.out.println("Client: " + InetAddress.getLocalHost().getHostAddress().toString() + " is sending message: " + msgArea.getText());
         //Get the text from the text area and send it to the server
         pw.println(msgArea.getText());
         pw.flush();
         
         //Clear the text area
         msgArea.setText("");
      }
      catch (Exception ex)
      {
         System.out.println("Error, please try again.");
         ex.printStackTrace();
      }
   }
   
   //Inner thread based class to focus on the receiving messages
   class ReceiveMessage extends Thread
   {
      //Thread run method
      public void run()
      {
         //Received message
         String message;
         
         try
         {
            //Read in the message
            while ((message = br.readLine()) != null)
            {
               System.out.println("Client: " + InetAddress.getLocalHost().getHostAddress().toString() + " is receiving message: " + message);
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
         new ChatGUITCPIP(IP_ADDRESS);
      }
   } 
}