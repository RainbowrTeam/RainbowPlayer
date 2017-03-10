/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.Core;

import rainbowplayer.Classes.Title;
import java.io.File;
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
    
    public void playTitle(Title title){
        currentTitle = title;
        
        Media tMedia = new Media(new File(currentTitle.getFilePath()).toURI().toString());
        mediaPlayer = new MediaPlayer(tMedia);
        
        mediaPlayer.play();
        isPlaying = true;
    }
    
    public void pausePlayback(){
        if(mediaPlayer != null & isPlaying){
            mediaPlayer.pause();
            isPlaying = false;
        } else if (mediaPlayer != null){
            mediaPlayer.play();
            isPlaying = true;
        }
    }
    
    public void stopPlayback(){
        if(mediaPlayer != null & isPlaying){
            mediaPlayer.stop();
            isPlaying = false;
        }
    }
    
    public Title getCurrentTitle(){
        return currentTitle;
    }
}