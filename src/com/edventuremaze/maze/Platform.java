package com.edventuremaze.maze;

/**
 * The platform represents generic method calls which are to be handled in a specific way by particular operating system
 * or platform such as Android, GWT or JavaApplet.
 *
 * @author brianpratt
 */
public interface Platform {
    public boolean usingSysSoundFiles();
    public void logError(String label, String msg);
    public void logInfo(String label, String msg);
    public void logDebug(String label, String msg);
    public void logWarn(String label, String msg);
    public void logFatal(String label, String msg);
}
