package com.squidtech.nodechan;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.List;

import java.io.IOException;

import java.util.Collections;
import java.util.Comparator;

/**
 * This class handles all incoming NodeChan packet traffic, and routes the
 * incoming data as necessary.
 */
public class IncomingThread extends Thread {
  /** The packet queue to pull packets from **/
  List<byte[]> queue;

  /** The local ChanThread storage **/
  List<ChanThread> threads;

  /** The local list of peers **/
  List<Peer> peers;

  public IncomingThread(List<byte[]> queue, List<ChanThread> threads, List<Peer> peers) {
    this.queue = queue;
    this.threads = threads;
    this.peers = peers;
  }

  public void run() {
    // process incoming packets indefinitely
    while (true) {
      // wait for a packet to come
      while(queue.size() == 0);

      byte[] recv_data = queue.remove(0);

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

      // check to make sure we haven't blocked this user
      // if so, ignore the packet
      if (NodeChan.checkBlocked(incoming)) continue;

      boolean havePeer = false;
      for (Peer p : peers) {
        if (p.equalsAddress(incoming)) {
          p.heard();
          havePeer = true;
          break;
        }
      }

      if (!havePeer && peers.size() < NodeChan.AUTO_ADD_PEER_LIMIT) {
        // new peer, add them to our list
        peers.add(new Peer(incoming.getHostAddress()));
      }

      switch(recv_data[2]) {
        case 'P':
          // decode the post packet
          ChanPost post = ChanPost.decodeUDP(recv_data);
          

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
                post = existThread.getPost(i);
                break;
              }
            }

            if (!havePost) {
              // we have the thread, but don't have this post yet, so add it
              existThread.addPost(post);
            }
          } else {
            // we don't have this thread yet, so we will create a new local
            // copy, and also ask the sending peer for the rest of the thread
            ChanThread tempThread = new ChanThread(post.getTid());
            tempThread.addPost(post);
            tempThread.setTitle(post.getTitle());
            threads.add(tempThread);

            // request the complete thread from the client that just
            // sent us this post
            NodeChan.requestThread(tempThread.getTid(), incoming);
          }

          // forward this packet to all peers (except the peer we received the
          // packet from)
          //
          // limit the number of times this client propagates a single post
          // based on NodeChan.MAX_PROPS
          if (post.getReceiptCount() < NodeChan.MAX_PROPS) {
            for (Peer p : peers) {
              if (!p.equalsAddress(post.getSender_addr())) {
                new OutgoingThread(p.getAddress(), NodeChan.NC_PORT, recv_data);
              }
            }
          }

          post.received();

          // sort our thread list by most recent activity first
          Collections.sort(threads, new Comparator<ChanThread>() {
            @Override
            public int compare(ChanThread thread1, ChanThread thread2) {
              return thread1.compareTo(thread2);
            }
          });

          break;
        case 'H':
          // do nothing, the hello-packet is just for adding new peers
          break;
        case 'R':
          // send a copy of the specified thread to the user we received
          // the thread-request from
          String tid = "";
          ChanThread reqThread = null;

          for (int i = 0; i < 8; i++) {
            tid += ((char) recv_data[i + 8]);
          }

          // find the requested thread in this client's thread list
          for (int i = 0; i < threads.size(); i++) {
            if (threads.get(i).getTid().equals(tid)) {
              reqThread = threads.get(i);
              break;
            }
          }

          if (reqThread == null) continue;

          new RequestedThreadSender(reqThread, incoming).start();
          break;
      }

      // check for peers that have timed out
      NodeChan.checkPeerTimeouts();

      // update the GUI when we receive packets, if GUI mode and auto-refresh
      // are both enabled
      if (!NodeChan.nogui && NodeChan.autorefresh) {
        NodeChan.mainGui.refreshThreads();
      }
    }
  }
}
