package com.edventuremaze.maze;

import java.util.Comparator;
import java.util.Map;

/**
 * This class is used to help sort the prop hit items map in the maze (based on distance) so that distant objects
 * don't appear on top of close objects.
 *
 * @author brianpratt
 */
public class PropHitItemComparator implements Comparator {

    Map fBase;

    public PropHitItemComparator(Map base) {
        fBase = base;
    }

    public int compare(Object a, Object b) {
        PropHitItem aPropHitItem = (PropHitItem)fBase.get(a);
        PropHitItem bPropHitItem = (PropHitItem)fBase.get(b);

        int distA = aPropHitItem.getDistance();
        int distB = bPropHitItem.getDistance();
        return distB - distA;
    }
}
