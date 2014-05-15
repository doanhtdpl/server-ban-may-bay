/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DB_MYSQL;

import DB_MYSQL.MySQLClient;
import DB_MYSQL.Utils.DataTable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import libCore.Config;
import libCore.Util;
import share.ShareMacros;

/**
 *
 * @author Mrkupi
 */
public class MySqlConnection_Rd {
    
    public static MySQLClient _connection;
    
    private MySqlConnection_Rd() {
        
         String host         =   Config.getParam(ShareMacros.SQL, ShareMacros.SQL_HOST);
        int port            =   Integer.valueOf(Config.getParam(ShareMacros.SQL,ShareMacros.SQL_PORT ));
        String userName     =   Config.getParam(ShareMacros.SQL, ShareMacros.SQL_USERNAME);
        String password     =   Config.getParam(ShareMacros.SQL, ShareMacros.SQL_PASS);
        String database     =   Config.getParam(ShareMacros.SQL, ShareMacros.SQL_DATABASE);
        _connection         =   MySQLClient.getInstance(host, port, userName, password, database);
    }
    
    private static class MySqlConnection_RdHolder {

        private static final MySqlConnection_Rd INSTANCE = new MySqlConnection_Rd();
    }
    
    public static MySqlConnection_Rd getInstance() {
        return MySqlConnection_Rd.MySqlConnection_RdHolder.INSTANCE;
    }
    
   
    public  DataTable excuteQuery( String strQuery, String... colums ) {
        return DataTable.createDataTable( _connection.excuteQuery(strQuery), colums );
    }
    
    public  DataTable selectAll( String table, String... colums ) {
        return DataTable.createDataTable( _connection.selectAll(table), colums );
    }
    
    public  DataTable selectCmd( String table, String... colums ) {
        String params   =   Util.arrayToString(colums);
        return DataTable.createDataTable( _connection.selectCmd( params, table), colums );
    }
    
    public  DataTable selectCmdWhere( String table, String where, String... colums ) {
        String params   =   Util.arrayToString(colums);
        return DataTable.createDataTable( _connection.selectCmdWhere( params, table, where ), colums );
    }
    
    public DataTable selectAllColumnCmdWhere (String table, String where) {
        try {
            String params   =   "*";
            ResultSet set = _connection.selectCmdWhere( params, table, where );
            
            ResultSetMetaData rsmd = set.getMetaData();
            int count = rsmd.getColumnCount();
            
            String[] columnNames = new String[count];
            
            for (int i = 1; i <= count; i++) {
                String name = rsmd.getColumnName(i);
                columnNames[i-1] = name;
                //System.out.println("Column name: " + name);
            }
            
            return DataTable.createDataTable( set, columnNames );
        } catch (SQLException ex) {
            Logger.getLogger(MySqlConnection_Rd.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public  DataTable selectCmdWhereEx( String table, String where, String ex, String... colums ) {
        String params   =   Util.arrayToString(colums);
        return DataTable.createDataTable( _connection.selectCmdWhereEx( params, table, where, ex ), colums );
    }
    
    // Test mysqlconnection Rd
    public static void main( String[] args ) {
        DataTable dt;//    =   MySqlConnection_Rd.getInstance().selectAll( "inbox", "nFood", "level", "exp" );
//        dt = MySqlConnection_Rd.getInstance().selectAll("inbox");
//       System.out.print(dt.size());
        
        String[] values  =   {
                Util.quote("linhta"),
                Util.quote("test"),
                Util.quote("DBPTK"),
                "" + utilities.time.UtilTime.getTimeNow(),
                Util.quote(ShareMacros.STATUS_NOT_RECEIVED),
                Util.quote("life"),
                "" + 2,
                Util.quote(""),
            };
            int result = MySqlConnection_W.getInstance().insertTable( share.ShareMacros.INBOX_TABLE, share.ShareMacros.INBOX_COLUMNS, values );
            System.out.println( "__________SEND ITEM: " + result );
    }
}
