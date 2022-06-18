package com.jordanbunke.wordleplus.io;

import com.jordanbunke.jbjgl.events.JBJGLKey;
import com.jordanbunke.jbjgl.events.JBJGLKeyEvent;
import com.jordanbunke.wordleplus.WordlePlus;
import com.jordanbunke.wordleplus.gameplay.Guess;

public class ControlScheme {
    private static final JBJGLKeyEvent.Action KEYSTROKE_ACTION = JBJGLKeyEvent.Action.PRESS;

    public enum Action {
        BACKSPACE, ATTEMPT_SUBMIT,
        TYPE_A, TYPE_B, TYPE_C, TYPE_D,
        TYPE_E, TYPE_F, TYPE_G, TYPE_H,
        TYPE_I, TYPE_J, TYPE_K, TYPE_L,
        TYPE_M, TYPE_N, TYPE_O, TYPE_P,
        TYPE_Q, TYPE_R, TYPE_S, TYPE_T,
        TYPE_U, TYPE_V, TYPE_W, TYPE_X,
        TYPE_Y, TYPE_Z;

        JBJGLKeyEvent getKeyEvent() {
            return switch (this) {
                case BACKSPACE -> JBJGLKeyEvent.generate(JBJGLKey.BACKSPACE, KEYSTROKE_ACTION);
                case ATTEMPT_SUBMIT -> JBJGLKeyEvent.generate(JBJGLKey.ENTER, KEYSTROKE_ACTION);
                default -> JBJGLKeyEvent.generate(JBJGLKey.valueOf(
                        this.name().substring("TYPE_".length())
                ), KEYSTROKE_ACTION);
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

    public static JBJGLKeyEvent getKeyEvent(final Action action) {
        return action.getKeyEvent();
    }
}
