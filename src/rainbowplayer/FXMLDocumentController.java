package rainbowplayer;

import static java.awt.Desktop.getDesktop;
import java.io.File;
import java.io.IOException;
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
import javafx.scene.control.Slider;
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
    
    // Track Queue Data
    private ArrayList<PlaylistEntry> trackQueue = new ArrayList<>();
    
    // private ArrayList<PlaylistEntry> playlist = new ArrayList<>();
    
   
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
    private Slider volumeSlider;
    @FXML
    private Slider trackPositionSlider;
    
    @FXML
    private Label ChildTrackNrTracklistLabel;
    @FXML
    private Button ChildDeleteTracklistButton;
    @FXML
    private TabPane listTabs;
    
    @FXML
    private Label ChildTrackNrQueueLabel;
    
    @FXML
    private Label ChildPlaylistLabel;
    
    @FXML
    private ListView ChildQueueList;
    @FXML
    private ListView ChildPlaylistList;
    @FXML
    private ListView ChildTracklistList;
    
    private void startTimer(){
        UiWorkerThread myThread = new UiWorkerThread(playerData, 0, songPlayer, this);
        myThread.start();
    }
    
    /**
     * Replace content of ListView with supplied string ArrayList
     * @param lV
     * @param list 
     */
    public void setListContent(ListView lV, ArrayList<String> list){
	ObservableList oL = FXCollections.observableArrayList(list);
	lV.setItems(oL);
    }
    
    /**
     * Refresh/Populate Track List ListView with tracks retrieved from database
     */
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
                ChildTrackNrTracklistLabel.setText("0 Tracks in Tracklist");
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
            try{
                int clickedIndex = ChildTracklistList.getSelectionModel().getSelectedIndex();
                if(clickedIndex <= trackCount){
                    Track clickedTrack = trackList.get(clickedIndex);

                    if(!deleteMode){
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle(clickedTrack.getFormattedTitle());
                        alert.setHeaderText(clickedTrack.getTitleName());
                        alert.setContentText("by " + clickedTrack.getArtistName() + 
                                " \nAlbum: " + 
                                clickedTrack.getAlbumName() + 
                                "\nGenre: " + 
                                clickedTrack.getGenreName() + 
                                "\nTrack ID: " + 
                                clickedTrack.getTrackId());

                        ButtonType openInFileSystemButton = new ButtonType("Open in Explorer");
                        ButtonType closeDialog = new ButtonType("Close", ButtonData.CANCEL_CLOSE);

                        alert.getButtonTypes().setAll(openInFileSystemButton, closeDialog);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == openInFileSystemButton){
                            try{
                                File trackFile = new File(clickedTrack.getFilePath());
                                getDesktop().open(trackFile.getParentFile());
                            }catch(IOException e){
                                
                            }
                           
                        } else {
                            //cancel
                        }
                    }else{
                        TrackRemoval tRemoval = new TrackRemoval();
                        if(tRemoval.removeTrack(clickedTrack.getTrackId())){
                            populateTrackList();
                        }else{
                            showAlert(AlertType.ERROR, "Could not remove track", "An error occurred!", "The track you selected could not be deleted. Please try again!");
                        }
                    }
                }
            }catch(ArrayIndexOutOfBoundsException e){
                
            }
            
        });
        
        ChildPlaylistList.setOnMouseClicked((MouseEvent event) -> {
            //to be added
        });
        
        ChildQueueList.setOnMouseClicked((MouseEvent event) -> {
            selectQueueTrack();
        });
    }
    
    /**
     * EventListener of primary TabPane
     */
    private void handleTabPaneEvents(){
        listTabs.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov, Number oldTabIndex, Number newTabIndex) -> {
            if(deleteMode){
                deleteMode = false;
                ChildDeleteTracklistButton.setText("Delete Track");
            }
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
    
    /**
     * Display JavaFX Alert
     * @param alertType
     * @param alertTitle
     * @param headerText
     * @param contentText 
     */
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
        if(songPlayer.getCurrentTitle() != null)
            songPlayer.pausePlayback();
    }
    
    
    @FXML
    private void handleListButtonAction(ActionEvent event) {
        
    }
    
    @FXML
    private void handleEmptyQueueButtonAction(ActionEvent event) {
        if(!trackQueue.isEmpty())
            trackQueue.clear();
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
        // addTrackFromTracklistToList(ChildPlaylistList, trackQueue);
    }
    
    @FXML
    private void handlePlayAllQueueButtonAction(ActionEvent event) {
        songPlayer.playTitleQueue(trackQueue);
        startTimer();
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
        addTrackFromTracklistToList(ChildTracklistList, trackQueue);
    }  
    
    @FXML
    private void handleAddToPlaylistTracklistButtonAction(ActionEvent event) {
        // addTrackFromTracklistToList(ChildTracklistList, playlist);
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
        bindSliderActions();
    }
    
    /**
     * Updates the title and artist in the demo interface.
     */
    public void updateInterface() {
        if(songPlayer.getPlayingTrack() != null) {
            ChildTitleLabel.setText(songPlayer.getPlayingTrack().getTitleName());
            ChildAuthorLabel.setText(songPlayer.getPlayingTrack().getArtistName());
            ChildAlbumLabel.setText(songPlayer.getPlayingTrack().getAlbumName());
            
            Duration durTotal = songPlayer.getPlayingTrack().getTotalDuration();
            Duration durRemaining = songPlayer.getPlayingTrack().getRemainingDuration();
            
            Duration durPlayed = new Duration();
            durPlayed.setTotalSeconds(durTotal.getTotalSeconds() - durRemaining.getTotalSeconds());
            
            ChildRemainTimeLabel.setText("-" + String.format("%02d", durRemaining.getMinutes()) + ":" + String.format("%02d", durRemaining.getSeconds()));
            ChildTotalTimeLabel.setText(String.format("%02d", durTotal.getMinutes()) + ":" + String.format("%02d", durTotal.getSeconds()));
            ChildCurrentTimeLabel.setText(String.format("%02d", durPlayed.getMinutes()) + ":" + String.format("%02d", durPlayed.getSeconds()));
            
            if(durPlayed.getTotalSeconds() != (int) trackPositionSlider.getMax())
                trackPositionSlider.setMax(durTotal.getTotalSeconds());
            
            if(!trackPositionSlider.isPressed())
                trackPositionSlider.setValue(durPlayed.getTotalSeconds());
            
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
    
    private void bindSliderActions(){
         volumeSlider.valueProperty().addListener((ov) -> {
            songPlayer.changeVolume(volumeSlider.getValue() / 100);
         });
         trackPositionSlider.valueProperty().addListener((ov) -> {
            if(trackPositionSlider.isPressed())
                songPlayer.seekSong((int) trackPositionSlider.getValue());
         });
    }
    
    /**
     * Refresh/Populate 
     */
    private void updateQueueList(){
        ArrayList<String> trackTitles = new ArrayList<>();
        
        for(PlaylistEntry pe : trackQueue){
            trackTitles.add(pe.getTrack().getFormattedTitle());
        }
        setListContent(ChildQueueList,trackTitles);
        
        int trackCount = trackTitles.size();
        String labelText = "Track";
        if(trackCount > 1)
            labelText = "Tracks";
        
        ChildTrackNrQueueLabel.setText(trackCount + " " + labelText);
    }
    
    /**
     * Refresh/Populate 
     
    private void updatePlaylistList(){
        ArrayList<String> trackTitles = new ArrayList<>();
        
        for(PlaylistEntry pe : playlist){
            trackTitles.add(pe.getTrack().getFormattedTitle());
        }
        setListContent(ChildPlaylistList, trackTitles);
        
        int trackCount = trackTitles.size();
        String labelText = "Track";
        if(trackCount > 1)
            labelText = "Tracks";
        
        ChildPlaylistLabel.setText(trackCount + " " + labelText);
    }
    * */
    
    private void addTrackFromTracklistToList(ListView source, ArrayList<PlaylistEntry> tartget) {
        int clickedIndex = ChildTracklistList.getSelectionModel().getSelectedIndex();
            if(clickedIndex <= trackCount){
                Track clickedTrack = trackList.get(clickedIndex);
                PlaylistEntry qTrack = new PlaylistEntry(clickedTrack);

                tartget.add(qTrack);
            }
            
        if(tartget == trackQueue)
            updateQueueList();
        /*if(tartget == playlist)
            updatePlaylistList();*/
    }
    
    private void selectQueueTrack() {
        if(songPlayer.getPlaybackQueue().isEmpty()){
            songPlayer.playTitleQueue(trackQueue);
        }
        
        int clickedIndex = ChildQueueList.getSelectionModel().getSelectedIndex();
        if(clickedIndex <= trackQueue.size() - 1){
            PlaylistEntry clickedTrack = trackQueue.get(clickedIndex);

            if(clickedTrack != null)
                songPlayer.jumpInQueue(clickedIndex + 1);
        }
        
        if(songPlayer.isPlaybackActive())
            startTimer();
    }
}
