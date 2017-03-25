/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer;

import rainbowplayer.Classes.Title;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import rainbowplayer.Classes.Duration;
import rainbowplayer.Core.SongPlayer;
import rainbowplayer.db.Database;

public class FXMLDocumentController implements Initializable {
    
    private SongPlayer songPlayer;
    private Database db;
    
    private HashMap<String, String> playerData = null;
    
    @FXML
    private Label titleLabel;
    @FXML
    private Label artistLabel;
    @FXML
    private Label timeLabel;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        ArrayList<Title> tQueue = new ArrayList<>();
        //tQueue.add(new Title("C:\\Users\\Tim\\Music\\01 - we came to gangbang.mp3", "we came to gangbang", "goreshit"));
        //tQueue.add(new Title("C:\\Users\\Tim\\Music\\05 - Awg.mp3", "Awg", "Farin Urlaub Racing Team"));
        tQueue.add(new Title("Z:\\Musik\\Artists\\Crusher-P\\Echo\\echo.mp3", "Echo", "Crusher-P"));
        tQueue.add(new Title("Z:\\Musik\\Artists\\Fleetwood Mac\\Fleetwood Mac Greatest Hits\\MP3\\12 Fleetwood Mac - Little Lies.mp3", "Little Lies", "Fleetwood Mac"));
        tQueue.add(new Title("Z:\\Musik\\Artists\\Rammstein\\Mutter\\05 Feuer frei.mp3", "Feuer Frei", "Rammstein"));
        
        songPlayer.playTitleQueue(tQueue);
        startTimer();
    }
    
    private void startTimer(){
        UiWorkerThread myThread = new UiWorkerThread(playerData, 0, songPlayer, this);
        myThread.start();
    }
    
    private void timerTick(){}
    
    @FXML
    private void handleStopButtonAction(ActionEvent event) {
        songPlayer.stopPlayback();
    }
    
    @FXML
    private void handlePauseButtonAction(ActionEvent event) {
        songPlayer.pausePlayback();
    }
    
    
    @FXML
    private void handleListButtonAction(ActionEvent event) {
        
    }
    
    @FXML
    private void handleSkipButtonAction(ActionEvent event) {
        songPlayer.skipSong();
    }
    
    @FXML
    private void handlePrevButtonAction(ActionEvent event) {
        songPlayer.prevSong();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        songPlayer = new SongPlayer(this);
        playerData = new HashMap<>();
    }
    
    /**
     * Updates the title and artist in the demo interface.
     */
    public void updateInterface() {
        if(songPlayer.getPlayingTrack() != null) {
            if(songPlayer.getPlayingTrack().getIsPaused())
                titleLabel.setText("(pausiert) " + songPlayer.getPlayingTrack().getTitleName());
            else
                titleLabel.setText(songPlayer.getPlayingTrack().getTitleName());
            artistLabel.setText(songPlayer.getPlayingTrack().getArtistName());
            
            Duration dur = songPlayer.getPlayingTrack().getRemainingDuration();
            
            timeLabel.setText(dur.getMinutes() + ":" + dur.getSeconds());
        } else {
            titleLabel.setText("N/A");
            artistLabel.setText("N/A");
        }
    }
}
