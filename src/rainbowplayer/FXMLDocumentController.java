package rainbowplayer;

import static java.awt.Desktop.getDesktop;
import java.io.File;
import java.io.IOException;
import rainbowplayer.Classes.Track;
import java.net.URL;
import java.text.SimpleDateFormat;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import rainbowplayer.Classes.Duration;
import rainbowplayer.Classes.Playlist;
import rainbowplayer.Classes.PlaylistEntry;
import rainbowplayer.Core.SongPlayer;
import rainbowplayer.db.PlaylistCreation;
import rainbowplayer.db.PlaylistFetcher;
import rainbowplayer.db.TrackFetcher;
import rainbowplayer.db.TrackRemoval;
import rainbowplayer.db.TrackUpdate;
import rainbowplayer.io.MetadataUpdater;
import rainbowplayer.io.TrackImport;

public class FXMLDocumentController implements Initializable {
    
    private SongPlayer songPlayer;
    
    //Track List Data
    private ArrayList<Track> trackList = new ArrayList<>();
    private int trackCount;
    private boolean trackListLoadError;
    private boolean emptyTrackListWarningEmitted = false;
    
    //Playlist List Data
    private ArrayList<Playlist> playlistList = new ArrayList<>();
    private int playlistCount;
    private boolean playlistLoadError = false;
    private boolean emptyPlaylistListWarningEmitted = false;
    
    //Tracklist item deletion 
    private boolean trackDeleteMode = false;
    
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
    private Label ChildPlaylistLabel;
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
    private ListView ChildQueueList;
    @FXML
    private ListView ChildPlaylistList;
    @FXML
    private Label ChildTrackNrQueueLabel;

    @FXML
    private ListView ChildTracklistList;
    
    /**
     * Launch separate timer thread
     */
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
     * Builds a string containing ArrayList content separated by given separator string
     * @param array
     * @param separator
     * @return concatenated string
     */
    public String concatenateArrayListToString(ArrayList<String> array, String separator){
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : array){
            stringBuilder.append(s);
            stringBuilder.append(separator);
        }
        return stringBuilder.toString();
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
                trackListLoadError = false;
                for(Track t : trackListFetcher.getAllTracks()){
                    ArrayList<String> trackInformation = new ArrayList<>();
                    
                    trackInformation.add((trackCount + 1) + ". " + t.getFormattedTitle());
                    trackInformation.add(t.getAlbumName() + " ("+ t.getReleaseYear() +")");
                    
                    trackList.add(t);
                    trackCount++;
                    trackTitles.add(concatenateArrayListToString(trackInformation, "\n"));
                }
                setListContent(ChildTracklistList,trackTitles);
                ChildTrackNrTracklistLabel.setText(trackCount + " Tracks in Tracklist");
                break;
            case "no_tracks_found":
                trackListLoadError = false;
                setListContent(ChildTracklistList, trackTitles);
                ChildTrackNrTracklistLabel.setText("0 Tracks in Tracklist");
                break;
            case "error":
            default:
                trackListLoadError = true;
                showAlert(AlertType.ERROR, "Could not load tracks", "Oops. An error occurred.", "The track loading process failed somehow, do you want to try it again?");
                setListContent(ChildTracklistList,trackTitles);
                break;
        }
    }
    
    /**
     * Refresh/Populate Playlist ListView with data retrieved from database
     */
    private void populatePlaylistList(){
        /*
            Populate Playlist List
        */
        
        PlaylistFetcher playListFetcher = new PlaylistFetcher();
        ArrayList<String> playlistInfo = new ArrayList<>();
        
        playlistList = new ArrayList<>();
        playlistCount = 0;
        
        switch(playListFetcher.retrieveAllPlaylists()){
            case "success":
                playlistLoadError = false;
                
                for(Playlist p : playListFetcher.getAllPlaylists()){
                    
                    ArrayList<String> playlistShortInformation = new ArrayList<>();
                    int playlistEntryCount = 0;
                    
                    for(PlaylistEntry e : p.getEntries()){
                        playlistEntryCount++;
                    }
                    
                    playlistShortInformation.add(p.getName() + " (" + playlistEntryCount +" Tracks)");
                    playlistShortInformation.add(p.getDescription() + " ("+ p.getTags() +")");
                    
                    playlistList.add(p);
                    playlistCount++;
                    playlistInfo.add(concatenateArrayListToString(playlistShortInformation, "\n"));
                }
                
                setListContent(ChildPlaylistList,playlistInfo);
                ChildPlaylistLabel.setText(playlistCount + " Playlists");
                
                break;
            case "no_playlists_found":
                playlistLoadError = false;
                setListContent(ChildPlaylistList, playlistInfo);
                ChildPlaylistLabel.setText("0 Playlists");
                break;
            case "error":
            default:
                playlistLoadError = true;
                showAlert(AlertType.ERROR, "Could not load playlists", "Oops. An error occurred.", "RainbowPlayer could not load your playlists, do you want to try again?");
                setListContent(ChildPlaylistList,playlistInfo);
                break;
        }
    }
    
    /**
     * Handle ListView events
     */
    private void handleListViewEvents(){
        ChildTracklistList.setOnMouseClicked((MouseEvent event) -> {
            try{
                int clickedIndex = ChildTracklistList.getSelectionModel().getSelectedIndex();
                if(clickedIndex <= trackCount){
                    Track clickedTrack = trackList.get(clickedIndex);

                    if(!trackDeleteMode){
                        if(event.getClickCount() == 2){
                            Alert trackInfoAlert = new Alert(AlertType.INFORMATION);
                            trackInfoAlert.setTitle(clickedTrack.getFormattedTitle());
                            trackInfoAlert.setHeaderText(clickedTrack.getTitleName());

                            ArrayList<String> contentText = new ArrayList<>();

                            contentText.add("by " + clickedTrack.getArtistName() + " (" + clickedTrack.getReleaseYear() +")");
                            contentText.add("Album: " + clickedTrack.getAlbumName());
                            contentText.add("Genre: " + clickedTrack.getGenreName());
                            contentText.add("Track ID: " + clickedTrack.getTrackId());
                            contentText.add("Location: " + clickedTrack.getFilePath());

                            trackInfoAlert.setContentText(concatenateArrayListToString(contentText, "\n"));

                            ButtonType openInFileSystemButton = new ButtonType("Open in Explorer", ButtonData.LEFT);
                            ButtonType editTrackButton = new ButtonType("Edit Track");
                            ButtonType closeDialog = new ButtonType("Close", ButtonData.CANCEL_CLOSE);

                            trackInfoAlert.getButtonTypes().setAll(openInFileSystemButton, editTrackButton, closeDialog);

                            Optional<ButtonType> result = trackInfoAlert.showAndWait();
                            if (result.get() == openInFileSystemButton){
                                try{
                                    File trackFile = new File(clickedTrack.getFilePath());
                                    getDesktop().open(trackFile.getParentFile());
                                }catch(IOException e){

                                }

                            } else if(result.get() == editTrackButton){
                                editTrack(clickedTrack);
                            } else {
                                //cancel
                            }
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
            try{
                if(event.getClickCount() == 2){
                    int clickedIndex = ChildPlaylistList.getSelectionModel().getSelectedIndex();
                    if(clickedIndex <= playlistCount){
                        Playlist clickedPlaylist = playlistList.get(clickedIndex);
                        Alert playlistInfoAlert = new Alert(AlertType.INFORMATION);
                        ArrayList<String> contentText = new ArrayList<>();

                        SimpleDateFormat dateDisplayFormat = new SimpleDateFormat("EEEE dd 'of' yyyy kk:mm");
                        String formattedCreationDate = dateDisplayFormat.format(clickedPlaylist.getCreatedAtDate().getTime());

                        playlistInfoAlert.setTitle(clickedPlaylist.getName());
                        playlistInfoAlert.setHeaderText(clickedPlaylist.getName());

                        contentText.add("Description: " + clickedPlaylist.getDescription());
                        contentText.add("Tags: " + clickedPlaylist.getTags());
                        contentText.add("Created " + formattedCreationDate);

                        playlistInfoAlert.setContentText(concatenateArrayListToString(contentText, "\n"));

                        ButtonType closeDialog = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
                        playlistInfoAlert.getButtonTypes().setAll(closeDialog);
                        Optional<ButtonType> result = playlistInfoAlert.showAndWait();

                        if (result.get() == closeDialog){
                            //cancel
                        }
                    }
                }
            }catch(ArrayIndexOutOfBoundsException e){
                
            }
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
            if(trackDeleteMode){
                trackDeleteMode = false;
                ChildDeleteTracklistButton.setText("Delete Tracks");
            }
            
            //Playlists Tab
            if(newTabIndex.intValue() == 1){
                if(!playlistLoadError){
                    if(playlistCount == 0){
                        if(!emptyPlaylistListWarningEmitted){
                            showAlert(AlertType.INFORMATION, "Hi there!", "It's empty here!", "Do you want to create some playlists? Just hit the 'Create Playlist' button!");
                            emptyPlaylistListWarningEmitted = true;
                        }
                    }
                }
            }
            //TrackList Tab
            if(newTabIndex.intValue() == 2){
                if(!trackListLoadError){
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
    
    /**
     * Edit Track
     * @param t 
     */
    private void editTrack(Track t){
        Dialog editTrackDialog = new Dialog();
        editTrackDialog.setTitle("Edit Track");
        editTrackDialog.setHeaderText("Edit Track");
        
        ButtonType submitButtonType = new ButtonType("Apply", ButtonData.OK_DONE);
        editTrackDialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane contentGrid = new GridPane();
        contentGrid.setHgap(2);
        contentGrid.setVgap(2); //spacing between elements
        
        TextField trackName = new TextField();
        TextField trackArtist = new TextField();
        TextField trackAlbum = new TextField();
        TextField trackReleaseYear = new TextField();
        TextField trackGenre = new TextField();
        CheckBox changeFileMetadataCheckbox = new CheckBox();
        
        trackName.setText(t.getTitleName());
        trackAlbum.setText(t.getAlbumName());
        trackArtist.setText(t.getArtistName());
        trackReleaseYear.setText(String.valueOf(t.getReleaseYear()));
        trackGenre.setText(t.getGenreName());
        
        contentGrid.add(new Label("Track Name:"), 0, 0);
        contentGrid.add(trackName, 1, 0);
        contentGrid.add(new Label("Track Album:"), 0, 1);
        contentGrid.add(trackAlbum, 1, 1);
        contentGrid.add(new Label("Track Artist:"), 0, 2);
        contentGrid.add(trackArtist, 1, 2);
        contentGrid.add(new Label("Track Genre:"), 0, 3);
        contentGrid.add(trackGenre, 1, 3);
        contentGrid.add(new Label("Track Release Year:"), 0, 4);
        contentGrid.add(trackReleaseYear, 1, 4);
        contentGrid.add(new Label("Apply changes to file metadata"), 0, 5);
        contentGrid.add(changeFileMetadataCheckbox, 1 , 5);

        editTrackDialog.getDialogPane().setContent(contentGrid);

        Optional result = editTrackDialog.showAndWait();
        if(result.get() == submitButtonType){
            String trackNameValue = trackName.getText();
            String trackAlbumValue = trackAlbum.getText();
            String trackArtistValue = trackArtist.getText();
            String trackReleaseYearValue = trackReleaseYear.getText();
            String trackGenreValue = trackGenre.getText();
            boolean updateFileMetadata = changeFileMetadataCheckbox.isSelected();
            
            Track updatedTrack = new Track(
                    t.getTrackId(),
                    t.getFilePath(),
                    trackNameValue,
                    trackArtistValue,
                    trackAlbumValue,
                    trackGenreValue,Integer.parseInt(trackReleaseYearValue)
            );
            
            TrackUpdate trackUpdate = new TrackUpdate();

            if(trackUpdate.updateTrack(updatedTrack)){
                if(updateFileMetadata){
                    MetadataUpdater metaUpdate = new MetadataUpdater();
                    boolean metaDataUpdateSuccess = false;
                    String metaDataUpdateErrorCode = "none";
                    //Try updating metadata 3 times
                    metaDataUpdateLoop : {
                        for(int attempts = 0; attempts < 3; attempts++){
                            switch(metaUpdate.updateMetadata(updatedTrack)){
                                case "success":
                                    metaDataUpdateSuccess = true;
                                    break;
                                case "tmpfile_delete_error":
                                    metaDataUpdateErrorCode = "tmpfile_del_err";
                                    break;
                                case "rename_error":
                                    metaDataUpdateErrorCode = "rename_err";
                                    break;
                                case "exception":
                                    metaDataUpdateErrorCode = "uncaught_err";
                                default:
                                    metaDataUpdateErrorCode = "err";
                                    break;
                            }
                            break metaDataUpdateLoop;
                        } 
                    }
                    
                    if(metaDataUpdateSuccess){
                        populateTrackList();
                        showAlert(AlertType.INFORMATION, "Track Updated", "Track Updated", "The track was successfully updated.");
                    }else{
                        populateTrackList();
                        showAlert(AlertType.INFORMATION, "Track Updated Failed Partially", "Track Updated With Errors", "The track was successfully updated but the file metadata could not be updated successfully. (ERR_CODE: " + metaDataUpdateErrorCode + ")");
                    }
                }
            }else{
                showAlert(AlertType.ERROR, "Track Update Failed", "Something went wrong", "The track could not be updated. Please try again.");
            }
        }else{
            //Cancel
        }
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
        
    }
    
    @FXML
    private void handlePlayAllQueueButtonAction(ActionEvent event) {
        songPlayer.playTitleQueue(trackQueue);
        startTimer();
    }
    
    @FXML
    private void handleCreatePlaylistButtonAction(ActionEvent event){
        
        Dialog dialog = new Dialog();
        dialog.setTitle("Create Playlist");
        dialog.setHeaderText("Create Playlist");
        
        ButtonType submitButtonType = new ButtonType("Create Playlist", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane contentGrid = new GridPane();
        contentGrid.setHgap(2);
        contentGrid.setVgap(2); //spacing between elements
        
        TextField playlistName = new TextField();
        TextField playlistDescription = new TextField();
        TextField playlistTags = new TextField();

        contentGrid.add(new Label("Playlist Name:"), 0, 0);
        contentGrid.add(playlistName, 1, 0);
        contentGrid.add(new Label("Playlist Description:"), 0, 1);
        contentGrid.add(playlistDescription, 1, 1);
        contentGrid.add(new Label("Playlist Tags:"), 0, 2);
        contentGrid.add(playlistTags, 1, 2);

        dialog.getDialogPane().setContent(contentGrid);

        Optional result = dialog.showAndWait();
        if(result.get() == submitButtonType){
            String playlistNameValue = playlistName.getText();
            String playlistDescValue = playlistDescription.getText();
            String playlistTagValue = playlistTags.getText();
            
            if(playlistNameValue.length() < 1 || playlistDescValue.length() < 1){
                showAlert(AlertType.ERROR, "Invalid Input", "Invalid Input", "Please enter a valid playlist name and description.");
                return;
            }
            
            Playlist p = new Playlist(playlistNameValue);
            
            p.setTags(playlistTagValue);
            p.setDescription(playlistDescValue);
           
            PlaylistCreation pCreation = new PlaylistCreation();
            
            if(pCreation.insertPlaylist(p)){
                populatePlaylistList();
                showAlert(AlertType.INFORMATION, "Success", "Playlist Created!", "Your playlist was successfully created!");
            }else{
                showAlert(AlertType.ERROR, "Error", "Something went wrong.", "RainbowPlayer could not create your playlist, please try again.");
            }
            
        }else{
            //Cancel
        }
    }
    
    @FXML
    private void handleDeletePlaylistButtonAction(ActionEvent event){
        
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
                populateTrackList(); //refresh track list
                showAlert(AlertType.INFORMATION, "Import Successful", "Success!", "The track import was successful!");
                break;
            case "no_selection":
                showAlert(AlertType.WARNING, "Import Failed: No Track Selected", "No Track Selected", "Please select a valid track file and try again.");
                break;
            case "invalid_format":
                showAlert(AlertType.WARNING, "Import Failed: Invalid Track Format", "Invalid Track Format", "RainbowPlayer only supports .mp3 and .wav files.");
                break;
            case "cancelled":
                break;
            case "partial_error":
                populateTrackList(); //refresh track list
                showAlert(AlertType.WARNING, "Import Failed: Partial Failure", "Partial Import Failure", "One or more tracks could not be imported successfully. Please try again.");
                break;
            case "error":
            default:
                showAlert(AlertType.ERROR, "Import Failed", "Something went wrong.", "RainbowPlayer could not import your selected track(s) successfully. Please try again.");
                break;
        }
    }
    
    @FXML
    private void handleDeleteTracklistButtonAction(ActionEvent event) {
        if(trackDeleteMode){
            trackDeleteMode = false;
            ChildDeleteTracklistButton.setText("Delete Tracks");
        }else{
            trackDeleteMode = true;
            ChildDeleteTracklistButton.setText("Exit Deletion Mode");
        }
    }
    
    @FXML
    private void handleAddToQueueTracklistButtonAction(ActionEvent event) {
        addTrackFromTracklistToList(ChildTracklistList, trackQueue);
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
        populatePlaylistList();
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
            
            if(durPlayed.getTotalSeconds() != (int) trackPositionSlider.getMax()){
                trackPositionSlider.setMax(durTotal.getTotalSeconds());
            }
                
            
            if(!trackPositionSlider.isPressed()){
                trackPositionSlider.setValue(durPlayed.getTotalSeconds());
            }
                
            
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
            if(trackPositionSlider.isPressed()){
                songPlayer.seekSong((int) trackPositionSlider.getValue());
            }
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
        if(trackCount > 1){
            labelText = "Tracks";
        } 
        
        ChildTrackNrQueueLabel.setText(trackCount + " " + labelText);
    }
    
    private void addTrackFromTracklistToList(ListView source, ArrayList<PlaylistEntry> tartget) {
        int clickedIndex = ChildTracklistList.getSelectionModel().getSelectedIndex();
            if(clickedIndex <= trackCount){
                Track clickedTrack = trackList.get(clickedIndex);
                PlaylistEntry qTrack = new PlaylistEntry(clickedTrack);

                tartget.add(qTrack);
            }
            
            if(tartget == trackQueue){
                updateQueueList();
            }
    }
    
    private void selectQueueTrack() {
        if(songPlayer.getPlaybackQueue().isEmpty()){
            songPlayer.playTitleQueue(trackQueue);
        }
        
        int clickedIndex = ChildQueueList.getSelectionModel().getSelectedIndex();
        if(clickedIndex <= trackQueue.size() - 1){
            PlaylistEntry clickedTrack = trackQueue.get(clickedIndex);

            if(clickedTrack != null){
                songPlayer.jumpInQueue(clickedIndex + 1);
            }
                
        }
        
        if(songPlayer.isPlaybackActive()){
            startTimer();
        }
            
    }
}
