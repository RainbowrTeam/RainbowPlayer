package rainbowplayer.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rainbowplayer.Classes.Track;

/**
 * @version STABLE
 * @author Bruno
 */
public class TrackFetcher {
    private Track t = null;
    private ArrayList<Track> tracks = new ArrayList<Track>();
    
    /**
     * Fetch track information and create returnable Track object
     * @param trackId
     * @return status (error, not_found, success)
     */
    public String retrieveTrack(String trackId){
        try {
            Database db = new Database();
            
            if(!db.initDB()){
                return "error";
            }
            
            ResultSet result = db.select_query("SELECT * FROM TRACKS WHERE track_id='" + trackId + "';");
            
            if (result.next() == false){
                return "not_found";
            }else{ 
                do{
                    String trackPath = result.getString("track_path");
                    String trackTitle = result.getString("track_title");
                    String trackArtist = result.getString("track_artist");
                    String trackAlbum = result.getString("track_album");
                    String trackGenre = result.getString("track_genre");

                    int trackReleaseDate = Integer.parseInt(result.getString("track_release_date"));
                    int trackDuration = Integer.parseInt(result.getString("track_duration"));

                    t = new Track(trackId,trackPath,trackTitle,trackArtist,trackAlbum,trackGenre,trackReleaseDate);
                    t.setDuration(trackDuration);
                }while(result.next()); 
                return "success";
            }
        } catch (SQLException ex) {
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            return "error";
        }
    }
    
    public String retrieveAllTracks(){
        try {
            ArrayList<Track> t = new ArrayList<>();
            Database db = new Database();
            
            if(!db.initDB()){
                return "error";
            }
            
            ResultSet result = db.select_query("SELECT * FROM TRACKS;");
            
            if(result.next() == false){
                return "no_tracks_found";
            }else{
                do{
                    String trackId = result.getString("track_id");
                    String trackPath = result.getString("track_path");
                    String trackTitle = result.getString("track_title");
                    String trackArtist = result.getString("track_artist");
                    String trackAlbum = result.getString("track_album");
                    String trackGenre = result.getString("track_genre");

                    int trackReleaseDate = Integer.parseInt(result.getString("track_release_date"));
                    int trackDuration = Integer.parseInt(result.getString("track_duration"));

                    Track track = new Track(trackId,trackPath,trackTitle,trackArtist,trackAlbum,trackGenre,trackReleaseDate);
                    track.setDuration(trackDuration);
                    tracks.add(track);
                }while(result.next());
                return "success";
            }
        } catch (SQLException ex) {
            Logger.getLogger(TrackFetcher.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }
    
    /**
     * Returns Track object
     * @return Track object
     */
    public Track getTrack(){
       return t;
    }
    
    public ArrayList<Track> getAllTracks(){
        return tracks;
    }
    
    
    
}
