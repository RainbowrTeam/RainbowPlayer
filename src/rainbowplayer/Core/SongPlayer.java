/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.Core;

import java.awt.event.ActionListener;
import rainbowplayer.Classes.Title;
import java.io.File;
import java.util.ArrayList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Timer;
import java.util.TimerTask;
import javafx.event.ActionEvent;
import rainbowplayer.Classes.LiveTrack;
import rainbowplayer.FXMLDocumentController;

/**
 *
 * @author Tim Weiß
 */
public class SongPlayer {
 
    private MediaPlayer mediaPlayer;
    
    private LiveTrack currentTrack;
    private Title currentTitle;
    
    private boolean isPlaying;
    private boolean isQueued;
    
    private int queuePosition = 0;
    
    private Timer songTimer;
    
    private ArrayList<Title> titleQueue;
    
    // wee need this to update the interface
    private final FXMLDocumentController pInterface;
    
    public SongPlayer(FXMLDocumentController interf) {
        titleQueue = new ArrayList<>();
        // songTimer = new Timer();
        pInterface = interf;
    }
    
    /**
     * The entry point for playing queued titles like playlists.
     * @param tQueue The ArrayList of titles
     */
    public void playTitleQueue(ArrayList<Title> tQueue) {
        stopPlayback();
        titleQueue = tQueue;
        isQueued = true;
        playQueuedTitle(true, true);
    }
    
    /**
     * Plays, skips and repeats the previous title depending on the action.
     * @param skip Should be set to true when the title needs to be skipped.
     * @param firstTitle Whether the title is the first in the queue. Only for the first time in the queue.
     */
    private void playQueuedTitle(boolean skip, boolean firstTitle) {
        if(titleQueue.size() > queuePosition && queuePosition >= 0) {
            if(skip) {
                if(!firstTitle)
                    queuePosition++;
                playTitle(titleQueue.get(queuePosition));
            } else {
                if(queuePosition != 0){
                    queuePosition--;
                    playTitle(titleQueue.get(queuePosition));
                } else {
                    stopPlayback();
                }
            }
        } else {
            stopPlayback();
        }
    }
    
    /**
     * Plays the title from the Title class.
     * @param title The designated title.
     */
    public void playTitle(Title title) {
        currentTitle = title;
        currentTrack = new LiveTrack(title.getFilePath(), title.getTitleName(), title.getArtistName());
        
        Media tMedia = new Media(new File(currentTrack.getFilePath()).toURI().toString());
        
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer(tMedia);
        } else {
            mediaPlayer.stop();
            mediaPlayer = new MediaPlayer(tMedia);
        }
        
        mediaPlayer.play();
        isPlaying = true;
        currentTrack.setIsPaused(false);
        
        // we need to run the timer after the player loaded the song
        mediaPlayer.setOnReady(() -> {
            currentTrack.setDuration((int) tMedia.getDuration().toSeconds());
            trackStarted();
        });
    }
    
    /**
     * Starts the timer for continuous playback.
     */
    private void trackStarted() {
        if(songTimer != null) {
            songTimer.cancel();
            songTimer.purge();
            songTimer = null;
            songTimer = new Timer();
        } else {
            songTimer = new Timer();
        }
        
        songTimer.schedule(new TimerTask() {
            @Override
            public void run() {
              playbackTimerTick();
            }
          }, 0, 1000);

        songRemainingSeconds = currentTrack.getDuration();
        pInterface.updateInterface();
    }
    
    private int songRemainingSeconds;
    
    private void playbackTimerTick(){
        if(isPlaying){
            songRemainingSeconds--;
            currentTrack.setRemainingSeconds(songRemainingSeconds);
            if(songRemainingSeconds <= 0){
                skipSong();
            }
            
            System.out.println("my time is now: " + songRemainingSeconds + " | " + currentTrack.getDuration());
            // pInterface.updateInterface();
        }    
    }
    
    /**
     * Pauses the current title and resumes it when paused.
     */
    public void pausePlayback(){
        if(mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            currentTrack.setIsPaused(!isPlaying);
        } else if (mediaPlayer != null) {
            mediaPlayer.play();
            isPlaying = true;
            currentTrack.setIsPaused(!isPlaying);
        }
        pInterface.updateInterface();
    }
    
    /**
     * Stops the playback and resets the player and queue.
     */
    public void stopPlayback(){
        if(mediaPlayer != null && isPlaying) {
            mediaPlayer.stop();
            isPlaying = false;
            isQueued = false;
            
            currentTrack = null;
            queuePosition = 0;
            pInterface.updateInterface();
        }
    }
    
    /**
     * Retrieves the currently playing title.
     * @return The playing title.
     */
    public Title getCurrentTitle() {
        return currentTitle;
    }
    
    public LiveTrack getPlayingTrack(){
        return currentTrack;
    }
    
    /**
     * Skips the current song and triggers the next title.
     */
    public void skipSong() {
        if(isQueued){
            playQueuedTitle(true, false);
        }
    }
    
    /**
     * Plays the song played beforehand.
     */
    public void prevSong() {
        if(isQueued){
            playQueuedTitle(false, false);
        }
    }
    
    public boolean isPlaybackActive(){
        return isPlaying;
    }
}