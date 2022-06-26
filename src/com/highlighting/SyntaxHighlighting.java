package com.highlighting;

import com.editor.Editor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class SyntaxHighlighting {
    public String fileType;

    public static HashMap<String, String> get_color_from_json(String fileType) {
        HashMap<String, String> wordAndColourMap = new HashMap<>();
        if (!Objects.equals(fileType, "")) {
            JSONParser jsonParser = new JSONParser();
            try (Reader reader = new FileReader("res/code_colors.json")) {
                JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
                JSONObject languageToGet = (JSONObject) jsonObject.get('.' + fileType);
                for (Object key : languageToGet.keySet()) {
                    String keyStr = (String) key;
                    String keyValue = (String) languageToGet.get(keyStr);
                    wordAndColourMap.put(keyStr, keyValue);
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        return wordAndColourMap;
    }

    public static void gets_all_code_from_editor(String fileType) throws BadLocationException {
        HashMap<String, String> KeywordsAndColoursMap = get_color_from_json(fileType);
        JTextPane contents = Editor.editor;
        int length = contents.getDocument().getLength();
        String all_code = contents.getDocument().getText(0, length);
        int lines = all_code.split("\r\n").length;
        System.out.println(lines);

        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset;
        for (Map.Entry<String, String> entry : KeywordsAndColoursMap.entrySet()) {
            String sub_code = entry.getKey();
            String color = entry.getValue();
            ArrayList<String> section_list = new ArrayList<>(Arrays.asList(all_code.split(sub_code)));
            int position_counter = 0;
            boolean completed_first = false;
            for (String section :
                    section_list) {
                position_counter += section.length();
                if (completed_first) {
                    position_counter += sub_code.length();
                }
                try {
                    aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.decode(color));
                    contents.setCaretPosition(position_counter);
                    contents.getDocument().remove(position_counter, sub_code.length());
                    contents.setCharacterAttributes(aset, false);
                    contents.replaceSelection(sub_code);

//                        contents.setCaretPosition(cursor_position);
                    completed_first = true;
                } catch (Exception e) {
                    // didnt change color
                }
            }
        }
        aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
        contents.setCharacterAttributes(aset, false);
    }
}
