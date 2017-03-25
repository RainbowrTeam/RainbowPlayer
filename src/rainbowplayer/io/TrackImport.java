package rainbowplayer.io;

import java.io.File;
import javafx.stage.FileChooser;

/**
 *
 * @author bruno.scheufler
 */
public class TrackImport {
    public File track;
    public String openSingleFileChooser(){
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        
        if (selectedFile != null) {
            //selected
            if(getExtension(selectedFile).equalsIgnoreCase("mp3")){
                track = selectedFile;
                return "success";
            }else{
                track = selectedFile;
                return "invalid";
            }
        }else {
            //cancelled
            return "cancelled";
        }
        
        
    }
    
    public File getFile(){
        return track;
    }
    
    public String getExtension(File file) {
        String name = file.getName();
        if(name.lastIndexOf(".") != -1 && name.lastIndexOf(".") != 0){
            return name.substring(name.lastIndexOf(".")+1);
        }else{
            return "";
        }
    }
}