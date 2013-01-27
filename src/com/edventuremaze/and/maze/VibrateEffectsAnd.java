package com.edventuremaze.and.maze;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import com.edventuremaze.maze.Platform;
import com.edventuremaze.maze.VibrateEffects;

/**
 * This class is used for vibration effects on the android device.
 *
 * @author brianpratt
 */

public class VibrateEffectsAnd implements VibrateEffects {

    final static String sLogLabel = "--->VibrateEffects:";
    Platform fPlatform;
    Vibrator fVibService;

    /**
     * Constructor
     *
     * @param platform The os specific connector object.
     */
    public VibrateEffectsAnd(Platform platform) {
        fPlatform = platform;
        Context context = ((PlatformAnd)fPlatform).getContext();
        Activity activity = ((PlatformAnd) fPlatform).getCurActivity();
        fVibService = (Vibrator)activity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * Vibrates for the specified duration
     */
    public void vibrate(int milliseconds) {
        fVibService.vibrate(milliseconds);
    }

}