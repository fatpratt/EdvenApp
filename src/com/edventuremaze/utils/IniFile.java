package com.edventuremaze.utils;

/**
 *  A class for handling Windows-style INI files.
 *
 *  @author brianpratt
 */
public interface IniFile {
    public String[] getVariables(String subject);
    public String[] getSubjects();
    public String getValue(String subject, String variable, String def);
    public int getIntValue(String subject, String variable, int def);
    public boolean getBoolValue(String subject, String variable, boolean def);
    public void loadFile();
}
