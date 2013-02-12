package com.edventuremaze.applet;

import com.edventuremaze.applet.maze.PlatformApplet;
import com.edventuremaze.factories.MapDataFactory;
import com.edventuremaze.factories.PropDataFactory;
import com.edventuremaze.factories.QuestionPosDataFactory;
import com.edventuremaze.factories.SoundEffectsFactory;
import com.edventuremaze.maze.*;

import javax.swing.JApplet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.MemoryImageSource;
import java.net.URL;

/**
 * Main entry point for the java applet.
 * 
 * This applet may be launched from a web page using parameters as shown in the following example:
 *  
 *  <applet name='MAuthMazeX2' code='MainHere.class' archive='MAuthX2.jar'
 * 		codebase='http://localhost:7770/WebContent/MazeDirX2/' 
 * 		cache_archive='MAuthX2.jar' width=960 height=400>
 * 	<param name="cache_archive" 	value="MAuthX2.jar">
 * 	<param name="id"				value='8xee74gw2a'>
 * 	<param name='primaryDir'		value='8xee74gw2a'>
 * 	<param name='viewSize'			value='X2'>
 * 	<param name="fgTextBoxClr"      value='#191919'>						
 * 	<param name="bgTextBoxClr"      value='#eeeeee'>						
 * 	<param name="bgGraphicBoxClr"   value='#fcfcfc'>						
 * </applet>
 * 
 *
 * To run in a much smaller applet box, use the following parameters:
 *
 *  <applet name='MAuthMazeX2' code='MainHere.class' 
 *          codebase='http://localhost:7770/WebContent/MazeDir/' 
 *          archive='MAuthX2.jar' width=640 height=200 align=top>
 *      <param name="id"           VALUE='8xee74gw2a'>
 *      <param name='primaryDir'   VALUE='8xee74gw2a'>
 *      <param name='viewSize'     VALUE='X1'>
 *  </applet>
 *  
 * @author: brianpratt
 */
public class MainHere extends JApplet implements Runnable, KeyListener, MazeListener {

    private Maze fMaze;					    // this is the game engine
    private ResourceDirectory fResourceDir;
    private String fMazeId;
    private boolean fViewSizeX2;            // when X2 == true graphic files and view are doubled
    private String fFGTextClrStr;
    private String fBGTextClrStr;
    private String fBGGraphicBoxClrStr;

    private Platform fPlatform;
    private SoundEffects fSoundEffects;     // pool of sound effects
    private GeneralConfig fGeneralConfig;
    private TextArea fTextQuestion;

    private Graphics fGraphics;
    private boolean fRunning = false;
    private Thread fThread;

    private boolean fKeyUp = false;
    private boolean fKeyDown = false;
    private boolean fKeyLeft = false;
    private boolean fKeyRight = false;

    private boolean fIsInQuestionMode = false;
    private Integer fNumAutoMoves = 0;              // count of automated moves produced by run forward action

    static final int SLEEP_VALUE = 30;


    /**
     * Called by the browser this method initializes everything.
     */
    public void init() {
        this.setVisible(true);
        setFocusable(true);
        requestFocus();
        addKeyListener(this);

        // setup resource directory info
        URL url = getCodeBase();
        String codeBase = url.toString();
        String primDir = getParameter("primaryDir");
        fResourceDir = new ResourceDirectory(codeBase, primDir);
        fResourceDir.dumpDirInfo();

        fMazeId = getParameter("id");
        String viewSize = getParameter("viewSize");
        if (viewSize == null) viewSize = "";
        fViewSizeX2 = viewSize.equalsIgnoreCase("X2");
        if (fViewSizeX2) MazeGlobals.doubleEverything();
        fFGTextClrStr = getParameter("fgTextBoxClr");
        fBGTextClrStr = getParameter("bgTextBoxClr");
        fBGGraphicBoxClrStr = getParameter("bgGraphicBoxClr");

        fGraphics = this.getGraphics();
        getStarted();
    }

    /**
     * Gets everything started.
     */
    public void getStarted() {
        this.setLayout(null);
        this.setSize(new Dimension(MazeGlobals.PROJECTIONPLANEWIDTH + 320, MazeGlobals.PROJECTIONPLANEHEIGHT));

        // if directory info passed to applet is invalid, show error and quit
        String error = fResourceDir.checkValidDirInfo();
        if (error.length() != 0) {  // if problem with directories, show error message
            TextUtils.drawErrorMessage(fGraphics, error);
            try {
                Thread.sleep(2300);
            }  // time delay
            catch (Exception sleepProblem) {
                System.out.println("Sleep problem-insomnia");
            }
            fGraphics.dispose();
            return;
        }

        // establish all general maze configurations
        fPlatform = (Platform)new PlatformApplet(fResourceDir, this, fViewSizeX2);
        fGeneralConfig = new GeneralConfig(fPlatform, fMazeId, fSoundEffects);

        // load config files
        MapData mapData = MapDataFactory.createMapData(fPlatform, fMazeId);
        PropData propData = PropDataFactory.createPropData(fPlatform, fMazeId, mapData.getMapHeight(), mapData.getMapWidth(),
                mapData.getMapShiftWidth(), mapData.MAP_DATA_FILE);
        fSoundEffects = SoundEffectsFactory.createSoundEffects(fPlatform, fMazeId);
        MazeConfig mazeConfig = new MazeConfig(fPlatform, fMazeId, mapData.getMapHeight(), mapData.getMapWidth(), fSoundEffects);
        Questions questions = new Questions(fPlatform, fMazeId);
        QuestionPosData questionPosData = QuestionPosDataFactory.createQuestionPosData(fPlatform, fMazeId, mapData.getMapHeight(),
                mapData.getMapWidth(), mapData.getMapShiftWidth(), mapData.MAP_DATA_FILE);
        MAuthAuthClient auth = new MAuthAuthClient(fGeneralConfig, fMazeId);

        // add question window
        fTextQuestion = new TextArea("", 0, 0, TextArea.SCROLLBARS_NONE);
        fTextQuestion.setFont(new Font("Arial", Font.PLAIN, 11));
        if (fFGTextClrStr != null) fTextQuestion.setForeground(ColorCreator.createColor(fFGTextClrStr)); // if color param specified, prefer them
        else fTextQuestion.setForeground(ColorCreator.createColor(fGeneralConfig.getTextClrStr()));
        if (fBGTextClrStr != null) fTextQuestion.setBackground(ColorCreator.createColor(fBGTextClrStr));
        else fTextQuestion.setBackground(ColorCreator.createColor(fGeneralConfig.getBackGroundClrStr()));
        fTextQuestion.setBounds(new Rectangle(MazeGlobals.PROJECTIONPLANEWIDTH, 0, 320, 200));
        fTextQuestion.setEditable(false);
        this.add(fTextQuestion, null);

        // show opening sequence
        if (!hasExpired(auth, fGeneralConfig)) return;
        String expireStr = getExpirationString(auth);
        showInstructions(expireStr);
        colorInIllustrationBox();
        showVisitUsAdvertisement();

        // if an error have been displayed, then give the user time to read it
        if (TextUtils.errorsExist()) {
            try {
                Thread.sleep(13000);        // time delay
            }
            catch (Exception sleepProblem) {
                System.out.println("Sleep problem-insomnia");
            }
        }

        // draw user-defined opening credits screen
        OpeningCreditsCanvas openingCredits = new OpeningCreditsCanvas(fPlatform, fGeneralConfig);
        openingCredits.drawOpeningCredits(fGraphics);
        TextUtils.resetOutputPosToTop(); // previous errors got cleared when drawing
        try {
            Thread.sleep(4000);
        }      // time delay if image file is present
        catch (Exception sleepProblem) {
            System.out.println("Sleep problem-insomnia");
        }

        // instantiate the maze and get it up and running
        MazeParams mazeParams = new MazeParams(fPlatform);
        mazeParams.setX2(fViewSizeX2);
        mazeParams.setMapData(mapData);
        mazeParams.setMazeConfig(mazeConfig);
        mazeParams.setPropData(propData);
        mazeParams.setQuestionPosData(questionPosData);
        mazeParams.setQuestions(questions);
        mazeParams.setMazeListener(this);
        fMaze = new Maze(mazeParams);

        fRunning = true;
        fThread = new Thread(this);
        fThread.start();
    }

    /**
     * Continuously update thread.
     */
    public void run() {
        MazeCanvas mazeCanvas = new MazeCanvas();
        while (fRunning) {
            consumeAutoMove();  // if there are banked automoves we apply them per each thread

            if (fKeyLeft) fMaze.rotateLeft();
            else if (fKeyRight) fMaze.rotateRight();

            if (fKeyUp) fMaze.moveForward();
            else if (fKeyDown) fMaze.moveBackward();

            int[] pixels = fMaze.renderOneFrame();
            mazeCanvas.drawFrame(fGraphics, pixels);

            try {
                Thread.sleep(SLEEP_VALUE);
            } catch (Exception sleepProblem) {
                System.out.println("Sleep problem-insomnia");
            }
        }
    }

    /**
     * Checks to see if maze has expired and if so, gives message to user.
     */
    private boolean hasExpired(MAuthAuthClient auth, GeneralConfig genConfig) {
        if (!auth.isAuth()) {
            String strMsg = "\r\n\r\n\r\n The maze has expired:" +
                    "\r\n\r\n     " + genConfig.getTitleLine1() +
                    "\r\n     " + genConfig.getTitleLine2() +
                    "\r\n     " + genConfig.getTitleLine3();
            fTextQuestion.setText(strMsg);
            try {
                Thread.sleep(10000);
            } catch (Exception sleepProblem) {
                System.out.println("Sleep problem-insomnia");
            }
            return false;
        }
        return true;
    }

    /**
     * If the maze is due to expire soon, this method returns an appropriate string.
     */
    private String getExpirationString(MAuthAuthClient auth) {
        int numDays = auth.getDaysRemaining();
        if (numDays <= 20) return "                              (maze expires in " + numDays + " days)\r\n";
        else return "";
    }

    /**
     * Shows the instructions in the text area question box.
     */
    private void showInstructions(String expireStr) {
        fTextQuestion.setText(expireStr +
            "\r\n     Instructions:\r\n\r\n" +
            "    + Give the viewer a minute to download.\r\n\r\n" +
            "    + Once downloaded, click the viewer\r\n" +
            "      to get started.\r\n\r\n" +
            "    + Use the arrow keys on your keyboard to navigate.\r\n\r\n" +
            "    + Look for question marks (?) giving you clues\r\n" +
            "      as to which pathway (A, B, C, or D) is best.");
    }

    /**
     * The bottom right corner might someday be an illustration area to present
     * images relating to the current questions, but for right now, just color
     * in this corner.
     */
    private void colorInIllustrationBox() {
            // fill in the graphic box in the bottom right corner if param present
        if (fBGGraphicBoxClrStr != null && fViewSizeX2) {
            fGraphics.setColor(ColorCreator.createColor(fBGGraphicBoxClrStr));
            fGraphics.drawRect(MazeGlobals.PROJECTIONPLANEWIDTH, MazeGlobals.PROJECTIONPLANEHEIGHT / 2, 320, 200);
            fGraphics.fillRect(MazeGlobals.PROJECTIONPLANEWIDTH, MazeGlobals.PROJECTIONPLANEHEIGHT / 2, 320, 200);
            getContentPane().setBackground(ColorCreator.createColor(fBGGraphicBoxClrStr));
        }
    }

    /**
     * Shows the "Visit Us..." advertisement.
     */
    private void showVisitUsAdvertisement() {
        fGraphics.setColor(Color.BLACK);
        fGraphics.drawRect(0, 0, MazeGlobals.PROJECTIONPLANEWIDTH - 1, MazeGlobals.PROJECTIONPLANEHEIGHT);
        fGraphics.fillRect(0, 0, MazeGlobals.PROJECTIONPLANEWIDTH - 1, MazeGlobals.PROJECTIONPLANEHEIGHT);
        TextUtils.drawShadowString(fGraphics, MazeGlobals.PROJECTIONPLANEWIDTH,
            "Build, Explore, Learn", new Font("TimesRoman", Font.PLAIN, 12), fViewSizeX2 ? 130 : 65, false);
        TextUtils.drawShadowString(fGraphics, MazeGlobals.PROJECTIONPLANEWIDTH,
            "Maze Authoring", new Font("TimesRoman", Font.BOLD, 36), fViewSizeX2 ? 200 : 100, true);
        TextUtils.drawShadowString(fGraphics, MazeGlobals.PROJECTIONPLANEWIDTH,
            "visit us at", new Font("TimesRoman", Font.PLAIN, 12), fViewSizeX2 ? 246 : 123, false);
        TextUtils.drawShadowString(fGraphics, MazeGlobals.PROJECTIONPLANEWIDTH,
            "EdVentureMaze.com", new Font("TimesRoman", Font.PLAIN, 20), fViewSizeX2 ? 300 : 150, true);
        try {  // time delay
            Thread.sleep(2900);
        }
        catch (Exception sleepProblem) {
            System.out.println("Sleep problem-insomnia");
        }
    }

    /**
     * Required for KeyListener.
     */
    public void keyTyped(KeyEvent e) {
        return;
    }

    /**
     * Respond to key pressed.
     */
    public void keyPressed(KeyEvent e) {
        fNumAutoMoves = new Integer(0);     // clear auto move queue
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                if (fIsInQuestionMode) {
                    String questionText = fMaze.getCurQuestionText();
                    fTextQuestion.setText(questionText + "\r\n\r\n" + "(Choose pathway A and see if your answer is correct.)");
                }
                break;
            case KeyEvent.VK_B:
                if (fIsInQuestionMode) {
                    String questionText = fMaze.getCurQuestionText();
                    fTextQuestion.setText(questionText + "\r\n\r\n" + "(Choose pathway B and see if your answer is correct.)");
                }
                break;
            case KeyEvent.VK_C:
                if (fIsInQuestionMode) {
                    String questionText = fMaze.getCurQuestionText();
                    fTextQuestion.setText(questionText + "\r\n\r\n" + "(Choose pathway C and see if your answer is correct.)");
                }
                break;
            case KeyEvent.VK_D:
                if (fIsInQuestionMode) {
                    String questionText = fMaze.getCurQuestionText();
                    fTextQuestion.setText(questionText + "\r\n\r\n" + "(Choose pathway D and see if your answer is correct.)");
                }
                break;
            case KeyEvent.VK_R:
                fNumAutoMoves = new Integer(19);
                break;
            case KeyEvent.VK_UP:
                fMaze.moveForward();
                fKeyUp = true;
                break;
            case KeyEvent.VK_DOWN:
                fMaze.moveBackward();
                fKeyDown = true;
                break;
            case KeyEvent.VK_LEFT:
                fMaze.rotateLeft();
                fKeyLeft = true;
                break;
            case KeyEvent.VK_RIGHT:
                fMaze.rotateRight();
                fKeyRight = true;
                break;
            default:
        }
    }

    /**
     * Respond to key release.
     */
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                fKeyUp = false;
                break;
            case KeyEvent.VK_DOWN:
                fKeyDown = false;
                break;
            case KeyEvent.VK_LEFT:
                fKeyLeft = false;
                break;
            case KeyEvent.VK_RIGHT:
                fKeyRight = false;
                break;
            default:
        }
    }

    /**
     * Called by game thread to use one of the banked auto moves and move the player forward.
     */
    public void consumeAutoMove() {
        if (fNumAutoMoves > 0) {
            // there is a gradation in momentum as the player losses steam
            if (fNumAutoMoves == 1)
                fMaze.moveForward(MazeGlobals.QTR_PLAYER_SPEED);
            else if (fNumAutoMoves == 3)
                fMaze.moveForward(MazeGlobals.HALF_PLAYER_SPEED);
            else if (fNumAutoMoves <= 7)
                fMaze.moveForward(MazeGlobals.PLAYER_SPEED);
            else if (fNumAutoMoves <= 12)
                fMaze.moveForward(MazeGlobals.QTR_PLAYER_SPEED + MazeGlobals.PLAYER_SPEED);
            else
                fMaze.moveForward(MazeGlobals.HALF_PLAYER_SPEED + MazeGlobals.PLAYER_SPEED);

            fNumAutoMoves = new Integer(--fNumAutoMoves);
            if (fNumAutoMoves <= 0) fMaze.setInAutoMoveMode(false);
        } else fMaze.setInAutoMoveMode(false);
    }

    /**
     * Event coming from the maze to report that we are in a question mode or that
     * we just ended question mode.  Here we show the appropriate question when
     * in question mode and we erase it when we leave the mode.
     */
    public void questionModeChanged(boolean isInQuestionMode) {
        String questionText = fMaze.getCurQuestionText();
        fTextQuestion.setText(questionText);
        fIsInQuestionMode = isInQuestionMode;
    }

    public void questionPopupModeChanged(boolean isInQuestionPopupMode) {
        // not used in this implementation of maze listener
    }

    public void autoMoveModeChanged(boolean isInAutoMoveMode) {
        // this event is ignored
    }

    /**
     * Private inner class used for drawing the actual maze.
     */
    private class MazeCanvas extends Canvas {
        public void drawFrame(Graphics graphics, int[] pixels) {
            MemoryImageSource memSource = new MemoryImageSource(MazeGlobals.PROJECTIONPLANEWIDTH, MazeGlobals.PROJECTIONPLANEHEIGHT, pixels, 0, MazeGlobals.PROJECTIONPLANEWIDTH);
            memSource.setAnimated(true);
            Image memImage = createImage(memSource);
            graphics.drawImage(memImage, 0, 0, this);
        }
    }
}
