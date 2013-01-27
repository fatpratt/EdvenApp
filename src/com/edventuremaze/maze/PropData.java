package com.edventuremaze.maze;

/**
 * Represents prop data which contains the arial view of the prop data map and all prop image pixels.
 *
 * @author brianpratt
 */
public interface PropData {

    static public final String PROP_DATA_FILE = "PropData.txt";

    // Returns the total number of different prop images.
    public int getNumPropImgs();

    // Returns true if the specified map item is a prop.
    public boolean isProp(int propPos);

    // Returns the value at the specified position.
    public char getValue(int propPos);

    // Returns the specified prop image pixels base upon the base 36 image number.
    public ImagePixels getImagePixelsForProp(String propId);
}
