package com.edventuremaze.factories;

//import com.edventuremaze.and.maze.PlatformAnd;
//import com.edventuremaze.and.maze.PropDataAnd;
import com.edventuremaze.applet.maze.PlatformApplet;
import com.edventuremaze.applet.maze.PropDataApplet;
import com.edventuremaze.maze.Platform;
import com.edventuremaze.maze.PropData;

/**
 * Prop data factory used to instantiate an object of appropriate platform returning a PropData interface.
 *
 * @author brianpratt
 */
public class PropDataFactory {

    /**
     * When passed in a platform object which is specific to an os or platform, this method will create an appropriate
     * PropData object of the specified platform.
     */
    public static PropData createPropData(Platform platform, String folder, int mapHeight, int mapWidth, int mapWidthShift, String wallFileName) {
//        if (platform instanceof PlatformAnd) {
//            return new PlatformAnd((PlatformApplet)platform, folder, mapHeight, mapWidth, mapWidthShift, wallFileName);
//        }

        if (platform instanceof PlatformApplet) {
            return new PropDataApplet((PlatformApplet)platform, folder, mapHeight, mapWidth, mapWidthShift, wallFileName);
        }

        // TODO: perhaps we will handle other platforms later...

//        if (platform instanceof PlatformGWT) {
//            return new PropDataGWT((PlatformGWT)platform, folder, mapHeight, mapWidth, mapWidthShift, wallFileName);
//        }


        return null;
    }

}
