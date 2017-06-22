package rainbowplayer.db;

import rainbowplayer.Classes.Track;

/**
 * @version STABLE
 * @author Bruno Scheufler
 * 
 */
public class TrackUpdate {
    /**
     * Update track in database
     * @param t
     * @return status
     */
    public boolean updateTrack(Track t){
        Database db = new Database();
        
        if(!db.initDB()){
            return false;
        }
        
        String[] data = {
            t.getTitleName(),
            t.getArtistName(),
            t.getAlbumName(),
            String.valueOf(t.getReleaseYear()),
            t.getGenreName(),
            t.getTrackId()
        };
        
        String query = "UPDATE TRACKS SET track_title=?, track_artist=?, track_album=?, track_release_date=?, track_genre=? WHERE track_id=?;";
        
        return db.execute_query(query, data);
    }
}
