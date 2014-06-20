/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DB_REDIS;

import libCore.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import share.KeysDefinition;

/**
 *
 * @author LinhTA
 */
public class RedisClient {

    private static Logger logger = Logger.getLogger(RedisClient.class);
    private static Map<String, RedisClient> _instances = new NonBlockingHashMap();
    private static final Lock createLock_ = new ReentrantLock();
    private JedisPool Pool;
    private String _host = "";
    private int _port = 0;
    private int _timeout = 0; //500 ms
    private String _password = "";
    private int _database = -1;
    
    private int MAX_ACTIVE = 0;
    private int MAX_IDLE = 0;
    private int MAX_WAIT = 0;

    public static RedisClient getInstance(String host, int port) {
        return getInstance(host, port, "", 0);
    }

    public static RedisClient getInstance(String host, int port, int database) {
        return getInstance(host, port, "", database);
    }

    public static RedisClient getInstance(String host, int port, String password, int database) {
        String key = host + port + password + database;
        if (!_instances.containsKey(key)) {
            createLock_.lock();
            try {
                if (_instances.get(key) == null) {
                    _instances.put(key, new RedisClient(host, port, password, database));
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instances.get(key);
    }

    public RedisClient(String host, int port, String password, int database) {
        JedisPoolConfig poolConf = new JedisPoolConfig();
        poolConf.setTestOnBorrow(true);
        poolConf.setMaxActive(Integer.valueOf(Config.getParam("redis", "max_active")));
        poolConf.setMaxIdle(Integer.valueOf(Config.getParam("redis", "max_idle")));
        poolConf.setMaxWait(Integer.valueOf(Config.getParam("redis", "max_wait")));
        
        
        if (password == null || password.isEmpty() || password =="") {
		Pool = new JedisPool(poolConf, host, port,this._timeout,null, database);
        } else {
			Pool = new JedisPool(poolConf, host, port, this._timeout, password, database);
        }
		
       // Pool = new JedisPool(poolConf, this._host, this._port);
    }

    public RedisClient(String host, int port, int timeout, String password, int database) {
        JedisPoolConfig poolConf = new JedisPoolConfig();
        poolConf.setTestOnBorrow(true);
        poolConf.setMaxActive(Integer.valueOf(Config.getParam("redis", "max_active")));
        poolConf.setMaxIdle(Integer.valueOf(Config.getParam("redis", "max_idle")));
        poolConf.setMaxWait(Integer.valueOf(Config.getParam("redis", "max_wait")));
        
        Pool = new JedisPool(poolConf, host, port, timeout,
                password, database);
    }

    public long set(String key, String value) {
        long ret = 0;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            jedis.set(key, value);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.get", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public String get(String key) {
        String ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.get(key);
           
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.get", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public String ping()
    {
        String ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.ping();
           
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.get", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }
    
     public Map<String,String> rand()
     {
         Map<String,String> data = new HashMap<String,String>();
         
         String from = "";
         String key = "";
         
          Jedis jedis = null;
         try {
            jedis = Pool.getResource();
            from = "";
            key = jedis.srandmember(from);
            
            data.put("from", from);
            data.put("key", key);
            
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.get", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
         
         
         return data;
     }
    
    public String set(byte[] key, byte[] value) {
        String ret = "0";
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
           ret = jedis.set(key, value);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.get", ex);
            ret = "-1";
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public byte[] get(byte[] key) {
        byte[] ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.get(key);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.get", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    /**
     * Add on member to sorted set
     *
     * @param key
     * @param value of member
     * @param score of member
     * @return number of member inserted
     */
    public long zadd(String key, double score,String member) {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zadd(key, score, member);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zadd", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    /**
     * Add multi members to sorted set
     *
     * @param key
     * @param value of member
     * @param score of member
     * @return number of member inserted
     */
    public int zadd(String key, Map<Double, String> scoreMembers) {
        int ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            jedis.zadd(key, scoreMembers);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zadd", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    /**
     * Get total element is sorted set
     *
     * @param key
     * @param value of member
     * @param score of member
     * @return number of member inserted
     */
    public long zcard(String key) {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zcard(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zcard", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public Set<String> zrange(String key, long start, long end) {
        Set<String> ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zrange(key, start, end);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zrange", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;

    }

    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        Set<Tuple> ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zrangeWithScores(key, start, end);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zrangeWithScores", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;

    }

    public Set<String> zrevrange(String key, long start, long end) {
        Set<String> ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zrevrange(key, start, end);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zrange", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
        Set<Tuple> ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zrevrangeWithScores(key, start, end);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zrevrangeWithScores", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long zrem(String key, String member) {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zrem(key, member);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zrem", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long zremMulti(String key, String[] member) {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zrem(key, member);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zremMulti", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long zremrangeByRank(String key, long start, long stop) {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zremrangeByRank(key, start, stop);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zremrangeByRank", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long smove(byte[] dstKeys,byte[] members,byte[] key)
    {
      long ret =0;      
      Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.smove( dstKeys, members,key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.smove", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
      return ret;
    }
    
    public long smove(String dstKeys,String members,String key)
    {
      long ret =0;      
      Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.smove( dstKeys, members,key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.smove", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
      return ret;
    }
    
    public byte[] srand(byte[] member)
    {
        byte[] ret =null;      
      Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.srandmember(member);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.smove", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
      return ret;
    }
    
    public String srand(String member)
    {
        String ret =null;      
      Jedis jedis = null;
        try {
            
            jedis = Pool.getResource();
            ret = jedis.srandmember(member);
           
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.smove", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
      return ret;
    }
    
    public long sadd(byte[] key,byte[] member)
    {
         long ret =0;      
      Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.sadd(key, member);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.sadd", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
      return ret;
    }
    
    
    public long sadd(String key,String[] members)
    {
//        String[] a = {"a","b","c"};
//           jedis.sadd(key, a);
        
         long ret =0;      
      Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.sadd(key, members);
            
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.sadd", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
      return ret;
    }
    
    public long sadd(String key,String member)
    {
         long ret =0;      
      Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.sadd(key, member);
            
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.sadd", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
      return ret;
    }
    
    public Set<byte[]> smember(byte[] key)
    {
        Set<byte[]> ret =null;      
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.smembers(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.smove", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
      return ret;
    }
    
    public Set<String> smember(String key)
    {
        Set<String> ret =null;      
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.smembers(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.smove", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
      return ret;
    }
    
    public Map<byte[],byte[]> getHm(byte[] key)
    {
        Map<byte[],byte[]> ret =null;      
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.hgetAll(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getHM", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
      return ret;
    }
    
    public Map<String,String> getHm(String key)
    {
        Map<String,String> ret =null;      
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.hgetAll(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getHM", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
      return ret;
    }
    
    public String hget(String key, String field)
    {
        String ret = null;
        Jedis jedis = null;
        try{
            jedis = Pool.getResource();
            ret = jedis.hget(key, field);
        }catch(Exception e)
        {
            logger.error("Exception in RedisClient.hget",e);
            ret= null;
        } 
        finally
        {
            if(jedis != null)
                Pool.returnResource(jedis);
        }
        
        return ret;
    }
    
    public byte[] hget(byte[] key, byte[] field)
    {
        byte[] ret = null;
        Jedis jedis = null;
        try{
            jedis = Pool.getResource();
            ret = jedis.hget(key, field);
        }catch(Exception e)
        {
            logger.error("Exception in RedisClient.hget",e);
            ret= null;
        } 
        finally
        {
            if(jedis != null)
                Pool.returnResource(jedis);
        }
        
        return ret;
    }
    
     public String setHm(byte[] key, Map<byte[],byte[]> hm)
    {
         String ret ="0";      
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.hmset(key, hm);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.setHm", ex);
            ret = "-1";
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
      return ret;
    }
     
    public String setHm(String key, Map<String,String> hm)
    {
         String ret ="0";      
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.hmset(key, hm);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.setHm", ex);
            ret = "-1";
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
      return ret;
    }
    
     public List<byte[]>getHm (byte[] key, byte[] hm)
     {
        List<byte[]> ret = new ArrayList<byte[]>();
        
        Jedis jedis = null;
        try {
            
            jedis = Pool.getResource();
            ret = jedis.hmget(key,hm);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.setHm", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
      return ret;
     }
     
    public long getAutoId(String AUTO_ID_KEY) {
        long ret = -1;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.incr(AUTO_ID_KEY);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long del(String key) {
        long ret = -1;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.del(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long del(byte[] key) {
        long ret = -1;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.del(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }
    
    public void flushAll(){
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            jedis.flushAll();
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
    }
    
     public boolean isExits(byte[] key)
     {
       Boolean ret = true;
         Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.exists(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = false;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        return ret;
    }
     
     public boolean isExits(String key)
     {
       Boolean ret = true;
         Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.exists(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = false;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        return ret;
    }
     
     public List<byte[]> list_getAll(byte[] key)
     {
         List<byte[]> list = new ArrayList<byte[]>();
         
          Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            list = jedis.lrange(key,0,-1);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            list = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
         return list;
     }
     
      public List<String> list_getAll(String key)
     {
         List<String> list = new ArrayList<String>();
         
          Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            list = jedis.lrange(key,0,-1);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            list = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
         return list;
     }
      
      public long list_push(String key, String val)
     {
         long ret = 0;
         
          Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.lpush(key,val);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
         return ret;
     }
      
       public long list_push(byte[] key, byte[] val)
     {
         long ret = 0;
         
          Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.lpush(key,val);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
         return ret;
     }
       
       public Set<String> sinter(String[] key) {
        Set<String>  ret = new HashSet<String>();

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.sinter(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zcard", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }
       
       public Map<String,Boolean> checkExits(List<String> keys)
       {
           Map<String,Boolean> keyExits = new HashMap<String,Boolean>();
            Map<String,Response<Boolean>> keysResp= new HashMap<String,Response<Boolean>> ();
            
            Jedis jedis = null;
            try {
                jedis = Pool.getResource();
                
                Pipeline p = jedis.pipelined();
                
                for (int i = 0; i < keys.size(); i++) {
                    
                   keysResp.put(keys.get(i),p.exists(keys.get(i)) );
                    
                }
                               
                p.sync(); 
                
                for (Map.Entry<String, Response<Boolean>> entry : keysResp.entrySet()) {
                    String string = entry.getKey();
                    Response<Boolean> response = entry.getValue();
                    
                    keyExits.put(string, response.get());
                }
                
            } catch (Exception ex) {
                logger.error("Exception in RedisClient.pipelineCheckExits", ex);
                keyExits = null;
            } finally {
                if (jedis != null) {
                    Pool.returnResource(jedis);
                }
            }
           
           return keyExits;
       }
       
        public List<String> getExits(List<String> keys,String appId,String meId, String fbId)
       {
           List<String> keyExits = new ArrayList<String>();
            Map<String,Response<Boolean>> keysResp= new HashMap<String,Response<Boolean>> ();
            
            Jedis jedis = null;
            try {
                jedis = Pool.getResource();
                
                Pipeline p = jedis.pipelined();
                
                for (int i = 0; i < keys.size(); i++) {
                     String kScore = "";
                     kScore = keys.get(i);
                    if(meId !="" && !meId.isEmpty())
                    {
                        kScore = KeysDefinition.getKeyUserME(kScore);
                    }
                    else
                    {
                        kScore = KeysDefinition.getKeyUserFB(kScore);
                    }
                                       
                    kScore = KeysDefinition.getKeyAppUser(kScore, appId);
                   keysResp.put(keys.get(i),p.exists(kScore) );
                    
                }
                               
                p.sync(); 
                
                for (Map.Entry<String, Response<Boolean>> entry : keysResp.entrySet()) {
                    String string = entry.getKey();
                    Response<Boolean> response = entry.getValue();
                    if(response.get())
                        keyExits.add(string);
                }
                
            } catch (Exception ex) {
                logger.error("Exception in RedisClient.pipelineGetExits", ex);
                keyExits = null;
            } finally {
                if (jedis != null) {
                    Pool.returnResource(jedis);
                }
            }
           
           return keyExits;
       }
       
         public List<String> getExits(List<String> keys)
       {
           List<String> keyExits = new ArrayList<String>();
            Map<String,Response<Boolean>> keysResp= new HashMap<String,Response<Boolean>> ();
            
            Jedis jedis = null;
            try {
                jedis = Pool.getResource();
                
                Pipeline p = jedis.pipelined();
                
                for (int i = 0; i < keys.size(); i++) {
                    
                    keysResp.put(keys.get(i),p.exists(keys.get(i)) );
                    
                }
                               
                p.sync(); 
                
                for (Map.Entry<String, Response<Boolean>> entry : keysResp.entrySet()) {
                    String string = entry.getKey();
                    Response<Boolean> response = entry.getValue();
                    if(response.get())
                        keyExits.add(string);
                }
                
            } catch (Exception ex) {
                logger.error("Exception in RedisClient.pipelineGetExits", ex);
                keyExits = null;
            } finally {
                if (jedis != null) {
                    Pool.returnResource(jedis);
                }
            }
           
           return keyExits;
       }
       
        
       public int isExits(List<String> keys)
       {
           int countCheck = 0;
           Map<String,Response<Boolean>> keysResp= new HashMap<String,Response<Boolean>> ();
            
            Jedis jedis = null;
            try {
                jedis = Pool.getResource();
                
                Pipeline p = jedis.pipelined();
                
                for (int i = 0; i < keys.size(); i++) {
                    
                   keysResp.put(keys.get(i),p.exists(keys.get(i)) );
                    
                }
                               
                p.sync(); 
                
                for (Map.Entry<String, Response<Boolean>> entry : keysResp.entrySet()) {
                    String string = entry.getKey();
                    Response<Boolean> response = entry.getValue();
                    
                    if(response.get())
                        countCheck++;
                }
                
                if(countCheck == keys.size())
                    return 1;
                
            } catch (Exception ex) {
                logger.error("Exception in RedisClient.zcard", ex);
                return -1;
            } finally {
                if (jedis != null) {
                    Pool.returnResource(jedis);
                }
            }
           
           return 0;
       }
       
       // high to low : top hight score
       public Set<String> zrevrange(String key, long start)
       {
           Set<String> ret = new HashSet<>();
           
            Jedis jedis = null;
            try {
                jedis = Pool.getResource();
                ret = jedis.sdiff(key);
            } catch (Exception ex) {
                logger.error("Exception in RedisClient.zcard", ex);
                ret = null;
            } finally {
                if (jedis != null) {
                    Pool.returnResource(jedis);
                }
            }
           
           return ret;
       }
       
       
        public Set<String> keys(String key)
       {
           Set<String> ret = new HashSet<>();
           
            Jedis jedis = null;
            try {
                jedis = Pool.getResource();
                ret = jedis.keys(key);
            } catch (Exception ex) {
                logger.error("Exception in RedisClient.keys", ex);
                ret = null;
            } finally {
                if (jedis != null) {
                    Pool.returnResource(jedis);
                }
            }
           
           return ret;
       }
        
        //value -> int 64bits ->data  ( if key notexit -> set key 0 )
        //data = data+1
        //data -> String -> DB
        public long incr(String key)
        {
            long ret = 0;
           
            Jedis jedis = null;
            try {
                jedis = Pool.getResource();
                ret = jedis.incr(key);
            } catch (Exception ex) {
                logger.error("Exception in RedisClient.keys", ex);
                ret = -1;
            } finally {
                if (jedis != null) {
                    Pool.returnResource(jedis);
                }
            }
           
           return ret;
        }
       
//       public Map<String,Object> pipeline(Map<>)
//     {
//         Map<String,Object> ret = new HashMap<String,Object>();
//         
//          Jedis jedis = null;
//        try {
//            jedis = Pool.getResource();
//            
//            Pipeline p = jedis.pipelined();
//            p.
//            
//            
//            ret = jedis.lpush(key,val);
//        } catch (Exception ex) {
//            logger.error("Exception in RedisClient.getAutoId", ex);
//            ret = null;
//        } finally {
//            if (jedis != null) {
//                Pool.returnResource(jedis);
//            }
//        }
//        
//         return ret;
//     } 
       
       
    
    public static void main(String[] args) throws TException {
        //RedisClient rc = new RedisClient("192.168.182.132", 6379, 500, "", 9);
        //System.out.println("Connected to Redis localhost:" + 6379);
        RedisClient client = getInstance("192.168.1.133", 6379);
        
        
        for(int count =0;count<1000;count++)
        {        
            System.out.print(client.srand("2013123"));
            System.out.print("\n");
        }        
//        String key = "test123";
//        client.set(key, "Linh");
//        System.out.print( client.set(key, "Linh"));
        
//        String value = "test111";
//        
      //  byte[] key_bytes = key.getBytes();
//        byte[] value_bytes = value.getBytes();
//        
//        client.set(key_bytes, value_bytes);
//        
//        byte[] value_get_bytes = client.get(key_bytes);
//        
//        String ret = new String(value_get_bytes);
//        
//        System.out.println("ret=" + ret);
        
//        ArrayList<TZMOA_AdsCandidate> x = new ArrayList<>();
//        x.add(new TZMOA_AdsCandidate("ads1", 1));
//        x.add(new TZMOA_AdsCandidate("ads2", 2));
//        x.add(new TZMOA_AdsCandidate("ads3", 3));
//        x.add(new TZMOA_AdsCandidate("ads4", 4));
//        TZMOA_Impression a = new TZMOA_Impression("zone1", "uuid", x);
////        ArrayList<TZMOA_ListAdsCandidate> t = new ArrayList<TZMOA_ListAdsCandidate>();
////        t.add(a);
//        ArrayList<TZMOA_UserAttribute> y = new ArrayList<>();
//        y.add(new TZMOA_UserAttribute("age", "21"));
//        y.add(new TZMOA_UserAttribute("gender", "male"));
//        TZMOA_User z = new TZMOA_User("userTest1", y);
//        System.out.println(client.set(key_bytes,
////                SerializeUtil.serializeUser(z)
//                SerializeUtil.serializeUser(z)
//                ));
//        byte[] k = client.get(key_bytes);
//        System.out.println(SerializeUtil.deserializeUser(k));
        //System.out.println(client.get("a"));
        
//        System.out.println(rc.getAutoId("yyy"));
//        JedisPoolConfig poolConf = new JedisPoolConfig();
//        JedisPool pool = new JedisPool(poolConf, "localhost", 6379, 500, "hc", 9);
//        Jedis jedis = pool.getResource();
//        jedis.flushAll();
//        jedis.set("key", "value");
//        System.out.println(jedis.get("key"));
//        TZMOA_Zone z = new TZMOA_Zone();
//        String skey = "skey";
//        byte[] skeyBytes=skey.getBytes();
//        for (int i = 0; i < 25; i++) {
//            z.id = i;
//            z.name = "name" + i;
//            z.desc = "desc" + i;
//            z.appName = "appName" + i;
//
//            jedis.zadd(skey, i, String.valueOf(i));
//        }
//        for (int i = 0; i < 20; i++) {
//            byte[] bytes = jedis.get(String.valueOf(i).getBytes());
//            z = SerializeUtil.deserializeZone(bytes);
//        }
//        Set<byte[]> rs = jedis.zrange(skeyBytes, 0, -1);
//        System.out.println(rs.size());
//        for (int i = 0; i < 10; i++) {
//            System.out.println(jedis.zcard(skey));
////            jedis.zadd(skey, i, String.valueOf(10 * i));
//            jedis.zrem(skey, String.valueOf(10 * i));
//            
//        }
//        Set<byte[]> l = jedis.zrange(skeyBytes, 0, 5);
//        Iterator ite = l.iterator();
//        while (ite.hasNext()) {
//            z= ZoneSerialize.deserializeZone((byte[])ite.next());
////            System.out.println(z.id);
//        }

//        System.out.println("---------------------------");
//        Set<Tuple> tuple = jedis.zrangeWithScores(skey, 3, 5);
//        ite = tuple.iterator();
//        while (ite.hasNext()) {
//            Tuple tp = (Tuple) ite.next();
//            System.out.println(tp.getElement() + tp.getScore());
//        }
//        jedis.z
//        String AUTO_ID = "AUTO_ID";
//        System.out.println(jedis.incr(AUTO_ID));
//        System.out.println(jedis.get(AUTO_ID));//        System.out.println(l.size());
//        //        System.out.println(jedis.zcard(skey));
//        //        jedis.zrem(skey, String.valueOf(50));
//        //        System.out.println(jedis.zcard(skey));
//        System.out.println(jedis.get("asdfasdsa"));
    }
}
