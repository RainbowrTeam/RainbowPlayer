/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.Classes;

/**
 *
 * @author Tim Weiß
 */
public class Track {
    // Database model data
    private String filePath;
    private String titleName;
    private String artistName;
    private String albumName;
    private String genreName;
    private int releaseYear;
    int duration;
    
    public Track(String path, String tit, String art){
        this.filePath = path;
        this.titleName = tit;
        this.artistName = art;
    }
    
    public Track(String path, String tit, String art, String album, String genre, int release){
        this.filePath = path;
        this.titleName = tit;
        this.artistName = art;
        this.albumName = album;
        this.genreName = genre;
        this.releaseYear = release;
    }
    
    public String getFilePath(){
        return filePath;
    }
    
    public String getTitleName(){
        return titleName;
    }
    
    public String getArtistName(){
        return artistName;
    }
    
    public String getFormattedTitle(){
        return artistName + " - " + titleName;
    }
    
    public String getAlbumName(){
        return albumName;
    }
    
    public String getGenreName(){
        return genreName;
    }
    
    public int getReleaseYear(){
        return releaseYear;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int seconds) {
        duration = seconds;
    }
}
