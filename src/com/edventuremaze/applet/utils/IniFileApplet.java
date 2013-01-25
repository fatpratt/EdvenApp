package com.edventuremaze.applet.utils;

import com.edventuremaze.applet.TextUtils;
import com.edventuremaze.applet.maze.PlatformApplet;
import com.edventuremaze.utils.IniFile;
import com.edventuremaze.maze.*;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Vector;

/*
 * IniFile.java
 * A class for handling Windows-style INI files.
 *
 * Copyright Steve DeGroof, degroof@mindspring.com,
 * http://www.mindspring.com/~degroof
 *
 * The source code provided here should be considered example
 * code. That is, you can use or modify it without permission. On the
 * other hand, you're using the code at your own risk.
 */

/**
 * Hacked up by Brian Pratt, this version is the Android specific implementation.
 */
public class IniFileApplet implements IniFile {
    static final String sLogLabel = "--->IniFileAnd:";

    protected Vector fLines;        // Actual text lines of the file stored in a vector.
    protected Vector fSubjects;     // A vector of all subjects
    protected Vector fVariables;    // A vector of variable name vectors grouped by subject
    protected Vector fValues;       // A vector of variable value vectors grouped by subject

    protected PlatformApplet fPlatform;
    protected String fFolder;
    protected String fIniFileName;
    protected Graphics fGraphics;

    public IniFileApplet(Platform platform, String folder, String iniFileName) {
        fPlatform = (PlatformApplet)platform;
        fFolder = folder;
        fIniFileName = iniFileName;
        fGraphics = fPlatform.getApplet().getGraphics();

        loadFile();
        parseLines();
    }

    public String[] getVariables(String subject) {
        String[] v;
        int index = fSubjects.indexOf(subject.toLowerCase());
        if (index != -1) {
            Vector vars = (Vector) (fVariables.elementAt(index));
            v = new String[vars.size()];
            vars.copyInto(v);
            return v;
        } else {
            v = new String[0];
            return v;
        }
    }

    public String[] getSubjects() {
        String[] s = new String[fSubjects.size()];
        fSubjects.copyInto(s);
        return s;
    }

    public String getValue(String subject, String variable, String def) {
        subject = subject.toLowerCase();
        int subjectIndex = fSubjects.indexOf(subject);
        if (subjectIndex == -1) {
            return def;
        }
        variable = variable.toLowerCase();
        Vector valVector = (Vector) (fValues.elementAt(subjectIndex));
        Vector varVector = (Vector) (fVariables.elementAt(subjectIndex));
        int valueIndex = varVector.indexOf(variable);
        if (valueIndex != -1) {
            return (String) (valVector.elementAt(valueIndex));
        }
        return def;
    }

    public int getIntValue(String subject, String variable, int def) {
        final String sdef = "Default\u2026";
        String value = getValue(subject, variable, sdef);
        int intValue = def;
        if (!value.equals(sdef)) {
            try {
                int pos = value.indexOf(';');
                // Windows will parse numbers with comments after them
                if (pos > 0) {
                    value = value.substring(0, pos).trim();
                }
                intValue = Integer.parseInt(value);
            } catch (NumberFormatException e) {
            }
        }
        return intValue;
    }

    public boolean getBoolValue(String subject, String variable, boolean def) {
        final String sdef = def ? "true" : "false";
        String value = getValue(subject, variable, sdef);
        if (value.equalsIgnoreCase("true")) return true;
        else return false;
    }

    /**
     * Loads and parses the INI file. Can be used to reload from file.
     */
    public void loadFile() {
        fLines = new Vector();
        fSubjects = new Vector();
        fVariables = new Vector();
        fValues = new Vector();
        String fullIniName = "";

        //open the file
        try {
            fullIniName = fPlatform.getResourceDir().getAppropPathAndFile(fIniFileName);
            System.out.println("fullIniName: " + fullIniName);
            URL namesFile = new URL(fullIniName);
            InputStream is = namesFile.openStream();
            MyDataInputStream dis = new MyDataInputStream(is);

            String line = "";
            //read all the lines in
            while (true) {
                line = dis.myReadLine();
                if (line == null) break;
                line = FileUtilsApplet.stripOffSlash(line);
                fLines.addElement(line.trim());
            }
            dis.close();
        } catch (IOException e) {
            TextUtils.drawErrorMessage(fGraphics, "Unable to read from file '" + fullIniName + ".'");
            TextUtils.drawErrorMessage(fGraphics, "Make sure the file exists and is present with runtime files.");
        }
    }

    protected boolean isASubject(String line) {
        return (line.startsWith("[") && line.endsWith("]"));
    }

    protected boolean isAnAssignment(String line) {
        if ((line.indexOf("=") != -1) && (!line.startsWith(";"))) {
            return true;
        } else {
            return false;
        }
    }

    protected void parseLines() {
        String currentLine = null;
        //current line being parsed
        String currentSubject = null;
        //the last subject found
        for (int i = 0; i < fLines.size(); i++) {
            //parse all lines
            currentLine = (String) fLines.elementAt(i);
            if (isASubject(currentLine)) {
                //if line is a subject, set currentSubject
                currentSubject = currentLine.substring(1, currentLine.length() - 1);
            } else if (isAnAssignment(currentLine)) {
                //if line is an assignment, add it
                String assignment = currentLine;
                addAssignment(currentSubject, assignment);
            }
        }
    }

    protected int findAssignmentLine(String subject, String variable) {
        int start = findSubjectLine(subject);
        int end = endOfSubject(start);
        return findAssignmentBetween(variable, start, end);
    }

    protected int findAssignmentBetween(String variable, int start, int end) {
        for (int i = start; i < end; i++) {
            if (((String) fLines.elementAt(i)).startsWith(variable + "=")) {
                return i;
            }
        }
        return -1;
    }

    protected void addSubjectLine(String subject) {
        fLines.addElement("[" + subject + "]");
    }

    protected int findSubjectLine(String subject) {
        String line;
        String formattedSubject = "[" + subject + "]";
        for (int i = 0; i < fLines.size(); i++) {
            line = (String) fLines.elementAt(i);
            if (formattedSubject.equals(line)) {
                return i;
            }
        }
        return -1;
    }

    protected int endOfSubject(int start) {
        int endIndex = start + 1;
        if (start >= fLines.size()) {
            return fLines.size();
        }
        for (int i = start + 1; i < fLines.size(); i++) {
            if (isAnAssignment((String) fLines.elementAt(i))) {
                endIndex = i + 1;
            }
            if (isASubject((String) fLines.elementAt(i))) {
                return endIndex;
            }
        }
        return endIndex;
    }

    /**
     * Adds and assignment (i.e. "variable=value") to a subject.
     *
     * @param subject    The feature to be added to the Assignment attribute
     * @param assignment The feature to be added to the Assignment attribute
     * @return Description of the Returned Value
     */
    protected boolean addAssignment(String subject, String assignment) {
        String value;
        String variable;
        int index = assignment.indexOf("=");
        variable = assignment.substring(0, index);
        value = assignment.substring(index + 1, assignment.length());
        if ((value.length() == 0) || (variable.length() == 0)) {
            return false;
        } else {
            return addValue(subject, variable, value, false);
        }
    }

    /**
     * Sets a specific subject/variable combination the given value. If the
     * subject doesn't exist, create it. If the variable doesn't exist, create
     * it.
     *
     * @param subject    the subject heading (e.g. "Widget Settings")
     * @param variable   the variable name (e.g. "Color")
     * @param value      the value of the variable (e.g. "green")
     * @param addToLines add the information to the lines vector
     * @return true if successful
     */
    protected boolean addValue(String subject, String variable, String value, boolean addToLines) {
        //if no subject, quit
        if ((subject == null) || (subject.length() == 0)) {
            return false;
        }

        //if no variable, quit
        if ((variable == null) || (variable.length() == 0)) {
            return false;
        }

        subject = subject.toLowerCase().trim();
        variable = variable.toLowerCase().trim();
        value = value.trim();
        //if the subject doesn't exist, add it to the end
        if (!fSubjects.contains(subject)) {
            fSubjects.addElement(subject);
            fVariables.addElement(new Vector());
            fValues.addElement(new Vector());
        }

        //set the value, if the variable doesn't exist, add it to the end of the subject
        int subjectIndex = fSubjects.indexOf(subject);
        Vector subjectVariables = (Vector) (fVariables.elementAt(subjectIndex));
        Vector subjectValues = (Vector) (fValues.elementAt(subjectIndex));
        if (!subjectVariables.contains(variable)) {
            subjectVariables.addElement(variable);
            subjectValues.addElement(value);
        }
        int variableIndex = subjectVariables.indexOf(variable);
        subjectValues.setElementAt(value, variableIndex);

        //add it to the lines vector?
        if (addToLines) {
            setLine(subject, variable, value);
        }

        return true;
    }

    /**
     * set a line in the lines vector
     *
     * @param subject  the subject heading (e.g. "Widget Settings")
     * @param variable the variable name (e.g. "Color")
     * @param value    the value of the variable (e.g. "green")
     */
    protected void setLine(String subject, String variable, String value) {
        //find the line containing the subject
        int subjectLine = findSubjectLine(subject);
        if (subjectLine == -1) {
            addSubjectLine(subject);
            subjectLine = fLines.size() - 1;
        }
        //find the last line of the subject
        int endOfSubject = endOfSubject(subjectLine);
        //find the assignment within the subject
        int lineNumber = findAssignmentBetween(variable, subjectLine, endOfSubject);

        //if an assignment line doesn't exist, insert one, else change the existing one
        if (lineNumber == -1) {
            fLines.insertElementAt(variable + "=" + value, endOfSubject);
        } else {
            fLines.setElementAt(variable + "=" + value, lineNumber);
        }
    }
}
