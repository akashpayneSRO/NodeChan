package com.squidtech.nodechan;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.DefaultListCellRenderer;
import javax.swing.AbstractAction;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.JPopupMenu;

import java.util.List;
import java.util.Vector;

/**
 * This is the main GUI class that runs when NodeChan starts. From this screen,
 * users can select threads to read and reply to, as well as manage preferences
 * and other options.
 */
public class GUIMain extends JFrame {
  /** This user's list of threads **/
  private List<ChanThread> threads;
  /** This user's list of peers **/
  private List<Peer> peers;

  /** The list of threads that will be displayed to the user **/
  private JList<ChanThread> threadList;
  private JScrollPane scrollPane;

  private JMenuBar menuBar;

  /** "File" menu options **/
  JMenu fileMenu;
  JMenuItem exit;

  /** "Threads" menu options **/
  JMenu threadsMenu;
  JMenuItem refresh;
  JCheckBoxMenuItem autorefresh;

  /** "Peers" menu options **/
  JMenu peersMenu;
  JMenuItem addPeer;
  JMenuItem getPeer;
  JCheckBoxMenuItem keepAlive;

  /** Bottom status bar that displays the num of peers this user has **/
  JPanel statusBar;
  JButton newThread;
  JLabel statusNumPeers;

  public GUIMain(List<ChanThread> threads, List<Peer> peers) {
    super("NodeChan");
    this.threads = threads;
    this.peers = peers;

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLayout(new BorderLayout());
    this.setSize(640, 480);

    menuBar = new JMenuBar();
    this.setJMenuBar(menuBar);

    fileMenu = new JMenu("File");

    exit = new JMenuItem(new AbstractAction("Exit") {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    fileMenu.add(exit);


    threadsMenu = new JMenu("Threads");

    refresh = new JMenuItem(new AbstractAction("Refresh") {
      public void actionPerformed(ActionEvent e) {
        refreshThreads();
      }
    });
    threadsMenu.add(refresh);

    autorefresh = new JCheckBoxMenuItem(new AbstractAction("Autorefresh") {
      public void actionPerformed(ActionEvent e) {
        NodeChan.autorefresh = autorefresh.getState();
      }
    });
    autorefresh.setState(true);
    threadsMenu.add(autorefresh);


    peersMenu = new JMenu("Peers");

    addPeer = new JMenuItem(new AbstractAction("Add Peer...") {
      public void actionPerformed(ActionEvent e) {
        new GUIAddPeer(getRef());
      }
    });
    peersMenu.add(addPeer);

    getPeer = new JMenuItem(new AbstractAction("Get Peer From Tracker") {
      public void actionPerformed(ActionEvent e) {
        if (NodeChan.getPeerFromTracker(NodeChan.peerTrackerURL)) {
          System.out.println("Added peer from tracker.");
        } else {
          System.out.println("No peer available from tracker.");
        }
      }
    });
    peersMenu.add(getPeer);

    keepAlive = new JCheckBoxMenuItem(new AbstractAction("Keep Alive") {
      public void actionPerformed(ActionEvent e) {
        NodeChan.keepAlive = keepAlive.getState();
      }
    });
    keepAlive.setState(true);
    peersMenu.add(keepAlive);


    menuBar.add(fileMenu);
    menuBar.add(threadsMenu);
    menuBar.add(peersMenu);
    
   
    statusBar = new JPanel(new BorderLayout());
    statusBar.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY),
                        new EmptyBorder(4, 4, 4, 4)));

    newThread = new JButton(new AbstractAction("New Thread") {
      public void actionPerformed(ActionEvent e) {
        new GUICreateNewThread();
      }
    });
    statusBar.add(newThread, BorderLayout.WEST);

    statusNumPeers = new JLabel("Peers: " + peers.size());
    statusBar.add(statusNumPeers, BorderLayout.EAST);

    this.add(statusBar, BorderLayout.SOUTH);
    

    threadList = new JList<>(new Vector<ChanThread>(threads));

    threadList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        // only open threads on double-click
        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2) {
          int index = threadList.locationToIndex(e.getPoint());
          ChanThread openThread = threadList.getModel().getElementAt(index);

          new GUIThreadView(openThread);
        } else if (e.getButton() == MouseEvent.BUTTON3) {
          int index = threadList.locationToIndex(e.getPoint());

          if (index > -1) {
            ChanThread openThread = threadList.getModel().getElementAt(index);

            new GUIRightClickMenu(openThread, openThread.getPost(0), e, (JFrame)getRef());
          }
        }
      }
    });

    threadList.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (renderer instanceof JLabel && value instanceof ChanThread) {
          ((JLabel)renderer).setText(((ChanThread)value).getTitle());
          ((JLabel)renderer).setBorder(new LineBorder(new Color(0, 0, 0)));
        }

        return renderer;
      }
    });

    threadList.setFixedCellHeight(40);

    scrollPane = new JScrollPane(threadList);
    this.add(scrollPane);

    setVisible(true);
  }

  public void refreshThreads() {
    threadList.setListData(new Vector<ChanThread>(threads));
    statusNumPeers.setText("Peers: " + peers.size());
  }

  /**
   * this is really weird, but it works... maybe a better way to do it?
   * I'm doing this so that this can be accessed from inside anonymous
   * classes
   */
  public GUIMain getRef() {
    return this;
  }
}
