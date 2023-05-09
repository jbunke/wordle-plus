package com.jordanbunke.wordleplus.menu;

import com.jordanbunke.jbjgl.contexts.JBJGLMenuManager;
import com.jordanbunke.jbjgl.menus.JBJGLMenu;
import com.jordanbunke.jbjgl.menus.menu_elements.JBJGLMenuElement;
import com.jordanbunke.jbjgl.menus.menu_elements.JBJGLMenuElementGrouping;
import com.jordanbunke.wordleplus.WPConstants;
import com.jordanbunke.wordleplus.WPSettings;
import com.jordanbunke.wordleplus.WPStats;
import com.jordanbunke.wordleplus.WordlePlus;
import com.jordanbunke.wordleplus.io.WPParserWriter;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class Menus {
    public static JBJGLMenuManager initializeMenus() {
        return JBJGLMenuManager.initialize(
                generateMainMenu(), MenuIDs.MAIN_MENU);
    }

    // SPECIFIC MENUS

    private static JBJGLMenu generateMainMenu() {
        return JBJGLMenu.of(
                // visual render order
                MenuHelper.generateBackground(),
                MenuHelper.generateGameTitle(),
                MenuHelper.generateListMenuOptions(
                        new String[] { "PLAY", "SETTINGS", "STATS", "QUIT" },
                        new Runnable[] {
                                () -> WordlePlus.manager.setActiveStateIndex(
                                        WordlePlus.GAME_STATE_INDEX),
                                () -> MenuHelper.linkMenu(MenuIDs.SETTINGS, generateSettingsMenu()),
                                () -> MenuHelper.linkMenu(MenuIDs.STATS, generateStatsMenu()),
                                WordlePlus::quitGame
                        }, 0),
                MenuHelper.generateVersionInfo()
        );
    }

    public static JBJGLMenu generateSettingsMenu() {
        JBJGLMenuElementGrouping buttons;

        try {
            buttons = MenuHelper.generateSettingsList(
                    new String[] {
                            "WORD LENGTH",
                            "SURPLUS GUESSES",
                            "FREQUENCY EXPONENT"
                    },
                    new Callable[] {
                            WPSettings::getWordLength,
                            WPSettings::getGuessToLetterSurplus,
                            WPSettings::getFrequencyExponent
                    },
                    new Callable[] {
                            WPSettings::getMinWordLength,
                            WPSettings::getMinGuessToLetterSurplus,
                            WPSettings::getMinFrequencyExponent
                    },
                    new Callable[] {
                            WPSettings::getMaxWordLength,
                            WPSettings::getMaxGuessToLetterSurplus,
                            WPSettings::getMaxFrequencyExponent
                    },
                    new Consumer[] {
                            (n) -> WPSettings.setWordLength((Double)n),
                            (n) -> WPSettings.setGuessToLetterSurplus((Double)n),
                            (n) -> WPSettings.setFrequencyExponent((Double)n)
                    },
                    new double[] { 1., 1., 0.5 },
                    0
            );
        } catch (Exception e) {
            buttons = JBJGLMenuElementGrouping.generateOf();
        }

        final JBJGLMenuElement resetSettingsButton = MenuHelper.generateTopRightCornerButton(
                "RESET...", () -> {
                    WPParserWriter.saveSettings(true);
                    WPParserWriter.loadSettings();
                    MenuHelper.linkMenu(MenuIDs.SETTINGS, generateSettingsMenu());
                    WordlePlus.reloadGame(true);
                }, WPConstants.WIDTH / 4);

        return generateBasicMenu("Settings",
                JBJGLMenuElementGrouping.generateOf(buttons, resetSettingsButton),
                MenuIDs.MAIN_MENU);
    }

    private static JBJGLMenu generateStatsMenu() {
        final JBJGLMenuElementGrouping buttons = MenuHelper.generateListMenuOptions(
                new String[] {
                        "4-LETTER WORDS",
                        "5-LETTER WORDS",
                        "6-LETTER WORDS",
                        "7-LETTER WORDS"
                }, new Runnable[] {
                        () -> MenuHelper.linkMenu(MenuIDs.WORD_LENGTH_STATS,
                                generateWordLengthStatsMenu(WPConstants.FOUR_L)),
                        () -> MenuHelper.linkMenu(MenuIDs.WORD_LENGTH_STATS,
                                generateWordLengthStatsMenu(WPConstants.FIVE_L)),
                        () -> MenuHelper.linkMenu(MenuIDs.WORD_LENGTH_STATS,
                                generateWordLengthStatsMenu(WPConstants.SIX_L)),
                        () -> MenuHelper.linkMenu(MenuIDs.WORD_LENGTH_STATS,
                                generateWordLengthStatsMenu(WPConstants.SEVEN_L)),
                }, 0);
        final JBJGLMenuElement resetStatsButton = MenuHelper.generateTopRightCornerButton(
                "RESET...", () -> MenuHelper.linkMenu(MenuIDs.YES_NO, generateResetStatsYesNoMenu()),
                WPConstants.WIDTH / 4);

        return generateBasicMenu("Stats",
                JBJGLMenuElementGrouping.generateOf(buttons, resetStatsButton),
                MenuIDs.MAIN_MENU);
    }

    private static JBJGLMenu generateWordLengthStatsMenu(final int lengthIndex) {
        final String title = (lengthIndex + WPConstants.INDEX_TO_LENGTH_OFFSET) + "-letter words";
        final boolean canGoLower = lengthIndex > WPConstants.FOUR_L;
        final boolean canGoHigher = lengthIndex < WPConstants.SEVEN_L;

        final int[] guesses = WPStats.getGuesses(lengthIndex);
        final int[] results = WPStats.getResults(lengthIndex);

        final JBJGLMenuElementGrouping elements = JBJGLMenuElementGrouping.generateOf(
                canGoLower
                        ? MenuHelper.generatePreviousButton(
                                "< " + ((lengthIndex + WPConstants.INDEX_TO_LENGTH_OFFSET) - 1),
                        () -> MenuHelper.linkMenu(MenuIDs.WORD_LENGTH_STATS,
                                generateWordLengthStatsMenu(lengthIndex - 1)))
                        : JBJGLMenuElementGrouping.generateOf(),
                canGoHigher
                        ? MenuHelper.generateNextButton(
                                ((lengthIndex + WPConstants.INDEX_TO_LENGTH_OFFSET) + 1) + " >",
                        () -> MenuHelper.linkMenu(MenuIDs.WORD_LENGTH_STATS,
                                generateWordLengthStatsMenu(lengthIndex + 1)))
                        : JBJGLMenuElementGrouping.generateOf(),
                MenuHelper.generateGuessGraph(guesses),
                MenuHelper.generateResultsSummary(results)
        );

        return generateBasicMenu(title, elements, MenuIDs.STATS);
    }

    private static JBJGLMenu generateResetStatsYesNoMenu() {
        return generateYesNoMenu(
                "RESET ALL STATS?",
                () -> {
                    WPParserWriter.saveStats(true);
                    WPParserWriter.loadStats();
                    MenuHelper.linkMenu(MenuIDs.NOTIFICATION,
                            generateNotificationPage("ALL STATS HAVE BEEN RESET.", MenuIDs.MAIN_MENU));
                }, MenuIDs.STATS);
    }

    // REUSABLE

    private static JBJGLMenu generateNotificationPage(
            final String notification, final String okayMenuID
    ) {
        final JBJGLMenuElementGrouping elements =
                MenuHelper.generateNotificationElements(notification, okayMenuID);

        return generateBasicMenu(" ", null, elements);
    }

    private static JBJGLMenu generateYesNoMenu(
            final String prompt, final Runnable yesBehaviour, final String noBackMenuID
    ) {
        final JBJGLMenuElementGrouping elements =
                MenuHelper.generateYesNoElements(prompt, yesBehaviour, noBackMenuID);

        return generateBasicMenu(" ", null, elements);
    }

    private static JBJGLMenu generateBasicMenu(
            final String title, final JBJGLMenuElementGrouping elements, final String backMenuID
    ) {
        return generateBasicMenu(title, () -> MenuHelper.linkMenu(backMenuID), elements);
    }

    private static JBJGLMenu generateBasicMenu(
            final String title, final Runnable backBehaviour, final JBJGLMenuElementGrouping elements
    ) {
        final JBJGLMenuElement maybeBackButton = backBehaviour == null
                        ? JBJGLMenuElementGrouping.generateOf()
                        : MenuHelper.generateBackButton(backBehaviour);

        return JBJGLMenu.of(
                // visual render order
                MenuHelper.generateBackground(),
                maybeBackButton,
                MenuHelper.generateMenuTitle(title),
                elements);
    }

    // SPECIAL

    public static JBJGLMenu generateGameButtons() {
        return JBJGLMenu.of(
                MenuHelper.generateBackButton(() -> {
                    WordlePlus.manager.setActiveStateIndex(WordlePlus.MENU_STATE_INDEX);
                    WordlePlus.menuManager.setActiveMenuID(MenuIDs.MAIN_MENU);
                }),
                MenuHelper.generateReloadButton(),
                MenuHelper.generateShowHideLettersButton()
        );
    }

    public static JBJGLMenu generateEndgameButtons() {
        return JBJGLMenu.of(
                MenuHelper.generatePlayAgainButton(),
                MenuHelper.generateBackToMenuButton()
        );
    }
}
