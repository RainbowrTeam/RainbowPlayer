package rainbowplayer.Classes;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple playlist data holding class.
 * @author Tim
 */
public class Playlist {
    private String id;
    private String name;
    private String description;
    private String tags;
    private Calendar createdAt;
    private ArrayList<PlaylistEntry> entries;
    
    // Constructors
    
    public Playlist(String name){
        this.name = name;
        entries = new ArrayList<>();
    }
    
    public Playlist(String name, ArrayList<PlaylistEntry> entries){
        this.name = name;
        this.entries = entries;
    }
    
    // Setters
    
    public void setId(String id){
        this.id = id;
    }
    
    public void setName(String text){
        name = text;
    }
    
    public void setDescription(String text){
        description = text;
    }
    
    public void setTags(String text){
        tags = text;
    }
    
    public void addEntry(PlaylistEntry entry){
        entries.add(entry);
    }
    
    public void setDate(Calendar date){
        createdAt = date;
    }
    
    // Getters
    
    public String getId(){
        return id;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDescription(){
        return description;
    }
    
    public String getTags(){
        return tags;
    }

    public Calendar getCreatedAtDate(){
        return createdAt;
    }
    
    public ArrayList<PlaylistEntry> getEntries(){
        return entries;
    }
}
