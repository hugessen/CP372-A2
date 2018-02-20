import java.io.*;
import java.net.*;
import java.util.*;

public class Receiver {
  static DatagramSocket socket;

  public static void main(String[] args) throws IOException {
    //        new ServerThread().start();
    socket = new DatagramSocket(8888);
    System.out.println("Socket initiated");

    while(true) {
      byte[] buf = new byte[256];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      socket.receive(packet);
      System.out.println("I got a packet!!!!!!!!");
    }
    
  }
}