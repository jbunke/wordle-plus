package com.jordanbunke.wordleplus;

public class WPStats {
    private static final int[][] GUESSES = new int[WPConstants.NUM_WORD_LENGTH_OPTIONS][];
    private static final int[][] RESULTS = new int[WPConstants.NUM_WORD_LENGTH_OPTIONS][];

    public static final int WIN_INDEX = 0, LOSS_INDEX = 1, FORFEIT_INDEX = 2, RESULTS_LENGTH = 3;

    public static void setResults(final int lengthIndex, final String[] results) {
        RESULTS[lengthIndex] = new int[RESULTS_LENGTH];
        
        for (int i = 0; i < RESULTS_LENGTH; i++)
            RESULTS[lengthIndex][i] = Integer.parseInt(results[i]);
    }
    
    public static void setGuesses(final int lengthIndex, final String[] guesses) {
        final int MAX_NUM_GUESSES =
                lengthIndex + WPConstants.INDEX_TO_LENGTH_OFFSET +
                        WPSettings.getMaxGuessToLetterSurplus();

        GUESSES[lengthIndex] = new int[MAX_NUM_GUESSES];

        for (int i = 0; i < GUESSES[lengthIndex].length; i++)
            GUESSES[lengthIndex][i] = Integer.parseInt(guesses[i]);
    }

    public static void addWin(final int lengthIndex) {
        RESULTS[lengthIndex][WIN_INDEX]++;
    }

    public static void addLoss(final int lengthIndex) {
        RESULTS[lengthIndex][LOSS_INDEX]++;
    }

    public static void addForfeit(final int lengthIndex) {
        RESULTS[lengthIndex][FORFEIT_INDEX]++;
    }

    public static void documentGuessAmount(final int lengthIndex, final int numberOfGuesses) {
        final int guessIndex = numberOfGuesses - 1;

        GUESSES[lengthIndex][guessIndex]++;
    }

    public static int[] getResults(final int lengthIndex) {
        return RESULTS[lengthIndex];
    }

    public static int[] getGuesses(final int lengthIndex) {
        return GUESSES[lengthIndex];
    }
}
