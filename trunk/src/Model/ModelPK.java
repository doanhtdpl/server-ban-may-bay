/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import DB_REDIS.Redis_Pipeline;
import DB_REDIS.Redis_Rd;
import DB_REDIS.Redis_W;
import Entities.OBJ_PK;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import share.KeysDefinition;
import share.ShareMacros;
import utilities.time.UtilTime;

/**
 *
 * @author LinhTA
 */
public class ModelPK {
    
    String _id ;
    
    public ModelPK (String uid)
    {
        _id = uid;
    }
    
    public boolean newPK(String frdId, String score,String typeMoney,String money )
    {
        boolean ret = true;
        
        long time = UtilTime.getTimeNow();
        
        //luu data pk
        String key = KeysDefinition.getKeyPK(_id, frdId);
        if(Redis_Rd.getInstance().isExits(key))
            return false;        
        
        Map<String,String> data = new HashMap<String,String>();
        data.put(ShareMacros.SCORE, score);
        data.put("money", money);
        data.put(ShareMacros.TYPE, typeMoney);
        data.put(ShareMacros.TIME, String.valueOf(time));
        
        
        String dataSave = Redis_W.getInstance().hset(key, data);        
        
        return (dataSave!="-1");
    }
    
    public boolean del2List_PK(String frdID)
    {
        String key = KeysDefinition.getKeyPK_listMe(_id);
        return (Redis_W.getInstance().s_remove(key,frdID)!= -1);
    }
    
    public boolean del2List_PK2Frd(String frdID,String meID, String fbID)
    {
        String key = KeysDefinition.getKeyPK_listToFrd(_id,meID,fbID);
        return (Redis_W.getInstance().s_remove(key,_id)!= -1);
    }
    
    public boolean delPK(String frdID)
    {
        String key = KeysDefinition.getKeyPK(_id,frdID);
        return (Redis_W.getInstance().del(key)!= -1);
    }
    
    public OBJ_PK getPK2Me(String frdID)
    {
        String key = KeysDefinition.getKeyPK(frdID, _id);
        Map<String,String> data = new HashMap<String,String>();
        
        data = Redis_Rd.getInstance().hget(key);        
        OBJ_PK pk = new OBJ_PK(data);
        
        return pk;
    }
    
    public Map<String,Map<String,String>> getListPk()
    {
        Map<String,Map<String,String>> listPK = new HashMap<String,Map<String,String>>();
                
        String key = KeysDefinition.getKeyPK_listMe(_id);
        Set<String> listFrdID = new HashSet<String>();
        listFrdID = Redis_Rd.getInstance().smember(key);
        
        listPK = Redis_Pipeline.getInstance().multi_hget_PK(_id,listFrdID);
        
        return listPK;
    }
    
    public Map<String,Map<String,String>> getListPk2Me()
    {
        Map<String,Map<String,String>> listPK2me = new HashMap<String,Map<String,String>>();
     
        String key = KeysDefinition.getKeyPK_listToMe(_id);
        Set<String> listFrdID = new HashSet<String>();
        listFrdID = Redis_Rd.getInstance().smember(key);
        
        Map<String, Map<String,String>> datas = new HashMap<String, Map<String,String>>();
        datas = Redis_Pipeline.getInstance().multi_hget_PK2Me(_id,listFrdID);
        
        for (Map.Entry<String, Map<String, String>> entry : datas.entrySet()) {
            String fid = entry.getKey();
            Map<String, String> data = entry.getValue();
            
            Map<String,String> dataget = new HashMap<String,String>();
            dataget.put(ShareMacros.MONEY, data.get(ShareMacros.MONEY));
            dataget.put(ShareMacros.TYPE, data.get(ShareMacros.TYPE));
            
            listPK2me.put(fid, dataget);
        }
        
        return listPK2me;
        
    }
  
    public boolean add2List_PK(String frdID)
    {
        boolean ret = true;
        
        String key = KeysDefinition.getKeyPK_listMe(_id);
        long kq = Redis_W.getInstance().lpush(key, frdID);
        if(kq ==-1)
            return false;
        
        return ret;
    }
    
    public boolean add2List_PK2Frd(String frdID,String meID,String fbID)
    {
        boolean ret = true;
        
        String key = KeysDefinition.getKeyPK_listToFrd(_id,meID,fbID);
        long kq = Redis_W.getInstance().lpush(key, _id);
        if(kq ==-1)
            return false;
        return ret;
    }
    
    
}

