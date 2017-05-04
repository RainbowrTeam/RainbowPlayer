package rainbowplayer.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rainbowplayer.Classes.Playlist;

/**
 * @version UNSTABLE
 * @author Bruno Scheufler
 * 
 */
public class PlaylistFetcher {
    
    private Playlist playlist;
    
    /**
     * Retrieve Playlist object
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
                return "playlist_not_found";
            }else{ 
                do{
                    String playlistName = result.getString("playlist_id");
                    String playlistDesc = result.getString("playlist_desc");
                    String playlistTags = result.getString("playlist_tags");
                    String playlistCreation = result.getString("playlist_creation"); //add creation setter to Playlist object

                    playlist = new Playlist(playlistName);
                    playlist.setId(playlistId);
                    playlist.setDescription(playlistDesc);
                    playlist.setTags(playlistTags);
                    
                }while(result.next());
                
            }
                return "success";
            
        } catch (SQLException ex) {
            Logger.getLogger(PlaylistFetcher.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }

    public String populatePlaylist(){
        try {
            Database db = new Database();
            
            if(!db.initDB()){
                return "error";
            }
            
            ResultSet result = db.select_query("SELECT entry_id FROM PLAYLIST_ENTRIES WHERE playlist_id='" + playlist.getId() + "'");
            List<String> entryIds = new ArrayList<>();
            if(result.next() == false){
                //No Entries
            }else{
                do{
                    entryIds.add(result.getString("entry_id")); 
                }while(result.next());
                    EntryFetcher eFetch = new EntryFetcher();
                    for(String s : entryIds){
                        switch(eFetch.retrievePlaylistEntry(s)){
                            case "success":
                                playlist.addEntry(eFetch.getEntry());
                                break;
                            case "entry_not_found":
                                //Entry not found in database
                            case "track_not_found":
                                //Track not found in database
                            case "error":
                            default:
                                break;
                        }
                    }
            }
            return "success";
        } catch (SQLException ex) {
            Logger.getLogger(PlaylistFetcher.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }
    
    public Playlist getPlaylist(){
        return playlist;
    }
}
