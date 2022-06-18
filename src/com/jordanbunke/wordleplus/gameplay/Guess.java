package com.jordanbunke.wordleplus.gameplay;

import com.jordanbunke.wordleplus.WPConstants;
import com.jordanbunke.wordleplus.io.WordList;

public class Guess {
    private static final char BLANK = ' ';
    private final int length;
    private final char[] letters;

    private boolean submitted;
    private int lettersProcessed;

    private Guess(final int length) {
        this.length = length;
        this.letters = new char[length];
        initializeLetters();
    }

    public static Guess initialize(final int length) {
        return new Guess(length);
    }

    private void initializeLetters() {
        for (int i = 0; i < length; i++)
            letters[i] = BLANK;
    }

    public void addToGuess(final char letter) {
        if (isSubmitted())
            return;

        if (lettersProcessed < length) {
            letters[lettersProcessed] = letter;
            lettersProcessed++;
        }
    }

    public void backspace() {
        if (isSubmitted())
            return;

        if (lettersProcessed > 0) {
            lettersProcessed--;
            letters[lettersProcessed] = BLANK;
        }
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public boolean canSubmit() {
        return lettersProcessed == length && !isSubmitted() &&
                WordList.isInWordList(generateWordFromGuess(),
                        length - WPConstants.INDEX_TO_LENGTH_OFFSET);
    }

    public void submit() {
        submitted = true;
    }

    public String generateWordFromGuess() {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < lettersProcessed; i++)
            sb.append(letters[i]);

        return sb.toString();
    }

    public char getLetterAt(final int index) {
        return letters[index];
    }
}
