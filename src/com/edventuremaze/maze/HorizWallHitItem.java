package com.edventuremaze.maze;

/**
 * A class representing a wall hit along a horizontal wall.
 * 
 * @author brianpratt
 */
public class HorizWallHitItem extends WallHitItem {

	/**
	 * Constructor - calls base class.
	 */
	public HorizWallHitItem(HitSide hitSide, int gridLine, float intersection, int castArc) {
		super(WallHitItem.HitType.HORIZ_HIT, hitSide, gridLine, intersection, castArc);
	}

	/**
	 * Converts the maze coordinates to a position in the small aerial map and
	 * sets data members accordingly. Generally you should call calcAndSetOffTheMap
	 * after calling this.
	 */
	public int calcAndSetMapPos(MapData mapData) {
		// round down to x position of intersection on small grid
		fXGridIndex = (int) (fIntersection / MazeGlobals.TILE_SIZE);
		fYGridIndex = (fGridLine >> MazeGlobals.TILE_SIZE_SHIFT);
		fMapPos = mapData.convertPointToMapPos(fXGridIndex, fYGridIndex);
		return fMapPos;
	}
}
