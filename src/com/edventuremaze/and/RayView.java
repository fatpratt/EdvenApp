package com.edventuremaze.and;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.*;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import com.edventuremaze.factories.MapDataFactory;
import com.edventuremaze.factories.PropDataFactory;
import com.edventuremaze.factories.QuestionPosDataFactory;
import com.edventuremaze.factories.SoundEffectsFactory;
import com.edventuremaze.and.maze.PlatformAnd;
import com.edventuremaze.maze.*;

/**
 * This class defines the view.  Here the game engine is established,
 * the game thread is engaged and user input is monitored.
 *
 * When using this calls call the constructor, then call initView().
 *
 * @author brianpratt
 */
public class RayView extends SurfaceView implements SurfaceHolder.Callback, MazeListener, PopupAnimTrackerListener, SensorEventListener {
    final static String sLogLabel = "--->RayView:";
    final static long INSTRUCTIONS_DURATION = 5000l;
    final static long OPENING_CREDITS_DURATION = 5000l;
    final static long OVERLAY_INST_DURATION = 5000l;

	private Maze fMaze;					    // this is the game engine
	private Context fContext;				// the android context
    private GeneralConfig fGeneralConfig;   // general config file
    private Activity fCurActivity;	        // the current activity
    private SurfaceHolder fHolder;          // houses info about the screen
	private PaintThread fThread;			// game loop thread
    private GestureDetector fGestures;
    private String fMazeId;                 // id of maze passed into constructor
    private boolean fX2;                    // true - two times bigger images
    private Platform fPlatform;
    private SoundEffects fSoundEffects;     // pool of sound effects
    private TextAreaBox fTextArea;             // text area to display question
    private Display fDisplay;               // display coordinates

    private Integer fNumAutoMoves = 0;              // count of automated moves produced by fling gesture and consumed by paintThread
    private OverlayGraphics fOverlayGraphics;       // collection of game buttons and graphics

    private long fInstructionsBeginTime = 0l;       // zero implies not in instructions mode, non-zero is beginning time
    private Instructions fInstructions = null;      // object lazy instantiated and used to draw the instructions
    private long fOpeningCreditsBeginTime = 0l;     // zero implies not in opening credits mode, non-zero is beginning time
    private OpeningCredits fOpeningCredits = null;  // object lazy instantiated and used to draw the opening credits

    private boolean fInOverlayInstMode = false;     // true means currently in overlay instruction mode showing the overlay inst graphic temporarily
    private long fOverlayInstBeginTime = 0l;        // zero implies not yet in overlay instruction mode, non-zero is beginning time

    private boolean fTiltMode = false;              // true means accelerometer tilt interface is on
    private boolean fInOverlayTiltMode = false;     // true means currently in overlay instruction mode showing the overlay inst graphic temporarily
    private long fOverlayTiltBeginTime = 0l;        // zero implies not yet in overlay instruction mode, non-zero is beginning time
    private boolean fSavedTiltMode = false;         // used to restore tilt mode after question popup question mode

    private boolean fIsInQuestionPopupMode = false; // true mean we are in question popup mode

    private float fSectionOne;                        // screen sections used to determine action on double click
    private float fSectionTwo;
    private float fSectionThree;
    private float fSectionFour;
    private float fSectionFive;

    private boolean fIsTabletOrientation = false;
    int fSensorChangedCnt = 0;                                   // these sensor values are used to throttle eager accelerometers
    long fSensorBeginTime = 0l;
    int fSensorThrowAwayRate = 0;
    private static final int SENSOR_TARGET_READ_PER_SEC = 10;   // how many accelerometer reads per second is most desirable

    private PopupAnimTracker fPopupAnimTracker = new PopupAnimTracker(0, 0, 0, null);

	/**
	 * Constructor to initialize the view.
	 * @param context The android activity
	 */
	public RayView(Context context, Activity curActivity, String mazeId, boolean x2) {
		super(context);
        fMazeId = mazeId;
		fContext = context;
        fCurActivity = curActivity;
        fX2 = x2;

        setKeepScreenOn(true);

        // init media player for background music
        MediaPlayer mediaPlayer = StartActivity.getMediaPlayer();
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        float musicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mediaPlayer.setVolume(musicVolume, musicVolume);
        mediaPlayer.setLooping(true);

		// initialize our screen holder
		fHolder = getHolder();
		fHolder.addCallback(this);

        SensorManager sensorManager = (SensorManager) fCurActivity.getSystemService(Activity.SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        fIsTabletOrientation = needToRemapOrientationMatrix();
        Log.d(sLogLabel, "---->" + " orientation matrix type: " + (fIsTabletOrientation ? "tablet orientation" : "phone orientation"));
    }

    /**
     * Returns true if we need to remap orientation matrix for sensors.
     * @return Returns true if the we are using a tablet orientation (landscape by default) as opposed to a typical
     * phone orientation (portrait by default).
     */
    public boolean needToRemapOrientationMatrix() {
        Display display = ((WindowManager) fContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int orientation;
        if (display.getWidth() < display.getHeight()) orientation = Configuration.ORIENTATION_PORTRAIT;
        else if (display.getWidth() > display.getHeight()) orientation = Configuration.ORIENTATION_LANDSCAPE;
        else orientation = Configuration.ORIENTATION_SQUARE;
        int rotation = display.getOrientation();
        return (orientation == Configuration.ORIENTATION_LANDSCAPE && (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)) ||
            (orientation == Configuration.ORIENTATION_PORTRAIT && (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270));
    }

    /**
	 * Initialize and establish the game engine, and the game loop.
	 */
	public void initView() {
        // if we have been passed in a parameter to double the size of everything, we do it here
        if (fX2) {
            MazeGlobals.doubleEverything();
        }

        // establish all maze configurations/params and instantiate the maze
        fPlatform = (Platform)new PlatformAnd(fContext, fCurActivity, fX2);
        fSoundEffects = SoundEffectsFactory.createSoundEffects(fPlatform, fMazeId);
        fGeneralConfig = new GeneralConfig(fPlatform, fMazeId, fSoundEffects);
        if (fGeneralConfig.getUsingBackgroundMusic() && !StartActivity.getMediaPlayer().isPlaying())
            StartActivity.getMediaPlayer().start();    // start playing background music
        fDisplay = ((Activity)fContext).getWindowManager().getDefaultDisplay();
        Log.d(sLogLabel, "---->" + " display width: " + fDisplay.getWidth() + "  display height: " + fDisplay.getHeight());

        // establish five screen sections
        fSectionOne = fDisplay.getWidth() / 5;
        fSectionTwo = fSectionOne * 2;
        fSectionThree = fSectionOne * 3;
        fSectionFour = fSectionOne * 4;
        fSectionFive = fDisplay.getWidth();

        // establish the game buttons/graphics and set up the text area
        fOverlayGraphics = new OverlayGraphics(fContext);
        OverlayGraphic closeButton = fOverlayGraphics.getGraphic(OverlayGraphics.OVR_BTN_CLOSE);
        OverlayGraphic questionLabel = fOverlayGraphics.getGraphic(OverlayGraphics.OVR_IMG_QUESTION);
        fTextArea = new TextAreaBox(fPlatform, fGeneralConfig.getBackGroundClrStr(), fGeneralConfig.getTextClrStr(),
            fDisplay.getWidth(), fDisplay.getHeight(), closeButton, questionLabel);

        MapData mapData = MapDataFactory.createMapData(fPlatform, fMazeId);
        PropData propData = PropDataFactory.createPropData(fPlatform, fMazeId, mapData.getMapHeight(), mapData.getMapWidth(),
                mapData.getMapShiftWidth(), mapData.MAP_DATA_FILE);
        MazeConfig mazeConfig = new MazeConfig(fPlatform, fMazeId, mapData.getMapHeight(), mapData.getMapWidth(), fSoundEffects);
        Questions questions = new Questions(fPlatform, fMazeId);
        QuestionPosData questionPosData = QuestionPosDataFactory.createQuestionPosData(fPlatform, fMazeId, mapData.getMapHeight(),
                mapData.getMapWidth(), mapData.getMapShiftWidth(), mapData.MAP_DATA_FILE);

        MazeParams mazeParams = new MazeParams(fPlatform);
        mazeParams.setX2(fX2);
        mazeParams.setMapData(mapData);
        mazeParams.setMazeConfig(mazeConfig);
        mazeParams.setPropData(propData);
        mazeParams.setQuestionPosData(questionPosData);
        mazeParams.setQuestions(questions);
        mazeParams.setMazeListener(this);

        fMaze = new Maze(mazeParams);

		// initialize our game thread 
		fThread = new PaintThread(this, fHolder, fContext, fMaze, fTextArea);

        // setup gesture listener
        fGestures = new GestureDetector(fContext, new GestureListener(RayView.this));

        setKeepScreenOn(true);
	}

    /**
     * MazeListener method implementation. Show or hide the question button based on the event from the maze.
     */
    public void questionModeChanged(boolean isInQuestionMode) {
        fOverlayGraphics.setActive(OverlayGraphics.OVR_BTN_QUESTION, isInQuestionMode);
    }

    /**
     * PopuAnimTrackerListener method implementation. Show the Close button and Question label when popup is done animating.
     * @param isDone
     */
    public void popupAnimModeChanged(boolean isDone) {
        if (isDone) {
            fOverlayGraphics.setActive(OverlayGraphics.OVR_BTN_CLOSE, fIsInQuestionPopupMode);
            fOverlayGraphics.setActive(OverlayGraphics.OVR_IMG_QUESTION, fIsInQuestionPopupMode);
        }
    }

    /**
     * MazeListener method implementation.  Show or hide the close button based on the event from the maze.
     */
    public void questionPopupModeChanged(boolean isInQuestionPopupMode) {
        if (isInQuestionPopupMode) fNumAutoMoves = new Integer(0);   // don't let player lunge forward after popup is closed
        //fOverlayGraphics.setActive(OverlayGraphics.OVR_BTN_CLOSE, isInQuestionPopupMode);
        //fOverlayGraphics.setActive(OverlayGraphics.OVR_IMG_QUESTION, isInQuestionPopupMode);
        if (isInQuestionPopupMode)      // the question button has to be disabled when the popup question is present
            fOverlayGraphics.setActive(OverlayGraphics.OVR_BTN_QUESTION, false);

        // switching to pop up mode
        if (!fIsInQuestionPopupMode && isInQuestionPopupMode) {
            fPopupAnimTracker = new PopupAnimTracker(fDisplay.getWidth(), fDisplay.getHeight(), 6, this);
            fSavedTiltMode = fTiltMode;  // when we transition to question mode, we must save current tilt mode
            fTiltMode = false;
        }

        // switching out of pop up mode
        if (fIsInQuestionPopupMode && !isInQuestionPopupMode) {
            fOverlayGraphics.setActive(OverlayGraphics.OVR_BTN_CLOSE, false);
            fOverlayGraphics.setActive(OverlayGraphics.OVR_IMG_QUESTION, false);
            fTiltMode = fSavedTiltMode;  // restore tilt mode when all done with question mode
        }

        fIsInQuestionPopupMode = isInQuestionPopupMode;
    }

    /**
     * MazeListener method implementation. This event is ignored.
     */
    public void autoMoveModeChanged(boolean isInAutoMoveMode) {
    }

    /**
     * Returns the collection of game graphics/buttons.
     */
    public OverlayGraphics getOverlayGraphics() {
        return fOverlayGraphics;
    }

    /**
     * Returns true if we are in instructions mode.
     */
    public boolean isInInstructionsMode() {
        return fInstructionsBeginTime != 0l;
    }

    /**
     * Returns true if we are in opening credits mode.
     */
    public boolean isInOpeningCreditsMode() {
        return fOpeningCreditsBeginTime != 0l;
    }

    /**
     * Returns true if we are in overlay inst mode showing the overlay instructions temporarily on the screen.
     */
    public boolean isInOverlayInstMode() {
        return fInOverlayInstMode;
    }

    /**
     * Checks to update clock to see if we are done with overlay inst mode.
     */
    public void checkOverlayInstModeComplete() {
        boolean before = fInOverlayInstMode;
        boolean after  = (fOverlayInstBeginTime != 0l && fOverlayInstBeginTime + OVERLAY_INST_DURATION >  System.currentTimeMillis());
        if (before && !after) fOverlayGraphics.setActive(OverlayGraphics.OVR_IMG_INST, false);  // turn off overlay, if time is meet
        fInOverlayInstMode = after;
    }

    /**
     * Returns true if we are in overlay tilt mode showing tilt instructions temporarily on the screen.
     */
    public boolean isInOverlayTiltMode() {
        return fInOverlayTiltMode;
    }

    /**
     * Checks to update clock to see if we are done with overlay tilt mode.
     */
    public void checkOverlayTiltModeComplete() {
        boolean before = fInOverlayTiltMode;
        boolean after  = (fOverlayTiltBeginTime != 0l && fOverlayTiltBeginTime + OVERLAY_INST_DURATION >  System.currentTimeMillis());
        if (before && !after) {
            fOverlayGraphics.setActive(fTiltMode ? OverlayGraphics.OVR_IMG_TILT_ON : OverlayGraphics.OVR_IMG_TILT_OFF, false);  // turn off overlay, if time is meet
            fOverlayTiltBeginTime = 0l;
        }
        fInOverlayTiltMode = after;
    }

    /**
     * Returns the popup animation tracker.
     */
    public PopupAnimTracker getPopupAnimTracker() {
        return fPopupAnimTracker;
    }

    /**
     * Draws the instructions bitmap.  This routine will be called multiple times
     * since we want the instructions to appear for a set period of time.
     */
    public void drawInstructions() {
        if (fInstructions == null) {      // lazy initialize fInstructions
            fInstructionsBeginTime = System.currentTimeMillis();
            fInstructions = new Instructions();
        }
        fInstructions.drawInstructions(fContext, fHolder);

        // see if we are done with instructions
        if ((System.currentTimeMillis() - fInstructionsBeginTime) > INSTRUCTIONS_DURATION) {
            fInstructionsBeginTime = 0l;      // zero denotes not in instructions mode
            drawOpeningCredits();             // move on to next phase
        }
    }

    /**
     * Draws the opening credits image along with the overlay opening credits text.  This routine will be called
     * multiple times since we want the opening credits to appear for a set period of time.
     */
    public void drawOpeningCredits() {
//        if (fOpeningCredits != null && fOpeningCredits.isAllDone()) return;
        if (fPlatform == null) return;
        if (fGeneralConfig == null) return;
        if (fOpeningCredits == null) {      // lazy initialize fOpeningCredits
            fOpeningCreditsBeginTime = System.currentTimeMillis();
            fOpeningCredits = new OpeningCredits(fPlatform, fGeneralConfig);
        }
        fOpeningCredits.drawOpeningCredits(fContext, fHolder);

        // see if we are done with opening credits
        if ((System.currentTimeMillis() - fOpeningCreditsBeginTime) > OPENING_CREDITS_DURATION) {
            fOpeningCreditsBeginTime = 0l;      // zero denotes not in opening credits mode
        }
    }

    /**
     * Called by the paint thread to use one of the banked auto moves and move the player forward.
     */
    public void consumeAutoMove() {
        if (fNumAutoMoves > 0) {
            // there is a gradation in momentum as the player losses steam
            if (fNumAutoMoves == 1)
                fMaze.moveForward(MazeGlobals.QTR_PLAYER_SPEED);
            else if (fNumAutoMoves == 2)
                fMaze.moveForward(MazeGlobals.HALF_PLAYER_SPEED);
            else if (fNumAutoMoves <= 5)
                fMaze.moveForward(MazeGlobals.PLAYER_SPEED);
            else if (fNumAutoMoves <= 7)
                fMaze.moveForward(MazeGlobals.QTR_PLAYER_SPEED + MazeGlobals.PLAYER_SPEED);
            else
                fMaze.moveForward(MazeGlobals.HALF_PLAYER_SPEED + MazeGlobals.PLAYER_SPEED);

            fNumAutoMoves = new Integer(--fNumAutoMoves);
            if (fNumAutoMoves <= 0) fMaze.setInAutoMoveMode(false);
        } else fMaze.setInAutoMoveMode(false);
    }

    /**
     * Set up handler to detect touch events.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return fGestures.onTouchEvent(event);
    }

	/**
	 * This method is overriden from the SurfaceView superclass and is automatically
	 * called when the view is created, changed, resumed, etc. 
	 */
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        //Toast.makeText(fContext, "surfaceChanged", Toast.LENGTH_SHORT).show();
        if (fGeneralConfig.getUsingBackgroundMusic() && !StartActivity.getMediaPlayer().isPlaying())
            StartActivity.getMediaPlayer().start();    // start playing background music on resume
		setFocusable(true);		// enable input from the user
		requestFocus();
		setFocusableInTouchMode(true);
	}

	/**
	 * This method is overridden from the SurfaceView superclass and is automatically
	 * called when the view is destroyed.  Here we terminate the game loop.
	 */
	public void surfaceDestroyed(SurfaceHolder arg0) {
        /////Toast.makeText(fContext, "surfaceDestroyed", Toast.LENGTH_SHORT).show();
        //fMediaPlayer.stop();
        if (StartActivity.getMediaPlayer().isPlaying()) {
            StartActivity.getMediaPlayer().stop();
            StartActivity.disposeMediaPlayer();
        }
		boolean retry = true;
		fThread.fState = PaintThread.PAUSED;
		while (retry) {
			try {
				fThread.join();		// kill the thread
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * This method is overridden from the SurfaceView superclass and is automatically
	 * called when the view is created. 
	 */
	public void surfaceCreated(SurfaceHolder arg0) {
        //Toast.makeText(fContext, "surfaceCreated", Toast.LENGTH_SHORT).show();
		setFocusable(true);		// enable input from the user
		requestFocus();
		setFocusableInTouchMode(true);

        drawInstructions();

		if (fThread.fState == PaintThread.PAUSED) {	// the game is opened again
			fThread = new PaintThread(this, getHolder(), fContext, fMaze, fTextArea);
			fThread.start();
		} else {
			fThread.start();						// game is created for the first time
		}
	}

	/**
	 * Call back for when keys are pressed.  Here we look for the up, down, right and left
	 * keys (or equivalents) and update player position accordingly.  This handles track
     * ball movement, too.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        fNumAutoMoves = new Integer(0);     // clear auto move queue
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_Z) {
			fMaze.rotateLeft();
            return true;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_X) {
			fMaze.rotateRight();
            return true;
		}	
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_O) {
			fMaze.moveForward();
            return true;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_L) {
			fMaze.moveBackward();
            return true;
		}
        if (keyCode == KeyEvent.KEYCODE_R) {    // r key will be a run forward move, so we bank automoves
            fNumAutoMoves = new Integer(11);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_V) {    // v key will close question window
            if (fMaze.isInQuestionPopupMode()) {
                fSoundEffects.playSoundFile(GeneralConfig.ANSWER_EFFECT);
                fTextArea.clear();
                fMaze.setInQuestionPopupMode(false);
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_Q) {    // quit key
            handleQuit();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_A) {
            Toast.makeText(fContext, "Close this window and choose pathway A to see if your answer is correct.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_B) {
            Toast.makeText(fContext, "Close this window and choose pathway B to see if your answer is correct.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_C) {
            Toast.makeText(fContext, "Close this window and choose pathway C to see if your answer is correct.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_D) {
            Toast.makeText(fContext, "Close this window and choose pathway D to see if your answer is correct.", Toast.LENGTH_SHORT).show();
            return true;
        }
		return false;
	}

    /**
     * Confirm quit and take action.
     */
    private void handleQuit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(fContext);
        builder.setMessage("Are you sure you want to exit this maze?")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();

                    fTiltMode = false;
                    StartActivity.getMediaPlayer().stop();
                    StartActivity.disposeMediaPlayer();

                    /////////////////((Activity)getContext()).finish();
                    Intent i = fContext.getPackageManager()
                            .getLaunchIntentForPackage(fContext.getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    fContext.startActivity(i);
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        builder.show();
    }

    /**
     * Private class for gestures.
     */

    private class GestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
        public int FLING_VELOCITY_MILLI_DENOMINATOR = 150;

        RayView fView;

        public GestureListener(RayView view) {
            fView = view;
        }

        /**
         *  Look at the onDown event to see if we need to bring up the inst overlay
         */
        public boolean onDown(MotionEvent e) {
            int x = Math.round(e.getX());
            int y = Math.round(e.getY());
            int btnIndex = fOverlayGraphics.getIndexOfGraphicAt(x, y);

            // only worry about onDown event if we have not hit a button
            if (btnIndex == OverlayGraphics.OVR_NOT_FOUND) {
                // only worry about onDown event if we are done with instruction mode and credits mode
                if (!isInInstructionsMode() && !isInOpeningCreditsMode()) {
                    // only worry about onDown event if we haven't previously shown the inst overlay before
                    if (fOverlayInstBeginTime == 0l) {
                        fInOverlayInstMode = true;
                        fOverlayInstBeginTime = System.currentTimeMillis();
                        fOverlayGraphics.setActive(OverlayGraphics.OVR_IMG_INST, true);
                    }
                }
            }
            return true;
        }

        /**
         * Called by gesture listener when there is a finger movement, this method moves the player position
         * accordingly.
         */
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //fView.onFingerMove(distanceX, distanceY);
            fNumAutoMoves = new Integer(0);   // clear auto move queue
            fMaze.moveRelative((new Float(distanceX)).intValue(), (new Float(distanceY)).intValue());
            return true;
        }

        /**
         * Single tap is used to close the question popup.
         */
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // if user taps the screen on the instructions screen, then move on
            if (isInInstructionsMode()) {
                fInstructionsBeginTime = 0l;      // zero denotes not in instructions mode anymore
                drawOpeningCredits();             // move on to next mode
                return true;
            }

            int x = Math.round(e.getX());
            int y = Math.round(e.getY());
            int btnIndex = fOverlayGraphics.getIndexOfGraphicAt(x, y);

            // if exit button pressed confirm with user
            if (btnIndex == OverlayGraphics.OVR_BTN_EXIT) {
                handleQuit();
                return true;
            }

            // if question button pressed bring back the question popup
            if (btnIndex == OverlayGraphics.OVR_BTN_QUESTION) {
                fMaze.setInQuestionPopupMode(true);
                fSoundEffects.playSoundFile(GeneralConfig.QUESTION_EFFECT);
                return true;
            }

            // toggle tilt mode on and off
            if (btnIndex == OverlayGraphics.OVR_BTN_TILT) {
                // if we are already showing an overlay and the user hits the button again, turn off old overlay before turning on new
                if (fInOverlayTiltMode) {
                    fOverlayGraphics.setActive(fTiltMode ? OverlayGraphics.OVR_IMG_TILT_ON : OverlayGraphics.OVR_IMG_TILT_OFF, false);
                    fOverlayTiltBeginTime = 0l;
                    fInOverlayTiltMode = false;
                }

                // toggle state and bring up the appropriate graphic
                fTiltMode = !fTiltMode;
                fInOverlayTiltMode = true;
                fOverlayTiltBeginTime = System.currentTimeMillis();
                fOverlayGraphics.setActive(fTiltMode ? OverlayGraphics.OVR_IMG_TILT_ON : OverlayGraphics.OVR_IMG_TILT_OFF, true);
                return true;
            }

            // if close button pressed on question then close popup mode.
            if (btnIndex == OverlayGraphics.OVR_BTN_CLOSE) {
                if (fMaze.isInQuestionPopupMode()) {
                    fSoundEffects.playSoundFile(GeneralConfig.ANSWER_EFFECT);
                    fTextArea.clear();
                    fMaze.setInQuestionPopupMode(false);

                    // whenever the close button is pressed, we have to turn on the question button again
                    fOverlayGraphics.setActive(OverlayGraphics.OVR_BTN_QUESTION, true);
                }
                return true;
            }

            return true;
        }


        public void onShowPress(MotionEvent e) {
            ;
        }

        public boolean onSingleTapUp(MotionEvent e) {
            ;
            return false;
        }

        /**
         * Called when user clicks the screen a couple of times in succession, this method straightens the players
         * position.
         */
        public boolean onDoubleTap(MotionEvent e) {
            //fNumAutoMoves = new Integer(0);   // clear auto move queue
            //fMaze.straightenUp();
            float xPos = e.getX();
            if (xPos < fSectionOne) {
                fMaze.rotateLeft();
                fMaze.rotateLeft();
                fNumAutoMoves = new Integer(3);
            } else if (xPos >= fSectionOne && xPos < fSectionTwo) {
                fMaze.rotateLeft();
                fNumAutoMoves = new Integer(7);
            } else if (xPos >= fSectionTwo && xPos < fSectionThree) {
                fNumAutoMoves = new Integer(11);
            } else if (xPos >= fSectionThree && xPos < fSectionFour) {
                fMaze.rotateRight();
                fNumAutoMoves = new Integer(7);
            } else {
                fMaze.rotateRight();
                fMaze.rotateRight();
                fNumAutoMoves = new Integer(3);
            }
            return true;
        }

        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        /**
         *  Fling is a continued motion after the user removes finger at the end of the gesture, allowing an animation
         *  motion to continue with a perceived kinetic energy.  Here we bank up auto moves so player will continue in
         *  a forward motion as a result of a fling (run forward scenario).
         */
        public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX, final float velocityY) {
            if (velocityY > 0) return true;                // only allow upward flings
            float vertVelocity = Math.abs(velocityY);
            float horzVelocity = Math.abs(velocityX);
            if (vertVelocity < 4 * horzVelocity)
                return true;    // y direction must be more significant than x, or it isn't an upward motion
            fNumAutoMoves = new Integer(Math.round(vertVelocity / FLING_VELOCITY_MILLI_DENOMINATOR));
            fMaze.setInAutoMoveMode(fNumAutoMoves > 0);
            return true;
        }

        public void onLongPress(MotionEvent e) {
           ;
        }
    }

    /**
     * Fulfills SensorEventListener interface.
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Fulfills SensorEventListener interface and detects when the accelerometer has changed.
     * @param event
     */
    public void onSensorChanged(SensorEvent event) {
        if (!fTiltMode) return;

        // we only care about accelerometer sensor type
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // we have to throttle down the accelerometer events on some over eager devices
            // the following code sets the throw away rate after reading so many events and then comparing it to time elapsed
            // this block is really just the test phase at the beginning to set throw away value
            if (fSensorChangedCnt <= SENSOR_TARGET_READ_PER_SEC + 1) {
                if (fSensorChangedCnt == 0) {
                    fSensorBeginTime = System.currentTimeMillis();
                }
                // after so many reads compare the begin time to current and set the throw away count accordingly
                if (fSensorChangedCnt > 0 && fSensorChangedCnt % SENSOR_TARGET_READ_PER_SEC == 0) {
                    long millisElapsed = System.currentTimeMillis() - fSensorBeginTime;
                    if (millisElapsed >= 1000) {     // greater than a thousand means sensor is too slow so throw nothing away
                        fSensorThrowAwayRate = 1;
                    } else {                         // only worry about sub seconds
                        double ratio = (double)millisElapsed / (double)1000.0;
                        if (ratio != 0) {
                            double throwAwayRateDbl = 1.0 / ratio;  // invert sub second gives us throw away ratio
                            fSensorThrowAwayRate = (int)Math.round(throwAwayRateDbl);
                            if (fSensorThrowAwayRate < 0) fSensorThrowAwayRate = 1;
                        } else fSensorThrowAwayRate = 1;
                    }
                }
                if (fSensorThrowAwayRate != 0) Log.d(sLogLabel, "---->" + " accelerometer sensor throw away rate: " + fSensorThrowAwayRate);
            }
            fSensorChangedCnt++;

            // if we have established the throw away rate, and it is significant and throw everything away accept when we hit perfectly on the rate
            if (fSensorChangedCnt != 0 && fSensorThrowAwayRate > 1 && !(fSensorChangedCnt % fSensorThrowAwayRate == 0)) return; // throw this one away

            // assign directions
            float x = event.values[0];
            float y = event.values[1];
            // float z = event.values[2];

            // orientation map determines how we read in the input from sensors
            if (fIsTabletOrientation) {
                if (y > 0.5)  fMaze.moveBackward();
                if (y < -0.5) {
                    if (y < -3.1) fNumAutoMoves = new Integer(11);   // run forward if tilt is extreme
                    else if (y < -2.3) fNumAutoMoves = new Integer(7);
                    else if (y < -1.5) fNumAutoMoves = new Integer(5);
                    else fMaze.moveForward();
                }
                if (x > 0.5)  fMaze.rotateLeft();
                if (x < -0.5) fMaze.rotateRight();
            } else {
                if (x > 0.5)  fMaze.moveBackward();
                if (x < -0.5) {
                    if (x < -3.1) fNumAutoMoves = new Integer(11);   // run forward if tilt is extreme
                    else if (x < -2.3) fNumAutoMoves = new Integer(7);
                    else if (x < -1.5) fNumAutoMoves = new Integer(5);
                    fMaze.moveForward();
                }
                if (y > 0.5)  fMaze.rotateRight();
                if (y < -0.5) fMaze.rotateLeft();
            }
        }
    }
}
