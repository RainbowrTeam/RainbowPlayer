package rainbowplayer.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rainbowplayer.Classes.Playlist;

/**
 * @version STABLE
 * @author Bruno Scheufler
 * 
 */
public class PlaylistFetcher {
    
    private Playlist playlist;
    private ArrayList<Playlist> playlists = new ArrayList<>();
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
            
            String query = "SELECT * FROM PLAYLISTS WHERE playlist_id=?;";
            String data[] = {playlistId};
            
            ResultSet result = db.select_query(query,data);
            
            if (result.next() == false){
                return "playlist_not_found";
            }else{ 
                do{
                    String playlistName = result.getString("playlist_name");
                    String playlistDesc = result.getString("playlist_desc");
                    String playlistTags = result.getString("playlist_tags");
                    String playlistCreation = result.getString("playlist_creation"); //add creation setter to Playlist object

                    playlist = new Playlist(playlistName);
                    playlist.setId(playlistId);
                    playlist.setDescription(playlistDesc);
                    playlist.setTags(playlistTags);
                    
                }while(result.next());
                
                populatePlaylist(playlist);
                return "success";
                
            }
                
            
        } catch (SQLException ex) {
            Logger.getLogger(PlaylistFetcher.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }
    
    /**
     * Retrieve all playlists from database
     * @return status (error, no_playlists_found, success)
     */
    public String retrieveAllPlaylists(){
        try {
            Database db = new Database();
            
            if(!db.initDB()){
                return "error";
            }
            
            String[] data = {};
            String query = "SELECT * FROM PLAYLISTS;";
            
            ResultSet result = db.select_query(query, data);
            
            if(result.next() == false){
                return "no_playlists_found";
            }else{
                do{
                    String playlistId = result.getString("playlist_id");
                    String playlistName = result.getString("playlist_name");
                    String playlistDesc = result.getString("playlist_desc");
                    String playlistTags = result.getString("playlist_tags");
                    String playlistCreation = result.getString("playlist_creation"); //add creation setter to Playlist object
                    long playlistCreationTimeStamp = Long.parseLong(playlistCreation);
                    
                    Calendar playlistCreationDate = Calendar.getInstance();
                    
                    playlistCreationDate.setTimeInMillis(playlistCreationTimeStamp);
                    
                    Playlist p = new Playlist(playlistName);
                    p.setId(playlistId);
                    p.setDescription(playlistDesc);
                    p.setTags(playlistTags);
                    p.setDate(playlistCreationDate);
                    playlists.add(p);
                }while(result.next());
                
                for(Playlist p : playlists){
                    populatePlaylist(p);
                }
                
                return "success";
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlaylistFetcher.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }

    /**
     * Additional step to add entries to the playlist
     * @param p
     * @return status
     */
    public String populatePlaylist(Playlist p){
        try {
            Database db = new Database();
            
            if(!db.initDB()){
                return "error";
            }
            String[] data = {p.getId()};
            ResultSet result = db.select_query("SELECT entry_id FROM PLAYLIST_ENTRIES WHERE playlist_id=?;", data);
            
            if(result.next() == false){
                //No Entries
            }else{
                
                List<String> entryIds = new ArrayList<>();
                do{
                    String entryId = result.getString("entry_id");
                    entryIds.add(entryId); 
                    System.out.println(entryId);
                }while(result.next());
                
                for(String entryId : entryIds){
                    EntryFetcher eFetch = new EntryFetcher();
                    switch(eFetch.retrievePlaylistEntry(entryId)){
                        case "success":
                            p.addEntry(eFetch.getEntry());
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
    
    /**
     * Get Playlist retrieved with retrievePlaylist(playlistId)
     * @return 
     */
    public Playlist getPlaylist(){
        return playlist;
    }
    
    /**
     * Get ArrayList of playlists loaded with retrieveAllPlaylists()
     * @return playlists
     */
    public ArrayList<Playlist> getAllPlaylists(){
        return playlists;
    }
}
