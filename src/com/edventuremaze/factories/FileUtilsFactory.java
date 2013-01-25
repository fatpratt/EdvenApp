package com.edventuremaze.factories;

//import com.edventuremaze.and.maze.PlatformAnd;
//import com.edventuremaze.and.utils.FileUtilsAnd;
import com.edventuremaze.applet.maze.PlatformApplet;
import com.edventuremaze.applet.utils.FileUtilsApplet;
import com.edventuremaze.maze.Platform;
import com.edventuremaze.utils.FileUtils;

/**
 * File utilities factory used to instantiate an object of appropriate platform returning a FileUtils interface.
 *
 * @author brianpratt
 */
public class FileUtilsFactory {

    /**
     * When passed in a platform object which is specific to an os or platform, this method will create an appropriate
     * FileUtils object of the specified platform.
     */
    public static FileUtils createFileUtils(Platform platform) {
//        if (platform instanceof PlatformAnd) {
//            return new FileUtilsAnd(platform);
//        }

        if (platform instanceof PlatformApplet) {
            return new FileUtilsApplet((PlatformApplet)platform);
        }

// TODO: perhaps we will handle other platforms later...
//        if (platform instanceof PlatformGWT) {
//            return new FileUtilsGWT((PlatformGWT)platform);
//        }

        return null;
    }

}
