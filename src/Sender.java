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
    short offset = 0;
    short segNum = 0;

    // First two bytes are reserved for segment number
    while (in.read(buf,0,MAX_BYTES - 2) != -1) 
    {
      boolean ack = false;
      while (ack == false)
      {
        byte[] bytes = new byte[MAX_BYTES];
        
       //Bitwise operations to turn segNum into byte[2]
        bytes[0] = (byte) (segNum & 0xFF);
        bytes[1] = (byte) ((segNum >> 8) & 0xFF);
        System.out.println(bytes[0]);
        System.out.println(bytes[1]);
        segNum += 1;
        
        //Make sure first two bytes are segment number and rest is data. Java makes things hard.
        byte[] str = new String(buf).getBytes();
        for (int i = 2; i < MAX_BYTES; i++) {
          bytes[i] = str[i - 2];
        }
        
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, receiverAddress, receiverPort);
		System.out.println(bytes + " " + bytes.length + " " + receiverAddress + " " + receiverPort);
        receiverSocket.send(packet);
        packet = new DatagramPacket(bytes, bytes.length);
        senderSocket.receive(packet);
        System.out.println("packet recieved. Segnum:"+segNum);
        ack = true;
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

