package com.edventuremaze.and;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;

public class StartActivity extends Activity {
    final static String sLogLabel = "--->StartActivity: ";

    static final int WHICH_MAZE_REQ_CODE = 100;

    private static Context sAppContext;
    private static MediaPlayer sMediaPlayer = null;
    private static boolean sHasShownSplashScreen = false;

    private Dialog fSplashDialog;

    private static String sCachedResponse = "";
    private static long sLastCacheTime = 0l;

    private static int sAdjustedDisplayWidth;
    private static int sAdjustedDisplayHeight;
    private static int sAdjustedLeftMargin;
    private static int sAdjustedTopMargin;
    private static boolean sAdjustedDisplayPos = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // setup global app context for all activities to use
        StartActivity.sAppContext = getApplicationContext();

        // volume control will adjust the media, not the phone volume for this app
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        establishScreenAdjustments();

        if (StartActivity.sHasShownSplashScreen == false)
            showSplashScreen();
        else
            showMainContent();
    }

    protected void establishScreenAdjustments() {
        // display can't be too big so we make adjustments
        Display display = ((Activity)this).getWindowManager().getDefaultDisplay();
        sAdjustedDisplayWidth = display.getWidth();
        sAdjustedDisplayHeight = display.getHeight();
        sAdjustedLeftMargin = 0;
        sAdjustedTopMargin = 0;
        if (display.getWidth() >  1024) {
            sAdjustedDisplayPos = true;
            sAdjustedDisplayWidth = 1024;
            sAdjustedDisplayHeight = (int)Math.round((double)sAdjustedDisplayWidth * 0.6);
            sAdjustedLeftMargin = (display.getWidth() - sAdjustedDisplayWidth) / 2;
            sAdjustedTopMargin = (display.getHeight() - sAdjustedDisplayHeight) / 2;
        }
    }

    protected void showMainContent() {
        // end splash screen music before showing the tabs
        if (StartActivity.getMediaPlayer().isPlaying()) {
            StartActivity.getMediaPlayer().stop();
        }
        StartActivity.disposeMediaPlayer();

        // as app starts bring up the "which maze" dialog
        //Intent intent = new Intent(this, WhichMazeActivity.class);
        Intent intent = new Intent(this, MazeTabActivity.class);
        startActivityForResult(intent, WHICH_MAZE_REQ_CODE);
    }


    // Removes the Dialog that displays the splash screen
    protected void removeSplashScreen() {
        if (fSplashDialog != null) {
            fSplashDialog.dismiss();
            fSplashDialog = null;
            showMainContent();
        }
    }

    // Shows the splash screen.
    protected void showSplashScreen() {
        StartActivity.sHasShownSplashScreen = true;

        // start playing music for the splash screen
        StartActivity.disposeMediaPlayer();
        StartActivity.getMediaPlayer(R.raw.twistin).start();

        // bring up the dialog
        fSplashDialog = new SplashScreen(this, R.style.SplashScreen);
        fSplashDialog.setContentView(R.layout.splashscreen);
        fSplashDialog.setCancelable(false);
        fSplashDialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {    // only allow the splash screen to appear for six seconds
            @Override
            public void run() {
                removeSplashScreen();
            }
        }, 6000);
    }

    /**
     * Static method which gets global app context.
     *
     * @return Returns the global app context.
     */
    public static Context getAppContext() {
        return StartActivity.sAppContext;
    }

    /**
     * Returns global media player for playing the background music.
     * @return Returns global media player for playing the background music.
     */
    public static MediaPlayer getMediaPlayer() {
        if (sMediaPlayer == null) {
            sMediaPlayer = MediaPlayer.create(StartActivity.sAppContext, R.raw.music);
        }
        return sMediaPlayer;
    }

    /**
     * Returns global media player for playing the specified music.
     * Same as getMediaPlayer above, but here you specify which media media resource.
     * @return Returns global media player for playing the specified media resource.
     */
    public static MediaPlayer getMediaPlayer(int resourceId) {
        if (sMediaPlayer == null) {
            sMediaPlayer = MediaPlayer.create(StartActivity.sAppContext, resourceId);
        }
        return sMediaPlayer;
    }

    /**
     * Must be called when finished playing music.
     */
    public static void disposeMediaPlayer() {
        sMediaPlayer = null;
    }

    public static long getLastCacheTime() {
        return StartActivity.sLastCacheTime;
    }

    public static void setLastCacheTime(long lastCacheTime) {
        StartActivity.sLastCacheTime = lastCacheTime;
    }

    public static String getCachedResponse() {
        return StartActivity.sCachedResponse;
    }

    public static void setCachedResponse(String cachedResponse) {
        StartActivity.sCachedResponse = cachedResponse;
    }

    public static int getAdjustedLeftMargin() {
        return StartActivity.sAdjustedLeftMargin;
    }

    public static int getAdjustedTopMargin() {
        return StartActivity.sAdjustedTopMargin;
    }

    public static int getAdjustedDisplayWidth() {
        return StartActivity.sAdjustedDisplayWidth;
    }

    public static int getAdjustedDisplayHeight() {
        return StartActivity.sAdjustedDisplayHeight;
    }

    public static boolean isAdjustedDisplayPos() {
        return sAdjustedDisplayPos;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(this, "onStart()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onStart();
        //Toast.makeText(this, "onRestart()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ////Toast.makeText(this, "onResume()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(this, "onPause()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Toast.makeText(this, "onStop()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unbindDrawables(findViewById(R.id.MainView));
        System.gc();
    }

    /**
     * private void unbindDrawables(View view) {
     * if (view == null) return;
     * if (view.getBackground() != null) {
     * view.getBackground().setCallback(null);
     * }
     * if (view instanceof ViewGroup) {
     * for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
     * unbindDrawables(((ViewGroup) view).getChildAt(i));
     * }
     * ((ViewGroup) view).removeAllViews();
     * }
     * }
     * **
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WHICH_MAZE_REQ_CODE) { // start the maze when "which maze" dialog is closed
            String returnMazeId = data.getStringExtra("returnedMazeId");
            String returnX2 = data.getStringExtra("returnedX2");
            showMaze(returnMazeId, Boolean.parseBoolean(returnX2));
        }
    }

    protected void showMaze(String mazeId, boolean x2) {
        RayView rayView = new RayView(this, this, mazeId, x2);
        setContentView(rayView);
        rayView.initView();
    }

}