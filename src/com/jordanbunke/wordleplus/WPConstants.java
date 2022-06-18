package com.jordanbunke.wordleplus;

public class WPConstants {
    public static final int WIDTH = 800, HEIGHT = 600;

    public static final double UPDATE_HZ = 60.0, TARGET_FPS = 60.0;

    public static final int FOUR_L = 0, FIVE_L = 1, SIX_L = 2, SEVEN_L = 3;
    public static final int NUM_WORD_LENGTH_OPTIONS = 4;
    public static final int INDEX_TO_LENGTH_OFFSET = 4;

    public static final int[] LENGTHS = new int[] { 4, 5, 6, 7 };
    public static final int RECENT_WORDS_BUFFER_SIZE = 50;
}
