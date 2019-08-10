package com.squidtech.nodechan;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.DefaultListCellRenderer;

import java.util.ArrayList;
import java.util.Vector;

public class GUIMain extends JFrame {
  private ArrayList<ChanThread> threads;
  private ArrayList<Peer> peers;

  private JList<ChanThread> threadList;
  private JScrollPane scrollPane;

  public GUIMain(ArrayList<ChanThread> threads, ArrayList<Peer> peers) {
    super("NodeChan");
    this.threads = threads;
    this.peers = peers;

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(640, 480);

    threadList = refreshThreads();

    this.add(new JScrollPane(threadList));
    setVisible(true);
  }

  private JList<ChanThread> refreshThreads() {
    JList<ChanThread> result = new JList<>(new Vector<ChanThread>(threads));

    result.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (renderer instanceof JLabel && value instanceof ChanThread) {
          ((JLabel)renderer).setText(((ChanThread)value).getTitle());
        }

        return renderer;
      }
    });

    return result;
  }
}
