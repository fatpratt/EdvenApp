package com.edventuremaze.maze;

/**
 * Trap POJO which represents a rectangular portion of the map from which the player, upon entering, will
 * be "hyperspaced" to another location (destination), or some other action will be applied.
 *
 * @author brianpratt
 */
public class Trap {
    private int fLeftSide;
    private int fRightSide;
    private int fTopSide;
    private int fBottomSide;

    private boolean fUsingDest;
    private int fGotoDest;

    private boolean fUsingOverlay;        // true - overlay exists for this trap
    private String fOverlayFile;          // file name of overlay image file
    private ImagePixels fOverlayPixels;   // pixels from overlay image file

    private boolean fUsingSound;
    private String fSoundFile;

    public int getLeftSide() {
        return fLeftSide;
    }

    public void setLeftSide(int fLeftSide) {
        this.fLeftSide = fLeftSide;
    }

    public int getRightSide() {
        return fRightSide;
    }

    public void setRightSide(int fRightSide) {
        this.fRightSide = fRightSide;
    }

    public int getTopSide() {
        return fTopSide;
    }

    public void setTopSide(int fTopSide) {
        this.fTopSide = fTopSide;
    }

    public int getBottomSide() {
        return fBottomSide;
    }

    public void setBottomSide(int fBottomSide) {
        this.fBottomSide = fBottomSide;
    }

    public boolean isUsingDest() {
        return fUsingDest;
    }

    public void setUsingDest(boolean fUsingDest) {
        this.fUsingDest = fUsingDest;
    }

    public int getGotoDest() {
        return fGotoDest;
    }

    public void setGotoDest(int fGotoDest) {
        this.fGotoDest = fGotoDest;
    }

    public boolean isUsingOverlay() {
        return fUsingOverlay;
    }

    public void setUsingOverlay(boolean fUsingOverlay) {
        this.fUsingOverlay = fUsingOverlay;
    }

    public String getOverlayFile() {
        return fOverlayFile;
    }

    public void setOverlayFile(String fOverlayFile) {
        this.fOverlayFile = fOverlayFile;
    }

    public ImagePixels getOverlayPixels() {
        return fOverlayPixels;
    }

    public void setOverlayPixels(ImagePixels fOverlayPixels) {
        this.fOverlayPixels = fOverlayPixels;
    }

    public boolean isUsingSound() {
        return fUsingSound;
    }

    public void setUsingSound(boolean fUsingSound) {
        this.fUsingSound = fUsingSound;
    }

    public String getSoundFile() {
        return fSoundFile;
    }

    public void setSoundFile(String fSoundFile) {
        this.fSoundFile = fSoundFile;
    }

    /**
     * Returns true if the x and y coordinates are inside the trap.
     */
    public boolean insideThisTrap(int x, int y) {
        return ((x >= fLeftSide) && (x <= fRightSide) && (y >= fTopSide) && (y <= fBottomSide));
    }

}

