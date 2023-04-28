package com.jordanbunke.wordleplus;

import java.nio.file.Path;

public class WPResources {

    public static Path getFontFolder() {
        return Path.of("font_files");
    }

    public static Path getIconFolder() {
        return Path.of("icons");
    }

    public static Path getWordListFolder() {
        return Path.of("word_lists");
    }

    public static Path getAltFolder() {
        return Path.of("alt");
    }
}
