package rainbowplayer.io;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * @version STABLE
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
        ExtensionUtil exUtil = new ExtensionUtil();
            if(exUtil.isMp3(sFile) || exUtil.isWav(sFile)){
                finalFile = sFile;
                return "success";
            }else{
                return "invalid_format";
            }
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
}
