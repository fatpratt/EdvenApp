package com.edventuremaze.maze;

/**
 * This class defines pre-calculated trigonometry functions for a set of select
 * angles relevant to ray casting.
 *  
 * @author brianpratt
 */
public class Trig {

	static final int ANGLE60  = MazeGlobals.PROJECTIONPLANEWIDTH; // field of view for player is 60 degrees and it follows
	// that 5.33 is the ratio of proj plan pixels and angle
	static final int ANGLE30  = (ANGLE60 / 2);
	static final int ANGLE90  = (ANGLE30 * 3);
	static final int ANGLE180 = (ANGLE90 * 2);
	static final int ANGLE270 = (ANGLE90 * 3);
	static final int ANGLE360 = (ANGLE60 * 6);
	static final int ANGLE0   = 0;
	static final int ANGLE5   = (ANGLE30 / 6);
	static final int ANGLE10  = (ANGLE5 * 2);
    static final int ANGLE45  = ANGLE5 + ANGLE10 + ANGLE10 + ANGLE10 + ANGLE10;
    static final int ANGLE135 = ANGLE90 + ANGLE45;
    static final int ANGLE225 = ANGLE180 + ANGLE45;
    static final int ANGLE315 = ANGLE270 + ANGLE45;
    static final int ANGLE85  = ANGLE90 - ANGLE5;
    static final int ANGLE95  = ANGLE90 + ANGLE5;
    static final int ANGLE265 = ANGLE270 - ANGLE5;
    static final int ANGLE275 = ANGLE270 + ANGLE5;
    static final int ANGLE355 = ANGLE360 - ANGLE5;
    static final int ANGLE175 = ANGLE180 - ANGLE5;
    static final int ANGLE185 = ANGLE180 + ANGLE5;
	
	// large precomputed trig and math tables for possible angle making life easier at runtime
	static float fSinTable[];
	static float fISinTable[];   // inverse sin table -- 1/sin(alpha)
	static float fCosTable[];
	static float fICosTable[];   // inverse cosine table -- 1/cos(alpha)
	static float fTanTable[];
	static float fITanTable[];   // inverse tangent table -- 1/tan(alpha)
	static float fFishTable[];   // corrects fish eye view
	static float fXStepTable[];  // for each possible angle, here is how far X spans when Y spans by 64
	static float fYStepTable[];  // for each possible angle, here is how far Y spans when X spans by 64

    /**
     * Convert from arc angles to radians for trig functions.
     */
    static float arcToRad(float arcAngle) {
        return ((float) (arcAngle * (float) Math.PI) / (float) Trig.ANGLE180);
    }

    /**
     * Converts from radians to degrees
     */
    static float radToDegrees(float angleRad) {
        return (float) (((float) 180.0 * (float) angleRad) / (float) Math.PI);
    }

    /**
     * Converts from ordinary degrees to the unique maze angle units used in this class.
     * For example:    60 (input)   320 (output)
     * @param degreesAngle Ordinary degrees such as 60, 320, 360, etc.
     * @return  Returns the unique maze angle units such as ANGLE60, ANGLE360
     */
    static int degreesToMazeAngleUnits(int degreesAngle) {
        // assumes:   ANGLE60 = PROJECTIONPLANEWIDTH
        return ((int) ((float) (MazeGlobals.PROJECTIONPLANEWIDTH * degreesAngle) / 60.0f));
    }

    /**
	 * Sets up the precalculated trig and math tables in memory which are indexed
	 * by angle look-ups to make things run smoothly at render time.
	 */
	static public void createTables() {
		int i;
		float radian;

		fSinTable = new float[ANGLE360 + 1]; // big tables for every possible
												// angle
		fISinTable = new float[ANGLE360 + 1];
		fCosTable = new float[ANGLE360 + 1];
		fICosTable = new float[ANGLE360 + 1];
		fTanTable = new float[ANGLE360 + 1];
		fITanTable = new float[ANGLE360 + 1];
		fFishTable = new float[ANGLE60 + 1];
		fXStepTable = new float[ANGLE360 + 1];
		fYStepTable = new float[ANGLE360 + 1];

		for (i = 0; i <= ANGLE360; i++) {
			radian = arcToRad(i) + (float) (0.0001); // convert to radian value
														// for trig calls
			fSinTable[i] = (float) Math.sin(radian);
			fISinTable[i] = (1.0F / (fSinTable[i]));
			fCosTable[i] = (float) Math.cos(radian);
			fICosTable[i] = (1.0F / (fCosTable[i]));
			fTanTable[i] = (float) Math.tan(radian);
			fITanTable[i] = (1.0F / fTanTable[i]);

			// west portion of aerial map
			if (i >= ANGLE90 && i < ANGLE270) {
				fXStepTable[i] = (float) (MazeGlobals.TILE_SIZE / fTanTable[i]);
				if (fXStepTable[i] > 0)
					fXStepTable[i] = -fXStepTable[i];
			}

			// east portion of aerial map
			else {
				fXStepTable[i] = (float) (MazeGlobals.TILE_SIZE / fTanTable[i]);
				if (fXStepTable[i] < 0)
					fXStepTable[i] = -fXStepTable[i];
			}

			// facing bottom portion of aerial map
			if (i >= ANGLE0 && i < ANGLE180) {
				fYStepTable[i] = (float) (MazeGlobals.TILE_SIZE * fTanTable[i]);
				if (fYStepTable[i] < 0)
					fYStepTable[i] = -fYStepTable[i];
			}

			// facing upper portion of aerial map
			else {
				fYStepTable[i] = (float) (MazeGlobals.TILE_SIZE * fTanTable[i]);
				if (fYStepTable[i] > 0)
					fYStepTable[i] = -fYStepTable[i];
			}
		}

		// build tables to correct fish eye view
		for (i = -ANGLE30; i <= ANGLE30; i++) {
			radian = arcToRad(i);
			fFishTable[i + ANGLE30] = (float) (1.0F / (float) Math.cos(radian));
		}
	}
}
