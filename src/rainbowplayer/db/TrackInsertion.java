/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.db;

import java.util.Date;
import java.util.UUID;
import rainbowplayer.Classes.Title;

/**
 *
 * @author Bruno
 */
public class TrackInsertion {
    private String trackId = null;
    
    /**
     * Insert track into database and generate unique identifier
     * @returns status
     */
    public boolean insertTrack(Title t){
        Database db = new Database();
        
        if(!db.initDB()){
            return false;
        }
        
        String tId = UUID.randomUUID().toString();
        //TODO add track album,track release date,track duration and track genre values
        String query = "INSERT INTO TRACKS"
                + "(track_id,track_title,track_path,track_artist,track_album,track_release_date,track_duration,track_genre) "
                + "VALUES('" + tId + "','" + t.getTitleName() +"','" + t.getFilePath() + "','" + t.getArtistName() +"','sample album','" + new Date().toString() + "','-','unset')";
        
        trackId = tId;
        //execute query
        if(db.execute_query(query)){
           return true; 
        }else{
            return false;
        }
        
    }
    
    /**
     * Return unique track identifier
     * @return track uuid
     */
    public String getTrackId(){
        return trackId;
    }
}
