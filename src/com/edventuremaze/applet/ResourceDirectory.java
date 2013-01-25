package com.edventuremaze.applet;

import java.net.URL;

/**
 * Holds code base and primary directory info for applet.
 *
 * @author: brianpratt
 */
public class ResourceDirectory {
    private String fCodeBaseDir = null;
    private String fPrimaryDir = null;

    ResourceDirectory(String codeBaseDir, String primaryDir) {
        if (codeBaseDir == null || codeBaseDir.length() == 0) {
            codeBaseDir = "";
        } else {
            if ((codeBaseDir.charAt(codeBaseDir.length() - 1) != '\\')
                    && (codeBaseDir.charAt(codeBaseDir.length() - 1) != '/'))
                codeBaseDir = codeBaseDir + "/";
        }

        if (primaryDir == null || primaryDir.length() == 0) {
            primaryDir = "";
        } else {
            if ((primaryDir.charAt(primaryDir.length() - 1) != '\\')
                    && (primaryDir.charAt(primaryDir.length() - 1) != '/'))
                primaryDir = primaryDir + "/";
        }

        this.fCodeBaseDir = codeBaseDir;
        this.fPrimaryDir = primaryDir;
    }

    /**
     * Dumps directory info.
     */
    public void dumpDirInfo() {
        System.out.println("codebase is: " + fCodeBaseDir);
        System.out.println("primary dir is: " + fPrimaryDir);
    }

    /**
     *  Returns an error string if significant data is missing.
     */
    public String checkValidDirInfo() {
        String errMessage = "";
        if (fPrimaryDir == null || fPrimaryDir.length() <= 0)
            errMessage += "Invalid 'primaryDir' parameter for applet.";
        return errMessage;
    }

    /**
     * Returns with the path and file name.
     */
    public String getAppropPathAndFile(String fileName) {
      return fCodeBaseDir + fPrimaryDir + fileName;
    }

    /**
     * Returns URL based upon resource directory.
     */
    public URL getAppropPathURL() {
        try {
            return (new URL(fCodeBaseDir + fPrimaryDir));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}




