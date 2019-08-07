import java.net.DatagramSocket;
import java.net.DatagramPacket;

import java.util.ArrayList;

import java.io.IOException;

/**
 * This class handles all incoming NodeChan packet traffic, and routes the
 * incoming data as necessary.
 */
public class IncomingThread extends Thread {
  /** The socket we're receiving UDP through **/
  DatagramSocket sock;

  /** The local ChanThread storage **/
  ArrayList<ChanThread> threads;

  /** The local list of peers **/
  ArrayList<Peer> peers;

  public IncomingThread(DatagramSocket sock, ArrayList<ChanThread> threads, ArrayList<Peer> peers) {
    this.sock = sock;
    this.threads = threads;
    this.peers = peers;
  }

  public void run() {
    // handle incoming packets indefinitely
    while (true) {
      byte[] recv_data = new byte[326];

      DatagramPacket receivePacket = new DatagramPacket(recv_data, recv_data.length);

      try {
        sock.receive(receivePacket);
      } catch (IOException e) {
        continue;
      }

      // check header
      if (recv_data[0] != 'N' || recv_data[1] != 'C') continue;

      switch(recv_data[2]) {
        case 'P':
          // decode the post packet
          ChanPost post = ChanPost.decodeUDP(recv_data);
          
          // TODO: actually handle posts
          System.out.println(post.getText());
          break;
      }
    }
  }
}
