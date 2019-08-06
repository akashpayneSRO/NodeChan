import java.util.Random;

/**
 *
 * This class represents a single post in a NodeChan thread.
 *
 */
public class ChanPost {
  /** The unique tid this post belongs to **/
  private String tid;

  /** The unique id representing this specific post **/
  private String pid;

  /** whether or not this post is the first in its thread **/
  boolean isRoot;

  /** The title of the thread this post belongs to **/
  String title;

  /** The time at which this post was created (in millis) **/
  long postTime;

  /** The text of the post **/
  private String text;

  /** The number of times this node has received this post **/
  private int receiptCount;

  public ChanPost(String tid, String pid, boolean isRoot, String title, String text) {
    this.tid = tid;
    this.isRoot = isRoot;
    this.title = title;
    this.text = text;
    this.receiptCount = 0;

    this.postTime = System.currentTimeMillis();

    // only generate a new post ID if this is a new post, not a received one
    if (pid.equals("")) {
      // generate random 8-character post ID
      byte[] pid_bytes = new byte[8];
      Random rand = new Random();

      for (int i = 0; i < 8; i++) {
        // generate a random ASCII character from 48 to 122
        pid_bytes[i] = (byte)(rand.nextInt(74) + 48);
      }

      this.pid = new String(pid_bytes);
    } else {
      this.pid = pid;
    }
  }

  /**
   * When we receive this message, increment the receipt count.
   * The point of this is to avoid over-propagation of messages. Each client
   * will limit the number of times they forward a single post, unless they
   * are specifically requested to by another client.
   */
  public void heard() {
    this.receiptCount++;
  }

  /**
   * Convert a ChanPost into a byte array for transmission over UDP. See
   * format.txt for information about packet formatting.
   */
  public static byte[] encodeUDP(ChanPost out) {
    byte[] result = new byte[326];

    String outTid = out.getTid();
    String outPid = out.getPid();
    String outTitle = out.getTitle();
    String outText = out.getText();

    // NodeChan header
    result[0] = 'N';
    result[1] = 'C';

    // message type (P = post)
    result[2] = 'P';

    // flags
    result[3] = out.getIsRoot() ? (byte)1 : (byte)0;

    // thread ID
    for (int i = 0; i < 8; i++) {
      result[i + 4] = (byte)outTid.charAt(i);
    }

    // post ID
    for (int i = 0; i < 8; i++) {
      result[i + 12] = (byte)outPid.charAt(i);
    }

    // title
    for (int i = 0; i < 50; i++) {
      if (i < outTitle.length()) {
        result[20 + i] = (byte)outTitle.charAt(i);
      } else {
        result[20 + i] = (byte)0;
      }
    }

    // post text
    for (int i = 0; i < 256; i++) {
      if (i < outText.length()) {
        result[70 + i] = (byte)outText.charAt(i);
      } else {
        result[70 + i] = (byte)0;
      }
    }

    return result;
  }

  /**
   * Convert a byte array received via UDP back into a ChanPost.
   * See format.txt for formatting details.
   */
  public static ChanPost decodeUDP(byte[] in) {
    // check header
    if (in[0] != 'N' || in[1] != 'C') return null;

    // verify that this message is a post
    if (in[2] != 'P') return null;

    // we should be good to go
    boolean newRoot = (in[3] == 1);

    String newTid   = new String(in, 4, 8);
    String newPid   = new String(in, 12, 8);
    String newTitle = new String(in, 20, 50);
    String newText  = new String(in, 70, 256);

    ChanPost result = new ChanPost(
        newTid,
        newPid,
        newRoot,
        newTitle,
        newText
    );

    return result;
  }

  /*
   * getters
   */ 
  public String getTid() {
    return this.tid;
  }

  public String getPid() {
    return this.pid;
  }

  public boolean getIsRoot() {
    return this.isRoot;
  }

  public String getTitle() {
    return this.title;
  }

  public String getText() {
    return this.text;
  }

  public long getPostTime() {
    return this.postTime;
  }

  public int getReceiptCount() {
    return this.receiptCount;
  }
}
