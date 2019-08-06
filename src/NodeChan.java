import java.util.Scanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.InetAddress;

/**
 *
 * NodeChan is a peer-to-peer distributed anonymous messageboard.
 * For more information, see README.MD.
 *
 */
public class NodeChan {
  /** The IP address of this NodeChan node. **/
  private static String this_ip;

  /** The IP address of the first peer to connect to. **/
  private static String first_peer_ip;

  public static void main(String[] args) {
    // get the local ip address
    try {
      URL whatis = new URL("http://bot.whatismyipaddress.com");

      BufferedReader sc = new BufferedReader(new InputStreamReader(
        whatis.openStream()));

      this_ip = sc.readLine().trim();
    } catch (Exception e) {
      System.err.println("Failed to retrieve this node's IP, quitting.");
      return;
    }

    System.out.println("Enter peer IP to connect directly, or leave blank to" +
                       " connect via the peer tracker: ");

    Scanner scan = new Scanner(System.in);
    String input = scan.nextLine();

    if (input.equals("")) {
      // the user has opted to retrieve a peer from the database
      first_peer_ip = retrieve_peer(this_ip);

      System.out.println(first_peer_ip);
    } else if (input.equals("debug")) {
      // special debug peer
      first_peer_ip = "debug";
    } else {
      // attempt to connect directly to the peer
      first_peer_ip = input;
    }
  }

  /**
   * Retrieve a peer from the peer database.
   *
   * @param me - the IP address of the local node
   */
  public static String retrieve_peer(String me) {
    try {
      URL peer_db = new URL("http://nodechan.000webhostapp.com/nodes/naccess" +
                            ".php?ip=" + this_ip);

      BufferedReader sc = new BufferedReader(new InputStreamReader(
        peer_db.openStream()));

      return sc.readLine().trim();
    } catch(Exception e) {
      System.err.println("Failed to connect to peer tracker, quitting.");
      return "ptfail";
    }
  }
}
