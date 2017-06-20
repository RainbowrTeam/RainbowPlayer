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
        SingleFileSelection sFileSelection = new SingleFileSelection();
        switch(sFileSelection.selectSingleFile()){
            case "no_selection":
                return "no_selection";
            case "invalid_format":
                return "invalid_format";
            case "success":
                File selectedFile = sFileSelection.getFile();

                Track t = buildTrackFromFile(selectedFile);
                
                if(t != null){
                    TrackInsertion tInsertion = new TrackInsertion();

                    if(tInsertion.insertTrack(t)){
                        sTrackId = tInsertion.getTrackId();
                        return "success";
                    }else{
                        return "error";
                    }
                }else{
                    return "error";
                }
                
            default:
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
                    Track t = buildTrackFromFile(selectedFile);
                    if(t != null){
                        TrackInsertion tInsertion = new TrackInsertion();

                        if(tInsertion.insertTrack(t)){
                            completedFiles++;
                            mTrackIds.add(tInsertion.getTrackId());
                        }else{
                            System.out.println("Could not import '" + selectedFile.getName() +"'");
                        }
                    }else{
                        System.out.println("Could not build track object for '"+ selectedFile.getName() +"'");
                    }
                     
                }
                if(completedFiles != totalFiles){
                    System.out.println(completedFiles + " out of " + totalFiles + " files imported.");
                    System.out.println("One or more files have not been imported successfully because of an error.");
                    return "partial_error";
                }
                return "success";
            default:
                return "error";
        }
    }
    
    /**
     * Creates a track object with supplied track file
     * @param file
     * @return Track object 
     */
    public Track buildTrackFromFile(File file){
        try {
            Mp3File sFileParsed = new Mp3File(file.getPath());
            
            String trackTitle;
            String trackArtist;
            String trackAlbum;
            String trackGenre;
            int trackReleaseYear;
            int trackDuration = (int)sFileParsed.getLengthInSeconds();
            
            if(sFileParsed.hasId3v1Tag()){
                ID3v1 metadata = sFileParsed.getId3v1Tag();
                
                trackTitle = metadata.getTitle();
                if(trackTitle == null){
                    trackTitle = "<Unknown>";
                }
                
                trackArtist = metadata.getArtist();
                if(trackArtist == null){
                    trackArtist = "<Unknown Artist>";
                }
                
                trackAlbum = metadata.getAlbum();
                if(trackAlbum == null){
                    trackAlbum = "<Unknown Album>";
                }
                
                trackGenre = metadata.getGenreDescription();
                if(trackGenre == null){
                    trackGenre = "<Genre not set>";
                }
                
                if(metadata.getYear() != null){
                    try{
                        trackReleaseYear = Integer.parseInt(metadata.getYear());
                    }catch(NumberFormatException ex){
                        Calendar c = Calendar.getInstance();
                        trackReleaseYear = c.get(Calendar.YEAR);
                    }
                }else{
                    Calendar c = Calendar.getInstance();
                    trackReleaseYear = c.get(Calendar.YEAR);
                }
                
            }else if(sFileParsed.hasId3v2Tag()){
                ID3v2 metadata = sFileParsed.getId3v2Tag();
                
                trackTitle = metadata.getTitle();
                if(trackTitle == null){
                    trackTitle = "<Unknown>";
                }
                
                trackArtist = metadata.getArtist();
                if(trackArtist == null){
                    trackArtist = "<Unknown Artist>";
                }
                
                trackAlbum = metadata.getAlbum();
                if(trackAlbum == null){
                    trackAlbum = "<Unknown Album>";
                }
                
                trackGenre = metadata.getGenreDescription();
                if(trackGenre == null){
                    trackGenre = "<Genre not set>";
                }
                
                if(metadata.getYear() != null){
                    trackReleaseYear = Integer.parseInt(metadata.getYear());
                }else{
                    Calendar c = Calendar.getInstance();
                    trackReleaseYear = c.get(Calendar.YEAR);
                }
                
            }else{
                trackTitle = "<Unknown>";
                trackArtist = "<Unknown Artist>";
                trackAlbum = "<Unknown Album>";
                trackGenre = "<Genre not set>";
                
                Calendar c = Calendar.getInstance();
                trackReleaseYear = c.get(Calendar.YEAR);
            }

            Track t = new Track(file.getPath(),trackTitle,trackArtist,trackAlbum,trackGenre,trackReleaseYear);
            t.setDuration(trackDuration);
            return t;
        } catch (IOException | UnsupportedTagException | InvalidDataException ex) {
            Logger.getLogger(TrackImport.class.getName()).log(Level.SEVERE, null, ex);
            return null;
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