package com.squidtech.nodechan;

import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.AbstractAction;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * This is the dialog window for creating and sending a new thread.
 */
public class GUICreateNewThread extends JFrame {
  /** Thread title **/
  JLabel titleLabel;
  JTextField titleField;
  JPanel titlePanel;

  /** Thread text **/
  JLabel textLabel;
  JTextArea textField;
  JPanel textPanel;

  /** Submit thread **/
  JButton submit;

  JPanel container;

  public GUICreateNewThread() {
    super("New Thread");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setSize(480, 240);
    setResizable(false);
    
    container = new JPanel(new BorderLayout());
    container.setBorder(new EmptyBorder(10, 10, 10, 10));

    titleLabel = new JLabel("Title:");
    titleField = new JTextField();
    titlePanel = new JPanel();
    titlePanel.setLayout(new BorderLayout());
    titlePanel.add(titleLabel, BorderLayout.WEST);
    titlePanel.add(titleField, BorderLayout.CENTER);
    
    textLabel = new JLabel("Text:");
    textField = new JTextArea();
    textField.setLineWrap(true);
    textField.setBackground(new Color(255, 255, 255));
    textField.setBorder(new LineBorder(Color.DARK_GRAY, 1));
    textPanel = new JPanel();
    textPanel.setLayout(new BorderLayout());
    textPanel.add(textLabel, BorderLayout.WEST);
    textPanel.add(textField, BorderLayout.CENTER);

    submit = new JButton(new AbstractAction("Post Thread") {
      public void actionPerformed(ActionEvent e) {
        if (titleField.getText().equals("") || textField.getText().equals("")) {
          System.out.println("New post must have a title and text!");
          return;
        }
        ChanThread newThread = NodeChan.createThreadAndSend(titleField.getText(), textField.getText());

        // TODO: open the user's new thread

        closeThreadCreateScreen();
      }
    });

    JPanel buttonPanel = new JPanel(new BorderLayout());
    buttonPanel.setBorder(new EmptyBorder(5, 120, 5, 120));

    container.add(titlePanel, BorderLayout.NORTH);
    container.add(textPanel, BorderLayout.CENTER);
    container.add(buttonPanel, BorderLayout.SOUTH);

    buttonPanel.add(submit, BorderLayout.CENTER);

    this.add(container, BorderLayout.CENTER);
    

    setVisible(true);
  }

  public void closeThreadCreateScreen() {
    this.setVisible(false);
    this.dispose();
  }
}
