package com.edventuremaze.and;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This class represents a single list item in the ActiveMaze list.
 * Author: Brian Pratt
 */
public class ActiveMazeListItem extends LinearLayout {

    private ActiveMazeRec fActiveMazeRec;
    private ActiveMazesListAdapter fAdapter;
    private Context fContext;

    private TextView tvMazeName;
    private TextView tvAuthor;

    /**
     * Constructor
     */
    public ActiveMazeListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        fContext = context;
    }

    /**
     * Called by getView() in the adapter to fuse data with the view.
     */
    public void setActiveMazeRec(ActiveMazeRec activeMazeRec) {
        fActiveMazeRec = activeMazeRec;
        tvMazeName.setText(fActiveMazeRec.getMazeName());
        tvAuthor.setText(fActiveMazeRec.getMazeAuthor());
    }

    /**
     * Called by getView() in the adapter, this method associates this item with its adapter.
     */
    public void setAdapter(ActiveMazesListAdapter adapter) {
        fAdapter = adapter;
    }

    /**
     * Called after a view and all of its children have been inflated from XML, this method populates fields in the
     * view with data.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvAuthor = (TextView)findViewById(R.id.tvAuthor);
        tvMazeName = (TextView)findViewById(R.id.tvMazeName);
    }

}
