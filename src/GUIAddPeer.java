package com.squidtech.nodechan;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.AbstractAction;

/**
 * This is the view for when the user wants to add a Peer by specific IP.
 */
public class GUIAddPeer extends JFrame {
  /** IP address input **/
  private JLabel infoLabel;
  private JTextField textField;
  private JButton button;

  /** main screen (for refreshing purposes) **/
  private GUIMain mainGui;

  public GUIAddPeer(GUIMain mainGui) {
    super("Add Peer");
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setSize(320, 120);
    this.setResizable(false);
    this.setLayout(new FlowLayout(FlowLayout.CENTER));

    this.mainGui = mainGui;

    infoLabel = new JLabel("IP: ");

    textField = new JTextField("", 20);

    button = new JButton(new AbstractAction("Add") {
      public void actionPerformed(ActionEvent e) {
        if (!textField.getText().equals("") && 
            NodeChan.addPeer(textField.getText())) {
          mainGui.refreshThreads();
          closePeerAdder();
        }
      }
    });

    this.add(infoLabel);
    this.add(textField);
    this.add(button);

    this.setVisible(true);
  }

  public void closePeerAdder() {
    this.setVisible(false);
    this.dispose();
  }
}
