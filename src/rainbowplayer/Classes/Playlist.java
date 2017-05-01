/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.Classes;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple playlist data holding class.
 * @author Tim
 */
public class Playlist {
    private String name;
    private String description;
    private String tags;
    private Date createdAt;
    private ArrayList<Track> tracks;
    
    // Constructors
    
    public Playlist(String name){
        this.name = name;
        tracks = new ArrayList<>();
    }
    
    public Playlist(String name, ArrayList<Track> tracks){
        this.name = name;
        this.tracks = tracks;
    }
    
    // Setters
    
    public void setName(String text){
        name = text;
    }
    
    public void setDescription(String text){
        description = text;
    }
    
    public void setTags(String text){
        tags = text;
    }
    
    public void setDate(Date date){
        createdAt = date;
    }
    
    public void addTrack(Track track){
        tracks.add(track);
    }
    
    // Getters
    
    public String getName(){
        return name;
    }
    
    public String getDescription(){
        return description;
    }
    
    public String getTags(){
        return tags;
    }

    public Date getCreatedAtDate(){
        return createdAt;
    }
    
    public ArrayList<Track> getTracks(){
        return tracks;
    }
}
