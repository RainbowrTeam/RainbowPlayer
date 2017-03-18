package rainbowplayer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Bruno
 */
public class Database {
    private Connection c = null;
    private Statement stmt = null;
    /**
     * Connects to database or creates new database file
     * @return true/false whether connection was established and database file created if it hadn't existed before
     */
    public boolean connect()
    {
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
                System.err.println( ex.getClass().getName() + ": " + ex.getMessage() );
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
    public boolean initDB()
    {
        if(connect()){
            try {
                stmt = c.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS TRACKS" +
                        "(track_id TEXT PRIMARY KEY NOT NULL," +
                        "track_title TEXT NOT NULL," +
                        "track_path TEXT NOT NULL," +
                        "track_artist TEXT NOT NULL, " +
                        "track_album TEXT NOT NULL, " +
                        "track_release_date TEXT NOT NULL, " +
                        "track_duration TEXT NOT NULL," +
                        "track_genre TEXT NOT NULL)";
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
     * @return true/fals whether query was successful
     */
    public boolean execute_query(String query)
    {
        if(connect()){
            try{
                stmt = c.createStatement();
                stmt.executeUpdate(query);
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
     * @return ResultSet
     */
    public ResultSet select_query(String query){
        if(!connect()){
            return null;
        }
         
        try {
                stmt = c.createStatement();
                return stmt.executeQuery(query);
            } catch (SQLException ex) {
                System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
                return null;
            }
    }
}