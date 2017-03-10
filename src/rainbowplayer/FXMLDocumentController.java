/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer;

import rainbowplayer.Classes.Title;
import java.net.URL;
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
        Title demoSong = new Title("Z:\\Musik\\Artists\\Senpai Club\\senpaiclub-dontgraduatesenpai.mp3", "Don't Graduate", "Satellite Young");
        songPlayer.playTitle(demoSong);
        titleLabel.setText(songPlayer.getCurrentTitle().getTitleName());
        artistLabel.setText(songPlayer.getCurrentTitle().getArtistName());
    }
    
    @FXML
    private void handleStopButtonAction(ActionEvent event){
        songPlayer.stopPlayback();
    }
    
    @FXML
    private void handlePauseButtonAction(ActionEvent event){
        songPlayer.pausePlayback();
    }
    
    
    @FXML
    private void handleListButtonAction(ActionEvent event){
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        songPlayer = new SongPlayer();
    }
}
