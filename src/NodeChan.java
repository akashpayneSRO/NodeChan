import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.InputMismatchException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.dosse.upnp.UPnP;

/**
 *
 * NodeChan is a peer-to-peer distributed anonymous messageboard client.
 * For more information, see README.MD.
 *
 */
public class NodeChan {
  /** The port the application will use to connect. **/
  public static final int NC_PORT = 13370;

  /** Max time to keep a peer alive without hearing from it (seconds)
      Set to 0 to keep peers alive indefinitely **/
  public static final int PEER_TIMEOUT = 300;

  /** Max number of times each client will propagate a single post **/
  public static final int MAX_PROPS = 3;

  /** The maximum number of threads to display on one page **/
  public static final int PAGE_SIZE = 10;



  // command-line options

  /** If true, run in console mode **/
  public static boolean nogui = false;

  /** If true, run the client using LAN IP addresses only 
      (no tracker or peers outside of LAN) **/
  public static boolean local = false;

  /** If true, do not send hello-packets to new peers.
      This will cause new peers to not see this client until a message travels 
      from this client to theirs. **/
  public static boolean nohello = false;

  /** If true, no initial peer will be retrieved from the tracker. **/
  public static boolean noinitpeer = false;




  /** The IP address of this NodeChan node. **/
  private static InetAddress node_ip;

  /** The IP address of the first peer to connect to. **/
  private static String first_peer_ip;

  /** UDP socket to send packets to peers with **/
  private static DatagramSocket nc_socket;

  /** Incoming packet-handling thread **/
  private static IncomingThread nc_incoming;

  /** List of this node's peers **/
  private static ArrayList<Peer> peers;

  /** Local list of ChanThreads this user has received **/
  private static ArrayList<ChanThread> threads;

  /** URL of the peer tracker to use **/
  private static String peerTrackerURL = "http://squid-tech.com/nodes/peer.php?ip=";

  public static void main(String[] args) {
    // parse command line args
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-nogui")) nogui = true;
      else if (args[i].equals("-local")) local = true;
      else if (args[i].equals("-nohello")) nohello = true;
      else if (args[i].equals("-noinitpeer")) noinitpeer = true;
    }

    System.out.println("Welcome to NodeChan.");

    peers = new ArrayList<Peer>();
    threads = new ArrayList<ChanThread>();

    // get the local ip address
    if (!local) {
      try {
        URL whatis = new URL("http://bot.whatismyipaddress.com");

        BufferedReader sc = new BufferedReader(new InputStreamReader(
          whatis.openStream()));

        node_ip = InetAddress.getByName(sc.readLine().trim());
      } catch (Exception e) {
        System.err.println("Failed to retrieve this node's IP, quitting.");
        return;
      }
    } else {
      System.out.println("Running on the local network only (-local)");

      try(final DatagramSocket socket = new DatagramSocket()){
        socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
        node_ip = socket.getLocalAddress();
      } catch (Exception e) {
        System.err.println("Failed to retrieve this node's local IP, quitting.");
        return;
      }
    }

    System.out.println("Your Node IP is " + node_ip.getHostAddress() + "\n");

    if (!local) {
      System.out.println("Attempting to enable UPnP port mapping...");

      if (UPnP.isUPnPAvailable()) {
        if (!UPnP.isMappedUDP(NC_PORT)) {
          if (UPnP.openPortUDP(NC_PORT)) {
            // UPnP port mapping successful
            System.out.println("UPnP port mapping enabled.\n");
          } else {
            // UPnP port mapping failed
            System.out.println("UPnP port mapping failed. You may need to " +
                               "manually forward port " + NC_PORT + " to your " +
                               "local IP.\n");
          }
        } else {
          System.out.println("Port " + NC_PORT + " already mapped, continuing.\n");
        }
      } else {
        // client does not have UPnP
        // the user is either not behind a NAT or they will need to manually
        // configure port forwarding on their router
        System.out.println("UPnP not available. You may need to manually " +
                           "forward port " + NC_PORT + " to your local IP.\n");
      }
    }

    // setup UDP socket
    try {
      nc_socket = new DatagramSocket(NC_PORT);
    } catch (SocketException e) {
      System.err.println("Failed to establish UDP socket, quitting.");
      System.err.println(e.getLocalizedMessage());
      return;
    }
    

    // initialize incoming packet-handling thread
    nc_incoming = new IncomingThread(nc_socket, threads, peers);
    nc_incoming.start();

    // command-line inputs
    Scanner scan = new Scanner(System.in);
    String input;

    if (!local) {
      System.out.println("Enter peer tracker URL, leave blank for default:");

      input = scan.nextLine();

      if (!input.equals("")) {
        peerTrackerURL = input;
      }

      if (!noinitpeer) {
        System.out.println("Retrieving initial peer from tracker...");

        if (getPeerFromTracker(peerTrackerURL)) {
          System.out.println("Initial peer retrieved!");
        } else {
          System.out.println("Could not get peer from tracker.");
        }
      }
    }

    if (nogui) {
      // command-line mode
      while(true) {
        checkPeerTimeouts();

        System.out.print("> ");
        input = scan.nextLine();

        if (input.equals("newthread")) {
          // create a new thread and send it to peers
          String title;
          String text;
          
          System.out.println("\nTHREAD TITLE:");
          title = scan.nextLine();
          System.out.println("\nTHREAD TEXT:");
          text = scan.nextLine();

          createThreadAndSend(title, text);
        } else if (input.equals("numpeers")) {
          System.out.println(peers.size());
        } else if (input.equals("exit")) {
          System.exit(0);
        } else if (input.equals("threadlist")) {
          if (threads.size() == 0) {
            System.out.println("No active threads.");
            continue;
          }

          int page = 1;

          if (threads.size() > PAGE_SIZE) {
            int pages = (threads.size() / PAGE_SIZE) + 1;
            System.out.print("Which page (out of " + pages + ")? ");

            int pageinput = 1;

            try {
              pageinput = scan.nextInt();
              
              // clear newline
              scan.nextLine();
            } catch (InputMismatchException e) {
              System.err.println("Invalid number.");
              continue;
            }

            if (pageinput < 1 || pageinput > pages) {
              System.err.println("Invalid page.");
              continue;
            } else {
              page = pageinput;
            }
          }           

          // Sort the threads based on their last post time (most recent first)
          Collections.sort(threads, new Comparator<ChanThread>() {
            @Override
            public int compare(ChanThread thread1, ChanThread thread2) {
              return thread1.compareTo(thread2);
            }
          });

          System.out.println("Page " + page);
          System.out.println("TID        Subject");
          for (int i = (page - 1) * PAGE_SIZE; 
                   i < (page - 1) * PAGE_SIZE + PAGE_SIZE &&
                   i < threads.size(); 
                   i++) {

            ChanThread l = threads.get(i);

            System.out.println(l.getTid() + " - " + l.getTitle() + "\n");
          }
        } else if (input.equals("readthread")) {
          System.out.print("Which TID? ");
          String readTid = scan.nextLine();
          boolean threadFound = false;

          for (ChanThread t : threads) {
            if (t.getTid().equals(readTid)) {
              threadFound = true;

              // print out the thread
              System.out.println("\n\nThread: " + t.getTid() + " - " + t.getTitle());
              System.out.println("====================");

              for (int i = 0; i < t.getNumPosts(); i++) {
                ChanPost postRead = t.getPost(i);

                if (postRead != null) {
                  System.out.println(postRead.getPid() + "\n");
                  System.out.println(postRead.getText());
                  System.out.println("====================");
                }
              }
            }
          }

          if (!threadFound) {
            System.out.println("Could not find thread by that TID.");
          }
        } else if (input.equals("reply")) {
          System.out.print("To which TID? ");
          String readTid = scan.nextLine();
          ChanThread replyThread = null;

          for (ChanThread t : threads) {
            if (t.getTid().equals(readTid)) {
              replyThread = t;
              break;
            }
          }

          if (replyThread == null) {
            System.out.println("Could not find thread by that TID.");
            continue;
          }

          System.out.println("\nYOUR REPLY:");
          String reply = scan.nextLine();

          createReplyAndSend(replyThread, reply);
        } else if (input.equals("addpeer")) {
          System.out.print("Enter peer address: ");
          String readIP = scan.nextLine();

          Peer newPeer = new Peer(readIP);

          if (!newPeer.isResolved()) {
            System.err.println("Could not add that address as a peer.");
          } else {
            peers.add(newPeer);
            sendHelloPacket(newPeer);

            System.out.println("\nPeer " + readIP + " added.");
          }
        } else if (input.equals("getpeer")) {
          if (local) {
            System.out.println("Cannot add external peers in LAN mode.");
            continue;
          }

          System.out.println("Attempting to retrieve peer from tracker...\n");

          if (getPeerFromTracker(peerTrackerURL)) {
            System.out.println("Successfully added/updated peer.");
          } else {
            System.out.println("Could not get peer from tracker.");
          }
        } else if (input.equals("hello")) {
          // keep ourselves alive in the peer lists of our peers
          if (nohello) {
            System.out.println("Cannot send hello-packets when the -nohello " +
                               "flag is set.");
            continue;
          } else if (peers.size() == 0) {
            System.out.println("No peers to send hello-packets to.");
          } else {
            for (Peer p : peers) {
              sendHelloPacket(p);
            }

            System.out.println("Hello-packet(s) sent.");
          }
        } else if (input.equals("help")) {
          // display a list of commands to the user
          System.out.println("=== NodeChan command list ===");
          System.out.println("To use a command, simply enter the command name and follow the prompts.\n");

          System.out.println("addpeer    - add a new peer by IP address");
          System.out.println("exit       - quit NodeChan");
          System.out.println("getpeer    - request a peer from the peer tracker");
          System.out.println("hello      - send hello-packets to all peers");
          System.out.println("newthread  - create a new thread");
          System.out.println("numpeers   - print the number of peers this client has");
          System.out.println("threadlist - list all threads");
          System.out.println("readthread - read a thread based on its TID");
          System.out.println("reply      - reply to a thread based on its TID");
          
        } else {
          System.out.println("Command not recognized.");
        }
      }
    } else {
      // TODO: implement gui...
      System.out.println("GUI not implemented yet. Run with option -nogui.");
    }
  }

  /**
   * Retrieve a peer from the peer tracker.
   *
   * @param me - the IP address of the local node
   */
  public static Peer retrieve_peer(String me, String trackerURL) {
    try {
      URL peer_db = new URL(trackerURL + me);

      BufferedReader sc = new BufferedReader(new InputStreamReader(
        peer_db.openStream()));

      String result = sc.readLine().trim();

      if (result.equals("nopeer") || result.equals("nodb")) {
        return null;
      } else {
        return new Peer(result);
      }
    } catch(Exception e) {
      return null;
    }
  }

  /**
   * Check the list of peers and remove all that have timed out.
   */
  public static void checkPeers() {
    for (int i = 0; i < peers.size(); i++) {
      if (System.currentTimeMillis() - peers.get(i).getLastHeard() > 
          (PEER_TIMEOUT * 1000)) {
        peers.remove(i);
        i--;
      }
    }
  }

  /**
   * Create a new thread based on title and text, and send it.
   */
  public static void createThreadAndSend(String title, String text) {
    ChanThread newThread = new ChanThread("");

    ChanPost newPost = new ChanPost(
                             newThread.getTid(),
                             "",
                             node_ip,
                             true,
                             title,
                             text
                       );

    newThread.addPost(newPost);

    // add thread to local thread storage
    threads.add(newThread);

    // translate post to bytes
    byte[] outbytes = ChanPost.encodeUDP(newPost);

    // send post to all peers
    for (Peer p : peers) {
      new OutgoingThread(p.getAddress(), NC_PORT, outbytes).start();
    }

    // report success
    System.out.println("\n\nThread posted. Your TID is " + newThread.getTid());
  }

  /**
   * Create a new reply to a thread and send it.
   */
  public static void createReplyAndSend(ChanThread thread, String reply) {
    ChanPost newPost = new ChanPost(
                             thread.getTid(),
                             "",
                             node_ip,
                             false,
                             thread.getTitle(),
                             reply
                       );

    // add new post to local thread
    thread.addPost(newPost);

    // translate post to bytes
    byte[] outbytes = ChanPost.encodeUDP(newPost);

    // send post to all peers
    for (Peer p : peers) {
      new OutgoingThread(p.getAddress(), NC_PORT, outbytes).start();
    }
  }

  /**
   * Request a peer from the tracker specified by trackerURL.
   * Return false if a peer could not be retrieved, true otherwise.
   */
  public static boolean getPeerFromTracker(String trackerURL) {
    Peer retrieved = retrieve_peer(node_ip.getHostAddress(), trackerURL);

    if (retrieved == null) {
      return false;
    }

    // check if we already have this peer
    // if so, update the peer's time
    for (Peer p : peers) {
      if (p.equalsAddress(retrieved.getAddress())) {
        p.heard();
        return true;
      }
    }

    // add the retrieved peer to our peer list
    peers.add(retrieved);

    // send a hello-packet to the new peer
    sendHelloPacket(retrieved);

    return true;
  }

  /**
   * Send a hello-packet in order to add ourselves to a peer's own peer list,
   * or to keep ourselves alive in their peer list
   */
  public static void sendHelloPacket(Peer p) {
    // do not send hello-packets if the -hohello option is specified
    if (nohello) return;

    byte[] hello = new byte[8];

    // header bytes
    hello[0] = 'N';
    hello[1] = 'C';
    
    // post type
    hello[2] = 'H';

    // flags
    // TODO: request threads from peer
    hello[3] = 0;

    // this node's IP
    byte[] node_addr_bytes = node_ip.getAddress();

    for (int i = 0; i < 4; i++) {
      hello[i + 4] = node_addr_bytes[i];
    }

    // send the hello-packet to the peer
    new OutgoingThread(p.getAddress(), NC_PORT, hello).start();
  }

  /**
   * Check for peers that are over the timeout limit and remove them.
   */
  public static void checkPeerTimeouts() {
    if (PEER_TIMEOUT == 0) return;

    for (int i = 0; i < peers.size(); i++) {
      if (System.currentTimeMillis() - peers.get(i).getLastHeard() > PEER_TIMEOUT * 1000) {
        peers.remove(i);
        
        if (peers.size() > 0) i--;
      }
    }
  }
}
