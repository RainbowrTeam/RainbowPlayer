package rainbowplayer.db;

/**
 * @version STABLE
 * @author Bruno Scheufler
 */
public class EntryRemoval {
    /**
     * Remove entry from database
     * @param entryId
     * @return status
     */
    public boolean removeEntry(String entryId){
        Database db = new Database();
        
        if(!db.initDB()){
            return false;
        }
        
        String query = "DELETE FROM PLAYLIST_ENTRIES WHERE entry_id=?;";
        String data[] = {entryId};
        
        return db.execute_query(query,data);
    }
}
