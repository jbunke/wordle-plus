package com.jordanbunke.wordleplus.io;

import com.jordanbunke.jbjgl.io.JBJGLFileIO;
import com.jordanbunke.jbjgl.io.JBJGLResourceLoader;
import com.jordanbunke.jbjgl.utility.JBJGLGlobal;
import com.jordanbunke.wordleplus.*;

import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class WPParserWriter {
    public static final String NOT_FOUND = "!!!";

    private static final Path GAME_DATA_ROOT = Path.of("game_data");
    private static final Path SETTINGS_FILE = GAME_DATA_ROOT.resolve(Path.of("settings", "settings.txt"));
    private static final Path
            RECENT_FILE = GAME_DATA_ROOT.resolve(Path.of("recent", "recent.txt")),
            ALT_RECENT_FILE = WPResources.getAltFolder().resolve("recent.txt");
    private static final Path STATS_FOLDER = GAME_DATA_ROOT.resolve("stats"),
            GUESSES_FILE = STATS_FOLDER.resolve("guesses.txt"),
            ALT_GUESSES_FILE = WPResources.getAltFolder().resolve("guesses.txt"),
            RESULTS_FILE = STATS_FOLDER.resolve("results.txt"),
            ALT_RESULTS_FILE = WPResources.getAltFolder().resolve("results.txt");

    private static final String EMPTY = "",
            CONTENT_FOLLOWING = ":",
            BIG_OPEN = "{", BIG_CLOSE = "}",
            BIG_SEP = ";",
            NEW_LINE = "\n", TAB = "\t";

    private static final String
            WORD_LENGTH = "word-length",
            SURPLUS_GUESSES = "surplus-guesses",
            FREQUENCY_EXPONENT = "frequency-exponent";

    private static final int NF = -1;

    // LOAD

    public static void loadSettings() {
        final String text = JBJGLFileIO.readFile(SETTINGS_FILE);

        final int wordLength, surplusGuesses;
        final double freqEx;

        if (text == null || text.equals(EMPTY)) {
            JBJGLGlobal.printMessageToJBJGLChannel("No settings file found. Settings set to defaults.");

            wordLength = WPSettings.getWordLength();
            surplusGuesses = WPSettings.getGuessToLetterSurplus();
            freqEx = WPSettings.getFrequencyExponent();
        } else {
            JBJGLGlobal.printMessageToJBJGLChannel(WPConstants.TITLE + " settings read successfully!");

            wordLength = Integer.parseInt(extractFromTag(WORD_LENGTH, text));
            surplusGuesses = Integer.parseInt(extractFromTag(SURPLUS_GUESSES, text));
            freqEx = Double.parseDouble(extractFromTag(FREQUENCY_EXPONENT, text));
        }

        WPSettings.setWordLength(wordLength);
        WPSettings.setGuessToLetterSurplus(surplusGuesses);
        WPSettings.setFrequencyExponent(freqEx);
    }

    public static void loadRecentWords() {
        wordLengthDependentDataLoader(RECENT_FILE, ALT_RECENT_FILE, WPRecent::setRecentWordsForLength);
    }

    public static void loadStats() {
        loadGuessesStats();
        loadResultsStats();
    }

    private static void loadGuessesStats() {
        wordLengthDependentDataLoader(GUESSES_FILE, ALT_GUESSES_FILE, WPStats::setGuesses);
    }

    private static void loadResultsStats() {
        wordLengthDependentDataLoader(RESULTS_FILE, ALT_RESULTS_FILE, WPStats::setResults);
    }

    private static void wordLengthDependentDataLoader(
            final Path file, final Path altResource,
            BiConsumer<Integer, String[]> setter) {
        String text = JBJGLFileIO.readFile(file);

        if (text == null || text.equals(EMPTY))
            text = JBJGLFileIO.readResource(JBJGLResourceLoader.loadResource(
                    WPResources.class, altResource), altResource.toString());

        for (int i = 0; i < WPConstants.NUM_WORD_LENGTH_OPTIONS; i++) {
            final String tag = (i + WPConstants.INDEX_TO_LENGTH_OFFSET) + "l";
            final String[] toSet = extractFromTag(tag, text).split(BIG_SEP);
            setter.accept(i, toSet);
        }
    }

    // PARSE

    private static String extractFromTag(final String tag, final String text) {
        final String open = tag + CONTENT_FOLLOWING + BIG_OPEN;
        int openIndex = text.indexOf(open);

        if (openIndex == NF)
            return NOT_FOUND;

        openIndex += open.length();

        int relativeCloseIndex = text.substring(openIndex).indexOf(BIG_CLOSE);

        if (relativeCloseIndex == NF)
            return NOT_FOUND;

        return text.substring(openIndex, relativeCloseIndex + openIndex).trim();
    }

    // WRITE

    public static void saveSettings(final boolean reset) {
        final String contents =
                encloseInTag(WORD_LENGTH, String.valueOf(
                        reset ? 5 : WPSettings.getWordLength()
                )) + NEW_LINE +
                encloseInTag(SURPLUS_GUESSES, String.valueOf(
                        reset ? 1 : WPSettings.getGuessToLetterSurplus()
                )) + NEW_LINE +
                encloseInTag(FREQUENCY_EXPONENT, String.valueOf(
                        reset ? 2.0 : WPSettings.getFrequencyExponent()
                )) + NEW_LINE;

        JBJGLFileIO.writeFile(SETTINGS_FILE, contents);
    }

    public static void saveRecentWords(final boolean reset) {
        wordLengthDependentDataSaver(
                reset, RECENT_FILE, (i) -> new String[] { "" },
                WPRecent::getRecentWordsForLength);
    }

    public static void saveStats(final boolean reset) {
        saveGuessesStats(reset);
        saveResultsStats(reset);
    }

    private static void saveGuessesStats(final boolean reset) {
        wordLengthDependentDataSaver(reset, GUESSES_FILE,
                (i) -> listOfNZeroes(i + WPConstants.INDEX_TO_LENGTH_OFFSET +
                        WPSettings.getMaxGuessToLetterSurplus()),
                (i) -> parseArray(WPStats.getGuesses(i)));
    }

    private static void saveResultsStats(final boolean reset) {
        wordLengthDependentDataSaver(reset, RESULTS_FILE,
                (i) -> listOfNZeroes(3),
                (i) -> parseArray(WPStats.getResults(i)));
    }

    private static void wordLengthDependentDataSaver(
            final boolean reset, final Path file,
            final Function<Integer, String[]> resetSetter,
            final Function<Integer, String[]> setter
    ) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < WPConstants.NUM_WORD_LENGTH_OPTIONS; i++) {
            final String tag = (i + WPConstants.INDEX_TO_LENGTH_OFFSET) + "l";
            sb.append(packAndEncloseInTag(tag, reset
                            ? resetSetter.apply(i)
                            : setter.apply(i),
                    false));
            sb.append(NEW_LINE);
        }

        JBJGLFileIO.writeFile(file, sb.toString());
    }

    // BUILD

    private static String[] listOfNZeroes(final int n) {
        final String[] zeroes = new String[n];

        for (int i = 0; i < n; i++)
            zeroes[i] = "0";

        return zeroes;
    }

    private static String[] parseArray(final int[] array) {
        final String[] parsed = new String[array.length];

        for (int i = 0; i < parsed.length; i++)
            parsed[i] = String.valueOf(array[i]);

        return parsed;
    }

    public static String encloseInTag(final String tag, final String content) {
        return tag + CONTENT_FOLLOWING + BIG_OPEN + content + BIG_CLOSE;
    }

    private static String packAndEncloseInTag(
            final String tag, final String[] elements, final boolean format
    ) {
        StringBuilder sb = new StringBuilder();

        pack(elements, sb, BIG_SEP, format);
        return encloseInTag(tag, sb.toString());
    }

    private static void pack(
            final String[] elements, final StringBuilder sb,
            final String separator, final boolean format
    ) {
        for (int i = 0; i < elements.length; i++) {
            if (format) {
                newLineSB(sb);
                tabSB(sb);
            }
            sb.append(elements[i]);
            if (i + 1 < elements.length)
                sb.append(separator);
        }

        if (format)
            newLineSB(sb);
    }

    private static void newLineSB(final StringBuilder sb) {
        sb.append(NEW_LINE);
    }

    private static void tabSB(final StringBuilder sb) {
        sb.append(TAB);
    }
}
