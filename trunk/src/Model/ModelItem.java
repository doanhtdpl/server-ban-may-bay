/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import Security.Scr_Base64;
import SqlDB.MySqlConnection_Rd;
import SqlDB.Utils.DataRow;
import SqlDB.Utils.DataTable;
import db.Redis_Rd;
import db.Redis_W;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import libCore.Config;
import libCore.Util;
import org.json.simple.JSONObject;
import share.KeysDefinition;
import share.ShareMacros;

/**
 *
 * @author LinhTA
 */
public class ModelItem {
    
    public static String getCountItem(String uid,String type)
      {
            String key = KeysDefinition.getKeyItem(uid, type);
             String ret = null;
             try
             {
                 ret = Redis_Rd.getInstance().getRetries(key);
             }
             catch(Exception e)
             {
                 
             }
             if(ret == null)
             {
                 String count = "0";
                 try
                {
                    count = libCore.Config.getParam(ShareMacros.DEFAULTITEM, type);
                }
                catch(Exception e)
                {
                    count = "0";
                }
                 
                 if(count == null)
                     count = "0";
                 
                 long ret2 =-1;
                try
                {
                    Test_LogCSV.LogCSV.log("count , type", count+","+type);
                    ret2= Redis_W.getInstance().setRetries(key, count);
                }
                catch(Exception e)
                {
                    Test_LogCSV.LogCSV.log("Error",e.getMessage() );
                }
                
                if(ret2 == -1)
                {
                    return null;
                }
                else
                {
                    return count;
                }

            }
             else
             {
                 return ret;
             }
      }
    
    public static Map<String,String> getLifeVSTime(String uid, HttpServletResponse resp)
     {
         Map<String,String> data = new HashMap<String,String>();
         
         int lifes = 0;
         
         int lifeDB = getCountLifeDB(uid);
         long timeOld = getTimeLifeDB(uid)  ;      
         
         long timeNow = utilities.time.UtilTime.getTimeNow();
         long timeAddLife = Long.parseLong(libCore.Config.getParam(ShareMacros.COUNTDOWN, ShareMacros.LIFE));
         int lifeMax = Integer.parseInt(libCore.Config.getParam(ShareMacros.MAX, ShareMacros.LIFE));
         
         int lifeAdd = Integer.parseInt(String.valueOf((timeNow- timeOld)/timeAddLife));
         int life = lifeDB + lifeAdd;
         if(life > lifeMax )
         {
             data.put(ShareMacros.LIFE,String.valueOf( lifeMax));
             data.put(ShareMacros.LASTTIME, String.valueOf(timeNow));
         }
         else
         {
             data.put(ShareMacros.LIFE, String.valueOf(life));
             long time =timeOld + (lifeAdd * timeAddLife);
             data.put(ShareMacros.LASTTIME, String.valueOf(time));
             
         }
         
         String ret = Redis_W.getInstance().hsetRetries(uid,ShareMacros.LIFE, String.valueOf(data.get(ShareMacros.LIFE)));
        String ret2 = Redis_W.getInstance().hsetRetries(uid,ShareMacros.LASTTIME, String.valueOf(data.get(ShareMacros.LASTTIME)));
         
        if(ret == null || ret2 == null)
        {
            data.put(ShareMacros.LASTTIME, "-1");
            data.put(ShareMacros.LIFE, "-1");
        }
        
         return data;
     }
    
    public static boolean addLife(String uid)
     {
         Map<String,String> data = new HashMap<String,String>();
         
         int lifes = 0;
         
         int lifeDB = getCountLifeDB(uid);
         
         
         int lifeMax = Integer.parseInt(libCore.Config.getParam(ShareMacros.MAX, ShareMacros.LIFE));
         
         
         int life = lifeDB + 1;
         if(life > lifeMax )
         {
            return false;
         }
         
         String ret = Redis_W.getInstance().hsetRetries(uid,ShareMacros.LIFE,String.valueOf(life));
         
        if(ret == null )
        {
            return false;
        }
        
         return true;
     }
    
    private static long getTimeLifeDB(String uid)
     {
         String ret = null;
             try
             {
                 ret = Redis_Rd.getInstance().hgetRetries(uid,ShareMacros.LASTTIME);
             }
             catch(Exception e)
             {
                 
             }
             if(ret == null)
             {
                 String time = "0";
               
                 String ret2 = null;
                try
                {
                    ret2= Redis_W.getInstance().hsetRetries(uid,ShareMacros.LASTTIME, time);
                }
                catch(Exception e)
                {
                    
                }
                
                if(ret2 == null)
                {
                    return -1;
                }
                else
                {
                    return Long.parseLong(time);
                }

            }
             else
             {
                 return  Long.parseLong(ret);
             }
     }
     
     private static int getCountLifeDB(String uid)
     {
         int life =0;
         
          String ret = null;
             try
             {
                 ret = Redis_Rd.getInstance().hgetRetries(uid,ShareMacros.LIFE);
             }
             catch(Exception e)
             {
                 
             }
             if(ret == null)
             {
                 String count = "0";
                 try
                {
                    count = libCore.Config.getParam(ShareMacros.DEFAULTUSER, ShareMacros.LIFE);
                }
                catch(Exception e)
                {
                    count = "0";
                }
                 
                 String ret2 = null;
                try
                {
                    ret2= Redis_W.getInstance().hsetRetries(uid,ShareMacros.LIFE, count);
                }
                catch(Exception e)
                {
                    
                }
                
                if(ret2 == null)
                {
                    return -1;
                }
                else
                {
                    return Integer.parseInt(count);
                }

            }
             else
             {
                 return  Integer.parseInt(ret);
             }
     }
    
     public static JSONObject defaultResponse_False()
    {
        JSONObject data = new JSONObject();
        
        data.put(ShareMacros.SUSSCES, "false");
        data.put(ShareMacros.TIME, String.valueOf(utilities.time.UtilTime.getTimeNow()));
        data.put(ShareMacros.LIFE, "-1");
        data.put(ShareMacros.Coin, "-1");
        
        return  data;
    }
       
       public static void outFalse(HttpServletResponse resp)
       {
           JSONObject mapjson = new JSONObject();
            mapjson = defaultResponse_False();
            out(mapjson.toJSONString(), resp);
       }
    
        public static void out(String content, HttpServletResponse respon) {

        PrintWriter out = null;
        try {
            out = respon.getWriter();
            out.print(Scr_Base64.Encode(content));
            out.close();
        } catch (Exception ex) {
            //logger_.error("CampainAction.out:" + ex.getMessage(), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
        
        public static boolean checkAddItem(String uid,String item)
        {
            if(item.equals(ShareMacros.LIFE))
               return checkAddLife(uid);
            
             int max = 0;
           try
           {
               max = Integer.valueOf( libCore.Config.getParam(ShareMacros.MAX, item));
           }
           catch(Exception e)
           {}
              String countStr = ModelItem.getCountItem(uid, item);
         int countIt = 0;
         if(countStr != null)
             countIt = Integer.parseInt(countStr);
         else
         {
             return false;
         }
         if(max != 0 && countIt >= max )
         {
             return false;
         }
            
            return true;
        }
        
        public static boolean checkAddLife(String uid)
        {
            int lifes = 0;
         
         int lifeDB = getCountLifeDB(uid);
         
         
         int lifeMax = Integer.parseInt(libCore.Config.getParam(ShareMacros.MAX, ShareMacros.LIFE));
         
         
         int life = lifeDB + 1;
         if(life > lifeMax )
         {
            return false;
         }
         
         return true;
        }
        
        public static boolean addItem(String uid,String item)
        {
           if(item.equals(ShareMacros.LIFE))
               return addLife(uid);
            
            boolean ret = false;
            
            Map<String,String> data = new HashMap<String,String>();
            
            int max = 0;
           try
           {
               max = Integer.valueOf( libCore.Config.getParam(ShareMacros.MAX, item));
           }
           catch(Exception e)
           {}
           
         String countStr = ModelItem.getCountItem(uid, item);
         int countIt = 0;
         if(countStr != null)
             countIt = Integer.parseInt(countStr);
         else
         {
             return false;
         }
         if(max != 0 && countIt >= max )
         {
             return false;
         }
         else
         {
          
           
            int count = 0;
            count = countIt + 1;
           
            String key = KeysDefinition.getKeyItem(uid, item);
            long ret2 = Redis_W.getInstance().setRetries(key, String.valueOf(count));
            if(ret2 == -1)
            {
                
                return false;
            }

                return true;
            
         }
          
        
        }
        
        public static Map<String,String> getTimeSendLife (String uid,String appID)
        {
            Map<String,String> data = new HashMap<String,String>();
            
            
             long timeMin = 0;
            try
            {
                String t = Config.getParam(ShareMacros.INBOX_MIN, ShareMacros.LIFE);
                timeMin = Long.parseLong(t);
            }
            catch(Exception e)
            {

            }
        
        long timeNow = utilities.time.UtilTime.getTimeNow();
        
        long time = timeNow - timeMin;
        
        String where = "";
        where += ShareMacros.SENDER_ID_COL;
            where +=" = ";
            where += Util.quote(uid);    
            where += " AND ";
        where +=ShareMacros.APP_ID_COL;
            where +=" = ";
            where +=Util.quote(appID);    
            where +=" AND ";
        where +=ShareMacros.TIME_COL;
            where +=" > ";
            where +=Util.quote(String.valueOf(time));
            
         DataTable re = MySqlConnection_Rd.getInstance().selectCmdWhere(ShareMacros.INBOX_TABLE,where, ShareMacros.RECEIVER_ID_COL,ShareMacros.TIME_COL);    
         
            for (DataRow dataRow : re) {
                data.put(dataRow.get(ShareMacros.RECEIVER_ID_COL), dataRow.get(ShareMacros.TIME_COL));
            }
            
            return data;
        }
}
