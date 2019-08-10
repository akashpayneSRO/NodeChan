package com.squidtech.nodechan;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.DefaultListCellRenderer;
import javax.swing.AbstractAction;

import java.util.ArrayList;
import java.util.Vector;

public class GUIMain extends JFrame {
  private ArrayList<ChanThread> threads;
  private ArrayList<Peer> peers;

  private JList<ChanThread> threadList;
  private JScrollPane scrollPane;

  private JMenuBar menuBar;

  JMenu fileMenu;
  JMenuItem exit;

  JMenu threadsMenu;
  JMenuItem refresh;

  public GUIMain(ArrayList<ChanThread> threads, ArrayList<Peer> peers) {
    super("NodeChan");
    this.threads = threads;
    this.peers = peers;

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

  private void refreshThreads() {
    threadList.setListData(new Vector<ChanThread>(threads));
  }
}
