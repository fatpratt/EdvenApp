package com.edventuremaze.maze;

import com.edventuremaze.factories.IniFileFactory;
import com.edventuremaze.utils.IniFile;

import java.util.Hashtable;

/**
 * Holds all questions from the Question.ini file.
 *
 * @author brianpratt
 */
public class Questions {
    static final String sLogLabel = "--->Questions:";
    String QUESTIONS_INI_FILE = "Questions.ini";

    IniFile fQuestionINIFile;
    int fNumQuestions;

    public Hashtable fHashQuestions;  // contains all questions--notice all questions
                                      // are accessible by their id and not by order of file

    /**
     * Constructor - loads the questions.ini file.
     * @param platform  The os specific connector object.
     * @param mazeId Id of the maze.
     */
    public Questions(Platform platform, String mazeId) {
        fQuestionINIFile = IniFileFactory.createIniFileObj(platform, mazeId, QUESTIONS_INI_FILE);
        fHashQuestions = new Hashtable();

        String ver = fQuestionINIFile.getValue("General", "Ver", "1.3");
        if (!ver.equals("1.3")) {
            platform.logError(sLogLabel, "Questions.ini file may be out of date. Expected");
            platform.logError(sLogLabel, "version ('Ver') is 1.3 but version found is " + ver + ".");
        }

        fNumQuestions = fQuestionINIFile.getIntValue("General", "NumQuestions", 0);

        for (int i = 0; i < fNumQuestions; i++) {
            Question question = getQuestion(i);
            fHashQuestions.put(question.getId(), question);    // make questions accessible by id
        }
    }

    /**
     * Reads the specified question information from the appropriate section of the ini file
     */
    private Question getQuestion(int num) {
        Question question = new Question();
        String strSection = "Question" + num;

        question.setQuestionNum(num);
        question.setTotalQuestions(fNumQuestions);

        question.setId(fQuestionINIFile.getValue(strSection, "ID", "0"));
        question.setQuestion(fQuestionINIFile.getValue(strSection, "Question", ""));

        question.setAnswerA(fQuestionINIFile.getValue(strSection, "AnswerA", ""));
        question.setAnswerB(fQuestionINIFile.getValue(strSection, "AnswerB", ""));
        question.setAnswerC(fQuestionINIFile.getValue(strSection, "AnswerC", ""));
        question.setAnswerD(fQuestionINIFile.getValue(strSection, "AnswerD", ""));

        int xRelAnswerA = fQuestionINIFile.getIntValue(strSection, "XRelAnswerA", 0);
        int yRelAnswerA = fQuestionINIFile.getIntValue(strSection, "YRelAnswerA", 0);
        int xRelAnswerB = fQuestionINIFile.getIntValue(strSection, "XRelAnswerB", 0);
        int yRelAnswerB = fQuestionINIFile.getIntValue(strSection, "YRelAnswerB", 0);
        int xRelAnswerC = fQuestionINIFile.getIntValue(strSection, "XRelAnswerC", 0);
        int yRelAnswerC = fQuestionINIFile.getIntValue(strSection, "YRelAnswerC", 0);
        int xRelAnswerD = fQuestionINIFile.getIntValue(strSection, "XRelAnswerD", 0);
        int yRelAnswerD = fQuestionINIFile.getIntValue(strSection, "YRelAnswerD", 0);

        question.setXRelAnswerA(xRelAnswerA);
        question.setXRelAnswerB(xRelAnswerB);
        question.setXRelAnswerC(xRelAnswerC);
        question.setXRelAnswerD(xRelAnswerD);

        question.setYRelAnswerA(yRelAnswerA);
        question.setYRelAnswerB(yRelAnswerB);
        question.setYRelAnswerC(yRelAnswerC);
        question.setYRelAnswerD(yRelAnswerD);

        return question;
    }

    /**
     * Public method which returns the specified question.
     * @param questionNum The desired question.
     * @return Returns the requested question.
     */
    public Question returnQuestion(char questionNum) {
        return ((Question) fHashQuestions.get("" + questionNum));
    }

}
