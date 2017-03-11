/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer;

import rainbowplayer.Classes.Title;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import rainbowplayer.Core.SongPlayer;
import rainbowplayer.db.Database;

public class FXMLDocumentController implements Initializable {
    
    private SongPlayer songPlayer;
    private Database db;
    
    @FXML
    private Label titleLabel;
    @FXML
    private Label artistLabel;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        ArrayList<Title> tQueue = new ArrayList<>();
        tQueue.add(new Title("Z:\\Musik\\Artists\\Senpai Club\\senpaiclub-dontgraduatesenpai.mp3", "Don't Graduate", "Satellite Young"));
        tQueue.add(new Title("Z:\\Musik\\Artists\\twenty one pilots\\Blurryface\\02 - Stressed Out.mp3", "Stressed Out", "twenty one pilots"));
        tQueue.add(new Title("Z:\\Musik\\Artists\\Crusher-P\\Echo\\echo.mp3", "Echo", "Crusher-P"));
        tQueue.add(new Title("Z:\\Musik\\Artists\\Fleetwood Mac\\Fleetwood Mac Greatest Hits\\MP3\\12 Fleetwood Mac - Little Lies.mp3", "Little Lies", "Fleetwood Mac"));
        tQueue.add(new Title("Z:\\Musik\\Artists\\Rammstein\\Mutter\\05 Feuer frei.mp3", "Feuer Frei", "Rammstein"));
        
        songPlayer.playTitleQueue(tQueue);
        updateInterface();
    }
    
    @FXML
    private void handleStopButtonAction(ActionEvent event) {
        songPlayer.stopPlayback();
        updateInterface();
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
        updateInterface();
    }
    
    @FXML
    private void handlePrevButtonAction(ActionEvent event) {
        songPlayer.prevSong();
        updateInterface();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        songPlayer = new SongPlayer();
    }
    
    /**
     * Updates the title and artist in the demo interface.
     */
    private void updateInterface() {
        if(songPlayer.getCurrentTitle() != null) {
            titleLabel.setText(songPlayer.getCurrentTitle().getTitleName());
            artistLabel.setText(songPlayer.getCurrentTitle().getArtistName());
        } else {
            titleLabel.setText("N/A");
            artistLabel.setText("N/A");
        }
    }
}
