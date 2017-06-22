/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.Classes;

/**
 * A simple class for timespans and durations.
 * @author Tim
 */
public class Duration {
    private int totalSeconds;
    
    // Setters
    
    /**
     * Sets the timespan according to the seconds passed.
     * @param seconds the seconds
     */
    public void setTotalSeconds(int seconds){
        totalSeconds = seconds;
    }
    
    /**
     * Sets the timespan according to the minutes passed.
     * @param minutes the minutes
     */
    public void setNewFromMinutes(int minutes){
        totalSeconds = minutes * 60;
    }
    
    /**
     * Sets the timespan according to the hours passed.
     * @param hours the hours
     */
    public void setNewFromHours(int hours){
        totalSeconds = hours * (60 * 60);
    }
    
    // Getters
    
    /**
     * Gets the seconds with minutes removed.
     * @return 
     */
    public int getSeconds(){
        int minutes = totalSeconds/60;
        return totalSeconds - minutes*60;
    }
    
    /**
     * Gets only the minutes.
     * @return 
     */
    public int getMinutes(){
        return totalSeconds/60;
    }
    
    /**
     * Gets only the hours (if any).
     * @return 
     */
    public int getHours(){
        return totalSeconds/60/60;
    }
    
    public int getTotalSeconds() {
        return totalSeconds;
    }
}
