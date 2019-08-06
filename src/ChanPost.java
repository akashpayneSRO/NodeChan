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

  public ChanPost(String tid, boolean isRoot, String title, String text) {
    this.tid = tid;
    this.isRoot = isRoot;
    this.title = title;
    this.text = text;

    this.postTime = System.currentTimeMillis();

    // generate random 8-character post ID
    byte[] pid_bytes = new byte[8];
    Random rand = new Random();

    for (int i = 0; i < 8; i++) {
      // generate a random ASCII character from 33 to 126
      pid_bytes[i] = (byte)(rand.nextInt(93) + 33);
    }

    this.pid = new String(pid_bytes);
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
}
