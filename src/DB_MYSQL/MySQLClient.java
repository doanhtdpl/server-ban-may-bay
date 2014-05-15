/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DB_MYSQL;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import share.ShareMacros;

/**
 *
 * @author LinhTA
 */
public class MySQLClient {
   
    private static Logger logger = Logger.getLogger(MySQLClient.class);
    private static Map<String, MySQLClient> _instances = new NonBlockingHashMap();
    private static final Lock createLock_ = new ReentrantLock();
    
    private static BoneCP _SQLpool;
    
    private static int _minConnectionPerPartition = 5;
    private static int _maxConnectionPerPartition = 10;
    private static int _partitions                = 1;
    private static long _timeout                   = 500;
    

    public static MySQLClient getInstance(String host, int port, String database) {
        return getInstance(host, port,"", "", database);
    }

    public static MySQLClient getInstance(String host, int port,String username,String password, String database) {
        String key = host + port +username+ password + database;
        if (!_instances.containsKey(key)) {
            createLock_.lock();
            try {
                if (_instances.get(key) == null) {
                    _instances.put(key, new MySQLClient(host, port,username, password, database));
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instances.get(key);
    }
    
    public MySQLClient(String host, int port,String username, String password, String database)
    {
        String dbURL = "jdbc:mysql://" + host + ":" + port + "/" + database;
       
        try {
			// load the database driver (make sure this is in your classpath!)
			Class.forName(ShareMacros.SQL_DRIVER);
		} 
        catch (Exception e) {
			e.printStackTrace();
			return;
		}
        
        try {
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(dbURL);
            config.setUsername(username);
            config.setPassword(password);
            config.setMinConnectionsPerPartition(_minConnectionPerPartition);
            config.setMaxConnectionsPerPartition(_maxConnectionPerPartition);
            config.setPartitionCount(_partitions);
            config.setConnectionTimeout(_timeout);

            _SQLpool = new BoneCP(config);
        }
        catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
 
    /*
     * Excute a any command
     */
    public Boolean excute( String cmd ) {
        
        Boolean result = false;
        Connection connection = null;
        try {
            connection = _SQLpool.getConnection();
            Statement sttm = connection.createStatement();
            result = sttm.execute(cmd);

        } 
        catch (SQLException ex) {
            logger.error(ex);
            System.out.println("SQLexception: " + ex);
        } 
        finally {
            if (connection != null) 
            {
                try {
                    connection.close();
                } 
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    }
    
    /*
     * Excute a query string get data
     */
    public ResultSet excuteQuery( String cmd ) {
        
        ResultSet result = null;
        Connection connection = null;
        try {
            connection = _SQLpool.getConnection();
            Statement sttm = connection.createStatement();
            result = sttm.executeQuery(cmd);

        } 
        catch (SQLException ex) {
            logger.error(ex);
            System.out.println("SQLexception: " + ex);
        } 
        finally {
            if (connection != null) 
            {
                try {
                    connection.close();
                } 
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    }
    
    /*
     * Excute a update query
     */
    public int excuteUpdate( String cmd ) {
        
        int result = -1;
        Connection connection = null;
        try {
            connection = _SQLpool.getConnection();
            Statement sttm = connection.createStatement();
            result = sttm.executeUpdate(cmd);

        } 
        catch (SQLException ex) {
            logger.error(ex);
            System.out.println("SQLexception: " + ex);
        } 
        finally {
            if (connection != null) 
            {
                try {
                    connection.close();
                } 
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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
    
    
}
