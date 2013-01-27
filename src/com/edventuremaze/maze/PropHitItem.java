package com.edventuremaze.maze;

/**
 *  A hit item class used to describe where a ray hits a prop.
 *
 *  @author brianpratt
 */
public class PropHitItem extends HitItem {
    private int fDist;             // distance between pl and prop
    private int fColMidProp;       // where does prop's center hit the rendered columns

    PropHitItem(int aMapPos) {
        super(aMapPos);
        fDist = -1;
        fColMidProp = -1;
    }

    void setDistance(int i) {
        fDist = i;
    }

    int getDistance() {
        return fDist;
    }

    void setColumnMidProp(int i) {
        fColMidProp = i;
    }

    int getColumnMidProp() {
        return fColMidProp;
    }

    /**
     * Calculates dist from player to prop and determines which column will be the prop's mid-point.  Sets member
     * variables and state accordingly.
     */
    public void setPropHitItemData(MapData mapData, int playerX, int playerY, int playerArc) {
        int x1 = playerX;
        int y1 = playerY;

        // initialize data we are gathering in case premature return
        this.setDistance(-1);
        this.setColumnMidProp(-1);

        // get mid point of prop
        int row = this.getMapPos() >> mapData.getMapShiftWidth();
        int col = this.getMapPos() % mapData.getMapWidth();
        int x2 = (col << MazeGlobals.TILE_SIZE_SHIFT) + (MazeGlobals.TILE_SIZE >> 1);
        int y2 = (row << MazeGlobals.TILE_SIZE_SHIFT) + (MazeGlobals.TILE_SIZE >> 1);

        // compute distance between player and prop
        int dist = 0;
        int xRel = x1 - x2;   // xRel: relative x diff from players's perspective
        int yRel = y1 - y2;
        if (0 == yRel) dist = Math.abs(xRel);
        else dist = (int) Math.sqrt(Math.abs(xRel * xRel + yRel * yRel));

        // determine angle defined by line between prop and pl
        float ang = 0;
        if ((y1 == y2) && (x1 - x2 < 0)) ang = 0;
        else if ((x1 == x2) && (y1 - y2 < 0)) ang = 90;
        else if ((y1 == y2) && (x1 - x2 > 0)) ang = 180;
        else if ((x1 == x2) && (y1 - y2 > 0)) ang = 270;
        else {
            int supAng = 0;    // must add supplemental angle to rotate to the correct quadrant
            if ((x1 < x2) && (y1 < y2)) supAng = 0;
            else if ((x1 > x2) && (y1 < y2)) supAng = 180;    // atan in this region should be neg
            else if ((x1 > x2) && (y1 > y2)) supAng = 180;
            else if ((x1 < x2) && (y1 > y2)) supAng = 360;    // atan in this region should be neg
            else return;      // should not be possible: x1 = x2 and y1 = y2

            float angleRad = (float) Math.atan((float) yRel / (float) xRel);
            float angleDegrees = Trig.radToDegrees(angleRad);

            ang = angleDegrees + supAng;
        }
        int angMazeUnits = (int) (ang * (float) Trig.ANGLE60 / (float) 60);  // convert to maze angle units

        // if angle difference is huge then it is likely we simply wrapped around from 0 to 360 degrees
        int fPlayerArcTemp = playerArc;
        if (fPlayerArcTemp > Trig.ANGLE270 && angMazeUnits < Trig.ANGLE90)
            fPlayerArcTemp -= Trig.ANGLE360;
        if (angMazeUnits > Trig.ANGLE270 && fPlayerArcTemp < Trig.ANGLE90)
            angMazeUnits -= Trig.ANGLE360;
        int colMidProp = (MazeGlobals.PROJECTIONPLANEWIDTH >> 1) - (fPlayerArcTemp - angMazeUnits);

        // adjust distance to avoid fish eye
        if (colMidProp >= 0 && colMidProp < MazeGlobals.PROJECTIONPLANEWIDTH)
            dist /= Trig.fFishTable[colMidProp];     // use table when possible
        else
            dist /= (float) (1.0F / (float) Math.cos(Trig.arcToRad(ang)));

        this.setDistance(dist);
        this.setColumnMidProp(colMidProp);
    }

}

