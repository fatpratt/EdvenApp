package com.edventuremaze.applet;

import java.awt.*;

/**
 * General routines for drawing text.
 *
 * @author: brianpratt
 */
public class TextUtils {

    static int fLine = 1;   // current line count or position where error is to be printed

    /**
     * Draws string with a shadow.
     */
    static void drawShadowString(Graphics g, int boundingWidth, String msg, Font fnt, int yPos, boolean moreBlending) {
        drawShadowString(g, boundingWidth, msg, fnt, yPos, moreBlending, new Color(12, 12, 12));
    }

    /**
     * Draws a shadow string centered on screen - used for "made with..." opening screen and more
     */
    static void drawShadowString(Graphics g, int boundingWidth, String msg, Font fnt,
                                 int yPos, boolean moreBlending, Color clrMain) {
        Color clrHMGray = new Color(165, 165, 165);
        Color clrHMDrkGray = new Color(99, 99, 99);

        Font oldFnt = g.getFont();
        g.setFont(fnt);
        FontMetrics fm = g.getFontMetrics();
        int stringWidth = fm.stringWidth(msg);

        int xPos = (boundingWidth - stringWidth) / 2;
        g.setColor(clrHMGray);
        g.drawString(msg, xPos, yPos);

        if (moreBlending) {
            xPos -= 1;
            yPos -= 1;
            g.setColor(clrHMDrkGray);
            g.drawString(msg, xPos, yPos);
        }

        xPos -= 1;

        yPos -= 1;
        g.setColor(clrMain);
        g.drawString(msg, xPos, yPos);
        g.setFont(oldFnt);
    }

    /**
     * Draw error message.
     */
    public static void drawErrorMessage(Graphics g, String msg) {
        final int leftMargin = 10;
        final int spaceBetweenLines = 2;
        Font oldFnt = g.getFont();
        g.setFont(new Font("TimesRoman", Font.PLAIN, 10));
        FontMetrics fm = g.getFontMetrics();
        int stringWidth = fm.stringWidth(msg);
        g.setColor(Color.red);
        g.drawString(msg, leftMargin, fLine++ * (fm.getHeight() + spaceBetweenLines));
        g.setFont(oldFnt);
    }

    /**
     * Returns true if we have previously displayed an error.
     */
    static boolean errorsExist() {
        return (fLine > 1);
    }

    /**
     * Resets the position of where the next error is to be displayed back to top.
     */
    static void resetOutputPosToTop() {
        fLine = 1;
    }
}
