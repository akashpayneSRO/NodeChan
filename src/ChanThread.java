import java.util.ArrayList;

public class ChanThread {
  private String tid;
  private String title;
  private ArrayList<ChanPost> posts;

  public ChanThread(String tid, ChanPost firstPost, String title) {
    this.tid = tid;
    this.title = title;

    posts = new ArrayList<ChanPost>();

    posts.insert(firstPost);
  }
}
