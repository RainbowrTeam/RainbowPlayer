package rainbowplayer.db;

import java.util.UUID;
import rainbowplayer.Classes.Track;

/**
 * @version STABLE
 * @author Bruno Scheufler
 * 
 */
public class TrackInsertion {
    private String trackId = null;
    
    /**
     * Insert track into database and generate unique identifier
     * @param t Track object to insert
     * @return status
     */
    public boolean insertTrack(Track t){
        Database db = new Database();
        
        if(!db.initDB()){
            return false;
        }
        
        String tId = UUID.randomUUID().toString();
        String query = "INSERT INTO TRACKS"
                + "(track_id,"
                + "track_title,"
                + "track_path,"
                + "track_artist,"
                + "track_album,"
                + "track_release_date,"
                + "track_duration,"
                + "track_genre) "
                + "VALUES(?,?,?,?,?,?,?,?);";
        
        String[] data = {
            tId,
            t.getTitleName(),
            t.getFilePath(),
            t.getArtistName(),
            t.getAlbumName(),
            String.valueOf(t.getReleaseYear()),
            String.valueOf(t.getDuration()),
            t.getGenreName()
        };
        trackId = tId;
        //execute query
        return db.execute_query(query, data);
        
    }
    
    /**
     * Return unique track identifier
     * @return track uuid
     */
    public String getTrackId(){
        return trackId;
    }
}
