import java.net.*;
import java.util.*;
import java.io.*;

public class Sender {

  static InetAddress receiverAddress;
  static int senderPort, receiverPort, timeout;
  static DatagramSocket socket;
  static BufferedReader in;
  final static int MAX_BYTES = 124;

  public static void main(String[] richard) throws IOException {
    String[] args = {"Not currently used","8888","5555","test.txt","0"}; //Hardcoding this stuff for now
    System.out.println("At least we got here yo. Little victories");
    
    if (args.length != 5) {
      System.out.println("Error: Wrong number of arguments");
      return;
    }
    
    try {
      in = new BufferedReader(new FileReader("test.txt"));
    } catch (FileNotFoundException e) {
      System.err.println("Could not open test file.");
    }


    // receiverAddress = InetAddress.getByName(args[0]);
    receiverAddress = InetAddress.getLocalHost(); // For now

    senderPort = Integer.valueOf(args[2]); //arbitrarily chosen
    receiverPort = Integer.valueOf(args[1]); //arbitrarily chosen
    timeout = Integer.valueOf(args[4]);

    socket = new DatagramSocket();

    char[] buf = new char[MAX_BYTES/2]; //Not sure if this is how he wants it, but a char is 2 bytes
    int offset = 0;

    // Keep trying to read MAX_BYTES/2 chars at a time until we hit EOF
    while (in.read(buf,0,MAX_BYTES/2) != -1) {
      String str = new String(buf);
      byte[] bytes = str.getBytes(); // Will not always contain the full 124 bytes because smaller chars are encoded with only 1 byte.
      System.out.println("Length of byte buffer: " + bytes.length); //For testing
      DatagramPacket packet = new DatagramPacket(bytes, bytes.length, receiverAddress, receiverPort);
			System.out.println(bytes + " " + bytes.length + " " + receiverAddress + " " + receiverPort);
      socket.send(packet);
      try {
        Thread.sleep(timeout);
      } catch (InterruptedException e) { e.printStackTrace(); }
    }
    System.out.println("Done");
    in.close();

    socket.close();
  }
}

