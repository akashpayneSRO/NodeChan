package com.squidtech.nodechan;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.DefaultListCellRenderer;
import javax.swing.AbstractAction;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.util.ArrayList;
import java.util.Vector;

/**
 * This is the main GUI class that runs when NodeChan starts. From this screen,
 * users can select threads to read and reply to, as well as manage preferences
 * and other options.
 */
public class GUIMain extends JFrame {
  /** This user's list of threads **/
  private ArrayList<ChanThread> threads;
  /** This user's list of peers **/
  private ArrayList<Peer> peers;

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

  /** Bottom status bar that displays the num of peers this user has **/
  JPanel statusBar;
  JLabel statusNumPeers;

  public GUIMain(ArrayList<ChanThread> threads, ArrayList<Peer> peers) {
    super("NodeChan");
    this.threads = threads;
    this.peers = peers;

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLayout(new BorderLayout());
    setSize(640, 480);

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


    menuBar.add(fileMenu);
    menuBar.add(threadsMenu);
    
   
    statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
    statusBar.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY),
                        new EmptyBorder(4, 4, 4, 4)));

    statusNumPeers = new JLabel("Peers: " + peers.size());
    statusBar.add(statusNumPeers);

    this.add(statusBar, BorderLayout.SOUTH);
    

    threadList = new JList<>(new Vector<ChanThread>(threads));

    threadList.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (renderer instanceof JLabel && value instanceof ChanThread) {
          ((JLabel)renderer).setText(((ChanThread)value).getTitle());
        }

        return renderer;
      }
    });

    this.add(new JScrollPane(threadList));
    setVisible(true);
  }

  public void refreshThreads() {
    threadList.setListData(new Vector<ChanThread>(threads));
    statusNumPeers.setText("Peers: " + peers.size());
  }
}
