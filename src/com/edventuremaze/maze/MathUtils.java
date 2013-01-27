package com.edventuremaze.maze;

/**
 * General math routines.
 *
 * @author brianpratt
 */
public class MathUtils {

    /**
     * Returns a base 36 number based upon given parameter.  For example if input is 10
     * the return is "a" and if input is 17 the return is "h"
     */
    static public String base10ToBase36(int i) {
        String strBase36;
        if (i > 9) {
            char base36 = (char) ((int) 'a' + (i - 10));
            strBase36 = "" + base36;
        } else strBase36 = "" + i;
        return strBase36;
    }

    /**
     * Returns a decimal number based upon a base 36 number for example if the input is an 'h' then return value is 17
     * assumes all input data is lowercase.
     */
    static public int base36ToBase10(char ch) {
        int dec = 0;
        if ((ch >= '0') && (ch <= '9')) dec = (int) ch - (int) '0';
        if (ch > '9') dec = ((int) ch - (int) 'a') + 10;
        return (dec);
    }

    /**
     * Power of two inverse:  for select values this method returns the power of two inverse (ie 64 returns 6 because 2
     * to the 6th is 64).
     */
    static public int logarithmBaseTwo(int num) {
        switch (num) {
            case 2:
                return 1;
            case 4:
                return 2;
            case 8:
                return 3;
            case 16:
                return 4;
            case 32:
                return 5;
            case 64:
                return 6;
            case 128:
                return 7;
            case 256:
                return 8;
            case 512:
                return 9;
            case 1024:
                return 10;
            default:
                return 6;
        }
    }

}
