package com.edventuremaze.applet.maze;

import com.edventuremaze.applet.ResourceDirectory;
import com.edventuremaze.maze.Platform;

import java.applet.Applet;

/**
 * This class is the Applet specific implementation of the Platform which contains input / output routines specific
 * to the Applet platform.
 *
 * @author brianpratt
 */
public class PlatformApplet implements Platform {
    private ResourceDirectory fResourceDir;
    private Applet fApplet;
    private boolean fX2;                // whether we are using images / configurations for higher res (2 times bigger)

    public PlatformApplet(ResourceDirectory resourceDir, Applet applet, boolean x2) {
        fResourceDir = resourceDir;
        fApplet = applet;
        fX2 = x2;
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
        return false;
    }

    public ResourceDirectory getResourceDir() {
        return fResourceDir;
    }

    public Applet getApplet() {
        return fApplet;
    }

    public void logError(String label, String msg) {
        System.out.println(label + ": " +  msg);
    }

    public void logInfo(String label, String msg){
        System.out.println(label + ": " + msg);
    }

    public void logDebug(String label, String msg) {
        System.out.println(label + ": " + msg);
    }

    public void logWarn(String label, String msg){
        System.out.println(label + ": " + msg);
    }

    public void logFatal(String label, String msg){
        System.out.println(label + ": " + msg);
    }
}
