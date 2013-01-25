package com.edventuremaze.maze;

/**
 * Question POJO with fields for the actual question and A, B, C and D answers with relative
 * positions for where the A, B, C and D images are placed relative to the question mark.
 *
 * @author brianpratt
 */
public class Question {
    private String fId;             // example: 1, 2, 3, ... 8, 9, a, b,  c,  d, etc
    private int fQuestionNum;       // example: 0, 1, 2, ... 7, 8, 9, 10, 11, 23, etc

    private int fTotalQuestions;

    private String fQuestion;
    private String fAnswerA;
    private String fAnswerB;
    private String fAnswerC;
    private String fAnswerD;

    private int fXRelAnswerA;
    private int fXRelAnswerB;
    private int fXRelAnswerC;
    private int fXRelAnswerD;

    private int fYRelAnswerA;
    private int fYRelAnswerB;
    private int fYRelAnswerC;
    private int fYRelAnswerD;

    public String getId() {
        return fId;
    }

    public void setId(String fId) {
        this.fId = fId;
    }

    public int getQuestionNum() {
        return fQuestionNum;
    }

    public void setQuestionNum(int fQuestionNum) {
        this.fQuestionNum = fQuestionNum;
    }

    public int getTotalQuestions() {
        return fTotalQuestions;
    }

    public void setTotalQuestions(int fTotalQuestions) {
        this.fTotalQuestions = fTotalQuestions;
    }

    public String getQuestion() {
        return fQuestion;
    }

    public void setQuestion(String fQuestion) {
        this.fQuestion = fQuestion;
    }

    public String getAnswerA() {
        return fAnswerA;
    }

    public void setAnswerA(String fAnswerA) {
        this.fAnswerA = fAnswerA;
    }

    public String getAnswerB() {
        return fAnswerB;
    }

    public void setAnswerB(String fAnswerB) {
        this.fAnswerB = fAnswerB;
    }

    public String getAnswerC() {
        return fAnswerC;
    }

    public void setAnswerC(String fAnswerC) {
        this.fAnswerC = fAnswerC;
    }

    public String getAnswerD() {
        return fAnswerD;
    }

    public void setAnswerD(String fAnswerD) {
        this.fAnswerD = fAnswerD;
    }

    public int getXRelAnswerA() {
        return fXRelAnswerA;
    }

    public void setXRelAnswerA(int fXRelAnswerA) {
        this.fXRelAnswerA = fXRelAnswerA;
    }

    public int getXRelAnswerB() {
        return fXRelAnswerB;
    }

    public void setXRelAnswerB(int fXRelAnswerB) {
        this.fXRelAnswerB = fXRelAnswerB;
    }

    public int getXRelAnswerC() {
        return fXRelAnswerC;
    }

    public void setXRelAnswerC(int fXRelAnswerC) {
        this.fXRelAnswerC = fXRelAnswerC;
    }

    public int getXRelAnswerD() {
        return fXRelAnswerD;
    }

    public void setXRelAnswerD(int fXRelAnswerD) {
        this.fXRelAnswerD = fXRelAnswerD;
    }

    public int getYRelAnswerA() {
        return fYRelAnswerA;
    }

    public void setYRelAnswerA(int fYRelAnswerA) {
        this.fYRelAnswerA = fYRelAnswerA;
    }

    public int getYRelAnswerB() {
        return fYRelAnswerB;
    }

    public void setYRelAnswerB(int fYRelAnswerB) {
        this.fYRelAnswerB = fYRelAnswerB;
    }

    public int getYRelAnswerC() {
        return fYRelAnswerC;
    }

    public void setYRelAnswerC(int fYRelAnswerC) {
        this.fYRelAnswerC = fYRelAnswerC;
    }

    public int getYRelAnswerD() {
        return fYRelAnswerD;
    }

    public void setYRelAnswerD(int fYRelAnswerD) {
        this.fYRelAnswerD = fYRelAnswerD;
    }

    public String getQuestionText() {
        String questNumStr = "" + (fQuestionNum + 1);
        return ("\r\n(Question: " + questNumStr + " of " + fTotalQuestions +") " + fQuestion + "\r\n\r\n"
                + " A. " + fAnswerA + "\r\n\r\n"
                + " B. " + fAnswerB + "\r\n\r\n"
                + " C. " + fAnswerC + "\r\n\r\n"
                + " D. " + fAnswerD);
    }
}
