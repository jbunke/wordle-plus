package com.jordanbunke.wordleplus.utility;

import com.jordanbunke.jbjgl.fonts.Font;
import com.jordanbunke.wordleplus.WPResources;

import java.nio.file.Path;

public class WPFonts {
    private static final Path FONT_FOLDER = WPResources.getFontFolder();

    private static final Font
            STANDARD = Font.loadFromSource(FONT_FOLDER, WPResources.class,
            "font-classic", true, 1.0, 2, false),
//            "font-hand-drawn", false, 1.0, 2, false),
            ITALICS_SPACED = Font.loadFromSource(FONT_FOLDER, WPResources.class,
        "font-classic-italics", true, 1.0, 2, false);
//        "font-hand-drawn", false, 1.0, 6, false);

    public static Font STANDARD() {
        return STANDARD;
    }

    public static Font ITALICS_SPACED() {
        return ITALICS_SPACED;
    }
}
