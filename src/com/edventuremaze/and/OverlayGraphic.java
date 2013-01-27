package com.edventuremaze.and;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;



/**
 * This class represents an overlay graphic image which can be used in various situations such as buttons.
 * When used as a button is just a clickable icon only.  There is no up, down, or highlight graphic as in more
 * sophisticated buttons.
 * @author brianpratt
 */
public class OverlayGraphic {

    private int fId;
    private int fXOffset;
    private int fYOffset;
    private int fWidth;
    private int fHeight;
    private int[] fPixels;
    private boolean fActive = true;

    /**
     * Constructor - initializes the graphic.  The position parameters (x, y, width, and height) are used not only
     * for drawing the icon, but can also but used to identify when this grapic is clicked.
     * @param id Unique identifier for this graphic.
     * @param pixels The pixels of the image for the graphic.
     * @param xOffset The x offset on the screen where the graphic should appear.
     * @param yOffset The y offset on the screen where the graphic should appear.
     * @param width The width of the graphic.
     * @param height The height of the graphic.
     * @param active Stat of the graphic: active vs inactive.
     */
    public OverlayGraphic(int id, int[] pixels, int xOffset, int yOffset, int width, int height, boolean active) {
        fId = id;
        fPixels = pixels;
        fXOffset = xOffset;
        fYOffset = yOffset;
        fWidth = width;
        fHeight = height;
        fActive = active;
    }

    public int getId() {
        return fId;
    }

    public int getXOffset() {
        return fXOffset;
    }

    public int getYOffset() {
        return fYOffset;
    }

    public int[] getPixels() {
        return fPixels;
    }

    public int getWidth() {
        return fWidth;
    }

    public int getHeight() {
        return fHeight;
    }

    public boolean isActive() {
        return fActive;
    }

    public void setActive(boolean active) {
        fActive = active;
    }

    /**
     * Draws the graphic on the specified canvas.
     */
    public void drawGraphic(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.setPixels(getPixels(), 0, getWidth(), 0, 0, getWidth(), getHeight());
        canvas.drawBitmap(bitmap,
                new Rect(0, 0, getWidth(), getHeight()),
                new Rect(getXOffset(), getYOffset(), getXOffset() + getWidth(), getYOffset() + getHeight()),
                null);
        bitmap.recycle();
    }
}


