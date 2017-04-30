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
        
        String query = "DELETE FROM TRACKS WHERE track_id='" + trackId +"';";
        
        return db.execute_query(query);
    }
}
