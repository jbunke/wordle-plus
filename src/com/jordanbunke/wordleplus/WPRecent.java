package com.jordanbunke.wordleplus;

public class WPRecent {
    private static final String[][] RECENT_WORDS = new String[WPConstants.NUM_WORD_LENGTH_OPTIONS][];

    private static final String BLANK_WORD = "-";

    public static void setRecentWordsForLength(final int lengthIndex, final String[] recentWords) {
        RECENT_WORDS[lengthIndex] = new String[WPConstants.RECENT_WORDS_BUFFER_SIZE];

        final int smallerBufferLength = Math.min(recentWords.length, RECENT_WORDS[lengthIndex].length);

        System.arraycopy(recentWords, 0, RECENT_WORDS[lengthIndex], 0, smallerBufferLength);

        for (int i = smallerBufferLength; i < WPConstants.RECENT_WORDS_BUFFER_SIZE; i++) {
            RECENT_WORDS[lengthIndex][i] = BLANK_WORD;
        }
    }

    public static boolean containsCandidate(final int lengthIndex, final String candidateWord) {
        for (String word : RECENT_WORDS[lengthIndex])
            if (word.equals(candidateWord))
                return true;

        return false;
    }

    public static void updateRecentWordsForLength(final int lengthIndex, final String newestWord) {
        for (int i = WPConstants.RECENT_WORDS_BUFFER_SIZE - 1; i > 0; i--) {
            RECENT_WORDS[lengthIndex][i] = RECENT_WORDS[lengthIndex][i - 1];
        }

        RECENT_WORDS[lengthIndex][0] = newestWord;
    }

    public static String[] getRecentWordsForLength(final int lengthIndex) {
        return RECENT_WORDS[lengthIndex];
    }
}
