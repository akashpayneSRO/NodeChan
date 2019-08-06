import java.util.Scanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.InetAddress;

import com.dosse.upnp.UPnP;

/**
 *
 * NodeChan is a peer-to-peer distributed anonymous messageboard client.
 * For more information, see README.MD.
 *
 */
public class NodeChan {
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
      
    } else {
      System.err.println("UPnP not available. You will need to port forward.\n");
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
