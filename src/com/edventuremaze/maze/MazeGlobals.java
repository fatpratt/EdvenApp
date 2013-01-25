package com.edventuremaze.maze;

import java.util.IllegalFormatCodePointException;

/**
 *
 * @author brianpratt 
 */
public class MazeGlobals {
    static public boolean X2 = false;
	static public int WALL_HEIGHT = 64;
    static public int PROP_HEIGHT = 64;
	static public int TILE_SIZE = 64;
	static public int TILE_SIZE_SHIFT = 6;   // used for bitwise shifting to simulate div and mult by TILE_SIZE
    static public int TILE_SIZE_HALF = 32;

	static public int PROJECTIONPLANEWIDTH = 320;
	static public int PROJECTIONPLANEHEIGHT = 200;

    static public int PLAYER_SPEED = 16;    // how far each step takes player
    static public int HALF_PLAYER_SPEED = 8;
    static public int QTR_PLAYER_SPEED = 4;

    static public int PLAYER_DIST_TO_PROJ_PLANE = 277;  // distance from play to the projection plane

    public static void doubleEverything() {
        X2 = true;
        WALL_HEIGHT = 128;
        PROP_HEIGHT = 128;
        TILE_SIZE = 128;
        TILE_SIZE_SHIFT = 7;   // used for bitwise shifting to simulate div and mult by TILE_SIZE
        TILE_SIZE_HALF = 64;

        PROJECTIONPLANEWIDTH = 640;
        PROJECTIONPLANEHEIGHT = 400;

        PLAYER_SPEED = 32;    // how far each step takes player
        HALF_PLAYER_SPEED = 16;
        QTR_PLAYER_SPEED = 8;

        PLAYER_DIST_TO_PROJ_PLANE = 554;
    }
}
