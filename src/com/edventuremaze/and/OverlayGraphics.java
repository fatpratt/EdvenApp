package com.edventuremaze.and;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.Display;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a collection of the overlay graphics used as buttons (and stock images) in the game
 * to start, stop and display questions. This class can be a useful way to track with buttons/images are active and
 * which are inactive throughout the app.
 * @author brianpratt
 */

public class OverlayGraphics {
    final static String sLogLabel = "--->OverlayGraphics: ";

    final static int OVR_NOT_FOUND = 0;
    final static int OVR_BTN_EXIT = 101;
    final static int OVR_BTN_QUESTION = 102;
    final static int OVR_BTN_TILT = 103;
    final static int OVR_BTN_CLOSE = 104;
    final static int OVR_IMG_QUESTION = 201;
    final static int OVR_IMG_INST = 202;
    final static int OVR_IMG_TILT_ON = 203;
    final static int OVR_IMG_TILT_OFF = 204;

    private Context fContext;				// the android context
    private ArrayList<OverlayGraphic> fOverlayGraphicsList = new ArrayList<OverlayGraphic>();   // collection of maze graphics tracking active/inactive state

    /**
     * Constructor - To add overlay images, you must modify this method.
     */
    public OverlayGraphics(Context context) {
        fContext = context;
        Display display = ((Activity)fContext).getWindowManager().getDefaultDisplay();

        Bitmap exitBitmap = BitmapFactory.decodeResource(fContext.getResources(), R.drawable.quit);
        int[] exitPixels = new int[exitBitmap.getWidth() * exitBitmap.getHeight()];
        exitBitmap.getPixels(exitPixels, 0, exitBitmap.getWidth(), 0, 0, exitBitmap.getWidth(), exitBitmap.getHeight());
        OverlayGraphic exitButton = new OverlayGraphic(OVR_BTN_EXIT, exitPixels, StartActivity.getAdjustedLeftMargin(), StartActivity.getAdjustedTopMargin(),
            exitBitmap.getWidth(), exitBitmap.getHeight(), true);
        exitBitmap.recycle();

        Bitmap questionBitmap = BitmapFactory.decodeResource(fContext.getResources(), R.drawable.clue);
        int[] questionPixels = new int[questionBitmap.getWidth() * questionBitmap.getHeight()];
        questionBitmap.getPixels(questionPixels, 0, questionBitmap.getWidth(), 0, 0, questionBitmap.getWidth(), questionBitmap.getHeight());
        OverlayGraphic questionButton = new OverlayGraphic(OVR_BTN_QUESTION, questionPixels,
                (StartActivity.getAdjustedLeftMargin() + StartActivity.getAdjustedDisplayWidth()) - questionBitmap.getWidth(),
                StartActivity.getAdjustedTopMargin(),
                questionBitmap.getWidth(), questionBitmap.getHeight(), false);
        questionBitmap.recycle();

        Bitmap tiltBitmap = BitmapFactory.decodeResource(fContext.getResources(), R.drawable.tilt);
        int[] tiltPixels = new int[tiltBitmap.getWidth() * tiltBitmap.getHeight()];
        tiltBitmap.getPixels(tiltPixels, 0, tiltBitmap.getWidth(), 0, 0, tiltBitmap.getWidth(), tiltBitmap.getHeight());
        OverlayGraphic tiltButton = new OverlayGraphic(OVR_BTN_TILT, tiltPixels,
                StartActivity.getAdjustedLeftMargin(),
                (StartActivity.getAdjustedTopMargin() + StartActivity.getAdjustedDisplayHeight()) - questionBitmap.getHeight(),
                tiltBitmap.getWidth(), tiltBitmap.getHeight(), true);
        tiltBitmap.recycle();

        Bitmap closeBitmap = BitmapFactory.decodeResource(fContext.getResources(), R.drawable.close);
        int[] closePixels = new int[closeBitmap.getWidth() * closeBitmap.getHeight()];
        closeBitmap.getPixels(closePixels, 0, closeBitmap.getWidth(), 0, 0, closeBitmap.getWidth(), closeBitmap.getHeight());
        OverlayGraphic closeButton = new OverlayGraphic(OVR_BTN_CLOSE, closePixels, display.getWidth() - closeBitmap.getWidth(), 0, closeBitmap.getWidth(), closeBitmap.getHeight(), false);
        closeBitmap.recycle();

        Bitmap questionLabelBitmap = BitmapFactory.decodeResource(fContext.getResources(), R.drawable.question);
        int[] questionLabelPixels = new int[questionLabelBitmap.getWidth() * questionLabelBitmap.getHeight()];
        questionLabelBitmap.getPixels(questionLabelPixels, 0, questionLabelBitmap.getWidth(), 0, 0, questionLabelBitmap.getWidth(), questionLabelBitmap.getHeight());
        OverlayGraphic questionLabelGraphic = new OverlayGraphic(OVR_IMG_QUESTION, questionLabelPixels, 0, 0, questionLabelBitmap.getWidth(), questionLabelBitmap.getHeight(), false);
        questionLabelBitmap.recycle();

        Bitmap overlayInstBitmap = BitmapFactory.decodeResource(fContext.getResources(), R.drawable.overlay);
        int[] overlayInstPixels = new int[overlayInstBitmap.getWidth() * overlayInstBitmap.getHeight()];
        overlayInstBitmap.getPixels(overlayInstPixels, 0, overlayInstBitmap.getWidth(), 0, 0, overlayInstBitmap.getWidth(), overlayInstBitmap.getHeight());
        OverlayGraphic overlayInstGraphic = new OverlayGraphic(OVR_IMG_INST, overlayInstPixels, (display.getWidth() / 2) - (overlayInstBitmap.getWidth() / 2),
            (display.getHeight() / 2) - (overlayInstBitmap.getHeight() / 2), overlayInstBitmap.getWidth(), overlayInstBitmap.getHeight(), false);
        overlayInstBitmap.recycle();

        Bitmap overlayTiltOnBitmap = BitmapFactory.decodeResource(fContext.getResources(), R.drawable.overlaytilton);
        int[] overlayTiltOnPixels = new int[overlayTiltOnBitmap.getWidth() * overlayTiltOnBitmap.getHeight()];
        overlayTiltOnBitmap.getPixels(overlayTiltOnPixels, 0, overlayTiltOnBitmap.getWidth(), 0, 0, overlayTiltOnBitmap.getWidth(), overlayTiltOnBitmap.getHeight());
        OverlayGraphic overlayTiltOnGraphic = new OverlayGraphic(OVR_IMG_TILT_ON, overlayTiltOnPixels, (display.getWidth() / 2) - (overlayTiltOnBitmap.getWidth() / 2),
            (display.getHeight() / 2) - (overlayTiltOnBitmap.getHeight() / 2), overlayTiltOnBitmap.getWidth(), overlayTiltOnBitmap.getHeight(), false);
        overlayTiltOnBitmap.recycle();

        Bitmap overlayTiltOffBitmap = BitmapFactory.decodeResource(fContext.getResources(), R.drawable.overlaytiltoff);
        int[] overlayTiltOffPixels = new int[overlayTiltOffBitmap.getWidth() * overlayTiltOffBitmap.getHeight()];
        overlayTiltOffBitmap.getPixels(overlayTiltOffPixels, 0, overlayTiltOffBitmap.getWidth(), 0, 0, overlayTiltOffBitmap.getWidth(), overlayTiltOffBitmap.getHeight());
        OverlayGraphic overlayTiltOffGraphic = new OverlayGraphic(OVR_IMG_TILT_OFF, overlayTiltOffPixels, (display.getWidth() / 2) - (overlayTiltOffBitmap.getWidth() / 2),
            (display.getHeight() / 2) - (overlayTiltOffBitmap.getHeight() / 2), overlayTiltOffBitmap.getWidth(), overlayTiltOffBitmap.getHeight(), false);
        overlayTiltOffBitmap.recycle();

        fOverlayGraphicsList.add(exitButton);
        fOverlayGraphicsList.add(questionButton);
        fOverlayGraphicsList.add(tiltButton);
        fOverlayGraphicsList.add(closeButton);
        fOverlayGraphicsList.add(questionLabelGraphic);
        fOverlayGraphicsList.add(overlayInstGraphic);
        fOverlayGraphicsList.add(overlayTiltOnGraphic);
        fOverlayGraphicsList.add(overlayTiltOffGraphic);
    }

    /**
     * Returns the list of overlay graphics to the caller as a list.
     */
    public List<OverlayGraphic> getOverlayButtonsList() {
        return fOverlayGraphicsList;
    }

    /**
     * Returns the graphic specified by the id.
     */
    public OverlayGraphic getGraphic(int graphicId) {
        for (OverlayGraphic overlayGraphic: fOverlayGraphicsList) {
            if (overlayGraphic.getId() == graphicId) {
                 return overlayGraphic;
            }
        }
        return null;
    }

    /**
     * Activates or deactivates the specified graphic.
     */
    public void setActive(int graphicId, boolean active) {
        OverlayGraphic graphic = getGraphic(graphicId);
        graphic.setActive(active);
    }

    /**
     * Draws the specified graphic the canvas.
     */
    public void drawGraphic(Canvas canvas, int graphicId) {
        OverlayGraphic overlayGraphic = getGraphic(graphicId);
        overlayGraphic.drawGraphic(canvas);
    }

    /**
     * Draws all active graphics.
     */
    public void drawAllActiveGraphics(Canvas canvas) {
        for (OverlayGraphic overlayGraphic : fOverlayGraphicsList) {
            if (overlayGraphic.isActive()) {
                drawGraphic(canvas, overlayGraphic.getId());
            }
        }
    }

    /**
     * Returns the graphic index of the graphic item found at the x and y coordinate.  This method ignores inactive graphics.
     * Please note that some graphics occupy the same screen position, but appear on different screens at different
     * times.  It is the responsibility of the caller to call setActive() at the appropriate times to turn on and
     * off buttons appropriately so this routine returns the properly clicked on and active button.
     */
    public int getIndexOfGraphicAt(int x, int y) {
        for (OverlayGraphic overlayGraphic : fOverlayGraphicsList) {
            if (overlayGraphic.isActive()) {
                Rect buttonRect = new Rect(overlayGraphic.getXOffset(), overlayGraphic.getYOffset(),
                    overlayGraphic.getXOffset() + overlayGraphic.getWidth(), overlayGraphic.getYOffset() + overlayGraphic.getHeight());
                if (!buttonRect.contains(x, y)) continue;

                if (overlayGraphic.getId() == OVR_BTN_EXIT) {
                    return OVR_BTN_EXIT;
                }
                if (overlayGraphic.getId() == OVR_BTN_QUESTION) {
                    return OVR_BTN_QUESTION;
                }
                if (overlayGraphic.getId() == OVR_BTN_TILT) {
                    return OVR_BTN_TILT;
                }
                if (overlayGraphic.getId() == OVR_IMG_TILT_ON) {
                    return OVR_IMG_TILT_ON;
                }
                if (overlayGraphic.getId() == OVR_IMG_TILT_OFF) {
                    return OVR_IMG_TILT_OFF;
                }
                if (overlayGraphic.getId() == OVR_BTN_CLOSE) {
                    return OVR_BTN_CLOSE;
                }
            }
        }
        return OVR_NOT_FOUND;
    }
}



