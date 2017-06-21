package rainbowplayer.io;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rainbowplayer.Classes.Track;

/**
 *
 * @author Bruno Scheufler
 */
public class MetadataUpdater {
    /**
     * Update track file metadata
     * @param t
     * @return status
     */
    public String updateMetadata(Track t){
        try {
            //Rename file to use old file name later
            File trackFile = new File(t.getFilePath());
            //Create temporary file to use
            File tmpFile = new File(trackFile.getParent() + File.separator + t.getTitleName() + " (updating).mp3");
            
            if(trackFile.renameTo(tmpFile)){
                String originalFilePath = trackFile.getPath();
                String tmpFilePath = tmpFile.getPath();

                //Copies whole track file, the data will be written into the old file path
                Mp3File trackMP3File = new Mp3File(tmpFilePath);

                ID3v2 updatedID3v2Tag = new ID3v24Tag();
                //ID3v1 updatedID3v1Tag = new ID3v1Tag();
                if (trackMP3File.hasId3v1Tag()) {
                    trackMP3File.removeId3v1Tag();
                }
                if (trackMP3File.hasId3v2Tag()) {
                    trackMP3File.removeId3v2Tag();
                }
                if (trackMP3File.hasCustomTag()) {
                    trackMP3File.removeCustomTag();
                }

                updatedID3v2Tag.setTitle(t.getTitleName());
                updatedID3v2Tag.setAlbum(t.getAlbumName());
                updatedID3v2Tag.setArtist(t.getArtistName());
                updatedID3v2Tag.setGenreDescription(t.getGenreName());
                updatedID3v2Tag.setYear(String.valueOf(t.getReleaseYear()));

                trackMP3File.setId3v2Tag(updatedID3v2Tag);

                //Create "original" file in original path with updated metadata
                trackMP3File.save(originalFilePath);
                //Delete updating temporary file
                if(tmpFile.delete()){
                    return "success";
                }else{
                    return "tmpfile_delete_error";
                }
            }
            return "rename_error";
        } catch (IOException | UnsupportedTagException | InvalidDataException | NotSupportedException ex) {
            Logger.getLogger(MetadataUpdater.class.getName()).log(Level.SEVERE, null, ex);
            return "exception";
        }
    }
}
