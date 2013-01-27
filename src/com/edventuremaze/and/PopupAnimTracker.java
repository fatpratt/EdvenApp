package com.edventuremaze.and;

import android.util.Log;
import android.widget.Filter;

import javax.crypto.NullCipher;

/**
 * Class used to track the popup animation sequence (which is a telescoping animation) when the pop-up
 * question window appears.
 *
 * @author brianpratt
 */
public class PopupAnimTracker {
    final static String sLogLabel = "--->PopupAnimTracker ";

    private int fMaxWidth = 0;
    private int fMaxHeight = 0;
    private int fNumSteps = 1;

    private double fXStepSize = 0d;
    private double fYStepSize = 0d;

    private int fCurXOffset = 0;
    private int fCurYOffset = 0;

    private int fCurWidth = 0;
    private int fCurHeight = 0;

    private int fXMidPoint = 0;
    private int fYMidPoint = 0;

    private int fCurCnt = 0;
    private boolean fAllDone = false;

    private PopupAnimTrackerListener fListener = null;

    /**
     * Constructor - initializes the tracker.
     * @param maxWidth Max window width.
     * @param maxHeight Max window height.
     * @param numSteps The number of animation steps as window grows small to big.
     */
    public PopupAnimTracker(int maxWidth, int maxHeight, int numSteps, PopupAnimTrackerListener listener) {
        fMaxWidth = maxWidth;
        fMaxHeight = maxHeight;
        fNumSteps = numSteps;
        if (fNumSteps == 0) fNumSteps = 1;  // avoid div by zero

        fCurCnt = 1;

        fXMidPoint = fMaxWidth / 2;
        fYMidPoint = fMaxHeight / 2;

        fXStepSize = (double)fXMidPoint / (double)fNumSteps;
        fYStepSize = (double)fYMidPoint / (double)fNumSteps;

        fCurXOffset = fXMidPoint - (int)Math.round(fXStepSize);
        fCurYOffset = fYMidPoint - (int)Math.round(fYStepSize);

        fCurWidth = (fXMidPoint + (int)Math.round(fXStepSize)) - fCurXOffset;
        fCurHeight = (fYMidPoint + (int)Math.round(fYStepSize)) - fCurYOffset;

        fAllDone = false;
        fListener = listener;
        if (fListener != null) fListener.popupAnimModeChanged(false);
    }

    public int getCurXOffset() {
        return fCurXOffset;
    }

    public int getCurYOffset() {
        return fCurYOffset;
    }

    public int getCurWidth() {
        return fCurWidth;
    }

    public int getCurHeight() {
        return fCurHeight;
    }

    /**
     * Advances the animation sequence to the next step.  Returns True if there is more steps left.
     */
    public boolean next() {
        if (fAllDone) return false;

        fCurCnt++;
        fCurXOffset = fXMidPoint - (int)Math.round(fCurCnt * fXStepSize);
        fCurYOffset = fYMidPoint - (int)Math.round(fCurCnt * fYStepSize);

        fCurXOffset = (fCurXOffset < 0) ? 0 : fCurXOffset;
        fCurYOffset = (fCurYOffset < 0) ? 0 : fCurYOffset;

        fCurWidth = (fXMidPoint + (int)Math.round(fCurCnt * fXStepSize)) - fCurXOffset;
        fCurHeight = (fYMidPoint + (int)Math.round(fCurCnt * fYStepSize)) - fCurYOffset;


        fCurWidth = (fCurWidth > fMaxWidth) ? fMaxWidth : fCurWidth;
        fCurHeight = (fCurHeight > fMaxHeight) ? fMaxHeight : fCurHeight;

        fAllDone = (fCurXOffset == 0) && (fCurYOffset == 0) && (fCurWidth == fMaxWidth) && (fCurHeight == fMaxHeight);

        if (fAllDone && fListener != null) fListener.popupAnimModeChanged(true);        // notify listener if we are done

        return !fAllDone;
    }
}


