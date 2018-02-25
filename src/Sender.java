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

    char[] buf = new char[MAX_BYTES];
    int offset = 0;

    // Keep trying to read MAX_BYTES chars at a time until we hit EOF
    while (in.read(buf,0,MAX_BYTES) != -1) 
    {
      boolean ack = false;
      while (ack == false)
      {
        byte[] bytes = new String(buf).getBytes();
        buf = new char[MAX_BYTES]; //Refresh the buffer
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, receiverAddress, receiverPort);
		System.out.println(bytes + " " + bytes.length + " " + receiverAddress + " " + receiverPort);
        receiverSocket.send(packet);
        //try {
          //socket.setSoTimeout(timeout);
          packet = new DatagramPacket(bytes, bytes.length);
          senderSocket.receive(packet);
          System.out.println("packet recieved");
          ack = true;
        //} 
        /*catch (InterruptedException e) 
        { 

        }*/
      }
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

