package rainbowplayer.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * @version STABLE
 * @author Bruno Scheufler
 * 
 */
public class MultipleFileSelection {
    private List<File> trackFiles;
    
    /**
     * Opens file chooser gui to select multiple mp3 files
     * @return status code
     */
    public String selectMultipleFiles(){
        FileChooser fChooser = new FileChooser();
        
        fChooser.getExtensionFilters().add(new ExtensionFilter("MP3 Files (.mp3)","*.mp3"));
        fChooser.getExtensionFilters().add(new ExtensionFilter("WAV Files (.wav)","*.wav"));
        
        List<File> selectedFiles = fChooser.showOpenMultipleDialog(null);
        
        if(selectedFiles == null){
            return "no_selection";
        }
        
        trackFiles = new ArrayList(selectedFiles);
        
        for (Iterator<File> iter = trackFiles.listIterator(); iter.hasNext(); ) {
            File f = iter.next();
            ExtensionUtil exUtil = new ExtensionUtil();
            if(!exUtil.isMp3(f) && !exUtil.isWav(f)){
                iter.remove();
            }
        }
        
        if(trackFiles.size() > 0){
            return "success";
        }else{
            return "invalid_format";
        }
        
    }
    
    /**
     * Returns list of selected files
     * @return trackFiles
     */
    public List<File> getFiles(){
        if(trackFiles != null){
            return trackFiles;
        }else{
            return null;
        }
    }
}
