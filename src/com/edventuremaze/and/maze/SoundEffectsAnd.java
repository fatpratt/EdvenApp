package com.edventuremaze.and.maze;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioManager;
import android.media.SoundPool;
import com.edventuremaze.and.utils.FileUtilsAnd;
import com.edventuremaze.maze.SoundEffects;
import com.edventuremaze.maze.Platform;

import java.io.File;
import java.util.HashMap;

/**
 * This class is used for playing sound effects.  Here is a sample as to how it is to be used:
 *        SoundEffects soundEffects = new SoundEffects(platform, fMazeId);
 *        soundEffects.addSoundFile("Audio1.wav");
 *        soundEffects.addSoundFile("Audio2.wav");
 *        ...
 *        soundEffects.playSoundFile("Audio1.wav");
 *        soundEffects.playSoundFile("Audio1.wav");
 *        soundEffects.playSoundFile("Audio2.wav");
 *        soundEffects.cleanup();
 *
 * @author brianpratt
 */

public class SoundEffectsAnd implements SoundEffects {

    final static String sLogLabel = "--->SoundEffects:";
    final static public String sIgnoreExtension = "au";

	private  SoundPool fSoundPool;
	private  HashMap<String, Integer> fSoundPoolFileMap;
    private  HashMap<Integer, Integer> fSoundPoolResourceMap;
	private  AudioManager  fAudioManager;

    private PlatformAnd fPlatform;
    private String fFolder;

    /**
     * Constructor
     *
     * @param platform The os specific connector object.
     * @param folder   The folder where the maze map and wall files are found.
     */
    public SoundEffectsAnd(Platform platform, String folder) {
        fPlatform = (PlatformAnd)platform;
        fFolder = folder;

        fSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        fSoundPoolFileMap = new HashMap<String, Integer>();
        fSoundPoolResourceMap = new HashMap<Integer, Integer>();
        Context context = ((PlatformAnd) fPlatform).getContext();
        fAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * Loads and adds the audio file to the collection.
     */
    public void addSoundFile(String soundFile) {
        Context context = ((PlatformAnd) fPlatform).getContext();
        ContextWrapper cw = new ContextWrapper(context);
        File path = cw.getDir(fFolder + fPlatform.getFolderSuffix(), Context.MODE_PRIVATE);
        String doctoredSoundFile = doctorFileName(soundFile); // kludge knowing .wav files substitute for .au in the android environment
        String fullPathStr = FileUtilsAnd.appendSlash(path.toString()) + doctoredSoundFile;

        fSoundPoolFileMap.put(soundFile, fSoundPool.load(fullPathStr, 1));
    }

    /**
     * Plays the specified audio file.
     */
    public void playSoundFile(String soundFile) {
        float streamVolume = fAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //streamVolume = streamVolume / fAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Integer soundId = fSoundPoolFileMap.get(soundFile);
        if (soundId != null) {
            fSoundPool.play(soundId, streamVolume, streamVolume, 1, 0, 1.0f);
        } else {
            ((PlatformAnd) fPlatform).logError(sLogLabel, "Can't find sound file: " + soundFile);
        }
    }

    /**
     * Stops the play of the specified audio file.
     */
    public void stopSoundFile(String soundFile) {
        fSoundPool.stop(fSoundPoolFileMap.get(soundFile));
    }

    /**
     * Cleans up the object instance.
     */
    public void cleanup() {
        fSoundPool.release();
        fSoundPool = null;
        fSoundPoolFileMap.clear();
        fSoundPoolResourceMap.clear();
        fAudioManager.unloadSoundEffects();
    }

     /**
     *  Android doesn't play .au audio files.  The mazeconfig file references audio files as .au files, however,
     *  only .wav files are downloaded to this platform.  This method doctors the filename to use a
     *  .wav extension so we are finding and playing the right audio files.
     */
     public String doctorFileName(String fileName) {
         String extension = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
         if (extension.equalsIgnoreCase(sIgnoreExtension)) {
             String trimmedFileName = fileName.substring(0, fileName.lastIndexOf('.'));
             return trimmedFileName + ".wav";
         }
         return fileName;
     }

}

