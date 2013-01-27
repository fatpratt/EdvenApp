package com.edventuremaze.maze;

import java.util.HashMap;

/**
 * This class is a container of image pixels from overlay, background or landscape files.
 * Each set of image pixels is indexed by a filename.   This class has a close relationship with classes: Maze,
 * MazeConfig, Trap, and Dest.
 *
 * @author brianpratt
 */
public class ImageFileCache {

    HashMap<String, ImagePixels> fImagePixelMap;   // this class wraps around this hashmap

    /**
     * Constructor
     */
    public ImageFileCache() {
        fImagePixelMap = new HashMap<String, ImagePixels>(); // pixels for each file
    }

    /**
     * Returns true if the specified fileName already is found in the collection.
     */
    public boolean contains(String fileName) {
        return fImagePixelMap.containsKey(fileName);
    }

    /**
     * Adds the specified image pixels to the collection, indexed by the specified fileName.
     */
    public void add(String fileName, ImagePixels imagePixels) {
        fImagePixelMap.put(fileName, imagePixels);
    }

    /**
     * Index look up using fileName to get the associated image pixels.
     */
    private ImagePixels getPixelsFromFileName(String fileName) {
        return fImagePixelMap.get(fileName);
    }

    /**
     * Returns the image pixels based upon a given destination.
     */
    public ImagePixels getBackgroundPixelsFromDest(Dest dest) {
        if (dest.isBackgroundFromFile())
            return getPixelsFromFileName(dest.getBackgroundFile());
        else return null;
    }

    /**
     * Returns the image pixels based upon a given trap.
     */
    public ImagePixels getOverlayPixelsFromTrap(Trap trap) {
        if (trap.isUsingOverlay())
            return getPixelsFromFileName(trap.getOverlayFile());
        else return null;
    }

    /**
     * Returns landscape image pixels based upon a given destination.
     */
    public ImagePixels getLandscapePixelsFromDest(Dest dest) {
        if (dest.isUsingALandscape())
            return getPixelsFromFileName(dest.getLandscapeFile());
        else return null;
    }
}
