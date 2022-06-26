package com.createsnippetgui;


import com.extractor.SnippetExtractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;

public class CreateSnippetGUI extends JFrame {

    JLabel nameLabel = new JLabel("Name: ");
    JLabel body = new JLabel("Body");
    JLabel description = new JLabel("Description: ");
    JTextField nameText = new JTextField(10);
    JTextField descriptionText = new JTextField(10);
    JTextArea bodyArea = new JTextArea(10, 20);
    JScrollPane scrollPane = new JScrollPane(bodyArea);
    JButton enterButton = new JButton("Enter");

    public CreateSnippetGUI() throws IOException {

        //Frame Properties

        setTitle("Create Snippet");
        setSize(300, 300);
        setVisible(true);
        setResizable(false);

        //Containers to improve presentation.

        JPanel container = new JPanel(new BorderLayout());
        JPanel namePanel = new JPanel(new FlowLayout());
        JPanel bodyPanel = new JPanel(new FlowLayout());
        JPanel descriptionPanel = new JPanel(new FlowLayout());

        //TextArea properties

        bodyArea.setLineWrap(true);

        //Enter Button Event Caller

        enterButton.addActionListener(new ButtonHandler(this, bodyArea));

        //TextArea size options

        nameText.setSize(250, 30);
        bodyArea.setSize(160, 170);
        descriptionText.setSize(250, 30);

        //Adding components to frame.

        add(container, BorderLayout.CENTER);
        container.add(namePanel, BorderLayout.NORTH);
        container.add(bodyPanel, BorderLayout.CENTER);
        container.add(descriptionPanel, BorderLayout.SOUTH);
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(nameText, BorderLayout.CENTER);
        bodyPanel.add(body, BorderLayout.WEST);
        bodyPanel.add(scrollPane, BorderLayout.EAST);
        descriptionPanel.add(description, BorderLayout.WEST);
        descriptionPanel.add(descriptionText, BorderLayout.CENTER);
        descriptionPanel.add(enterButton, BorderLayout.SOUTH);

        pack();
    }


    private class ButtonHandler implements ActionListener {
        CreateSnippetGUI theApp;
        JTextArea text;

        ButtonHandler(CreateSnippetGUI app, JTextArea textArea) {
            theApp = app;
            text = textArea;

        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if ("Enter".equals(e.getActionCommand())) {
                System.out.println(nameText.getText() + bodyArea.getText() + descriptionText.getText());
                SnippetExtractor.createSnippet(nameText.getText(), bodyArea.getText(), descriptionText.getText());
                System.out.println("The snippet has been created and added to the JSON file");
            }
        }
    }
}
