package com.jordanbunke.wordleplus;

import com.jordanbunke.jbjgl.io.FileIO;
import com.jordanbunke.jbjgl.io.ResourceLoader;
import com.jordanbunke.jbjgl.utility.StringProcessing;
import com.jordanbunke.jbjgl.utility.Version;
import com.jordanbunke.wordleplus.io.WPParserWriter;

import java.nio.file.Path;

public class WPConstants {
    public static final int WIDTH = 800, HEIGHT = 600;

    public static final double UPDATE_HZ = 60.0, TARGET_FPS = 60.0;

    public static final int FOUR_L = 0, FIVE_L = 1, SIX_L = 2, SEVEN_L = 3;
    public static final int NUM_WORD_LENGTH_OPTIONS = 4;
    public static final int INDEX_TO_LENGTH_OFFSET = 4;

    public static final int[] LENGTHS = new int[] { 4, 5, 6, 7 };
    public static final int RECENT_WORDS_BUFFER_SIZE = 50;

    private static final String INFO_FILENAME = "wordle_plus_info.txt", CODEBASE_RESOURCE_ROOT = "res",
            TITLE_TAG = "title", VERSION_TAG = "version";

    public static final String TITLE;
    public static final Version VERSION;

    static {
        final String SEPARATOR = ":", OPEN = "{", CLOSE = "}";
        final int HAS_BUILD_LENGTH = 4, MAJOR = 0, MINOR = 1, PATCH = 2, BUILD = 3;

        final Path INFO_FILE = Path.of(INFO_FILENAME);
        final String contents = FileIO.readResource(ResourceLoader.loadResource(INFO_FILE), INFO_FILENAME);

        TITLE = StringProcessing.getContentsFromTag(contents,
                TITLE_TAG, SEPARATOR, OPEN, CLOSE, "failed");

        final String[] versionInfo = StringProcessing.getContentsFromTag(contents,
                VERSION_TAG, SEPARATOR, OPEN, CLOSE, "1.0.0").split("\\.");

        if (versionInfo.length == HAS_BUILD_LENGTH)
            VERSION = new Version(Integer.parseInt(versionInfo[MAJOR]),
                    Integer.parseInt(versionInfo[MINOR]), Integer.parseInt(versionInfo[PATCH]),
                    Integer.parseInt(versionInfo[BUILD]));
        else
            VERSION = new Version(Integer.parseInt(versionInfo[MAJOR]),
                    Integer.parseInt(versionInfo[MINOR]), Integer.parseInt(versionInfo[PATCH]));
    }

    public static void writeInfoFile() {
        final String[] infoFileContents = new String[] {
                WPParserWriter.encloseInTag(TITLE_TAG, TITLE),
                WPParserWriter.encloseInTag(VERSION_TAG, VERSION.toString()),
                ""
        };

        FileIO.writeFile(Path.of(CODEBASE_RESOURCE_ROOT, INFO_FILENAME), infoFileContents);
    }
}
