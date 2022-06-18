package com.jordanbunke.wordleplus.utility;

import com.jordanbunke.jbjgl.fonts.Font;
import com.jordanbunke.jbjgl.fonts.FontFamily;

import java.nio.file.Path;
import java.nio.file.Paths;

public class WPFonts {
    private static final Path FONT_FOLDER = Paths.get("resources", "font-files");
    private static final FontFamily CLASSIC = FontFamily.loadFromSources(
            "Classic", FONT_FOLDER,
            "font-classic", FontFamily.NOT_AVAILABLE, "font-classic-italics",
            2, 2, 1, true
    );
    private static final Font CLASSIC_ITALICS_SPACED = Font.loadFromSource(
            FONT_FOLDER, "font-classic-italics", true, 2
    );

    public static Font STANDARD() {
        return CLASSIC.getStandard();
    }

    public static Font ITALICS() {
        return CLASSIC.getItalics();
    }

    public static Font ITALICS_SPACED() {
        return CLASSIC_ITALICS_SPACED;
    }
}
