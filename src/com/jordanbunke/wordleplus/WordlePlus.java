package com.jordanbunke.wordleplus;

import com.jordanbunke.jbjgl.OnStartup;
import com.jordanbunke.jbjgl.contexts.MenuManager;
import com.jordanbunke.jbjgl.debug.GameDebugger;
import com.jordanbunke.jbjgl.game.Game;
import com.jordanbunke.jbjgl.game.GameEngine;
import com.jordanbunke.jbjgl.game.GameManager;
import com.jordanbunke.wordleplus.gameplay.WPGameState;
import com.jordanbunke.wordleplus.io.WPParserWriter;
import com.jordanbunke.wordleplus.menu.Menus;
import com.jordanbunke.wordleplus.utility.WPImages;

public class WordlePlus {
    public static final int MENU_STATE_INDEX = 0, GAME_STATE_INDEX = 1;

    public static MenuManager menuManager;
    public static WPGameState gameState;

    public static GameManager manager;
    public static GameDebugger debugger;
    public static GameEngine gameEngine;
    public static Game game;

    public static void main(String[] args) {
        OnStartup.run();

        processArgs(args);
        launch();
    }

    private static void processArgs(final String[] args) {
        final String RESET_ARG = "r", INCREMENT_BUILD_VERSION_ARG = "ibv";

        boolean resetFlag = false, incrementBuildVersionFlag = false;

        for (String arg : args)
            if (arg.equals(RESET_ARG)) {
                resetFlag = true;
            } else if (arg.equals(INCREMENT_BUILD_VERSION_ARG)) {
                incrementBuildVersionFlag = true;
            }

        if (incrementBuildVersionFlag)
            incrementBuildVersion();

        if (resetFlag)
            resetGameData();
        else
            loadGameData();
    }

    private static void launch() {
        menuManager = Menus.initializeMenus();
        gameState = generateNewPuzzle();

        manager = new GameManager(MENU_STATE_INDEX,
                menuManager, gameState);
        game = Game.assemble(WPConstants.TITLE, manager,
                WPConstants.WIDTH, WPConstants.HEIGHT,
                WPImages.getIcon(), true, false,
                WPConstants.UPDATE_HZ, WPConstants.TARGET_FPS);
        gameEngine = game.getGameEngine();
        debugger = gameEngine.getDebugger();

        debugger.hideBoundingBoxes();
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

    public static void drawGameSafely() {
        if (gameState != null)
            gameState.draw();
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

    private static void resetGameData() {
        saveGameData(true);
        loadGameData();
    }

    private static void incrementBuildVersion() {
        WPConstants.VERSION.incrementBuild();
        WPConstants.writeInfoFile();
    }
}
