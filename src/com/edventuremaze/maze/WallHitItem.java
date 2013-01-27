package com.edventuremaze.maze;

/**
 * Data for a position on a wall which is hit by a ray. The WallItem serves as
 * a means of tracking and comparing all wall hits for the Maze.
 * 
 * @author brianpratt
 */
public abstract class WallHitItem {

    public enum HitSide {
        TOP_SIDE_HIT, RIGHT_SIDE_HIT, BOTTOM_SIDE_HIT, LEFT_SIDE_HIT
    }

    public enum HitType {
        HORIZ_HIT, VERT_HIT
    }

	// gridline and intersection are the x, y positions of the hit
	protected int fGridLine = 0;          // the position of the grid line which is hit by the ray
	                                      // ... this is a horiz grid line described in terms of y for horiz hits
	                                      // ... this is a vert grid line described in terms of x vert hits
	protected float fIntersection = 0.0f; // intersection of ray on grid line
	                                      // ...this is an x value for horiz grid and y for vert
	
	private HitType fHitType = HitType.HORIZ_HIT;     // horiz or vertical grid line hit
    private HitSide fHitSide = HitSide.TOP_SIDE_HIT;  // hit top, bottom, right or left side of cube (from arial perspective)

	protected boolean fOffTheMap = false; // true - this ray went off the map
	protected float fDistToItem = 0.0f;   // distance from player to wall being hit
	
	protected int fXGridIndex = 0;        // x position of hit on the smaller (aerial) map
	protected int fYGridIndex = 0;        // y position of hit on the smaller (aerial) map
	protected int fMapPos = 0;            // x and y converted to one dimensional (y * mapWidth) + x
	
	int fCastArc = 0;                      // for debugging purposes only

	/**
	 * Constructor - defines a wall hit item.
	 * @param hitType This should be HORIZ_HIT or VERT_HIT
     * @param hitSide Side hit: left, right top, bottom (from arial perspective)
	 * @param gridLine Int is okay here because it is always even multiple of 64
	 * @param intersection Float here because it is usually not an a evenly rounded number
	 * @param castArc Just useful for debugging
	 */
	public WallHitItem(HitType hitType, HitSide hitSide, int gridLine, float intersection, int castArc) {
		fHitType = hitType;
        fHitSide = hitSide;
		fGridLine = gridLine; 
		fIntersection = intersection;  
		fCastArc = castArc; 
	}

	// -------- setters and getters ------------

	public boolean isOffTheMap() {
		return fOffTheMap;
	}

	public boolean setOffTheMap(boolean offTheMap) {
		return fOffTheMap = offTheMap;
	}

	public boolean isHorizHit() {
		return (fHitType == HitType.HORIZ_HIT);
	}

	public boolean isVertHit() {
		return (fHitType == HitType.VERT_HIT);
	}

    public HitSide getHitSide() {
        return fHitSide;
    }

	public void setMapPos(int mapPos) {
		fMapPos = mapPos;
	}

	public int getMapPos() {
		return fMapPos;
	}

	public float getDistToItem() {
		return fDistToItem;
	}

	public void setDistToItem(float distToItem) {
		fDistToItem = distToItem;
	}

	public float getIntersection() {
		return fIntersection;
	}

	public void setIntersection(float intersection) {
		fIntersection = intersection;
	}

	public int getGridLine() {
		return fGridLine;
	}

	public void setGridLine(int gridLine) {
		fGridLine = gridLine;
	}

	/**
	 * Static method that returns which of the two WallItems is the closest.
	 */
	public static WallHitItem determineClosestHit(WallHitItem horizItemHit, WallHitItem vertItemHit) {
		if (vertItemHit.isOffTheMap() && horizItemHit.isOffTheMap())
			return horizItemHit;
		if (vertItemHit.isOffTheMap())
			return horizItemHit;
		if (horizItemHit.isOffTheMap())
			return vertItemHit;
		if (horizItemHit.getDistToItem() < vertItemHit.getDistToItem())
			return horizItemHit;
		else
			return vertItemHit;
	}

	/**
	 * Determines if the member variables xGridIndex and yGridIndex are off the
	 * map and if so, it sets data members including distance accordingly.
	 * Assumes calcAndSetMapPos was called just prior.
	 */
	public boolean calcAndSetOffTheMap(MapData mapData) {
		// fXGridIndex and fYGridIndex index is hopefully set prior
		if ((fXGridIndex >= mapData.getMapWidth())
				|| (fYGridIndex >= mapData.getMapHeight())
				|| fXGridIndex < 0 || fYGridIndex < 0) {
			fDistToItem = Float.MAX_VALUE;
			fOffTheMap = true;
		}
		return fOffTheMap;
	}

	/**
	 * Defined in derived classes, converts the maze coordinates to a position
	 * in the small aerial map.
	 */
	abstract public int calcAndSetMapPos(MapData mapData);
}


