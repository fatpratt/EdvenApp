package com.edventuremaze.maze;

import com.edventuremaze.factories.ImagePixelsFactory;
import com.edventuremaze.factories.VibrateEffectsFactory;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * Maze - This is the main graphics component for the program.  To alleviate the complexity of this module, other
 * objects/classes provide minimal functionality to service this class and are passed in through MazeParam object.
 *
 * @author brianpratt 
 */
public class Maze {

	// player's coordinates in the maze
	int fPlayerX;
	int fPlayerY;
	int fPlayerArc;   // player's current angle he/she is facing

	// these are helpful vectors describing the current x and y direction of the player and must correspond with fPlayerArc
	float fPlayerXDir;      // should always be equal to fCosTable[fPlayerArc];
	float fPlayerYDir;      // should always be equal to fSinTable[fPlayerArc];

	int fProjectionPlaneYCenter = MazeGlobals.PROJECTIONPLANEHEIGHT / 2;
	static final int SLICE_WIDTH = 1;  // width of vertical slice drawn

    Platform fPlatform;
	MapData fMapData;                   // map view of walls and all associate image pixels
    PropData fPropData;                 // map view of props and all associated image pixels
    QuestionPosData fQuestionPosData;   // map view of question positions and all associated image pixels
    Questions fQuestions;               // collection of all questions with answers and relative answer positions
    MazeConfig fConfig;                 // maze configuration
    MazeListener fMazeListener;         // external class registering with this class as a listener to become aware of changes
    VibrateEffects fVibrateEffects;     // holds device vibrate services

    Dest fCurDest;        // current Destination object
    Trap fCurTrap;        // current Trap object - we hold on to this just for the message info

    float fClipper[] = new float[MazeGlobals.PROJECTIONPLANEWIDTH];   // clipping array tracking wall distances for clipping props around corners
    HashMap<Integer, PropHitItem> fPropsHitMap = new HashMap<Integer, PropHitItem>();    // track prop items hit and distance in a given refresh cycle

	// pixel objects
	int[] fMemPixels = new int[MazeGlobals.PROJECTIONPLANEWIDTH * MazeGlobals.PROJECTIONPLANEHEIGHT];   // the drawing surface
	Background fBackground = null;                                              // the one and only current background
    ImagePixels fLandscapePixels = null;

    // questions
    char fCurQuestion = '0';            // current question we are on... '0' means no question at all

    int fTooCloseProximity = 7;            // too close to the wall proximity
    boolean fInAutoMoveMode =  false;      // true means user has requested a "run forward" move to bank forward movements
    boolean fInQuestionPopupMode = false;  // true means the question is currently popped up over the display

	/**
	 * Constructor -- Gets the maze up and running by establishing state from mazeParams.
	 */
    public Maze(MazeParams mazeParams) {
		Trig.createTables();
		fMapData = mazeParams.getMapData();
        fPlatform = mazeParams.getPlatform();

        fPropData = mazeParams.getPropData();
        fQuestions = mazeParams.getQuestions();
        fQuestionPosData = mazeParams.getQuestionPosData();
        fConfig = mazeParams.getMazeConfig();
        fMazeListener = mazeParams.getMazeListener();

        fCurDest = fConfig.advanceToDest(0);   // zero is the initial starting destination
		fPlayerX = fCurDest.getXPos();             // initial player position comes from first destination
		fPlayerY = fCurDest.getYPos();
		fPlayerArc = fCurDest.getAngle();
        fBackground = Background.createBackgroundFromDest(fPlatform, fConfig.getBackgroundCache(), fCurDest);
        if (fCurDest.isUsingALandscape())
            fLandscapePixels = fConfig.getLandscapeCache().getLandscapePixelsFromDest(fCurDest);
        else
            fLandscapePixels = ImagePixelsFactory.createBlankImagePixels(fPlatform);

        fVibrateEffects = VibrateEffectsFactory.createVibrateEffects(fPlatform);

		setPlayerPos();
	}

    /**
     * Sets internal flag to denote we are in auto move mode where we have banked forward movements and informs
     * maze listener of change of mode.
     */
    public void setInAutoMoveMode(boolean inAutoMoveMode) {
       fInAutoMoveMode = inAutoMoveMode;
        if (fMazeListener != null) fMazeListener.autoMoveModeChanged(inAutoMoveMode);
    }

    /**
     * This method informs maze listeners of change of mode.
     * Note: question mode and question popup mode differ slightly.  Question popup mode is specifically when the
     * popup appears and question mode is more general meaning a question has been activated, but no answer has
     * yet deactived the question.
     */
    public void setInQuestionMode(boolean inQuestionMode) {
        if (fMazeListener != null) fMazeListener.questionModeChanged(inQuestionMode);
    }

    /**
     * Returns true if in question mode.
     */
    public boolean isInQuestionMode() {
        return (fCurQuestion != '0');
    }

    /**
     * Sets internal flag to denote the question should popup over the display area and informs maze listener of change of
     * mode.
     */
    public void setInQuestionPopupMode(boolean inQuestionPopupMode) {
        fInQuestionPopupMode = inQuestionPopupMode;
        if (fMazeListener != null) fMazeListener.questionPopupModeChanged(inQuestionPopupMode);
    }

    /**
     * Returns true if in question popup mode.
     */
    public boolean isInQuestionPopupMode() {
        return fInQuestionPopupMode;
    }

    /**
     * Returns the question text for the current question.
     */
    public String getCurQuestionText() {
        if (fCurQuestion == 0) return "";
        Question curQuest = fQuestions.returnQuestion(fCurQuestion);
        if (curQuest == null) return "";
        else return curQuest.getQuestionText();
    }

	/**
	 * Casts one ray of specified angle looking at all possible intersections with grid lines (in the
	 * aerial view sense) to find and return the closest horizontal wall hit.
<<<<<<< HEAD
	 * See associated document (JavaRayCasting.doc) which describes steps one through four in great detail.
=======
	 * (See associated document which describes steps one through four in great detail.)
>>>>>>> 213c4e638effcd620abf520ba3eeb4bc613d04aa
	 */
	private HorizWallHitItem castRayForHorizHit(int castArc, int playerX, int playerY, int playerArc) {
		float distToNextXIntersection;
		int distToNextHorizontalGrid;

		int ay = 0;
		float ax = 0.0f;
        WallHitItem.HitSide wallSideHit = WallHitItem.HitSide.BOTTOM_SIDE_HIT;

		// STEP ONE -- (the hardest one) find the coord of where the first horiz wall is hit

		// ray is facing down
		if (castArc > Trig.ANGLE0 && castArc < Trig.ANGLE180) {
            // if we do hit, it will be the bottom of cube
            wallSideHit = WallHitItem.HitSide.BOTTOM_SIDE_HIT;

			// the following line is simply ay = (py/64) * (64) + 64 where "p" is the
			// players position and "a" is the position of the first horiz line hit
			// this is simply looking at the next horizontal line past the player
			ay = ((playerY >> MazeGlobals.TILE_SIZE_SHIFT) << MazeGlobals.TILE_SIZE_SHIFT) + MazeGlobals.TILE_SIZE;

			// if we know one side and one angle, we can get the other side
			// ax = px + (ay - py) / tan(alpha)
			ax = playerX + ((ay - playerY) * Trig.fITanTable[castArc]);

			// ray is going down so increment by positive 64 going
			// from one line to the next in step 3
			distToNextHorizontalGrid = MazeGlobals.TILE_SIZE;
		}

		// else, the ray is facing up
		else {
            // if we do hit, it will be the top of the cube
            wallSideHit = WallHitItem.HitSide.TOP_SIDE_HIT;

			// ay = (py/64) * (64)
			// this is simply looking at the previous horizontal line just prior to the player
			ay = (playerY >> MazeGlobals.TILE_SIZE_SHIFT) << MazeGlobals.TILE_SIZE_SHIFT;

			// if we know one side and one angle, we can get the other side
			// ax = px + (ay - py) / tan(alpha)
			ax = playerX + ((ay - playerY) * Trig.fITanTable[castArc]);

			// ray is going up so decrement by 64 (going up means less y)
			// as we move from one horiz line to the next in step 3
			distToNextHorizontalGrid = -MazeGlobals.TILE_SIZE;

			// convention used to determine if the line is part of the block
			// above or below the line
			ay--;
		}
		HorizWallHitItem horizItemHit = new HorizWallHitItem(wallSideHit, ay, ax, castArc);
        //horizItemHit.setHitSide(wallSideHit);

		// if horizontal ray
		if (castArc == Trig.ANGLE0 || castArc == Trig.ANGLE180) {
			// if casting parallel to horiz wall ignore all horiz hits
			horizItemHit.setDistToItem(Float.MAX_VALUE);
		}

		// else, move the ray until it hits a horizontal wall
		else {
			// STEP TWO -- set distToNextXIntersection and distToNextHorizontalGrid (already done)

			// set precalculated distance between x lines for this angle
			distToNextXIntersection = Trig.fXStepTable[castArc];
			while (true) {

				// STEP THREE -- convert to the small grid coordinates and see if we are on a wall

                char questItemTypeHit = '0';    // type of question item hit, '0' denotes no hit
				int mapPos = horizItemHit.calcAndSetMapPos(fMapData);
				horizItemHit.calcAndSetOffTheMap(fMapData);
				if (horizItemHit.isOffTheMap()) {
					break;
				}

                // if prop item was hit, add to prop list and keep going
                else if (fPropData.isProp(mapPos) && !fPropsHitMap.containsKey(new Integer(mapPos))) {
                    PropHitItem propHit = new PropHitItem(mapPos);
                    propHit.setPropHitItemData(fMapData, playerX, playerY, playerArc);
                    fPropsHitMap.put(new Integer(mapPos), propHit);
                }

                // if question item was hit, determine which item was hit, and treat it similarly
                // to prop item hit by adding it to the prop list and keep going
                else if (((questItemTypeHit = fQuestionPosData.getQuestionItemTypeAtSpecial(mapPos, fQuestions, fCurQuestion)) != '0')
                        && !fPropsHitMap.containsKey(new Integer(mapPos))) {
                    PropHitItem questionPropHit = new QuestionHitItem(mapPos, questItemTypeHit);
                    questionPropHit.setPropHitItemData(fMapData, playerX, playerY, playerArc);
                    fPropsHitMap.put(new Integer(mapPos), questionPropHit);
                }

				// if wall was hit, stop here
				// else if (fMapData.isWall(horizItemHit.getMapPos())) {
                else if (!fPropData.isProp(mapPos) && fMapData.isWall(horizItemHit.getMapPos())) {
					// if we know one side and one angle, we can get the hypotenuse
					horizItemHit.setDistToItem((horizItemHit.getIntersection() - playerX) * Trig.fICosTable[castArc]);
					break;
				}

				// STEP FOUR -- continue to next horiz line intersection by incrementing
				// by the exact same offsets each time

				else { // else, the ray is not blocked, extend to the next block
					horizItemHit.setIntersection(horizItemHit.getIntersection() + distToNextXIntersection);
					horizItemHit.setGridLine(horizItemHit.getGridLine() + distToNextHorizontalGrid);
				}
			}
		}
		return horizItemHit;
	}

	/**
	 * Casts one ray of specified angle looking at all possible intersections with
	 * grid lines (in the aerial view sense) to find and return the closest vertical
	 * wall hit. Comments are omitted in this method for the sake of brevity, but would
	 * closely parallel comments in the castRayForHorzHit method above.
	 */
	private VertWallHitItem castRayForVertHit(int castArc, int playerX, int playerY, int playerArc) {
		float distToNextYIntersection;
		int distToNextVerticalGrid;

		int ax = 0;
		float ay = 0.0f;
        WallHitItem.HitSide wallSideHit = WallHitItem.HitSide.RIGHT_SIDE_HIT;

		if (castArc < Trig.ANGLE90 || castArc > Trig.ANGLE270) {
            // if we do hit, it will be the bottom of cube
            wallSideHit = WallHitItem.HitSide.RIGHT_SIDE_HIT;
			ax = ((playerX >> MazeGlobals.TILE_SIZE_SHIFT) << MazeGlobals.TILE_SIZE_SHIFT) + MazeGlobals.TILE_SIZE;
			ay = playerY + ((ax - playerX) * Trig.fTanTable[castArc]);
			distToNextVerticalGrid = MazeGlobals.TILE_SIZE;
		} else {
            // if we do hit, it will be the bottom of cube
            wallSideHit = WallHitItem.HitSide.LEFT_SIDE_HIT;
			ax = (playerX >> MazeGlobals.TILE_SIZE_SHIFT) << MazeGlobals.TILE_SIZE_SHIFT;
			ay = playerY + ((ax - playerX) * Trig.fTanTable[castArc]);
			distToNextVerticalGrid = -MazeGlobals.TILE_SIZE;
			ax--;
		}
		VertWallHitItem vertItemHit = new VertWallHitItem(wallSideHit, ax, ay, castArc);
        //vertItemHit.setHitSide(wallSideHit);

		if (castArc == Trig.ANGLE90 || castArc == Trig.ANGLE270) {
			vertItemHit.setDistToItem(Float.MAX_VALUE);
		} else {
			distToNextYIntersection = Trig.fYStepTable[castArc];
			while (true) {
                char questItemTypeHit = '0';    // type of question item hit, '0' denotes no hit
				int mapPos = vertItemHit.calcAndSetMapPos(fMapData);
				vertItemHit.calcAndSetOffTheMap(fMapData);

				if (vertItemHit.isOffTheMap()) {
					break;
				}

                else if (fPropData.isProp(mapPos) && !fPropsHitMap.containsKey(new Integer(mapPos))) {
                    PropHitItem propHit = new PropHitItem(mapPos);
                    propHit.setPropHitItemData(fMapData, playerX, playerY, playerArc);
                    fPropsHitMap.put(new Integer(mapPos), propHit);
                }

                else if (((questItemTypeHit = fQuestionPosData.getQuestionItemTypeAtSpecial(mapPos, fQuestions, fCurQuestion)) != '0')
                        && !fPropsHitMap.containsKey(new Integer(mapPos))) {
                    PropHitItem questionPropHit = new QuestionHitItem(mapPos, questItemTypeHit);
                    questionPropHit.setPropHitItemData(fMapData, playerX, playerY, playerArc);
                    fPropsHitMap.put(new Integer(mapPos), questionPropHit);
                }

                else if (!fPropData.isProp(mapPos) && fMapData.isWall(vertItemHit.getMapPos())) {
					vertItemHit.setDistToItem((vertItemHit.getIntersection() - playerY) * Trig.fISinTable[castArc]);
					break;
				}

				else {
					vertItemHit.setIntersection(vertItemHit.getIntersection() + distToNextYIntersection);
					vertItemHit.setGridLine(vertItemHit.getGridLine() + distToNextVerticalGrid);
				}
			}
		}

		return vertItemHit;
	}

    /**
     * Returns true if the given pixel is transparent.
     */
    private boolean isPixelTransparent(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        return alpha == 0;
    }

    /**
     * Draws a vertical slice of image on the specfied column scaling appropriately (based on lineHeight).
     * @param col Column of projection plane we are drawing.
     * @param topOfLine Top pixel location.
     * @param lineHeight Line length to be drawn, which should be scaled based upon distance.
     * @param srcImagePixels The source image pixels.
     * @param srcImageHeight The height of the source image.
     * @param srcColImage Column from the source image to be drawn.
     */
    private void drawVertSliceOfImage(int col, int topOfLine, int lineHeight, int[] srcImagePixels, int srcImageHeight, int srcColImage) {

        float ratio = (float) srcImageHeight / (float) lineHeight;  // ratio between source and dest

        int yImage = 0;
        for (int y = topOfLine; y < (topOfLine + lineHeight); y++) {
            yImage++;
            int pixelPos = (y * MazeGlobals.PROJECTIONPLANEWIDTH) + col;
            if (pixelPos >= 0 && pixelPos < MazeGlobals.PROJECTIONPLANEWIDTH * MazeGlobals.PROJECTIONPLANEHEIGHT) {
                int srcPixelPos = ((int) ((ratio) * yImage) * srcImageHeight) + srcColImage;
                if (srcPixelPos >= 0 && srcPixelPos < srcImageHeight * srcImageHeight) {   // assumes height and width are equal
                    if (!isPixelTransparent(srcImagePixels[srcPixelPos]))
                        fMemPixels[pixelPos] = srcImagePixels[srcPixelPos];
                }
            }
        }
    }

    /**
     * Draws the specified prop based upon hit item details.
     * @param prop
     */
    private void castProp(PropHitItem prop) {
        int dist = prop.getDistance();
        int colMidProp = prop.getColumnMidProp();
        if (dist == -1 || colMidProp == -1) return;

        int projectedPropHeight = (int) (MazeGlobals.PROP_HEIGHT * (float) MazeGlobals.PLAYER_DIST_TO_PROJ_PLANE / dist);
        int bottomOfProp = fProjectionPlaneYCenter + (int) (projectedPropHeight * 0.5F);
        int topOfProp = MazeGlobals.PROJECTIONPLANEHEIGHT - bottomOfProp;
        int propWidth = projectedPropHeight; // assumes width and height of tile are the same

        // grab the appropriate image from hashtable(s)
        ImagePixels imagePixelsProp = fQuestionPosData.getImagePixelsForQuestionType("?"); // initialize this to something
        if (prop instanceof QuestionHitItem) { // question items hit are treated like props
            // get '?', 'A', 'B', 'C', or 'D' and use it as an index to get pixels
            char itemType = ((QuestionHitItem)prop).getQuestionItemType();
            if (itemType != '0') imagePixelsProp = fQuestionPosData.getImagePixelsForQuestionType("" + itemType);
        } else {
            char ch = fPropData.getValue(prop.getMapPos());
            if (ch == '0') return;
            String strIndex = "" + ch;
            imagePixelsProp = (ImagePixels)fPropData.getImagePixelsForProp(strIndex);
        }

        // draw left side of prop
        int leftBound = (colMidProp - (propWidth >> 1));  // column number of left end of prop
        int rightBound = (colMidProp + (propWidth >> 1)); // column number of right end of prop
        for (int i = colMidProp; i >= 0 && i < MazeGlobals.PROJECTIONPLANEWIDTH && i >= leftBound; i--) {
            if (dist < fClipper[i]) {   // make sure this slice isn't behind a wall
                int sliceOnImage = (int) (((i - leftBound) << MazeGlobals.TILE_SIZE_SHIFT) / propWidth);
                drawVertSliceOfImage(i, topOfProp, projectedPropHeight, imagePixelsProp.getPixels(), MazeGlobals.TILE_SIZE, sliceOnImage);
            }
        }

        // draw right side of prop
        for (int i = (colMidProp + 1); i >= 0 && i < MazeGlobals.PROJECTIONPLANEWIDTH && i < rightBound; i++) {
            if (dist < fClipper[i]) {  // make sure this slice isn't behind a wall
                int sliceOnImage = (int) (((i - leftBound) << MazeGlobals.TILE_SIZE_SHIFT) / propWidth);
                drawVertSliceOfImage(i, topOfProp, projectedPropHeight, imagePixelsProp.getPixels(), MazeGlobals.TILE_SIZE, sliceOnImage);
            }
        }
    }

    /**
     * Draws the landscape if we are casting within the right range.
     */
    void drawLandscape(int castArc, int castColumn) {
        if (null == fCurDest) return;

        if (!fCurDest.isUsingALandscape()) return;     // landscape image is optional

        int offSetFromTop = fCurDest.getLandscapeOffsetFromTop();
        int offSetArc = fCurDest.getLandscapeStartAngle();
        int width = fLandscapePixels.getWidth();
        int height = fLandscapePixels.getHeight();
        int[] pixels = fLandscapePixels.getPixels();

        if (castArc >= offSetArc && castArc < offSetArc + width) {
            for (int y = 0; y < height; y++) {
                if (!isPixelTransparent(pixels[width * y + (castArc - offSetArc)]))
                    fMemPixels[(MazeGlobals.PROJECTIONPLANEWIDTH * (offSetFromTop + y)) + castColumn]
                            = pixels[width * y + (castArc - offSetArc)];
            }
        }
    }

    /**
     * Draw overlay image (usually a message) on top of everything else.
     */
    private void drawOverlay() {
        if (null == fCurTrap) return;

        if (fCurTrap.isUsingOverlay()) {
            ImagePixels imagePixels = fConfig.getOverlayCache().getOverlayPixelsFromTrap(fCurTrap);
            if (imagePixels == null) return;
            int[] pixels = imagePixels.getPixels();
            for (int row = 0; row < MazeGlobals.PROJECTIONPLANEHEIGHT; row++) {
                for (int col = 0; col < MazeGlobals.PROJECTIONPLANEWIDTH; col++) {
                    if (!isPixelTransparent(pixels[(row * MazeGlobals.PROJECTIONPLANEWIDTH) + col])) {
                        fMemPixels[(row * MazeGlobals.PROJECTIONPLANEWIDTH) + col] =
                                pixels[(row * MazeGlobals.PROJECTIONPLANEWIDTH) + col];
                    }
                }
            }
        }
    }

    /**
	 * Draws one complete frame starting with the background then each vertical line on the projection plane is
	 * casted and drawn from left to right covering 60 degrees of the players field of vision.
	 */
	public int[] renderOneFrame() {
		fBackground.copyBackgroundTo(fMemPixels);

        // player position variables (fPlayerArc, fPlayerX, etc)are subject to change while rendering, but we want to work with consistent settings
        int playerArc =  fPlayerArc;
        int playerX = fPlayerX;
        int playerY = fPlayerY;

		// field of view is 60 degree with player's direction (angle) in the middle
		// we will trace the rays starting from the leftmost ray
		int castArc = playerArc - Trig.ANGLE30;
		if (castArc < 0) // wrap around if necessary
			castArc = Trig.ANGLE360 + castArc;

        // initialize prop and clipper
        for (int i = 0; i < MazeGlobals.PROJECTIONPLANEWIDTH; i++)
            fClipper[i] = Float.MAX_VALUE;
        fPropsHitMap.clear();

		// go from left most column to right most column
		for (int castColumn = 0; castColumn < MazeGlobals.PROJECTIONPLANEWIDTH; castColumn += SLICE_WIDTH) {
			// try out same angle with both vert and horiz walls
			HorizWallHitItem horizWallHitItem = castRayForHorizHit(castArc, playerX, playerY, playerArc);
			VertWallHitItem vertWallHitItem = castRayForVertHit(castArc, playerX, playerY, playerArc);

			// draw the closeset of the two wall hits either vert or horiz
			if (!(vertWallHitItem.isOffTheMap() && horizWallHitItem.isOffTheMap())) {
				WallHitItem closestHit = WallHitItem.determineClosestHit(horizWallHitItem, vertWallHitItem);
				if (closestHit.getDistToItem() <= -0.0F) // -0.0 happens sometimes and must be changed
					closestHit.setDistToItem(1.0f);

                drawLandscape(castArc, castColumn);
				drawWallSlice(castColumn, closestHit);
			}

			// increment angle moving on to the next slice
			// (remember ANGLE60 == PROJECTIONPLANEWIDTH)
			castArc += SLICE_WIDTH;
			if (castArc >= Trig.ANGLE360)
				castArc -= Trig.ANGLE360;

			// we are done with these so enable garbage collection
			horizWallHitItem = null;
			vertWallHitItem = null;
		}

        // bjp:  sept 19... this caused props to move about when player presses right, left, forward, backward keys
        // establish distance for each prop item hit
       // for (PropHitItem propHitItem: fPropsHitMap.values()) {
           // propHitItem.setPropHitItemData(fMapData, fPlayerX, fPlayerY, fPlayerArc);
        //}

        // sort the prop hit items based on distance so that distant objects don't appear on top of close objects
        PropHitItemComparator comp =  new PropHitItemComparator(fPropsHitMap);
        TreeMap<Integer, PropHitItem> sortedPropsHitMap = new TreeMap<Integer, PropHitItem>(comp);
        sortedPropsHitMap.putAll(fPropsHitMap);

        // draw each prop hit
        for (Integer key: sortedPropsHitMap.keySet()) {
            PropHitItem propHitItem = sortedPropsHitMap.get(key);
            castProp(propHitItem);
        }

        // draw overlay image (usually a message) on top of everything else
        drawOverlay();

		return fMemPixels;
	}

	/**
	 * Draws one wall slice scaling it based upon distance and accounts for fish
	 * eye lens correction.
	 */
	private void drawWallSlice(int castColumn, WallHitItem itemHit) {
		int sliceOfWall; // where slice hits wall
		float dist;
		int topOfWall; // used to compute the top and bottom of the sliver that
		int bottomOfWall; // ...will be the starting point of floor and ceiling

		if (!itemHit.isOffTheMap()) {
			sliceOfWall = ((int) itemHit.getIntersection()) % MazeGlobals.TILE_SIZE;
			dist = itemHit.getDistToItem();
			dist /= Trig.fFishTable[castColumn];

			int projectedWallHeight = (int) (MazeGlobals.WALL_HEIGHT * (float) MazeGlobals.PLAYER_DIST_TO_PROJ_PLANE / dist);
			bottomOfWall = fProjectionPlaneYCenter + (int) (projectedWallHeight * 0.5F);
			topOfWall = MazeGlobals.PROJECTIONPLANEHEIGHT - bottomOfWall;
			if (bottomOfWall >= MazeGlobals.PROJECTIONPLANEHEIGHT) bottomOfWall = MazeGlobals.PROJECTIONPLANEHEIGHT - 1;

            int sliceWidth = SLICE_WIDTH;
            int leftMostOfSlice;
            if (itemHit.getHitSide() == WallHitItem.HitSide.TOP_SIDE_HIT || itemHit.getHitSide() == WallHitItem.HitSide.RIGHT_SIDE_HIT) {
                leftMostOfSlice = ((sliceOfWall - sliceWidth) > 0) ? (sliceOfWall - sliceWidth) : 0;
            }
            else {  // with bottom and right wall hits you must invert the image
                leftMostOfSlice = ((sliceOfWall + sliceWidth) <= MazeGlobals.TILE_SIZE) ? (sliceOfWall + sliceWidth) : MazeGlobals.TILE_SIZE;
                leftMostOfSlice = MazeGlobals.TILE_SIZE - leftMostOfSlice;
            }

            // grab the appropriate image from hashtable
            char ch = fMapData.getValue(itemHit.getMapPos());
            if (ch == '0') return;
            String strIndex = "" + ch;
            ImagePixels imagePixelsWall = fMapData.getImagePixelsForWall(strIndex);

            drawVertSliceOfImage(castColumn, topOfWall, projectedWallHeight, imagePixelsWall.getPixels(), MazeGlobals.TILE_SIZE, leftMostOfSlice);

            if (!fPropData.isProp(itemHit.getMapPos()))  // if this is a wall, save distance in clip array
              fClipper[castColumn] = dist;               //  ...for use when drawing prop
		}
	}

	/**
	 * Rotates the player's angle left.
	 */
	public void rotateLeft() {
		if ((fPlayerArc -= Trig.ANGLE10) < Trig.ANGLE0)
			fPlayerArc += Trig.ANGLE360;
		setPlayerPos();
	}

	/**
	 * Rotate's player's angle right.
	 */
	public void rotateRight() {
		if ((fPlayerArc += Trig.ANGLE10) >= Trig.ANGLE360)
			fPlayerArc -= Trig.ANGLE360;
		setPlayerPos();
	}

    /**
     * Rotates the player the specified angle to the left.
     * @param angle Degrees left to rotate the player.
     */
    public void rotateLeft(int angle) {
        if ((fPlayerArc -= angle) < Trig.ANGLE0)
            fPlayerArc += Trig.ANGLE360;
        setPlayerPos();
    }

    /**
     * Rotates the player the specified angle to the right.
     * @param angle Degrees right to rotate the player.
     */
    public void rotateRight(int angle) {
        if ((fPlayerArc += angle) >= Trig.ANGLE360)
            fPlayerArc -= Trig.ANGLE360;
        setPlayerPos();
    }


	/**
	 * Sets the players x and y directions based upon the current angle.
	 */
	private void setPlayerPos() {
		fPlayerXDir = Trig.fCosTable[fPlayerArc];
		fPlayerYDir = Trig.fSinTable[fPlayerArc];
	}

	/**
	 * Moves the player forward. 
	 */
	public void moveForward() {
		int newPlayerX = fPlayerX + (int) (fPlayerXDir * MazeGlobals.PLAYER_SPEED);
		int newPlayerY = fPlayerY + (int) (fPlayerYDir * MazeGlobals.PLAYER_SPEED);
		attemptMove(newPlayerX, newPlayerY, true);
	}

	/**
	 * Moves the player backward. 
	 */
	public void moveBackward() {
		int newPlayerX = fPlayerX - (int) (fPlayerXDir * MazeGlobals.PLAYER_SPEED);
		int newPlayerY = fPlayerY - (int) (fPlayerYDir * MazeGlobals.PLAYER_SPEED);
		attemptMove(newPlayerX, newPlayerY, false);
	}

    /**
     * Moves player forward the specified distance.
     * @param dist Distance to move the player forward.
     */
    public void moveForward(int dist) {
        int newPlayerX = fPlayerX + (int) (fPlayerXDir * dist);
        int newPlayerY = fPlayerY + (int) (fPlayerYDir * dist);
        attemptMove(newPlayerX, newPlayerY, true);
    }

    /**
     * Moves player backward the specified distance.
     * @param dist Distance to move the player backward.
     */
    public void moveBackward(int dist) {
        int newPlayerX = fPlayerX - (int) (fPlayerXDir * dist);
        int newPlayerY = fPlayerY - (int) (fPlayerYDir * dist);
        attemptMove(newPlayerX, newPlayerY, false);
    }

    /**
     * Moves player in a relative position both in an x and y directions, though a change in x is considered a rotation
     * and a change in y is considered a move forward or backward.
     * @param dx Delta x--denotes an angle to rotate either positive (left) or negative (right).
     * @param dy Delta y--denotes distance to move:  positive for forward and negative for backward.
     */
    public void moveRelative(int dx, int dy) {
        if (dy < 0) {
            dy = Math.abs(dy);
            dy = dy > (MazeGlobals.PLAYER_SPEED + MazeGlobals.HALF_PLAYER_SPEED) ? MazeGlobals.PLAYER_SPEED + MazeGlobals.HALF_PLAYER_SPEED : dy;       // don't allow a movement too far in the distance
            moveBackward(dy);
        }
        else {
            dy = dy > (MazeGlobals.PLAYER_SPEED  + MazeGlobals.HALF_PLAYER_SPEED) ? MazeGlobals.PLAYER_SPEED + MazeGlobals.HALF_PLAYER_SPEED : dy;       // don't allow a movement too far in the distance
            moveForward(dy);
        }

        // a change in x is considered a rotational move
        if (dx < 0) {
            dx = Math.abs(dx);
            dx = dx > (Trig.ANGLE10 * 2) ? Trig.ANGLE10 * 2 : dx;       // don't allow the rotation angle to be too big
            rotateRight(dx);
        }
        else {
            dx = dx > (Trig.ANGLE10 * 2) ? Trig.ANGLE10 * 2 : dx;       // don't allow the rotation angle to be too big
            rotateLeft(dx);
        }
    }

    /**
     * Move to center of the current tile and turn directly north, south, east, or west.
     */
    public void straightenUp() {
        // move to center of current tile
        int newX = ((fPlayerX >> MazeGlobals.TILE_SIZE_SHIFT) << MazeGlobals.TILE_SIZE_SHIFT) + MazeGlobals.TILE_SIZE_HALF;
        int newY = ((fPlayerY >> MazeGlobals.TILE_SIZE_SHIFT) << MazeGlobals.TILE_SIZE_SHIFT) + MazeGlobals.TILE_SIZE_HALF;
        attemptMove(newX, newY, false);

        // adjust the angle to straight north, south, east, or west base upon what is closest
        int high = Trig.ANGLE0;
        int low = Trig.ANGLE0;

        if (fPlayerArc >= Trig.ANGLE0 && fPlayerArc < Trig.ANGLE90) {
            low = Trig.ANGLE0;
            high = Trig.ANGLE90;
        }
        if (fPlayerArc >= Trig.ANGLE90 && fPlayerArc < Trig.ANGLE180) {
            low = Trig.ANGLE90;
            high = Trig.ANGLE180;
        }
        if (fPlayerArc >= Trig.ANGLE180 && fPlayerArc < Trig.ANGLE270) {
            low = Trig.ANGLE180;
            high = Trig.ANGLE270;
        }
        if (fPlayerArc >= Trig.ANGLE270 && fPlayerArc <=Trig.ANGLE360) {
            low = Trig.ANGLE270;
            high = Trig.ANGLE360;
        }

        // determine if we are closer to the high end or low end
        fPlayerArc = ((fPlayerArc - low) > (high - fPlayerArc)) ? high : low;
        setPlayerPos();
    }

    /**
     * Check position to see if we are in a question item, if so, advance question items.
     * @param mapIndex
     */
    private void checkQuestions(int mapIndex) {
        char questItemTypeHit = fQuestionPosData.getQuestionItemTypeAt(mapIndex, fQuestions, fCurQuestion);

        if (questItemTypeHit == '?') {
            char questNumberHit = fQuestionPosData.getValue(mapIndex);
            if (fCurQuestion == questNumberHit) return;  // if we encountered the same question nothing changes
            fCurQuestion = questNumberHit;  // we are now in question mode, track which question we are on
            //Question curQuest = fQuestions.returnQuestion(fCurQuestion);
            setInQuestionMode(true);
            setInQuestionPopupMode(true);
            if (fPlatform.usingSysSoundFiles()) fConfig.getfSoundEffects().playSoundFile(GeneralConfig.QUESTION_EFFECT);
        } else {
            // if we are in question mode and we hit an answer, we are done with this
            // question, go back to looking for another question
            if ((fCurQuestion != '0')
                    && ((questItemTypeHit == 'A')
                    || (questItemTypeHit == 'B')
                    || (questItemTypeHit == 'C')
                    || (questItemTypeHit == 'D'))) {
                if (fPlatform.usingSysSoundFiles()) fConfig.getfSoundEffects().playSoundFile(GeneralConfig.ANSWER_EFFECT);
                fCurQuestion = '0';             // we are done with question--go to normal mode looking for a new question
                setInQuestionMode(false);
                setInQuestionPopupMode(false);
            }
        }
    }

    /**
     * Check position to see if we are in a trap zone, if so, go to specified destination or perform the specified action.
     * Returns true if we are inside a trap, otherwise returns false.
     */
    private boolean checkTraps(int x, int y) {
        Trap oldTrap = fCurTrap;
        fCurTrap = fConfig.insideATrap(x, y);
        if (fCurTrap == null) return false;
        if (fCurTrap == oldTrap) return true;  // is still in the same old trap don't do everything all over again

        // play sound effect if available
        if (fCurTrap.isUsingSound()) {
            fConfig.getfSoundEffects().playSoundFile(fCurTrap.getSoundFile());
        }

        // go to a destination if available
        if (fCurTrap.isUsingDest()) {
            Dest newDest = fConfig.advanceToDest(fCurTrap.getGotoDest());
            if (newDest == null) return true;

            fCurDest = newDest;

            // if new destination has a new background, update background accordingly
            if (!newDest.isUseExistingBackground())
                fBackground = Background.createBackgroundFromDest(fPlatform, fConfig.getBackgroundCache(), newDest);

            // if new destination has a landscape, update landscape pixels accordingly
            if (!newDest.isUseExistingLandscape())
                fLandscapePixels = fConfig.getLandscapeCache().getLandscapePixelsFromDest(newDest);

            fPlayerX = fCurDest.getXPos();
            fPlayerY = fCurDest.getYPos();

            // sometimes maze designer wants to keep player's current angle after advancing
            if (!fCurDest.isUseExistingAngle()) {
                fPlayerArc = fCurDest.getAngle();
                fPlayerXDir = Trig.fCosTable[fPlayerArc];
                fPlayerYDir = Trig.fSinTable[fPlayerArc];
            }
        }
        return true;
    }

    /**
	 * Attempts a move to a new position checking to make sure we are not moving
	 * inside a wall. 
	 */
	private void attemptMove(int newPlayerX, int newPlayerY, boolean allowWallBounce) {
		// attempt moving to new x/y position
		int xGridIndex = newPlayerX >> MazeGlobals.TILE_SIZE_SHIFT; // this is essentially rounding down to a close grid line
		int yGridIndex = newPlayerY >> MazeGlobals.TILE_SIZE_SHIFT;
		int mapIndex = fMapData.convertPointToMapPos(xGridIndex, yGridIndex);
		if (mapIndex < (fMapData.getMapHeight() << fMapData.getMapShiftWidth())
				&& !fMapData.isWall(mapIndex)) {
			fPlayerX = newPlayerX;
			fPlayerY = newPlayerY;
            checkQuestions(mapIndex);
            checkTraps(fPlayerX, fPlayerY);
			return;
		}
        boolean isDesiredPosAProp = fPropData.isProp(mapIndex);  //track prop for bounce determination

		// can't move new x/y so just try x
		xGridIndex = newPlayerX >> MazeGlobals.TILE_SIZE_SHIFT;
		yGridIndex = fPlayerY >> MazeGlobals.TILE_SIZE_SHIFT; // keep old y
		mapIndex = fMapData.convertPointToMapPos(xGridIndex, yGridIndex);      // (newx, oldy) implies horiz wall bounce
		if (mapIndex < (fMapData.getMapHeight() << fMapData.getMapShiftWidth())
				&& !fMapData.isWall(mapIndex)) {
			fPlayerX = newPlayerX;

            checkQuestions(mapIndex);
            // do bounce if we are not inside a trap and not a prop
            if (!checkTraps(fPlayerX, fPlayerY) && allowWallBounce && !isDesiredPosAProp) {
                // correct angle and position as we bounce off horiz wall
                horizWallBounce();
                tooCloseToWallCorrection();
                if (fInAutoMoveMode) {
                    if (fPlatform.usingSysSoundFiles()) fConfig.getfSoundEffects().playSoundFile(GeneralConfig.BOUNCE_EFFECT);
                    fVibrateEffects.vibrate(50);
                }
            }
			return;
		}

		// just try y
		xGridIndex = fPlayerX >> MazeGlobals.TILE_SIZE_SHIFT; // keep old x
		yGridIndex = newPlayerY >> MazeGlobals.TILE_SIZE_SHIFT;
		mapIndex = fMapData.convertPointToMapPos(xGridIndex, yGridIndex);       // (oldx, newy) implies vert wall bounce
		if (mapIndex < (fMapData.getMapHeight() << fMapData.getMapShiftWidth())
				&& !fMapData.isWall(mapIndex)) {
			fPlayerY = newPlayerY;

            checkQuestions(mapIndex);
            // do bounce if not inside a trap and not a prop
            if (!checkTraps(fPlayerX, fPlayerY) && allowWallBounce && !isDesiredPosAProp) {
                // correct angle and position to simulate bounce off vert wall
                vertWallBounce();
                tooCloseToWallCorrection();
                if (fInAutoMoveMode) {
                    if (fPlatform.usingSysSoundFiles()) fConfig.getfSoundEffects().playSoundFile(GeneralConfig.BOUNCE_EFFECT);
                    fVibrateEffects.vibrate(50);
                }
            }
			return;
		}
	}

    /**
     * Make the player angle appear as if there was a slight bounce off the wall and straighten
     * up the player in the meantime to a more favorable position, if possible.
     */
    private void horizWallBounce() {

        // horiz wall hit going nearly straight 90 degrees means a head-on hit
        if ((Trig.ANGLE85 < fPlayerArc) && (fPlayerArc < Trig.ANGLE95)) {
            int xGridIndex = fPlayerX >> MazeGlobals.TILE_SIZE_SHIFT;
            int yGridIndex = fPlayerY >> MazeGlobals.TILE_SIZE_SHIFT;
            int curPlayerPosMapIndex = fMapData.convertPointToMapPos(xGridIndex, yGridIndex);

            // correct angle to a more favorable position by looking at surrounding walls
            if (fMapData.isWall(curPlayerPosMapIndex + 1))    // (next tile is left tile)
                fPlayerArc = fPlayerArc + Trig.ANGLE45;       // on the left is a wall, so go right
            else
                fPlayerArc = fPlayerArc - Trig.ANGLE45;       // otherwise go right
        }

        // horiz wall hit going nearly straight 270 degrees means a head-on hit
        else if ((Trig.ANGLE265 < fPlayerArc) && (fPlayerArc < Trig.ANGLE275)) {
            int xGridIndex = fPlayerX >> MazeGlobals.TILE_SIZE_SHIFT;
            int yGridIndex = fPlayerY >> MazeGlobals.TILE_SIZE_SHIFT;
            int curPlayerPosMapIndex = fMapData.convertPointToMapPos(xGridIndex, yGridIndex);

            // correct angle to a more favorable position by looking at surrounding walls
            if (fMapData.isWall(curPlayerPosMapIndex - 1))     // (prev tile is left tile)
                fPlayerArc = fPlayerArc + Trig.ANGLE45;        // on the left is a wall, so go right
            else
                fPlayerArc = fPlayerArc - Trig.ANGLE45;        // otherwise go right
        }

        else if (fPlayerArc >= Trig.ANGLE0   && fPlayerArc <  Trig.ANGLE45)  fPlayerArc = Trig.ANGLE0;
        else if (fPlayerArc >= Trig.ANGLE45  && fPlayerArc <  Trig.ANGLE90)  fPlayerArc = fPlayerArc - Trig.ANGLE45;
        else if (fPlayerArc >= Trig.ANGLE90  && fPlayerArc <  Trig.ANGLE135) fPlayerArc = fPlayerArc + Trig.ANGLE45;
        else if (fPlayerArc >= Trig.ANGLE135 && fPlayerArc <  Trig.ANGLE180) fPlayerArc = Trig.ANGLE180;
        else if (fPlayerArc >= Trig.ANGLE180 && fPlayerArc <  Trig.ANGLE225) fPlayerArc = Trig.ANGLE180;
        else if (fPlayerArc >= Trig.ANGLE225 && fPlayerArc <  Trig.ANGLE270) fPlayerArc = fPlayerArc - Trig.ANGLE45;
        else if (fPlayerArc >= Trig.ANGLE270 && fPlayerArc <  Trig.ANGLE315) fPlayerArc = fPlayerArc + Trig.ANGLE45;
        else if (fPlayerArc >= Trig.ANGLE315 && fPlayerArc <= Trig.ANGLE360) fPlayerArc = Trig.ANGLE0;

        if (fPlayerArc >= Trig.ANGLE360) fPlayerArc -= Trig.ANGLE360;
        if (fPlayerArc < Trig.ANGLE0) fPlayerArc += Trig.ANGLE360;
        setPlayerPos();
    }

    /**
     * Make the player angle appear as if there was a slight bounce off the wall and straighten
     * up the player in the meantime to a more favorable position, if possible.
     */
    private void vertWallBounce() {

        // vert wall hit going nearly straight 0 degrees means a head-on hit
        if ((Trig.ANGLE355 < fPlayerX) && (fPlayerX < Trig.ANGLE360)
                || ((Trig.ANGLE0 <= fPlayerArc) && (fPlayerArc < Trig.ANGLE5))) {
            int xGridIndex = fPlayerX >> MazeGlobals.TILE_SIZE_SHIFT;
            int yGridIndex = fPlayerY >> MazeGlobals.TILE_SIZE_SHIFT;
            int curPlayerPosMapIndex = fMapData.convertPointToMapPos(xGridIndex, yGridIndex);

            // correct angle to a more favorable position by looking at surrounding walls
            if (fMapData.isWall(curPlayerPosMapIndex - fMapData.getMapWidth()))    // (one row up, same column is left tile)
                fPlayerArc = fPlayerArc + Trig.ANGLE45;     // on the left is a wall, so go right
            else
                fPlayerArc = fPlayerArc - Trig.ANGLE45;     // otherwise go right
        }

        // vert wall hit going nearly straight 180 degrees means a head-on hit
        else if ((Trig.ANGLE175 < fPlayerArc) && (fPlayerArc < Trig.ANGLE185)) {
            int xGridIndex = fPlayerX >> MazeGlobals.TILE_SIZE_SHIFT;
            int yGridIndex = fPlayerY >> MazeGlobals.TILE_SIZE_SHIFT;
            int curPlayerPosMapIndex = fMapData.convertPointToMapPos(xGridIndex, yGridIndex);

            // correct angle to a more favorable position by looking at surrounding walls
            if (fMapData.isWall(curPlayerPosMapIndex + fMapData.getMapWidth()))     // (one row below, same column is left tile)
                fPlayerArc = fPlayerArc + Trig.ANGLE45;     // on the left is a wall, so go right
            else
                fPlayerArc = fPlayerArc - Trig.ANGLE45;     // otherwise go right
        }

        else if (fPlayerArc >= Trig.ANGLE0   && fPlayerArc <  Trig.ANGLE45)  fPlayerArc = fPlayerArc + Trig.ANGLE45;
        else if (fPlayerArc >= Trig.ANGLE45  && fPlayerArc <  Trig.ANGLE90)  fPlayerArc = Trig.ANGLE90;
        else if (fPlayerArc >= Trig.ANGLE90  && fPlayerArc <  Trig.ANGLE135) fPlayerArc = Trig.ANGLE90;
        else if (fPlayerArc >= Trig.ANGLE135 && fPlayerArc <  Trig.ANGLE180) fPlayerArc = fPlayerArc - Trig.ANGLE45;
        else if (fPlayerArc >= Trig.ANGLE180 && fPlayerArc <  Trig.ANGLE225) fPlayerArc = fPlayerArc + Trig.ANGLE45;
        else if (fPlayerArc >= Trig.ANGLE225 && fPlayerArc <  Trig.ANGLE270) fPlayerArc = Trig.ANGLE270;
        else if (fPlayerArc >= Trig.ANGLE270 && fPlayerArc <  Trig.ANGLE315) fPlayerArc = Trig.ANGLE270;
        else if (fPlayerArc >= Trig.ANGLE315 && fPlayerArc <= Trig.ANGLE360) fPlayerArc = fPlayerArc - Trig.ANGLE45;

        if (fPlayerArc >= Trig.ANGLE360) fPlayerArc -= Trig.ANGLE360;
        if (fPlayerArc < Trig.ANGLE0) fPlayerArc += Trig.ANGLE360;
        setPlayerPos();
    }

    /**
     * When walking parallel to a very close wall, the overall image looks indiscernible.  Here we check and correct
     * for this by adjusting the player position.
     */
    private void tooCloseToWallCorrection() {
        if (((Trig.ANGLE355 < fPlayerX) && (fPlayerX < Trig.ANGLE360) || ((Trig.ANGLE0 <= fPlayerArc) && (fPlayerArc < Trig.ANGLE5)))
                || ((Trig.ANGLE175 < fPlayerArc) && (fPlayerArc < Trig.ANGLE185))) {
            // let the player get inside any corner just case it is a kissing corner walls scenario where player can walk through
            if (nearCornerOfTile()) return;
            int xGridIndex = fPlayerX >> MazeGlobals.TILE_SIZE_SHIFT;
            int yGridIndex = (fPlayerY - fTooCloseProximity) >> MazeGlobals.TILE_SIZE_SHIFT;
            int mapIndex = fMapData.convertPointToMapPos(xGridIndex, yGridIndex);
            if (fMapData.isWall(mapIndex) && !fPropData.isProp(mapIndex)) {
                fPlayerY += MazeGlobals.HALF_PLAYER_SPEED;
            }

            xGridIndex = fPlayerX >> MazeGlobals.TILE_SIZE_SHIFT;
            yGridIndex = (fPlayerY + fTooCloseProximity) >> MazeGlobals.TILE_SIZE_SHIFT;
            mapIndex = fMapData.convertPointToMapPos(xGridIndex, yGridIndex);
            if (fMapData.isWall(mapIndex) && !fPropData.isProp(mapIndex)) {
                fPlayerY -= MazeGlobals.HALF_PLAYER_SPEED;
            }
        }

        else if (((Trig.ANGLE85 < fPlayerArc) && (fPlayerArc < Trig.ANGLE95))
                || ((Trig.ANGLE265 < fPlayerArc) && (fPlayerArc < Trig.ANGLE275))) {
            if (nearCornerOfTile()) return;
            int xGridIndex = (fPlayerX + fTooCloseProximity) >> MazeGlobals.TILE_SIZE_SHIFT;
            int yGridIndex = fPlayerY >> MazeGlobals.TILE_SIZE_SHIFT;
            int mapIndex = fMapData.convertPointToMapPos(xGridIndex, yGridIndex);
            if (fMapData.isWall(mapIndex) && !fPropData.isProp(mapIndex)) {
                fPlayerX -= MazeGlobals.HALF_PLAYER_SPEED;
            }
            xGridIndex = (fPlayerX - fTooCloseProximity) >> MazeGlobals.TILE_SIZE_SHIFT;
            yGridIndex = fPlayerY >> MazeGlobals.TILE_SIZE_SHIFT;
            mapIndex = fMapData.convertPointToMapPos(xGridIndex, yGridIndex);
            if (fMapData.isWall(mapIndex) && !fPropData.isProp(mapIndex)) {
                fPlayerX += MazeGlobals.HALF_PLAYER_SPEED;
            }
        }
    }

    /**
     * Returns true if player is in one of the four corners of the current tile.
     */
    private boolean nearCornerOfTile() {
        int edgeBoundary = fTooCloseProximity + 1;
        int xTilePos = fPlayerX % MazeGlobals.TILE_SIZE;
        int yTilePos = fPlayerY % MazeGlobals.TILE_SIZE;
        boolean xNearEdge = (xTilePos < edgeBoundary || xTilePos > (MazeGlobals.TILE_SIZE - edgeBoundary));
        boolean yNearEdge = (yTilePos < edgeBoundary || yTilePos > (MazeGlobals.TILE_SIZE - edgeBoundary));
        return xNearEdge && yNearEdge;
    }
}



