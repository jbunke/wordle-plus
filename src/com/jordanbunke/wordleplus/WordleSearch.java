package com.jordanbunke.wordleplus;

import com.jordanbunke.wordleplus.io.WordList;

import java.util.Scanner;

public class WordleSearch {
    public static final char BLANK_CHAR = '-';

    private static final String QUIT_CODE = "q";

    public static void main(String[] args) {
        welcomeMessage();
        searchManager();
    }

    private static void welcomeMessage() {
        printDivider();
        printLine("Welcome to the word pattern search engine for " +
                WordlePlus.TITLE + " (v" + WordlePlus.VERSION + ")!");
        printDivider();
        printLine("Search for a word pattern to retrieve the matching words from the");
        printLine("word list in order of how commonly they occur in written English.");
        printLine("");
        printLine("Example: Searching for \"sc-re\" would return [score, scare].");
        printLine("Enter \"" + QUIT_CODE + "\" to quit.");
    }

    private static void searchManager() {
        final Scanner IN = new Scanner(System.in);

        prompt();
        String input = IN.nextLine().trim();

        do {
            if (input.equals(QUIT_CODE))
                quit();
            else {
                final int LENGTH = input.length();

                if (LENGTH > WPSettings.getMaxWordLength() ||
                        LENGTH < WPSettings.getMinWordLength())
                    invalidQueryLength(input);
                else
                    searchForQuery(input);
            }

            prompt();
            input = IN.nextLine().trim();
        } while (!input.equals(QUIT_CODE));

        quit();
    }

    private static void prompt() {
        printDivider();
        System.out.print("Your search: ");
    }

    private static void invalidQueryLength(final String query) {
        final int MIN = WPSettings.getMinWordLength(),
                MAX = WPSettings.getMaxWordLength(),
                LENGTH = query.length();

        printDivider();
        printLine("Search query of length " + LENGTH +
                " is outside the legal bounds of " + MIN + " and " + MAX + ".");
    }

    private static void searchForQuery(final String query) {
        final int lengthIndex = query.length() - WPConstants.INDEX_TO_LENGTH_OFFSET;

        final String[] results =
                WordList.getWordsMatchingSearchQuery(lengthIndex, query);
        printSearchResults(results);
    }

    private static void printSearchResults(final String[] results) {
        printDivider();

        if (results.length == 0)
            printLine("No results...");

        for (String result : results)
            printLine("> " + result);
    }

    private static void quit() {
        quitMessage();
        System.exit(0);
    }

    private static void quitMessage() {
        printDivider();
        printLine("Quitting...");
    }

    private static void printDivider() {
        final int LENGTH = 75;
        final String divider = "-".repeat(LENGTH);

        printLine(divider);
    }

    private static void printLine(final String line) {
        System.out.println(line);
    }
}
