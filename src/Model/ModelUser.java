/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import DB_REDIS.Redis_Rd;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author LinhTA
 */
public class ModelUser {
    String _uid ;
    
    public ModelUser(String uid)
    {
        _uid = uid;
    }
    
    public Map<String,String> getINFO()
    {
        Map<String,String> data = new HashMap<String,String>();
        
        data = Redis_Rd.getInstance().hget(_uid);
        
        return data;
    }
}
