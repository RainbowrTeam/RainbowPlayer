/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import rainbowplayer.Core.SongPlayer;

/**
 *
 * @author Tim
 */
public class UiWorkerThread extends Thread {
    private HashMap<String, String> list = null;
    private SongPlayer player = null;
    
    private FXMLDocumentController app = null;
 
    public UiWorkerThread(HashMap<String, String> list, int count, SongPlayer player, FXMLDocumentController app) {
        setDaemon(true);
        setName("Thread " + count);
        this.list = list;
        this.player = player;
        this.app = app;
    }
 
    @Override
    public void run() {
 
        while (!this.isInterrupted()) {
             
            // UI updaten
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                   // Elemente updaten
                   app.updateInterface();
                }
            });
 
            // Thread schlafen
            try {
                // eine Sekunde warten
                sleep(TimeUnit.SECONDS.toMillis(1));
            } catch (InterruptedException ex) {
                Logger.getLogger(UiWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
 
 
    }
}
