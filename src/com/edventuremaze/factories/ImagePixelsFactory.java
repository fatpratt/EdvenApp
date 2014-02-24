package com.edventuremaze.factories;

import com.edventuremaze.and.maze.PlatformAnd;
import com.edventuremaze.and.maze.ImagePixelsAnd;
import com.edventuremaze.maze.ImagePixels;
import com.edventuremaze.maze.Platform;

/**
 * Image pixels factory used to instantiate an object of appropriate platform returning an ImagePixels interface.
 *
 * @author brianpratt
 */
public class ImagePixelsFactory {

    /**
     * When passed in a platform object which is specific to an os or platform, this method will create an appropriate
     * image pixel object of the specified platform.
     */
    public static ImagePixels createImagePixels(Platform platform, String folder, String fileName) {
        if (platform instanceof PlatformAnd) {
            return new ImagePixelsAnd((PlatformAnd)platform, folder, fileName);
        }

//        if (platform instanceof PlatformApplet) {
//            return new PlatformApplet((PlatformApplet)platform, folder, fileName);
//        }

          // TODO: perhaps we will handle other platforms later...
//        if (platform instanceof PlatformGWT) {
//            return new ImagePixelsGWT((PlatformGWT)platform, folder, fileName);
//        }
        return null;
    }

    /**
     * When passed in a platform object which is specific to an os or platform, this method will create a blank
     * image pixel object of the specified platform.
     */
    public static ImagePixels createBlankImagePixels(Platform platform) {
        if (platform instanceof PlatformAnd) {
            return new ImagePixelsAnd((PlatformAnd)platform);
        }

//        if (platform instanceof PlatformApplet) {
//            return new ImagePixelsApplet((PlatformApplet)platform);
//        }

        // TODO: perhaps we will handle other platforms later...

//        if (platform instanceof PlatformGWT) {
//            return new ImagePixelsGWT((PlatformGWT)platform);
//        }

        return null;
    }

}
