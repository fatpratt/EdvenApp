package com.edventuremaze.factories;

//import com.edventuremaze.and.maze.MapDataAnd;
//import com.edventuremaze.and.maze.PlatformAnd;
import com.edventuremaze.applet.maze.MapDataApplet;
import com.edventuremaze.applet.maze.PlatformApplet;
import com.edventuremaze.maze.MapData;
import com.edventuremaze.maze.Platform;

/**
 * Map data factory used to instantiate an object of appropriate platform returning a MapData interface.
 *
 * @author brianpratt
 */
public class MapDataFactory {

    /**
     * When passed in a platform object which is specific to an os or platform, this method will create an appropriate
     * PropData object of the specified platform.
     */
    public static MapData createMapData(Platform platform, String folder) {

//        if (platform instanceof PlatformAnd) {
//            return new MapDataAnd((PlatformAnd)platform, folder);
//        }


        if (platform instanceof PlatformApplet) {
            return new MapDataApplet((PlatformApplet)platform, folder);
        }

        // TODO: perhaps we will handle other platforms later...

//        if (platform instanceof PlatformGWT) {
//            return new MapDataGWT((PlatformGWT)platform, folder);
//        }

        return null;
    }

}
