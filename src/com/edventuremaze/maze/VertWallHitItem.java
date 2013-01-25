package com.edventuremaze.maze;

/**
 * A class representing a wall hit along a vertical wall.
 * 
 * @author brianpratt
 */
public class VertWallHitItem extends WallHitItem {

	/**
	 * Constructor - calls base class. 
	 */
	public VertWallHitItem(WallHitItem.HitSide hitSide, int gridLine, float intersection, int castArc) {
		super(WallHitItem.HitType.VERT_HIT, hitSide, gridLine, intersection, castArc);
	}

	/**
	 * Converts the maze coordinates to a position in the small aerial map and
	 * sets data members accordingly.  Generally you should call calcAndSetOffTheMap
	 * after calling this.
	 */
	public int calcAndSetMapPos(MapData mapData) {
		fXGridIndex = (fGridLine >> MazeGlobals.TILE_SIZE_SHIFT);
		fYGridIndex = (int) (fIntersection / MazeGlobals.TILE_SIZE);
		fMapPos = mapData.convertPointToMapPos(fXGridIndex, fYGridIndex);
		return fMapPos;
	}
}
































