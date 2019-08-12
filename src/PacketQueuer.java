package com.squidtech.nodechan;

import java.util.List;

import java.net.DatagramSocket;
import java.net.DatagramPacket;

import java.io.IOException;

/**
 * This class acts as a queue for incoming packets, so the IncomingThread class can process
 * a packet without interfering with the receipt of any further packets
 */
public class PacketQueuer extends Thread {
  /** The queue to add incoming packets to **/
  List<byte[]> queue;

  /** The socket to listen on **/
  DatagramSocket socket;

  public PacketQueuer(List<byte[]> queue, DatagramSocket socket) {
    this.queue = queue;
    this.socket = socket;
  }

  public void run() {
    while(true) {
      byte[] recv_data = new byte[338];

      DatagramPacket receivePacket = new DatagramPacket(recv_data, recv_data.length);

      try {
        socket.receive(receivePacket);
      } catch (IOException e) {
        continue;
      }

      // add the packet to the queue
      queue.add(recv_data);
    }
  }
}
