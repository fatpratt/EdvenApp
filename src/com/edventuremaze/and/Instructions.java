package com.edventuremaze.and;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;

public class Instructions {
    final static String sLogLabel = "--->Instructions:";

    /**
     * Draws the instructions bitmap saved as a resource.
     */
    public void drawInstructions(Context context, SurfaceHolder holder) {

        Surface surface = holder.getSurface();
        Canvas canvas = null;
        try {
            // lock canvas so nothing else can use it
            canvas = holder.lockCanvas(null);
            if (canvas == null) return;
            if (context == null) return;
            if (holder == null) return;
            synchronized (holder) {
                Bitmap instructionsBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.instructions);

                Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
                int displayWidth = display.getWidth();
                int displayHeight = display.getHeight();

                canvas.drawBitmap(instructionsBitmap,
                        new Rect(0, 0, instructionsBitmap.getWidth(), instructionsBitmap.getHeight()),
                        new Rect(0, 0, displayWidth, displayHeight),
                        null);
                instructionsBitmap.recycle();
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}


