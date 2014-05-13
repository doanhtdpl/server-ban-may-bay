/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Security;

import Model.Request.ClientRequest;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import db.Redis_Rd;
import libCore.Config;
import share.KeysDefinition;
/**
 *
 * @author LinhTA
 */
public class Authenticate {
    
    String _keyAuth;
    String _sign;
    
    public Authenticate(String appID,String sign,String fbId,String meId)
    {
        
        _sign = sign;
        String keyApp = getKeyApp(appID);
        
        String keyStr = appID+keyApp+fbId+meId;
        _keyAuth =Scr_MD5.parseMD5(keyStr);        
      
    }
    
    protected String getKeyApp(String appId)
    {
        String key = "";
        
        key = Redis_Rd.getInstance().Hget(KeysDefinition.getAppKey(), appId);
        
        return key;
    }
    
    public boolean checkAuth()
    {
        if(_sign.equals(_keyAuth))
            return true;
        Test_LogCSV.LogCSV.log("AUTH Fail",_sign +","+_keyAuth);
        return false;
    }
    
}
