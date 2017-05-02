/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rainbowplayer.Classes.Playlist;
import rainbowplayer.Classes.Track;

/**
 *
 * @author Bruno Scheufler
 * 
 */
public class PlaylistFetcher {
    
    private Playlist playlist;
    
    /**
     * 
     * @param playlistId
     * @return status (error, not_found, success)
     */
    public String retrievePlaylist(String playlistId){
        try {
            Database db = new Database();
            
            if(!db.initDB()){
                return "error";
            }
            
            String query = "SELECT * FROM PLAYLISTS WHERE playlist_id='" + playlistId +"';";
            
            ResultSet result = db.select_query(query);
            
            if (result.next() == false){
                return "not_found";
            }else{ 
                do{
                    String playlistName = result.getString("playlist_id");
                    String playlistDesc = result.getString("playlist_desc");
                    String playlistTags = result.getString("playlist_tags");
                    String playlistEntries = result.getString("playlist_entries");

                    playlist = new Playlist(playlistName);
                    playlist.setDescription(playlistDesc);
                    playlist.setTags(playlistTags);
                    
                    //create array containing playlist entries
                    List<String> entryIds = Arrays.asList(playlistEntries.split(","));
                    
                    EntryFetcher eFetcher = new EntryFetcher();
                    
                    if(entryIds.size() > 0){
                       //loop through entries and fetch data
                       for(String entryId : entryIds){
                           switch(eFetcher.retrieveTrackByEntryId(entryId)){
                               case "success":
                                   playlist.addTrack(eFetcher.getTrack());
                               break; 
                               case "entry_not_found":
                                   System.out.println("Could not add track to playlist because the entry was not found.");
                               break;
                               case "track_not_found":
                                   System.out.println("Could not add track to playlist because the track data could not be found.");
                               break;
                               case "error":
                               default:
                                   System.out.println("An error occurred while trying to add the track to the playlist.");
                               break;
                           }
                       }
                    }else{
                    //empty playlist
                    }
                }while(result.next()); 
                return "success";
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlaylistFetcher.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }
    
    public Playlist getPlaylist(){
        return playlist;
    }
}
