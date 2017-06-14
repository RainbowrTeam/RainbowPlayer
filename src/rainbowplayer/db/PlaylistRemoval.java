package rainbowplayer.db;

/**
 * @version STABLE
 * @author Bruno Scheufler
 */
public class PlaylistRemoval {
    
    /**
     * Remove playlist from database
     * @param playlistId
     * @return status
     */
    public boolean removePlaylist(String playlistId){
        Database db = new Database();
        
        if(!db.initDB()){
            return false;
        }
        
        String[] data = {playlistId};
        String query = "DELETE FROM PLAYLISTS WHERE playlist_id=?;";
        
        return db.execute_query(query,data);
    }
}
