/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

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
    
    
    public static String set(byte[] key, byte[] val)
    {
        String ret = "0";
        
        ret = _jedis.set(key, val);
        
        return ret;
        
    }
    
    public static long set(String key, String val)
    {
        long ret = 0;
        
        ret = _jedis.set(key, val);
        
        return ret;
        
    }
    
    public static String hset(byte[] key, Map<byte[],byte[]> val )
    {
        String ret = "0";
        
        ret = _jedis.setHm(key, val);
        
        return ret;
    }
    
     public static String hset(String key, Map<String,String> val )
    {
        String ret = "0";
        
        ret = _jedis.setHm(key, val);
        
        return ret;
    }
    
    public static long sadd(byte[] key, byte[] val)
    {
        long ret = 0;
        
        ret = _jedis.sadd(key, val);
        
        return ret;
    }
    
//     String[] a = {"x","b"};           
//     Redis_W.getInstance().sadd("g", a);
     public static long sadd(String key, String[] val)
    {
        long ret = 0;
        
        ret = _jedis.sadd(key, val);
        
        return ret;
    }
    
    public static long sadd(String key, String val)
    {
        long ret = 0;
        
        ret = _jedis.sadd(key, val);
        
        return ret;
    }
    
    public static long smove(byte[] dstKeys,byte[] members,byte[] key)
    {
        long ret = 0 ;
        
        ret = _jedis.smove( dstKeys, members,key);
        
        return ret;
    }
    
     public static long smove(String dstKeys,String members,String key)
    {
        long ret = 0 ;
        
        ret = _jedis.smove( dstKeys, members,key);
        
        return ret;
    }
     
      public static long lpudh(String list,String key )
    {
        long ret = 0 ;
        
        ret = _jedis.list_push(list,key );
        
        return ret;
    }
      
       public static long lpudh(byte[] key,byte[] list)
    {
        long ret = 0 ;
        
        ret = _jedis.list_push(key, list);
        
        return ret;
    }
       
       public static void main(String[] args) {
           
           String[] a = {"x","b"};
           
        System.out.print(Redis_W.getInstance().sadd("g", a));
    }
}
