package com.edventuremaze.maze;

/**
 * Represents map data which contains the arial view of the maze wall map and all wall image pixels.
 *
 * @author brianpratt
 */
public interface MapData {

    static public final String MAP_DATA_FILE= "WallData.txt";

    // Returns the maze map height.
    public int getMapHeight();

    // Returns the maze width.
    public int getMapWidth();

    // Returns the maze shift width used for fast mult/div in power of two scenarios.
    public int getMapShiftWidth();

    // Returns the total number of different wall images.
    public int getNumWallImgs();

    // Returns true if the specified map item is a wall.
    public boolean isWall(int mapPos);

    // Returns the value at the specified position.
    public char getValue(int mapPos);

    // Returns the specified wall image pixels base upon the base 36 image number.
    public ImagePixels getImagePixelsForWall(String wallId);

    // Converts x,y point to a position in the one dimensional aerial view array.
    public int convertPointToMapPos(int x, int y);
}
