import java.net.*;
import java.io.*;

public class Sender {

  static InetAddress receiverAddress;
  static int senderPort, receiverPort, timeout;
  static DatagramSocket senderSocket, receiverSocket;
  static BufferedReader in;
  static String inputFilename;
  final static int MAX_BYTES = 124;

  public static void main(String[] args) throws IOException {    
    if (args.length != 5) 
    {
      
      receiverAddress = InetAddress.getLocalHost();
      receiverPort = 8888;
      senderPort = 5555;
      inputFilename = "test.txt";
      timeout = 500;
      System.out.println("No arguments selected, therefore values autoassigned\nReceiver address set to localhost: " + receiverAddress + ",\nReceiver port=8888,\nSender port=5555,\ninput filename=test.txt,\ntimeout=500\n");
      
    }
    else
    {
      try 
      {
        receiverPort = Integer.parseInt(args[1]); 
        senderPort = Integer.parseInt(args[2]);        
        timeout = Integer.parseInt(args[4]);
      }
      catch (Exception e)
      {
        System.out.println("Error: invalid port number or timeout");
        return;
      }
      if (senderPort < 0 || senderPort > 65535 || receiverPort < 0 || receiverPort > 65535 || timeout < 1)
      {
        System.out.println("Error: invalid port number or timeout");
        return;
      }
      try 
      {
        receiverAddress = InetAddress.getByName(args[0]);
      }
      catch (UnknownHostException e)
      {
        System.out.println("Error: invalid receiver host address");
        return;
      }

      inputFilename = args[3];
    }
    
    try {
      in = new BufferedReader(new FileReader(inputFilename));
    } catch (FileNotFoundException e) {
      System.err.println("Error: could not open test file.");
      return;
    }
    
    // receiverAddress = InetAddress.getByName(args[0]);
     // For now

    receiverSocket = new DatagramSocket();
    senderSocket = new DatagramSocket(senderPort);
    senderSocket.setSoTimeout( 1000 );

    char[] buf = new char[MAX_BYTES];

    // First two bytes are reserved for segment number
    while (in.read(buf,0,MAX_BYTES - 2) != -1) 
    {
      byte[] sendData = new String(buf).getBytes();
      
      boolean hasTimeout = true;
      while (hasTimeout) {
        try {
          //Attempt to send packet
          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receiverAddress, receiverPort);
          System.out.println(sendData + " " + sendData.length + " " + receiverAddress + " " + receiverPort);
          receiverSocket.send(sendPacket);
          
          //Receive the ACK
          byte[] receiveData = new byte[MAX_BYTES];
          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
          senderSocket.receive(receivePacket);
          hasTimeout = false; //Can exit loop once ACK has been received.
        } 
        catch (SocketTimeoutException e) {
          System.out.println("RINGG! Time out! Let's send that data again");
        }

      }

      buf = new char[MAX_BYTES]; //Refresh the buffer for the next iteration.
    }
    
    //Send termination message
    byte[] bytes = "DONE".getBytes();
    DatagramPacket packet = new DatagramPacket(bytes, bytes.length, receiverAddress, receiverPort);
    receiverSocket.send(packet);
    
    System.out.println("Done");
    in.close();

    receiverSocket.close();
    senderSocket.close();
  }
}

