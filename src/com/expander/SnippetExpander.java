package com.expander;

public class SnippetExpander {
    private final String WordsInEditor;
    private final int CaretPosition;
    public int startOfAbbreviationInTextArea;
    public int endOfAbbreviationInTextArea;
    private String abbreviation = "";

    public SnippetExpander(String EditorWords, int CurrentCaretPosition) {
        this.WordsInEditor = EditorWords;
        this.CaretPosition = CurrentCaretPosition;
    }

    public String getUserAbbreviation() {

        // Convert text within Editor to a formatted string
        String allWordsFromTextEditor = " " + WordsInEditor;
        //                  ^ : newline character added to "work-around" firstline abbreviations errors
        //remove newline & carriage return & TAB notation
        allWordsFromTextEditor = allWordsFromTextEditor
                .replace("\n", " ")
                .replace("\r", "")
                .replace("\t", " ");

        //Reading from the Cursor Position backwards to the most immediate space character
        for (int c = CaretPosition; c >= 0; c--) {
            if (allWordsFromTextEditor.charAt(c) == ' ') break;
                // Append current character to abbreviation
            else {
                abbreviation += (allWordsFromTextEditor.charAt(c));
            }
        }

        // Since reading occurs backwards, the abbreviation should be reversed
        abbreviation = new StringBuilder(abbreviation).reverse().toString();

        return (abbreviation);
    }

    public int getCursorStart() {
        // Indicate start of abbreviation
        startOfAbbreviationInTextArea = endOfAbbreviationInTextArea - abbreviation.length();
        return startOfAbbreviationInTextArea;
    }

    public int countOccurrencesInSnippet(String snippet, String wordToCount) {
        int countOfOccurrences = 0;

        String[] snippetSplitIntoArrayOfWords = snippet.split("\\s+");
        for (String eachWordInSnippet : snippetSplitIntoArrayOfWords) {
            if (eachWordInSnippet.contains(wordToCount)) {
                countOfOccurrences++;
            }
        }

        return countOfOccurrences;
    }

    public int getCursorEnd() {
        // Indicate end of abbreviation
        endOfAbbreviationInTextArea = CaretPosition;
        return endOfAbbreviationInTextArea;
    }
}
