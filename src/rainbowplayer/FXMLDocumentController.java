/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer;

import java.io.File;
import java.io.IOException;
import rainbowplayer.Classes.Title;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import rainbowplayer.Classes.Duration;
import rainbowplayer.Core.SongPlayer;
import rainbowplayer.db.Database;
import rainbowplayer.db.TrackInsertion;
import rainbowplayer.io.SingleFileSelection;

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
    
    @FXML
    private void handleImportButtonAction(ActionEvent event){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Choose an import mode");
        alert.setHeaderText("Choose an import mode");
        alert.setContentText("You can either import a single file or import multiple files.");

        ButtonType button_imp_singleFile = new ButtonType("Import Single File");
        ButtonType button_imp_multiFile = new ButtonType("Import Multiple Files");
        ButtonType button_cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(button_imp_singleFile, button_imp_multiFile, button_cancel);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == button_imp_singleFile){
            
            
            // single file import
            SingleFileSelection sfs = new SingleFileSelection();
            switch(sfs.selectSingleFile()){
                case "success":
                    
                    File fileToImport = sfs.getFile();
                    
                    Media tMedia = new Media(fileToImport.toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(tMedia);
                    
                    tMedia.setOnError(()->{
                        Alert alert_error = new Alert(AlertType.ERROR);
                        alert_error.setTitle("Error");
                        alert_error.setHeaderText(null);
                        alert_error.setContentText("Could not parse file: " + fileToImport.getName() + ".");
                        alert_error.showAndWait(); 
                    });
                    
                    mediaPlayer.setOnReady(() -> {
                        String filePath = "";
                        try {
                            filePath = fileToImport.getCanonicalPath();
                        } catch (IOException ex) {
                            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //TODO add more metadata
                        String mediaTitle = tMedia.getMetadata().get("title").toString();
                        String mediaArtist = tMedia.getMetadata().get("artist").toString();
                        Title t = new Title(filePath,mediaTitle,mediaArtist);
                        TrackInsertion tIns = new TrackInsertion();
                        if(!tIns.insertTrack(t)){
                            //insert failed
                            Alert alert_error = new Alert(AlertType.ERROR);
                            alert_error.setTitle("Error");
                            alert_error.setHeaderText(null);
                            alert_error.setContentText("Could not import file: " + fileToImport.getName() + ".");
                            alert_error.showAndWait();
                        }
                        String trackId = tIns.getTrackId();
                        Alert alert_success = new Alert(AlertType.CONFIRMATION);
                        alert_success.setTitle("Success");
                        alert_success.setHeaderText(null);
                        alert_success.setContentText("The track was successfully imported!");
                        alert_success.showAndWait();
                        //TODO rerender UI
            });
                    
                break;
                case "invalid_format":
                    Alert alert_invalid_format = new Alert(AlertType.ERROR);
                    alert_invalid_format.setTitle("Invalid File Type");
                    alert_invalid_format.setHeaderText(null);
                    alert_invalid_format.setContentText("Please select a valid media file type. The player supports mp3 and wav files.");
                    alert_invalid_format.showAndWait();
                break;
                case "no_selection":
                    Alert alert_no_selection = new Alert(AlertType.WARNING);
                    alert_no_selection.setTitle("Warning");
                    alert_no_selection.setHeaderText(null);
                    alert_no_selection.setContentText("Please select a file you want to import!");
                    alert_no_selection.showAndWait();
                break;
            }
        } else if (result.get() == button_imp_multiFile) {
            // multiple file import
        }
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
