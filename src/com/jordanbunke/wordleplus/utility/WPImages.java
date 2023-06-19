package com.jordanbunke.wordleplus.utility;

import com.jordanbunke.jbjgl.image.GameImage;
import com.jordanbunke.jbjgl.text.Text;
import com.jordanbunke.jbjgl.text.TextBuilder;
import com.jordanbunke.wordleplus.WPConstants;

public class WPImages {
    private static final GameImage ICON = generateIcon();
    private static final GameImage LOGO = generateLogo();
    private static final GameImage BACKGROUND = generateBackground();

    private static GameImage generateIcon() {
        return generateLetterPanel('W', 32, 2.);
    }

    private static GameImage generateBackground() {
        final int width = WPConstants.WIDTH, height = WPConstants.HEIGHT;

        final GameImage background = new GameImage(width, height);
        background.fillRectangle(WPColors.BACKGROUND, 0, 0, width, height);
        return background.submit();
    }

    private static GameImage generateLogo() {
        final String title = WPConstants.TITLE;
        final int SQUARE_DIM = 48, MARGIN = 8, LENGTH = title.length();

        final GameImage logo = new GameImage((LENGTH * SQUARE_DIM) + ((LENGTH - 1) * MARGIN), SQUARE_DIM);

        for (int i = 0; i < LENGTH; i++) {
            final char c = title.charAt(i);
            logo.draw(generateLetterPanel(c, SQUARE_DIM, 3.), (SQUARE_DIM + MARGIN) * i, 0);
        }

        return logo.submit();
    }

    private static GameImage generateLetterPanel(
            final char c, final int SQUARE_DIM, final double textSize
    ) {
        final GameImage letterPanel = new GameImage(SQUARE_DIM, SQUARE_DIM);

        letterPanel.fillRectangle(WPColors.RIGHT_SPOT, 0, 0, SQUARE_DIM, SQUARE_DIM);

        final GameImage letter = new TextBuilder(textSize,
                Text.Orientation.CENTER, WPColors.WHITE,
                WPFonts.STANDARD()).addText(String.valueOf(c)).build().draw();
        letterPanel.draw(letter, (SQUARE_DIM / 2) - (letter.getWidth() / 2), -(SQUARE_DIM / 2));

        return letterPanel.submit();
    }

    // GETTERS

    public static GameImage getIcon() {
        return ICON;
    }

    public static GameImage getLogo() {
        return LOGO;
    }

    public static GameImage getBackground() {
        return BACKGROUND;
    }
}
