package com.edventuremaze.utils;

/**
 * This module is a collection of useful routines relating to downloading and tracking files on the file system.
 *
 * @author brianpratt
 */
public interface FileUtils {

    /**
	 * Returns true if the file exists on the device in the specified folder
     * @param folder Folder name.
     * @param fileName Name of the file.
     */
    public boolean doesFileExistOnDevice(String folder, String fileName);

    /**
     * Updates the modified file date for all files in the specified folder.
     * @param folder Folder name.
     */
    public void touchAllFiles(String folder);

    /**
     * Returns the oldest file date of all files in the specified folder.
     * @param folder Folder name.
     */
    public Long getOldestFileTime(String folder);

    /**
     * Downloads the specified file from the host server in the specified folder.
     * @param folder Folder name.
     * @param fileName File name.
     * @return Returns true if the download was successful.
     */
    public boolean download(String folder, String fileName, boolean x2);

}
