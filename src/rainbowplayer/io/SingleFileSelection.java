/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.io;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * @author Bruno
 */
public class SingleFileSelection {
    private File finalFile;
    
    /**
     * Opens file chooser gui to select single mp3 file
     * @return status code
     */
    public String selectSingleFile(){
        FileChooser fChooser = new FileChooser();

        fChooser.getExtensionFilters().add(new ExtensionFilter("MP3 Files (.mp3)","*.mp3"));
        fChooser.getExtensionFilters().add(new ExtensionFilter("WAV Files (.wav)","*.wav"));

        File sFile = fChooser.showOpenDialog(null);
        
        if(sFile == null){
          return "no_selection";
        }
        
        if(!getExtension(sFile).equalsIgnoreCase("mp3")){
          return "invalid_format";
        }
        
        finalFile = sFile;
        return "success";
    }
    
    /**
     * Returns file or null
     * @return file
     */
    public File getFile(){
        if(finalFile != null){
            return finalFile;
        }else{
            return null;
        }
    }
    
    /**
     * Returns extension of given file object
     * @param file
     * @return file extension
     */
    public String getExtension(File file) {
        String name = file.getName();
        if(name.lastIndexOf(".") != -1 && name.lastIndexOf(".") != 0){
            return name.substring(name.lastIndexOf(".")+1);
        }else{
            return "";
        }
    }
}
