package com.edventuremaze.and;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.*;
import com.edventuremaze.and.maze.PlatformAnd;
import com.edventuremaze.maze.MazeGlobals;
import com.edventuremaze.maze.Platform;

/**
 * The text area box is the graphical component that is used to display a question (and possibly error messages in the
 * future).
 * @author brianpratt
 */
public class TextAreaBox {

    static final String sLogLabel = "--->TextArea: ";

    public static final int TEXT_START_POS_X = 6;
    public static final int TEXT_START_POS_Y = 36;
    public static final int LINE_SEPARATOR_HEIGHT = 5;

    private static final int TOP_MARGIN = 41;

    private Bitmap fBitmap;
    private String fDisplayText = "programmer error: call setDisplayText()";
    private Paint fPaint;
    private Canvas fCanvas;
    private Platform fPlatform;

    private int fDisplayWidth = 320;
    private int fDisplayHeight = 200;

    private int fBackGroundClr = Color.parseColor("#efa501");
    private int fTextClr = Color.parseColor("#191919");
    private int fFontSize = 16;  // future feature: for longer questions that don't fit on the screen, we will adjust the font dynamically

    private OverlayGraphic fCloseButton;
    private OverlayGraphic fQuestionLabelGraphic;

    private String fCloseMsg = "[Don't answer the question here. Use this as a clue to choose a path in the maze.]";

    /**
     * Static method helpful for getting font sizes right on android.
     * We add a little to the web font sizes coming from config files based upon the android screen configuration.
     * @return Supplemental font size value.
     */
    public static int getFontSizeSupplement(Context context) {
        // configuration reports screen size of small, normal or large (and implies xlarge)
        if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            return MazeGlobals.X2 ? 12 : 6;
        } else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            return MazeGlobals.X2 ? 6 : 3;
        } else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            return MazeGlobals.X2 ? 2 : 1;
        } else {   // xlarge ??
            return MazeGlobals.X2 ? 24 : 12;
        }
    }

    /**
     * Constructor - establishes the colors, bitmap, paint and canvas.
     */
    public TextAreaBox(Platform platform, String backGroundClrStr, String textClrStr,
                       int displayWidth, int displayHeight, OverlayGraphic closeButton, OverlayGraphic questionLabelGraphic) {
        fPlatform = platform;

        /***  The app will control the color and not the configuration files
        try {
            fBackGroundClr = Color.parseColor(backGroundClrStr.toLowerCase());
        } catch (IllegalArgumentException e) {
            fPlatform.logError(sLogLabel, "Unable to parse background color:  " + backGroundClrStr + ".");
        }

        try {
            fTextClr = Color.parseColor(textClrStr.toLowerCase());
        } catch (IllegalArgumentException e) {
            fPlatform.logError(sLogLabel, "Unable to parse text color:  " + textClrStr + ".");
        }
        ***/

        fDisplayWidth = displayWidth;
        fDisplayHeight = displayHeight;

        fBitmap = Bitmap.createBitmap(fDisplayWidth, fDisplayHeight, Bitmap.Config.ARGB_8888);
        fCloseButton = closeButton;
        fQuestionLabelGraphic = questionLabelGraphic;

        initializePaint();
        initializeCanvas();
    }

    /**
     * Establishes paint object.
     */
    private void initializePaint() {
        PlatformAnd platformAnd = (PlatformAnd) fPlatform;
        Context context = platformAnd.getContext();

        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
        fPaint = new Paint();
        fPaint.setStyle(Paint.Style.FILL);
        fPaint.setColor(fTextClr);
        fPaint.setTextSize(fFontSize + TextAreaBox.getFontSizeSupplement(context));
        fPaint.setTypeface(font);
        fPaint.setAntiAlias(true);
    }

    /**
     * Establishes canvas object.
     */
    private void initializeCanvas() {
        fCanvas = new Canvas(fBitmap);
        fCanvas.drawColor(fBackGroundClr);
    }

    /**
     * Called externally to set the display text.
     */
    public void setDisplayText(String str) {
        fDisplayText = removeChar('\r', str);
        fDisplayText += "\n\n" + fCloseMsg;
        fDisplayText = wrapLines(fDisplayText, fDisplayWidth - TEXT_START_POS_X);
    }

    /**
     * Renders the display text.
     */
    public void render() {
        drawString(fDisplayText, TEXT_START_POS_X, TEXT_START_POS_Y, fTextClr);
        fCloseButton.drawGraphic(fCanvas);
        fQuestionLabelGraphic.drawGraphic(fCanvas);
    }

    /**
     * Clears display canvas and display text.
     */
    public void clear() {
        // apparently you need to draw over your old text with the background color to erase it
        drawString(fDisplayText, TEXT_START_POS_X, TEXT_START_POS_Y, fBackGroundClr);
        fDisplayText = "";
    }

    /**
     * Injects newline characters into the input string where the words spill off the right margin.  Returns the modified
     * string.
     */
    private String wrapLines(String displayStr, int maxDisplayWidth) {
        StringBuffer out = new StringBuffer();
        String[] lines = displayStr.split("\n");
        for (int l = 0; l < lines.length; ++l) {
            String[] words = lines[l].split(" ");
            StringBuffer accumWords = new StringBuffer();
            String separator = "";
            for (int w = 0; w < words.length; ++w) {
                String tempAccumWords = accumWords.toString() + separator + words[w];
                Rect bounds = new Rect();
                fPaint.getTextBounds(tempAccumWords, 0, tempAccumWords.length(), bounds);
                if (bounds.right < maxDisplayWidth) {
                    accumWords.append(separator + words[w]);
                    separator = " ";
                } else {
                    out.append(accumWords.toString() + "\n");
                    accumWords = new StringBuffer(words[w]);
                    separator = " ";
                }
            }
            out.append(accumWords.toString() + "\n");
        }
        return out.toString();
    }

    /**
     * Removes the specified character from the input string.  Returns the modified string.
     */
    public static String removeChar(char ch, String str) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == ch) continue;
            out.append(str.charAt(i));
        }
        return out.toString();
    }

    /**
     * Draws the string at the specified location with the specified color.
     */
    private void drawString(String str, int x, int y, int color) {
        fPaint.setColor(color);
        fCanvas.drawColor(fBackGroundClr);

        String[] lines = str.split("\n");
        Rect bounds = new Rect();
        int yoff = TOP_MARGIN;
        for (int i = 0; i < lines.length; ++i) {
            fCanvas.drawText(lines[i], x, y + yoff, fPaint);
            fPaint.getTextBounds(lines[i], 0, lines[i].length(), bounds);
            yoff += LINE_SEPARATOR_HEIGHT + bounds.height();
        }
    }

    /**
     * Returns the bitmap representation of the text area.
\    */
    public Bitmap getBitmap() {
        return fBitmap;
    }

}
