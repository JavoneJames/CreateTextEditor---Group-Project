package com.editor;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.io.File;

public class EditorSettings {
    public static JTextPane editor;
    public static JTextPane lineNumbers;
    public static JMenu editMenu;
    static int snippetStart = 0;
    static int snippetEnd = 0;
    final File[] fileSelectedByTheUser = new File[1];
    final UndoManager undoManager = new UndoManager();
    final JScrollPane scrollPane;
    final JMenuBar menuBar;
    final JMenu menu;
    final JMenuItem saveMenuItem;
    final JMenuItem createFileMenuItem;
    final JMenuItem loadMenuItem;
    final JMenuItem createSnippet;
    public JMenuItem redo;
    public JMenuItem undo;
    JFileChooser FILECHOOSER;
    JFrame frame;
    JPanel panel;
    Popup pop;
    JPanel pan;
    boolean cycle = false;
    Document content;
    File fileSelectedByUser;

    public EditorSettings(int width, int height) {
        FILECHOOSER = new JFileChooser();
        FILECHOOSER.setCurrentDirectory(new File(System.getProperty("user.home")));

        // Assign variables
        frame = new JFrame();
        panel = new JPanel();
        editor = new JTextPane();
        editor.setPreferredSize(new Dimension(width, height));

        editor.setEditable(true);

        // Create a new scroll pane
        scrollPane = new JScrollPane();

        scrollPane.setSize(new Dimension(width, height));

        content = editor.getDocument();

        // Line-numbers
        lineNumbers = new JTextPane();
        lineNumbers.setBackground(Color.GRAY);
        lineNumbers.setEditable(false);

        SimpleAttributeSet rightAlign = new SimpleAttributeSet();
        StyleConstants.setAlignment(rightAlign, StyleConstants.ALIGN_RIGHT);
        lineNumbers.setParagraphAttributes(rightAlign, true);

        scrollPane.getViewport().add(editor);
        scrollPane.setRowHeaderView(lineNumbers);

        //create a menu bar
        menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        menuBar.add(menu);

        saveMenuItem = new JMenuItem("Save");
        createFileMenuItem = new JMenuItem("Create File");
        loadMenuItem = new JMenuItem("Load");

        createSnippet = new JMenuItem("Create Snippet");

        menu.add(createFileMenuItem);
        menu.add(saveMenuItem);
        menu.add(loadMenuItem);
        menu.add(createSnippet);

        panel.add(scrollPane);

        editMenu = new JMenu("Edit");
        redo = new JMenuItem("Redo");
        undo = new JMenuItem("Undo");
        editMenu.add(redo);
        editMenu.add(undo);

        menuBar.add(editMenu);

        frame.setJMenuBar(menuBar);
        frame.add(panel, BorderLayout.CENTER);
        frame.add(panel);
        frame.pack();
        frame.setLayout(null);
        frame.setVisible(true);

        // Set to quit application when close button is pressed
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
