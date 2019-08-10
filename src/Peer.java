package com.squidtech.nodechan;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * This class represents a single peer to which the client may send and receive
 * posts and other data.
 *
 */
public class Peer {
  /** This peer's address information **/
  private InetAddress addr;

  /** The time this peer was last heard **/
  private long lastHeard;

  /** Whether or not we have resolved this peer's address **/
  private boolean resolved;

  public Peer(String ip) {
    this.lastHeard = System.currentTimeMillis();

    try {
      this.addr = InetAddress.getByName(ip);

      this.resolved = true;
    } catch (UnknownHostException e) {
      this.resolved = false;
    }
  }

  /**
   * We just heard from this peer, update their time
   */
  public void heard() {
    this.lastHeard = System.currentTimeMillis();
  }

  /**
   * Return whether or not the address of this peer equals another address
   */
  public boolean equalsAddress(InetAddress other) {
    return this.addr.getHostAddress().equals(other.getHostAddress());
  }

  /*
   * getters
   */
  public InetAddress getAddress() {
    return addr;
  }

  public long getLastHeard() {
    return this.lastHeard;
  }

  public boolean isResolved() {
    return this.resolved;
  }
}
