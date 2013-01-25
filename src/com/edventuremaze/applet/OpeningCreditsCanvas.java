package com.edventuremaze.applet;

import com.edventuremaze.applet.maze.PlatformApplet;
import com.edventuremaze.maze.GeneralConfig;
import com.edventuremaze.maze.ImagePixels;
import com.edventuremaze.maze.MazeGlobals;
import com.edventuremaze.maze.Platform;

import java.awt.*;
import java.awt.image.MemoryImageSource;

/**
 * Helper class used to draw opening credits including the background image and text.
 * @author brianpratt
 */
public class OpeningCreditsCanvas extends Canvas {
    private String fTitleLine1 = "";
    private String fTitleLine2 = "";
    private String fTitleLine3 = "";
    private String fTitleLine4 = "";

    private Color fLine1Clr = Color.gray;
    private Color fLine2Clr = Color.gray;
    private Color fLine3Clr = Color.gray;
    private Color fLine4Clr = Color.red;

    private int fFontSizeLine1;
    private int fFontSizeLine2;
    private int fFontSizeLine3;
    private int fFontSizeLine4;

    private int fYPosLine1;
    private int fYPosLine2;
    private int fYPosLine3;
    private int fYPosLine4;

    private ImagePixels fImagePixels;

    private PlatformApplet fPlatform;

    private MemoryImageSource fMemSource;
    private Image fMemImage;

    /**
     * Constructor - Establish member variables used to in drawing routine.
     */
    public OpeningCreditsCanvas(Platform platform, GeneralConfig generalConfig) {
        fPlatform = (PlatformApplet)platform;
        fImagePixels = generalConfig.getOpeningCreditsImagePixels();

        try {
            fLine1Clr = ColorCreator.createColor(generalConfig.getLine1ClrStr());
        } catch (IllegalArgumentException e) {
            System.out.println("Unable to parse line 1 color.");
        }
        try {
            fLine2Clr = ColorCreator.createColor(generalConfig.getLine2ClrStr());
        } catch (IllegalArgumentException e) {
            System.out.println("Unable to parse line 2 color.");
        }
        try {
            fLine3Clr = ColorCreator.createColor(generalConfig.getLine3ClrStr());
        } catch (IllegalArgumentException e) {
            System.out.println("Unable to parse line 3 color.");
        }
        try {
            fLine4Clr = ColorCreator.createColor(generalConfig.getLine4ClrStr());
        } catch (IllegalArgumentException e) {
            System.out.println("Unable to parse line 4 color.");
        }
        fFontSizeLine1 = generalConfig.getFontSizeLine1();
        fFontSizeLine2 = generalConfig.getFontSizeLine2();
        fFontSizeLine3 = generalConfig.getFontSizeLine3();
        fFontSizeLine4 = generalConfig.getFontSizeLine4();

        fTitleLine1 = generalConfig.getTitleLine1();
        fTitleLine2 = generalConfig.getTitleLine2();
        fTitleLine3 = generalConfig.getTitleLine3();
        fTitleLine4 = generalConfig.getTitleLine4();

        fYPosLine1 = generalConfig.getYPosLine1();
        if (MazeGlobals.X2) fYPosLine1 += fYPosLine1;
        fYPosLine2 = generalConfig.getYPosLine2();
        if (MazeGlobals.X2) fYPosLine2 += fYPosLine2;
        fYPosLine3 = generalConfig.getYPosLine3();
        if (MazeGlobals.X2) fYPosLine3 += fYPosLine3;
        fYPosLine4 = generalConfig.getYPosLine4();
        if (MazeGlobals.X2) fYPosLine4 += fYPosLine4;
    }

    /**
     * Draws the opening credits on the applet graphic surface.
     */
    public void drawOpeningCredits(Graphics graphics) {
        fMemSource = new MemoryImageSource(MazeGlobals.PROJECTIONPLANEWIDTH, MazeGlobals.PROJECTIONPLANEHEIGHT, fImagePixels.getPixels(), 0, MazeGlobals.PROJECTIONPLANEWIDTH);
        if (fMemSource != null) fMemSource.newPixels();
        fMemSource.setAnimated(true);
        fMemImage = createImage(fMemSource);
        graphics.drawImage(fMemImage, 0, 0, this);

        TextUtils.drawShadowString(graphics, MazeGlobals.PROJECTIONPLANEWIDTH, fTitleLine1,
            new Font("TimesRoman", Font.BOLD, fFontSizeLine1), fYPosLine1, true, fLine1Clr);
        TextUtils.drawShadowString(graphics, MazeGlobals.PROJECTIONPLANEWIDTH, fTitleLine2,
            new Font("TimesRoman", Font.BOLD, fFontSizeLine2), fYPosLine2, true, fLine2Clr);
        TextUtils.drawShadowString(graphics, MazeGlobals.PROJECTIONPLANEWIDTH, fTitleLine3,
            new Font("TimesRoman", Font.BOLD, fFontSizeLine3), fYPosLine3, true, fLine3Clr);
        TextUtils.drawShadowString(graphics, MazeGlobals.PROJECTIONPLANEWIDTH, fTitleLine4,
            new Font("TimesRoman", Font.BOLD, fFontSizeLine4), fYPosLine4, true, fLine4Clr);
    }

}


