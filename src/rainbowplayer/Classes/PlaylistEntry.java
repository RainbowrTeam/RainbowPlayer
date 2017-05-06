package rainbowplayer.Classes;

/**
 *
 * @author Bruno Scheufler
 */
public class PlaylistEntry {
    private String entryId;
    private String playlistId;
    private Track track;
    
    // Constructors
    
    public PlaylistEntry(String entryId){
        this.entryId = entryId;
    }
    
    public PlaylistEntry(String entryId,String playlistId,Track t){
        this.entryId = entryId;
        this.playlistId = playlistId;
        this.track = t;
    }
    
    public PlaylistEntry(Track t){
        this.track = t;
    }
    
    // Setters 
    
    public void setTrack(Track t){
        this.track = t;
    }
    
    public void setPlaylistId(String playlistId){
        this.playlistId = playlistId;
    }
    
    // Getters
    
    public Track getTrack(){
        return track;
    }
    
    public String getEntryId(){
        return entryId;
    }
    
    public String getPlaylistId(){
        return playlistId;
    }
}
