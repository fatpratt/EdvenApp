package com.edventuremaze.applet.maze;

import com.edventuremaze.applet.TextUtils;
import com.edventuremaze.applet.utils.FileUtilsApplet;
import com.edventuremaze.applet.utils.MyDataInputStream;
import com.edventuremaze.factories.ImagePixelsFactory;
import com.edventuremaze.maze.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * This class is the Applet specific implementation of the Map Data which contains the arial view of the wall data
 * and all wall image pixels.
 *
 * @author brianpratt
 */
public class MapDataApplet implements MapData {
    static final String sLogLabel = "--->MapDataApplet:";

    private PlatformApplet fPlatform;
    private String fFolder;
    private Graphics fGraphics;

    static int fMapHeight = 64;         // these are default values
    static int fMapWidth = 64;
    static int fMapWidthShift = 6;      // used for bitwise shifting to simulate div and mult

    static int fNumWallImgs = 1;        // number of different wall images

    private HashMap<String, ImagePixels> fWallHashMap = new HashMap<String, ImagePixels>(); // pixels for each wall image

    private char fMapData[];

    /**
     * Constructor - loads the maze map view and all wall data.
     * @param platform  The os specific connector object.
     * @param folder The folder where the maze map and wall files are found.
     */
    public MapDataApplet(Platform platform, String folder) {
        fPlatform = (PlatformApplet)platform;
        fFolder = folder;
        fGraphics = fPlatform.getApplet().getGraphics();

        loadMaze();         // load the wall arial view
        fPlatform.logInfo(sLogLabel, "mapData fMapWidth is: "      + fMapWidth);
        fPlatform.logInfo(sLogLabel, "mapData fMapWidthShift is: " + fMapWidthShift);
        fPlatform.logInfo(sLogLabel, "mapData fMapHeight is: "     + fMapHeight);

        getWallPixels();    // load the pixels of all walls
    }

    /**
     * Returns the maze map height.
     */
    public int getMapHeight() {
        return fMapHeight;
    }

    /**
     * Returns the maze map width.
     */
    public int getMapWidth() {
        return fMapWidth;
    }

    /**
     * Used in bitwise shifting (for mult and div), this method returns the maze map shift width.
     */
    public int getMapShiftWidth() {
        return fMapWidthShift;
    }

    /**
     * Returns the total number of different wall images.
     */
    public int getNumWallImgs() {
        return fNumWallImgs;
    }

    /**
     *  Loads the arial view of the props the maze's walls.
     */
    void loadMaze() {
        String fullWallName = fPlatform.getResourceDir().getAppropPathAndFile(MAP_DATA_FILE);
        System.out.println("fullWallName: " + fullWallName);
        try {
            URL namesFile = new URL(fullWallName);
            InputStream is = namesFile.openStream();
            MyDataInputStream dis = new MyDataInputStream(is);
            String line = "";

            // read all lines of the file
            int lineNum = 0;
            String completeFile = "";
            fMapWidth = -1;                 // denotes first time through loop and indicates failure
            while (true) {
                line = dis.myReadLine();
                if (line == null) break;
                line = FileUtilsApplet.stripOffSlash(line);
                if (0 == line.length()) continue;

                lineNum++;
                if (fMapWidth == -1) {          // set Width/WidthShift fields the first time only
                    fMapWidth = line.length();  // first line dictates the size of all lines in file
                    if (isLineLengthGood(fMapWidth)) {
                        fMapWidthShift = MathUtils.logarithmBaseTwo(fMapWidth);
                    } else {
                        fMapWidth = -1;            // flag indicates something is wrong
                        break;
                    }
                } else {                           // handle all other non-first lines here
                    if (line.length() != fMapWidth) {
                        TextUtils.drawErrorMessage(fGraphics, "Line # " + lineNum + " in file '" + MAP_DATA_FILE + "' is inconsistent with the first line of the file.");
                        TextUtils.drawErrorMessage(fGraphics, "Each line in the file must be the exact same length.");
                        fMapWidth = -1;
                        break;
                    }
                }
                line.toUpperCase();           // everything is okay with this line so add it to our collection
                completeFile += line;
            }
            dis.close();
            if (fMapWidth == -1) {              // -1 indicates something is messed up
                fMapHeight = 64;                // go back to default values
                fMapWidth = 64;
                fMapWidthShift = 6;
            } else {
                fMapHeight = lineNum;
                fMapData = completeFile.toCharArray();
                setAllTimeHighImageNum();
            }
        } catch (IOException e) {
            TextUtils.drawErrorMessage(fGraphics, "Unable to read from file '" + fullWallName + ".'");
            TextUtils.drawErrorMessage(fGraphics, "Make sure the file exists and is present with runtime files.");
        }
    }

    /**
     * Checks to make sure the line width is good - it must be a power of two so we can do the fast div and mult.
     */
    protected boolean isLineLengthGood(int width)  {
        if ((width != 16) && (width != 32) && (width != 64) && (width != 128) && (width != 256)) {
            TextUtils.drawErrorMessage(fGraphics, "The length of the first line in file '" + MAP_DATA_FILE + "' is " + width + ".");
            TextUtils.drawErrorMessage(fGraphics, "Line length must be 16, 32, 64, 128, or 256.");
            return false;
        }
        return true;
    }

    /**
     *  Sets member variable based upon the highest image number encountered in the wall data.
     */
    protected void setAllTimeHighImageNum() {
        for (int i = 0; i < fMapData.length; i++) {
            int curVal = MathUtils.base36ToBase10(fMapData[i]);
            fNumWallImgs = Math.max(curVal, fNumWallImgs);
        }
        fPlatform.logInfo(sLogLabel, "number of wall images expected is: " + fNumWallImgs);
    }

    /**
     * Returns true if the specified map item is a wall.
     */
    public boolean isWall(int mapPos) {
        if (mapPos < 0 || mapPos >= fMapData.length) return true;
        return (!(fMapData[mapPos] == '0'));
    }

    /**
     * Returns the value at the specified position
     */
    public char getValue(int mapPos) {
        if (mapPos < 0 || mapPos >= fMapHeight << fMapWidthShift) return ('0');
        else return (fMapData[mapPos]);
    }

    /**
    * Loads all wall images and grabs their pixels and puts them in an accessible HashMap indexed by
    * the base 36 image number.
    */
    protected void getWallPixels() {
        String wallFullImgName = "";
        for (int j = 1; j <= fNumWallImgs; j++) {     // grab all wall images
            String b36 = MathUtils.base10ToBase36(j);
            wallFullImgName = "Wall" + b36 + ".gif";
            ImagePixels pixels = ImagePixelsFactory.createImagePixels(fPlatform, fFolder, wallFullImgName);
            if (null == pixels) {
                TextUtils.drawErrorMessage(fGraphics, "Unable to load wall image(s). Make sure the files wall1.gif through ");
                TextUtils.drawErrorMessage(fGraphics, wallFullImgName + " exist and are present with runtime files.");
                return;
            }

            if (pixels.getWidth() != MazeGlobals.WALL_HEIGHT) {
                TextUtils.drawErrorMessage(fGraphics, "Wall image '" + wallFullImgName + "' must be "
                    + MazeGlobals.WALL_HEIGHT + " pixels wide.");
                return;
            }

            if (pixels.getHeight() != MazeGlobals.WALL_HEIGHT) {
                TextUtils.drawErrorMessage(fGraphics, "Wall image '" + wallFullImgName + "' must be "
                     + MazeGlobals.WALL_HEIGHT + " pixels high.");
             return;
            }

            fWallHashMap.put(b36, pixels);  // stores all pixels in table accessible by string
        }
    }

   /**
    * Returns the specified wall image pixels base upon the base 36 image number.
    */
    public ImagePixels getImagePixelsForWall(String wallId) {
        return fWallHashMap.get(wallId);
    }

    /**
     * Takes x, y coordinates and converts them to a position in the one dimensional array representing the aerial
     * view of the maze map.
     */
	public int convertPointToMapPos(int x, int y) {
		return ((y << fMapWidthShift) + x); // shifting makes this go faster
	}

}
