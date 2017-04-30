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
        
        
        return db.execute_query(query);
    }
}
