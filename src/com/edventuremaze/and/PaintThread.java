package com.edventuremaze.and;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import com.edventuremaze.maze.Maze;
import com.edventuremaze.maze.MazeGlobals;

/**
 * This class defines the game loop thread and follows the general game design
 * principles of a consistent cycle of refreshing the view.  Sleep time is adjusted  
 * based upon update time to keep the game smooth.
 * 
 * @author brianpratt
 */
public class PaintThread extends Thread {
    final static String sLogLabel = "--->PaintThread: ";

	// state of game (running or paused)
	public final static int RUNNING = 1;
	public final static int PAUSED = 2;
	public int fState = 1;
	
	public final static int IDEAL_LOOP_TIME = 70;  // ideal update loop time in millis (approx 14 fps)
	public final static int MIN_SLEEP_TIME = 5;    // minimum sleep time in millis

    protected RayView fRayView;
	protected SurfaceHolder fSurfaceHolder;
	protected Maze fMaze;
    protected TextAreaBox fTextArea;

	protected Bitmap fMazeBitmap;

	protected int fDisplayWidth = 320;
	protected int fDisplayHeight = 200;

	/**
	 * Constructor to establish the game loop thread.
     * @param rayView The view that created this paint thread.
	 * @param surfaceHolder  Surface holder.
	 * @param context The android activitiy.
	 * @param maze The game engine.
	 */
	public PaintThread(RayView rayView, SurfaceHolder surfaceHolder, Context context, Maze maze, TextAreaBox textArea) {
        fRayView = rayView;
		fSurfaceHolder = surfaceHolder;
		if (context instanceof Activity) {
			Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
			fDisplayWidth = display.getWidth();
            fDisplayHeight = display.getHeight();
		}

		fMaze = maze;
        fTextArea = textArea;
    	fMazeBitmap = Bitmap.createBitmap(MazeGlobals.PROJECTIONPLANEWIDTH, MazeGlobals.PROJECTIONPLANEHEIGHT, Bitmap.Config.ARGB_8888);
	}

	/**
	 * This method defines that update loop and is expected to be invoked via start() in the 
	 * SurfaceView class.  Unless paused, the thread loops continuously until the game is finished
	 * or the application is suspended.
	 */
	@Override
	public void run() {
        OverlayGraphics overlayGraphics = fRayView.getOverlayGraphics();
		while (fState == RUNNING) {
			long beforeTime = System.currentTimeMillis();
			//fMaze.update();	// future: update the game engine such as antagonists' positions
            fRayView.consumeAutoMove();  // if there are banked automoves we apply them per each thread

            if (fRayView.isInInstructionsMode()) {
                fRayView.drawInstructions();
            } else if (fRayView.isInOpeningCreditsMode()) {
                fRayView.drawOpeningCredits();
            } else {
                if (fRayView.isInOverlayInstMode()) fRayView.checkOverlayInstModeComplete();
                if (fRayView.isInOverlayTiltMode()) fRayView.checkOverlayTiltModeComplete();
                Canvas canvas = null;
                try {
                    // lock canvas so nothing else can use it
                    canvas = fSurfaceHolder.lockCanvas(null);
                    if (canvas == null) continue;
                    synchronized (fSurfaceHolder) {
                        if (StartActivity.isAdjustedDisplayPos()) canvas.drawColor(Color.BLACK);
                        // here is where the primary work is done... render a frame and display to screen
                        int[] pixels = fMaze.renderOneFrame();
                        fMazeBitmap.setPixels(pixels, 0, MazeGlobals.PROJECTIONPLANEWIDTH, 0, 0, MazeGlobals.PROJECTIONPLANEWIDTH, MazeGlobals.PROJECTIONPLANEHEIGHT);
                        canvas.drawBitmap(fMazeBitmap,        // pixels are drawn with scaling to match the display size
                                new Rect(0, 0, MazeGlobals.PROJECTIONPLANEWIDTH, MazeGlobals.PROJECTIONPLANEHEIGHT),
                                new Rect(StartActivity.getAdjustedLeftMargin(), StartActivity.getAdjustedTopMargin(),
                                        StartActivity.getAdjustedDisplayWidth()  + StartActivity.getAdjustedLeftMargin(),
                                        StartActivity.getAdjustedDisplayHeight() + StartActivity.getAdjustedTopMargin()),
                                null);

                        // see what overlay buttons need to be rendered
                        overlayGraphics.drawAllActiveGraphics(canvas);

                        // show question, if we are in question mode
                        if (fMaze.isInQuestionPopupMode()) {
                            fTextArea.setDisplayText(fMaze.getCurQuestionText());
                            fTextArea.render();
                            Bitmap textAreaBitmap = fTextArea.getBitmap();
                            canvas.drawBitmap(textAreaBitmap,       // we use popupAnimTracker to animate a telescoping popup window
                                new Rect(0, 0, textAreaBitmap.getWidth(), textAreaBitmap.getHeight()),
                                new Rect(fRayView.getPopupAnimTracker().getCurXOffset(),
                                    fRayView.getPopupAnimTracker().getCurYOffset(),
                                    fRayView.getPopupAnimTracker().getCurXOffset() + fRayView.getPopupAnimTracker().getCurWidth(),
                                    fRayView.getPopupAnimTracker().getCurYOffset() + fRayView.getPopupAnimTracker().getCurHeight()),
                                null);
                            fRayView.getPopupAnimTracker().next();
                        }
                    }
                } finally {
                    // don't leave the surface in an inconsistent state if an exception is thrown
                    if (canvas != null) {
                        fSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }

            // don't allow too much sleep if we have wasted too much time in the update
		    //Log.d("update loop", "--->" + (System.currentTimeMillis() - beforeTime));
		    long sleepTime = IDEAL_LOOP_TIME - (System.currentTimeMillis() - beforeTime);
		    if (sleepTime < MIN_SLEEP_TIME) sleepTime = MIN_SLEEP_TIME;

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException ex) {
				Log.e(sLogLabel, "--->" + "insomnia ... sleep issue");
			}
		}
	}
}