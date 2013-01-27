package com.edventuremaze.maze;

/**
 * Establishes a sound effects interface for playing small audio files.
 * @author brianpratt
 */

public interface SoundEffects {

    // loads and adds the audio file to the collection
    public void addSoundFile(String soundFile);

    // platform specific doctoring of audio file name
    public String doctorFileName(String fileName);

    // plays the specified audio file
    public void playSoundFile(String soundFile);

    // stops the play of the specified file
    public void stopSoundFile(String soundFile);

    // cleans up the object instance
    public void cleanup();
}




