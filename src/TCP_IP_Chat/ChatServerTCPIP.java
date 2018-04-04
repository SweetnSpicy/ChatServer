/** 
  * TCP/IP Based Chat Server
   
  * Purpose: This program is the TCP/IP based Server for the TCP/IP Chat Application. The user just simply starts the Server by running the program. To end
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

public class ChatServerTCPIP extends JFrame
{
   //Attributes
   //Collection of PrintWriters, one is added for each client
   Vector<PrintWriter> pwCollection = new Vector<>();
   //Send text
   PrintWriter pw;
   //Read in text
   BufferedReader br;
   
   //Constructor
   public ChatServerTCPIP()
   {
      try
      {
         //Port
         int portNum = 12345;
         //Set up the server socket
         ServerSocket serverSocket = new ServerSocket(portNum);
         //Get the host IP address so we can tell the user which IP address to connect to
         InetAddress IP = InetAddress.getLocalHost();
         
         //GUI
         JPanel panel = new JPanel();
         JTextArea text = new JTextArea(1, 1);
         
         //Set Up
         this.setTitle("TCP/IP Chat Server");
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
         text.setText("Welcome to the TCP/IP Server.\nTo connect to this server, please enter the IP Address in the Client's command line.\nServer IP Address: " + IP.getHostAddress() + "\nWaiting for Clients...");

         //Constantly wait for new Clients
         while(true)
         {
            //Console Message
            System.out.println("TCP/IP Server Is Running...");
            System.out.println("To connect to this server, please enter the IP Address in the Client's command line.");
            System.out.println("Server IP Address: " + IP.getHostAddress());
            System.out.println("Waiting for Clients...");
            //Accept the connection
            Socket mainSocket = serverSocket.accept();
            System.out.println("Client Is Attempting to Connect to the Server...");
            //Make a new thread for this new Client
            ChatThreads chatServer = new ChatThreads(mainSocket);
            //Start the thread
            chatServer.start();  
            System.out.println("Client has successfully connected to the Server. Client Address: " + mainSocket.getInetAddress().toString());
         }
      }
      catch (BindException be)
      {
         System.out.println("Error: Server is already running.");
      }
      catch (IOException ioe)
      {
         System.out.println("Error: File Input/Output Error.");
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
      Socket socket; 
      
      //Constructor
      public ChatThreads(Socket socket)
      {
         this.socket = socket;
      }
      
      //Thread run method
      public void run()
      {
         try
         {  
            //Set up the reader and writer for the Client
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            //Set up the message string
            String msg = null;
            //Add this Client's PrintWriter to the Server's collection of PrintWriters
            pwCollection.add(pw);
            
            while (true)
            {
               //Read in the message from the Client
               msg = br.readLine();
               System.out.println("Received message: " + msg + " from " + socket.getInetAddress().toString());
               System.out.println("Sending: " + msg + " to all connected Clients.");
               
               //For each Client in the collection, send them the received message
               for (PrintWriter client : pwCollection)
               {
                  try
                  {
                     //Send the message
                     client.println(msg);
                     //Flush the message
                     client.flush();
                  }
                  catch (Exception ex)
                  {
                     System.out.println("Error, please try again.");
                     ex.printStackTrace();
                  }
               }
               System.out.println("Message has been sent to all connected Clients.");
            }
         }
         catch (IOException ioe)
         {
            System.out.print("\nClient with IP Address: " + socket.getInetAddress() + " has Disconnected.");
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
      new ChatServerTCPIP();
   }
}