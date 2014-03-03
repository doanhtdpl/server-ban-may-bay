/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet;

import com.google.gson.Gson;
import db.Redis_Rd;
import db.Redis_W;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import share.KeysDefinition;
import share.ShareMacros;

/**
 *
 * @author LinhTA
 */
public class FriendAction {
    
    private FriendAction() {
    }
    
    public static FriendAction getInstance() {
        return FriendActionHolder.INSTANCE;
    }
    
    private static class FriendActionHolder {

        private static final FriendAction INSTANCE = new FriendAction();
    }
    
     private void out(String content, HttpServletResponse respon) {

        PrintWriter out = null;
        try {
            out = respon.getWriter();
            out.print(content);
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
        resp.addHeader("Server", "BlogCommunity");
    }
    
     public void handle(HttpServletRequest req, HttpServletResponse resp) {
        try {
            prepareHeader(resp);
            if(req.getParameter(ShareMacros.METHOD).matches("get"))
                getFriendList(req, resp);
            else
                setFriendList(req, resp);
            
        } catch (Exception ex) {
           // logger_.error("CampainAction.handle:" + ex.getMessage() + ", Username:" + req.getAttribute("ownerName").toString(), ex);
        }
    }
     
     public Map<String,String> getDataJsonReq(HttpServletRequest request)
     {
          Map<String,String> reqData = new HashMap<String,String>();
        
         String data2Json = request.getParameter("data");
         

        
        Gson g = new Gson();
         JSONObject j = g.fromJson(data2Json, JSONObject.class);
         
         reqData = (HashMap<String,String>) j;
         
         return reqData;
     }
     
     public void getFriendList(HttpServletRequest req , HttpServletResponse resp)
     {
         Map<String, String> reqData = new HashMap<String,String>();
        reqData = getDataJsonReq(req);
        
        String faceId = reqData.get(ShareMacros.FACEID);
        String appId = reqData.get(ShareMacros.APPID);
        
        String key = KeysDefinition.getKey_ListFriends(faceId);
        Set<String> friendsFace = new HashSet<String>();        
       friendsFace =  Redis_Rd.getInstance().smember(key);
       
//         for (int i = 0; i < friendsFace.size(); i++) 
//         {
//             String friendId = KeysDefinition.getKeyUser(friendsFace.toArray()[i].toString());
//             if(Redis_Rd.getInstance().isExits( friendId) == false)
//             {
//                 friendsFace.remove(friendId);
//             }
//            
//         }
       
        Map<String,String> data = new HashMap<String,String>();
        
        for(int j = 0; j < friendsFace.size(); j++) 
        {
             String friendId = KeysDefinition.getKeyUser(friendsFace.toArray()[j].toString());
            String name = Redis_Rd.getInstance().Hget(friendId, ShareMacros.NAME);
            String score = Redis_Rd.getInstance().Hget(KeysDefinition.getKeyAppUser(friendId, appId), ShareMacros.SCORE);
            if(score != null && name != null)
            {
                Map<String,String> friendProfile = new HashMap<String,String>();
                friendProfile.put(ShareMacros.NAME, name);
                friendProfile.put(ShareMacros.SCORE, score);
                
                data.put(friendId, score);
            } 
                
        }
        
       
       JSONObject mapjson = new JSONObject();
       mapjson.putAll(data);
       
        out(mapjson.toJSONString(), resp);
     }
     
     public void setFriendList(HttpServletRequest req , HttpServletResponse resp)
     {
         Map<String, String> reqData = new HashMap<String,String>();
        reqData = getDataJsonReq(req);
        
       
       
         ArrayList<String> friendList = (ArrayList<String>) JSONValue.parse(String.valueOf(reqData.get(ShareMacros.FRIENDLIST)));
        String faceId = reqData.get(ShareMacros.FACEID);
        
        String key = KeysDefinition.getKey_ListFriends(faceId);
      
        String[] friends = new String[friendList.size()]; 
         for (int i = 0; i < friendList.size(); i++) {
             friends[i]= String.valueOf(friendList.get(i));
         }
       
        
        String check;
        if (Redis_W.getInstance().sadd(key, friends) == friends.length)
            check = "true";
        else
            check = "false";
        
        JSONObject mapjson = new JSONObject();
       mapjson.put(ShareMacros.SUSSCES, check);
       out(mapjson.toJSONString(), resp);
     }
}
