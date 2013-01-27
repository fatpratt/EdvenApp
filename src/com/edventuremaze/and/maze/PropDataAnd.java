package com.edventuremaze.and.maze;

import android.content.Context;
import android.content.ContextWrapper;
import com.edventuremaze.factories.ImagePixelsFactory;
import com.edventuremaze.and.utils.FileUtilsAnd;
import com.edventuremaze.maze.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 * This class is the Android specific implementation of Prop Data which contains the arial view of the prop data and all
 * prop image pixels.
 *
 * @author brianpratt
 */
public class PropDataAnd implements PropData {
    static final String sLogLabel = "--->PropDataAnd:";

    PlatformAnd fPlatform;
    String fFolder;
    int fMapHeight;
    int fMapWidth;
    int fMapWidthShift;
    String fWallFileName;

    char fPropData[];                           // like mapData, this is an arial view of prop locations
    static int fNumPropImgs = 1;                // number of different prop images
    HashMap<String, ImagePixels> fPropHashMap = new HashMap<String, ImagePixels>(); // pixels for each prop image

    /**
     * Constructor - loads the prop data from file and all prop image pixels.
     * @param platform  The os specific connector object.
     * @param folder The folder where the prop data is found.
     */
    public PropDataAnd(Platform platform, String folder, int mapHeight, int mapWidth, int mapWidthShift, String wallFileName) {
        fPlatform = (PlatformAnd)platform;
        fFolder = folder;
        fMapHeight = mapHeight;
        fMapWidth = mapWidth;
        fMapWidthShift = mapWidthShift;
        fWallFileName = wallFileName;
        loadProps();
        getPropPixels();
    }

    /**
     * Returns the total number of different prop images.
     */
    public int getNumPropImgs() {
        return fNumPropImgs;
    }

    /**
     *  Loads the arial view of the props from file.
     */
    protected void loadProps() {
        Context context = ((PlatformAnd)fPlatform).getContext();
        ContextWrapper cw = new ContextWrapper(context);
        File path = cw.getDir(fFolder + fPlatform.getFolderSuffix(), Context.MODE_PRIVATE);

        String fullFileName = FileUtilsAnd.appendSlash(path.toString()) + PROP_DATA_FILE;

        BufferedReader buffReader = null;
        String nextLine = "";
        try {
            int lineNum = 0;
            String completeFile = "";
            boolean everythingOkay = true;
            buffReader = new BufferedReader(new FileReader(fullFileName));
            while ((nextLine = buffReader.readLine()) != null) {
                nextLine = FileUtilsAnd.stripOffSlash(nextLine);
                if (0 == nextLine.length()) continue;
                lineNum++;

                if (nextLine.length() != fMapWidth) {
                    fPlatform.logInfo(sLogLabel, "Line # " + lineNum + " in file '" + PROP_DATA_FILE + "' is inconsistent with ");
                    fPlatform.logInfo(sLogLabel, "the first line of the '" + fWallFileName + "' file.");
                    fPlatform.logInfo(sLogLabel, "Each line in the files must be the exact same length.");
                    break;
                }
                nextLine.toUpperCase();
                completeFile += nextLine;
            }
            buffReader.close();

            fPlatform.logInfo(sLogLabel, "num lines in prop file is: " + lineNum);
            if (lineNum != fMapHeight) {
                fPlatform.logInfo(sLogLabel, "The number of lines in the file '" + PROP_DATA_FILE + "' doesn't match the ");
                fPlatform.logInfo(sLogLabel, "number of lines in the file '" + fWallFileName + ".'");
                fPlatform.logInfo(sLogLabel, "The number of lines in the files must be equal.");
                everythingOkay = false;
            }
            if (everythingOkay) {
                fPropData = completeFile.toCharArray();
                setAllTimeHighImageNum();
            }

        } catch (Exception e) {
            fPlatform.logError(sLogLabel, "Unable to read from file '" + fullFileName + ".'");
            fPlatform.logError(sLogLabel, "Make sure the file exists and is present with runtime files.");
        }
    }

    /**
     *  Sets member variable based upon the highest image number encountered in the prop data.
     */
    protected void setAllTimeHighImageNum() {
        for (int i = 0; i < fPropData.length; i++) {
            int curVal = MathUtils.base36ToBase10(fPropData[i]);
            fNumPropImgs = Math.max(curVal, fNumPropImgs);
        }
        fPlatform.logInfo(sLogLabel, "number of prop images expected is: " + fNumPropImgs);
    }

    /**
     * Returns true if the specified map item is a prop.
     */
    public boolean isProp(int propPos) {
        return (!(fPropData[propPos] == '0'));
    }

    /**
     * Returns the value at the specified position
     */
    public char getValue(int propPos) {
        if (propPos < 0 || propPos >= fMapHeight << fMapWidthShift) return ('0');
        else return (fPropData[propPos]);
    }

    /**
     * Loads all prop images and grabs their pixels and puts them in an accessible HashMap indexed by
     * the base 36 image number.
     */
    protected void getPropPixels() {
        String propFullImgName = "";
        for (int i = 1; i <= fNumPropImgs; i++) {     // grab all prop images
            String b36 = MathUtils.base10ToBase36(i);
            propFullImgName = "Prop" + b36 + ".gif";
            ImagePixels pixels = ImagePixelsFactory.createImagePixels(fPlatform, fFolder, propFullImgName);
            if (null == pixels) {
                 fPlatform.logInfo(sLogLabel, "Unable to load prop image(s). Make sure the files Prop1.gif through");
                 fPlatform.logInfo(sLogLabel, propFullImgName + " exist and are present with runtime files.");
                return;
            }

            if (pixels.getWidth() != MazeGlobals.WALL_HEIGHT) {
                 fPlatform.logInfo(sLogLabel, "Prop image '" + propFullImgName + "' must be "
                        + MazeGlobals.WALL_HEIGHT + " pixels wide.");
                return;
            }

            if (pixels.getHeight() != MazeGlobals.WALL_HEIGHT) {
                 fPlatform.logInfo(sLogLabel, "Prop image '" + propFullImgName + "' must be "
                        + MazeGlobals.WALL_HEIGHT + " pixels high.");
                return;
            }

            fPropHashMap.put(b36, pixels);  // store all pixels in table accessible by string
        }
    }

    /**
     * Returns the specified prop image pixels base upon the base 36 image number.
     */
    public ImagePixels getImagePixelsForProp(String propId) {
        return fPropHashMap.get(propId);
    }
}
