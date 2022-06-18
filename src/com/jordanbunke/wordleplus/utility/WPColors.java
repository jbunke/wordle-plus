package com.jordanbunke.wordleplus.utility;

import java.awt.*;

public class WPColors {
    private static final int OPAQUE = 255;
    private static final int FULL = 255, EMPTY = 0;

    public static final Color NOT_PRESENT = new Color(75, 75, 75, OPAQUE);
    public static final Color NOT_TRIED = new Color(150, 150, 150, OPAQUE);
    public static final Color RIGHT_SPOT = new Color(50, 150, 50, OPAQUE);
    public static final Color WRONG_SPOT = new Color(150, 150, 0, OPAQUE);

    public static final Color BLACK = new Color(EMPTY, EMPTY, EMPTY, OPAQUE);
    public static final Color WHITE = new Color(FULL, FULL, FULL, OPAQUE);

    public static final Color BACKGROUND = new Color(200, 200, 200, OPAQUE);
}
