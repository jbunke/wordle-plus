package com.jordanbunke.wordleplus;

import com.jordanbunke.wordleplus.io.WordList;

import java.util.Scanner;

public class WordleSearch {
    public static final char BLANK_CHAR = '-';

    private static final Scanner IN = new Scanner(System.in);

    private static final String QUIT_CODE = "q";
    private static final String SEARCH_CODE = "search";
    private static final String SOLVE_CODE = "solve";

    private static final String SEPARATOR = ",";
    private static final String BLANK = "-";

    // SECTION - BASIC CONTROL FLOW

    public static void main(String[] args) {
        welcomeMessage();
        modeSelect();
    }

    private static void modeSelect() {
        modeSelectPrompt();

        final String input = getInput();

        switch (input) {
            case SEARCH_CODE -> searchManager(false);
            case SOLVE_CODE -> searchManager(true);
            case QUIT_CODE -> quit();
            default -> {
                invalidInput(input);
                modeSelect();
            }
        }
    }

    private static void searchManager(final boolean solve) {
        searchExplanation();

        wordPatternPrompt();
        String input = getInput();

        do {
            if (input.equals(QUIT_CODE))
                quit();
            else {
                final int LENGTH = input.length();

                if (LENGTH > WPSettings.getMaxWordLength() ||
                        LENGTH < WPSettings.getMinWordLength())
                    invalidQueryLength(input);
                else if (solve) {
                    final char[] toOmit = processOmissions();
                    final char[] mustInclude = processInclusions();
                    solveForQuery(input, toOmit, mustInclude);
                } else
                    searchForQuery(input);
            }

            wordPatternPrompt();
            input = getInput();
        } while (!input.equals(QUIT_CODE));

        quit();
    }

    private static void searchForQuery(final String query) {
        final int lengthIndex = query.length() - WPConstants.INDEX_TO_LENGTH_OFFSET;

        final String[] results =
                WordList.getWordsMatchingSearchQuery(lengthIndex, query);
        printSearchResults(results, false);
    }

    private static void solveForQuery(
            final String query, final char[] toOmit, final char[] mustInclude
    ) {
        final int lengthIndex = query.length() - WPConstants.INDEX_TO_LENGTH_OFFSET;

        if (existsOverlap(toOmit, mustInclude)) {
            printLine("Omissions and inclusions have an overlap...");
            printLine("No solutions were found.");
        }

        final String[] results = filterResults(
                WordList.getWordsMatchingSearchQuery(lengthIndex, query),
                toOmit, mustInclude);
        printSearchResults(results, true);
    }

    private static void quit() {
        quitMessage();
        System.exit(0);
    }

    // SECTION - MESSAGES

    private static void welcomeMessage() {
        printDivider();
        printLine("Welcome to the word pattern search engine for " +
                WordlePlus.TITLE + " (v" + WordlePlus.VERSION + ")!");
        printLine("");
        printLine("Enter \"" + QUIT_CODE + "\" to quit.");
    }

    private static void searchExplanation() {
        printDivider();
        printLine("Search for a word pattern to retrieve the matching words from the");
        printLine("word list in order of how commonly they occur in written English.");
        printLine("");
        printLine("Example: Searching for \"sc-re\" would return [score, scare].");
    }

    private static void invalidInput(final String input) {
        printDivider();
        printLine("\"" + input + "\" is not a valid mode identifier.");
    }

    private static void invalidQueryLength(final String query) {
        final int MIN = WPSettings.getMinWordLength(),
                MAX = WPSettings.getMaxWordLength(),
                LENGTH = query.length();

        printDivider();
        printLine("Search query of length " + LENGTH +
                " is outside the legal bounds of " + MIN + " and " + MAX + ".");
    }

    private static void printSearchResults(final String[] results, final boolean solve) {
        printDivider();

        if (results.length == 0)
            printLine("No " + (solve ? "solutions" : "search results") + "...");

        for (String result : results)
            printLine("> " + result);
    }

    private static void quitMessage() {
        printDivider();
        printLine("Quitting...");
    }

    // SECTION - PROMPTS

    private static void modeSelectPrompt() {
        printDivider();
        prompt("Select a mode by typing \"" + SEARCH_CODE +
                "\" or \"" + SOLVE_CODE + "\": ");
    }

    private static void wordPatternPrompt() {
        printDivider();
        prompt("Enter a word pattern: ");
    }

    private static void omissionsPrompt() {
        commaSeparatedLetterPrompt("should be omitted from");
    }

    private static void inclusionsPrompt() {
        commaSeparatedLetterPrompt("must be included in");
    }

    private static void commaSeparatedLetterPrompt(final String context) {
        printDivider();
        printLine("Provide the letters that " + context + " the solution.");
        prompt("Letters separated by commas; \"-\" to leave blank: ");
    }

    // SECTION - COMPLEX BEHAVIOUR

    private static char[] processOmissions() {
        omissionsPrompt();
        return getCommaSeparatedLetters();
    }

    private static char[] processInclusions() {
        inclusionsPrompt();
        return getCommaSeparatedLetters();
    }

    private static char[] getCommaSeparatedLetters() {
        final String input = getInput();

        if (input.equals(BLANK) || input.equals(""))
            return new char[] {};

        final String[] unformatted = input.split(SEPARATOR);
        final char[] results = new char[unformatted.length];

        for (int i = 0; i < results.length; i++) {
            final char c = unformatted[i].trim().toLowerCase().charAt(0);
            results[i] = c;
        }

        return results;
    }

    private static String[] filterResults(
            final String[] results, final char[] toOmit, final char[] mustInclude
    ) {
        int matchingCount = 0;
        final String[] matchingWords = new String[results.length];

        for (String word : results)
            if (containsNoOmittedChars(word, toOmit) &&
                    containsAllMandatoryInclusions(word, mustInclude)) {
                matchingWords[matchingCount] = word;
                matchingCount++;
            }

        final String[] matchingResults = new String[matchingCount];
        System.arraycopy(matchingWords, 0, matchingResults, 0, matchingCount);

        return matchingResults;
    }

    private static boolean containsNoOmittedChars(final String word, final char[] toOmit) {
        for (char c : toOmit)
            for (int j = 0; j < word.length(); j++) {
                final char at = word.charAt(j);

                if (c == at)
                    return false;
            }

        return true;
    }

    private static boolean containsAllMandatoryInclusions(final String word, final char[] mustInclude) {
        final boolean[] contains = new boolean[mustInclude.length];

        for (int i = 0; i < mustInclude.length; i++)
            for (int j = 0; j < word.length(); j++) {
                final char at = word.charAt(j);
                if (at == mustInclude[i]) {
                    contains[i] = true;
                    break;
                }
            }

        boolean containsAll = true;

        for (boolean contain : contains)
            containsAll &= contain;

        return containsAll;
    }

    private static boolean existsOverlap(final char[] a, final char[] b) {
        for (char aChar : a)
            for (char bChar : b)
                if (aChar == bChar)
                    return true;

        return false;
    }

    // SECTION - IO HELPERS

    private static void printDivider() {
        final int LENGTH = 75;
        final String divider = "-".repeat(LENGTH);

        printLine(divider);
    }

    private static void printLine(final String line) {
        System.out.println(line);
    }

    private static void prompt(final String prompt) {
        System.out.print(prompt);
    }

    private static String getInput() {
        return IN.nextLine().toLowerCase().trim();
    }
}
