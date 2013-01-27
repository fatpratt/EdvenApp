package com.edventuremaze.and.maze;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import com.edventuremaze.and.utils.FileUtilsAnd;
import com.edventuremaze.maze.Platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * This class is the Android specific implementation of the AudioPlayer used to play an audio file.
 * Here is an example of how this class is used:
 *
 *        AudioPlayer audioPlayer = AudioPlayerFactory.createAudioPlayer(platform, fMazeId);
 *        audioPlayer.play("Audio1.wav");
 *        ...
 *        audioPlayer = AudioPlayerFactory.createAudioPlayer(platform, fMazeId);
 *        audioPlayer.play("Audio2.wav");
 *
 * Note:  This file is not currently in use and is not referenced by other classes.  It may be a useful
 * backup for the SoundEffectsAnd class so the code is retained.
 *
 * @author brianpratt
 */

public class AudioPlayerAnd {
    final static String sLogLabel = "--->AudioPlayerAnd:";

    private PlatformAnd fPlatform;
    private String fFolder;
    private MediaPlayer fMediaPlayer;
    private AudioManager fAudioManager;

    /**
     * Constructor - establishes the audio player.
     * @param platform  The os specific connector object.
     * @param folder The folder where the maze map and wall files are found.
     */
    public AudioPlayerAnd(Platform platform, String folder) {
        fPlatform = (PlatformAnd)platform;
        fFolder = folder;
        Context context = ((PlatformAnd) fPlatform).getContext();
        fAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     *  Android doesn't play .au audio files.  The mazeconfig file references audio files as .au files, however,
     *  only .wav files are downloaded to this platform.  This method doctors the filename to use a
     *  .wav extension so we are finding and playing the right audio files.
     */
    public String doctorFileName(String fileName) {
        String trimmedFileName = fileName.substring(0, fileName.lastIndexOf('.'));
        return trimmedFileName + ".wav";
    }

    /**
     * Plays the specified audio file.
     */
    public void play(String fileName) {
        Context context = ((PlatformAnd) fPlatform).getContext();
        ContextWrapper cw = new ContextWrapper(context);
        File path = cw.getDir(fFolder + fPlatform.getFolderSuffix(), Context.MODE_PRIVATE);
        InputStream in = null;
        fileName = doctorFileName(fileName);

        try {
            String fullFileName = FileUtilsAnd.appendSlash(path.toString()) + fileName;
            Uri streamUri = Uri.parse(fullFileName);
            fMediaPlayer = new MediaPlayer();

            fMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                //@Override
                public void onPrepared(MediaPlayer mp) {
                    try {
                        //float streamVolume = fAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        float streamVolume = fAudioManager.getStreamVolume(AudioManager.STREAM_RING);
                        streamVolume = streamVolume / fAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        fMediaPlayer.setVolume(streamVolume, streamVolume);
                        fMediaPlayer.start();
                    } catch (IllegalStateException e) {
                        ((PlatformAnd) fPlatform).logError(sLogLabel, "Illegal state exception thrown in start.");
                    }
                }
            });

            fMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                //@Override
                public void onCompletion(MediaPlayer mp) {
                    fMediaPlayer.release();
                }
            });

            File file = new File(fullFileName);
            FileInputStream fis = new FileInputStream(file);
            fMediaPlayer.setDataSource(fis.getFD());
            fMediaPlayer.prepareAsync();
        } catch (Exception e) {
            ((PlatformAnd) fPlatform).logError(sLogLabel, e.getMessage());
        }

    }
}




