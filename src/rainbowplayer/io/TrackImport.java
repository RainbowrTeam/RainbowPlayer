package rainbowplayer.io;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rainbowplayer.Classes.Track;
import rainbowplayer.db.TrackInsertion;

/**
 * @version STABLE
 * @author bruno.scheufler
 */
public class TrackImport {
    /**
     * Import single track file
     * @return status (success,invalid_format,no_selection,error)
     */
    private String sTrackId;
    private List<String> mTrackIds;
    public String importSingleTrack(){
        try {
            SingleFileSelection sFileSelection = new SingleFileSelection();
            switch(sFileSelection.selectSingleFile()){
                case "no_selection":
                    return "no_selection";
                case "invalid_format":
                    return "invalid_format";
                case "success":
                    File selectedFile = sFileSelection.getFile();
                    Mp3File sFileParsed = new Mp3File(sFileSelection.getFile().getPath());
                    
                    String trackTitle;
                    String trackArtist;
                    String trackAlbum;
                    String trackGenre;
                    int trackReleaseYear;
                    int trackDuration = (int)sFileParsed.getLengthInSeconds();
                            
                    if(sFileParsed.hasId3v1Tag()){
                        ID3v1 metadata = sFileParsed.getId3v1Tag();
                        
                        trackTitle = metadata.getTitle();
                        trackArtist = metadata.getArtist();
                        trackAlbum = metadata.getAlbum();
                        trackGenre = metadata.getGenreDescription();
                        
                        if(metadata.getYear() != null){
                            trackReleaseYear = Integer.parseInt(metadata.getYear());
                        }else{
                            Calendar c = Calendar.getInstance();
                            trackReleaseYear = c.get(Calendar.YEAR);
                        }
                           
                    }else if(sFileParsed.hasId3v2Tag()){
                        ID3v2 metadata = sFileParsed.getId3v2Tag();
                        
                        trackTitle = metadata.getTitle();
                        trackArtist = metadata.getArtist();
                        trackAlbum = metadata.getAlbum();
                        trackGenre = metadata.getGenreDescription();
                        
                        if(metadata.getYear() != null){
                            trackReleaseYear = Integer.parseInt(metadata.getYear());
                        }else{
                            Calendar c = Calendar.getInstance();
                            trackReleaseYear = c.get(Calendar.YEAR);
                        }
                        
                    }else{
                        trackTitle = "Unknown";
                        trackArtist = "Unknown Artist";
                        trackAlbum = "Unknown";
                        trackGenre = "";
                        
                        Calendar c = Calendar.getInstance();
                        trackReleaseYear = c.get(Calendar.YEAR);
                    }
                    //TODO copy file to library
                    Track t = new Track(selectedFile.getPath(),trackTitle,trackArtist,trackAlbum,trackGenre,trackReleaseYear);
                    t.setDuration(trackDuration);

                    TrackInsertion tInsertion = new TrackInsertion();
                        
                    if(tInsertion.insertTrack(t)){
                        sTrackId = tInsertion.getTrackId();
                        return "success";
                    }else{
                        return "error";
                    }
                default:
                    return "error";
            }
        } catch (IOException | UnsupportedTagException | InvalidDataException ex) {
            Logger.getLogger(TrackImport.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }
    
    /**
     * Import multiple track files
     * @return status (success,invalid_format,no_selection,error)
     */
    public String importMultipleTracks(){
        mTrackIds = new ArrayList<>();
        MultipleFileSelection mFileSelection = new MultipleFileSelection();
        switch(mFileSelection.selectMultipleFiles()){
            case "no_selection":
                return "no_selection";
            case "invalid_format":
                return "invalid_format";
            case "success":
                List<File> selectedFiles = mFileSelection.getFiles();
                int totalFiles = selectedFiles.size();
                int completedFiles = 0;
                for(File selectedFile : selectedFiles){
                    try {
                        Mp3File sFileParsed = new Mp3File(selectedFile.getPath());

                        String trackTitle;
                        String trackArtist;
                        String trackAlbum;
                        String trackGenre;
                        int trackReleaseYear;
                        int trackDuration = (int)sFileParsed.getLengthInSeconds();

                        if(sFileParsed.hasId3v1Tag()){
                            ID3v1 metadata = sFileParsed.getId3v1Tag();

                            trackTitle = metadata.getTitle();
                            trackArtist = metadata.getArtist();
                            trackAlbum = metadata.getAlbum();
                            trackGenre = metadata.getGenreDescription();

                            if(metadata.getYear() != null){
                                trackReleaseYear = Integer.parseInt(metadata.getYear());
                            }else{
                                Calendar c = Calendar.getInstance();
                                trackReleaseYear = c.get(Calendar.YEAR);
                            }
                            
                        }else if(sFileParsed.hasId3v2Tag()){
                            ID3v2 metadata = sFileParsed.getId3v2Tag();

                            trackTitle = metadata.getTitle();
                            trackArtist = metadata.getArtist();
                            trackAlbum = metadata.getAlbum();
                            trackGenre = metadata.getGenreDescription();

                            if(metadata.getYear() != null){
                                trackReleaseYear = Integer.parseInt(metadata.getYear());
                            }else{
                                Calendar c = Calendar.getInstance();
                                trackReleaseYear = c.get(Calendar.YEAR);
                            }
                            
                        }else{
                            trackTitle = "Unknown";
                            trackArtist = "Unknown Artist";
                            trackAlbum = "Unknown";
                            trackGenre = "";

                            Calendar c = Calendar.getInstance();
                            trackReleaseYear = c.get(Calendar.YEAR);
                        }
                        //TODO copy file to library
                        Track t = new Track(selectedFile.getPath(),trackTitle,trackArtist,trackAlbum,trackGenre,trackReleaseYear);
                        t.setDuration(trackDuration);

                        TrackInsertion tInsertion = new TrackInsertion();

                        if(tInsertion.insertTrack(t)){
                            completedFiles++;
                            mTrackIds.add(tInsertion.getTrackId());
                            break;
                        }else{
                            break;
                        }
                    } catch (IOException | UnsupportedTagException | InvalidDataException ex) {
                        Logger.getLogger(TrackImport.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    }
                }
                if(completedFiles != totalFiles){
                    System.out.println("One or more files have not been imported successfully because of an error.");
                }
                return "success";
            default:
                return "error";
        }
    }
    
    /**
     * Returns track id of imported track
     * @return  sTrackId
     */
    public String getSingleTrackId(){
        return sTrackId;
    }
    
    /**
     * Returns track ids of imported tracks as a list
     * @return  mTrackIds
     */
    public List<String> getMultipleTrackIds(){
        return mTrackIds;
    }
}