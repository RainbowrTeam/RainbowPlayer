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
public class Title {
    private String filePath;
    private String titleName;
    private String artistName;
    int duration;
    
    public Title(String path, String tit, String art){
        this.filePath = path;
        this.titleName = tit;
        this.artistName = art;
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
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int seconds) {
        duration = seconds;
    }
}
