package com.edventuremaze.applet;

import com.edventuremaze.maze.GeneralConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Routines used to authenticate applet client with the server making sure the expiration date is valid for current
 * maze.
 *
 * This class is copied nearly in its entirety from the server because there are a number of common routines
 * shared between the client and server (such as weak encryption routines that scramble the data between client
 * and server).  While it is better to maintain a single copy of this code, we cut and paste it
 * from the server to client so as to embed it as a private class within the client applet.
 *
 * @author brian pratt
 */

public final class MAuthAuthClient {
    GeneralConfig genConfig;
    Integer numDaysRemaining = null;
    String strMazeID;
    MAuthAuthShared authShared;

    private static final String AUTH_URL = "http://www.EdventureMaze.com/WebContent/mauthauth";
    private static final String AUTH_URL_SECONDARY = "http://www.HarvestMoon.cc/WebContent/mauthauth";

    public MAuthAuthClient(GeneralConfig genConfig, String strMazeID) {
        this.genConfig = genConfig;
        this.strMazeID = strMazeID;
        this.authShared = new MAuthAuthShared();
        this.authShared.setMazeID(strMazeID);
        this.authShared.setFullMAuthVerNum(GeneralConsts.FULL_MAUTH_VER_NUM);
        this.authShared.setAuthURL(AUTH_URL);
        this.authShared.setAuthSecondaryURL(AUTH_URL_SECONDARY);
    }

    private final boolean isBackDoorGood() {
        // check backdoor
        String strBackDoor = genConfig.getAuth();
        if (strBackDoor == null || strBackDoor.length() != 9) return false;
        if (
                strBackDoor.charAt(3) == strBackDoor.charAt(2)
                        && strBackDoor.charAt(5) == 'c'
                        && strBackDoor.charAt(0) == 'b'
                        && strBackDoor.charAt(6) == 'o'
                        && strBackDoor.charAt(1) == 'r'
                        && strBackDoor.charAt(4) == 's'
                        && strBackDoor.charAt(7) == strBackDoor.charAt(6)
                        && strBackDoor.charAt(8) == 'l'
                        && strBackDoor.charAt(2) == 'i') {

            return true;
        } else return false;
    }

    private final boolean isDisconnectMode() {
        String strBackDoor = genConfig.getAuth();
        if (strBackDoor.equalsIgnoreCase("disconnected")) return true;
        else return false;
    }

    protected Integer getNumDaysRemainingFromServer() {
        int days = 0;
        try {
            String requestParam = this.authShared.createRequestParam();
            String key = this.authShared.getEncryptKeyFromParam(requestParam);
            String requestScrambled = this.authShared.scramble(requestParam);                 // scramble before going over wire

            String serverResponse = null;
            String serverResponseUnscrambled = null;
            String strEncDays = null;

            serverResponse = this.authShared.getResponseFromServer(AUTH_URL, requestScrambled);
            serverResponseUnscrambled = this.authShared.unscramble(serverResponse);
            strEncDays = this.authShared.getDaysFromParam(serverResponseUnscrambled);
            days = this.authShared.decrypt(strEncDays, key);

            if (days == 0) {
                serverResponse = this.authShared.getResponseFromServer(AUTH_URL_SECONDARY, requestScrambled);
                serverResponseUnscrambled = this.authShared.unscramble(serverResponse);
                strEncDays = this.authShared.getDaysFromParam(serverResponseUnscrambled);
                days = this.authShared.decrypt(strEncDays, key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Integer(days);
    }

    protected Integer getNumDaysRemainingInternal() {
        if (numDaysRemaining == null) {
            if (isBackDoorGood())
                numDaysRemaining = new Integer(Integer.MAX_VALUE);
            else if (isDisconnectMode())  // in disconnected mode the maze works, but not reliably (this is intentional)
                numDaysRemaining = (authShared.getRandNumBetween(0, 2) == 1) ? new Integer(Integer.MAX_VALUE) : new Integer(0);
            else
                numDaysRemaining = getNumDaysRemainingFromServer();
        }
        return numDaysRemaining;
    }

    public boolean isAuth() {
        return true;
//    Integer numDaysRemain = getNumDaysRemainingInternal();
//    return (numDaysRemain.intValue() > 0);
    }

    public int getDaysRemaining() {
        return getNumDaysRemainingInternal().intValue();
    }

    /*
    The following is an internal private class that is copied in its entirety from
    the server.  There are a number of common routines such as encryption that are
    shared between the server and this applet client.  It is better to maintain a
    single copy of this code, however, we cut and paste it from the server to client
    so as to embed it as a private class within this class.  This reduces visibility
    on the client so the general public cannot make use of this class and have
    access to our encryption.
    */

    //--------------------------------------------------------- begin private class

    private class MAuthAuthShared {
        private String authURL = "";
        private String authSecondaryURL = "";
        private String fullMAuthVerNum = "";
        private String mazeID = "";

        private final int VER_SIZE = 5;
        private final int DATE_SIZE = 8;

        private final int MAZE_ID_SIZE = 10;

        // 0   1   2   3   4   5   6   7   8   9
        int[] scrambleMappingV13 = {1, 0, 2, 3, 5, 4, 19, 11, 7, 6,
                12, 9, 10, 13, 18, 15, 14, 8, 16, 17,
                42, 23, 30, 20, 37, 27, 25, 34, 41, 36,
                22, 39, 24, 28, 29, 31, 33, 26, 35, 21,
                38, 32, 40};

        public MAuthAuthShared() {
            System.out.println("MAuthAuthShared init");
        }

        protected Integer getNumDaysRemainingFromServer() {
            int days = 0;
            try {
                String requestParam = createRequestParam();
                String key = getEncryptKeyFromParam(requestParam);
                String requestScrambled = scramble(requestParam);                 // scramble before going over wire

                String serverResponse = null;
                String serverResponseUnscrambled = null;
                String strEncDays = null;

                serverResponse = getResponseFromServer(this.authURL, requestScrambled);
                serverResponseUnscrambled = unscramble(serverResponse);
                strEncDays = getDaysFromParam(serverResponseUnscrambled);
                days = decrypt(strEncDays, key);

                if (days == 0) {
                    serverResponse = getResponseFromServer(this.authSecondaryURL, requestScrambled);
                    serverResponseUnscrambled = unscramble(serverResponse);
                    strEncDays = getDaysFromParam(serverResponseUnscrambled);
                    days = decrypt(strEncDays, key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new Integer(days);
        }

        public String getResponseFromServer(String strServletURL, String strParam)
                throws ProtocolException, Exception {
            String tempResponse = "";

            URLConnection urlConn = getHttpConnection(strServletURL + "?id=" + strParam);
            if (urlConn == null) return "";

            // set the appropriate HTTP parameters
            urlConn.setRequestProperty("Content-Type", "text/html; charset=utf-8");
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);

            // post the request
            OutputStream out = urlConn.getOutputStream();
            out.close();

            InputStreamReader isr = new InputStreamReader(urlConn.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String temp;

            // create a string using response
            while ((temp = br.readLine()) != null) {
                tempResponse = tempResponse + temp;
            }
            br.close();
            isr.close();
            return tempResponse;
        }

        protected URLConnection getHttpConnection(String strServletURL) {
            URLConnection connection = null;
            try {
                URL url = new URL(strServletURL);
                connection = url.openConnection();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return connection;
        }

        // returns a five character string representing the version numb with decimals as zeros
        protected String getVersion() {
            StringBuffer buffy = new StringBuffer(this.fullMAuthVerNum);
            StringBuffer buffyOut = new StringBuffer(VER_SIZE);
            for (int i = 0; i < VER_SIZE; i++) {
                if (i < buffy.length()) {
                    try {
                        char ch = buffy.charAt(i);
                        buffyOut.append((Character.isDigit(ch)) ? ch : '0');
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else buffyOut.setCharAt(i, '0');
            }
            return buffyOut.toString();
        }

        // returns an eight digit current date
        protected String getCurDate() {
            SimpleDateFormat simpleForm = new SimpleDateFormat("yyyyMMdd");
            return simpleForm.format(new Date());
        }

        // returns a ten character string of random numbers
        protected String getRandomKey() {
            StringBuffer buffy = new StringBuffer(getKeySize());
            for (int i = 0; i < getKeySize(); i++) {
                int randNum = getRandNumBetween(0, 9);
                String strRandNum = "" + randNum;
                buffy.append(strRandNum.charAt(0));
            }
            return buffy.toString();
        }

        public int getRandNumBetween(int low, int high) {
            if (high <= low) throw new RuntimeException("high must be larger than low");
            int length = (high - low) + 1;
            int numBetween = (int) (Math.floor(Math.random() * length));
            return (low + numBetween);
        }

        public String createRequestParam() {
            String strVer = getVersion();
            String strDate = getCurDate();
            String strRandomKey = getRandomKey();
            String strFiller = getRandomKey();
            return strVer + strDate + strRandomKey + strFiller + this.mazeID;
        }

        public String getDaysFromParam(String strParam) throws Exception {
            if (strParam.length() != VER_SIZE + DATE_SIZE + getKeySize() + getRetSize() + MAZE_ID_SIZE)
                throw new Exception("Invalid response string length.");
            int beginPos = VER_SIZE + DATE_SIZE + getKeySize();
            int endPos = VER_SIZE + DATE_SIZE + getKeySize() + getRetSize();
            return strParam.substring(beginPos, endPos);
        }

        public String getEncryptKeyFromParam(String strParam) throws Exception {
            if (strParam.length() != VER_SIZE + DATE_SIZE + getKeySize() + getRetSize() + MAZE_ID_SIZE)
                throw new Exception("Invalid response string length.");
            int begin = VER_SIZE + DATE_SIZE;
            int end = VER_SIZE + DATE_SIZE + getKeySize();
            return strParam.substring(begin, end);
        }

        public final int decrypt(String strEncryptedDays, String strKey) throws Exception {
            if (strEncryptedDays.length() != getRetSize()) throw new Exception("days return string invalid length");

            int encDays = Integer.parseInt(strEncryptedDays);

            String firstPart = strKey.substring(0, 5);
            int firstPartInt = Integer.parseInt(firstPart);

            String secondPart = strKey.substring(6, 10);
            int secondPartInt = Integer.parseInt(secondPart);

            int six = Integer.parseInt(strKey.substring(6, 7));
            int seven = Integer.parseInt(strKey.substring(7, 8));
            int eight = Integer.parseInt(strKey.substring(8, 9));
            int nine = Integer.parseInt(strKey.substring(9, 10));

            int days = ((((((encDays - nine) - eight) - seven) - six) - secondPartInt) - firstPartInt);
            if (days < 0) throw new Exception("days returned is a negative number");
            if (days > 50000) throw new Exception("days returned in too big");
            return days;
        }

        public String getMazeIDFromParam(String strParam) throws Exception {
            if (strParam.length() != VER_SIZE + DATE_SIZE + getKeySize() + getRetSize() + MAZE_ID_SIZE)
                throw new Exception("Param is invalid length.");
            int begin = VER_SIZE + DATE_SIZE + getKeySize() + getKeySize();
            int end = strParam.length();
            return strParam.substring(begin, end);
        }

        public String getVerFromParam(String strParam) throws Exception {
            if (strParam.length() != VER_SIZE + DATE_SIZE + getKeySize() + getRetSize() + MAZE_ID_SIZE)
                throw new Exception("Param is invalid length.");
            int begin = 0;
            int end = VER_SIZE;
            return strParam.substring(begin, end);
        }

        public String getDateFromParam(String strParam) throws Exception {
            if (strParam.length() != VER_SIZE + DATE_SIZE + getKeySize() + getRetSize() + MAZE_ID_SIZE)
                throw new Exception("Param is invalid length.");
            int begin = VER_SIZE;
            int end = VER_SIZE + DATE_SIZE;
            return strParam.substring(begin, end);
        }

        public final String unscramble(String in) throws Exception {
            StringBuffer inBuffer = new StringBuffer(in);
            if (inBuffer.length() != getExpectedSize()) throw new Exception("unscramble failure");

            StringBuffer outBuffer = new StringBuffer(in);
            if (inBuffer.charAt(3) != '3') throw new Exception("unscramble failure");
            for (int i = 0; i < getExpectedSize(); i++) {
                int index = scrambleMappingV13[i];
                outBuffer.setCharAt(index, inBuffer.charAt(i));
            }
            return outBuffer.toString();
        }

        public final String scramble(String in) throws Exception {
            StringBuffer inBuffer = new StringBuffer(in);
            if (inBuffer.length() != getExpectedSize()) throw new Exception("scramble failure");

            StringBuffer outBuffer = new StringBuffer();
            if (inBuffer.charAt(3) != '3') throw new Exception("scramble failure");
            for (int i = 0; i < getExpectedSize(); i++)
                outBuffer.append(inBuffer.charAt(scrambleMappingV13[i]));
            return outBuffer.toString();
        }

        //------------------- Getters and Setters ----------------------

        public String getAuthSecondaryURL() {
            return authSecondaryURL;
        }

        public void setAuthSecondaryURL(String authSecondaryURL) {
            this.authSecondaryURL = authSecondaryURL;
        }

        public String getAuthURL() {
            return authURL;
        }

        public void setAuthURL(String authURL) {
            this.authURL = authURL;
        }

        public String getFullMAuthVerNum() {
            return fullMAuthVerNum;
        }

        public void setFullMAuthVerNum(String fullMAuthVerNum) {
            this.fullMAuthVerNum = fullMAuthVerNum;
        }

        public String getMazeID() {
            return mazeID;
        }

        public void setMazeID(String mazeID) {
            this.mazeID = mazeID;
        }

        public int getRetSize()  // static methods are not allowed in private classes
        {
            return 10;
        }

        public int getKeySize()  // static methods are not allowed in private classes
        {
            return 10;
        }

        public int getExpectedSize() {
            return VER_SIZE + DATE_SIZE + getKeySize() + getRetSize() + MAZE_ID_SIZE;
        }
    }

    //--------------------------------------------------------- end private class


    //--------------------------- test routines ----------------------------------

    protected static void testClient(String mazeID) {
        try {
            MAuthAuthClient client = new MAuthAuthClient(null, mazeID);
            String requestParam = client.authShared.createRequestParam();
            String key = client.authShared.getEncryptKeyFromParam(requestParam);
            String requestScrambled = client.authShared.scramble(requestParam);                 // scramble before going over wire
            String serverResponse = client.authShared.getResponseFromServer(AUTH_URL, requestScrambled);

            String serverResponseUnscrambled = client.authShared.unscramble(serverResponse);    // server response comes back scrambled
            String strEncDays = client.authShared.getDaysFromParam(serverResponseUnscrambled);
            int days = client.authShared.decrypt(strEncDays, key);

            System.out.println(mazeID + " ---> " + days);
        } catch (Exception e) {
            System.out.println(mazeID + " ---> " + "couldn't get a decent response");
        }
    }

    public static void main(String[] args) { // test routines
        testClient("abcdefghij");
        testClient("1111111111");
        testClient("2222222222");
        testClient("3333333333");
        testClient("4444444444");
        testClient("5555555555");
        testClient("1010101010");
        testClient("1919191919");
        testClient("2020202020");
        testClient("2121212121");
        testClient("3030303030");
        try {
            System.in.read();
        } catch (Exception e) {
            System.out.println("exception -- test failed");
        }
    }
}
