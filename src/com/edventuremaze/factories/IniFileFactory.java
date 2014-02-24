package com.edventuremaze.factories;

import com.edventuremaze.and.maze.PlatformAnd;
import com.edventuremaze.and.utils.IniFileAnd;
import com.edventuremaze.maze.Platform;
import com.edventuremaze.utils.IniFile;

/**
 * Ini file factory used to instantiate an object of appropriate platform returning an IniFile interface.
 *
 * @author brianpratt
 */
public class IniFileFactory {

    /**
     * When passed in a platform object which is specific to an os or platform, this method will create an appropriate
     * ini file object of the specified platform.
     */
    public static IniFile createIniFileObj(Platform platform, String folder, String iniFileName) {
        if (platform instanceof PlatformAnd) {
            return new IniFileAnd((PlatformAnd)platform, folder, iniFileName);
        }

//        if (platform instanceof PlatformApplet) {
//            return new IniFileApplet((PlatformApplet)platform, folder, iniFileName);
//        }

        // TODO: perhaps we will handle other platforms later...
//        if (platform instanceof PlatformGWT) {
//            return new IniFileGWT((PlatformGWT)platform, folder, iniFileName);
//        }

        return null;
    }


}
