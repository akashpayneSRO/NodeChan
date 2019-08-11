package com.squidtech.nodechan;

import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;
import javax.swing.JFrame;

/**
 * This menu appears when the user right-clicks on a thread or post.
 */
public class GUIRightClickMenu extends JPopupMenu {
  /** The thread that has been right-clicked **/
  private ChanThread selectedThread;
  /** The post that has been-clicked, or the first post in the thread that has been right-clicked **/
  private ChanPost selectedPost;
  /** The triggering MouseEvent **/
  private MouseEvent e;
  /** The frame to show the JPopupMenu in **/
  private JFrame frame;

  public GUIRightClickMenu(ChanThread selectedThread, ChanPost selectedPost, MouseEvent e, JFrame frame) {
    super();
    this.selectedThread = selectedThread;
    this.selectedPost = selectedPost;
    this.e = e;
    this.frame = frame;

    JMenuItem block = new JMenuItem(new AbstractAction("Block") {
      public void actionPerformed(ActionEvent a) {
        NodeChan.blockUser(selectedThread.getTid(), selectedPost.getPid());
      }
    });

    this.add(block);

    frame.add(this);

    this.show(frame, e.getX(), e.getY());
  }
}
