package com.edventuremaze.maze;

/**
 * Dest POJO which is a destination or position within the map consisting of background, landscape
 * and X/Y coordinates of the player.  The first destination is always the starting position of the player
 * when the map loads.
 *
 * @author brianpratt
 */
public class Dest {

    private int fXPos;
    private int fYPos;
    private int fAngle;
    private boolean fUseExistingAngle;       // no angle specified--use player's existing angle

    private boolean fBackgroundFromFile;     // true --background file specified,  false--RGB values specified
    private boolean fBackgroundFromRGB;      // false--background file specified,  true--RGB values specified
    private boolean fUseExistingBackground;  // no background specified--use existing background

    private String fBackgroundFile;          // if fBackgroundFromFile here is its file name

    private int fSkyRed;                     // if fBackgroundFromRGB here are the RGB specifics
    private int fSkyGreen;
    private int fSkyBlue;
    private int fSkyRedStep;
    private int fSkyGreenStep;
    private int fSkyBlueStep;
    private int fGroundRed;
    private int fGroundGreen;
    private int fGroundBlue;
    private int fGroundRedStep;
    private int fGroundGreenStep;
    private int fGroundBlueStep;

    private boolean fUsingALandscape;
    private boolean fUseExistingLandscape;  // no landscape specified--use existing landscape
    private String fLandscapeFile;
    private int fLandscapeOffsetFromTop;
    private int fLandscapeStartAngle;

    public int getXPos() {
        return fXPos;
    }

    public void setXPos(int fXPos) {
        this.fXPos = fXPos;
    }

    public int getYPos() {
        return fYPos;
    }

    public void setYPos(int fYPos) {
        this.fYPos = fYPos;
    }

    public int getAngle() {
        return fAngle;
    }

    public void setAngle(int fAngle) {
        this.fAngle = fAngle;
    }

    public boolean isUseExistingAngle() {
        return fUseExistingAngle;
    }

    public void setUseExistingAngle(boolean fUseExistingAngle) {
        this.fUseExistingAngle = fUseExistingAngle;
    }

    public boolean isBackgroundFromFile() {
        return fBackgroundFromFile;
    }

    public void setBackgroundFromFile(boolean fBackgroundFromFile) {
        this.fBackgroundFromFile = fBackgroundFromFile;
    }

    public boolean isBackgroundFromRGB() {
        return fBackgroundFromRGB;
    }

    public void setBackgroundFromRGB(boolean fBackgroundFromRGB) {
        this.fBackgroundFromRGB = fBackgroundFromRGB;
    }

    public boolean isUseExistingBackground() {
        return fUseExistingBackground;
    }

    public void setUseExistingBackground(boolean fUseExistingBackground) {
        this.fUseExistingBackground = fUseExistingBackground;
    }

    public String getBackgroundFile() {
        return fBackgroundFile;
    }

    public void setBackgroundFile(String fBackgroundFile) {
        this.fBackgroundFile = fBackgroundFile;
    }

    public int getSkyRed() {
        return fSkyRed;
    }

    public void setSkyRed(int fSkyRed) {
        this.fSkyRed = fSkyRed;
    }

    public int getSkyGreen() {
        return fSkyGreen;
    }

    public void setSkyGreen(int fSkyGreen) {
        this.fSkyGreen = fSkyGreen;
    }

    public int getSkyBlue() {
        return fSkyBlue;
    }

    public void setSkyBlue(int fSkyBlue) {
        this.fSkyBlue = fSkyBlue;
    }

    public int getSkyRedStep() {
        return fSkyRedStep;
    }

    public void setSkyRedStep(int fSkyRedStep) {
        this.fSkyRedStep = fSkyRedStep;
    }

    public int getSkyGreenStep() {
        return fSkyGreenStep;
    }

    public void setSkyGreenStep(int fSkyGreenStep) {
        this.fSkyGreenStep = fSkyGreenStep;
    }

    public int getSkyBlueStep() {
        return fSkyBlueStep;
    }

    public void setSkyBlueStep(int fSkyBlueStep) {
        this.fSkyBlueStep = fSkyBlueStep;
    }

    public int getGroundRed() {
        return fGroundRed;
    }

    public void setGroundRed(int fGroundRed) {
        this.fGroundRed = fGroundRed;
    }

    public int getGroundGreen() {
        return fGroundGreen;
    }

    public void setGroundGreen(int fGroundGreen) {
        this.fGroundGreen = fGroundGreen;
    }

    public int getGroundBlue() {
        return fGroundBlue;
    }

    public void setGroundBlue(int fGroundBlue) {
        this.fGroundBlue = fGroundBlue;
    }

    public int getGroundRedStep() {
        return fGroundRedStep;
    }

    public void setGroundRedStep(int fGroundRedStep) {
        this.fGroundRedStep = fGroundRedStep;
    }

    public int getGroundGreenStep() {
        return fGroundGreenStep;
    }

    public void setGroundGreenStep(int fGroundGreenStep) {
        this.fGroundGreenStep = fGroundGreenStep;
    }

    public int getGroundBlueStep() {
        return fGroundBlueStep;
    }

    public void setGroundBlueStep(int fGroundBlueStep) {
        this.fGroundBlueStep = fGroundBlueStep;
    }

    public boolean isUsingALandscape() {
        return fUsingALandscape;
    }

    public void setUsingALandscape(boolean fUsingALandscape) {
        this.fUsingALandscape = fUsingALandscape;
    }

    public boolean isUseExistingLandscape() {
        return fUseExistingLandscape;
    }

    public void setUseExistingLandscape(boolean fUseExistingLandscape) {
        this.fUseExistingLandscape = fUseExistingLandscape;
    }

    public String getLandscapeFile() {
        return fLandscapeFile;
    }

    public void setLandscapeFile(String fLandscapeFile) {
        this.fLandscapeFile = fLandscapeFile;
    }

    public int getLandscapeOffsetFromTop() {
        return fLandscapeOffsetFromTop;
    }

    public void setLandscapeOffsetFromTop(int fLandscapeOffsetFromTop) {
        this.fLandscapeOffsetFromTop = fLandscapeOffsetFromTop;
    }

    public int getLandscapeStartAngle() {
        return fLandscapeStartAngle;
    }

    public void setLandscapeStartAngle(int fLandscapeStartAngle) {
        this.fLandscapeStartAngle = fLandscapeStartAngle;
    }

}

