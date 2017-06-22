package rainbowplayer.io;

import java.io.File;

/**
 *
 * @author Bruno Scheufler
 */
public class ExtensionUtil {
    /**
     * Check if file extension equals .mp3
     * @param file
     * @return true/false
     */
    public boolean isMp3(File file){
        return getExtension(file).equals("mp3");
    }
    
    /**
     * Check if file extension equals .wav
     * @param file
     * @return true/false
     */
    public boolean isWav(File file){
        return getExtension(file).equals("wav");
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
