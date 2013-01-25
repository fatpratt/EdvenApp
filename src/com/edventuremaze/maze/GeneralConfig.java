package com.edventuremaze.maze;

import com.edventuremaze.factories.FileUtilsFactory;
import com.edventuremaze.factories.ImagePixelsFactory;
import com.edventuremaze.factories.IniFileFactory;
import com.edventuremaze.utils.FileUtils;
import com.edventuremaze.utils.IniFile;

/**
 * Holds all general configuration information found the GeneralConfig.ini file.
 *
 * @author brianpratt
 */
public class GeneralConfig {

    public static final String MAUTH_VER_NUM      = "1.3";
    public static final String FULL_MAUTH_VER_NUM = "01.30";

    public static final String BOUNCE_EFFECT   = "Sound1.wav";       // sys sound effects may not exist on all platforms
    public static final String QUESTION_EFFECT = "Sound2.wav";
    public static final String ANSWER_EFFECT   = "Sound3.wav";

    static final String sLogLabel = "--->GeneralConfig:";
    String GENERAL_CONFIG_INI_FILE = "GeneralConfig.ini";

    Platform fPlatform;
    SoundEffects fSoundEffects;
    String fMazeId;
    IniFile fGenConfigINIFile;

    private String fVer;
    private String fAuth;

    private String fBackGroundClrStr = "Black";
    private String fTextClrStr = "White";

    private String fOpeningCreditsFile;
    private String fTitleLine1 = "";
    private String fTitleLine2 = "";
    private String fTitleLine3 = "";
    private String fTitleLine4 = "";

    private String fLine1ClrStr;
    private String fLine2ClrStr;
    private String fLine3ClrStr;
    private String fLine4ClrStr;

    private int fYPosLine1;
    private int fYPosLine2;
    private int fYPosLine3;
    private int fYPosLine4;

    private int fFontSizeLine1;
    private int fFontSizeLine2;
    private int fFontSizeLine3;
    private int fFontSizeLine4;

    private int fNumSysSoundFiles = 3;
    private boolean fUsingBackgroundMusic = true;

    FileUtils fFileUtils;
    ImagePixels fOpeningCreditsImagePixels;

    /**
     * Constructor - loads the GeneralConfig.ini file.
     * @param platform  The os specific connector object.
     * @param mazeId Id of the maze.
     * @param soundEffects Pool of sound files and player
     */
    public GeneralConfig(Platform platform, String mazeId, SoundEffects soundEffects) {
        fPlatform = platform;
        fMazeId = mazeId;
        fSoundEffects = soundEffects;

        fGenConfigINIFile = IniFileFactory.createIniFileObj(platform, mazeId, GENERAL_CONFIG_INI_FILE);

        fVer = fGenConfigINIFile.getValue("General", "Ver", "1.3");
        if (!fVer.equals(MAUTH_VER_NUM)) {
            platform.logError(sLogLabel, "Questions.ini file may be out of date. Expected");
            platform.logError(sLogLabel, "version ('Ver') is 1.3 but version found is " + fVer + ".");
        }

        fAuth = fGenConfigINIFile.getValue("General", "MAuthAuth", "");
        fBackGroundClrStr = fGenConfigINIFile.getValue("QuestionViewer", "BackGroundClr", "Black");
        fTextClrStr = fGenConfigINIFile.getValue("QuestionViewer", "TextClr", "White");

        fOpeningCreditsFile = fGenConfigINIFile.getValue("OpeningCredits", "OpeningCreditsFile", "OpeningCredits.gif");
        fTitleLine1 = fGenConfigINIFile.getValue("OpeningCredits", "TitleLine1", "");
        fTitleLine2 = fGenConfigINIFile.getValue("OpeningCredits", "TitleLine2", "");
        fTitleLine3 = fGenConfigINIFile.getValue("OpeningCredits", "TitleLine3", "");
        fTitleLine4 = fGenConfigINIFile.getValue("OpeningCredits", "TitleLine4", "");

        fLine1ClrStr = fGenConfigINIFile.getValue("OpeningCredits", "ColorLine1", "Black");
        fLine2ClrStr = fGenConfigINIFile.getValue("OpeningCredits", "ColorLine2", "Black");
        fLine3ClrStr = fGenConfigINIFile.getValue("OpeningCredits", "ColorLine3", "Black");
        fLine4ClrStr = fGenConfigINIFile.getValue("OpeningCredits", "ColorLine4", "Black");

        fYPosLine1 = fGenConfigINIFile.getIntValue("OpeningCredits", "YPosLine1", 58);
        fYPosLine2 = fGenConfigINIFile.getIntValue("OpeningCredits", "YPosLine2", 84);
        fYPosLine3 = fGenConfigINIFile.getIntValue("OpeningCredits", "YPosLine3", 111);
        fYPosLine4 = fGenConfigINIFile.getIntValue("OpeningCredits", "YPosLine4", 140);

        fFontSizeLine1 = fGenConfigINIFile.getIntValue("OpeningCredits", "FontSizeLine1", 22);
        fFontSizeLine2 = fGenConfigINIFile.getIntValue("OpeningCredits", "FontSizeLine2", 16);
        fFontSizeLine3 = fGenConfigINIFile.getIntValue("OpeningCredits", "FontSizeLine3", 18);
        fFontSizeLine4 = fGenConfigINIFile.getIntValue("OpeningCredits", "FontSizeLine4", 16);

        fUsingBackgroundMusic = fGenConfigINIFile.getBoolValue("General", "UsingBackgroundMusic", true);

        fFileUtils = FileUtilsFactory.createFileUtils(fPlatform);

        loadSysSoundFiles();
        loadOpeningCreditsFile();
    }

    /**
     * Loads opening credits bitmap file.
     */
    private void loadOpeningCreditsFile() {
        if (!fFileUtils.doesFileExistOnDevice(fMazeId, fOpeningCreditsFile))
            fPlatform.logError(sLogLabel, "Unable to find opening credits file " + fOpeningCreditsFile);
        fOpeningCreditsImagePixels = ImagePixelsFactory.createImagePixels(fPlatform, fMazeId, fOpeningCreditsFile);
    }

    /**
     * Loads system sound effects. Note: system sound effects (Sound1.wav, Sound2.wav, etc) are for general sound
     * effects used by the system and differ from other sound effects that are specific to a given maze
     * (Audio1.wav, Audio2.wav, etc).
     */
    private void loadSysSoundFiles() {
        if (fPlatform.usingSysSoundFiles() == false) {
            fNumSysSoundFiles = 0;
            return;
        }

        fNumSysSoundFiles = fGenConfigINIFile.getIntValue("General", "NumSysSounds", 3);

        for (int i = 1; i <= fNumSysSoundFiles; i++) {
            String sysSoundFile = "Sound" + i + ".wav";
            if (fFileUtils.doesFileExistOnDevice(fMazeId, sysSoundFile))
                fSoundEffects.addSoundFile(sysSoundFile);
            else
                fPlatform.logError(sLogLabel, "Unable to find system sound file " + sysSoundFile);
        }
    }

    public String getAuth() {
        return fAuth;
    }

    public String getBackGroundClrStr() {
        return fBackGroundClrStr;
    }

    public String getTextClrStr() {
        return fTextClrStr;
    }

    public String getTitleLine1() {
        return fTitleLine1;
    }

    public String getTitleLine2() {
        return fTitleLine2;
    }

    public String getTitleLine3() {
        return fTitleLine3;
    }

    public String getTitleLine4() {
        return fTitleLine4;
    }

    public String getLine1ClrStr() {
        return fLine1ClrStr;
    }

    public String getLine2ClrStr() {
        return fLine2ClrStr;
    }

    public String getLine3ClrStr() {
        return fLine3ClrStr;
    }

    public String getLine4ClrStr() {
        return fLine4ClrStr;
    }

    public int getYPosLine1() {
        return fYPosLine1;
    }

    public int getYPosLine2() {
        return fYPosLine2;
    }

    public int getYPosLine3() {
        return fYPosLine3;
    }

    public int getYPosLine4() {
        return fYPosLine4;
    }

    public int getFontSizeLine1() {
        return fFontSizeLine1;
    }

    public int getFontSizeLine2() {
        return fFontSizeLine2;
    }

    public int getFontSizeLine3() {
        return fFontSizeLine3;
    }

    public int getFontSizeLine4() {
        return fFontSizeLine4;
    }

    public int getNumSysSoundFiles() {
        return fNumSysSoundFiles;
    }

    public boolean getUsingBackgroundMusic() {
        return fUsingBackgroundMusic;
    }

    /**
     * Returns the opening credits bitmap pixels.
     */
    public ImagePixels getOpeningCreditsImagePixels() {
        return fOpeningCreditsImagePixels;
    }
}
