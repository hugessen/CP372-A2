import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.net.*;
import java.util.*;
import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;

public class Receiver {
  static DatagramSocket socket;
  static boolean unreliable = false;
  static InetAddress senderAddress;
  static int senderPort = 5555; //arbitrarily chosen
  static int receiverPort = 8888; //arbitrarily chosen
  static String outputFilePath = "output.txt";

  public static void main(String[] args)  
  {
    try 
    {
      senderAddress = InetAddress.getLocalHost();
    }
    catch (UnknownHostException e)
    {
      // doesn't really matter since the user can input it anyways, just used to autofill input field
    }
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
        openGUI();
      }
    });
  }

  static JFrame frame;

  private static void openGUI()
  {
    frame = new JFrame("CP372A2");
    frame.getContentPane().setLayout(null);

    //setBounds(x, y, width, height)
    frame.setBounds(100,100,400,400);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JLabel unreliableLabel = new JLabel("unreliable mode");
    unreliableLabel.setBounds(20, 240, 100, 20);
    JCheckBox unreliableOption = new JCheckBox();
    unreliableOption.setBounds(150, 240, 20, 20);
    frame.getContentPane().add(unreliableLabel);
    frame.getContentPane().add(unreliableOption);

    // Input field for the host
    JLabel hostLabel = new JLabel("Sender host address:");
    hostLabel.setBounds(20, 50, 129, 20);
    JTextField hostTextField = new JTextField();
    hostTextField.setBounds(150, 50, 120, 20);
    hostTextField.setText(senderAddress.getHostAddress());
    frame.getContentPane().add(hostLabel);
    frame.getContentPane().add(hostTextField);

    // Input field for the port
    JLabel senderPortLabel = new JLabel("Sender Port:");
    senderPortLabel.setBounds(20, 100, 100, 20);
    JTextField senderPortTextField = new JTextField();
    senderPortTextField.setBounds(150, 100, 50, 20);
    senderPortTextField.setText(Integer.toString(senderPort));
    frame.getContentPane().add(senderPortLabel);
    frame.getContentPane().add(senderPortTextField);

    // Input field for the port
    JLabel receiverPortLabel = new JLabel("Receiver Port:");
    receiverPortLabel.setBounds(20, 150, 100, 20);
    JTextField receiverPortTextField = new JTextField();
    receiverPortTextField.setBounds(150, 150, 50, 20);
    receiverPortTextField.setText(Integer.toString(receiverPort));
    frame.getContentPane().add(receiverPortLabel);
    frame.getContentPane().add(receiverPortTextField);

    // Input field for the output file
    JLabel outputFileLabel = new JLabel("Output file name:");
    outputFileLabel.setBounds(20, 200, 100, 20);
    JTextField outputFileTextField = new JTextField();
    outputFileTextField.setBounds(150, 200, 200, 20);
    outputFileTextField.setText("output.txt");
    frame.getContentPane().add(outputFileLabel);
    frame.getContentPane().add(outputFileTextField);

    // button to submit request
    JButton submitButton = new JButton();
    submitButton.setText("Submit Request");
    submitButton.setBounds(135, 300, 130, 20);
    frame.getContentPane().add(submitButton);
    submitButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent a)
      {
        if (unreliableOption.isSelected())
        {
          unreliable = true;
        }
        else
        {
          unreliable = false;
        }

        try 
        {
          senderPort = Integer.parseInt(senderPortTextField.getText());
          receiverPort = Integer.parseInt(receiverPortTextField.getText());
        }
        catch (Exception e)
        {
          System.out.println("Error: invalid port number");
          return;
        }
        if (senderPort < 0 || senderPort > 65535 || receiverPort < 0 || receiverPort > 65535)
        {
          System.out.println("Error: invalid port number");
          return;
        }
        else
        {
          outputFilePath = outputFileTextField.getText();
          frame.setVisible(false); //you can't see me!
          frame.dispose(); //Destroy the JFrame object
          openConnection();
        }

      }
    });

    //Display the window.
    frame.setVisible(true);
  }

  private static void openConnection()
  {
    boolean finished = false;
    String file = new String("");
    DatagramSocket receiverSocket;
    DatagramSocket senderSocket;
    int packetDropCounter = new Random().nextInt(100) % 10; // initially drop a random packet between the 1st and 10th packet
    try
    {
      receiverSocket = new DatagramSocket(receiverPort);
      senderSocket = new DatagramSocket();
    }
    catch (SocketException e)
    {
      System.out.println("Error opening sockets, check port numbers");
      return;
    }
    System.out.println("Socket initiated");

    while(!finished) {
      byte[] buf = new byte[128];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      try
      {
        receiverSocket.receive(packet);
        System.out.println("Packet Received");
      }
      catch (IOException e)
      {
        System.out.println("Error receiving packet");
        return;
      }
      
 
      String str = new String(buf); //Data payload
      
      
      if (str.trim().equals("DONE")) 
      {
        finished = true;
      }
      else 
      {
        if (!unreliable || packetDropCounter != 0) 
        {
          file += str.replace("\0", "");
          if (unreliable)
          {
            packetDropCounter -= 1; 
          } 
          packet = new DatagramPacket(buf, buf.length, senderAddress, senderPort);
          try
          {
            senderSocket.send(packet);
            System.out.println("sending ACK to " + senderAddress + ":" + senderPort);
          }
          catch (IOException e)
          {
            System.out.println("Error sending packet");
            return;
          }
        }
        else {
          System.out.println("Packet drop");
          packetDropCounter = 10; // drop every 10th packet
        }
      }
    }
    writeFile(file);
    senderSocket.close();
    receiverSocket.close();
  }

  private static void writeFile(String s)
  {
    BufferedWriter w;

    try 
    {
      File outputFile = new File(outputFilePath);
      w = new BufferedWriter(new FileWriter(outputFile));
      w.write(s);
      w.close();
    } 
    catch (Exception e) 
    {
      System.out.println("Error writing to output file!");
    } 
  }
}