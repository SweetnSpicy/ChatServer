/** 
  * UDP Based Chat Server
   
  * Purpose: This program is the UDP based Server for the UDP Chat Application. The user just simply starts the Server by running the program. To end
  * the Server, simply end the program or click the top, right EXIT button in the GUI. The GUI that appears will display the host Server's IP Address.
  * This address is used by the Clients who wish to connect to this Chat Server. This will also display in the console as well.
  
  * Course: NSSA.290.01 - Networking Essentials For Dev
  * Professor: Ann Warren
  * Date: May 4th, 2017
  * @author Cauldierre Mckay, Curtis Nelson, Matthew Turczmanovicz
  * @version 1.0 
  */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServerUDP extends JFrame
{
   //Attribute
   DatagramSocket serverSocket;
   
   //Constructor
   public ChatServerUDP()
   {
      try
      {
         //Set up the server socket
         serverSocket = new DatagramSocket(12345);
         //Get the host IP address so we can tell the user which IP address to connect to
         InetAddress IP = InetAddress.getLocalHost();
                  
         //GUI
         JPanel panel = new JPanel();
         JTextArea text = new JTextArea(1, 1);
         
         //Set Up
         this.setTitle("UDP Chat Server");
         this.setLayout(new BorderLayout());
         this.setSize(400, 200);
         this.setLocationRelativeTo(null);
         this.setVisible(true);
         this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         panel.setLayout(new BorderLayout());
         text.setEditable(false);
         text.setLineWrap(true);
         text.setWrapStyleWord(true);
         panel.add(text, BorderLayout.CENTER);
         this.add(panel, BorderLayout.CENTER);
         
         //Server GUI Message
         text.setText("Welcome to the UDP Server.\nTo connect to this server, please enter the IP Address in the Client's command line.\nServer IP Address: " + IP.getHostAddress() + "\nWaiting for Clients...");
         //Console Message
         System.out.println("UDP Server Is Running...");
         System.out.println("To connect to this server, please enter the IP Address in the Client's command line.");
         System.out.println("Server IP Address: " + IP.getHostAddress());
         System.out.println("Waiting for Clients...");
         //Make a new thread for this new Client
         ChatThreads chatServer = new ChatThreads(serverSocket);
         //Start the thread
         chatServer.start();
         System.out.println("Client has successfully connected to the Server. Client Address: " + serverSocket.getInetAddress().toString());
         while (true) {}
      }
      catch (Exception ex)
      {
         System.out.println("Error, please try again.");
         ex.printStackTrace();
      }
   }
   
   //Inner thread based class
   class ChatThreads extends Thread
   {
      DatagramSocket dataSocket;
      //Collection of Client IP Addresses
      Vector<InetAddress> addressInfo = new Vector<InetAddress>();
      DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
      
      //Constructor
      public ChatThreads(DatagramSocket serverSocket)
      {
         dataSocket = serverSocket;
      }  
      
      //Thread run method
      public void run()
      {
         while (true)
         {
            try
            {
               //Receive the message packet
               serverSocket.receive(this.packet);
               //Convert this packet into a String, which is the text that was sent to the Server by a Client
               String message = new String(packet.getData(), packet.getOffset(), packet.getLength());
               //Get the address of the sender
               InetAddress packetAddress = packet.getAddress();
               //Get the port of the sender
               int packetPort = packet.getPort();
               //Check to see if this a new user
               if(!addressInfo.contains(packetAddress)){
                  addressInfo.add(packetAddress);
                  System.out.println("new client connected!");
               }
               //For each Client on the Server, make a packet of the message received, and send it to the Client
               for (int i = 0; i < addressInfo.size(); i++)
               {       
                  try
                  {
                     //Get the bytes of the message received
                     byte[] messageData = message.getBytes();
                     //Turn this message into a packet
                     DatagramPacket dataPacket = new DatagramPacket(messageData, messageData.length, addressInfo.elementAt(i),12345);
                     //Send the packet
                     dataSocket.send(dataPacket);
                  }
                  catch (Exception ex)
                  {  
                     System.out.println("Error, please try again.");
                     ex.printStackTrace();
                  }
               }
               System.out.println("Message has been sent to all connected Clients.");
            }
            catch (IOException ioe)
            {
               System.out.print(dataSocket.getInetAddress() + " Disconnected");
            } 
            catch(Exception ex)
            {
               System.out.println("Error, please try again.");
            }
         }
      }
   }
   
   //Main
   public static void main(String[] args)
   {
      new ChatServerUDP();
   }
}