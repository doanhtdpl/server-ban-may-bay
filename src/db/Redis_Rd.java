/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

import libCore.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 *
 * @author linhta
 */
public class Redis_Rd {
    
    public static RedisClient _jedis ;
    
    private Redis_Rd()
    {
        String host = Config.getParam("redis", "host");
        int port = Integer.valueOf(Config.getParam("redis", "port"));
        String password = Config.getParam("redis", "pass");
        int database = Integer.valueOf(Config.getParam("redis", "database"));
        _jedis = RedisClient.getInstance(host, port, password, database);
    }
    
    public static Redis_Rd getInstance() {
        return Redis_Rd.Redis_RdHolder.INSTANCE;
    }
    
    private static class Redis_RdHolder {

        private static final Redis_Rd INSTANCE = new Redis_Rd();
    }
    
    
    public static byte[] get( byte[] key)
    {
        byte[] data = null;
        
        data = _jedis.get(key);
        
        return data;
    }
    
    public static String get(String key)
    {
        String data = null;
        
        data = _jedis.get(key);
        
        return data;
    }
    
    public static Map<String,String> ranb()
    {
        Map<String,String> data = new HashMap<String, String>();
        
        data = _jedis.rand();
        
        return data;
    }
    
     public static Map<byte[],byte[]> hget( byte[] key)
    {
        Map<byte[],byte[]> data = null;
        
        data = _jedis.getHm(key);
        
        return data;
    }
     
      public static Map<String,String> hget( String key)
    {
        Map<String,String> data = null;
        
        data = _jedis.getHm(key);
        
        return data;
    }
     
     public static byte[] srand( byte[] key)
    {
        byte[] data = null;
        
        data = _jedis.srand(key);
        
        return data;
    }
     
       public static String srand( String key)
    {
        String data = null;
        
        data = _jedis.srand(key);
        
        return data;
    }
       
      public static Set<byte[]> smember( byte[] key)
    {
         Set<byte[]> data = null;
        
        data = _jedis.smember(key);
        
        return data;
    }
      
       public static Set<String> smember( String key)
    {
         Set<String> data = null;
        
        data = _jedis.smember(key);
        
        return data;
    }
      
      public static List<byte[]> getHm (byte[] key, byte[] hm)
     {
          List<byte[]> data = new ArrayList<byte[]>();
        
          data = _jedis.getHm(key, hm);
        
            return data;
     }
      
      public static String Hget(String key,String field)
      {
          String ret = "";
          
          ret = _jedis.hget(key,field);
          
          return ret;
      }
      
      public static byte[] Hget(byte[] key,byte[] field)
      {
          byte[] ret = null;
          
          ret = _jedis.hget(key,field);
          
          return ret;
      }
      
      public static boolean isExits(byte[] key)
      {
          Boolean ret = true;
          
          ret = _jedis.isExits(key);
          
          return ret;
      }
      
      public static boolean isExits(String key)
      {
          Boolean ret = true;
          
          ret = _jedis.isExits(key);
          
          return ret;
      }
      
      public static List<byte[]> list_getAll(byte[] key)
      {
          List<byte[]> ret = new ArrayList<byte[]>();
          
          ret = _jedis.list_getAll(key);
          
          return ret;
      }
      
       public static List<String> list_getAll(String key)
      {
          List<String> ret = new ArrayList<String>();
          
          ret = _jedis.list_getAll(key);
          
          return ret;
      }
       
       
       
       public static long list_push(String key, String val)
       {
           long ret = 0;
           
           ret = _jedis.list_push(key, val);
           
           return ret;
       }
       
       public static long list_push(byte[] key, byte[] val)
       {
           long ret = 0;
           
           ret = _jedis.list_push(key, val);
           
           return ret;
       }
       
       public static Set<String> sInter(String[] key)
    {
        Set<String>  data = new HashSet<String>();
        
        data = _jedis.sinter(key);
        
        return data;
    }
}
