package rainbowplayer.db;

import rainbowplayer.Classes.Playlist;
import rainbowplayer.Classes.PlaylistEntry;
import rainbowplayer.Classes.Track;
import rainbowplayer.io.TrackImport;

/**
 * @version STABLE
 * @author Bruno Scheufler
 */
public class TestPlayground {
    /**
     * Run several integration tests using the Database and IO functions
     */
    public void run(){
        Database db = new Database();
        System.out.println(db.initDB());
        //STEP 1: INSERT PLAYLIST
        Playlist pll = new Playlist("TestPlayground Test Playlist");
        pll.setDescription("Playlist for testing purposes only.");
        pll.setTags("test");
        
        PlaylistCreation pCreation = new PlaylistCreation();
        System.out.println(pCreation.insertPlaylist(pll));
        
        //STEP 2: GET PLAYLIST
        PlaylistFetcher pFetch = new PlaylistFetcher();
        System.out.println(pFetch.retrievePlaylist(pCreation.getPlaylist().getId()));
        
        //STEP 3: GET ENTRY
        for(PlaylistEntry entry : pFetch.getPlaylist().getEntries()){
            EntryFetcher eFetch = new EntryFetcher();
            System.out.println(eFetch.retrievePlaylistEntry(entry.getEntryId()));
        }
        
        //STEP 4: IMPORT SINGLE TRACK
        TrackImport tImport = new TrackImport();
        System.out.println(tImport.importSingleTrack());
        System.out.println(tImport.importMultipleTracks());
        
        for(String t : tImport.getMultipleTrackIds()){
            System.out.println(t);
        }
        //STEP 5: GET IMPORTED TRACK
        TrackFetcher tFetch = new TrackFetcher();
        System.out.println(tFetch.retrieveTrack(tImport.getSingleTrackId()));
        
        //STEP 6: INSERT ENTRY OF IMPORTED AND FETCHED TRACK
        EntryGeneration eGen = new EntryGeneration();
        System.out.println(eGen.generateEntry(pFetch.getPlaylist(), tFetch.getTrack()));
        
        //STEP 7: REMOVE PLAYLIST ENTRIES
        for(PlaylistEntry entry : pFetch.getPlaylist().getEntries()){
            EntryRemoval eRem = new EntryRemoval();
            System.out.println(eRem.removeEntry(entry.getEntryId()));
        }
        
        //STEP 8: REMOVE PLAYLIST
        PlaylistRemoval pRem = new PlaylistRemoval();
        System.out.println(pRem.removePlaylist(pFetch.getPlaylist().getId()));
        
        TrackFetcher completeTrackFetcher = new TrackFetcher();
        System.out.println(completeTrackFetcher.retrieveAllTracks());
        for(Track t : completeTrackFetcher.getAllTracks()){
            System.out.println(t.getFormattedTitle());
        }
    }
}
