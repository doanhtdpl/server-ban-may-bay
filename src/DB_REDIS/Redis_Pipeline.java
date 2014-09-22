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
import share.ShareMacros;

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
    
    public Map<String,Map<String,String>> multi_hget_PK(String uid ,List<String> keys)
    {
        Map<String,Map<String,String>> data = new HashMap<String,Map<String,String>>();
        
        Map<String,String> keysPk = new HashMap<String, String>();        
        for (String k : keys) {
            keysPk.put(k, KeysDefinition.getKeyPK(uid, k));
        }
        
        data = _jedis.multi_Hget(keysPk);
        
        return data;
        
    }
    
    public Map<String,Map<String,String>> multi_hget_PK2Me(String meId,String fbId ,List<String> keys)
    {
        Map<String,Map<String,String>> data = new HashMap<String,Map<String,String>>();
        Map<String,String> listKeyFriends = new HashMap<String, String>();
        Map<String,String> keysPk = new HashMap<String, String>();
        
        boolean checkFb = false;
        if(meId != null && meId != "")
            checkFb = false;
        else 
            checkFb = true;
        String idMe =  checkFb ? fbId:meId;
        
        for (String k : keys) {
            String kFriend =checkFb ? KeysDefinition.getKeyUserFB(k) : KeysDefinition.getKeyUserME(k);
            listKeyFriends.put(k, kFriend);
            keysPk.put(k, KeysDefinition.getKeyPK(kFriend,idMe));
        }
        
        data = _jedis.multi_Hget(keysPk);
        
        for (Map.Entry<String, Map<String,String>> entry : data.entrySet()) {
            String k = entry.getKey();
             Map<String,String> v = entry.getValue();
             v.put(ShareMacros.FRIENDID, listKeyFriends.get(k));
        }
        
        return data;
        
    }
}
