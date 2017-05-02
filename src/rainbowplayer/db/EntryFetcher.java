package rainbowplayer.db;

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
    private Track t;
    
    /**
     * Retrieves a track object by a given entryId
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
    
    public Track getTrack(){
        return t;
    }
}
