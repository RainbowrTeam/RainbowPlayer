package rainbowplayer.db;

/**
 * @version STABLE
 * @author Bruno Scheufler
 */
public class TrackRemoval {
    public boolean removeTrack(String trackId){
        Database db = new Database();
        
        if(!db.initDB()){
            return false;
        }
        
        String[] data = {trackId};
        String query = "DELETE FROM TRACKS WHERE track_id=?;";
        
        return db.execute_query(query,data);
    }
}
