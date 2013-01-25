package com.edventuremaze.applet.maze;

import com.edventuremaze.applet.ResourceDirectory;
import com.edventuremaze.maze.SoundEffects;
import com.edventuremaze.maze.Platform;

import java.applet.Applet;
import java.applet.AudioClip;
import java.util.HashMap;

/**
 * This class is used for playing sound effects.
 *
 * @author brianpratt
 */

public class SoundEffectsApplet implements SoundEffects {

    final static String sLogLabel = "--->SoundEffectsApplet:";

    private String fFolder;
    private PlatformApplet fPlatform;
    private  HashMap<String, AudioClip> fSoundPoolFileMap;

    /**
     * Constructor
     *
     * @param platform The os specific connector object.
     * @param folder   The folder where the maze map and wall files are found.
     */
    public SoundEffectsApplet(Platform platform, String folder) {
        fPlatform = (PlatformApplet)platform;
        fFolder = folder;
        fSoundPoolFileMap = new HashMap<String, AudioClip>();
    }

    /**
     * Adds the audio clip file to the collection.
     */
    public void addSoundFile(String soundFile) {
        ResourceDirectory resourceDir = fPlatform.getResourceDir();
        Applet app = fPlatform.getApplet();
        AudioClip audioClip = app.getAudioClip(resourceDir.getAppropPathURL(), soundFile);
        fSoundPoolFileMap.put(soundFile, audioClip);
    }

    /**
     * Plays the specified audio file.
     */
    public void playSoundFile(String soundFile) {
        AudioClip audioClip = fSoundPoolFileMap.get(soundFile);
        audioClip.play();
    }

    /**
     * Not used in this implementation of SoundEffects.
     */
    public void stopSoundFile(String soundFile) {
    }

    /**
     * Not used in this implementation of SoundEffects.
     */
    public void cleanup() {
    }

    /**
     *  Used to doctor audio files names in implementations of the SoundEffects interface, however
     *  it is unneeded in the applet version.
     */
    public String doctorFileName(String fileName) {
        return fileName;
    }
}

