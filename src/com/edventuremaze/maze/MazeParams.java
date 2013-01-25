package com.edventuremaze.maze;

/**
 * This class contains all configurations passed into the maze.  Most notably the MapData object is always
 * passed into the maze class which describes what the maze looks like from a birds-eye-view. Similarly the
 * PropData and the QuestionPosData track where questions and props are from an arial perspective. All image
 * pixels associated with walls, landscapes, questions and props are contained in their respective objects.
 *
 * @author brianpratt
 */
public class MazeParams {
    private Platform fPlatform;

    private MapData fMapData;
    private MazeConfig fMazeConfig;
    private PropData fPropData;
    private QuestionPosData fQuestionPosData;
    private Questions fQuestions;
    private MazeListener fMazeListener;
    private boolean fX2;        // X2 refers to a general sizing parameter
                                // ... X2 == false means smaller, original, standard size
                                // ... X2 == true means double everything and now is the typical default size

    public MazeParams(Platform platform) {
        fPlatform = platform;
    }

    public Platform getPlatform() {
        return fPlatform;
    }

    public boolean getX2() {
        return fX2;
    }

    public void setX2(boolean x2) {
        fX2 = x2;
    }

    public MapData getMapData() {
        return fMapData;
    }

    public void setMapData(MapData mapData) {
        fMapData = mapData;
    }

    public MazeConfig getMazeConfig() {
        return fMazeConfig;
    }

    public void setMazeConfig(MazeConfig mazeConfig) {
        fMazeConfig = mazeConfig;
    }

    public PropData getPropData() {
        return fPropData;
    }

    public void setPropData(PropData propData) {
        fPropData = propData;
    }

    public QuestionPosData getQuestionPosData() {
        return fQuestionPosData;
    }

    public void setQuestionPosData(QuestionPosData questionPosData) {
        fQuestionPosData = questionPosData;
    }

    public Questions getQuestions() {
        return fQuestions;
    }

    public void setQuestions(Questions questions) {
        fQuestions = questions;
    }

    public MazeListener getMazeListener() {
        return fMazeListener;
    }

    public void setMazeListener(MazeListener mazeListener) {
        fMazeListener = mazeListener;
    }
}

