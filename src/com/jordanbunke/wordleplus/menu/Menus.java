package com.jordanbunke.wordleplus.menu;

import com.jordanbunke.jbjgl.contexts.MenuManager;
import com.jordanbunke.jbjgl.menus.Menu;
import com.jordanbunke.jbjgl.menus.MenuBuilder;
import com.jordanbunke.jbjgl.menus.MenuSelectionLogic;
import com.jordanbunke.jbjgl.menus.menu_elements.MenuElement;
import com.jordanbunke.jbjgl.menus.menu_elements.container.MenuElementGrouping;
import com.jordanbunke.wordleplus.WPConstants;
import com.jordanbunke.wordleplus.WPSettings;
import com.jordanbunke.wordleplus.WPStats;
import com.jordanbunke.wordleplus.WordlePlus;
import com.jordanbunke.wordleplus.io.WPParserWriter;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class Menus {
    public static MenuManager initializeMenus() {
        return new MenuManager(generateMainMenu(), MenuIDs.MAIN_MENU);
    }

    // SPECIFIC MENUS

    private static Menu generateMainMenu() {
        return new MenuBuilder(new MenuElementGrouping(
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
        )).build(MenuSelectionLogic.basic());
    }

    public static Menu generateSettingsMenu() {
        MenuElementGrouping buttons;

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
            buttons = new MenuElementGrouping();
        }

        final MenuElement resetSettingsButton = MenuHelper.generateTopRightCornerButton(
                "RESET...", () -> {
                    WPParserWriter.saveSettings(true);
                    WPParserWriter.loadSettings();
                    MenuHelper.linkMenu(MenuIDs.SETTINGS, generateSettingsMenu());
                    WordlePlus.reloadGame(true);
                }, WPConstants.WIDTH / 4);

        return generateBasicMenu("Settings",
                new MenuElementGrouping(buttons, resetSettingsButton),
                MenuIDs.MAIN_MENU);
    }

    private static Menu generateStatsMenu() {
        final MenuElementGrouping buttons = MenuHelper.generateListMenuOptions(
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
        final MenuElement resetStatsButton = MenuHelper.generateTopRightCornerButton(
                "RESET...", () -> MenuHelper.linkMenu(MenuIDs.YES_NO, generateResetStatsYesNoMenu()),
                WPConstants.WIDTH / 4);

        return generateBasicMenu("Stats",
                new MenuElementGrouping(buttons, resetStatsButton),
                MenuIDs.MAIN_MENU);
    }

    private static Menu generateWordLengthStatsMenu(final int lengthIndex) {
        final String title = (lengthIndex + WPConstants.INDEX_TO_LENGTH_OFFSET) + "-letter words";
        final boolean canGoLower = lengthIndex > WPConstants.FOUR_L;
        final boolean canGoHigher = lengthIndex < WPConstants.SEVEN_L;

        final int[] guesses = WPStats.getGuesses(lengthIndex);
        final int[] results = WPStats.getResults(lengthIndex);

        final MenuElementGrouping elements = new MenuElementGrouping(
                canGoLower
                        ? MenuHelper.generatePreviousButton(
                                "< " + ((lengthIndex + WPConstants.INDEX_TO_LENGTH_OFFSET) - 1),
                        () -> MenuHelper.linkMenu(MenuIDs.WORD_LENGTH_STATS,
                                generateWordLengthStatsMenu(lengthIndex - 1)))
                        : new MenuElementGrouping(),
                canGoHigher
                        ? MenuHelper.generateNextButton(
                                ((lengthIndex + WPConstants.INDEX_TO_LENGTH_OFFSET) + 1) + " >",
                        () -> MenuHelper.linkMenu(MenuIDs.WORD_LENGTH_STATS,
                                generateWordLengthStatsMenu(lengthIndex + 1)))
                        : new MenuElementGrouping(),
                MenuHelper.generateGuessGraph(guesses),
                MenuHelper.generateResultsSummary(results)
        );

        return generateBasicMenu(title, elements, MenuIDs.STATS);
    }

    private static Menu generateResetStatsYesNoMenu() {
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

    private static Menu generateNotificationPage(
            final String notification, final String okayMenuID
    ) {
        final MenuElementGrouping elements =
                MenuHelper.generateNotificationElements(notification, okayMenuID);

        return generateBasicMenu(" ", null, elements);
    }

    private static Menu generateYesNoMenu(
            final String prompt, final Runnable yesBehaviour, final String noBackMenuID
    ) {
        final MenuElementGrouping elements =
                MenuHelper.generateYesNoElements(prompt, yesBehaviour, noBackMenuID);

        return generateBasicMenu(" ", null, elements);
    }

    private static Menu generateBasicMenu(
            final String title, final MenuElementGrouping elements, final String backMenuID
    ) {
        return generateBasicMenu(title, () -> MenuHelper.linkMenu(backMenuID), elements);
    }

    private static Menu generateBasicMenu(
            final String title, final Runnable backBehaviour, final MenuElementGrouping elements
    ) {
        final MenuElement maybeBackButton = backBehaviour == null
                        ? new MenuElementGrouping()
                        : MenuHelper.generateBackButton(backBehaviour);

        return new MenuBuilder(new MenuElementGrouping(
                // visual render order
                MenuHelper.generateBackground(),
                maybeBackButton,
                MenuHelper.generateMenuTitle(title),
                elements
        )).build(MenuSelectionLogic.basic());
    }

    // SPECIAL

    public static Menu generateGameButtons() {
        return new Menu(
                MenuHelper.generateBackButton(() -> {
                    WordlePlus.manager.setActiveStateIndex(WordlePlus.MENU_STATE_INDEX);
                    WordlePlus.menuManager.setActiveMenuID(MenuIDs.MAIN_MENU);
                }),
                MenuHelper.generateReloadButton(),
                MenuHelper.generateShowHideLettersButton()
        );
    }

    public static Menu generateEndgameButtons() {
        return new MenuBuilder(new MenuElementGrouping(
                MenuHelper.generatePlayAgainButton(),
                MenuHelper.generateBackToMenuButton()
        )).build(MenuSelectionLogic.basic());
    }
}
