/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.Core;

import rainbowplayer.Classes.Title;
import java.io.File;
import java.util.ArrayList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author Tim Weiß
 */
public class SongPlayer {
 
    private MediaPlayer mediaPlayer;
    private Title currentTitle;
    
    private boolean isPlaying;
    private boolean isQueued;
    
    private int queuePosition = 0;
    
    private ArrayList<Title> titleQueue;
    
    public SongPlayer() {
        titleQueue = new ArrayList<>();
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
     * @param firstTitle Wheter the title is the first in the queue. Only for the first time in the queue.
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
        
        Media tMedia = new Media(new File(currentTitle.getFilePath()).toURI().toString());
        
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer(tMedia);
        } else {
            mediaPlayer.stop();
            mediaPlayer = new MediaPlayer(tMedia);
        }
        
        mediaPlayer.play();
        isPlaying = true;
    }
    
    /**
     * Pauses the current title and resumes it when paused.
     */
    public void pausePlayback(){
        if(mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
        } else if (mediaPlayer != null) {
            mediaPlayer.play();
            isPlaying = true;
        }
    }
    
    /**
     * Stops the playback and resets the player and queue.
     */
    public void stopPlayback(){
        if(mediaPlayer != null && isPlaying) {
            mediaPlayer.stop();
            isPlaying = false;
            isQueued = false;
            
            currentTitle = null;
            queuePosition = 0;
        }
    }
    
    /**
     * Retrieves the currently playing title.
     * @return The playing title.
     */
    public Title getCurrentTitle() {
        return currentTitle;
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
}