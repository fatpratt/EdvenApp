package com.edventuremaze.maze;

/**
 * This class is the Android specific implementation of Question Pos Data which contains the arial view of the question
 * position data and all questions image pixels.
 *
 * @author brianpratt
 */
public interface QuestionPosData  {

    static public final String QUESTION_POS_DATA_FILE = "QuestionPosData.txt";

    static public String[][] QUESTION_FILE_NAMES =
            {
                    {"?", "QuestionMark.gif"},
                    {"A", "AnswerA.gif"},
                    {"B", "AnswerB.gif"},
                    {"C", "AnswerC.gif"},
                    {"D", "AnswerD.gif"}
            };

    // Returns the total number of different question images.
    public int getNumQuestionPosImgs();

    // Returns true if the specified map item is a question item.
    public boolean isQuestionPosItem(int pos);

    // Returns the value at the specified position.
    public char getValue(int questionPosData);

    // Returns either a '?', 'A', 'B', 'C', 'D' or '0' telling what the item type is at the given position.
    public char getQuestionItemTypeAt(int mapIndex, Questions questions, char curQuestion);

    // Returns either a '?', 'A', 'B', 'C', 'D' or '0' telling what the item type is at the given position.
    // This method behaves exactly as the original, except with this special version, if the item hit is a
    // question mark, then the question mark is ignored if it is the exact same question we currently activated.
    public char getQuestionItemTypeAtSpecial(int mapIndex, Questions questions, char curQuestion);

     // Returns the specified question image pixels base upon question image type:  '?', 'A', 'B', 'C', 'D' or '0'
    public ImagePixels getImagePixelsForQuestionType(String type);

}
