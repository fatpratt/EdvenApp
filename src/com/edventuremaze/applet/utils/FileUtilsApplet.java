package com.edventuremaze.applet.utils;

import com.edventuremaze.applet.maze.PlatformApplet;
import com.edventuremaze.maze.Platform;
import com.edventuremaze.utils.FileUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * This module is a collection of useful routines relating to downloading and tracking files on the file system.
 *
 * @author brianpratt
 */

public class FileUtilsApplet implements FileUtils {
    final static String sLogLabel = "--->FileUtils:";

    PlatformApplet fPlatform;

    /**
	 * Helpful static method:  Appends a slash to the end of a path, if needed.
     * @param input Directory path.
	 */
    public static String appendSlash(String input) {
        if (input.length() == 0) return "/";
        if (input.charAt(input.length() - 1) == '/')
            return input;
        else
            return input + '/';
    }

    /**
     * Helpful static method: Remove leading comment slashes in a string.
     */
    public static String stripOffSlash(String str) {
        final int STATE_NO_SLASH = 0;
        final int STATE_FIRST_SLASH = 1;
        final int STATE_SECOND_SLASH = 2;

        int state = STATE_NO_SLASH;
        String outStr = "";
        for (int i = 0; i < str.length(); i++) {
            if ((state == STATE_NO_SLASH) && (str.charAt(i) == '/')) state = STATE_FIRST_SLASH;
            else if (state == STATE_FIRST_SLASH) {
                if (str.charAt(i) == '/') state = STATE_SECOND_SLASH;
                else { // we now know there was only one slash so revert back to original state
                    state = STATE_NO_SLASH;
                    outStr += '/';               // make up for missing slash
                    outStr += str.charAt(i);     // append current character on to end
                }
            } else if (state == STATE_SECOND_SLASH) ;   // do nothing
            else outStr += str.charAt(i);
        }
        return (outStr);
    }

    /**
	 * Constructor
	 */
    public FileUtilsApplet(Platform platform) {
        fPlatform = (PlatformApplet)platform;
    }

    /**
	 * Returns true if the file exists on the device in the specified folder
     * @param folder Folder name.
     * @param fileName Name of the file.
     */
    public boolean doesFileExistOnDevice(String folder, String fileName) {
        try {
            String fullFileName = fPlatform.getResourceDir().getAppropPathAndFile(fileName);
            URL namesFile = new URL(fullFileName);
            InputStream is = namesFile.openStream();
            MyDataInputStream dis = new MyDataInputStream(is);
            dis.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * NOTE: this method not implemented for applets.
     * Updates the modified file date for all files in the specified folder.
     * @param folder Folder name.
     */
    public void touchAllFiles(String folder) {
    }

    /**
     * NOTE: this method not implemented for applets.
     * Returns the oldest file date of all files in the specified folder.
     * @param folder Folder name.
     */
    public Long getOldestFileTime(String folder) {
        return 0l;
	}

    /**
     * NOTE: this method not implemented for applets.
     * Downloads the specified file from the host server in the specified folder.
     * @param folder Folder name.
     * @param fileName File name.
     * @return Returns true if the download was successful.
     */
    public boolean download(String folder, String fileName, boolean x2) {
        return false;
    }

}
