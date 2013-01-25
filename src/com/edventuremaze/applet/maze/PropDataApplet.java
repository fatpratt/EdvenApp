package com.edventuremaze.applet.maze;

import com.edventuremaze.applet.TextUtils;
import com.edventuremaze.applet.utils.FileUtilsApplet;
import com.edventuremaze.applet.utils.MyDataInputStream;
import com.edventuremaze.factories.ImagePixelsFactory;
import com.edventuremaze.maze.*;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.HashMap;

/**
 * This class is the Applet specific implementation of Prop Data which contains the arial view of the prop data and all
 * prop image pixels.
 *
 * @author brianpratt
 */
public class PropDataApplet implements PropData {
    static final String sLogLabel = "--->PropDataApplet:";

    private PlatformApplet fPlatform;
    private String fFolder;
    private int fMapHeight;
    private int fMapWidth;
    private int fMapWidthShift;
    private String fWallFileName;
    private Graphics fGraphics;

    private char fPropData[];                           // like mapData, this is an arial view of prop locations
    private static int fNumPropImgs = 1;                // number of different prop images
    private HashMap<String, ImagePixels> fPropHashMap = new HashMap<String, ImagePixels>(); // pixels for each prop image

    /**
     * Constructor - loads the prop data from file and all prop image pixels.
     * @param platform  The os specific connector object.
     * @param folder The folder where the prop data is found.
     */
    public PropDataApplet(Platform platform, String folder, int mapHeight, int mapWidth, int mapWidthShift, String wallFileName) {
        fPlatform = (PlatformApplet)platform;
        fFolder = folder;
        fMapHeight = mapHeight;
        fMapWidth = mapWidth;
        fMapWidthShift = mapWidthShift;
        fWallFileName = wallFileName;
        fGraphics = fPlatform.getApplet().getGraphics();

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
    void loadProps() {
        String fullPropName = fPlatform.getResourceDir().getAppropPathAndFile(PROP_DATA_FILE);
        System.out.println("fullPropName: " + fullPropName);
        try {
            URL namesFile = new URL(fullPropName);
            InputStream is = namesFile.openStream();
            MyDataInputStream dis = new MyDataInputStream(is);

            // read all lines of the file
            String line = "";
            int lineNum = 0;
            String completeFile = "";
            boolean AllIsWell = true;
            while (true) {
                line = dis.myReadLine();
                if (line == null) break;
                line = FileUtilsApplet.stripOffSlash(line);
                if (0 == line.length()) continue;
                lineNum++;

                if (line.length() != fMapWidth) {
                    TextUtils.drawErrorMessage(fGraphics, "Line # " + lineNum + " in file '" + PROP_DATA_FILE + "' is inconsistent with ");
                    TextUtils.drawErrorMessage(fGraphics, "the first line of the '" + fWallFileName + "' file.");
                    TextUtils.drawErrorMessage(fGraphics, "Each line in the files must be the exact same length.");
                    AllIsWell = false;
                    break;
                }
                line.toUpperCase();
                completeFile += line;
            }
            dis.close();
            System.out.println("num lines in prop file is: " + lineNum);
            if (lineNum != fMapHeight) {
                TextUtils.drawErrorMessage(fGraphics, "The number of lines in the file '" + PROP_DATA_FILE + "' doesn't match the ");
                TextUtils.drawErrorMessage(fGraphics, "number of lines in the file '" + fWallFileName + ".'");
                TextUtils.drawErrorMessage(fGraphics, "The number of lines in the files must be equal.");
                AllIsWell = false;
            }
            if (AllIsWell) {
                fPropData = completeFile.toCharArray();
                setAllTimeHighImageNum();
            }
        } catch (IOException e) {
            TextUtils.drawErrorMessage(fGraphics, "Unable to read from file '" + fullPropName + ".'");
            TextUtils.drawErrorMessage(fGraphics, "Make sure the file exists and is present with runtime files.");
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
                TextUtils.drawErrorMessage(fGraphics, "Unable to load prop image(s). Make sure the files Prop1.gif through");
                TextUtils.drawErrorMessage(fGraphics, propFullImgName + " exist and are present with runtime files.");
                return;
            }

            if (pixels.getWidth() != MazeGlobals.WALL_HEIGHT) {
                TextUtils.drawErrorMessage(fGraphics, "Prop image '" + propFullImgName + "' must be "
                        + MazeGlobals.WALL_HEIGHT + " pixels wide.");
                return;
            }

            if (pixels.getHeight() != MazeGlobals.WALL_HEIGHT) {
                TextUtils.drawErrorMessage(fGraphics, "Prop image '" + propFullImgName + "' must be "
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
