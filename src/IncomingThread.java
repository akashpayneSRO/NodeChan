import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

      // update local peer list with the IP of the sender
      // if we don't already have the sender in the local peer list, we should
      // add them to our list

      byte[] incomingIP = new byte[4];
      for (int i = 0; i < 4; i++) {
        incomingIP[i] = recv_data[i + 4];
      }

      InetAddress incoming = null;

      try {
        incoming = InetAddress.getByAddress(incomingIP);
      } catch (UnknownHostException e) {
        continue;
      }

      boolean havePeer = false;
      for (Peer p : peers) {
        if (p.getAddress().getHostAddress().equals(incoming.getHostAddress())) {
          p.heard();
          havePeer = true;
          break;
        }
      }

      if (!havePeer) {
        // new peer, add them to our list
        peers.add(new Peer(incoming.getHostAddress()));
      }

      switch(recv_data[2]) {
        case 'P':
          // TODO: post propagation/forwarding
          // decode the post packet
          ChanPost post = ChanPost.decodeUDP(recv_data);
          
          if (post.getIsRoot()) {
            // check whether we already have a copy of this OP
            boolean haveOP = false;

            for (ChanThread t : threads) {
              if (t.getTid().equals(post.getTid())) {
                haveOP = true;
                break;
              }
            }

            if (!haveOP) {
              // create a new local thread with this OP
              ChanThread newThread = new ChanThread(post.getTid());
              newThread.addPost(post);
              threads.add(newThread);
            }
          } else {
            // check whether we have this thread
            // if not, ignore this post, since it would be pointless to start
            // in the middle of the conversation
            ChanThread existThread = null;

            for (ChanThread t : threads) {
              if (t.getTid().equals(post.getTid())) {
                existThread = t;
                break;
              }
            }

            if (existThread != null) {
              // check whether we already have this post
              boolean havePost = false;

              for (int i = 0; i < existThread.getNumPosts(); i++) {
                if (existThread.getPost(i).getPid().equals(post.getPid())) {
                  havePost = true;
                  break;
                }
              }

              if (!havePost) {
                // we have the thread, but don't have this post yet, so add it
                existThread.addPost(post);
              }
            } else {
              // ignore the post... for now
            }
          }

          break;
      }
    }
  }
}
