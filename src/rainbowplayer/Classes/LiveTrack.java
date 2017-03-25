/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.Classes;

/**
 *
 * @author Tim
 */
public class LiveTrack extends Title {
    
    private boolean isPaused;
    private int remainingSeconds;
    
    private Duration remainingDuration;
    
    public LiveTrack(String path, String tit, String art) {
        super(path, tit, art);
        remainingDuration = new Duration();
    }
    
    public void setRemainingSeconds(int sec) {
        remainingDuration.setTotalSeconds(sec);
    }
    
    public Duration getRemainingDuration(){
        return remainingDuration;
    }
    
    public void setIsPaused(boolean paused) {
        isPaused = paused;
    }
    
    public boolean getIsPaused() {
        return isPaused;
    }
}
