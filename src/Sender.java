import java.net.*;
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

