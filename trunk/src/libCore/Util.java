/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package libCore;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import java.util.HashMap;
import java.util.Map;
import share.KeysDefinition;
import share.ShareMacros;

/**
 *
 * @author LinhTA
 */
public class Util {
    
    public static Map<String,Object> obj2Map(Object obj)
    {
        Map<String, Object> data = new HashMap<String, Object>();
        try
        {
            data = (HashMap<String, Object>) obj;
        }
        catch(Exception e)
        {
            try
            {
                JSONArray a = new JSONArray();
                a= (JSONArray)obj;
            
                for (int i = 0; i < a.size(); i++) {
                    data.put(String.valueOf(i), a.get(i));
                }
            }
            catch(Exception e2)
            {
                
            }
            
        }
        return data;
    }
    
   public static String obj2String(Object obj)
   {
       Gson j = new Gson();
       
       String strData = j.toJson(obj);
       
       return strData;
   }
    
   public static String getUserId(Map<String,String> data,String faceId ,String meId)
   {
       String uID = "";
       
       if(data.containsKey(ShareMacros.FACEID) && data.containsKey(ShareMacros.MEID))
       {
           meId = KeysDefinition.getKeyUserFB(data.get(ShareMacros.MEID)); 
           faceId = KeysDefinition.getKeyUserFB(data.get(ShareMacros.FACEID)); 
           
           if(meId.isEmpty() || meId == "" || meId == null)
           {
               uID = faceId;
           }
           else
           {
               uID = meId;
           }
       }
       else    
       if(data.containsKey(ShareMacros.FACEID))
       {
           faceId = data.get(ShareMacros.FACEID);
           uID = KeysDefinition.getKeyUserFB(faceId);           
           meId = "";
       }
       else if(data.containsKey(ShareMacros.MEID))
       {
            meId = data.get(ShareMacros.MEID);
            uID = KeysDefinition.getKeyUserME(meId);  
            faceId = "";
           
       }
       
       return uID;
   }
}