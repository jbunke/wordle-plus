package com.jordanbunke.wordleplus.io;

import com.jordanbunke.jbjgl.events.GameKeyEvent;
import com.jordanbunke.jbjgl.events.Key;
import com.jordanbunke.wordleplus.WordlePlus;
import com.jordanbunke.wordleplus.gameplay.Guess;

public class ControlScheme {
    public enum Action {
        BACKSPACE, ATTEMPT_SUBMIT,
        TYPE_A, TYPE_B, TYPE_C, TYPE_D,
        TYPE_E, TYPE_F, TYPE_G, TYPE_H,
        TYPE_I, TYPE_J, TYPE_K, TYPE_L,
        TYPE_M, TYPE_N, TYPE_O, TYPE_P,
        TYPE_Q, TYPE_R, TYPE_S, TYPE_T,
        TYPE_U, TYPE_V, TYPE_W, TYPE_X,
        TYPE_Y, TYPE_Z;

        GameKeyEvent getKeyEvent() {
            return switch (this) {
                case BACKSPACE -> GameKeyEvent.newKeyStroke(Key.BACKSPACE, GameKeyEvent.Action.PRESS);
                case ATTEMPT_SUBMIT -> GameKeyEvent.newKeyStroke(Key.ENTER, GameKeyEvent.Action.PRESS);
                default -> GameKeyEvent.newTypedKey(Character.toLowerCase(name().charAt("TYPE_".length())));
            };
        }

        public void behaviour() {
            final Guess guess = WordlePlus.gameState.getGuess();

            switch (this) {
                case BACKSPACE -> {
                    if (WordlePlus.gameState.isPlaying())
                        guess.backspace();
                }
                case ATTEMPT_SUBMIT -> WordlePlus.gameState.attemptSubmit();
                default -> {
                    if (WordlePlus.gameState.isPlaying()) {
                        final char c = name().substring("TYPE_".length()).toLowerCase().charAt(0);
                        guess.addToGuess(c);
                    }
                }
            }

            WordlePlus.gameState.draw();
        }
    }

    public static GameKeyEvent getKeyEvent(final Action action) {
        return action.getKeyEvent();
    }
}
