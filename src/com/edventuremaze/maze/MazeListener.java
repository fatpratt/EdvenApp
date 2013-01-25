package com.edventuremaze.maze;

/**
 */
public interface MazeListener {
    public void questionModeChanged(boolean isInQuestionMode);
    public void questionPopupModeChanged(boolean isInQuestionPopupMode);
    public void autoMoveModeChanged(boolean isInAutoMoveMode);
}
