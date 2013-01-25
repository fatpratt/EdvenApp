package com.edventuremaze.factories;

//import com.edventuremaze.and.maze.PlatformAnd;
//import com.edventuremaze.and.maze.SoundEffectsAnd;
import com.edventuremaze.applet.maze.PlatformApplet;
import com.edventuremaze.applet.maze.SoundEffectsApplet;
import com.edventuremaze.maze.Platform;
import com.edventuremaze.maze.SoundEffects;

/**
 * Sound effects factory used to instantiate an object of appropriate platform returning a SoundEffects interface.
 *
 * @author brianpratt
 */
public class SoundEffectsFactory {

    /**
     * When passed in a platform object which is specific to an os or platform, this method will create an appropriate
     * SoundEffects object of the specified platform.
     */
    public static SoundEffects createSoundEffects(Platform platform, String folder) {
//        if (platform instanceof PlatformAnd) {
//            return new SoundEffectsAnd((PlatformAnd)platform, folder);
//        }

        if (platform instanceof PlatformApplet) {
            return new SoundEffectsApplet((PlatformApplet)platform, folder);
        }

        // TODO: perhaps we will handle other platforms later...

//        if (platform instanceof PlatformGWT) {
//            return new SoundEffectsGWT((PlatformGWT)platform, folder);
//        }

        // add more code here for more platforms
        return null;
    }

}
