package rainbowplayer.db;

import java.util.UUID;
import rainbowplayer.Classes.Playlist;
import rainbowplayer.Classes.PlaylistEntry;
import rainbowplayer.Classes.Track;

/**
 * @version STABLE
 * @author Bruno Scheufler
 */
public class EntryGeneration {
    private PlaylistEntry pEntry;
    
    /**
     * Generates PlaylistEntry object and inserts it into the database
     * @param p
     * @param t
     * @return status
     */
    public boolean generateEntry(Playlist p, Track t){
        Database db = new Database();
        
        if(!db.initDB()){
            return false;
        }
        
        String entryId = UUID.randomUUID().toString();
        String playlistId = p.getId();
        
        pEntry = new PlaylistEntry(entryId,playlistId,t);
        
        String query = "INSERT INTO PLAYLIST_ENTRIES"
                + "(entry_id,"
                + "playlist_id,"
                + "track_id) "
                + "VALUES('" + pEntry.getEntryId() + "',"
                + "'" + pEntry.getPlaylistId() +"',"
                + "'" + pEntry.getTrack().getTrackId() + "')";
        
        return db.execute_query(query);  
    }
    
    /**
     * Return PlaylistEntry object
     * @return pEntry
     */
    public PlaylistEntry getEntry(){
        return pEntry;
    }
}
