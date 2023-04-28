package com.jordanbunke.wordleplus.utility;

import com.jordanbunke.jbjgl.image.JBJGLImage;
import com.jordanbunke.jbjgl.text.JBJGLText;
import com.jordanbunke.jbjgl.text.JBJGLTextBuilder;
import com.jordanbunke.wordleplus.WPConstants;
import com.jordanbunke.wordleplus.WordlePlus;

import java.awt.*;

public class WPImages {
    private static final JBJGLImage ICON = generateIcon();
    private static final JBJGLImage LOGO = generateLogo();
    private static final JBJGLImage BACKGROUND = generateBackground();

    private static JBJGLImage generateIcon() {
        return generateLetterPanel('W', 32, 2.);
    }

    private static JBJGLImage generateBackground() {
        final int width = WPConstants.WIDTH, height = WPConstants.HEIGHT;

        final JBJGLImage background = JBJGLImage.create(width, height);
        final Graphics g = background.getGraphics();

        g.setColor(WPColors.BACKGROUND);
        g.fillRect(0, 0, width, height);

        return background;
    }

    private static JBJGLImage generateLogo() {
        final String title = WordlePlus.TITLE;
        final int SQUARE_DIM = 48, MARGIN = 8, LENGTH = title.length();

        final JBJGLImage logo = JBJGLImage.create(
                (LENGTH * SQUARE_DIM) + ((LENGTH - 1) * MARGIN), SQUARE_DIM);
        final Graphics logoG = logo.getGraphics();

        for (int i = 0; i < LENGTH; i++) {
            final char c = title.charAt(i);

            final JBJGLImage letterPanel = generateLetterPanel(c, SQUARE_DIM, 3.);

            logoG.drawImage(letterPanel, (SQUARE_DIM + MARGIN) * i, 0, null);
        }

        logoG.dispose();
        return logo;
    }

    private static JBJGLImage generateLetterPanel(
            final char c, final int SQUARE_DIM, final double textSize
    ) {
        final JBJGLImage letterPanel = JBJGLImage.create(SQUARE_DIM, SQUARE_DIM);
        final Graphics letterG = letterPanel.getGraphics();

        letterG.setColor(WPColors.RIGHT_SPOT);
        letterG.fillRect(0, 0, SQUARE_DIM, SQUARE_DIM);

        final JBJGLImage letter = JBJGLTextBuilder.initialize(
                textSize, JBJGLText.Orientation.CENTER, WPColors.WHITE, WPFonts.STANDARD()
        ).addText(String.valueOf(c)).build().draw();
        letterG.drawImage(letter,
                ((SQUARE_DIM / 2) - (letter.getWidth() / 2)) + (int)textSize,
                -(SQUARE_DIM / 2), null);

        letterG.dispose();
        return letterPanel;
    }

    // GETTERS

    public static JBJGLImage getIcon() {
        return ICON;
    }

    public static JBJGLImage getLogo() {
        return LOGO;
    }

    public static JBJGLImage getBackground() {
        return BACKGROUND;
    }
}
