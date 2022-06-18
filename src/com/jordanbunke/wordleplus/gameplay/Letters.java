package com.jordanbunke.wordleplus.gameplay;

import com.jordanbunke.wordleplus.utility.WPColors;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Letters {
    public enum Status {
        NOT_TRIED, NOT_PRESENT, WRONG_SPOT, RIGHT_SPOT;

        public Color getColor() {
            return switch (this) {
                case NOT_TRIED -> WPColors.NOT_TRIED;
                case NOT_PRESENT -> WPColors.NOT_PRESENT;
                case WRONG_SPOT -> WPColors.WRONG_SPOT;
                case RIGHT_SPOT -> WPColors.RIGHT_SPOT;
            };
        }
    }

    private static final Map<Character, Status> INITIAL_MAP = Map.ofEntries(
            Map.entry('a', Status.NOT_TRIED),
            Map.entry('b', Status.NOT_TRIED),
            Map.entry('c', Status.NOT_TRIED),
            Map.entry('d', Status.NOT_TRIED),
            Map.entry('e', Status.NOT_TRIED),
            Map.entry('f', Status.NOT_TRIED),
            Map.entry('g', Status.NOT_TRIED),
            Map.entry('h', Status.NOT_TRIED),
            Map.entry('i', Status.NOT_TRIED),
            Map.entry('j', Status.NOT_TRIED),
            Map.entry('k', Status.NOT_TRIED),
            Map.entry('l', Status.NOT_TRIED),
            Map.entry('m', Status.NOT_TRIED),
            Map.entry('n', Status.NOT_TRIED),
            Map.entry('o', Status.NOT_TRIED),
            Map.entry('p', Status.NOT_TRIED),
            Map.entry('q', Status.NOT_TRIED),
            Map.entry('r', Status.NOT_TRIED),
            Map.entry('s', Status.NOT_TRIED),
            Map.entry('t', Status.NOT_TRIED),
            Map.entry('u', Status.NOT_TRIED),
            Map.entry('v', Status.NOT_TRIED),
            Map.entry('w', Status.NOT_TRIED),
            Map.entry('x', Status.NOT_TRIED),
            Map.entry('y', Status.NOT_TRIED),
            Map.entry('z', Status.NOT_TRIED)
    );

    private final Map<Character, Status> letterStatusMap;

    private Letters() {
        this.letterStatusMap = new HashMap<>(INITIAL_MAP);
    }

    public static Letters initialize() {
        return new Letters();
    }

    public void updateLetter(final char letter, final Status toSet) {
        final Status current = letterStatusMap.get(letter);

        if (toSet.ordinal() > current.ordinal())
            letterStatusMap.replace(letter, toSet);
    }

    public Status getStatus(final char letter) {
        return letterStatusMap.getOrDefault(letter, Status.NOT_PRESENT);
    }
}
