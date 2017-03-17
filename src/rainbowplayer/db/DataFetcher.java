package rainbowplayer.db;

import java.util.Date;
import rainbowplayer.Classes.Title;

/**
 *
 * @author Bruno
 */
public class DataFetcher {
    /**
     * Insert track into database
     * @returns status
     */
    public boolean insertTrack(Title t){
        Database db = new Database();
        
        //create tables
        if(!db.initDB()){
            return false;
        }
        
        
        if(!db.connect()){
            return false;
        }
        
        //TODO add track album,track release date,track duration and track genre values
        String query = "INSERT INTO TRACKS"
                + "(track_title,track_artist,track_album,track_release_date,track_duration,track_genre) "
                + "VALUES('" + t.getTitleName() +"','" + t.getArtistName() +"','sample album','" + new Date().toString() + "','-','unset')";
        
        //execute query
        if(db.execute_query(query)){
           return true; 
        }else{
            return false;
        }
        
    }
    

}
