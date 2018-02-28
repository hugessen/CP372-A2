import java.net.*;
import java.util.*;
import java.io.*;

public class Sender {

  static InetAddress receiverAddress;
  static int senderPort, receiverPort, timeout;
  static DatagramSocket senderSocket, receiverSocket;
  static BufferedReader in;
  final static int MAX_BYTES = 124;

  public static void main(String[] richard) throws IOException {
    String[] args = {"Not currently used","8888","5555","test.txt","500"}; //Hardcoding this stuff for now
    
    if (args.length != 5) {
      System.out.println("Error: Wrong number of arguments");
      return;
    }
    
    try {
      in = new BufferedReader(new FileReader("/Users/richard/eclipse-workspace/CP372-A2/src/test.txt"));
    } catch (FileNotFoundException e) {
      System.err.println("Could not open test file.");
    }
    
    // receiverAddress = InetAddress.getByName(args[0]);
    receiverAddress = InetAddress.getLocalHost(); // For now

    senderPort = Integer.valueOf(args[2]); //arbitrarily chosen
    receiverPort = Integer.valueOf(args[1]); //arbitrarily chosen
    timeout = Integer.valueOf(args[4]);

    receiverSocket = new DatagramSocket();
    senderSocket = new DatagramSocket(senderPort);
    senderSocket.setSoTimeout( 1000 );

    char[] buf = new char[MAX_BYTES];
    short offset = 0;
    short segNum = 0;
    short lastConfirmedSegnum = 0;

    // First two bytes are reserved for segment number
    while (in.read(buf,0,MAX_BYTES - 2) != -1) 
    {
      byte[] sendData = new byte[MAX_BYTES];
      
      prependSegNum(sendData,segNum);
      segNum++;
      
      //Fill buffer starting at index 2
      byte[] str = new String(buf).getBytes();
      for (int i = 2; i < MAX_BYTES; i++) {
        sendData[i] = str[i - 2];
      }
      
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
        } catch (SocketTimeoutException e) {
          System.out.println("RINGG! Time out! Let's send that data again");
        }

      }

      buf = new char[MAX_BYTES]; //Refresh the buffer for the next iteration.
    }
    
    //Send termination message
    byte[] bytes = new byte[6];
    prependSegNum(bytes,segNum);
    byte[] done = "DONE".getBytes();
    for (int i = 2; i < bytes.length; i++)
      bytes[i] = done[i - 2];
    
    DatagramPacket packet = new DatagramPacket(bytes, bytes.length, receiverAddress, receiverPort);
    receiverSocket.send(packet);
    
    System.out.println("Done");
    in.close();

    receiverSocket.close();
    senderSocket.close();
  }
  
  //Bitwise operations to turn segNum into byte[2]
  private static void prependSegNum(byte[] bytes, int segNum) {
    bytes[0] = (byte) (segNum & 0xFF);
    bytes[1] = (byte) ((segNum >> 8) & 0xFF);
  }
}

