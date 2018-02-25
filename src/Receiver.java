import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.net.*;
import java.util.*;

public class Receiver {
  static DatagramSocket socket;

  public static void main(String[] args) throws IOException {
    //        new ServerThread().start();
    InetAddress senderAddress = InetAddress.getLocalHost();
    int senderPort = 5555; //arbitrarily chosen
    int receiverPort = 8888; //arbitrarily chosen
    boolean finished = false;
    String file = new String("");
    int highestSegmentSeen = 0;
    DatagramSocket receiverSocket = new DatagramSocket(receiverPort);
    DatagramSocket senderSocket = new DatagramSocket();
    System.out.println("Socket initiated");

    while(!finished) {
      byte[] buf = new byte[256];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      receiverSocket.receive(packet);
      System.out.println("Packet Received");

      ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOfRange(buf, 0, 2));
      bb.order(ByteOrder.LITTLE_ENDIAN);
      short segNum = bb.getShort();
      System.out.println(segNum);
      String str = new String(buf);
      str = str.substring(2);
      
      if (segNum == highestSegmentSeen + 1) {
        if (str.trim().equals("DONE")) {
          finished = true;
        }
        else {
          file += str;
        } 
      }
      else {
        // Do nothing. I think.
      }


      packet = new DatagramPacket(buf, buf.length, senderAddress, senderPort);
      senderSocket.send(packet);
      System.out.println("sending ACK to " + senderAddress + ":" + senderPort);
    }
    System.out.println(file);
    
  }
}