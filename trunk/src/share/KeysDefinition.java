/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author LinhTA
 */
public class KeysDefinition {

    static String LIST_FRIENDS = "F_";
    static String LIST_DEVICE = "D_";
    
    static  String USER_FB = "Fb_";
    static  String USER_ME = "ME_";
    static  String USERS = "Users";
    static  String USERME = "UserMEs";
    static  String USERFB = "UserFBs";
    static  String USERSCORES = "UserScores";
    static  String APPKEYS = "KeyApps";
    
    public static String getAppKey()
    {
        return APPKEYS;
    }
    
    public static byte[] ramdomKey()
    {
        byte[] id= null;
        
        UUID uuid = UUID.randomUUID();
        long longOne = uuid.getMostSignificantBits();
        long longTwo = uuid.getLeastSignificantBits();
        
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(longOne);
        bb.putLong(longTwo);
        id= bb.array();
        
        return id;
    }
      
        public static String ramdomKeySTR()
    {
        String id= "";
        
        id = ramdomKey().toString();
        
        return id;
    }
        
        public static String getKeyAppScores(String appId)
        {
            return USERSCORES +"_"+ appId;
        }
        
        
        public static String getKeyUserFB(String uid)
        {
            return USER_FB +uid;
        }
        
        public static String getKeyUserFB(int uid)
        {
            return  USER_FB + String.valueOf(uid);
        }
        
        public static String getKeyUserME(String uid)
        {
            return USER_ME +uid;
        }
        
        public static String getKeyUserME(int uid)
        {
            return  USER_ME + String.valueOf(uid);
        }
        
        public static String getAllUserME(String appId)
        {
            return USERME +"_"+appId;
        }
        
        public static String getAllUserME(int appId)
        {
            return USERME +"_"+String.valueOf(appId);
        }
        
        public static String getAllUserFB(String appId)
        {
            return USERFB +"_"+appId;
        }
        
        public static String getAllUserFB(int appId)
        {
            return USERFB +"_"+String.valueOf(appId);
        }
        
        public static String getKey_ListFriends(int uid)
        {
            String key = "";
            
            key = LIST_FRIENDS + String.valueOf(uid);
            
            return key;
        }
        
        public static String getKey_ListFriends(String uid)
        {
            String key = "";
            
            key = LIST_FRIENDS + uid;
            
            return key;
        }   
        
         public static String getKeyDevices(String uid)
        {
            return LIST_DEVICE +uid;
        }
        
        public static String getKeyDevices(int uid)
        {
            return  LIST_DEVICE + String.valueOf(uid);
        }
        
         public static String getKeyAppUser(String uid,String appid)
        {
            return uid +"_A_"+appid;
        }
        
        public static String getKeyAppUser(int uid,int appid)
        {
            return  String.valueOf(uid) +"_A_"+ String.valueOf(appid);
        }
        
        public static String getKeyDevice(String uid,String deviceid)
        {
            return uid +"_D_"+deviceid;
        }
        
         public static String getHeadKeyDevices(int uid)
        {
            return  String.valueOf(uid) +"_D_";
        }
        
          public static String getHeadKeyDevices(String uid)
        {
            return  uid +"_D_";
        }
        
        public static String getKeyDevice(int uid,int deviceid)
        {
            return  String.valueOf(uid) +"_D_"+ String.valueOf(deviceid);
        }
        
        public static Map<String,String> getUidResponse(String id)
        {
            Map<String,String> data = new HashMap<String,String>();
            
            String meID = "";
            String fID = "";
            
            String[] idArr = id.split("_");
            if(idArr[0] == USER_FB)
                fID = idArr[1];
            else if(idArr[0] == USER_ME)
                meID = idArr[1];
            
            data.put(ShareMacros.FACEID, id);
            data.put(ShareMacros.MEID, id);
            
            return data;
        }
        
        public static List<String>  getFaceIds(List<String> ids)
     {
         List<String> fIds = new ArrayList<String>();
         
         for (Iterator<String> it = ids.iterator(); it.hasNext();) {
             String string = it.next();
             string = KeysDefinition.getKeyUserFB(string);
             fIds.add(string);
         }
         
         return fIds;
     }     
     
     public static List<String>  getMeIds(List<String> ids)
     {
         List<String> fIds = new ArrayList<String>();
         
         for (Iterator<String> it = ids.iterator(); it.hasNext();) {
             String string = it.next();
             string = KeysDefinition.getKeyUserME(string);
             fIds.add(string);
         }
         
         return fIds;
     }
     
     public static List<String>  getFaceScoreIds(List<String> ids,String appId)
     {
         List<String> fIds = new ArrayList<String>();
         
         for (Iterator<String> it = ids.iterator(); it.hasNext();) {
             String string = it.next();
             string = KeysDefinition.getKeyAppUser(string, appId);
             fIds.add(string);
         }
         
         return fIds;
     }     
     
     public static List<String>  getMeScoreIds(List<String> ids,String appId)
     {
         List<String> fIds = new ArrayList<String>();
         
         for (Iterator<String> it = ids.iterator(); it.hasNext();) {
             String string = it.next();
             string = KeysDefinition.getKeyAppUser(string,appId);
             fIds.add(string);
         }
         
         return fIds;
     }
}
