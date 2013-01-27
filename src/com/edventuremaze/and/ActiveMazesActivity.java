package com.edventuremaze.and;

import android.app.ListActivity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Author: Brian Pratt
 */

public class ActiveMazesActivity extends ListActivity {
    final static long CACHE_STALE_DURATIUON = 7000l;
    final static String sLogLabel = "--->ActiveMazesActivity: ";
    final static String SERVLET_GET_ACTIVE_LIST = "GetActiveMazeList.jsp";

    private ActiveMazesListAdapter fAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activemazes);

        // volume control will adjust the media, not the phone volume for this app
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // makes sure media player is not playing
        if (StartActivity.getMediaPlayer().isPlaying()) {
            StartActivity.getMediaPlayer().stop();
            StartActivity.disposeMediaPlayer();
        }

        populateList();
    }

    private void populateList() {
        String[] tokens;
        try {
            String strActiveList = doGetActiveList();
            strActiveList = strActiveList.trim();
            String delims = "[~]";
            tokens = strActiveList.split(delims);
        } catch (Exception e) {
            Toast.makeText(ActiveMazesActivity.this, "Unable to establish connection with host.", Toast.LENGTH_LONG).show();
            Toast.makeText(ActiveMazesActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            tokens = new String[]{};
        }

        ArrayList activeList = new ArrayList<ActiveMazeRec>();
        int cnt = 0;
        ActiveMazeRec active = null;
        for (String token : tokens) {
            int pos = cnt % 4;
            if (pos == 0) {
                if (active != null) {
                    activeList.add(active);
                }
                active = new ActiveMazeRec();
                active.setId(cnt / 4);
                active.setMazeName(token);
            }

            if (pos == 1) {
                if (active != null)
                    active.setMazeAuthor(token);
            }

            if (pos == 2) {
                if (active != null)
                    active.setExpiredStr(token);
            }

            if (pos == 3) {
                if (active != null)
                    active.setMazeId(token);
            }

            cnt++;
        }
        if (active != null) {
            activeList.add(active);
        }

        fAdapter = new ActiveMazesListAdapter(this, activeList);
        setListAdapter(fAdapter);
    }

    // Get the active maze list by calling server.
    protected String doGetActiveList() throws Exception {
        // pull the active list out of a make-shift cache, if it isn't stale already... this eliminates unnecessary multiple calls to the server
        // Log.i(sLogLabel, "BEFORE --->--->--->--->--->--->--->--->--->---> connecting with server");
        if (StartActivity.getCachedResponse().length() > 0 && System.currentTimeMillis() < StartActivity.getLastCacheTime() + CACHE_STALE_DURATIUON) {
            //Log.i(sLogLabel, "AFTER (USING CACHE)  --->--->--->--->--->: " + StartActivity.getCachedResponse());
            return StartActivity.getCachedResponse();
        }

        String response = doServerCallForActiveList(this);

        // if we got a valid response, then update cache and time received
        //Log.e(sLogLabel, "AFTER --->--->--->--->--->: " + response);
        if (response.length() > 0) {
            StartActivity.setCachedResponse(response);
            StartActivity.setLastCacheTime(System.currentTimeMillis());
            //Log.i(sLogLabel, "summary --->--->--->: " + StartActivity.getLastCacheTime() + " " + response);
        }

        return response;
    }

    // Static method called internally and externally to get the list of active mazes from the server.
    public static String doServerCallForActiveList(Context context) throws Exception {
        String host = context.getString(R.string.maze_host_url);
        String urlStr = host + SERVLET_GET_ACTIVE_LIST;

        String response = "";
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(urlStr);

        HttpResponse execute = client.execute(httpGet);

        // check the http return status code
        int statusCode = execute.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new Exception("Unable to connect with server");
        }

        // get response from url
        InputStream content = execute.getEntity().getContent();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
        String line = "";
        while ((line = buffer.readLine()) != null) {
            response += line;
        }

        return response;
    }

    /**
     * Force an adapter reload when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // makes sure media player is not playing
        if (StartActivity.getMediaPlayer().isPlaying()) {
            StartActivity.getMediaPlayer().stop();
            StartActivity.disposeMediaPlayer();
        }

        if (fAdapter != null)
            fAdapter.forceReload();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activemazesmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
        case R.id.refreshlist:
            //fAdapter.forceReload();
            populateList();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}


