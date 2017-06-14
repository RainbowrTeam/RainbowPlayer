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
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import rainbowplayer.Classes.Duration;
import rainbowplayer.Classes.Playlist;
import rainbowplayer.Classes.PlaylistEntry;
import rainbowplayer.Core.SongPlayer;
import rainbowplayer.db.TrackFetcher;
import rainbowplayer.db.TrackRemoval;
import rainbowplayer.io.TrackImport;

public class FXMLDocumentController implements Initializable {
    
    private SongPlayer songPlayer;
    
    //Track List Data
    private ArrayList<Track> trackList = new ArrayList<>();
    private int trackCount;
    private boolean trackImportError;
    private boolean emptyTrackListWarningEmitted = false;
    
    //Tracklist item deletion 
    private boolean deleteMode = false;
    
    private HashMap<String, String> playerData = null;
    
   
    @FXML
    private Label ChildTitleLabel;
    @FXML
    private Label ChildAuthorLabel;
    @FXML 
    private Label ChildAlbumLabel;
  
    @FXML
    private Label ChildRemainTimeLabel;
    @FXML
    private Label ChildTotalTimeLabel;
    @FXML
    private Label ChildCurrentTimeLabel;
    @FXML
    private Label playlistLabel;
    
    @FXML
    private Label ChildTrackNrTracklistLabel;
    @FXML
    private Button ChildDeleteTracklistButton;
    @FXML
    private TabPane listTabs;
    
    @FXML
    private final ListView<String> ChildQueueList = new ListView<String>();
    @FXML
    private final ListView<String> ChildPlaylistList = new ListView<String>();
    @FXML
    private ListView ChildTracklistList;
    
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
    
    public void setListContent(ListView lV, ArrayList list){
	ObservableList oL = FXCollections.observableArrayList(list);
	lV.setItems(oL);
    }
    
    private void populateTrackList(){
        /*
            Populate TrackList
        */
        
        TrackFetcher trackListFetcher = new TrackFetcher();
        ArrayList<String> trackTitles = new ArrayList<>();
        trackList = new ArrayList<>();
        trackCount = 0;
        
        switch(trackListFetcher.retrieveAllTracks()){
            case "success":
                trackImportError = false;
                for(Track t : trackListFetcher.getAllTracks()){
                    trackList.add(t);
                    trackTitles.add(t.getFormattedTitle());
                    trackCount++;
                }
                setListContent(ChildTracklistList,trackTitles);
                ChildTrackNrTracklistLabel.setText(trackCount + " Tracks in Tracklist");
                break;
            case "no_tracks_found":
                trackImportError = false;
                setListContent(ChildTracklistList, trackTitles);
                break;
            case "error":
            default:
                trackImportError = true;
                showAlert(AlertType.ERROR, "Could not import tracks", "Oops. An error occurred.", "The track import failed somehow, do you want to try it again?");
                setListContent(ChildTracklistList,trackTitles);
                break;
        }
    }
    
    private void handleListViewEvents(){
        ChildTracklistList.setOnMouseClicked((MouseEvent event) -> {
            int clickedIndex = ChildTracklistList.getSelectionModel().getSelectedIndex();
            Track clickedTrack = trackList.get(clickedIndex);
            
            if(!deleteMode){
                showAlert(AlertType.INFORMATION, clickedTrack.getFormattedTitle(), clickedTrack.getTitleName(), 
                    "by " + clickedTrack.getArtistName() + " \nAlbum: " + clickedTrack.getAlbumName() + "\nGenre: " + clickedTrack.getGenreName() + "\nTrack ID: " + clickedTrack.getTrackId());
            }else{
                TrackRemoval tRemoval = new TrackRemoval();
                if(tRemoval.removeTrack(clickedTrack.getTrackId())){
                    populateTrackList();
                }else{
                    showAlert(AlertType.ERROR, "Could not remove track", "An error occurred!", "The track you selected could not be deleted. Please try again!");
                }
            }
        });
        
        ChildPlaylistList.setOnMouseClicked((MouseEvent event) -> {
            //to be added
        });
        
        ChildQueueList.setOnMouseClicked((MouseEvent event) -> {
            //to be added
        });
    }
    
    private void handleTabPaneEvents(){
        listTabs.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov, Number oldTabIndex, Number newTabIndex) -> {
            //TrackList tab
            if(newTabIndex.intValue() == 2){
                if(!trackImportError){
                    if(trackCount == 0){
                        if(!emptyTrackListWarningEmitted){
                            showAlert(AlertType.INFORMATION, "Hi there!", "It's empty here!", "Do you want to import some tracks? Just hit the 'Import' button!");
                            emptyTrackListWarningEmitted = true;
                        }
                    }
                }
            }
        }); 
    }
    
    private void showAlert(AlertType alertType, String alertTitle, String headerText, String contentText){
        Alert alert = new Alert(alertType);
        alert.setTitle(alertTitle);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
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
        
        TrackImport tImport = new TrackImport();
        String importStatus;
        
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Import Track");
        alert.setHeaderText("Import Track");
        alert.setContentText("You can either import a single track or select multiple tracks to import.");

        ButtonType importSingleTrackButton = new ButtonType("Import Single Track");
        ButtonType importMultiTrackButton = new ButtonType("Import Multiple Tracks");
        ButtonType cancelActionButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(importSingleTrackButton, importMultiTrackButton, cancelActionButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == importSingleTrackButton){
            //Import single track
            importStatus = tImport.importSingleTrack();
        } else if (result.get() == importMultiTrackButton) {
            //Import multiple tracks
            importStatus = tImport.importMultipleTracks();
        } else {
            //Cancel Import
            importStatus = "cancelled";
        }
        
        switch(importStatus){
            case "success":
                showAlert(AlertType.INFORMATION, "Import Successful", "Success!", "The track import was successful!");
                populateTrackList(); //refresh track list
                break;
            case "no_selection":
                showAlert(AlertType.WARNING, "Import Failed: No Track Selected", "No Track Selected", "Please select a valid track file and try again.");
                break;
            case "invalid_format":
                showAlert(AlertType.WARNING, "Import Failed: Invalid Track Format", "Invalid Track Format", "RainbowPlayer only supports .mp3 and .wav files.");
                break;
            case "cancelled":
                break;
            case "error":
            default:
                showAlert(AlertType.ERROR, "Import Failed", "Something went wrong.", "RainbowPlayer could not import your selected track(s) successfully. Please try again.");
                break;
        }
    }
    
     @FXML
    private void handleDeleteTracklistButtonAction(ActionEvent event) {
        if(deleteMode){
            deleteMode = false;
            ChildDeleteTracklistButton.setText("Delete Track");
        }else{
            deleteMode = true;
            ChildDeleteTracklistButton.setText("Exit Delete Mode");
        }
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
    
    @FXML 
    private void handleTracklistMouseClick(MouseEvent event) {
        System.out.println("clicked on " + ChildTracklistList.getSelectionModel().getSelectedItem());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        songPlayer = new SongPlayer(this);
        playerData = new HashMap<>();
 
        populateTrackList();
        handleListViewEvents();
        handleTabPaneEvents();
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
            ChildTitleLabel.setText("Title: not available");
            ChildAuthorLabel.setText("Author: not available");
            ChildAlbumLabel.setText("Album: not available");
            ChildRemainTimeLabel.setText("-00:00:00");
            ChildTotalTimeLabel.setText("00:00:00");
            ChildCurrentTimeLabel.setText("00:00:00");
            
        }
    }
}
