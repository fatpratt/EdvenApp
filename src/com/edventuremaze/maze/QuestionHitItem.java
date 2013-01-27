package com.edventuremaze.maze;

/**
 * This class represents information for when a ray hits a question item.  Question item hits are treated just like
 * PropItemHits with an additional piece of information describing what type of question was hit.
 *
 * @author brianpratt
 */
public class QuestionHitItem extends PropHitItem {
    private char fQuestionItemType;   // '?', 'A', 'B', 'C', or 'D'

    QuestionHitItem(int mapPos, char questionItemType) {
        super(mapPos);
        fQuestionItemType = questionItemType;
    }

    public char getQuestionItemType() {
        return fQuestionItemType;
    }
}

