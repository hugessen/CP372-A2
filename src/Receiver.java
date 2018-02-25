import java.io.*;
import java.net.*;
import java.util.*;

public class Receiver {
  static DatagramSocket socket;

  public static void main(String[] args) throws IOException {
    //        new ServerThread().start();
    InetAddress senderAddress = InetAddress.getLocalHost();
    int senderPort = 5555; //arbitrarily chosen
    int receiverPort = 8888; //arbitrarily chosen
    DatagramSocket receiverSocket = new DatagramSocket(receiverPort);
    DatagramSocket senderSocket = new DatagramSocket();
    System.out.println("Socket initiated");
    
    String file = new String("");
    boolean finished = false;

    while(!finished) {
      byte[] buf = new byte[256];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      receiverSocket.receive(packet);
      System.out.println("Packet Received");
      String str = new String(buf);
      
      if (str.trim().equals("DONE")) {
        finished = true;
      }
      else {
        file += str;
      } 

      packet = new DatagramPacket(buf, buf.length, senderAddress, senderPort);
      senderSocket.send(packet);
      System.out.println("sending ACK to " + senderAddress + ":" + senderPort);
    }
    System.out.println(file);
    
  }
}