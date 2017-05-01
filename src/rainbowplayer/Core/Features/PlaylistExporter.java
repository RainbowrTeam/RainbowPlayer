/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.Core.Features;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import rainbowplayer.Classes.Playlist;

/**
 *
 * @author tim
 */
public class PlaylistExporter extends Feature {

    @Override
    public boolean isAvailable() {
        // should only work on tested platforms
        if(System.getProperty("os.name").contains("Windows"))
            return true;
        
        return false;
    }

    @Override
    public String getName() {
        return "PlaylistExporter";
    }
    
    public void savePlaylistFile(Playlist pls, String path){
        if(pls.getTracks() != null){
            Gson gson = new Gson();
            String json = gson.toJson(pls);
            
            try(PrintWriter out = new PrintWriter(path)){
                out.print(json);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PlaylistExporter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
