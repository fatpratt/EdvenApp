package com.edventuremaze.and.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import com.edventuremaze.and.R;
import com.edventuremaze.and.maze.PlatformAnd;
import com.edventuremaze.maze.Platform;
import com.edventuremaze.and.maze.PlatformAnd;
import com.edventuremaze.utils.FileUtils;
import org.apache.http.util.ByteArrayBuffer;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * This module is a collection of useful routines relating to downloading and tracking files on the file system.
 *
 * @author brianpratt
 */

public class FileUtilsAnd implements FileUtils {
    final static String sLogLabel = "--->FileUtils:";

    Activity fActivity;
    PlatformAnd fPlatform;

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
                else  // we now know there was only one slash so revert back to original state
                {
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
    public FileUtilsAnd(Platform platform) {
        fPlatform = (PlatformAnd)platform;
        fActivity = ((PlatformAnd)platform).getCurActivity();
    }

    /**
	 * Returns true if the file exists on the device in the specified folder
     * @param folder Folder name.
     * @param fileName Name of the file.
     */
    public boolean doesFileExistOnDevice(String folder, String fileName) {
		ContextWrapper cw = new ContextWrapper(fActivity);
		File path = cw.getDir(folder + fPlatform.getFolderSuffix(), Context.MODE_PRIVATE);
		InputStream in = null;
		try {
			File file = new File(appendSlash(path.toString()) + fileName);
            return file.exists();
		} catch(Exception e) {
            Log.e(sLogLabel, e.getMessage());
		}
        return false;
	}

    /**
     * Updates the modified file date for all files in the specified folder.
     * @param folder Folder name.
     */
    public void touchAllFiles(String folder) {
        ContextWrapper cw = new ContextWrapper(fActivity);
        File path = cw.getDir(folder + fPlatform.getFolderSuffix(), Context.MODE_PRIVATE);
        File[] listOfFiles = path.listFiles();

        Date curDate = new Date();
	    Long curTime = curDate.getTime();   // current time in millis since 1/1/70 GMT

        for (int i = 0; i < listOfFiles.length; i++) {
		    File file = listOfFiles[i];
		    if (file.isFile()) {
                file.setLastModified(curTime);
            }
        }
    }

    /**
     * Returns the oldest file date of all files in the specified folder.
     * @param folder Folder name.
     */
    public Long getOldestFileTime(String folder) {
        ContextWrapper cw = new ContextWrapper(fActivity);
        File path = cw.getDir(folder + fPlatform.getFolderSuffix(), Context.MODE_PRIVATE);
        Log.d(sLogLabel, "checking for oldest file times in: " + path);
        File[] listOfFiles = path.listFiles();

        Date curDate = new Date();
	    Long oldestTime = curDate.getTime();   // current time in millis since 1/1/70 GMT

        // Long curTime = oldestTime;

        for (int i = 0; i < listOfFiles.length; i++) {
		    File file = listOfFiles[i];
		    if (file.isFile()) {
			    Long lastModified = file.lastModified();
                if (lastModified < oldestTime) oldestTime = lastModified;
                //  Log.d(sLogLabel, "FILE: " + file + " curTime: " + curTime + " lastModified: " + lastModified + " howOld: " +  (((curTime - lastModified)/1000.0)/3600.0) );
            }
        }

        return oldestTime;
	}

    /**
     * Downloads the specified file from the host server in the specified folder.
     * @param folder Folder name.
     * @param fileName File name.
     * @return Returns true if the download was successful.
     */
    public boolean download(String folder, String fileName, boolean x2) {
        try {
            String host = appendSlash(fActivity.getString(R.string.maze_host_url)) + fActivity.getString(x2 ? R.string.maze_dir_x2 : R.string.maze_dir);
            URL url = new URL(appendSlash(appendSlash(host) + folder) + fileName);

            long startTime = System.currentTimeMillis();
            URLConnection ucon = url.openConnection();

            // define InputStreams to read from the URLConnection
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            // read bytes to the buffer until there is nothing more to read(-1)
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            ContextWrapper cw = new ContextWrapper(fActivity);
            File path = cw.getDir(folder + fPlatform.getFolderSuffix(), Context.MODE_PRIVATE);

            // Write the bits to the file
            OutputStream os = new FileOutputStream(path + "/" + fileName);
            os.write(baf.toByteArray());
            os.close();

            Log.d(sLogLabel, "download ending: " + appendSlash(folder) + fileName + " " + (System.currentTimeMillis() - startTime) + " millis");

        } catch (Exception e) {
            Log.d(sLogLabel, "Error: " + e);
            return false;
        }
        return true;
    }

}
