package com.edventuremaze.applet.utils;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class used for reading data stream line by line.
 *
 * @author brianpratt
 */
public class MyDataInputStream extends DataInputStream {
    public MyDataInputStream(InputStream is) {
        super(is);
    }

    // DataInputStream.readLine is depricated so here is a replacement
    public String myReadLine() throws IOException {
        String strLine = "";
        try {
            while (true) {
                char ch = (char) readByte();
                if (ch == '\r') continue;
                if (ch == '\n') return strLine;
                else strLine = strLine + ch;
            }
        } catch (EOFException e) {
            if (strLine.length() <= 0)
                strLine = null;
        }
        return strLine;
    }
}
