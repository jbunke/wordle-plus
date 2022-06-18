package com.jordanbunke.wordleplus.io;

import com.jordanbunke.jbjgl.io.JBJGLFileIO;
import com.jordanbunke.wordleplus.WPConstants;
import com.jordanbunke.wordleplus.WPRecent;
import com.jordanbunke.wordleplus.WPSettings;
import com.jordanbunke.wordleplus.WordleSearch;
import com.jordanbunke.wordleplus.utility.WPRandom;

import java.nio.file.Path;
import java.nio.file.Paths;

public class WordList {
    private static final Path WORD_LISTS_FOLDER = Paths.get("resources", "word-lists");
    private static final String NEW_LINE = "\n";

    private static final String[][] WORD_LISTS = generateWordLists();

    private static String[][] generateWordLists() {
        final String[][] wordLists = new String[4][];

        for (int i = 0; i < 4; i++) {
            final Path filename = WORD_LISTS_FOLDER.resolve(
                    Paths.get((i + WPConstants.INDEX_TO_LENGTH_OFFSET) + "l.txt"));

            wordLists[i] = JBJGLFileIO.readFile(filename).split(NEW_LINE);
        }

        return wordLists;
    }

    public static String selectGoalWord(final int lengthIndex) {
        final int candidateCutoff = WPSettings.getCandidateCutoff(lengthIndex);

        String candidate;

        do {
            final int position = WPRandom.boundedRandomPower(
                    0, candidateCutoff, WPSettings.getFrequencyExponent());
            candidate = WORD_LISTS[lengthIndex][position];
        } while (!isLegalGoalWord(candidate, lengthIndex));

        return candidate;
    }

    public static boolean isInWordList(final String word, final int lengthIndex) {
        final int length = WPConstants.LENGTHS[lengthIndex];
        final String wordProcessed = word.trim().toLowerCase();

        if (wordProcessed.length() != length)
            return false;

        for (String wordInList : WORD_LISTS[lengthIndex])
            if (wordProcessed.equals(wordInList))
                return true;

        return false;
    }

    public static int getWordListLength(final int lengthIndex) {
        return WORD_LISTS[lengthIndex].length;
    }

    public static boolean isLegalGoalWord(final String candidate, final int lengthIndex) {
        return candidate.length() == WPConstants.LENGTHS[lengthIndex] &&
                !WPRecent.containsCandidate(lengthIndex, candidate);
    }

    // WORD SEARCH EXTENSION

    public static String[] getWordsMatchingSearchQuery(
            final int lengthIndex, final String query) {
        final String[] matchingWords = new String[WORD_LISTS[lengthIndex].length];
        int matchingCount = 0;

        for (String word : WORD_LISTS[lengthIndex]) {
            if (wordMatchesSearchQuery(word, query)) {
                matchingWords[matchingCount] = word;
                matchingCount++;
            }
        }

        final String[] results = new String[matchingCount];
        System.arraycopy(matchingWords, 0, results, 0, matchingCount);

        return results;
    }

    private static boolean wordMatchesSearchQuery(
            final String word, final String query) {
        if (word.length() != query.length())
            return false;

        final int LENGTH = word.length();

        for (int i = 0; i < LENGTH; i++)
            if (word.charAt(i) != query.charAt(i) &&
                    query.charAt(i) != WordleSearch.BLANK_CHAR)
                return false;

        return true;
    }
}
