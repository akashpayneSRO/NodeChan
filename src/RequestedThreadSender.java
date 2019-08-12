package com.squidtech.nodechan;

import java.net.InetAddress;

/**
 * This thread is invoked to separate sending entire groups of posts
 * from the main processing threads
 */
public class RequestedThreadSender extends Thread {
  /** The thread containing the posts we are sending **/
  ChanThread thread;

  /** The recipient of the thread **/
  InetAddress recip;

  public RequestedThreadSender(ChanThread thread, InetAddress recip) {
    this.thread = thread;
    this.recip = recip;
  }

  public void run() {
    // Send all posts in the thread to the recipient
    for (int i = 0; i < thread.getNumPosts(); i++) {
      byte[] out = ChanPost.encodeUDP(thread.getPost(i));

      OutgoingThread outThread = new OutgoingThread(recip, NodeChan.NC_PORT, out).start();

      outThread.start();

      try {
        outThread.join();
      } catch (InterruptedException e) {
        break;
      }
    }
  }
}
