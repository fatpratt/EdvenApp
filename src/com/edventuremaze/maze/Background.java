package com.edventuremaze.maze;

/**
 * Used to define a cache of pixels which define the background image in the view.
 *
 * The destination object actually defines the background.  There are two types of backgrounds: gradients
 * from RGB values, and backgrounds defined from an external file.
 * 
 * @author brianpratt
 */
public class Background {

    static final String sLogLabel = "--->Background:";

    private boolean fBackgroundFromFile;     // true --background file specified,  false--RGB values specified
    private boolean fBackgroundFromRGB;      // false--background file specified,  true--RGB values specified

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

    private int[] fMemPixels;

	/**
	 * Non-public constructor.  Call createBackgroundFromDest() instead.
	 */
	private Background() {
	}

    /**
     * Static function to create and return a background object instance based upon a destination object.
     * Call this method instead of a constructor to create an object instance.
     */
    public static Background createBackgroundFromDest(Platform platform, ImageFileCache backgroundCache, Dest dest) {
        if (dest.isUseExistingBackground()) {
            platform.logInfo(sLogLabel, "programmer:  check fUseExistingBackground before calling createBackgroundFromDest()");
        }

        Background thisBackground = new Background();
        thisBackground.fBackgroundFile = dest.getBackgroundFile();
        thisBackground.fBackgroundFromFile = dest.isBackgroundFromFile();
        thisBackground.fBackgroundFromRGB = dest.isBackgroundFromRGB();

        thisBackground.fSkyRed = dest.getSkyRed();
        thisBackground.fSkyGreen = dest.getSkyGreen();
        thisBackground.fSkyBlue = dest.getSkyBlue();
        thisBackground.fGroundRed = dest.getGroundRed();
        thisBackground.fGroundGreen = dest.getGroundGreen();
        thisBackground.fGroundBlue = dest.getGroundBlue();

        thisBackground.fSkyRedStep = dest.getSkyRedStep();
        thisBackground.fSkyGreenStep = dest.getSkyGreenStep();
        thisBackground.fSkyBlueStep = dest.getSkyBlueStep();

        thisBackground.fGroundRedStep = dest.getGroundRedStep();
        thisBackground.fGroundGreenStep = dest.getGroundGreenStep();
        thisBackground.fGroundBlueStep = dest.getGroundBlueStep();

        if (thisBackground.fBackgroundFromRGB) {
            thisBackground.fMemPixels = new int[MazeGlobals.PROJECTIONPLANEWIDTH * MazeGlobals.PROJECTIONPLANEHEIGHT];
            thisBackground.createGradientBackground();
        }
        if (thisBackground.fBackgroundFromFile) {
            ImagePixels imagePixels = backgroundCache.getBackgroundPixelsFromDest(dest);
            thisBackground.fMemPixels = imagePixels.getPixels();
        }

        return thisBackground;
    }

	/**
	 * Copies the background pixels to the destination.  Assumes the width and height of the destination 
	 * matches the background's dimensions.  This method accommodates both scenarios:  pixels from a gradient and
     * those from a file.
	 * @param destination Array of integers to represent destination pixels.
	 */
	public void copyBackgroundTo(int[] destination) {
        for (int row = 0; row < (MazeGlobals.PROJECTIONPLANEHEIGHT); row++) {
            for (int col = 0; col < MazeGlobals.PROJECTIONPLANEWIDTH; col++) {
                destination[(row * MazeGlobals.PROJECTIONPLANEWIDTH) + col] = fMemPixels[(row * MazeGlobals.PROJECTIONPLANEWIDTH) + col];
            }
        }
	}

	/**
	 * Creates a gradient background.
	 */
	protected void createGradientBackground() {
		int red = fSkyRed; // make adjustments here to change hues of sky
		int green = fSkyGreen;
		int blue = fSkyBlue;
		int redStep = fSkyRedStep;
		int greenStep = fSkyGreenStep;
		int blueStep = fSkyBlueStep;

		// paint sky
		for (int row = 0; row < (MazeGlobals.PROJECTIONPLANEHEIGHT >> 1); row++) { // ( >> 1 is dividing by 2)
			for (int col = 0; col < MazeGlobals.PROJECTIONPLANEWIDTH; col++) {
				fMemPixels[(row * MazeGlobals.PROJECTIONPLANEWIDTH) + col] = 255 << 24
						| red << 16 | green << 8 | blue;
			}
			red += redStep;
			red = red > 255 ? 255 : red;
			red = red < 0 ? 0 : red;
			green += greenStep;
			green = green > 255 ? 255 : green;
			green = green < 0 ? 0 : green;
			blue += blueStep;
			blue = blue > 255 ? 255 : blue;
			blue = blue < 0 ? 0 : blue;
		}

		red = fGroundRed; // make adjustments here to change color of ground
		green = fGroundGreen;
		blue = fGroundBlue;
		redStep = fGroundRedStep;
		greenStep = fGroundGreenStep;
		blueStep = fGroundBlueStep;

		// paint ground
		for (int row = (MazeGlobals.PROJECTIONPLANEHEIGHT >> 1); row < MazeGlobals.PROJECTIONPLANEHEIGHT; row++) {
			for (int col = 0; col < MazeGlobals.PROJECTIONPLANEWIDTH; col++) {
				fMemPixels[(row * MazeGlobals.PROJECTIONPLANEWIDTH) + col] = 255 << 24
						| red << 16 | green << 8 | blue;
			}
			red += redStep;
			red = red > 255 ? 255 : red;
			red = red < 0 ? 0 : red;
			green += greenStep;
			green = green > 255 ? 255 : green;
			green = green < 0 ? 0 : green;
			blue += blueStep;
			blue = blue > 255 ? 255 : blue;
			blue = blue < 0 ? 0 : blue;
		}
	}
	
}