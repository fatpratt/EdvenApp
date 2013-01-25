package com.edventuremaze.applet.maze;

import com.edventuremaze.maze.Platform;
import com.edventuremaze.maze.VibrateEffects;

import javax.crypto.NullCipher;

/**
 * This class is used for vibration effects on devices that support it.  However, applets don't currently support this
 * effect, so it is unused in this implementation.
 *
 * @author brianpratt
 */

public class VibrateEffectsApplet implements VibrateEffects {

    final static String sLogLabel = "--->VibrateEffectsApplet:";
    private Platform fPlatform;

    /**
     * Constructor
     *
     * @param platform The os specific connector object.
     */
    public VibrateEffectsApplet(Platform platform) {
        fPlatform = platform;
    }

    /**
     * Not used in this implementation of SoundEffects.
     */
    public void vibrate(int milliseconds) {
    }

}