import java.util.Random;
import java.util.ArrayList;

/**
 *
 * An entity that represents a single collection of posts or "conversation" on
 * NodeChan.
 * Despite the unfortunate name, this class has nothing to do with
 * multithreading. It's only a collection of posts.
 *
 * This class is a local collection of the posts in a specific thread that the
 * client has received.
 *
 */
public class ChanThread {
  /** Unique thread ID **/
  private String tid;

  /** The title of this thread **/
  private String title;

  /** Storage for all of the posts that have been made in this thread **/
  private ArrayList<ChanPost> posts;

  public ChanThread(String tid) {
    if (tid.equals("")) {
      // generate random 8-character thread ID
      byte[] tid_bytes = new byte[8];
      Random rand = new Random();

      for (int i = 0; i < 8; i++) {
        // generate a random ASCII character from 48 to 122
        tid_bytes[i] = (byte)(rand.nextInt(74) + 48);
      }

      this.tid = new String(tid_bytes);
    } else {
      this.tid = tid;
    }

    this.posts = new ArrayList<ChanPost>();
    this.title = "";
  }

  /**
   * Add a post to this thread.
   * Sorted by post time - earliest first.
   */
  public void addPost(ChanPost post) {
    if (posts.size() == 0 && post.getIsRoot()) {
      // this is the first post in the thread
      this.title = post.getTitle();
      this.posts.add(post);
    } else {
      // sort by time (earliest first)
      for (int i = 0; i < posts.size(); i++) {
        if (posts.get(i + 1) != null &&
            posts.get(i).getPostTime() < post.getPostTime() &&
            posts.get(i + 1).getPostTime() >= post.getPostTime()) {
          this.posts.add(i + 1, post);
          return;
        }
      }

      this.posts.add(post);
    }
  }


  /*
   * getters
   */
  public String getTid() {
    return this.tid;
  }

  public String getTitle() {
    return this.title;
  }

  public int getNumPosts() {
    return this.posts.size();
  }

  /**
   * We want to avoid giving outside classes direct access to the posts
   * ArrayList
   */
  public ChanPost getPost(int i) {
    return this.posts.get(i);
  }
}
