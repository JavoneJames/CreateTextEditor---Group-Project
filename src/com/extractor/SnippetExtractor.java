package com.extractor;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class SnippetExtractor {
    public static void createSnippet(String type, String result, String description) {

        //Read File and store contents

        StringBuilder sb = new StringBuilder();
        String strLine = "";
        String str_data = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader("res/snippets.json"));
            while (strLine != null) {
                if (strLine == null) {
                    break;
                }
                sb.append(strLine).append("\n");
                strLine = br.readLine();
            }
            System.out.println(str_data);
            br.close();
        } catch (FileNotFoundException e) {
            System.err.println("File Not Found");
        } catch (IOException e) {
            System.err.println("Unable to read the file");
        }

        //Re-Write file

        JSONObject obj = new JSONObject();
        obj.put("body", result);
        obj.put("description", description);

        try (FileWriter file = new FileWriter("res/snippets.json")) {
            file.write((sb.substring(0, sb.length() - 2) + "," + "\n" + " " + '"' + type + '"' + ":\n" + obj.toJSONString() + "}\n")); //Refactored in Iteration 1.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String GetSnippet(String snippet) {
        String codeBlock = null;
        JSONParser jsonParser = new JSONParser();
        try (Reader reader = new FileReader("res/snippets.json")) {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONObject snippetSection = (JSONObject) jsonObject.get(snippet);
            codeBlock = snippetSection.get("body").toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return codeBlock;
    }

    public static String GetSnippetName(String word) {

        String line;
        StringBuilder result = new StringBuilder();
        try {
            File file = new File("res/snippets.json");
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            line = reader.readLine();
            while (line != null) {
                if (line.startsWith("  " + '"' + word)) {
                    result.append(line);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }


}
