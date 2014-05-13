/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlDB;

import java.io.File;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.sql.*;
import java.math.*;
import java.util.logging.Level;
import libCore.Config;
import share.ShareMacros;

/**
 *
 * @author Mrkupi
 */
public class MySqlConnectionClient {

    private static Map<String, MySqlConnectionClient> _instances = new NonBlockingHashMap();
    private static final Lock createLock_ = new ReentrantLock();
    private Connection _connection;
    private String _host = "";
    private int _port = 0;
    private String _userName = "";
    private String _password = "";
    private String _database = "";
    private Boolean _auto_Reconnect = true;
    private int _timeout = 0; //500 ms
    private int MAX_ACTIVE = 0;
    private int MAX_IDLE = 0;
    private int MAX_WAIT = 0;

    public static MySqlConnectionClient getInstance(String host, int port) {
        return getInstance(host, port, "", "", "");
    }

    public static MySqlConnectionClient getInstance(String host, int port, String database) {
        return getInstance(host, port, "", "", database);
    }

    public static MySqlConnectionClient getInstance(String host, int port, String userName, String password, String database) {
        String key = host + port + password + database;
        if (!_instances.containsKey(key)) {
            _instances.put(key, new MySqlConnectionClient(host, port, userName, password, database));
        }
        return _instances.get(key);
    }

    public MySqlConnectionClient(String host, int port, String userName, String password, String database) {

        this._host = host;
        this._port = port;
        this._userName = userName;
        this._password = password;
        this._database = database;

        this._auto_Reconnect    =   Boolean.valueOf(Config.getParam(ShareMacros.SQL, ShareMacros.MYSQL_AUTO_RECONNECT ));
        
        getConnection();
    }

    public MySqlConnectionClient(String host, int port, int timeout, String userName, String password, String database) {

        this._host = host;
        this._port = port;
        this._userName = userName;
        this._timeout = timeout;
        this._password = password;
        this._database = database;

        this._auto_Reconnect = Boolean.valueOf(Config.getParam(ShareMacros.SQL, ShareMacros.MYSQL_AUTO_RECONNECT));
        
        getConnection();
    }

    /*
     * return JDBC connection
     */
    public Connection getConnection() {

        if (_connection == null) {
            try {
                String driver = ShareMacros.SQL_DRIVER; // "com.mysql.jdbc.Driver";// "org.gjt.mm.mysql.Driver";
                // load the driver
                Class.forName(driver);
                String dbURL = "jdbc:mysql://" + _host + ":" + _port + "/" + _database;

                java.util.Properties connProperties = new java.util.Properties();
                connProperties.put( ShareMacros.DATABASE_USER, _userName );
                connProperties.put( ShareMacros.DATABASE_PASSWORD, _password );

                connProperties.put( ShareMacros.MYSQL_AUTO_RECONNECT, true );
               
                // Create jdbc connect
               // this._connection = DriverManager.getConnection( dbURL, _userName, _password );                         
                this._connection = DriverManager.getConnection( dbURL, connProperties );
                
            } catch (ClassNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage() + "\n" + ex.toString());
                java.util.logging.Logger.getLogger(MySqlConnectionClient.class.getName()).log(Level.SEVERE, null, ex);
                
                Test_LogCSV.LogCSV.log(" SQL Error : ", ex.getMessage() + "," + ex.toString());
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(MySqlConnectionClient.class.getName()).log(Level.SEVERE, null, ex);
                Test_LogCSV.LogCSV.log(" SQL Error : ", ex.getMessage() + "," + ex.toString());
            }
        }

        return _connection;
    }

    /*
     * Close current JDBC connection in safe
     */
    public void close() {
        try {
            this._connection.close();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(MySqlConnectionClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    

    /*
     * Excute a any command
     */
    public Boolean excute( String cmd ) {
        Boolean result  =   false;
        try {
            
            Statement sttm      =   _connection.createStatement();
            result      =   sttm.execute( cmd );
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(MySqlConnectionClient.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println( "Happen exception: " + ex );
        } finally {
            return result;
        }
    }
    
    /*
     * Excute a query string get data
     */
    public ResultSet excuteQuery( String cmd ) {
        ResultSet rs    =   null;
        try {
            
            Statement sttm  =   _connection.createStatement();
            rs              =   sttm.executeQuery( cmd );
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(MySqlConnectionClient.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println( "Happen exception: " + ex );
        } finally {
            return rs;
        }
    }
    
    /*
     * Excute a update query
     */
    public int excuteUpdate( String cmd ) {
        int result          =   -1;
        try {
            Statement sttm  =   _connection.createStatement();
            
            Test_LogCSV.LogCSV.log("SQL query : ",cmd);            
            result      =   sttm.executeUpdate( cmd );
            Test_LogCSV.LogCSV.log("SQL result : ", String.valueOf(result));
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(MySqlConnectionClient.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println( "Happen exception: " + ex );
            
            Test_LogCSV.LogCSV.log("Error SQL", ex.getMessage()+","+ex);
        } finally {
            Test_LogCSV.LogCSV.log("SQL query finally : ", String.valueOf(result));
            return result;
        }
    }
    
    
    /*
     * create new table in db
     * @tableName : Table Name
     * @colums : List of column in table
     * @types : List type correctly with column
     */
    public int createTable( String tableName, String[] colums, String[] types ) {
        if( colums.length != types.length )
            return -1;
        
        String strQuery =   String.format( "CREATE TABLE IF NOT EXISTS %s (", tableName );
        for( int i = 0; i < colums.length; ++i ) {
            String columContent;
            if( i == colums.length - 1 ) {
                columContent    =   String.format( "%s %s)", colums[i], types[i] );
            } else {
                columContent    =   String.format( "%s %s,", colums[i], types[i] );
            }
            strQuery            =   strQuery + columContent;
        }
        
        return excuteUpdate(strQuery );
    }
    
    /*
     * drop a table from database
     * @tableName : Name of table need to drop
     */
    public int dropTable( String tableName ) {
        String strQuery =   String.format( "DROP TABLE \'s\'", tableName );
        
        return excuteUpdate(strQuery );
    }
    
    /*
     * select all info of table
     * @fromTable : table need to get info
     */
    public ResultSet selectAll( String fromTable ) {
        
        String querryStr=   String.format( "SELECT * FROM %s", fromTable );
        
        return excuteQuery( querryStr );
    }
    
    /*
     * select infos of table
     * @params : list info want to get
     * @fromTable : table need to get info
     */
    public ResultSet selectCmd( String params, String fromTable ) {
        
        String querryStr=   String.format( "SELECT %s FROM %s", params, fromTable );
        
        return excuteQuery( querryStr );
    }
    
    /*
     * select infos of table with condition
     * @params : list info want to get
     * @fromTable : table need to get info
     * @where : a condition
     */
    public ResultSet selectCmdWhere( String params, String fromTable, String where ) {
        
        String querryStr=   String.format( "SELECT %s FROM %s WHERE %s", params, fromTable, where );
        
        return excuteQuery( querryStr );
    }
    
    /*
     * select infos of table with condition
     * @params : list info want to get
     * @fromTable : table need to get info
     * @where : a condition
     * @ex : extension command
     */
    public ResultSet selectCmdWhereEx( String params, String fromTable, String where, String ex ) {
        
        String querryStr=   String.format( "SELECT %s FROM %s WHERE %s %s", params, fromTable, where, ex );
        
        return excuteQuery( querryStr );
    }
    
    /*
     * insert new record to table
     * @toTable : table inserted
     * @values : record
     */
    public int insertTable( String toTable, String values ) {
        
        String strInsert    =   String.format( "INSERT INTO %s VALUES (%s)", toTable, values );
        
        return excuteUpdate( strInsert );
    }
    
    /*
     * insert new record to table
     * @toTable : table inserted
     * @params : list of columns
     * @values : record
     */
    public int insertTable( String toTable, String params, String values ) {
        
        String strInsert    =   String.format( "INSERT INTO %s (%s) VALUES (%s)", toTable, params, values );
        
        return excuteUpdate( strInsert );
    }
    
    /*
     * update record in table
     * @table : table inserted
     * @content : info updated
     * @where : condition update
     */
    public int updateTable( String table, String content, String where ) {
        
        String strQuery =   String.format( "UPDATE %s SET %s WHERE %s", table, content, where );
        return excuteUpdate( strQuery );
    }
    
    /*
     * delete all record from table
     * @table : table droped
     */
    public int deleteAll( String table ) {
        
        String strQuery =   String.format( "DELETE FROM %s", table );
        return excuteUpdate( strQuery );
    }
    
    /*
     * delete all record from table with condition
     * @table : table droped
     * @where : condition
     */
    public int deleteAllWhere( String table, String where ) {
        
        String strQuery =   String.format( "DELETE FROM %s WHERE %s", table, where );
        return excuteUpdate( strQuery );
    }
    
    /*
     * add new column to table
     * @table : table
     * @columName : column add
     * @type : type of column
     */
    public int addColumn( String table, String columName, String type ) {
        
        String strQuery =   String.format( "ALTER TABLE %s ADD COLUMN %s %s", table, columName, type );
        return excuteUpdate( strQuery );
    }
    
    /*
     * delete column from table
     * @table : table
     * @columName : column deleted
     */
    public int deleteColumn( String table, String columName ) {
        
        String strQuery =   String.format( "ALTER TABLE %s DROP COLUMN %s", table, columName );
        return excuteUpdate( strQuery );
    }
    
    /*
     * change name of column
     * @table : table
     * @oldName : a old name of column want to change
     * @newName : a new name of column want to change
     * @type : type of column
     */
    public int changeColumnName( String table, String oldName, String newName, String type ) {
        
        String strQuery =   String.format( "ALTER TABLE %s CHANGE %s %s %s", table, oldName, newName, type );
        return excuteUpdate( strQuery );
    }
    
    
    
    public static void main(String[] args) {
        //Connection conn = MySqlConnectionClient.getInstance("localhost", 8888, "root", "cgk", "cgk_dbptk").getConnection();
        //System.out.println("Result: " + conn);
        
        Connection conn = null;

//        String url = "jdbc:mysql://"+Config.getParam(ShareMacros.SQL, ShareMacros.SQL_HOST)+":"+Config.getParam(ShareMacros.SQL, ShareMacros.SQL_PORT)+"/";
//        String dbName = Config.getParam(ShareMacros.SQL, ShareMacros.SQL_DATABASE);
//        String driver = ShareMacros.SQL_DRIVER;
//        String userName = Config.getParam(ShareMacros.SQL, ShareMacros.SQL_USERNAME);
//        String password = Config.getParam(ShareMacros.SQL, ShareMacros.SQL_PASS);
        
         String url = "jdbc:mysql://dbp.cgkstudio.com:3306/";
        String dbName = "cgk_gift_inbox";
        String driver = ShareMacros.SQL_DRIVER;
        String userName = "root";
        String password = "nochange";
        
        
        java.util.Properties connProperties = new java.util.Properties();
        connProperties.put( share.ShareMacros.DATABASE_USER, userName );
        connProperties.put( share.ShareMacros.DATABASE_PASSWORD, password );

        connProperties.put( share.ShareMacros.MYSQL_AUTO_RECONNECT, true );
        connProperties.put( share.ShareMacros.MYSQL_MAX_RECONNECTS, 4 );
        try {

        Class.forName(driver).newInstance();

        conn = DriverManager.getConnection( url+dbName, connProperties );

        System.out.println("Connected to the database");

        conn.close();

        System.out.println("Disconnected from database");

        } catch (Exception e) {

        System.out.println("NO CONNECTION =(");

        }
        
    }
}
