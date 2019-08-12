package com.squidtech.nodechan;

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

  /** The time of the most recent post in this thread **/
  private long lastTime;

  /** Storage for all of the posts that have been made in this thread **/
  private ArrayList<ChanPost> posts;

  public ChanThread(String tid) {
    if (tid.equals("")) {
      // generate random 8-character thread ID
      byte[] tid_bytes = new byte[8];
      Random rand = new Random();

      for (int i = 0; i < 8; i++) {
        tid_bytes[i] = (byte)(rand.nextInt(42) + 48);
      }

      this.tid = new String(tid_bytes);
    } else {
      this.tid = tid;
    }

    this.posts = new ArrayList<ChanPost>();
    this.title = "";

    this.lastTime = 0;
  }

  /**
   * Add a post to this thread.
   * Sorted by post time - earliest first.
   */
  public void addPost(ChanPost post) {
    if (post.getIsRoot()) {
      // this is the first post in the thread
      this.title = post.getTitle();
    }

    this.posts.add(post);

    if (post.getPostTime() > this.lastTime)
      this.lastTime = post.getPostTime();
  }

  /**
   * For sorting purposes
   * Sort from most recent (so, highest) post time to oldest (top-to-bottom)
   */
  public int compareTo(ChanThread other) {
    long t = this.lastTime - other.getLastTime();
    
    if (t < 0) return 1;
    else if (t > 0) return -1;
    else return 0;
  }

  public void setTitle(String title) {
    this.title = title;
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

  public long getLastTime() {
    return this.lastTime;
  }

  public ChanPost getPost(int i) {
    return this.posts.get(i);
  }

  public ArrayList<ChanPost> getPosts() {
    return this.posts;
  }

  /**
   * Delete a post (in case of blocking)
   */
  public void removePost(int i) {
    this.posts.remove(i);
  }
}
