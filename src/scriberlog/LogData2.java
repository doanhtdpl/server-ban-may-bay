/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scriberlog;

/**
 *
 * @author LinhTA
 */
//import common.DataCoreContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import scriberlog.core.LogEntry;
import org.eclipse.jetty.util.Loader;

public class LogData2 {

    public static class ActionIdDescription {

        public static String ACTION_HELPFRIEND = "1";
        public static String ACTION_VITSITFRIEND = "2";
               
    }
         
    //login : time , uid , level user 
    public static  String LOGIN = "LOGIN";
    public static String LOG_LOGIN_TEMPLATE = "%s\t%s\t%s";
    
    //new user : time, uid, level user 
    public static  String NEW_USER = "NEW_USER";
    public static String LOG_NEW_USER_TEMPLATE = "%s\t%s\t%s";
    
    //spend money : time, uid ,level user, type money, spend for, sum spend
    public static  String SPEND_MONEY = "SPEND_MONEY";
    public static String LOG_SPEND_MONEY_TEMPLATE = "%s\t%s\t%s\t%s\t%s\t%s";
    
    //earn money : time, uid ,level user, type money, earn from, sum earn
    public static  String EARN_MONEY = "EARN_MONEY";
    public static String LOG_EARN_MONEY_TEMPLATE = "%s\t%s\t%s\t%s\t%s\t%s";
    
    //action game : time, uid ,level user, action id, data
    public static  String ACTION = "ACTION";
    public static String LOG_ACTION_TEMPLATE = "%s\t%s\t%s\t%s\t%s";
    
    public List<LogEntry> logs;

   public LogData2()
   {
       logs = new ArrayList<LogEntry>();
   }

   private void login(String uid)
   {
       String time = Calendar.getInstance(TimeZone.getDefault()).getTime().toString();
       String data = String.format(LOG_LOGIN_TEMPLATE, time,uid);
       LogEntry le = new LogEntry(LOGIN,data );
       
       logs.add(le);
   }

    private void add_newUser(String uid,String level)
   {
       String time = Calendar.getInstance(TimeZone.getDefault()).getTime().toString();
       String data = String.format(LOG_NEW_USER_TEMPLATE, time,uid);
       LogEntry le = new LogEntry(NEW_USER,data );
       
       logs.add(le);
   }
    
    private void add_spendMoney(String uid,String level,String typeMoney,String spendFor, String sumSpend)
    {
         String time = Calendar.getInstance(TimeZone.getDefault()).getTime().toString();
         String data = String.format(LOG_SPEND_MONEY_TEMPLATE, time,uid,level,typeMoney, spendFor, sumSpend);
         
         LogEntry le = new LogEntry(SPEND_MONEY,data );
       
       logs.add(le);
    }
    
    private void add_earnMoney(String uid,String level,String typeMoney,String earnFrom, String sumSpend)
    {
         String time = Calendar.getInstance(TimeZone.getDefault()).getTime().toString();
         String data = String.format(LOG_EARN_MONEY_TEMPLATE, time,uid, level,typeMoney, earnFrom, sumSpend);
    
         LogEntry le = new LogEntry(EARN_MONEY,data );
       
       logs.add(le);
    }
    
    private void add_action(String uid,String level,String actionID, String other)
    {
         String time = Calendar.getInstance(TimeZone.getDefault()).getTime().toString();
        
         String data = String.format(LOG_ACTION_TEMPLATE, time,uid,level,actionID, other);
    
         LogEntry le = new LogEntry(ACTION,data );
       
       logs.add(le);
    }
    
    private void write_End()
    {
        Scribelog2.getInstance().writeLog(logs);
    }
    
}
