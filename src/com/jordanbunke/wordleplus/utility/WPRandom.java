package com.jordanbunke.wordleplus.utility;

import java.util.List;
import java.util.Random;

public class WPRandom {
    private static final Random r = new Random();

    public static <T> T randomElementFromList(final List<T> list) {
        if (list.size() == 0)
            return null;

        return list.get(boundedRandom(0, list.size()));
    }

    public static <T> T randomElementFromArray(final T[] array) {
        if (array.length == 0)
            return null;

        return array[boundedRandom(0, array.length)];
    }

    public static int boundedRandomPower(final int min, final int max, final double power) {
        return bounded(min, max, Math.pow(r.nextDouble(), power));
    }

    public static int boundedRandom(final int min, final int max) {
        return bounded(min, max, r.nextDouble());
    }

    public static int bounded(final int min, final int max, final double fromMinToMax) {
        return min + (int)(fromMinToMax * (max - min));
    }

    public static <T> T coinToss(final T heads, final T tails) {
        return coinToss(0.5, heads, tails);
    }

    public static <T> T coinToss(final double headProb, final T heads, final T tails) {
        return r.nextDouble() < headProb ? heads : tails;
    }
}
