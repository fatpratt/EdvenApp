package com.edventuremaze.factories;

import com.edventuremaze.and.maze.PlatformAnd;
import com.edventuremaze.and.maze.VibrateEffectsAnd;
import com.edventuremaze.maze.Platform;
import com.edventuremaze.maze.VibrateEffects;

/**
 * Vibrate effects factory used to instantiate an object of appropriate platform returning a VibrateEffects interface.
 *
 * @author brianpratt
 */
public class VibrateEffectsFactory {

    /**
     * When passed in a platform object which is specific to an os or platform, this method will create an appropriate
     * VibrateEffects object of the specified platform.
     */
    public static VibrateEffects createVibrateEffects(Platform platform) {
        if (platform instanceof PlatformAnd) {
            return new VibrateEffectsAnd((PlatformAnd)platform);
        }

//        if (platform instanceof PlatformApplet) {
//            return new VibrateEffectsApplet((PlatformApplet)platform);
//        }

        // TODO: perhaps we will handle other platforms later...

//        if (platform instanceof PlatformGWT) {
//            return new VibrateEffectsGWT((PlatformGWT)platform);
//        }

        // add more code here for more platforms
        return null;
    }

}
