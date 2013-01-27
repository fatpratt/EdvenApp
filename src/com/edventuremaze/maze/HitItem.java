package com.edventuremaze.maze;

/**
 * Generic base class for an item hit by a ray.
 *
 * @author brianpratt
 */
public class HitItem {
    private int mapPos;       // current position in map where item was hit

    HitItem(int aMapPos) {
        mapPos = -1;
        setMapPos(aMapPos);
    }

    void setMapPos(int aMapPos) {
        mapPos = aMapPos;
    }

    int getMapPos() {
        return mapPos;
    }
}

