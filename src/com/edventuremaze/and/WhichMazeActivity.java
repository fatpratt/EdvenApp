package com.edventuremaze.and;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This module handles the dialog for the Which Maze activity that prompts the user
 * for the desired maze.
 *
 * @author brianpratt
 */

public class WhichMazeActivity extends Activity {
    final static String sLogLabel = "--->WhichMazeActivity:";

    public String fMazeId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whichmazeactivity);

        // makes sure media player is not playing
        if (StartActivity.getMediaPlayer().isPlaying()) {
            StartActivity.getMediaPlayer().stop();
            StartActivity.disposeMediaPlayer();
        }

        // volume control will adjust the media, not the phone volume for this app
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

    	Button buttonStart = (Button)findViewById(R.id.startbutton);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText mazeIdText = (EditText)findViewById(R.id.mazeidtext);
                Editable editableMazeId = mazeIdText.getText();
                fMazeId = editableMazeId.toString().trim();
                if (fMazeId.length() == 0) {
                    Toast.makeText(WhichMazeActivity.this, "Maze ID cannot be blank", Toast.LENGTH_LONG).show();
                    mazeIdText.requestFocus();
                    return;
                }

                // if the server is present then run the update task to get latest maze info
                if (pingServer()) {
                    v.setKeepScreenOn(true);
                    RunUpdateTask task = new RunUpdateTask(WhichMazeActivity.this, WhichMazeActivity.this, fMazeId, MazeTabActivity.getX2());
		            task.execute();
                } else {  // if not, we can still pull the maze out of the cache, maybe
                    Toast.makeText(WhichMazeActivity.this, "Unable to reach server for latest update, but attempting to load local copy of specified maze.", Toast.LENGTH_LONG).show();
                    finish();
                }

                //allDone(true);

            }
        });
    }

    // Returns true if the server is present.
    private boolean pingServer() {
        try {
            String response = ActiveMazesActivity.doServerCallForActiveList(this);
            return (response.length() > 0);
        } catch(Exception e) {
            return false;
        }
    }

    /**
     * To be called when RunUpdateTask completes as a signal that the background process is complete
     * @param success Means the download/update process returned successfully.
     */
/***
    private void allDone(boolean success) {
        if (success) {
            //Intent intent = getIntent();
            //intent.putExtra("returnedData", fMazeId);
            WhichMazeActivity.this.finish();
        }
    }
 ***/

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
    }

    @Override
	public void finish() {
        //Intent intent = getIntent();
        //intent.putExtra("returnedData", fMazeId);
        /***
		Intent data = new Intent();
		data.putExtra("returnedData", fMazeId);
		setResult(RESULT_OK, data);
         ****/

        MazeTabActivity.setMazeId(fMazeId);
        //MazeTabActivity.setX2(true);
        this.getParent().finish();
		super.finish();
	}


}
