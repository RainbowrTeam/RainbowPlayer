package rainbowplayer.db;
import java.time.Instant;
import java.util.UUID;
import rainbowplayer.Classes.Playlist;

/**
 * @version STABLE
 * @author Bruno Scheufler
 */
public class PlaylistCreation {
    private Playlist playlist;
    
    /**
     * Insert playlist into database and adds playlist id to playlist object
     * @param playlist
     * @return status
     */
    public boolean insertPlaylist(Playlist playlist){
        Database db = new Database();
        
        if(!db.initDB()){
            return false;
        }
        Instant timestamp = Instant.now();
        String pId = UUID.randomUUID().toString();
        
        String query = "INSERT INTO PLAYLISTS(playlist_id,playlist_name,playlist_desc,playlist_tags,playlist_creation) VALUES(?,?,?,?,?);";
        String data[] = {pId, playlist.getName(), playlist.getDescription(), playlist.getTags(), String.valueOf(timestamp.toEpochMilli()) };
        
        playlist.setId(pId);
        
        this.playlist = playlist;
        
        return db.execute_query(query,data);
    }
    
    /**
     * Return playlist object
     * @return playlist
     */
    public Playlist getPlaylist(){
        return playlist;
    }
}
