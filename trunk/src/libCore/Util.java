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
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
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
    
     public static String quote(String s) {
        return "'" + s + "'";
    }
      public static String removePrefix(String userId) {
        
        int prefixLenght;
        
        if (userId.startsWith(ShareMacros.PREFIX_FB)) {
            
            prefixLenght = ShareMacros.PREFIX_FB.length();
            
        } else {
            
            prefixLenght = ShareMacros.PREFIX_ME.length();
            
        }
        
        String newStr = userId.substring(prefixLenght);
        
        return newStr;
        
    }
    
   public static String obj2String(Object obj)
   {
        Gson j = new Gson();
       
       String strData = j.toJson(obj);
       
       return strData;
   }
   
   public static Map<String , String> string2Map(String str)
   {
       Map<String, String> data = new HashMap<String, String>();
       
        Gson g = new Gson();
        System.out.println("STR: " + str);
        JSONObject j = g.fromJson(str, JSONObject.class);

        data = (HashMap<String,String>) j;

        return data;
   }
    
   public static Map<String,String> getUserId(Map<String,String> data)
   {
       Map<String,String> ret = new HashMap<String,String>();
       String meId = "";
       String faceId = "";       
       String uID = "";
       
       if(data.containsKey(ShareMacros.FACEID) && data.containsKey(ShareMacros.MEID) && data.get(ShareMacros.FACEID)!=null &&data.get(ShareMacros.FACEID)==""&& data.get(ShareMacros.MEID)!= null && data.get(ShareMacros.MEID)!= "")
       {
           meId = KeysDefinition.getKeyUserME(data.get(ShareMacros.MEID)); 
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
       
       ret.put(ShareMacros.MEID, meId);
       ret.put(ShareMacros.FACEID, faceId);
       ret.put(ShareMacros.ID, uID);
       
       return ret;
   }
   
       public static String getClientIP(HttpServletRequest request) {
        String clientIp = request.getHeader("X-FORWARDED-FOR");
        if (clientIp == null || clientIp.length() == 0) {
            clientIp = request.getHeader("X-Forwarded-For");
        }
        if (clientIp == null || clientIp.length() == 0) {
            clientIp = request.getHeader("x-forwarded-for");
        }
        if (clientIp == null || clientIp.length() == 0) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
       
        public static String arrayToString( String... strs )
    {
        String result    =   "";
        for( int i = 0; i < strs.length; ++i ) {
            if( i == strs.length - 1 ) {
                result   +=  strs[i];
            } else { 
                result   +=  strs[i] + ",";
            }
        }
        return result;
    }
}
