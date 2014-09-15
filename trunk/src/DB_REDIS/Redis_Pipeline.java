/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DB_REDIS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import libCore.Config;
import share.KeysDefinition;

/**
 *
 * @author LinhTA
 */
public class Redis_Pipeline {
    
   public static RedisClient _jedis ;
    
    private Redis_Pipeline()
    {
        String host = Config.getParam("redis", "host");
        int port = Integer.valueOf(Config.getParam("redis", "port"));
        String password = Config.getParam("redis", "pass");
        int database = Integer.valueOf(Config.getParam("redis", "database"));
        _jedis = RedisClient.getInstance(host, port, password, database);
    }
    
    public static Redis_Pipeline getInstance() {
        return Redis_PipelineHolder.INSTANCE;
    }
    
    private static class Redis_PipelineHolder {

        private static final Redis_Pipeline INSTANCE = new Redis_Pipeline();
    }
    
    public List<String> getExitsScore(List<String> keys,String appId,String meId, String fbId)
    {
        List<String> ret = new ArrayList<String>();
        
        ret = _jedis.getExits(keys,appId,meId,fbId);
        
        return ret;
    }
    
    public Map<String,Boolean> checkExits(List<String> keys)
    {
        Map<String,Boolean> ret = new HashMap<String,Boolean>();
        
        ret = _jedis.checkExits(keys);
        
        return ret;
    }
    
    public Map<String,Map<String,String>> multi_hget(List<String> keys)
    {
        Map<String,Map<String,String>> data = new HashMap<String,Map<String,String>>();
        
        data = _jedis.multi_Hget(keys);
        
        return data;
        
    }
    
    public Map<String,Map<String,String>> multi_hget_PK(String uid ,Set<String> keys)
    {
        Map<String,Map<String,String>> data = new HashMap<String,Map<String,String>>();
        
        Map<String,String> keysPk = new HashMap<String, String>();        
        for (String k : keys) {
            keysPk.put(k, KeysDefinition.getKeyPK(uid, k));
        }
        
        data = _jedis.multi_Hget(keysPk);
        
        return data;
        
    }
    
    public Map<String,Map<String,String>> multi_hget_PK2Me(String uid ,Set<String> keys)
    {
        Map<String,Map<String,String>> data = new HashMap<String,Map<String,String>>();
        
        Map<String,String> keysPk = new HashMap<String, String>();        
        for (String k : keys) {
            keysPk.put(k, KeysDefinition.getKeyPK(k, uid));
        }
        
        data = _jedis.multi_Hget(keysPk);
        
        return data;
        
    }
}
