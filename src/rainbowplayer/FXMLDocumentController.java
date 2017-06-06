/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer;

import rainbowplayer.Classes.Track;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import rainbowplayer.Classes.Duration;
import rainbowplayer.Classes.Playlist;
import rainbowplayer.Classes.PlaylistEntry;
import rainbowplayer.Core.SongPlayer;
import rainbowplayer.db.Database;

public class FXMLDocumentController implements Initializable {
    
    private SongPlayer songPlayer;
    private Database db;
    
    private HashMap<String, String> playerData = null;
    
    @FXML
    private Button prevButton;
    
    @FXML
    private Label ChildTitleLabel;
    @FXML
    private Label ChildAuthorLabel;
    @FXML private Label ChildAlbumLabel;
  
    @FXML
    private Label ChildRemainTimeLabel;
    @FXML
    private Label ChildTotalTimeLabel;
    @FXML
    private Label ChildCurrentTimeLabel;
    @FXML
    private Label playlistLabel;
    @FXML
    private ListView ChildQueueList; 
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        ArrayList<PlaylistEntry> tQueueNotebook = new ArrayList<>();
        tQueueNotebook.add(new PlaylistEntry(new Track("C:\\Users\\Tim\\Music\\01 - we came to gangbang.mp3", "we came to gangbang", "goreshit")));
        tQueueNotebook.add(new PlaylistEntry(new Track("C:\\Users\\Tim\\Music\\05 - Awg.mp3", "Awg", "Farin Urlaub Racing Team")));
        Playlist notebook = new Playlist("Tims Notebook-Playlist", tQueueNotebook);
        
        ArrayList<PlaylistEntry> tQueueDesktop = new ArrayList<>();
        tQueueDesktop.add(new PlaylistEntry(new Track("D:\\Musik\\Artists\\Crusher-P\\Echo\\echo.mp3", "Echo", "Crusher-P")));
        tQueueDesktop.add(new PlaylistEntry(new Track("D:\\Musik\\Artists\\Fleetwood Mac\\Fleetwood Mac Greatest Hits\\MP3\\12 Fleetwood Mac - Little Lies.mp3", "Little Lies", "Fleetwood Mac")));
        tQueueDesktop.add(new PlaylistEntry(new Track("D:\\Musik\\Artists\\Rammstein\\Mutter\\05 Feuer frei.mp3", "Feuer Frei", "Rammstein")));
        tQueueDesktop.add(new PlaylistEntry(new Track("D:\\Musik\\Artists\\twenty one pilots\\Blurryface\\02 - Stressed Out.mp3", "Stressed Out", "twenty one pilots")));
        Playlist desktop = new Playlist("Tims Desktop-Playlist", tQueueDesktop);
        desktop.setDescription("Das ist eine Beschreibung.");
        desktop.setTags("Pop, Rock, Electro");
        
        // PlaylistExporter pe = (PlaylistExporter) FeatureManager.getInstance().useFeature("PlaylistExporter");
        // pe.savePlaylistFile(desktop, "C:\\Users\\Tim.WEISSHOME\\Desktop\\desktop.rbpls");
        
        // PlaylistImporter pi = (PlaylistImporter) FeatureManager.getInstance().useFeature("PlaylistImporter");
        // Playlist loaded = pi.loadPlaylist("C:\\Users\\Tim.WEISSHOME\\Desktop\\desktop.rbpls");
        
        songPlayer.playPlaylist(desktop);
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
    private void handleEmptyQueueButtonAction(ActionEvent event) {
        
    }
    
     @FXML
    private void handleLoopButtonAction(ActionEvent event) {
        
    } 
    
    @FXML
    private void handleMixButtonAction(ActionEvent event) {
        
    } 
    
    @FXML
    private void handleLooptButtonAction(ActionEvent event) {
        
    }
    
     @FXML
    private void handleAddToQueueButtonAction(ActionEvent event) {
        
    }
    
     @FXML
    private void handlePlayAllQueueButtonAction(ActionEvent event) {
        
    }
    
     @FXML
    private void handleImportTracklistButtonAction(ActionEvent event) {
        
    }
    
     @FXML
    private void handleDeleteTracklistButtonAction(ActionEvent event) {
        
    }
    
     @FXML
    private void handleAddToQueueTracklistButtonAction(ActionEvent event) {
        
    }  
    
    @FXML
    private void handleAddToPlaylistTracklistButtonAction(ActionEvent event) {
        
    }
    
    
    @FXML
    private void handleSkipButtonAction(ActionEvent event) {
        songPlayer.skipSong();
    }
    
    @FXML
    private void handlePrevButtonAction(ActionEvent event) {
        /*prevButton.setGraphic("/uf04a");*/
        songPlayer.prevSong();
    }
   
    public void setListContent(ListView lV, ArrayList list){
	ObservableList oL = FXCollections.observableArrayList(list);
	lV.setItems(oL);
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        songPlayer = new SongPlayer(this);
        playerData = new HashMap<>();
        ArrayList<String>tL = new ArrayList<>();
        tL.add("Hi!");
        setListContent(ChildQueueList,tL);
    }
    
    /**
     * Updates the title and artist in the demo interface.
     */
    public void updateInterface() {
        if(songPlayer.getPlayingTrack() != null) {
            ChildTitleLabel.setText(songPlayer.getPlayingTrack().getTitleName());
            ChildAuthorLabel.setText(songPlayer.getPlayingTrack().getArtistName());
            
            Duration durTotal = songPlayer.getPlayingTrack().getRemainingDuration();
            Duration durRemaining = songPlayer.getPlayingTrack().getRemainingDuration();
            
            ChildRemainTimeLabel.setText(durRemaining.getMinutes() + ":" + durRemaining.getSeconds());
            ChildTotalTimeLabel.setText(durTotal.getMinutes() + ":" + durTotal.getSeconds());
            ChildCurrentTimeLabel.setText(Integer.toString(Integer.parseInt(ChildTotalTimeLabel.getText()) - Integer.parseInt(ChildRemainTimeLabel.getText())));
            
            if(songPlayer.getPlaylist() != null){
                playlistLabel.setText(songPlayer.getPlaylist().getName());
            }
        } else {
            ChildTitleLabel.setText("not available");
            ChildAuthorLabel.setText("not available");
        }
    }
}
