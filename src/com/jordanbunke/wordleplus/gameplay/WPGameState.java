package com.jordanbunke.wordleplus.gameplay;

import com.jordanbunke.jbjgl.contexts.ProgramContext;
import com.jordanbunke.jbjgl.debug.GameDebugger;
import com.jordanbunke.jbjgl.image.GameImage;
import com.jordanbunke.jbjgl.io.InputEventLogger;
import com.jordanbunke.jbjgl.menus.Menu;
import com.jordanbunke.jbjgl.text.Text;
import com.jordanbunke.jbjgl.text.TextBuilder;
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

public class WPGameState implements ProgramContext {

    private Menu gameButtons;

    private final int length;
    private final int guessesAllowed;
    private final String goalWord;

    private final Letters letters;
    private final Guess[] guesses;

    private int guessIndex;
    private int timer;

    private FinishStatus finishStatus;
    private int endgameCountdown;

    private GameImage renderState;

    public enum FinishStatus {
        PLAYING, WON, LOST
    }

    private WPGameState(final int length) {
        gameButtons = Menus.generateGameButtons();

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
        renderState = new GameImage(WPConstants.WIDTH, WPConstants.HEIGHT);

        renderState.draw(WPImages.getBackground());

        if (endgameCountdown == 0)
            drawEndgame();
        else {
            drawGuesses();
            drawLetterStatuses();
        }

        renderState.free();
    }

    private void drawEndgame() {
        final int SQUARE_DIM = 72, MARGIN = 12,
                PANEL_WIDTH = (int)(WPConstants.WIDTH * 0.7),
                PANEL_HEIGHT = (int)(WPConstants.HEIGHT * 0.55),
                PANEL_X = (WPConstants.WIDTH / 2) - (PANEL_WIDTH / 2),
                PANEL_Y = 44;

        final boolean winScreen = finishStatus == FinishStatus.WON;

        final Color panelBackground = WPColors.BLACK;
        final Color panelTitleColor = winScreen
                ? WPColors.RIGHT_SPOT
                : WPColors.NOT_PRESENT;

        final GameImage panel = new GameImage(PANEL_WIDTH, PANEL_HEIGHT);
        panel.fillRectangle(panelBackground, 0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        final GameImage title = new TextBuilder(
                4.0, Text.Orientation.CENTER, panelTitleColor, WPFonts.ITALICS_SPACED()
        ).addText("YOU " + (winScreen ? "WON!" : "LOST...")).build().draw();
        panel.draw(title, (PANEL_WIDTH / 2) - (title.getWidth() / 2), 0);

        if (winScreen) {
            final GameImage guessText = getTextLine("Guesses: " + guessIndex);
            panel.draw(guessText, (PANEL_WIDTH / 2) - (guessText.getWidth() / 2), SQUARE_DIM * 2);

            final GameImage timeText = getTextLine("Time: " + formatTime());
            panel.draw(timeText, (PANEL_WIDTH / 2) - (timeText.getWidth() / 2), SQUARE_DIM * 3);
        } else {
            final GameImage correction = getTextLine("The word was:");
            panel.draw(correction, (PANEL_WIDTH / 2) - (correction.getWidth() / 2), SQUARE_DIM * 2);

            final int SOLUTION_WIDTH = (SQUARE_DIM * length) + (MARGIN * (length - 1));

            final GameImage solution = new GameImage(SOLUTION_WIDTH, SQUARE_DIM);

            for (int i = 0; i < length; i++) {
                solution.draw(drawLetter(goalWord.charAt(i), WPColors.RIGHT_SPOT, 3),
                        i * (SQUARE_DIM + MARGIN), 0);
            }

            panel.draw(solution, (PANEL_WIDTH / 2) - (solution.getWidth() / 2), SQUARE_DIM * 3);
        }

        renderState.draw(panel.submit(), PANEL_X, PANEL_Y);
    }

    private String formatTime() {
        final int SECONDS_IN_MIN = 60;
        final int seconds = (int)(timer / WPConstants.UPDATE_HZ);

        if (seconds >= SECONDS_IN_MIN)
            return (seconds / SECONDS_IN_MIN) + "m " + (seconds % SECONDS_IN_MIN) + "s";
        else
            return seconds + "s";
    }

    private GameImage getTextLine(final String text) {
        return new TextBuilder(2.0, Text.Orientation.CENTER, WPColors.WHITE,
                WPFonts.STANDARD()).addText(text).build().draw();
    }

    private void drawLetterStatuses() {
        if (WPSettings.areLettersHidden())
            return;

        final char[] letterChars = new char[] {
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
        };

        final int MARGIN = 8, SQUARE_DIM = 48, COLUMNS = 3,
                ROWS = (int)Math.ceil(letterChars.length / (double)COLUMNS),
                INITIAL_X = WPConstants.WIDTH - (COLUMNS * (MARGIN + SQUARE_DIM)),
                INITIAL_Y = WPConstants.HEIGHT - (ROWS * (MARGIN + SQUARE_DIM)),
                INCREMENT = MARGIN + SQUARE_DIM;

        for (int i = 0; i < letterChars.length; i++) {
            final int x = INITIAL_X + ((i % COLUMNS) * INCREMENT),
                    y = INITIAL_Y + ((i / COLUMNS) * INCREMENT);
            final GameImage letterImage = drawLetter(
                    letterChars[i], letters.getStatus(letterChars[i]).getColor(), 2);

            renderState.draw(letterImage, x, y);
        }
    }

    private GameImage drawLetter(
            final char letter, final Color backgroundColor, final int textSize
    ) {
        final int SQUARE_DIM = 24 * textSize;

        final GameImage letterImage = new GameImage(SQUARE_DIM, SQUARE_DIM);
        letterImage.fillRectangle(backgroundColor, 0, 0, SQUARE_DIM, SQUARE_DIM);

        final GameImage letterText = new TextBuilder(
                textSize, Text.Orientation.LEFT, WPColors.WHITE, WPFonts.STANDARD()
        ).addText(String.valueOf(letter).toUpperCase()).build().draw();

        final int offset = ((SQUARE_DIM / 2) - (letterText.getWidth() / 2)) + 2;

        letterImage.draw(letterText, offset, -4 * textSize);
        return letterImage.submit();
    }

    private void drawGuesses() {
        for (int i = 0; i < guessesAllowed; i++) {
            final Guess guess = guesses[i];
            final boolean submitted = guess.isSubmitted();
            // GUESS_INIT_Y + (i * GUESS_Y_INC);

            final int SQUARE_DIM = 48, MARGIN = 8;
            final int GUESS_INIT_Y = (WPConstants.HEIGHT -
                    ((SQUARE_DIM * guessesAllowed) + (MARGIN * (guessesAllowed - 1)))) / 2;
            final int width = (SQUARE_DIM * length) + (MARGIN * (length - 1));

            final GameImage guessImage = new GameImage(width, SQUARE_DIM);

            final int x = (WPConstants.WIDTH / 2) - (guessImage.getWidth() / 2);
            final int y = GUESS_INIT_Y + (i * (SQUARE_DIM + MARGIN));

            for (int j = 0; j < length; j++) {
                final char letter = guess.getLetterAt(j);
                final int letterX = (SQUARE_DIM + MARGIN) * j;
                final Color backgroundColor = submitted
                        ? getLetterStatus(j, guess.generateWordFromGuess()).getColor()
                        : WPColors.BLACK;

                final GameImage letterImage = drawLetter(letter, backgroundColor, 2);
                guessImage.draw(letterImage, letterX, 0);
            }

            renderState.draw(guessImage.submit(), x, y);
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
    public void update(final double deltaTime) {
        gameButtons.update(deltaTime);

        if (finishStatus == FinishStatus.PLAYING)
            timer++;

        if (endgameCountdown > 0) {
            endgameCountdown--;

            if (endgameCountdown == 0) {
                gameButtons = Menus.generateEndgameButtons();
                draw();
            }
        }
    }

    @Override
    public void render(final GameImage canvas) {
        canvas.draw(renderState, 0, 0);

        gameButtons.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {
        gameButtons.debugRender(canvas, debugger);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        // pass on for the buttons in the game state
        gameButtons.process(eventLogger);

        for (ControlScheme.Action action : ControlScheme.Action.values())
            eventLogger.checkForMatchingKeyStroke(
                    ControlScheme.getKeyEvent(action), action::behaviour);
    }
}
