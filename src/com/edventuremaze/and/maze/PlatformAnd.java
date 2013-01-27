package com.edventuremaze.and.maze;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.edventuremaze.maze.Platform;

/**
 * This class is the Android specific implementation of the Platform which contains input / output routines specific
 * to the Android platform.
 *
 * @author brianpratt
 */
public class PlatformAnd implements Platform {
    final static String X2_FOLDERNAME_SUFFIX = "EXII";

    Context fContext;
    Activity fCurActivity;
    boolean fX2;                // whether we are using images / configurations for higher res (2 times bigger)

    public PlatformAnd(Context context, Activity curActivity, boolean x2) {
        fContext = context;
        fCurActivity = curActivity;
        fX2 = x2;
    }

    public Context getContext() {
        return fContext;
    }

    public Activity getCurActivity() {
        return fCurActivity;
    }

    public void logError(String label, String msg) {
        Log.e(label, msg);
    }

    public void logInfo(String label, String msg){
        Log.i(label, msg);
    }

    public void logDebug(String label, String msg) {
        Log.d(label, msg);
    }

    public void logWarn(String label, String msg){
        Log.w(label, msg);
    }

    public void logFatal(String label, String msg){
        Log.e(label, msg);
    }

    public boolean isX2() {
        return fX2;
    }

    /**
     * Returns true if this platform uses system sound files.
     * Note: system sound effects (Sound1.wav, Sound2.wav, etc) are for general sound
     * effects used by the system and differ from other sound effects that are specific to a given maze
     */
    public boolean usingSysSoundFiles() {
        return true;
    }

    /**
     * Higher res images and configurations are saved in their own folder using a folder name suffix to
     * differentiate them from low res images.
     */
    public String getFolderSuffix() {
        return(fX2 ? X2_FOLDERNAME_SUFFIX : "");
    }
}
