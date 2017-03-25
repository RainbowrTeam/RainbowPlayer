/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer;

import java.io.File;
import rainbowplayer.Classes.Title;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import rainbowplayer.Classes.Duration;
import rainbowplayer.Core.SongPlayer;
import rainbowplayer.db.Database;
import rainbowplayer.db.TrackFetcher;
import rainbowplayer.db.TrackInsertion;
import rainbowplayer.io.TrackImport;

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
        TrackImport tImport = new TrackImport();
        switch(tImport.openSingleFileChooser()){
            case "success":
                File trackFile = tImport.getFile();
                if(trackFile != null){
                        //insert title into db
                        Media tMedia = new Media(trackFile.toURI().toString());
                        MediaPlayer mediaPlayer = new MediaPlayer(tMedia);
                        mediaPlayer.setOnReady(()->{
                            Title track = new Title(trackFile.getPath(),tMedia.getMetadata().get("title").toString(),tMedia.getMetadata().get("artist").toString());
                        
                            TrackInsertion tIns = new TrackInsertion();
                            if(!tIns.insertTrack(track)){
                              //could not insert track into db
                            }
                            
                            //Feedback > returns inserted track
                            TrackFetcher tFetch = new TrackFetcher();
                            if(!tFetch.retrieveTrack(tIns.getTrackId())){
                              //could not retrieve track from db
                            }

                            Title returnedTrack = tFetch.getTrack();
                            System.out.println(returnedTrack.getFormattedTitle());
                        
                        });
                      
                }
            break;
            case "invalid":
                    if(tImport.getFile() != null){
                        System.out.println(tImport.getExtension(tImport.getFile()));
                    }else{
                        System.out.println("null");
                    }
                    System.out.println("Invalid file format. Supported file types are: .mp3 .wav");
            break;
            case "cancelled":
                    System.out.println("No file selected.");
            break;
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
