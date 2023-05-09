package com.jordanbunke.wordleplus.menu;

import com.jordanbunke.jbjgl.fonts.Font;
import com.jordanbunke.jbjgl.image.JBJGLImage;
import com.jordanbunke.jbjgl.io.JBJGLResourceLoader;
import com.jordanbunke.jbjgl.menus.JBJGLMenu;
import com.jordanbunke.jbjgl.menus.menu_elements.*;
import com.jordanbunke.jbjgl.text.JBJGLText;
import com.jordanbunke.jbjgl.text.JBJGLTextBuilder;
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

    public static void linkMenu(final String menuID, final JBJGLMenu menu) {
        WordlePlus.menuManager.addMenu(menuID, menu, true);
    }

    public static JBJGLStaticMenuElement generateBackground() {
        return JBJGLStaticMenuElement.generate(
                new int[] { 0, 0 }, JBJGLMenuElement.Anchor.LEFT_TOP,
                WPImages.getBackground());
    }

    public static JBJGLMenuElement generateBackButton(final Runnable behaviour) {
        final int MARGIN = 8, BUTTON_WIDTH = 64;

        return generateMenuButton(
                "<", new int[] { MARGIN, MARGIN }, behaviour,
                BUTTON_WIDTH, JBJGLMenuElement.Anchor.LEFT_TOP);
    }

    public static JBJGLMenuElement generateTopRightCornerButton(
            final String label, final Runnable behaviour, final int buttonWidth
    ) {
        final int MARGIN = 8;

        return generateMenuButton(
                label, new int[] { WPConstants.WIDTH - MARGIN, MARGIN }, behaviour,
                buttonWidth, JBJGLMenuElement.Anchor.RIGHT_TOP);
    }

    public static JBJGLMenuElement generatePreviousButton(final String label, final Runnable behaviour) {
        final int MARGIN = 8, BUTTON_WIDTH = 96, HEIGHT = WPConstants.HEIGHT;

        return generateMenuButton(
                label, new int[] { MARGIN + (BUTTON_WIDTH / 2), HEIGHT / 2 }, behaviour,
                BUTTON_WIDTH, JBJGLMenuElement.Anchor.CENTRAL);
    }

    public static JBJGLMenuElement generateNextButton(final String label, final Runnable behaviour) {
        final int MARGIN = 8, BUTTON_WIDTH = 96, WIDTH = WPConstants.WIDTH, HEIGHT = WPConstants.HEIGHT;

        return generateMenuButton(
                label, new int[] { WIDTH - (MARGIN + (BUTTON_WIDTH / 2)), HEIGHT / 2 }, behaviour,
                BUTTON_WIDTH, JBJGLMenuElement.Anchor.CENTRAL);
    }

    public static JBJGLMenuElement generateReloadButton() {
        final int MARGIN = 8;

        final Path folder = WPResources.getIconFolder(),
                nhPath = folder.resolve("reload.png"),
                hPath = folder.resolve("reload-highlighted.png");

        final JBJGLImage nhSource = JBJGLResourceLoader.loadImageResource(WPResources.class, nhPath);
        final JBJGLImage hSource = JBJGLResourceLoader.loadImageResource(WPResources.class, hPath);

        final int width = nhSource.getWidth(), height = nhSource.getHeight();

        final JBJGLImage nh = drawNonHighlightedButton(width, height, nhSource, WPColors.BLACK);
        final JBJGLImage h = drawHighlightedButton(width, height, hSource);

        return JBJGLClickableMenuElement.generate(
                new int[] { (MARGIN * 2) + width, MARGIN }, new int[] { width, height },
                JBJGLMenuElement.Anchor.LEFT_TOP, nh, h,
                () -> WordlePlus.reloadGame(false));
    }

    public static JBJGLToggleClickableMenuElement generateShowHideLettersButton() {
        final int MARGIN = 8, BUTTON_WIDTH = 160;

        final JBJGLImage[] nhbs = new JBJGLImage[] {
                drawNonHighlightedButton(BUTTON_WIDTH, "HIDE", WPColors.BLACK),
                drawNonHighlightedButton(BUTTON_WIDTH, "SHOW", WPColors.BLACK)
        };
        final JBJGLImage[] hbs = new JBJGLImage[] {
                drawHighlightedButton("HIDE", nhbs[0]),
                drawHighlightedButton("SHOW", nhbs[1])
        };

        return JBJGLToggleClickableMenuElement.generate(
                new int[] { WPConstants.WIDTH - MARGIN, MARGIN },
                new int[] { BUTTON_WIDTH, nhbs[0].getHeight() },
                JBJGLMenuElement.Anchor.RIGHT_TOP, nhbs, hbs, new Runnable[] {
                        () -> WPSettings.setLettersHidden(true),
                        () -> WPSettings.setLettersHidden(false)
                }, () -> WPSettings.areLettersHidden() ? 1 : 0, WordlePlus::drawGameSafely
        );
    }

    public static JBJGLMenuElement generatePlayAgainButton() {
        final int BUTTON_WIDTH = (int)(WPConstants.WIDTH * 0.7);

        return generateMenuButton("PLAY AGAIN",
                new int[] { WPConstants.WIDTH / 2, WPConstants.HEIGHT - (2 * BUTTON_LIST_Y_INC) },
                () -> WordlePlus.reloadGame(false),
                BUTTON_WIDTH, JBJGLMenuElement.Anchor.CENTRAL_TOP);
    }

    public static JBJGLMenuElement generateBackToMenuButton() {
        final int BUTTON_WIDTH = (int)(WPConstants.WIDTH * 0.7);

        return generateMenuButton("BACK TO MENU",
                new int[] { WPConstants.WIDTH / 2, WPConstants.HEIGHT - BUTTON_LIST_Y_INC },
                () -> {
                    WordlePlus.reloadGame(false);
                    WordlePlus.manager.setActiveStateIndex(WordlePlus.MENU_STATE_INDEX);
                    WordlePlus.menuManager.setActiveMenuID(MenuIDs.MAIN_MENU);
                },
                BUTTON_WIDTH, JBJGLMenuElement.Anchor.CENTRAL_TOP);
    }

    public static JBJGLMenuElementGrouping generateResultsSummary(final int[] results) {
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
        final JBJGLTextMenuElement[] elements = new JBJGLTextMenuElement[WPStats.RESULTS_LENGTH * 2];

        for (int i = 0; i < WPStats.RESULTS_LENGTH; i++) {
            final int baseIndex = i * 2;

            elements[baseIndex] = JBJGLTextMenuElement.generate(
                    new int[] { xs[i], HEADING_Y }, JBJGLMenuElement.Anchor.CENTRAL_TOP,
                    generateRegularMenuText(2., headings[i]));
            elements[baseIndex + 1] = JBJGLTextMenuElement.generate(
                    new int[] { xs[i], VALUE_Y }, JBJGLMenuElement.Anchor.CENTRAL_TOP,
                    generateRegularMenuText(4., String.valueOf(results[i])));
        }

        return JBJGLMenuElementGrouping.generate(elements);
    }

    public static JBJGLMenuElementGrouping generateGuessGraph(final int[] guesses) {
        final int WIDTH = WPConstants.WIDTH, HEIGHT = WPConstants.HEIGHT;
        final int MAX_BAR_WIDTH = (int)(WIDTH * 0.45), INC_Y = 30,
                LEFT_TEXT_X = (int)(WIDTH * 0.25), RIGHT_TEXT_X = (int)(WIDTH * 0.8),
                BAR_INITIAL_X = (int)(WIDTH * 0.32), BAR_HEIGHT = 12,
                INITIAL_Y = (int)(HEIGHT * 0.2);

        int max = 0;
        for (int guess : guesses)
            if (guess > max)
                max = guess;

        final JBJGLMenuElement[] elements =
                new JBJGLMenuElement[guesses.length * 3];

        for (int i = 0; i < guesses.length; i++) {
            final int baseIndex = i * 3;
            final int y = INITIAL_Y + (i * INC_Y);
            final int barWidth = Math.max(1, (int)((guesses[i] / (double)max) * MAX_BAR_WIDTH));
            final Color barColor = barWidth == 1 ? WPColors.BACKGROUND : WPColors.RIGHT_SPOT;

            final String guessesText = (i + 1) + " guess" + (i == 0 ? "" : "es");

            elements[baseIndex] = JBJGLTextMenuElement.generate(
                    new int[] { LEFT_TEXT_X, y }, JBJGLMenuElement.Anchor.CENTRAL_TOP,
                    generateRegularMenuText(1., guessesText));
            elements[baseIndex + 1] = JBJGLStaticMenuElement.generate(
                    new int[] { BAR_INITIAL_X, y + 12 }, JBJGLMenuElement.Anchor.LEFT_TOP,
                    generateSolidRectangle(barWidth, BAR_HEIGHT, barColor));
            elements[baseIndex + 2] = JBJGLTextMenuElement.generate(
                    new int[] { RIGHT_TEXT_X, y }, JBJGLMenuElement.Anchor.LEFT_TOP,
                    generateRegularMenuText(1., String.valueOf(guesses[i])));
        }

        return JBJGLMenuElementGrouping.generate(elements);
    }

    public static JBJGLMenuElementGrouping generateSettingsList(
            final String[] descriptors, final Callable<Number>[] getters,
            final Callable<Number>[] minGetters, final Callable<Number>[] maxGetters,
            final Consumer<Number>[] setters, final double[] increments, final int offsetY
    ) throws Exception {
        final int width = WPConstants.WIDTH, ELEMENTS_PER_SETTING = 4;

        final int amount = descriptors.length;
        int drawY = BUTTON_LIST_INIT_Y + offsetY;
        final JBJGLMenuElement[] menuElements = new JBJGLMenuElement[amount * ELEMENTS_PER_SETTING];

        for (int i = 0; i < amount; i++) {
            final int BASE_INDEX = i * ELEMENTS_PER_SETTING;

            final Number min = minGetters[i].call(), value = getters[i].call(),
                    max = maxGetters[i].call();

            final JBJGLMenuElement descriptorLabel = JBJGLTextMenuElement.generate(
                    new int[] { (int)(width * 0.05), drawY + TEXT_ON_BUTTON_Y },
                    JBJGLMenuElement.Anchor.LEFT_TOP, JBJGLTextBuilder.initialize(
                            2., JBJGLText.Orientation.LEFT,
                            WPColors.BLACK, WPFonts.STANDARD()).
                            addText(descriptors[i]).build());
            final JBJGLMenuElement valueLabel = JBJGLTextMenuElement.generate(
                    new int[] { (int)(width * 0.8), drawY + TEXT_ON_BUTTON_Y },
                    JBJGLMenuElement.Anchor.CENTRAL_TOP, JBJGLTextBuilder.initialize(
                                    2., JBJGLText.Orientation.LEFT,
                                    WPColors.BLACK, WPFonts.STANDARD()).
                            addText(value.toString()).build());
            final JBJGLMenuElement decrementButton = generateSettingsButton(
                    true, value, min, max, setters[i], drawY, increments[i]);
            final JBJGLMenuElement incrementButton = generateSettingsButton(
                    false, value, min, max, setters[i], drawY, increments[i]);

            menuElements[BASE_INDEX] = descriptorLabel;
            menuElements[BASE_INDEX + 1] = decrementButton;
            menuElements[BASE_INDEX + 2] = valueLabel;
            menuElements[BASE_INDEX + 3] = incrementButton;
            drawY += BUTTON_LIST_Y_INC;
        }

        return JBJGLMenuElementGrouping.generate(menuElements);
    }

    public static JBJGLMenuElementGrouping generateListMenuOptions(
            final String[] headings, final Runnable[] behaviours, final int offsetY
    ) {
        final int width = WPConstants.WIDTH;

        final int amount = headings.length;
        int drawY = BUTTON_LIST_INIT_Y + offsetY;

        final JBJGLMenuElement[] menuElements = new JBJGLMenuElement[amount];

        for (int i = 0; i < amount; i++) {
            final JBJGLMenuElement button = generateMenuButton(
                    headings[i], new int[] { width / 2, drawY }, behaviours[i],
                    width / 2, JBJGLMenuElement.Anchor.CENTRAL_TOP);
            menuElements[i] = button;
            drawY += BUTTON_LIST_Y_INC;
        }

        return JBJGLMenuElementGrouping.generate(menuElements);
    }

    public static JBJGLMenuElementGrouping generateYesNoElements(
            final String prompt, final Runnable yesBehaviour, final String noBackMenuID
    ) {
        final int width = WPConstants.WIDTH, height = WPConstants.HEIGHT;

        return JBJGLMenuElementGrouping.generateOf(
                JBJGLTextMenuElement.generate(
                        new int[] { width / 2, (int)(height * 0.4) },
                        JBJGLMenuElement.Anchor.CENTRAL_TOP,
                        generateText(prompt, 2, WPFonts.ITALICS_SPACED(), WPColors.BLACK)),
                generateMenuButton("NO",
                        new int[] { (int)(width * 0.35), (int)(height * 0.6) },
                        () -> MenuHelper.linkMenu(noBackMenuID),
                        width / 6, JBJGLMenuElement.Anchor.CENTRAL),
                generateMenuButton("YES",
                        new int[] { (int)(width * 0.65), (int)(height * 0.6) },
                        yesBehaviour, width / 6,
                        JBJGLMenuElement.Anchor.CENTRAL)
        );
    }

    public static JBJGLMenuElementGrouping generateNotificationElements(
            final String notification, final String okayMenuID
    ) {
        final int width = WPConstants.WIDTH, height = WPConstants.HEIGHT;

        return JBJGLMenuElementGrouping.generateOf(
                JBJGLTextMenuElement.generate(
                        new int[] { width / 2, (int)(height * 0.4) },
                        JBJGLMenuElement.Anchor.CENTRAL_TOP,
                        generateText(notification, 2, WPFonts.ITALICS_SPACED(), WPColors.BLACK)),
                generateMenuButton("OKAY",
                        new int[] { (int)(width * 0.5), (int)(height * 0.6) },
                        () -> MenuHelper.linkMenu(okayMenuID),
                        width / 6, JBJGLMenuElement.Anchor.CENTRAL)
        );
    }

    public static JBJGLTextMenuElement generateMenuTitle(final String title) {
        final int width = WPConstants.WIDTH;
        final JBJGLText text = generateRegularMenuText(4., title);

        return JBJGLTextMenuElement.generate(
                new int[] { width / 2, TITLE_Y },
                JBJGLMenuElement.Anchor.CENTRAL, text);
    }

    public static JBJGLStaticMenuElement generateGameTitle() {
        final int width = WPConstants.WIDTH;

        return JBJGLStaticMenuElement.generate(
                new int[] { width / 2, TITLE_Y },
                JBJGLMenuElement.Anchor.CENTRAL, WPImages.getLogo());
    }

    public static JBJGLMenuElementGrouping generateVersionInfo() {
        final int width = WPConstants.WIDTH, height = WPConstants.HEIGHT, margin = 4;

        final JBJGLText versionAndDev = JBJGLTextBuilder.initialize(
                1., JBJGLText.Orientation.LEFT,
                WPColors.BLACK, WPFonts.STANDARD()).addText("v" + WPConstants.VERSION)
                .addLineBreak().addText("Jordan Bunke, 2022").build();
        final JBJGLText inspiration = JBJGLTextBuilder.initialize(
                1., JBJGLText.Orientation.RIGHT, WPColors.BLACK, WPFonts.STANDARD()
        ).addText("inspired by \"Wordle\" by Josh Wardle").build();

        return JBJGLMenuElementGrouping.generateOf(
                JBJGLTextMenuElement.generate(
                        new int[] { margin, height - margin },
                        JBJGLMenuElement.Anchor.LEFT_BOTTOM, versionAndDev
                ),
                JBJGLTextMenuElement.generate(
                        new int[] { width - margin, height - margin },
                        JBJGLMenuElement.Anchor.RIGHT_BOTTOM, inspiration
                )
        );
    }

    /* HIDDEN */

    private static JBJGLText generateRegularMenuText(final double textSize, final String text) {
        return JBJGLTextBuilder.initialize(
                textSize, JBJGLText.Orientation.CENTER,
                WPColors.BLACK, WPFonts.STANDARD()).addText(text).build();
    }

    private static JBJGLMenuElement generateSettingsButton(
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
                new int[] { isDecrement ? DECREMENT_X : INCREMENT_X, drawY },
                behaviour, width / 12, JBJGLMenuElement.Anchor.CENTRAL_TOP);
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

    private static JBJGLMenuElement generateMenuButton(
            final String label, final int[] position,
            final Runnable behaviour, final int width,
            final JBJGLMenuElement.Anchor anchor
    ) {
        final boolean hasBehaviour = behaviour != null;
        final Color color = hasBehaviour ? WPColors.BLACK : WPColors.NOT_PRESENT;

        JBJGLImage nonHighlightedButton = drawNonHighlightedButton(width, label, color);
        JBJGLImage highlightedButton = drawHighlightedButton(label, nonHighlightedButton);

        return hasBehaviour
                ? JBJGLClickableMenuElement.generate(
                position, new int[] { width, nonHighlightedButton.getHeight() },
                anchor,
                nonHighlightedButton, highlightedButton, behaviour
        )
                : JBJGLStaticMenuElement.generate(
                position, anchor, nonHighlightedButton
        );
    }

    private static JBJGLImage drawNonHighlightedButton(
            final int width, final int height,
            final JBJGLImage icon, final Color color
    ) {
        JBJGLImage nonHighlightedButton =
                JBJGLImage.create(width, height);
        drawButtonPixelBorder(nonHighlightedButton, color);
        Graphics nhbg = nonHighlightedButton.getGraphics();

        nhbg.drawImage(icon, 0, 0, null);

        return nonHighlightedButton;
    }

    private static JBJGLImage drawNonHighlightedButton(
            final int width, final String label, final Color color
    ) {
        JBJGLImage text = drawText(label, 2., WPFonts.STANDARD(), color);

        final int height = text.getHeight();
        final int trueWidth = Math.max(
                width, text.getWidth() + 20);

        JBJGLImage nonHighlightedButton =
                JBJGLImage.create(trueWidth, height);
        drawButtonPixelBorder(nonHighlightedButton, color);
        Graphics nhbg = nonHighlightedButton.getGraphics();

        drawTextOnButton(nhbg, text, width);

        return nonHighlightedButton;
    }

    private static JBJGLImage drawText(
            final String label, final double size, final Font font, final Color color
    ) {
        return generateText(label, size, font, color).draw();
    }

    private static JBJGLText generateText(
            final String label, final double size, final Font font, final Color color
    ) {
        return JBJGLTextBuilder.initialize(
                size, JBJGLText.Orientation.CENTER,
                color, font).addText(label).build();
    }

    private static void drawTextOnButton(
            final Graphics g, final JBJGLImage text, final int width
    ) {
        final int trueWidth = Math.max(
                width,
                text.getWidth() + 20
        );
        final int x = (trueWidth - text.getWidth()) / 2;

        g.drawImage(text, x, TEXT_ON_BUTTON_Y, null);
    }

    private static JBJGLImage drawHighlightedButton(
            final int width, final int height,
            final JBJGLImage icon
    ) {
        JBJGLImage highlightedButton = JBJGLImage.create(width, height);
        Graphics hbg = highlightedButton.getGraphics();
        hbg.setColor(WPColors.BLACK);
        hbg.fillRect(0, 0, width, height);
        hbg.drawImage(icon, 0, 0, null);

        return highlightedButton;
    }

    private static JBJGLImage drawHighlightedButton(
            final String label, final JBJGLImage nonHighlightedButton
    ) {
        final int width = nonHighlightedButton.getWidth(),
                height = nonHighlightedButton.getHeight();

        JBJGLImage highlightedButton = JBJGLImage.create(width, height);
        Graphics hbg = highlightedButton.getGraphics();
        hbg.setColor(WPColors.BLACK);
        hbg.fillRect(0, 0, width, height);
        drawTextOnButton(hbg,
                drawText(label, 2., WPFonts.ITALICS_SPACED(), WPColors.WHITE),
                width);

        return highlightedButton;
    }

    private static void drawButtonPixelBorder(final JBJGLImage image, final Color c) {
        final Graphics g = image.getGraphics();
        final int thickness = 4, width = image.getWidth(), height = image.getHeight();

        g.setColor(c);
        g.fillRect(0, 0, width, thickness);
        g.fillRect(0, 0, thickness, height);
        g.fillRect(0, height - thickness, width, thickness);
        g.fillRect(width - thickness, 0, thickness, height);
    }

    private static JBJGLImage generateSolidRectangle(
            final int width, final int height, final Color color
    ) {
        final JBJGLImage rectangle = JBJGLImage.create(width, height);
        final Graphics g = rectangle.getGraphics();

        g.setColor(color);
        g.fillRect(0, 0, width, height);

        g.dispose();
        return rectangle;
    }
}
