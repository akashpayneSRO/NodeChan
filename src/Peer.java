import java.net.InetAddress;

public class Peer {
  /** This peer's address information **/
  private InetAddress addr;

  /** The time this peer was last heard **/
  private long lastHeard;

  public Peer(String ip, long time) {
    this.addr = InetAddress.getByName(ip);
    this.time = System.currentTimeMillis();
  }

  /**
   * We just heard from this peer, update their time
   */
  public void heard() {
    this.time = System.currentTimeMillis();
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
}
