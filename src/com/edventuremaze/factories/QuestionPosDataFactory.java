package com.edventuremaze.factories;

import com.edventuremaze.and.maze.PlatformAnd;
import com.edventuremaze.and.maze.QuestionPosDataAnd;
import com.edventuremaze.maze.Platform;
import com.edventuremaze.maze.QuestionPosData;

/**
 * Question pos data factory used to instantiate an object of appropriate platform returning a QuestionPosData interface.
 *
 * @author brianpratt
 */
public class QuestionPosDataFactory {

    /**
     * When passed in a platform object which is specific to an os or platform, this method will create an appropriate
     * QuestionPosData object of the specified platform.
     */
    public static QuestionPosData createQuestionPosData(Platform platform, String folder, int mapHeight, int mapWidth, int mapWidthShift, String wallFileName) {
        if (platform instanceof PlatformAnd) {
            return new QuestionPosDataAnd((PlatformAnd)platform, folder, mapHeight, mapWidth, mapWidthShift, wallFileName);
        }

//        if (platform instanceof PlatformApplet) {
//            return new QuestionPosDataApplet((PlatformApplet)platform, folder, mapHeight, mapWidth, mapWidthShift, wallFileName);
//        }

        // TODO: perhaps we will handle other platforms later...

//        if (platform instanceof PlatformGWT) {
//            return new QuestionPosDataGWT((PlatformGWT)platform, folder, mapHeight, mapWidth, mapWidthShift, wallFileName);
//        }

        return null;
    }

}
