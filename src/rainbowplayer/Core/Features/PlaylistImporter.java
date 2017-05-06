/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.Core.Features;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import rainbowplayer.Classes.Playlist;

/**
 *
 * @author tim
 */
public class PlaylistImporter extends Feature {

    @Override
    public boolean isAvailable() {
        // should only work on tested platforms
        if(System.getProperty("os.name").contains("Windows"))
            return true;
        
        return false;
    }

    @Override
    public String getName() {
        return "PlaylistImporter";
    }
    
    public Playlist loadPlaylist(String path){
        File file = new File(path);
        if(file.exists() && !file.isDirectory()) { 
            String json = "";
            String line = null;
            
            // read all content
            try {
                FileReader fileReader = 
                    new FileReader(path);

                BufferedReader bufferedReader = 
                    new BufferedReader(fileReader);

                while((line = bufferedReader.readLine()) != null) {
                    json += line;
                }   

                bufferedReader.close();         
            }
            catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                path + "'");                
            }
            catch(IOException ex) {
                System.out.println(
                    "Error reading file '" 
                    + path + "'");                  
            }
            
            // deserialize playlist
            Gson gson = new Gson();
            Playlist pls = gson.fromJson(json, Playlist.class);
            
            return pls;
        }
        
        return null;
    }
}
