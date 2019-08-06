import java.util.Scanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

import com.dosse.upnp.UPnP;

/**
 *
 * NodeChan is a peer-to-peer distributed anonymous messageboard client.
 * For more information, see README.MD.
 *
 */
public class NodeChan {
  /** The port the application will use to connect. **/
  public static final int PORT = 13370;

  /** The IP address of this NodeChan node. **/
  private static String node_ip;

  /** The IP address of the first peer to connect to. **/
  private static String first_peer_ip;

  public static void main(String[] args) {
    // get the local ip address
    try {
      URL whatis = new URL("http://bot.whatismyipaddress.com");

      BufferedReader sc = new BufferedReader(new InputStreamReader(
        whatis.openStream()));

      node_ip = sc.readLine().trim();
    } catch (Exception e) {
      System.err.println("Failed to retrieve this node's IP, quitting.");
      return;
    }

    System.out.println("Your Node IP is " + node_ip + "\n");

    if (UPnP.isUPnPAvailable()) {
      if (!UPnP.isMappedUDP(PORT)) {
        if (UPnP.openPortUDP(PORT)) {
          // UPnP port mapping successful
          System.out.println("UPnP port mapping enabled.\n");
        } else {
          // UPnP port mapping failed
          System.out.println("UPnP port mapping failed. You will need to " +
                             "manually forward port " + PORT + " to your " +
                             "local IP.\n");
        }
      } else {
        System.out.println("Port " + PORT + " already mapped, continuing.\n");
      }
    } else {
      // client does not have UPnP
      // the user is either not behind a NAT or they will need to manually
      // configure port forwarding on their router
      System.out.println("UPnP not available. You will need to manually " +
                         "forward port " + PORT + " to your local IP.\n");
    }

    System.out.println("Enter peer IP to connect directly,\nleave blank to" +
                       " connect via the peer tracker: ");

    Scanner scan = new Scanner(System.in);
    String input = scan.nextLine();

    if (input.equals("")) {
      // retrieve a peer from the peer tracker
      first_peer_ip = retrieve_peer(node_ip);
    } else {
      // try to connect directly to the user-specified peer
      first_peer_ip = input;
    }

    if (first_peer_ip.equals("nopeer")) {
      System.out.println("No peer available from tracker. Waiting for " +
                         "connections...\n");
    }
  }

  /**
   * Retrieve a peer from the peer tracker.
   *
   * @param me - the IP address of the local node
   */
  public static String retrieve_peer(String me) {
    try {
      URL peer_db = new URL("http://nodechan.000webhostapp.com/nodes/peer" +
                            ".php?ip=" + me);

      BufferedReader sc = new BufferedReader(new InputStreamReader(
        peer_db.openStream()));

      return sc.readLine().trim();
    } catch(Exception e) {
      System.err.println("Failed to connect to peer tracker, quitting.");
      return "ptfail";
    }
  }
}
