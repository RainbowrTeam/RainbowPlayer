package rainbowplayer.db;

import rainbowplayer.Classes.PlaylistEntry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rainbowplayer.Classes.Track;

/**
 * @version STABLE
 * @author Bruno Scheufler
 */
public class EntryFetcher {
    @Deprecated
    private Track t;
    
    private PlaylistEntry pEntry;
    
    /**
     * Construct PlaylistEntry object from entryId
     * @param entryId
     * @return status
     */
    public String retrievePlaylistEntry(String entryId){
        try {
            Database db = new Database();
            
            if(!db.initDB()){
                return "error";
            }
            
            ResultSet result = db.select_query("SELECT * FROM PLAYLIST_ENTRIES WHERE entry_id='" + entryId +"';");
            
            if(result.next() == false){
                //entry not found
                return "entry_not_found";
            }else{
                
                do{
                    String playlistId = result.getString("playlist_id");
                    String trackId = result.getString("track_id");
                    
                    pEntry = new PlaylistEntry(entryId);
                    pEntry.setPlaylistId(playlistId);
                    
                    TrackFetcher tFetch = new TrackFetcher();
                    switch(tFetch.retrieveTrack(trackId)){
                        case "success":
                            pEntry.setTrack(tFetch.getTrack());
                            break;
                        case "not_found":
                            return "track_not_found";
                        case "error":
                        default:
                            return "error";
                    }
                       
                }while(result.next());
                
            }
            return "success";
        } catch (SQLException ex) {
            Logger.getLogger(EntryFetcher.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }
    
        /**
     * Retrieves a track object by a given entryId
     * @deprecated 
     * @param entryId
     * @return status (error,entry_not_found,track_not_found,success)
     */
    public String retrieveTrackByEntryId(String entryId){
        try {
            Database db = new Database();
            if(!db.initDB()){
                return "error";
            }
            
            ResultSet result = db.select_query("SELECT track_id FROM PLAYLIST_ENTRIES WHERE entry_id='" + entryId +"';");
            
            if(result.next() == false){
              return "entry_not_found";  
            }else{
                do{
                    String trackId = result.getString("track_id");
                    TrackFetcher tFetcher = new TrackFetcher();
                    switch(tFetcher.retrieveTrack(trackId)){
                        case "success":
                            t = tFetcher.getTrack();
                            return "success";
                        case "not_found":
                            return "track_not_found";
                        case "error":
                        default:
                            return "error";
                    }
                    
                }while(result.next());
            }
        } catch (SQLException ex) {
            Logger.getLogger(EntryFetcher.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }
    
    /**
     * Returns Track object from PlaylistEntry
     * @return track object
     */
    public Track getTrack(){
        return pEntry.getTrack();
    }
    
    /**
     * Returns PlaylistEntry object
     * @return e
     */
    public PlaylistEntry getEntry(){
        return pEntry;
    }
}
