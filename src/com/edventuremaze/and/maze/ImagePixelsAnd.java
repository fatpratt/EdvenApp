package com.edventuremaze.and.maze;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.edventuremaze.and.utils.FileUtilsAnd;

import com.edventuremaze.maze.ImagePixels;
import com.edventuremaze.maze.Platform;

import java.io.File;
import java.io.InputStream;

/**
 * This class is the Android specific implementation of the ImagePixels used to define a cache of pixels accessible from
 * an integer array.
 *
 * @author brianpratt
 */

public class ImagePixelsAnd implements ImagePixels {
    final static String sLogLabel = "--->ImagePixelsAnd:";

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
    public ImagePixelsAnd(Platform platform) {
        fWidth = 0;
        fHeight = 0;
        fPixels = new int[0];
    }

    /**
     * Establishes pixels from an image file.
     */
    public ImagePixelsAnd(Platform platform, String folder, String fileName) {
        Context context = ((PlatformAnd)platform).getContext();
        ContextWrapper cw = new ContextWrapper(context);
        File path = cw.getDir(folder + ((PlatformAnd)platform).getFolderSuffix(), Context.MODE_PRIVATE);
        InputStream in = null;
        try {
            String fullFileName = FileUtilsAnd.appendSlash(path.toString()) + fileName;
            Bitmap bitmap = BitmapFactory.decodeFile(fullFileName);
            if (bitmap == null) throw new Exception("BitmapFactory.decodeFile() failed perhaps due to invalid filename: " + fullFileName);
            fWidth = bitmap.getWidth();
            fHeight = bitmap.getHeight();

            fPixels = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(fPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            bitmap.recycle();
        } catch(Exception e) {
            ((PlatformAnd)platform).logError(sLogLabel, e.getMessage());
        }
    }
}




