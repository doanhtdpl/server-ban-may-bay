/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet.Action;

import Model.ModelItem;
import Model.Request.ClientRequest;
import Security.Scr_Base64;
import com.google.gson.Gson;
import DB_REDIS.Redis_Rd;
import DB_REDIS.Redis_W;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import share.KeysDefinition;
import share.ShareMacros;
import libCore.Util;
/**
 *
 * @author LinhTA
 */
public class ProfileAction {
    
    public ProfileAction() {
    }
    
    
    
    
    public void updateInfo( ClientRequest req, HttpServletResponse resp )
    {
       
        String meID = req._meID;//ids.get(ShareMacros.MEID);
        String faceID = req._fbID;//ids.get(ShareMacros.FACEID);
        String uid = req._uid;//ids.get(ShareMacros.ID);
        String appId = req._appId;
        
        String name = req._data.get(ShareMacros.NAME);
        String email = req._data.get(ShareMacros.EMAIL);
                       
        Map<String,String> data = new HashMap<>();
        data.put(ShareMacros.NAME, name);
        data.put(ShareMacros.EMAIL, email);
        
        if(uid != "")
            setDefault(uid);
        
        String check = "false";
        if(meID != null && !meID.isEmpty() )
        {
            if (Redis_W.getInstance().hset(uid, data) != "-1" && Redis_W.getInstance().sadd(KeysDefinition.getAllUserME(appId),meID) != -1 )
                check = "true";
            else
                check = "false";
        }
        else
            if(faceID != null && !faceID.isEmpty() )
            {
                if (Redis_W.getInstance().hset(uid, data) != "-1" && Redis_W.getInstance().sadd(KeysDefinition.getAllUserFB(appId),faceID) != -1 && Redis_W.getInstance().set(email,uid )!= -1)
                    check = "true";
                else
                    check = "false";
            }
        
        JSONObject mapjson = new JSONObject();
       mapjson.put(ShareMacros.SUSSCES, check);
       out(mapjson.toJSONString(), resp);
       
      
       // addAppId(uid, appId);
    }
    
    public void getInfo( ClientRequest req, HttpServletResponse resp )
    {
        
       String meId = req._meID;//ids.get(ShareMacros.MEID);
        String faceId = req._fbID;//ids.get(ShareMacros.FACEID);
        String uid = req._uid;//ids.get(ShareMacros.ID);
       
        Map<String,String> data = new HashMap<>();
        
       data =  Redis_Rd.getInstance().hget(uid);
       data.put(ShareMacros.FACEID, faceId);
       data.put(ShareMacros.MEID,meId);
       
//       String keyCoin = KeysDefinition.getKeyCoinUser(uid);
//       String coin = Redis_Rd.getInstance().get(keyCoin);
//       if(coin == null || coin.isEmpty() || coin =="")
//       {
//           coin = "0";
//       }
       data.put(ShareMacros.Coin, ModelItem.getCountItem(uid, ShareMacros.Coin));
       
       JSONObject mapjson = new JSONObject();
       mapjson.putAll(data);
     
        out(mapjson.toJSONString(), resp);
    }
    
    private void out(String content, HttpServletResponse respon) {

        PrintWriter out = null;
        try {
            out = respon.getWriter();
            out.print(Scr_Base64.Encode(content));
            out.close();
        } catch (Exception ex) {
            //logger_.error("CampainAction.out:" + ex.getMessage(), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void prepareHeader(HttpServletResponse resp) {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html; charset=UTF-8");
        resp.addHeader("Server", "DBPTK");
    }
    
     public void handle(ClientRequest request, HttpServletResponse resp) {
        try {
            prepareHeader(resp);
            
            if(request._method.equals("get"))
                getInfo(request, resp);
            else if(request._method.equals("set"))
                updateInfo(request, resp);
            
        } catch (Exception ex) {
           // logger_.error("CampainAction.handle:" + ex.getMessage() + ", Username:" + req.getAttribute("ownerName").toString(), ex);
        }
    }
     
    
     
     public void setDefault(String uid)
     {
//         String keyCoin = KeysDefinition.getKeyCoinUser(uid);
//         String coin = "";
//         int retries = configuration.Configuration.retries;
//         while(retries>0)
//         {
//             coin = Redis_Rd.getInstance().get(keyCoin);
//             if(coin.isEmpty() || coin == null)
//                 retries --;
//             else break;
//         }
//               
//         if(coin.isEmpty() || coin == null)
//         {
//             retries = configuration.Configuration.retries;
//              while(retries>0)
//            {
//                long ret = Redis_W.getInstance().set(keyCoin,configuration.Configuration.Coin_UserDefault);
//                if(ret == -1)
//                    retries --;
//                else break;
//            }
//         }
//        
     }
     
    
     public void outFalse(HttpServletResponse resp)
     {
         JSONObject mapjson = new JSONObject();
       mapjson = defaultResponse_False();
       
        out(mapjson.toJSONString(), resp);
     }
     
      private JSONObject defaultResponse_False()
    {
        JSONObject data = new JSONObject();
        data.put(share.ShareMacros.SUSSCES, "false");
         data.put(ShareMacros.Coin, "0");
         data.put(ShareMacros.NAME, "");
          data.put(ShareMacros.EMAIL, "");
        data.put(ShareMacros.FACEID, "");
       data.put(ShareMacros.MEID,"");
 
       
        return  data;
    }
//     public Map<String,String> getDataJsonReq(HttpServletRequest request)
//     {
//          Map<String,String> reqData = new HashMap<String,String>();
//        
//         String data2Json = request.getParameter("data");
//         
//
//        
//        Gson g = new Gson();
//         JSONObject j = g.fromJson(data2Json, JSONObject.class);
//         
//         reqData = (HashMap<String,String>) j;
//         
//         return reqData;
//     }
     
//     private void addAppId(String uid, String appId)
//     {
//         boolean checkExits = false;
//         
//         String key = KeysDefinition.getKeyAppUser(uid, appId);
//         checkExits = Redis_Rd.getInstance().isExits(key);
//         
//         if(!checkExits)
//         {
//             Map<String,String> dataFake = new HashMap<String,String>();
//             dataFake.put(ShareMacros.SCORE, "0");
//             dataFake.put(ShareMacros.TIME, "-1");
//             
//             Redis_W.getInstance().hset(key, dataFake);
//         } 
//     }
}
