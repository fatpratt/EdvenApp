package com.edventuremaze.and;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

/**
 * Main entry point activity for the app.
 * Author: Brian Pratt
 */
public class MazeTabActivity extends android.app.TabActivity {
    final static String sLogLabel = "--->MazeTabActivity:";

    static String sMazeId = "";
    static boolean sX2 = false;

    final static int TAB_WIDTH = 160;
    final static int TAB_HEIGHT = 41;

    final static long MEM_CAPACITY_REQUIRED = 29000000L;

    TabHost mTabHost;

    /**
     * Sets the maze id of the maze selected by the user.
     */
    static void setMazeId(String mazeId) {
        sMazeId = mazeId;
    }

    /**
     * Sets the value of whether or not to use high res images (X2).
     */
    static void setX2(boolean x2) {
        sX2 = x2;
    }

    /**
     * Gets whether or not we are using high res images (X2).
     */
    static boolean getX2() {
        return sX2;
    }

    /**
     * Establishes the tab control.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mazetab);

        mTabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent().setClass(this, ActiveMazesActivity.class);
        spec = mTabHost.newTabSpec("active").setIndicator("Active Mazes").setContent(intent);
        mTabHost.addTab(spec);

        intent = new Intent().setClass(this, WhichMazeActivity.class);
        spec = mTabHost.newTabSpec("search").setIndicator("Maze Search").setContent(intent);
        mTabHost.addTab(spec);

        mTabHost.getTabWidget().getChildAt(0).setLayoutParams(new LinearLayout.LayoutParams(TAB_WIDTH, TAB_HEIGHT));
        mTabHost.getTabWidget().getChildAt(1).setLayoutParams(new LinearLayout.LayoutParams(TAB_WIDTH, TAB_HEIGHT));

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                int i = getTabHost().getCurrentTab();
            }
        });

        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        setX2(maxMemory > MEM_CAPACITY_REQUIRED);
        Log.d(sLogLabel, "---> maxMemory is: " + maxMemory + "   X2 is: " + sX2);
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("returnedMazeId", sMazeId);
        data.putExtra("returnedX2", "" + sX2);
        setResult(RESULT_OK, data);
        super.finish();
    }

}



