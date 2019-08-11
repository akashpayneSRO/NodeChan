package com.squidtech.nodechan;

import java.util.ArrayList;
import java.util.Vector;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.DefaultListCellRenderer;
import javax.swing.AbstractAction;

/**
 * This is the main view for displaying and interacting with the posts of a
 * single thread.
 */
public class GUIThreadView extends JFrame {
  /** The thread to be displayed/interacted with **/
  private ChanThread thread;

  /** List of all posts in the thread **/
  private ArrayList<ChanPost> threadPosts;

  private JScrollPane scrollPane;
  private JList<ChanPost> chanPostJList;

  /** reply/refresh buttons **/
  private JButton replyButton;
  private JButton refreshButton;

  /** reply text field **/
  private JTextField replyText;

  /** reply bar **/
  private JPanel replyBar;

  public GUIThreadView(ChanThread thread) {
    super(thread.getTitle());
    this.thread = thread;

    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLayout(new BorderLayout());
    this.setSize(540, 380);

    threadPosts = new ArrayList<ChanPost>();

    // initialize the posts
    for (int i = 0; i < thread.getNumPosts(); i++) {
      threadPosts.add(thread.getPost(i));
    }

    replyBar = new JPanel(new BorderLayout());
    replyBar.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY),
                       new EmptyBorder(5, 5, 10, 5)));

    replyText = new JTextField();

    replyButton = new JButton(new AbstractAction("Reply") {
      public void actionPerformed(ActionEvent e) {
        NodeChan.createReplyAndSend(thread, replyText.getText());
        replyText.setText("");
        refreshPosts();
      }
    });

    refreshButton = new JButton(new AbstractAction("Refresh") {
      public void actionPerformed(ActionEvent e) {
        refreshPosts();
      }
    });

    JPanel buttons = new JPanel(new FlowLayout());
    buttons.add(replyButton);
    buttons.add(refreshButton);

    replyBar.add(replyText, BorderLayout.CENTER);
    replyBar.add(buttons, BorderLayout.EAST);

    this.add(replyBar, BorderLayout.SOUTH);


    chanPostJList = new JList<>(new Vector<ChanPost>(threadPosts));

    chanPostJList.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component renderer = new JTextArea();

        ((JTextArea)renderer).setLineWrap(true);
        ((JTextArea)renderer).setText(((ChanPost)value).getPid() + "\n" + ((ChanPost)value).getText());
        ((JTextArea)renderer).setBorder(new LineBorder(new Color(0, 0, 0)));

        return renderer;
      }
    });

    chanPostJList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
          int index = chanPostJList.locationToIndex(e.getPoint());

          if (index > -1) {
            ChanPost openPost = chanPostJList.getModel().getElementAt(index);

            new GUIRightClickMenu(thread, openPost, e, (JFrame)getRef());
          }
        }
      }
    });

    chanPostJList.setFixedCellHeight(100);

    scrollPane = new JScrollPane(chanPostJList);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    this.add(scrollPane);

    this.setVisible(true);
  }

  public void refreshPosts() {
    threadPosts = new ArrayList<ChanPost>();

    for (int i = 0; i < thread.getNumPosts(); i++) {
      threadPosts.add(thread.getPost(i));
    }

    chanPostJList.setListData(new Vector<ChanPost>(threadPosts));
  }

  /**
   * For accessing this object from within an anonymous class
   */
  public GUIThreadView getRef() {
    return this;
  }
}
