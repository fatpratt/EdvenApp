package com.edventuremaze.and;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.edventuremaze.maze.GeneralConfig;
import com.edventuremaze.maze.ImagePixels;
import com.edventuremaze.maze.MazeGlobals;
import com.edventuremaze.maze.Platform;

/**
 * Helper class used to draw opening credits including the background image and text.
 * @author brianpratt
 */
public class OpeningCredits {
    final static String sLogLabel = "--->OpeningCredits:";

    private String fTitleLine1 = "";
    private String fTitleLine2 = "";
    private String fTitleLine3 = "";
    private String fTitleLine4 = "";

    private int fLine1Clr = Color.LTGRAY;
    private int fLine2Clr = Color.LTGRAY;
    private int fLine3Clr = Color.LTGRAY;
    private int fLine4Clr = Color.RED;

    private int fFontSizeLine1;
    private int fFontSizeLine2;
    private int fFontSizeLine3;
    private int fFontSizeLine4;

    private int fYPosLine1;
    private int fYPosLine2;
    private int fYPosLine3;
    private int fYPosLine4;

    private ImagePixels fImagePixels;

    private Platform fPlatform;

    /**
     * Constructor - Establish member variables used to in drawing routine.
     */
    public OpeningCredits(Platform platform, GeneralConfig generalConfig) {
        fPlatform = platform;
        fImagePixels = generalConfig.getOpeningCreditsImagePixels();

        try {
            fLine1Clr = Color.parseColor(generalConfig.getLine1ClrStr().toLowerCase());
        } catch (IllegalArgumentException e) {
            platform.logError(sLogLabel, "Unable to parse line 1 color.");
        }
        try {
            fLine2Clr = Color.parseColor(generalConfig.getLine2ClrStr().toLowerCase());
        } catch (IllegalArgumentException e) {
            platform.logError(sLogLabel, "Unable to parse line 2 color.");
        }
        try {
            fLine3Clr = Color.parseColor(generalConfig.getLine3ClrStr().toLowerCase());
        } catch (IllegalArgumentException e) {
            platform.logError(sLogLabel, "Unable to parse line 3 color.");
        }
        try {
            fLine4Clr = Color.parseColor(generalConfig.getLine4ClrStr().toLowerCase());
        } catch (IllegalArgumentException e) {
            platform.logError(sLogLabel, "Unable to parse line 4 color.");
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
     * Draws the opening credits (bitmap and text) on the canvas of the specified context.
     */
    public void drawOpeningCredits(Context context, SurfaceHolder holder) {

        Surface surface = holder.getSurface();
        Canvas canvas = null;
        try {
            // lock canvas so nothing else can use it
            canvas = holder.lockCanvas(null);
            if (canvas == null) return;
            if (context == null) return;
            if (holder == null) return;
            synchronized (holder) {
                // draw opening credits image
                Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
                Bitmap openingCreditsBitmap = Bitmap.createBitmap(fImagePixels.getWidth(), fImagePixels.getHeight(), Bitmap.Config.ARGB_8888);
                openingCreditsBitmap.setPixels(fImagePixels.getPixels(), 0, fImagePixels.getWidth(), 0, 0, fImagePixels.getWidth(), fImagePixels.getHeight());

                canvas.drawBitmap(openingCreditsBitmap,
                        new Rect(0, 0, openingCreditsBitmap.getWidth(), openingCreditsBitmap.getHeight()),
                        new Rect(0, 0, display.getWidth(), display.getHeight()),
                        null);

                // draws opening credits text
                Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(12 + TextAreaBox.getFontSizeSupplement(context));
                paint.setTypeface(font);
                paint.setAntiAlias(true);
                paint.setShadowLayer(3, 3, 3, Color.DKGRAY);
                paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                Rect bounds = new Rect();
                paint.setColor(fLine1Clr);
                paint.setTextSize(fFontSizeLine1 + TextAreaBox.getFontSizeSupplement(context));
                paint.getTextBounds(fTitleLine1, 0, fTitleLine1.length(), bounds);
                double ratio = (double)fYPosLine1 / (double)fImagePixels.getHeight();
                int margin = (display.getWidth() - bounds.width()) / 2;
                canvas.drawText(fTitleLine1, margin, Math.round((double)display.getHeight() * ratio),  paint);

                paint.setColor(fLine2Clr);
                paint.setTextSize(fFontSizeLine2 + TextAreaBox.getFontSizeSupplement(context));
                paint.getTextBounds(fTitleLine2, 0, fTitleLine2.length(), bounds);
                ratio = (double)fYPosLine2 / (double)fImagePixels.getHeight();
                margin = (display.getWidth() - bounds.width()) / 2;
                canvas.drawText(fTitleLine2, margin, Math.round((double)display.getHeight() * ratio),  paint);

                paint.setColor(fLine3Clr);
                paint.setTextSize(fFontSizeLine3 + TextAreaBox.getFontSizeSupplement(context));
                paint.getTextBounds(fTitleLine3, 0, fTitleLine3.length(), bounds);
                ratio = (double)fYPosLine3 / (double)fImagePixels.getHeight();
                margin = (display.getWidth() - bounds.width()) / 2;
                canvas.drawText(fTitleLine3, margin, Math.round((double)display.getHeight() * ratio),  paint);

                paint.setColor(fLine4Clr);
                paint.setTextSize(fFontSizeLine4 + TextAreaBox.getFontSizeSupplement(context));
                paint.getTextBounds(fTitleLine4, 0, fTitleLine4.length(), bounds);
                ratio = (double)fYPosLine4 / (double)fImagePixels.getHeight();
                margin = (display.getWidth() - bounds.width()) / 2;
                canvas.drawText(fTitleLine4, margin, Math.round((double)display.getHeight() * ratio),  paint);

                openingCreditsBitmap.recycle();
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}


