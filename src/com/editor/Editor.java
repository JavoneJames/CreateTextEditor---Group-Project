package com.editor;

import com.createsnippetgui.CreateSnippetGUI;
import com.expander.SnippetExpander;
import com.extractor.SnippetExtractor;
import com.highlighting.SyntaxHighlighting;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.Objects;

import static java.awt.event.KeyEvent.*;

public class Editor extends EditorSettings implements KeyListener {

    private String fileType;

    public Editor(int width, int height) {
        super(width, height);
        FILECHOOSER = new JFileChooser();
        FILECHOOSER.setCurrentDirectory(new File(System.getProperty("user.home")));

        // Create a key listener
        editor.addKeyListener(this);

        content.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                lineNum();
            }

            private void lineNum() {
                try {
                    String input = editor.getText();
                    SimpleAttributeSet plain = new SimpleAttributeSet();
                    StyleConstants.setFontFamily(plain, "monospaced");
                    StyleConstants.setFontSize(plain, 11);

                    Document doc = lineNumbers.getDocument();
                    doc.remove(0, doc.getLength());

                    int length = input.length() - input.replaceAll("\n", "").length() + 1;

                    for (int i = 1; i <= length; i++) {
                        doc.insertString(doc.getLength(), i + "\n", plain);
                    }

                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lineNum();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lineNum();
            }
        });

        content.addUndoableEditListener(undoManager);

        menu.add(createFileMenuItem);
        menu.add(saveMenuItem);
        menu.add(loadMenuItem);
        menu.add(createSnippet);

        createFile(createFileMenuItem);
        saveToFile(saveMenuItem);
        loadFile(loadMenuItem);

        createSnippet.addActionListener(e -> {
            try {
                new CreateSnippetGUI();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        panel.add(scrollPane);


        redo.addActionListener(e -> {
            try {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            } catch (CannotUndoException ignored) {
            }
        });

        undo.addActionListener(e -> {
            try {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            } catch (CannotUndoException ignored) {
            }
        });


        Undo();

        Redo();
    }

    private void Redo() {
        editor.getActionMap().put("Redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canRedo()) {
                        undoManager.redo();
                    }
                } catch (CannotRedoException ignored) {
                }
            }
        });

        editor.getInputMap().put(KeyStroke.getKeyStroke("ctrl shift Z"), "Redo");
    }

    private void Undo() {
        editor.getActionMap().put("Undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canUndo()) {
                        undoManager.undo();
                    }
                } catch (CannotUndoException ignored) {
                }
            }
        });
        editor.getInputMap().put(KeyStroke.getKeyStroke("ctrl Z"), "Undo");
    }

    private void loadFile(JMenuItem loadMenuItem) {
        loadMenuItem.addActionListener(e -> {
            int result = FILECHOOSER.showOpenDialog(panel);
            if (result == JFileChooser.APPROVE_OPTION) {
                fileSelectedByTheUser[0] = FILECHOOSER.getSelectedFile();
                System.out.println("Selected file: " + fileSelectedByTheUser[0].getAbsolutePath());
                fileType = fileSelectedByTheUser[0].getName();
                frame.setTitle(fileType);
            }
            try (BufferedReader br = new BufferedReader(new FileReader(fileSelectedByTheUser[0].toString()))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                editor.setText(stringBuilder.toString());
            } catch (IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }

        });
    }

    private void saveToFile(JMenuItem saveMenuItem) {
        saveMenuItem.addActionListener(e -> {
            File fileSelectedByUser = null;
            do {
                if (e.getSource() == saveMenuItem) {
                    int returnValue = FILECHOOSER.showSaveDialog(panel);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        fileSelectedByUser = getSelectedFile();
                    } else break;
                }
            } while (!Objects.requireNonNull(fileSelectedByUser).exists());
        });
    }

    private File getSelectedFile() {
        File fileSelectedByUser;
        fileSelectedByUser = FILECHOOSER.getSelectedFile();
        if (!fileSelectedByUser.exists())
            savingFileConfirmDialog(fileSelectedByUser);
        else fileExtraction(fileSelectedByUser);
        frame.setTitle(fileSelectedByUser.toString());
        return fileSelectedByUser;
    }

    private void savingFileConfirmDialog(File fileSelectedByUser) {
        int value = JOptionPane.showConfirmDialog(frame, "File doesn't exists\n" +
                        "Would you like to save the file", "Error",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (value == JOptionPane.YES_OPTION) {
            fileExtraction(fileSelectedByUser);
        }
    }

    private void fileExtraction(File fileSelectedByUser) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter(fileSelectedByUser))) {
            bufferedWriter.write(editor.getText());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void createFile(JMenuItem createFileMenuItem) {
        createFileMenuItem.addActionListener(e -> {
            File file;
            int returnValue;
            do {
                returnValue = FILECHOOSER.showDialog(panel, "Create");
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    fileSelectedByUser = FILECHOOSER.getSelectedFile();
                    file = new File(FILECHOOSER.getSelectedFile().getPath());
                    try {
                        if (file.createNewFile()) {
                            System.out.println("file created");
                            break;
                        } else {
                            System.out.println("not created");
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                } else {
                    break;
                }
            } while (file.exists());
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //For autocomplete ref ticket #101
        if (e.getKeyChar() == '[') {
            try {
                editor.getDocument().insertString(editor.getCaretPosition(), " " + ']', null);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
            editor.setCaretPosition(editor.getCaretPosition() - 2);
        }
        if (e.getKeyChar() == '(') {
            try {
                editor.getDocument().insertString(editor.getCaretPosition(), " " + ')', null);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
            editor.setCaretPosition(editor.getCaretPosition() - 2);
        }
        if (e.getKeyChar() == '"') {
            try {
                editor.getDocument().insertString(editor.getCaretPosition(), " " + '"', null);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
            editor.setCaretPosition(editor.getCaretPosition() - 2);
        }
        if (e.getKeyChar() == '{') {
            try {
                editor.getDocument().insertString(editor.getCaretPosition(), " " + '}', null);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
            editor.setCaretPosition(editor.getCaretPosition() - 2);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char line = e.getKeyChar();

        String findWord = null;
        if (e.getKeyCode() == VK_UP || e.getKeyCode() == VK_DOWN || e.getKeyCode() == VK_LEFT || e.getKeyCode() == VK_RIGHT) {
            findWord = "";
        }

        String word = String.valueOf(line);
        findWord += word;
        editor.setToolTipText(SnippetExtractor.GetSnippetName(findWord.replaceAll("[^a-zA-Z0-9]", "")));

        if (e.getKeyCode() == VK_TAB || e.getKeyCode() == VK_BACK_SPACE) {
            findWord = "";
        }
        if (e.getKeyCode() != VK_F1 && cycle) {
            cycle = false;
            editor.setEditable(true);
        }
        if (e.getKeyCode() == VK_F1 && !cycle) {
            cycle = true;
            editor.setEditable(false);
        }

        if (e.getKeyCode() == VK_ENTER) {
            cycle = false;
        }

        if (e.getKeyCode() == VK_F1 && cycle || (e.getKeyCode() == VK_F1 && !cycle)) {
            vkF1pressed();
            if (e.getKeyCode() == VK_F1 && !cycle) {
                vkF1pressed();
            }
            if (e.getKeyCode() == VK_F1 && cycle) {
                vkF1pressed();
            }
        }

        //on TAB press call to SnippetExpander
        if (e.getKeyCode() == VK_TAB && !cycle) {

            //Expand User input by passing all text in editor and the current text cursor position
            SnippetExpander expanded = new SnippetExpander(editor.getText(), editor.getCaretPosition());

            try {
                //pass into SnippetExtractor
                //SnippetExtractor will extract the body of the JSON file that matches the given title
                String template = SnippetExtractor.GetSnippet(expanded.getUserAbbreviation());

                // Move Cursor over abbreviation
                editor.setSelectionEnd(expanded.getCursorEnd());
                editor.setSelectionStart(expanded.getCursorStart());

                //write returning template into the editor, replacing the highlighted text
                editor.replaceSelection(template);

                cycle = true;
                snippetStart = expanded.startOfAbbreviationInTextArea;
                snippetEnd = template.length() + expanded.startOfAbbreviationInTextArea;
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
        if (e.getKeyCode() != VK_ENTER && e.getKeyCode() != VK_F1 &&
                e.getKeyCode() != VK_TAB && !cycle) {
            System.out.println(e.getKeyCode());
            try {
                //the method call that caused the issue with System repeatedly printing for snippets that contained 'System'
                String[] grabextension = frame.getTitle().split("\\.");
                int _last = editor.getCaretPosition();
                SyntaxHighlighting.gets_all_code_from_editor(grabextension[1]);
                editor.setCaretPosition(_last);
            } catch (Exception ignored) {
            }
        }
    }

    public void vkF1pressed() {
        cycle = true;
        System.out.println("F1 pressed and caught");
        editor.setEditable(false);
        int start = (snippetEnd < editor.getCaretPosition()) ? snippetStart : editor.getCaretPosition() - 1;
        int end = snippetEnd;
        int secondEndPoint = 0;
        //Find next wildcard '$' - if nothing is found starts at 1
        for (int i = start + 1; i < snippetEnd; i++) {
            try {
                if (editor.getDocument().getText(0, editor.getDocument().getLength()).charAt(i) == '$') {
                    end = i + 1;
                    break;
                }
            } catch (Exception e) {
                i = start;
                break;
            }
        }

        //Highlights the next area the cursor will move to.

        for (int j = end + 1; j < snippetEnd; j++) {
            /*Taking the end of the previous for loop I shall +1 to get the next character,
            it will look until the end of the snippet or terminate when $ is found, if so it will store the position.*/
            try {
                if (editor.getText().charAt(j) == '$') {
                    secondEndPoint = j;
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //Using the two for loops, I can then set the start and end position of the highlighting.

        Highlighter hl = editor.getHighlighter();
        Highlighter.HighlightPainter painter =
                new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
        try {
            editor.getHighlighter().removeAllHighlights();
            hl.addHighlight(end + 1, secondEndPoint - 1, painter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        //Set cursor position start and highlight positions
        editor.setSelectionStart(start);
        editor.setSelectionEnd(end);

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
