package com.jordanbunke.wordleplus.gameplay;

import com.jordanbunke.jbjgl.contexts.ProgramContext;
import com.jordanbunke.jbjgl.debug.JBJGLGameDebugger;
import com.jordanbunke.jbjgl.image.JBJGLImage;
import com.jordanbunke.jbjgl.io.JBJGLListener;
import com.jordanbunke.jbjgl.menus.JBJGLMenu;
import com.jordanbunke.jbjgl.text.JBJGLText;
import com.jordanbunke.jbjgl.text.JBJGLTextBuilder;
import com.jordanbunke.wordleplus.WPConstants;
import com.jordanbunke.wordleplus.WPRecent;
import com.jordanbunke.wordleplus.WPSettings;
import com.jordanbunke.wordleplus.WPStats;
import com.jordanbunke.wordleplus.io.ControlScheme;
import com.jordanbunke.wordleplus.io.WordList;
import com.jordanbunke.wordleplus.menu.Menus;
import com.jordanbunke.wordleplus.utility.WPColors;
import com.jordanbunke.wordleplus.utility.WPFonts;
import com.jordanbunke.wordleplus.utility.WPImages;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class WPGameState extends ProgramContext {

    private static final int GUESS_INIT_Y = 8, GUESS_Y_INC = 56;

    private static final JBJGLMenu gameButtons = Menus.generateGameButtons();

    private final int length;
    private final int guessesAllowed;
    private final String goalWord;

    private final Letters letters;
    private final Guess[] guesses;

    private int guessIndex;
    private int timer;

    private FinishStatus finishStatus;
    private int endgameCountdown;

    private JBJGLImage renderState;

    public enum FinishStatus {
        PLAYING, WON, LOST
    }

    private WPGameState(final int length) {
        this.length = length;
        guessesAllowed = length + WPSettings.getGuessToLetterSurplus();
        goalWord = WordList.selectGoalWord(length - WPConstants.INDEX_TO_LENGTH_OFFSET);
        WPRecent.updateRecentWordsForLength(length - WPConstants.INDEX_TO_LENGTH_OFFSET, goalWord);

        letters = Letters.initialize();

        guesses = new Guess[guessesAllowed];
        guessIndex = 0;

        timer = 0;

        finishStatus = FinishStatus.PLAYING;
        endgameCountdown = -1;

        initializeGuesses();
        draw();
    }

    public static WPGameState create(final int length) {
        return new WPGameState(length);
    }

    private void initializeGuesses() {
        for (int i = 0; i < guessesAllowed; i++)
            guesses[i] = Guess.initialize(length);
    }

    public void attemptSubmit() {
        if (finishStatus != FinishStatus.PLAYING)
            return;

        final Guess guess = getGuess();
        if (guess.canSubmit())
            submit(guess);
    }

    private void submit(final Guess guess) {
        guess.submit();

        updateLetterStatuses(guess);

        if (guess.generateWordFromGuess().equals(goalWord))
            gameWon();
        else if (guessIndex + 1 >= guessesAllowed)
            gameLost();

        guessIndex++;
    }

    private void gameWon() {
        finishStatus = FinishStatus.WON;

        final int lengthIndex = length - WPConstants.INDEX_TO_LENGTH_OFFSET;
        WPStats.addWin(lengthIndex);
        WPStats.documentGuessAmount(lengthIndex, guessIndex + 1);

        setEndgameCountdown();
    }

    private void gameLost() {
        finishStatus = FinishStatus.LOST;

        final int lengthIndex = length - WPConstants.INDEX_TO_LENGTH_OFFSET;
        WPStats.addLoss(lengthIndex);

        setEndgameCountdown();
    }

    private void setEndgameCountdown() {
        endgameCountdown = 100;
    }

    private void updateLetterStatuses(final Guess guess) {
        for (int i = 0; i < length; i++) {
            final char letter = guess.getLetterAt(i);
            final Letters.Status toSet = getLetterStatus(i, guess.generateWordFromGuess());

            letters.updateLetter(letter, toSet);
        }
    }

    public Guess getGuess() {
        return guesses[guessIndex];
    }

    public int getLength() {
        return length;
    }

    public boolean isPlaying() {
        return finishStatus == FinishStatus.PLAYING;
    }

    public void draw() {
        renderState = JBJGLImage.create(WPConstants.WIDTH, WPConstants.HEIGHT);
        final Graphics g = renderState.getGraphics();

        g.drawImage(WPImages.getBackground(), 0, 0, null);

        drawGuesses(g);

        drawLetterStatuses(g);

        if (endgameCountdown == 0)
            drawEndgame(g);

        g.dispose();
    }

    private void drawEndgame(final Graphics g) {
        final int SQUARE_DIM = 72, MARGIN = 12,
                PANEL_WIDTH = (int)(WPConstants.WIDTH * 0.7),
                PANEL_HEIGHT = (int)(WPConstants.HEIGHT * 0.55),
                PANEL_X = (WPConstants.WIDTH / 2) - (PANEL_WIDTH / 2),
                PANEL_Y = (WPConstants.HEIGHT / 2) - (PANEL_HEIGHT / 2);

        final boolean winScreen = finishStatus == FinishStatus.WON;

        final Color panelBackground = WPColors.BLACK;
        final Color panelTitleColor = winScreen
                ? WPColors.RIGHT_SPOT
                : WPColors.NOT_PRESENT;

        final JBJGLImage panel = JBJGLImage.create(PANEL_WIDTH, PANEL_HEIGHT);
        final Graphics panelG = panel.getGraphics();

        panelG.setColor(panelBackground);
        panelG.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        final JBJGLImage title = JBJGLTextBuilder.initialize(
                4.0, JBJGLText.Orientation.CENTER, panelTitleColor, WPFonts.ITALICS_SPACED()
        ).addText("YOU " + (winScreen ? "WON!" : "LOST...")).build().draw();
        panelG.drawImage(title, (PANEL_WIDTH / 2) - (title.getWidth() / 2), 0, null);

        if (winScreen) {
            final JBJGLImage guessText = getTextLine("Guesses: " + guessIndex);
            panelG.drawImage(guessText, (PANEL_WIDTH / 2) - (guessText.getWidth() / 2),
                    SQUARE_DIM * 2, null);

            final JBJGLImage timeText = getTextLine("Time: " + formatTime());
            panelG.drawImage(timeText, (PANEL_WIDTH / 2) - (timeText.getWidth() / 2),
                    SQUARE_DIM * 3, null);
        } else {
            final JBJGLImage correction = getTextLine("The word was:");
            panelG.drawImage(correction, (PANEL_WIDTH / 2) - (correction.getWidth() / 2),
                    SQUARE_DIM * 2, null);

            final int SOLUTION_WIDTH = (SQUARE_DIM * length) + (MARGIN * (length - 1));

            final JBJGLImage solution = JBJGLImage.create(SOLUTION_WIDTH, SQUARE_DIM);
            final Graphics solutionG = solution.getGraphics();

            for (int i = 0; i < length; i++) {
                solutionG.drawImage(
                        drawLetter(goalWord.charAt(i), WPColors.RIGHT_SPOT, 3),
                        i * (SQUARE_DIM + MARGIN), 0, null);
            }

            panelG.drawImage(solution, (PANEL_WIDTH / 2) - (solution.getWidth() / 2),
                    SQUARE_DIM * 3, null);
        }

        panelG.dispose();
        g.drawImage(panel, PANEL_X, PANEL_Y, null);
    }

    private String formatTime() {
        final int SECONDS_IN_MIN = 60;
        final int seconds = (int)(timer / WPConstants.UPDATE_HZ);

        if (seconds >= SECONDS_IN_MIN)
            return (seconds / SECONDS_IN_MIN) + "m " + (seconds % SECONDS_IN_MIN) + "s";
        else
            return seconds + "s";
    }

    private JBJGLImage getTextLine(final String text) {
        return JBJGLTextBuilder.initialize(
                2.0, JBJGLText.Orientation.CENTER, WPColors.WHITE, WPFonts.STANDARD()
        ).addText(text).build().draw();
    }

    private void drawLetterStatuses(final Graphics g) {
        final char[] letterChars = new char[] {
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
        };

        final int MARGIN = 8, SQUARE_DIM = 48, COLUMNS = 3,
                INITIAL_X = WPConstants.WIDTH - (COLUMNS * (MARGIN + SQUARE_DIM)),
                INITIAL_Y = MARGIN + GUESS_Y_INC,
                INCREMENT = MARGIN + SQUARE_DIM;

        for (int i = 0; i < letterChars.length; i++) {
            final int x = INITIAL_X + ((i % COLUMNS) * INCREMENT),
                    y = INITIAL_Y + ((i / COLUMNS) * INCREMENT);
            final JBJGLImage letterImage = drawLetter(
                    letterChars[i], letters.getStatus(letterChars[i]).getColor(), 2);

            g.drawImage(letterImage, x, y, null);
        }
    }

    private JBJGLImage drawLetter(
            final char letter, final Color backgroundColor, final int textSize
    ) {
        final int SQUARE_DIM = 24 * textSize;
        final JBJGLImage letterImage = JBJGLImage.create(SQUARE_DIM, SQUARE_DIM);
        final Graphics g = letterImage.getGraphics();

        final Color letterColor = WPColors.WHITE;

        g.setColor(backgroundColor);
        g.fillRect(0, 0, SQUARE_DIM, SQUARE_DIM);

        final JBJGLImage letterText = JBJGLTextBuilder.initialize(
                (double)textSize, JBJGLText.Orientation.LEFT,
                letterColor, WPFonts.STANDARD()
        ).addText(String.valueOf(letter).toUpperCase()).build().draw();

        final int offset = ((SQUARE_DIM / 2) - (letterText.getWidth() / 2)) + 2;

        g.drawImage(letterText, offset, -4 * textSize, null);
        g.dispose();

        return letterImage;
    }

    private void drawGuesses(final Graphics g) {
        for (int i = 0; i < guessesAllowed; i++) {
            final Guess guess = guesses[i];
            final boolean submitted = guess.isSubmitted();
            final int Y = GUESS_INIT_Y + (i * GUESS_Y_INC);

            final int SQUARE_DIM = 48, MARGIN = 8;
            final int width = (SQUARE_DIM * length) + (MARGIN * (length - 1));

            final JBJGLImage guessImage = JBJGLImage.create(width, SQUARE_DIM);
            final Graphics guessG = guessImage.getGraphics();

            final int X = (WPConstants.WIDTH / 2) - (guessImage.getWidth() / 2);

            for (int j = 0; j < length; j++) {
                final char letter = guess.getLetterAt(j);
                final int letterX = (SQUARE_DIM + MARGIN) * j;
                final Color backgroundColor = submitted
                        ? getLetterStatus(j, guess.generateWordFromGuess()).getColor()
                        : WPColors.BLACK;

                final JBJGLImage letterImage = drawLetter(letter, backgroundColor, 2);
                guessG.drawImage(letterImage, letterX, 0, null);
            }

            guessG.dispose();

            g.drawImage(guessImage, X, Y, null);
        }
    }

    private Letters.Status getLetterStatus(final int index, final String guessWord) {
        if (goalWord.charAt(index) == guessWord.charAt(index))
            return Letters.Status.RIGHT_SPOT;
        else if (letterInWrongSpot(index, guessWord))
            return Letters.Status.WRONG_SPOT;
        else
            return Letters.Status.NOT_PRESENT;
    }

    private boolean letterInWrongSpot(final int index, final String guessWord) {
        final char c = guessWord.charAt(index);
        final Set<Integer> occurrences = new HashSet<>();

        for (int i = 0; i < goalWord.length(); i++)
            if (goalWord.charAt(i) == c)
                occurrences.add(i);

        boolean allOccurrencesMatch = true;

        for (Integer occurrence : occurrences)
            allOccurrencesMatch &= guessWord.charAt(occurrence) == c;

        int previousOccurrencesInGuess = 0;

        for (int i = 0; i < index; i++)
            if (guessWord.charAt(i) == c)
                previousOccurrencesInGuess++;

        return !allOccurrencesMatch && previousOccurrencesInGuess < occurrences.size();
    }

    @Override
    public void update() {
        gameButtons.update();

        if (finishStatus == FinishStatus.PLAYING)
            timer++;

        if (endgameCountdown > 0) {
            endgameCountdown--;

            if (endgameCountdown == 0)
                draw();
        }
    }

    @Override
    public void render(final Graphics g, final JBJGLGameDebugger debugger) {
        g.drawImage(renderState, 0, 0, null);

        gameButtons.render(g, debugger);
    }

    @Override
    public void process(final JBJGLListener listener) {
        // pass on for the buttons in the game state
        gameButtons.process(listener, null);

        for (ControlScheme.Action action : ControlScheme.Action.values())
            listener.checkForMatchingKeyStroke(
                    ControlScheme.getKeyEvent(action), action::behaviour);
    }
}
