package com.jordanbunke.wordleplus;

import com.jordanbunke.jbjgl.contexts.JBJGLMenuManager;
import com.jordanbunke.jbjgl.debug.JBJGLGameDebugger;
import com.jordanbunke.jbjgl.game.JBJGLGame;
import com.jordanbunke.jbjgl.game.JBJGLGameEngine;
import com.jordanbunke.jbjgl.game.JBJGLGameManager;
import com.jordanbunke.jbjgl.io.JBJGLInputTask;
import com.jordanbunke.jbjgl.io.JBJGLListener;
import com.jordanbunke.wordleplus.gameplay.WPGameState;
import com.jordanbunke.wordleplus.io.ControlScheme;
import com.jordanbunke.wordleplus.io.WPParserWriter;
import com.jordanbunke.wordleplus.menu.Menus;
import com.jordanbunke.wordleplus.utility.WPImages;

public class WordlePlus {
    public static final String TITLE = "Wordle+";
    public static final String VERSION = "0.1.3";

    public static final int MENU_STATE_INDEX = 0, GAME_STATE_INDEX = 1;

    public static JBJGLMenuManager menuManager;
    public static WPGameState gameState;

    public static JBJGLGameManager manager;
    public static JBJGLGameDebugger debugger;
    public static JBJGLGameEngine gameEngine;
    public static JBJGLGame game;

    public static void main(String[] args) {
        processArgs(args);
        launch();
    }

    private static void processArgs(final String[] args) {
        final String RESET_ARG = "r";

        boolean resetFlag = false;

        for (String arg : args)
            if (arg.equals(RESET_ARG)) {
                resetFlag = true;
                break;
            }

        if (resetFlag)
            resetGameData();
        else
            loadGameData();
    }

    private static void launch() {
        menuManager = Menus.initializeMenus();
        gameState = generateNewPuzzle();

        manager = JBJGLGameManager.createOf(MENU_STATE_INDEX,
                menuManager, gameState);
        game = JBJGLGame.create(TITLE, manager,
                WPConstants.WIDTH, WPConstants.HEIGHT,
                WPImages.getIcon(),
                true, false, WPConstants.UPDATE_HZ, WPConstants.TARGET_FPS);
        gameEngine = game.getGameEngine();
        debugger = gameEngine.getDebugger();

        debugger.hideBoundingBoxes();

        setControls();
    }

    private static void setControls() {
        final JBJGLListener listener = gameEngine.getWindow().getListener();

        for (ControlScheme.Action action : ControlScheme.Action.values()) {
            final JBJGLInputTask task = JBJGLInputTask.create(
                    () -> ControlScheme.getKeyEvent(action), action::behaviour);

            listener.addTask(task);
        }
    }

    private static void loadGameData() {
        WPParserWriter.loadSettings();
        WPParserWriter.loadRecentWords();
        WPParserWriter.loadStats();
    }

    private static WPGameState generateNewPuzzle() {
        saveGameData(false);

        return WPGameState.create(WPSettings.getWordLength());
    }

    public static void reloadGame(final boolean reloadedFromSettings) {
        if (gameState.isPlaying() && !reloadedFromSettings)
            WPStats.addForfeit(gameState.getLength() - WPConstants.INDEX_TO_LENGTH_OFFSET);

        gameState = generateNewPuzzle();
        manager.setGameStateAtIndex(GAME_STATE_INDEX, gameState);
    }

    public static void quitGame() {
        saveGameData(false);
        System.exit(0);
    }

    private static void saveGameData(final boolean reset) {
        WPParserWriter.saveSettings(reset);
        WPParserWriter.saveRecentWords(reset);
        WPParserWriter.saveStats(reset);
    }

    public static void resetGameData() {
        saveGameData(true);
        loadGameData();
    }
}
