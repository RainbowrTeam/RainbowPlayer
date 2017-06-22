package rainbowplayer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @version STABLE
 * @author Bruno
 */
public class Database {
    private Connection c = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;
    /**
     * Connects to database or creates new database file
     * @return true/false whether connection was established and database file created if it hadn't existed before
     */
    public boolean connect(){
        try {
          Class.forName("org.sqlite.JDBC");
          c = DriverManager.getConnection("jdbc:sqlite:storage.db");
        } catch ( ClassNotFoundException | SQLException e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          return false;
        }
        return true;
    }
    
    /**
     * Closes database connection
     * @return true/false whether connection was closed
     */
    public boolean closeConnection()
    {
        if(c != null){
            try {
                c.close();
                return true;
            } catch (SQLException ex) {
                System.err.println(ex.getClass().getName() + ": " + ex.getMessage() );
                return false;
            }
        }else{
            return false;
        }
    }
    
    /**
     * Creates default tables in database if they don't exist
     * @return true/false whether connection was created and table(s) were generated if they didn't exist
     */
    public boolean initDB(){
        if(connect()){
            try {
                stmt = c.createStatement();
                
                //TRACKS table
                String sql = "CREATE TABLE IF NOT EXISTS TRACKS" +
                        "(track_id TEXT PRIMARY KEY," +
                        "track_title TEXT," +
                        "track_path TEXT," +
                        "track_artist TEXT, " +
                        "track_album TEXT, " +
                        "track_release_date TEXT, " +
                        "track_duration TEXT," +
                        "track_genre TEXT)";
                stmt.executeUpdate(sql);
                
                //PLAYLISTS table
                sql = "CREATE TABLE IF NOT EXISTS PLAYLISTS" +
                        "(playlist_id TEXT PRIMARY KEY," +
                        "playlist_name TEXT," +
                        "playlist_desc TEXT," +
                        "playlist_tags TEXT, " +
                        "playlist_creation TEXT)";
                stmt.executeUpdate(sql);
                
                //PLAYLIST_ENTRIES table
                sql = "CREATE TABLE IF NOT EXISTS PLAYLIST_ENTRIES" +
                        "(entry_id TEXT PRIMARY KEY," +
                        "playlist_id TEXT," +
                        "track_id TEXT)";
                
                stmt.executeUpdate(sql);
                
                stmt.close();
                
                return closeConnection();
                
            } catch (SQLException ex) {
                System.err.println( ex.getClass().getName() + ": " + ex.getMessage() );
                return false;
            }
        }else{
            return false;
        }
    }
    
    /**
     * Executes INSERT/UPDATE/DELETE/COUNT query 
     * @param query string
     * @param data
     * @return true/fals whether query was successful
     */
    public boolean execute_query(String query, String[] data){
        if(connect()){
            try{
                pstmt = c.prepareStatement(query);
                for(int i = 0; i < data.length; i++){
                    pstmt.setString(i + 1, data[i]);
                }
                pstmt.executeUpdate();
                return closeConnection();
            }catch (SQLException ex){
                System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
                return false;
            }
        }else{
            return false;
        }
    }
    
    /**
     * Executes SELECT query
     * @param query string
     * @param data
     * @return ResultSet
     */
    public ResultSet select_query(String query,String[] data){
        if(!connect()){
            return null;
        }
         
        try {
                pstmt = c.prepareStatement(query);
                for(int i = 0; i < data.length; i++){
                    pstmt.setString(i + 1, data[i]);
                }
                return pstmt.executeQuery();
            } catch (SQLException ex) {
                System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
                return null;
            }
    }
}