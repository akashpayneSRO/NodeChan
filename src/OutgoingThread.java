import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

import java.io.IOException;

/**
 * This class serves to send a single outgoing data packet over UDP.
 */
public class OutgoingThread extends Thread {
  /** The UDP socket we're using **/
  DatagramSocket sock;

  /** The recipient of the packet **/
  InetAddress recip;

  /** The port we're sending over **/
  int port;

  /** The data packet to send **/
  private byte[] outbytes;

  public OutgoingThread(DatagramSocket sock, InetAddress recip, int port, byte[] outbytes) {
    this.sock = sock;
    this.recip = recip;
    this.port = port;
    this.outbytes = outbytes;
  }

  public void run() {
    DatagramPacket outPacket = new DatagramPacket(outbytes, outbytes.length, recip, port);

    try {
      sock.send(outPacket);
    } catch (IOException e) {
      System.err.println(e.getLocalizedMessage());
    }
  }
}
