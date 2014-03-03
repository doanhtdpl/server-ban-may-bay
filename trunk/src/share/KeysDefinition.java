/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 *
 * @author LinhTA
 */
public class KeysDefinition {

    static String LIST_FRIENDS = "F_";
    static String LIST_DEVICE = "D_";
    
    static  String USER = "U_";
    static  String USERS = "Users";
    
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
        
        public static String getKeyUser(String uid)
        {
            return USER +uid;
        }
        
        public static String getKeyUser(int uid)
        {
            return  USER + String.valueOf(uid);
        }
        
        public static String getKeyUserList()
        {
            return USERS;
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
        
        public static String getKeyDevice(int uid,int deviceid)
        {
            return  String.valueOf(uid) +"_D_"+ String.valueOf(deviceid);
        }
}
