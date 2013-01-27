package com.edventuremaze.and;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

/**
 * User: upratbr
 */
public class SplashScreen extends Dialog implements DialogInterface  {
    private static Context fContext = null;

    public SplashScreen(Context context, int theme) {
        super(context, theme);
        fContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.splashscreen);

        // don't let screen go into saver mode
        View thisView = findViewById(R.id.SplashId);
        thisView.setKeepScreenOn(true);

        // after a fraction of a second (once things are settled), launch task to get active list
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callServerForActiveList();  // do something productive (load active list) while splash screen is showing
            }
        }, 400);
    }

    // call global static method to get active maze list and cache it
    protected void callServerForActiveList() {
        try {
            String response = ActiveMazesActivity.doServerCallForActiveList(fContext);
            if (response.length() > 0) {
                StartActivity.setCachedResponse(response);
                StartActivity.setLastCacheTime(System.currentTimeMillis());
            }
        } catch (Exception e) {
            ;
        }
    }
}
