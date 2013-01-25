package com.edventuremaze.applet.maze;

import com.edventuremaze.applet.ResourceDirectory;
import com.edventuremaze.applet.TextUtils;
import com.edventuremaze.maze.ImagePixels;
import com.edventuremaze.maze.Platform;

import java.applet.Applet;
import java.awt.*;
import java.awt.image.PixelGrabber;
import java.net.URL;

/**
 * This class is the Applet specific implementation of the ImagePixels used to define a cache of pixels accessible from
 * an integer array.
 *
 * @author brianpratt
 */

public class ImagePixelsApplet implements ImagePixels {
    final static String sLogLabel = "--->ImagePixelsApplet:";

    private int fPixels[];
    private int fWidth = 0;
    private int fHeight = 0;

    public int getWidth() {
        return fWidth;
    }

    public int getHeight() {
        return fHeight;
    }

    public int[] getPixels() {
        return fPixels;
    }

    /**
     *  Constructor - creates a blank image pixel object.
     */
    public ImagePixelsApplet(Platform platform) {
        fWidth = 0;
        fHeight = 0;
        fPixels = new int[0];
    }

    /**
     * Establishes pixels from an image file.
     */
    public ImagePixelsApplet(Platform platform, String folder, String fileName) {
        Applet app = ((PlatformApplet) platform).getApplet();
        ResourceDirectory resourceDir = ((PlatformApplet) platform).getResourceDir();

        URL appropPathURL = resourceDir.getAppropPathURL();
        System.out.println("Loading image: " + fileName);

        Image anImg = app.getImage(appropPathURL, fileName);
        MediaTracker watch = new MediaTracker(app);  // do the media tracker thing
        watch.addImage(anImg, 201);

        try {
            watch.waitForAll();
        } catch (InterruptedException i) {
            i.printStackTrace();
            System.out.println("Couldn't load " + fileName + " image.");
            return;
        }

        if (watch.isErrorAny()) {
            System.out.println("Couldn't load " + fileName + " image. Watch issue.");
            return;
        }

        // grab pixels from image
        int[] tmpPixels = new int[anImg.getWidth(null) * anImg.getHeight(null)];
        PixelGrabber grabber = new PixelGrabber(anImg, 0, 0, anImg.getWidth(null), anImg.getHeight(null),
                tmpPixels, 0, anImg.getWidth(null));
        try {
            grabber.grabPixels();
            fWidth = anImg.getWidth(null);
            fHeight = anImg.getHeight(null);
            fPixels = tmpPixels;
        } catch (InterruptedException e) {
            Graphics g = app.getGraphics();
            TextUtils.drawErrorMessage(g, "Unable to get pixels from image '" + fileName + ".'");
            TextUtils.drawErrorMessage(g, "Make sure the file exists and is present in the appropriate path.");
            g.dispose();
        }
    }
}




