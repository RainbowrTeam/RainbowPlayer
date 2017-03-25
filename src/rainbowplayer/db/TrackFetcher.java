/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import rainbowplayer.Classes.Title;

/**
 *
 * @author Bruno
 */
public class TrackFetcher {
    private Title t = null; //rename to Track
    
    /**
     * Fetch track information and create returnable Title/Track object
     * @param trackId
     * @return status
     */
    public boolean retrieveTrack(String trackId){
        try {
            Database db = new Database();
            
            if(!db.initDB()){
                return false;
            }
            
            ResultSet result = db.select_query("SELECT * FROM TRACKS WHERE track_id='" + trackId + "';");
            
            while(result.next()){
                
                String trackPath = result.getString("track_path");
                String trackTitle = result.getString("track_title");
                String trackArtist = result.getString("track_artist");
                
                t = new Title(trackPath,trackTitle,trackArtist);
            }
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            return false;
        }
    }
    
    /**
     * Returns Title/Track object
     * @return Title/Track object
     */
    public Title getTrack(){
       return t;
    }
    
    
    
}
