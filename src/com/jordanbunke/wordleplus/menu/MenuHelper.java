package com.jordanbunke.wordleplus.menu;

import com.jordanbunke.jbjgl.fonts.Font;
import com.jordanbunke.jbjgl.image.GameImage;
import com.jordanbunke.jbjgl.io.ResourceLoader;
import com.jordanbunke.jbjgl.menus.Menu;
import com.jordanbunke.jbjgl.menus.menu_elements.*;
import com.jordanbunke.jbjgl.menus.menu_elements.button.SimpleMenuButton;
import com.jordanbunke.jbjgl.menus.menu_elements.button.SimpleToggleMenuButton;
import com.jordanbunke.jbjgl.menus.menu_elements.container.MenuElementGrouping;
import com.jordanbunke.jbjgl.menus.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.jbjgl.text.Text;
import com.jordanbunke.jbjgl.text.TextBuilder;
import com.jordanbunke.jbjgl.utility.Coord2D;
import com.jordanbunke.wordleplus.*;
import com.jordanbunke.wordleplus.utility.WPColors;
import com.jordanbunke.wordleplus.utility.WPFonts;
import com.jordanbunke.wordleplus.utility.WPImages;

import java.awt.*;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class MenuHelper {
    public static final int TITLE_Y = 60;
    public static final int BUTTON_LIST_INIT_Y = 120;
    public static final int BUTTON_LIST_Y_INC = 100;

    private static final int TEXT_ON_BUTTON_Y = 4;

    /* PUBLICLY ACCESSIBLE */

    public static void linkMenu(final String menuID) {
        WordlePlus.menuManager.setActiveMenuID(menuID);
    }

    public static void linkMenu(final String menuID, final Menu menu) {
        WordlePlus.menuManager.addMenu(menuID, menu, true);
    }

    public static StaticMenuElement generateBackground() {
        return new StaticMenuElement(new Coord2D(), MenuElement.Anchor.LEFT_TOP,
                WPImages.getBackground());
    }

    public static MenuElement generateBackButton(final Runnable behaviour) {
        final int MARGIN = 8, BUTTON_WIDTH = 64;

        return generateMenuButton(
                "<", new Coord2D(MARGIN, MARGIN), behaviour,
                BUTTON_WIDTH, MenuElement.Anchor.LEFT_TOP);
    }

    public static MenuElement generateTopRightCornerButton(
            final String label, final Runnable behaviour, final int buttonWidth
    ) {
        final int MARGIN = 8;

        return generateMenuButton(
                label, new Coord2D(WPConstants.WIDTH - MARGIN, MARGIN), behaviour,
                buttonWidth, MenuElement.Anchor.RIGHT_TOP);
    }

    public static MenuElement generatePreviousButton(final String label, final Runnable behaviour) {
        final int MARGIN = 8, BUTTON_WIDTH = 96, HEIGHT = WPConstants.HEIGHT;

        return generateMenuButton(label,
                new Coord2D(MARGIN + (BUTTON_WIDTH / 2), HEIGHT / 2),
                behaviour, BUTTON_WIDTH, MenuElement.Anchor.CENTRAL);
    }

    public static MenuElement generateNextButton(final String label, final Runnable behaviour) {
        final int MARGIN = 8, BUTTON_WIDTH = 96, WIDTH = WPConstants.WIDTH, HEIGHT = WPConstants.HEIGHT;

        return generateMenuButton(label,
                new Coord2D(WIDTH - (MARGIN + (BUTTON_WIDTH / 2)), HEIGHT / 2),
                behaviour, BUTTON_WIDTH, MenuElement.Anchor.CENTRAL);
    }

    public static MenuElement generateReloadButton() {
        final int MARGIN = 8;

        final Path folder = WPResources.getIconFolder(),
                nhPath = folder.resolve("reload.png"),
                hPath = folder.resolve("reload-highlighted.png");

        final GameImage nhSource = ResourceLoader.loadImageResource(nhPath);
        final GameImage hSource = ResourceLoader.loadImageResource(hPath);

        final int width = nhSource.getWidth(), height = nhSource.getHeight();

        final GameImage nh = drawNonHighlightedButton(width, height, nhSource, WPColors.BLACK);
        final GameImage h = drawHighlightedButton(width, height, hSource);

        return new SimpleMenuButton(
                new Coord2D((MARGIN * 2) + width, MARGIN),
                new Coord2D(width, height), MenuElement.Anchor.LEFT_TOP, true,
                () -> WordlePlus.reloadGame(false), nh, h);
    }

    public static SimpleToggleMenuButton generateShowHideLettersButton() {
        final int MARGIN = 8, BUTTON_WIDTH = 160;

        final GameImage[] nhbs = new GameImage[] {
                drawNonHighlightedButton(BUTTON_WIDTH, "HIDE", WPColors.BLACK),
                drawNonHighlightedButton(BUTTON_WIDTH, "SHOW", WPColors.BLACK)
        };
        final GameImage[] hbs = new GameImage[] {
                drawHighlightedButton("HIDE", nhbs[0]),
                drawHighlightedButton("SHOW", nhbs[1])
        };

        return new SimpleToggleMenuButton(
                new Coord2D(WPConstants.WIDTH - MARGIN, MARGIN),
                new Coord2D(BUTTON_WIDTH, nhbs[0].getHeight()),
                MenuElement.Anchor.RIGHT_TOP, true, nhbs, hbs, new Runnable[] {
                        () -> WPSettings.setLettersHidden(true),
                        () -> WPSettings.setLettersHidden(false)
                }, () -> WPSettings.areLettersHidden() ? 1 : 0,
                WordlePlus::drawGameSafely);
    }

    public static MenuElement generatePlayAgainButton() {
        final int BUTTON_WIDTH = (int)(WPConstants.WIDTH * 0.7);

        return generateMenuButton("PLAY AGAIN",
                new Coord2D(WPConstants.WIDTH / 2, WPConstants.HEIGHT - (2 * BUTTON_LIST_Y_INC)),
                () -> WordlePlus.reloadGame(false),
                BUTTON_WIDTH, MenuElement.Anchor.CENTRAL_TOP);
    }

    public static MenuElement generateBackToMenuButton() {
        final int BUTTON_WIDTH = (int)(WPConstants.WIDTH * 0.7);

        return generateMenuButton("BACK TO MENU",
                new Coord2D(WPConstants.WIDTH / 2, WPConstants.HEIGHT - BUTTON_LIST_Y_INC),
                () -> {
                    WordlePlus.reloadGame(false);
                    WordlePlus.manager.setActiveStateIndex(WordlePlus.MENU_STATE_INDEX);
                    WordlePlus.menuManager.setActiveMenuID(MenuIDs.MAIN_MENU);
                },
                BUTTON_WIDTH, MenuElement.Anchor.CENTRAL_TOP);
    }

    public static MenuElementGrouping generateResultsSummary(final int[] results) {
        final int WIDTH = WPConstants.WIDTH, HEIGHT = WPConstants.HEIGHT;
        final int HEADING_Y = (int)(HEIGHT * 0.85), VALUE_Y = (int)(HEIGHT * 0.7);

        final int[] xs = new int[] {
                (int)(WIDTH * 0.25),
                (int)(WIDTH * 0.5),
                (int)(WIDTH * 0.75)
        };
        final String[] headings = new String[] {
                results[WPStats.WIN_INDEX] == 1 ? "win" : "wins",
                results[WPStats.LOSS_INDEX] == 1 ? "loss" : "losses",
                results[WPStats.FORFEIT_INDEX] == 1 ? "forfeit" : "forfeits"
        };
        final StaticMenuElement[] elements = new StaticMenuElement[WPStats.RESULTS_LENGTH * 2];

        for (int i = 0; i < WPStats.RESULTS_LENGTH; i++) {
            final int baseIndex = i * 2;

            elements[baseIndex] = StaticMenuElement.fromText(
                    new Coord2D(xs[i], HEADING_Y), MenuElement.Anchor.CENTRAL_TOP,
                    generateRegularMenuText(2., headings[i]));
            elements[baseIndex + 1] = StaticMenuElement.fromText(
                    new Coord2D(xs[i], VALUE_Y), MenuElement.Anchor.CENTRAL_TOP,
                    generateRegularMenuText(4., String.valueOf(results[i])));
        }

        return new MenuElementGrouping(elements);
    }

    public static MenuElementGrouping generateGuessGraph(final int[] guesses) {
        final int WIDTH = WPConstants.WIDTH, HEIGHT = WPConstants.HEIGHT;
        final int MAX_BAR_WIDTH = (int)(WIDTH * 0.45), INC_Y = 30,
                LEFT_TEXT_X = (int)(WIDTH * 0.25), RIGHT_TEXT_X = (int)(WIDTH * 0.8),
                BAR_INITIAL_X = (int)(WIDTH * 0.32), BAR_HEIGHT = 12,
                INITIAL_Y = (int)(HEIGHT * 0.2);

        int max = 0;
        for (int guess : guesses)
            if (guess > max)
                max = guess;

        final MenuElement[] elements =
                new MenuElement[guesses.length * 3];

        for (int i = 0; i < guesses.length; i++) {
            final int baseIndex = i * 3;
            final int y = INITIAL_Y + (i * INC_Y);
            final int barWidth = Math.max(1, (int)((guesses[i] / (double)max) * MAX_BAR_WIDTH));
            final Color barColor = barWidth == 1 ? WPColors.BACKGROUND : WPColors.RIGHT_SPOT;

            final String guessesText = (i + 1) + " guess" + (i == 0 ? "" : "es");

            elements[baseIndex] = StaticMenuElement.fromText(
                    new Coord2D(LEFT_TEXT_X, y), MenuElement.Anchor.CENTRAL_TOP,
                    generateRegularMenuText(1., guessesText));
            elements[baseIndex + 1] = new StaticMenuElement(
                    new Coord2D(BAR_INITIAL_X, y + 12), MenuElement.Anchor.LEFT_TOP,
                    generateSolidRectangle(barWidth, BAR_HEIGHT, barColor));
            elements[baseIndex + 2] = StaticMenuElement.fromText(
                    new Coord2D(RIGHT_TEXT_X, y), MenuElement.Anchor.LEFT_TOP,
                    generateRegularMenuText(1., String.valueOf(guesses[i])));
        }

        return new MenuElementGrouping(elements);
    }

    public static MenuElementGrouping generateSettingsList(
            final String[] descriptors, final Callable<Number>[] getters,
            final Callable<Number>[] minGetters, final Callable<Number>[] maxGetters,
            final Consumer<Number>[] setters, final double[] increments, final int offsetY
    ) throws Exception {
        final int width = WPConstants.WIDTH, ELEMENTS_PER_SETTING = 4;

        final int amount = descriptors.length;
        int drawY = BUTTON_LIST_INIT_Y + offsetY;
        final MenuElement[] menuElements = new MenuElement[amount * ELEMENTS_PER_SETTING];

        for (int i = 0; i < amount; i++) {
            final int BASE_INDEX = i * ELEMENTS_PER_SETTING;

            final Number min = minGetters[i].call(), value = getters[i].call(),
                    max = maxGetters[i].call();

            final MenuElement descriptorLabel = StaticMenuElement.fromText(
                    new Coord2D((int)(width * 0.05), drawY + TEXT_ON_BUTTON_Y),
                    MenuElement.Anchor.LEFT_TOP,
                    new TextBuilder(2., Text.Orientation.LEFT,
                            WPColors.BLACK, WPFonts.STANDARD())
                            .addText(descriptors[i]).build());
            final MenuElement valueLabel = StaticMenuElement.fromText(
                    new Coord2D((int)(width * 0.8), drawY + TEXT_ON_BUTTON_Y),
                    MenuElement.Anchor.CENTRAL_TOP,
                    new TextBuilder(2., Text.Orientation.LEFT,
                            WPColors.BLACK, WPFonts.STANDARD())
                            .addText(value.toString()).build());
            final MenuElement decrementButton = generateSettingsButton(
                    true, value, min, max, setters[i], drawY, increments[i]);
            final MenuElement incrementButton = generateSettingsButton(
                    false, value, min, max, setters[i], drawY, increments[i]);

            menuElements[BASE_INDEX] = descriptorLabel;
            menuElements[BASE_INDEX + 1] = decrementButton;
            menuElements[BASE_INDEX + 2] = valueLabel;
            menuElements[BASE_INDEX + 3] = incrementButton;
            drawY += BUTTON_LIST_Y_INC;
        }

        return new MenuElementGrouping(menuElements);
    }

    public static MenuElementGrouping generateListMenuOptions(
            final String[] headings, final Runnable[] behaviours, final int offsetY
    ) {
        final int width = WPConstants.WIDTH;

        final int amount = headings.length;
        int drawY = BUTTON_LIST_INIT_Y + offsetY;

        final MenuElement[] menuElements = new MenuElement[amount];

        for (int i = 0; i < amount; i++) {
            final MenuElement button = generateMenuButton(
                    headings[i], new Coord2D(width / 2, drawY), behaviours[i],
                    width / 2, MenuElement.Anchor.CENTRAL_TOP);
            menuElements[i] = button;
            drawY += BUTTON_LIST_Y_INC;
        }

        return new MenuElementGrouping(menuElements);
    }

    public static MenuElementGrouping generateYesNoElements(
            final String prompt, final Runnable yesBehaviour, final String noBackMenuID
    ) {
        final int width = WPConstants.WIDTH, height = WPConstants.HEIGHT;

        return new MenuElementGrouping(StaticMenuElement.fromText(
                        new Coord2D(width / 2, (int)(height * 0.4)),
                        MenuElement.Anchor.CENTRAL_TOP,
                        generateText(prompt, 2, WPFonts.ITALICS_SPACED(), WPColors.BLACK)),
                generateMenuButton("NO",
                        new Coord2D((int)(width * 0.35), (int)(height * 0.6)),
                        () -> MenuHelper.linkMenu(noBackMenuID),
                        width / 6, MenuElement.Anchor.CENTRAL),
                generateMenuButton("YES",
                        new Coord2D((int)(width * 0.65), (int)(height * 0.6)),
                        yesBehaviour, width / 6,
                        MenuElement.Anchor.CENTRAL)
        );
    }

    public static MenuElementGrouping generateNotificationElements(
            final String notification, final String okayMenuID
    ) {
        final int width = WPConstants.WIDTH, height = WPConstants.HEIGHT;

        return new MenuElementGrouping(
                StaticMenuElement.fromText(
                        new Coord2D(width / 2, (int)(height * 0.4)),
                        MenuElement.Anchor.CENTRAL_TOP,
                        generateText(notification, 2, WPFonts.ITALICS_SPACED(), WPColors.BLACK)),
                generateMenuButton("OKAY",
                        new Coord2D((int)(width * 0.5), (int)(height * 0.6)),
                        () -> MenuHelper.linkMenu(okayMenuID),
                        width / 6, MenuElement.Anchor.CENTRAL)
        );
    }

    public static StaticMenuElement generateMenuTitle(final String title) {
        final int width = WPConstants.WIDTH;
        final Text text = generateRegularMenuText(4., title);

        return StaticMenuElement.fromText(
                new Coord2D(width / 2, TITLE_Y),
                MenuElement.Anchor.CENTRAL, text);
    }

    public static StaticMenuElement generateGameTitle() {
        final int width = WPConstants.WIDTH;

        return new StaticMenuElement(new Coord2D(width / 2, TITLE_Y),
                MenuElement.Anchor.CENTRAL, WPImages.getLogo());
    }

    public static MenuElementGrouping generateVersionInfo() {
        final int width = WPConstants.WIDTH, height = WPConstants.HEIGHT, margin = 4;

        final Text versionAndDev = new TextBuilder(
                1., Text.Orientation.LEFT,
                WPColors.BLACK, WPFonts.STANDARD()).addText("v" + WPConstants.VERSION)
                .addLineBreak().addText("Jordan Bunke, 2022").build();
        final Text inspiration = new TextBuilder(
                1., Text.Orientation.RIGHT, WPColors.BLACK, WPFonts.STANDARD()
        ).addText("inspired by \"Wordle\" by Josh Wardle").build();

        return new MenuElementGrouping(
                StaticMenuElement.fromText(new Coord2D(margin, height - margin),
                        MenuElement.Anchor.LEFT_BOTTOM, versionAndDev),
                StaticMenuElement.fromText(new Coord2D(width - margin, height - margin),
                        MenuElement.Anchor.RIGHT_BOTTOM, inspiration)
        );
    }

    /* HIDDEN */

    private static Text generateRegularMenuText(final double textSize, final String text) {
        return new TextBuilder(
                textSize, Text.Orientation.CENTER,
                WPColors.BLACK, WPFonts.STANDARD()).addText(text).build();
    }

    private static MenuElement generateSettingsButton(
            final boolean isDecrement, final Number value,
            final Number min, final Number max, final Consumer<Number> setter,
            final int drawY, final double increment
    ) {
        final int width = WPConstants.WIDTH,
                DECREMENT_X = (int)(width * 0.7), INCREMENT_X = (int)(width * 0.9);
        final Runnable behaviour = isDecrement
                ? (value.doubleValue() > min.doubleValue()
                        ? generateSettingButtonBehaviour(setter, value, -increment)
                        : null)
                : (value.doubleValue() < max.doubleValue()
                        ? generateSettingButtonBehaviour(setter, value, increment)
                        : null);

        return generateMenuButton(
                isDecrement ? "-" : "+",
                new Coord2D(isDecrement ? DECREMENT_X : INCREMENT_X, drawY),
                behaviour, width / 12, MenuElement.Anchor.CENTRAL_TOP);
    }

    private static Runnable generateSettingButtonBehaviour(
            final Consumer<Number> setter, final Number value, final double increment
    ) {
        return () -> {
            setter.accept(value.doubleValue() + increment);
            linkMenu(MenuIDs.SETTINGS, Menus.generateSettingsMenu());
            WordlePlus.reloadGame(true);
        };
    }

    private static MenuElement generateMenuButton(
            final String label, final Coord2D position,
            final Runnable behaviour, final int width,
            final MenuElement.Anchor anchor
    ) {
        final boolean hasBehaviour = behaviour != null;
        final Color color = hasBehaviour ? WPColors.BLACK : WPColors.NOT_PRESENT;

        GameImage nonHighlightedButton = drawNonHighlightedButton(width, label, color);
        GameImage highlightedButton = drawHighlightedButton(label, nonHighlightedButton);

        return hasBehaviour
                ? new SimpleMenuButton(position,
                new Coord2D(width, nonHighlightedButton.getHeight()),
                anchor, true, behaviour, nonHighlightedButton,
                highlightedButton)
                : new StaticMenuElement(position, anchor, nonHighlightedButton);
    }

    private static GameImage drawNonHighlightedButton(
            final int width, final int height,
            final GameImage icon, final Color color
    ) {
        GameImage nonHighlightedButton = new GameImage(width, height);
        drawButtonPixelBorder(nonHighlightedButton, color);

        nonHighlightedButton.draw(icon, 0, 0);
        return nonHighlightedButton.submit();
    }

    private static GameImage drawNonHighlightedButton(
            final int width, final String label, final Color color
    ) {
        GameImage text = drawText(label, 2., WPFonts.STANDARD(), color);

        final int height = text.getHeight();
        final int trueWidth = Math.max(width, text.getWidth() + 20);

        GameImage nonHighlightedButton = new GameImage(trueWidth, height);
        drawButtonPixelBorder(nonHighlightedButton, color);
        drawTextOnButton(nonHighlightedButton.g(), text, width);

        return nonHighlightedButton.submit();
    }

    private static GameImage drawText(
            final String label, final double size, final Font font, final Color color
    ) {
        return generateText(label, size, font, color).draw();
    }

    private static Text generateText(
            final String label, final double size, final Font font, final Color color
    ) {
        return new TextBuilder(size, Text.Orientation.CENTER, color, font).addText(label).build();
    }

    private static void drawTextOnButton(
            final Graphics g, final GameImage text, final int width
    ) {
        final int trueWidth = Math.max(
                width,
                text.getWidth() + 20
        );
        final int x = (trueWidth - text.getWidth()) / 2;

        g.drawImage(text, x, TEXT_ON_BUTTON_Y, null);
    }

    private static GameImage drawHighlightedButton(
            final int width, final int height,
            final GameImage icon
    ) {
        final GameImage highlightedButton = new GameImage(width, height);
        highlightedButton.fillRectangle(WPColors.BLACK, 0, 0, width, height);
        highlightedButton.draw(icon);
        return highlightedButton.submit();
    }

    private static GameImage drawHighlightedButton(
            final String label, final GameImage nonHighlightedButton
    ) {
        final int width = nonHighlightedButton.getWidth(),
                height = nonHighlightedButton.getHeight();

        final GameImage highlightedButton = new GameImage(width, height);
        highlightedButton.fillRectangle(WPColors.BLACK, 0, 0, width, height);
        drawTextOnButton(highlightedButton.g(), drawText(label,
                2., WPFonts.ITALICS_SPACED(), WPColors.WHITE), width);

        return highlightedButton.submit();
    }

    private static void drawButtonPixelBorder(final GameImage image, final Color c) {
        final Graphics g = image.getGraphics();
        final int thickness = 4, width = image.getWidth(), height = image.getHeight();

        g.setColor(c);
        g.fillRect(0, 0, width, thickness);
        g.fillRect(0, 0, thickness, height);
        g.fillRect(0, height - thickness, width, thickness);
        g.fillRect(width - thickness, 0, thickness, height);
    }

    private static GameImage generateSolidRectangle(
            final int width, final int height, final Color color
    ) {
        final GameImage rectangle = new GameImage(width, height);
        rectangle.fillRectangle(color, 0, 0, width, height);
        return rectangle.submit();
    }
}
