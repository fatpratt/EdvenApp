package com.edventuremaze.applet;

import java.awt.Color;

/**
 * Factor type creator that creates a color based on string description.
 *
 * @author brianpratt
 */
abstract public class ColorCreator {
    public static Color createColor(String colorDesc) {
        Color color = Color.BLACK;
        try {
            if (colorDesc.equalsIgnoreCase("black"))
                color = Color.black;
            else if (colorDesc.equalsIgnoreCase("blue"))
                color = Color.blue;
            else if (colorDesc.equalsIgnoreCase("cyan"))
                color = Color.cyan;
            else if (colorDesc.equalsIgnoreCase("dark gray") || colorDesc.equalsIgnoreCase("darkgray"))
                color = Color.darkGray;
            else if (colorDesc.equalsIgnoreCase("gray"))
                color = Color.gray;
            else if (colorDesc.equalsIgnoreCase("green"))
                color = Color.green;
            else if (colorDesc.equalsIgnoreCase("light gray") || colorDesc.equalsIgnoreCase("lightgray"))
                color = Color.lightGray;
            else if (colorDesc.equalsIgnoreCase("magenta"))
                color = Color.magenta;
            else if (colorDesc.equalsIgnoreCase("orange"))
                color = Color.orange;
            else if (colorDesc.equalsIgnoreCase("pink"))
                color = Color.pink;
            else if (colorDesc.equalsIgnoreCase("red"))
                color = Color.red;
            else if (colorDesc.equalsIgnoreCase("white"))
                color = Color.white;
            else if (colorDesc.equalsIgnoreCase("yellow"))
                color = Color.yellow;
            else color = Color.decode(colorDesc);
        } catch (Exception e) {
            color = Color.BLACK;
        }
        return color;
    }
}

