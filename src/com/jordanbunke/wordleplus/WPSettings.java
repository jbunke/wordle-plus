package com.jordanbunke.wordleplus;

import com.jordanbunke.wordleplus.io.WordList;

public class WPSettings {
    private static int WORD_LENGTH = 5;
    private static final int MIN_WL = 4, MAX_WL = 7;

    private static final int[] CANDIDATE_CUTOFFS = new int[] { 1500, 2000, 4000, 4000 };
    private static final int MIN_CUTOFF = 200;

    private static double FREQUENCY_EXPONENT = 2.0;
    private static final double MIN_FE = 1.0, MAX_FE = 3.0;

    private static int GUESS_TO_LETTER_SURPLUS = 1;
    private static final int MIN_GLS = -1, MAX_GLS = 3;

    public static int getWordLength() {
        return WORD_LENGTH;
    }

    public static int getMinWordLength() {
        return MIN_WL;
    }

    public static int getMaxWordLength() {
        return MAX_WL;
    }

    public static int getCandidateCutoff(final int index) {
        return CANDIDATE_CUTOFFS[index];
    }

    public static double getFrequencyExponent() {
        return FREQUENCY_EXPONENT;
    }

    public static double getMinFrequencyExponent() {
        return MIN_FE;
    }

    public static double getMaxFrequencyExponent() {
        return MAX_FE;
    }

    public static int getGuessToLetterSurplus() {
        return GUESS_TO_LETTER_SURPLUS;
    }

    public static int getMinGuessToLetterSurplus() {
        return MIN_GLS;
    }

    public static int getMaxGuessToLetterSurplus() {
        return MAX_GLS;
    }

    public static void setWordLength(final Number wordLength) {
        WORD_LENGTH = boundBy(MIN_WL, wordLength.intValue(), MAX_WL);
    }

    public static void setCandidateCutoff(final int index, final int cutoff) {
        CANDIDATE_CUTOFFS[index] = boundBy(MIN_CUTOFF, cutoff, WordList.getWordListLength(index));
    }

    public static void setFrequencyExponent(final Number frequencyExponent) {
        FREQUENCY_EXPONENT = boundBy(MIN_FE, frequencyExponent.doubleValue(), MAX_FE);
    }

    public static void setGuessToLetterSurplus(final Number guessToLetterSurplus) {
        GUESS_TO_LETTER_SURPLUS = boundBy(MIN_GLS, guessToLetterSurplus.intValue(), MAX_GLS);
    }

    // HELPERS

    private static double boundBy(final double min, final double value, final double max) {
        return Math.max(min, Math.min(value, max));
    }

    private static int boundBy(final int min, final int value, final int max) {
        return Math.max(min, Math.min(value, max));
    }
}
