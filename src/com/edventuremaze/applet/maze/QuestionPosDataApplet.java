package com.edventuremaze.applet.maze;

import com.edventuremaze.applet.TextUtils;
import com.edventuremaze.applet.utils.FileUtilsApplet;
import com.edventuremaze.applet.utils.MyDataInputStream;
import com.edventuremaze.factories.ImagePixelsFactory;
import com.edventuremaze.maze.*;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.HashMap;

/**
 * This class is the applet specific implementation of Question Pos Data which contains the arial view of the question
 * position data and all questions image pixels.
 *
 * @author brianpratt
 */
public class QuestionPosDataApplet implements QuestionPosData {
    static final String sLogLabel = "--->QuestionPosDataApplet:";

    private PlatformApplet fPlatform;
    private String fFolder;
    private int fMapHeight;
    private int fMapWidth;
    private int fMapWidthShift;
    private String fWallFileName;
    private Graphics fGraphics;

    private char fQuestionPosData[];              // like mapData, this is an arial view of question locations
    private static int fNumQuestionPos = 1;       // number of different question images

    private HashMap<String, Integer> fHashQuestionsPosMap = new HashMap<String, Integer>();
                                          // position map contains a pairing of a question number with a
                                          // position of where located in the maze

    private HashMap<String, ImagePixels> fHashQuestionsPixelMap = new HashMap<String, ImagePixels>();
                                          // pixels for each question image

    /**
     * Constructor - loads the question data from file and all question image pixels.
     * @param platform  The os specific connector object.
     * @param folder The folder where the question data is found.
     */
    public QuestionPosDataApplet(Platform platform, String folder, int mapHeight, int mapWidth, int mapWidthShift, String wallFileName) {
        fPlatform = (PlatformApplet)platform;
        fFolder = folder;
        fMapHeight = mapHeight;
        fMapWidth = mapWidth;
        fMapWidthShift = mapWidthShift;
        fWallFileName = wallFileName;
        fGraphics = fPlatform.getApplet().getGraphics();

        loadQuestionPos();
        getQuestionPixels();
    }

    /**
     * Returns the total number of different question images.
     */
    public int getNumQuestionPosImgs() {
        return fNumQuestionPos;
    }

    /**
     *  Loads the arial view of the question positions from file.
     */
    void loadQuestionPos() {
        String fullQuestionPosName = fPlatform.getResourceDir().getAppropPathAndFile(QUESTION_POS_DATA_FILE);
        System.out.println("fullQuestionPosName: " + fullQuestionPosName);

        try {
            URL namesFile = new URL(fullQuestionPosName);
            InputStream is = namesFile.openStream();
            MyDataInputStream dis = new MyDataInputStream(is);

            // read all lines of the file
            String line = "";
            int lineNum = 0;
            String completeFile = "";
            boolean AllIsWell = true;
            while (true) {
                line = dis.myReadLine();
                if (line == null) break;
                line = FileUtilsApplet.stripOffSlash(line);
                if (0 == line.length()) continue;
                lineNum++;

                if (line.length() != fMapWidth) {
                    TextUtils.drawErrorMessage(fGraphics, "Line # " + lineNum + " in file '" + fullQuestionPosName + "' is inconsistent with ");
                    TextUtils.drawErrorMessage(fGraphics, "the first line of the '" + fWallFileName + "' file.");
                    TextUtils.drawErrorMessage(fGraphics, "Each line in the files must be the exact same length.");
                    AllIsWell = false;
                    break;
                }
                line.toUpperCase();
                completeFile += line;
            }
            dis.close();
            System.out.println("num lines in question pos file is: " + lineNum);
            if (lineNum != fMapHeight) {
                TextUtils.drawErrorMessage(fGraphics, "The number of lines in the file '" + fullQuestionPosName + "' doesn't match the ");
                TextUtils.drawErrorMessage(fGraphics, "number of lines in the file '" + fWallFileName + ".'");
                TextUtils.drawErrorMessage(fGraphics, "The number of lines in the files must be equal.");
                AllIsWell = false;
            }
            if (AllIsWell) {
                fQuestionPosData = completeFile.toCharArray();
                setAllTimeHighImageNum();
                setupPositionList();
            }
        } catch (IOException e) {
            TextUtils.drawErrorMessage(fGraphics, "Unable to read from file '" + fullQuestionPosName + ".'");
            TextUtils.drawErrorMessage(fGraphics, "Make sure the file exists and is present with runtime files.");
        }
    }

    /**
     *  Sets member variable based upon the highest image number encountered in the question pos data.
     */
    protected void setAllTimeHighImageNum() {
        for (int i = 0; i < fQuestionPosData.length; i++) {
            int curVal = MathUtils.base36ToBase10(fQuestionPosData[i]);
            fNumQuestionPos = Math.max(curVal, fNumQuestionPos);
        }
        fPlatform.logInfo(sLogLabel, "number of question images expected is: " + fNumQuestionPos);
    }

    /**
     * Returns true if the specified map item is a question item.
     */
    public boolean isQuestionPosItem(int pos) {
        return (!(fQuestionPosData[pos] == '0'));
    }

    /**
     * Returns the value at the specified position
     */
    public char getValue(int questionPosData) {
        if (questionPosData < 0 || questionPosData >= fMapHeight << fMapWidthShift) return ('0');
        else return (fQuestionPosData[questionPosData]);
    }

    /**
     * Walk through the entire question position array and set up a position list.
     */
    protected void setupPositionList() {
        fHashQuestionsPosMap = new HashMap<String, Integer>();
        for (int i = 0; i < fQuestionPosData.length; i++) {
            if (fQuestionPosData[i] != '0') {   // only add real questions to list
                Integer anInteger = new Integer(i);
                // i is the map index and tells the position in the arial view
                fHashQuestionsPosMap.put("" + fQuestionPosData[i], anInteger);
            }
        }
    }

    /**
     * Returns either a '?', 'A', 'B', 'C', 'D' or '0' telling what the item type is at the given position.
     * @param mapIndex The position we are evaluating in this call.
     * @param //questions Questions and ABCD answers with positions relative to question mark.
     * @param curQuestion Current question, if we are on a question, otherwise zero.
     * @return Returns either a '?', 'A', 'B', 'C', 'D' or '0' telling what the item type is at the given position.
     */
    public char getQuestionItemTypeAt(int mapIndex, Questions questions, char curQuestion) {
        if (getValue(mapIndex) != '0') return '?';
        if ('0' == curQuestion) return '0';

        // If curQuestion is anything other than '0' this denotes we are currently on a question looking for an
        // answer.  We look at all answer positions for the current question and see if they represent the same
        // location as that passed in as mapIndex.  Note:  The ABCD positions are relative positions tracked in the
        // Questions file as relative positions from the question mark.

        Question curQuest = questions.returnQuestion(curQuestion);

        Integer anInteger = (Integer) fHashQuestionsPosMap.get("" + curQuestion);
        int questMapIndex = anInteger.intValue();

        int row = questMapIndex >> fMapWidthShift;       // convert mapindex to a row column
        int col = questMapIndex % fMapWidth;

        int rowA = row + curQuest.getYRelAnswerA();   // height used here for row offset
        int rowB = row + curQuest.getYRelAnswerB();
        int rowC = row + curQuest.getYRelAnswerC();
        int rowD = row + curQuest.getYRelAnswerD();

        int colA = col + curQuest.getXRelAnswerA();
        int colB = col + curQuest.getXRelAnswerB();
        int colC = col + curQuest.getXRelAnswerC();
        int colD = col + curQuest.getXRelAnswerD();

        if (colA > fMapWidth) return '0';
        if (colB > fMapWidth) return '0';
        if (colC > fMapWidth) return '0';
        if (colD > fMapWidth) return '0';

        if (rowA > fMapHeight) return '0';
        if (rowB > fMapHeight) return '0';
        if (rowC > fMapHeight) return '0';
        if (rowD > fMapHeight) return '0';

        int mapPosA = ((rowA << fMapWidthShift) + colA);
        int mapPosB = ((rowB << fMapWidthShift) + colB);
        int mapPosC = ((rowC << fMapWidthShift) + colC);
        int mapPosD = ((rowD << fMapWidthShift) + colD);

        if (mapPosA == mapIndex) return 'A';
        if (mapPosB == mapIndex) return 'B';
        if (mapPosC == mapIndex) return 'C';
        if (mapPosD == mapIndex) return 'D';

        return '0';
    }

    /**
     * Returns either a '?', 'A', 'B', 'C', 'D' or '0' telling what the item type is at the given position.
     * This is a special version of getQuestionItemTypeAt() motivated by the need to drop rendering of the
     * question mark when you have currently activated a question mark and are examining answers.  This
     * method behaves exactly as the original, except with this special version, if the item hit is a
     * question mark, then the question mark is ignored if it is the exact same question we currently activated.
     * @param mapIndex The position we are evaluating in this call.
     * @param //questions Questions and ABCD answers with positions relative to question mark.
     * @param curQuestion Current question, if we are on a question, otherwise zero.
     * @return Returns either a '?', 'A', 'B', 'C', 'D' or '0' telling what the item type is at the given position.
     */
    public char getQuestionItemTypeAtSpecial(int mapIndex, Questions questions, char curQuestion) {
        char questItemTypeHit = getQuestionItemTypeAt(mapIndex, questions, curQuestion);

        // if item hit is an answer image, then there is no special case
        if (questItemTypeHit != '?') return questItemTypeHit;

        // else we are dealing with a question
        char questNumberHit = getValue(mapIndex);
        if (questNumberHit == curQuestion) return '0';  // ignore if it is the current question
        else return '?';
    }

    /**
     * Loads all question images and grabs their pixels and puts them in an accessible HashMap indexed by
     * ?, A, B, C, and D.
     */
    private void getQuestionPixels() {

        for (int i = 0; i < QUESTION_FILE_NAMES.length; i++) {
            ImagePixels pixels = ImagePixelsFactory.createImagePixels(fPlatform, fFolder, QUESTION_FILE_NAMES[i][1]);
            if (null == pixels) {
                TextUtils.drawErrorMessage(fGraphics, "Unable to load '" + QUESTION_FILE_NAMES[i][1] + "'.");
                return;
            }

            if (pixels.getWidth() != MazeGlobals.WALL_HEIGHT) {
                TextUtils.drawErrorMessage(fGraphics, "Question image '" + QUESTION_FILE_NAMES[i][1] + "' must be "
                        + MazeGlobals.WALL_HEIGHT + " pixels wide.");
                return;
            }

            if (pixels.getHeight() != MazeGlobals.WALL_HEIGHT) {
                TextUtils.drawErrorMessage(fGraphics, "Question image '" + QUESTION_FILE_NAMES[i][1] + "' must be "
                        + MazeGlobals.WALL_HEIGHT + " pixels high.");
                return;
            }
            fHashQuestionsPixelMap.put(QUESTION_FILE_NAMES[i][0], pixels);  // store all pixels in table accessible by string
        }
    }

    /**
     * Returns the specified question image pixels base upon question image type:  '?', 'A', 'B', 'C', 'D' or '0'
     * @param type  '?', 'A', 'B', 'C', 'D' or '0'
     */
    public ImagePixels getImagePixelsForQuestionType(String type) {
        return fHashQuestionsPixelMap.get(type);
    }
}
