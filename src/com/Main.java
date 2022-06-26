package com;

import com.editor.Editor;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Editor(600, 600));
    }
}
