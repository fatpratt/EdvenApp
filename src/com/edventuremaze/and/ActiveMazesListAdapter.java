package com.edventuremaze.and;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Author: Brian Pratt
 */
public class ActiveMazesListAdapter extends BaseAdapter {

    private ActiveMazesActivity fActiveMazesActivity;
    private ArrayList<ActiveMazeRec> fActiveMazeList;
    private Context fContext;

    /**
     * Constructor
     */
	public ActiveMazesListAdapter(ActiveMazesActivity activeMazesActivity, ArrayList<ActiveMazeRec> activeMazeList) {
		super();
        this.fActiveMazeList = activeMazeList;
		this.fContext = activeMazesActivity;
        this.fActiveMazesActivity = activeMazesActivity;
	}

    /**
     * Returns the count of items in the list.
     */
	public int getCount() {
        return fActiveMazeList == null ? 0 : fActiveMazeList.size();
	}

    /**
     * Gets the specified active maze record.
     */
	public ActiveMazeRec getItem(int position) {
		return (null == fActiveMazeList) ? null : fActiveMazeList.get(position);
	}

    /**
     * Fulfills Adapter contract.
     */
	public long getItemId(int position) {
		return position;
	}

    /**
     * Fulfills Adapter contract. Gets a view that displays data based on the specified position in the data set.
     */
 	public View getView(int position, View convertView, ViewGroup parent) {
		ActiveMazeListItem activeListItem;
        ArrayList<ActiveMazeRec> activeList = fActiveMazeList;

        // recycle existing view if passed as parameter ... this will save memory and time on Android
		// (fyi...this only works if the base layout for all classes are the same)
		if (null == convertView) {
			activeListItem = (ActiveMazeListItem) View.inflate(fContext, R.layout.activemazeitem, null);
		} else {
			activeListItem = (ActiveMazeListItem) convertView;
		}
		activeListItem.setActiveMazeRec(activeList.get(position));   // marries the view with the data
        activeListItem.setAdapter(this);                            // tells which adapter it belongs with

        activeListItem.setOnClickListener(new OnItemClickListener(position));

		return activeListItem;
	}

    /**
     * Called by contained activity, this method simply calls notifyDataSetChanged which notified listeners
     * that the underlying data has been changed and any View reflecting the data set should refresh itself.
     */
	public void forceReload() {
		notifyDataSetChanged();
	}


    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position) {
            mPosition = position;
        }

        public void onClick(View arg0) {
            ActiveMazeRec mazeRec = (ActiveMazeRec)getItem(mPosition);
            MazeTabActivity.setMazeId(mazeRec.getMazeId());
            //MazeTabActivity.setX2(false);
            arg0.setKeepScreenOn(true);
            RunUpdateTask task = new RunUpdateTask(fActiveMazesActivity, fActiveMazesActivity, mazeRec.getMazeId(), MazeTabActivity.getX2());
            task.execute();
//            fActiveMazesActivity.finish();
        }
    }

}


