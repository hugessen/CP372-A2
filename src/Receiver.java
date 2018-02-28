import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.net.*;
import java.util.*;

public class Receiver {
  static DatagramSocket socket;
  static final boolean UNRELIABLE = true;

  public static void main(String[] args) throws IOException {
    InetAddress senderAddress = InetAddress.getLocalHost();
    int senderPort = 5555; //arbitrarily chosen
    int receiverPort = 8888; //arbitrarily chosen
    boolean finished = false;
    String file = new String("");
    DatagramSocket receiverSocket = new DatagramSocket(receiverPort);
    DatagramSocket senderSocket = new DatagramSocket();
    System.out.println("Socket initiated");

    while(!finished) {
      byte[] buf = new byte[256];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      receiverSocket.receive(packet);
      System.out.println("Packet Received");

      //Get segment number
      ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOfRange(buf, 0, 2));
      bb.order(ByteOrder.LITTLE_ENDIAN);
      short segNum = bb.getShort();
      
      System.out.println(segNum);
      String str = new String(buf).substring(2); //Data payload
      
      if (!UNRELIABLE || new Random().nextInt(100) % 10 != 0) {
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
      else {
        System.out.println("Packet drop");
      }
    }
    System.out.println(file);
    
  }
}