/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

import java.util.HashMap;
import libCore.Config;
import java.util.Map;
import redis.clients.jedis.JedisPoolConfig;
/**
 *
 * @author linhta
 */
public class Redis_W {
    
    public static RedisClient _jedis ;
    
    public Redis_W()
    {        
        String host = Config.getParam("redis", "host");
        int port = Integer.valueOf(Config.getParam("redis", "port"));
        String password = Config.getParam("redis", "pass");
        int database = Integer.valueOf(Config.getParam("redis", "database"));
        _jedis = RedisClient.getInstance(host, port, password, database);
    
    }
    
    public static Redis_W getInstance() {
        return Redis_W.Redis_WHolder.INSTANCE;
    }
    
    private static class Redis_WHolder {

        private static final Redis_W INSTANCE = new Redis_W();
    }
    
    
    public String set(byte[] key, byte[] val)
    {
        String ret = "0";
        
        ret = _jedis.set(key, val);
        
        return ret;
        
    }
    
    public long set(String key, String val)
    {
        long ret = 0;
        
        ret = _jedis.set(key, val);
        
        return ret;
        
    }
    
    public String hset(byte[] key, Map<byte[],byte[]> val )
    {
        String ret = "0";
        
        ret = _jedis.setHm(key, val);
        
        return ret;
    }
    
     public String hset(String key, Map<String,String> val )
    {
        String ret = "0";
        
        ret = _jedis.setHm(key, val);
        
        return ret;
    }
    
    public long sadd(byte[] key, byte[] val)
    {
        long ret = 0;
        
        ret = _jedis.sadd(key, val);
        
        return ret;
    }
    
//     String[] a = {"x","b"};           
//     Redis_W.getInstance().sadd("g", a);
     public long sadd(String key, String[] val)
    {
        long ret = 0;
        
        ret = _jedis.sadd(key, val);
        
        return ret;
    }
    
    public long sadd(String key, String val)
    {
        long ret = 0;
        
        ret = _jedis.sadd(key, val);
        
        return ret;
    }
    
    public long smove(byte[] dstKeys,byte[] members,byte[] key)
    {
        long ret = 0 ;
        
        ret = _jedis.smove( dstKeys, members,key);
        
        return ret;
    }
    
     public long smove(String dstKeys,String members,String key)
    {
        long ret = 0 ;
        
        ret = _jedis.smove( dstKeys, members,key);
        
        return ret;
    }
     
      public long lpudh(String list,String key )
    {
        long ret = 0 ;
        
        ret = _jedis.list_push(list,key );
        
        return ret;
    }
      
       public long lpudh(byte[] key,byte[] list)
    {
        long ret = 0 ;
        
        ret = _jedis.list_push(key, list);
        
        return ret;
    }
       
       public long zAdd(String key, double score, String member)
       {
           long ret = 0;
           
           ret = _jedis.zadd(key, score,member);
           
           return ret;
       }
       
       public static void main(String[] args) {
           
           String[] a = {"x","b"};
           
        System.out.print(Redis_W.getInstance().sadd("g", a));
    }
       
         public long setRetries(String key,String val)
       {
           long ret = -1;
           
            int retries = Integer.parseInt(Config.getParam("redis", "retries"));
         while(retries>0)
         {
             ret = set(key,val);
             if( ret == -1 )
             {
                 retries --;
             }
             else break;
         }
           
           return ret;
       }
         
          public String hsetRetries(String key,String field,String val)
       {
           String ret = null;
           
            int retries = Integer.parseInt(Config.getParam("redis", "retries"));
            Map<String,String> data = new HashMap<String,String>();
            data.put(field, val);
         while(retries>0)
         {
             ret = hset(key,data);
             if( ret == "-1" )
             {
                 retries --;
             }
             else break;
         }
           
           return ret;
       }
}
