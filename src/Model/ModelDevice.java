/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import DB_REDIS.Redis_Rd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import share.KeysDefinition;
import share.ShareMacros;

/**
 *
 * @author LinhTA
 */
public class ModelDevice {
    
    public String _id ;
    public String _meID;
    public String _fbID;
    
    public ModelDevice(String id,String meId, String fbId)
    {
        _id = id;
        _meID = meId;
        _fbID = fbId;
    }
    
    public List<String> getTokens(List<String> ids,String appId)
    {
        List<String> data = new ArrayList<>();
        
        
        
        for (String id : ids) {
            
            List<String> devices = new ArrayList<String>();
            devices = getListDevice(id);
            
            for (String deviceId : devices) {
                
                String token = "";
                token = Redis_Rd.getInstance().Hget(deviceId,ShareMacros.TOKEN);
                data.add(token);
                
            }
            
            
            
        }
        
        return data;
    }
    
    private List<String> getListDevice(String uid)
    {
        List<String> data = new ArrayList<String>();
        
        String headKey = KeysDefinition.getHeadKeyDevices(uid);
        Set<String> keys = new HashSet<String>();
        keys = Redis_Rd.getInstance().searchHead(headKey);
        
        data.addAll(keys);
        
        return data;
    }
}
