package com.edventuremaze.maze;

import com.edventuremaze.factories.FileUtilsFactory;
import com.edventuremaze.factories.ImagePixelsFactory;
import com.edventuremaze.factories.IniFileFactory;
import com.edventuremaze.utils.FileUtils;
import com.edventuremaze.utils.IniFile;

import java.util.ArrayList;

/**
 * Holds all general configuration information as established in the MazeConfig.ini file which primarily contains
 * traps and destinations.  Contained within are backgrounds, sound effect audio files, landscapes, and overlay files.
 *
 * @author brianpratt
 */
public class MazeConfig {
    static final String sLogLabel = "--->MazeConfig:";

    protected String MAZE_CONFIG_INI_FILE = "MazeConfig.ini";
    protected IniFile fConfigFile;
    protected String fMazeId;
    protected Platform fPlatform;

    private ImageFileCache fBackgroundCache;     // container of all background image pixels from all background files
    private ImageFileCache fOverlayCache;        // container of all overlay image pixels from all overlay files
    private ImageFileCache fLandscapeCache;      // container of all landscape image pixels from all landscape files
    private SoundEffects fSoundEffects;          // container of all sound effects

    protected int fNumTraps;
    protected int fNumDests;

    protected ArrayList<Trap> fTraps;
    protected ArrayList<Dest> fDests;

    protected FileUtils fFileUtils;  // FileUtils object used throughout to access file system

    /**
     * Constructor - loads the MazeConfig.ini file.
     * @param platform  The os specific connector object.
     * @param mazeId Id of the maze.
     * @param mapHeight Height of map.
     * @param mapWidth Width of map.
     * @param soundEffects Pool of sound files and player.
     */
    public MazeConfig(Platform platform, String mazeId, int mapHeight, int mapWidth, SoundEffects soundEffects) {
        fConfigFile = IniFileFactory.createIniFileObj(platform, mazeId, MAZE_CONFIG_INI_FILE);
        fMazeId = mazeId;
        fPlatform = platform;

        // need to establish a FileUtils object for use through out this class in verifying files
        fFileUtils = FileUtilsFactory.createFileUtils(platform);

        String ver = fConfigFile.getValue("General", "Ver", "1.3");
        if (!ver.equals(GeneralConfig.MAUTH_VER_NUM)) {
            platform.logError(sLogLabel, "Questions.ini file may be out of date. Expected");
            platform.logError(sLogLabel, "version ('Ver') is 1.3 but version found is " + ver + ".");
        }

        fNumTraps = fConfigFile.getIntValue("General", "NumTraps", 0);
        fNumDests = fConfigFile.getIntValue("General", "NumDests", 0);

        fTraps = new ArrayList<Trap>();
        fDests = new ArrayList<Dest>();

        if (fNumDests <= 0) {
            platform.logError(sLogLabel, "Number of destinations ('NumDests') in MazeConfig.ini file");
            platform.logError(sLogLabel, "is zero. You must have at least one for starting coordinates.");
            return;
        }

        fSoundEffects = soundEffects;

        for (int i = 0; i < fNumDests; i++)
            fDests.add(getDest(i, mapWidth, mapHeight));
        for (int i = 0; i < fNumTraps; i++)
            fTraps.add(getTrap(i, mapWidth, mapHeight, fNumDests));

        fBackgroundCache = createBackgroundCache();
        fOverlayCache = createOverlayCache();
        fLandscapeCache = createLandscapeCache();
    }

    /**
     * If the coordinates are inside the trap then this function returns the trap, otherwise return null.
     */
    public Trap insideATrap(int x, int y) {
        for (int i = 0; i < fTraps.size(); i++) {
            Trap curTrap = (Trap) fTraps.get(i);
            if (curTrap.insideThisTrap(x, y)) return curTrap;
        }
        return null;
    }

    /**
     * After all the information is read in from file, this public method is called by maze routines
     * to get the destination specified by number.
     */
    public Dest advanceToDest(int destNum) {
        if (destNum >= 0 && destNum < fDests.size())
            return (Dest) fDests.get(destNum);
        else return null;
    }

    /**
     * Returns the cache of background image pixels.
     */
    public ImageFileCache getBackgroundCache() {
        return fBackgroundCache;
    }

    /**
     * Returns the cache of overlay image pixels.
     */
    public ImageFileCache getOverlayCache() {
        return fOverlayCache;
    }

    /**
     * Returns the cache of landscape image pixels.
     */
    public ImageFileCache getLandscapeCache() {
        return fLandscapeCache;
    }

    /**
     * Returns the cache of sound effects.
     */
    public SoundEffects getfSoundEffects() {
        return fSoundEffects;
    }

    /**
     * Examines all destination objects and creates a background cache of all image pixels needed for all destinations
     * with file based backgrounds.
     */
    private ImageFileCache createBackgroundCache() {
        ImageFileCache cache = new ImageFileCache();
        for (int i = 0; i < fDests.size(); i++) {
            Dest dest = fDests.get(i);
            if (dest.isBackgroundFromFile()) {
                if (!cache.contains(dest.getBackgroundFile())) {
                    cache.add(dest.getBackgroundFile(), ImagePixelsFactory.createImagePixels(fPlatform, fMazeId, dest.getBackgroundFile()));
                }
            }
        }
        return cache;
    }

    /**
     * Examines all trap objects and creates an overly cache of all image pixels needed for all traps with overlays.
     */
    private ImageFileCache createOverlayCache() {
        ImageFileCache cache = new ImageFileCache();
        for (int i = 0; i < fTraps.size(); i++) {
            Trap trap = fTraps.get(i);
            if (trap.isUsingOverlay()) {
                if (!cache.contains(trap.getOverlayFile())) {
                    cache.add(trap.getOverlayFile(), ImagePixelsFactory.createImagePixels(fPlatform, fMazeId, trap.getOverlayFile()));
                }
            }
        }
        return cache;
    }

    /**
     * Examines all trap objects and creates a landscape cache of all image pixels needed for all traps with landscapes.
     */
    private ImageFileCache createLandscapeCache() {
        ImageFileCache cache = new ImageFileCache();
        for (int i = 0; i < fDests.size(); i++) {
            Dest dest = fDests.get(i);
            if (dest.isUsingALandscape()) {
                if (!cache.contains(dest.getLandscapeFile())) {
                    cache.add(dest.getLandscapeFile(), ImagePixelsFactory.createImagePixels(fPlatform, fMazeId, dest.getLandscapeFile()));
                }
            }
        }
        return cache;
    }

    /**
     * Reads the specified trap information from the appropriate section of the ini file.
     */
    private Trap getTrap(int num, int mapWidth, int mapHeight, int numDests) {
        Trap trap = new Trap();
        String section = "Trap" + num;

        // -- trap's position --

        int LeftTile = getValueCheckingRange(section, "LeftTile", 15, 0, mapWidth - 1,
                "This constraint is based upon the map width.");
        int RightTile = getValueCheckingRange(section, "RightTile", 16, 0, mapWidth - 1,
                "This constraint is based upon the map width.");
        int TopTile = getValueCheckingRange(section, "TopTile", 15, 0, mapHeight - 1,
                "This constraint is based upon the map height.");
        int BottomTile = getValueCheckingRange(section, "BottomTile", 16, 0, mapHeight - 1,
                "This constraint is based upon the map height.");

        int leftTileOffset = getValueCheckingRange(section, "LeftTileOffset", 0, 0, MazeGlobals.TILE_SIZE - 1, "");
        if (MazeGlobals.X2) leftTileOffset = leftTileOffset * 2;
        int rightTileOffset = getValueCheckingRange(section, "RightTileOffset", 0, 0, MazeGlobals.TILE_SIZE - 1, "");
        if (MazeGlobals.X2) rightTileOffset = rightTileOffset * 2;
        int topTileOffset = getValueCheckingRange(section, "TopTileOffset", 0, 0, MazeGlobals.TILE_SIZE - 1, "");
        if (MazeGlobals.X2) topTileOffset = topTileOffset * 2;
        int bottomTileOffset = getValueCheckingRange(section, "BottomTileOffset", 0, 0, MazeGlobals.TILE_SIZE - 1, "");
        if (MazeGlobals.X2) bottomTileOffset = bottomTileOffset * 2;

        trap.setLeftSide((LeftTile * MazeGlobals.TILE_SIZE) + leftTileOffset);
        trap.setRightSide((RightTile * MazeGlobals.TILE_SIZE) + rightTileOffset);
        trap.setTopSide((TopTile * MazeGlobals.TILE_SIZE) + topTileOffset);
        trap.setBottomSide((BottomTile * MazeGlobals.TILE_SIZE) + bottomTileOffset);

        // -- dest --

        trap.setGotoDest(getValueCheckingRange(section, "GotoDest", -1, 0, numDests - 1, ""));
        trap.setUsingDest(trap.getGotoDest() != -1);

        // -- sound --

        trap.setUsingSound(false);
        trap.setSoundFile(fConfigFile.getValue(section, "SoundEffect", ""));
        if ((trap.getSoundFile() != null) && (trap.getSoundFile().length() > 0)) {
            String docSoundFile = fSoundEffects.doctorFileName(trap.getSoundFile()); // hack for platform specific audio file
            if (!fFileUtils.doesFileExistOnDevice(fMazeId, docSoundFile))
                fPlatform.logError(sLogLabel, "Could not find sound file on device: " + docSoundFile + ".");
            else {
                fSoundEffects.addSoundFile(trap.getSoundFile());    // queue up sound file
                trap.setUsingSound(true);
            }
        }

        // -- overlay --

        trap.setOverlayFile(fConfigFile.getValue(section, "Overlay", ""));
        trap.setUsingOverlay(trap.getOverlayFile().length() > 0);

        if (trap.isUsingOverlay()) {
            if (!fFileUtils.doesFileExistOnDevice(fMazeId, trap.getOverlayFile())) {
                fPlatform.logError(sLogLabel, "Could not find overlay file on device: " + trap.getOverlayFile() + ".");
                trap.setUsingOverlay(false);
            }
        }

        return (trap);
    }

    /**
     * Reads the specified destination information from the appropriate section of the ini file.
     */
    private Dest getDest(int num, int mapWidth, int mapHeight) {
        Dest dest = new Dest();
        String section = "Dest" + num;

        //-----position variables------

        int XTile = getValueCheckingRange(section, "XTile", 10, 0, mapWidth - 1,
                "This constraint is based upon the map width.");
        int YTile = getValueCheckingRange(section, "YTile", 10, 0, mapHeight - 1,
                "This constraint is based upon the map height.");

        int xTileOffset = getValueCheckingRange(section, "XTileOffset", 0, 0, MazeGlobals.TILE_SIZE - 1, "");
        if (MazeGlobals.X2) xTileOffset = xTileOffset * 2;
        int yTileOffset = getValueCheckingRange(section, "YTileOffset", 0, 0, MazeGlobals.TILE_SIZE - 1, "");
        if (MazeGlobals.X2) yTileOffset = yTileOffset * 2;
        int angle = getValueCheckingRange(section, "Angle", -1, 0, 359, "");

        // sometimes maze designers wants to keep player's current angle
        // if no angle specified then just use the player's existing angle
        if (angle == -1) {
            dest.setUseExistingAngle(true);
            angle = 45;
        } else dest.setUseExistingAngle(false);

        dest.setXPos((XTile * MazeGlobals.TILE_SIZE) + xTileOffset);
        dest.setYPos((YTile * MazeGlobals.TILE_SIZE) + yTileOffset);
        dest.setAngle(Trig.degreesToMazeAngleUnits(angle));

        // ----- gather landscape information ------

        dest.setLandscapeFile(fConfigFile.getValue(section, "Landscape", ""));
        dest.setUsingALandscape(dest.getLandscapeFile().length() > 0);
        dest.setUseExistingLandscape(!(dest.isUsingALandscape()));
        if (dest.isUsingALandscape()) {
            dest.setLandscapeOffsetFromTop(fConfigFile.getIntValue(section, "LandscapeOffsetFromTop", 0));
            if (MazeGlobals.X2) dest.setLandscapeOffsetFromTop(dest.getLandscapeOffsetFromTop() * 2);
            int landscapeStartAngle = fConfigFile.getIntValue(section, "LandscapeStartAngle", 0);
            dest.setLandscapeStartAngle(Trig.degreesToMazeAngleUnits(landscapeStartAngle));
            if (!fFileUtils.doesFileExistOnDevice(fMazeId, dest.getLandscapeFile())) {
                fPlatform.logError(sLogLabel, "Could not find landscape file on device: " + dest.getLandscapeFile() + ".");
                dest.setUsingALandscape(false);
            }
        }

        // ----- gather background information ------

        dest.setBackgroundFile(fConfigFile.getValue(section, "Background", ""));
        dest.setBackgroundFromFile(dest.getBackgroundFile().length() > 0);
        dest.setBackgroundFromRGB(!(dest.isBackgroundFromFile()));

        if (dest.isBackgroundFromRGB()) {   // gather RGB values from ini file
            Boolean objBoolMissing = new Boolean(false);
            dest.setSkyRed(getValueCheckingRangeFlagMissing(section, "SkyRed", 40, 0, 255, "", objBoolMissing));
            dest.setSkyGreen(getValueCheckingRangeFlagMissing(section, "SkyGreen", 125, 0, 255, "", objBoolMissing));
            dest.setSkyBlue(getValueCheckingRangeFlagMissing(section, "SkyBlue", 225, 0, 255, "", objBoolMissing));
            dest.setGroundRed(getValueCheckingRangeFlagMissing(section, "GroundRed", 100, 0, 255, "", objBoolMissing));
            dest.setGroundGreen(getValueCheckingRangeFlagMissing(section, "GroundGreen", 80, 0, 255, "", objBoolMissing));
            dest.setGroundBlue(getValueCheckingRangeFlagMissing(section, "GroundBlue", 40, 0, 255, "", objBoolMissing));
            dest.setSkyRedStep(getValueCheckingRange(section, "SkyRedStep", 2, -10, 10, ""));
            if (MazeGlobals.X2) dest.setSkyRedStep(Math.round((float)dest.getSkyRedStep() / 2.0f));
            dest.setSkyGreenStep(getValueCheckingRange(section, "SkyGreenStep", 0, -10, 10, ""));
            if (MazeGlobals.X2) dest.setSkyGreenStep(Math.round((float)dest.getSkyGreenStep() / 2.0f));
            dest.setSkyBlueStep(getValueCheckingRange(section, "SkyBlueStep", 0, -10, 10, ""));
            if (MazeGlobals.X2) dest.setSkyBlueStep(Math.round((float)dest.getSkyBlueStep() / 2.0f));

            dest.setGroundRedStep(getValueCheckingRange(section, "GroundRedStep", 1, -10, 10, ""));
            if (MazeGlobals.X2) dest.setGroundRedStep(Math.round((float)dest.getGroundRedStep() / 2.0f));
            dest.setGroundGreenStep(getValueCheckingRange(section, "GroundGreenStep", 1, -10, 10, ""));
            if (MazeGlobals.X2) dest.setGroundGreenStep(Math.round((float)dest.getGroundGreenStep() / 2.0f));
            dest.setGroundBlueStep(getValueCheckingRange(section, "GroundBlueStep", 1, -10, 10, ""));
            if (MazeGlobals.X2) dest.setGroundBlueStep(Math.round((float)dest.getGroundBlueStep() / 2.0f));
            dest.setUseExistingBackground(objBoolMissing.booleanValue());  // no background file specified and no RGB values
        }

        if (dest.isBackgroundFromFile()) {
            if (!fFileUtils.doesFileExistOnDevice(fMazeId, dest.getBackgroundFile())) {
                fPlatform.logError(sLogLabel, "Could not find background file on device: " + dest.getBackgroundFile() + ".");
            }
        }

        // if we are getting the first destination then we do some additional checking
        if (num == 0) {
            System.out.println("[Dest0] | fXPos: " + dest.getXPos());
            System.out.println("[Dest0] | fYPos:" + dest.getYPos());
            System.out.println("[Dest0] | fAngle:" + dest.getAngle());
            if (dest.isUseExistingBackground()) {     // no background file specified and no RGB values
                fPlatform.logError(sLogLabel, "[Dest0] in MazeConfig.ini requires either a 'Background' value");
                fPlatform.logError(sLogLabel, "or all Red, Green, Blue background values must be specified.");
            }
        }

        return (dest);
    }

    /**
     * Gets the specified value from the MazeConfig.ini file checking the specified range.
     * This version of this method sets flag if we are missing data.
     *  NOTE:  this method doesn't work correctly if -1 is a valid value
     */
    private int getValueCheckingRangeFlagMissing(String strSection, String strKey, int nDefault,
                int nLowEnd, int nHighEnd, String strAdditionalMsg, Boolean flag) {
        int retValue = getValueCheckingRange(strSection, strKey, -1, nLowEnd, nHighEnd, strAdditionalMsg);
        if (retValue == -1) {
            flag = new Boolean(true);
            retValue = nDefault;
        }
        return retValue;
    }

    /**
     * Gets the specified value from the MazeConfig.ini file. If the value isn't found this routine will return the
     * specified default value.  This method allows you to specify a range and if the value read in
     * does not fall within that range then a message will appear. To suppress a message pass in a default value
     * that is itself out of range.
     */
    private int getValueCheckingRange(String strSection, String strKey, int nDefault,
                int nLowEnd, int nHighEnd, String strAdditionalMsg) {
        int nValue = fConfigFile.getIntValue(strSection, strKey, nDefault);
        if (nValue > nHighEnd || nValue < nLowEnd) {
            if (nValue == nDefault) return nValue;  // we don't need a message if default is out of range
            fPlatform.logError(sLogLabel, strKey + " in " + strSection + " of MazeConfig.ini");
            fPlatform.logError(sLogLabel, "must be in the range " + nLowEnd + " to " + nHighEnd + ".");
            fPlatform.logError(sLogLabel, strAdditionalMsg);
            fPlatform.logError(sLogLabel, "Using default values.");
            nValue = nDefault;
        }
        return nValue;
    }

}
