/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet.Action;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import db.Redis_Rd;
import db.Redis_W;
import java.io.PrintWriter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import share.KeysDefinition;
import share.ShareMacros;
import  libCore.Util;
import Model.ModelFriend;
import Model.Request.ClientRequest;
/**
 *
 * @author LinhTA
 */
public class FriendAction {
    
    public FriendAction() {
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
        resp.addHeader("Server", "DBPTK");
    }
    
     public void handle(ClientRequest request, HttpServletResponse resp) {
        try {
            prepareHeader(resp);
            
            if(request._method.equals("get"))
                getFriendList3(request, resp);
            else
                setFriendList(request, resp);
            
        } catch (Exception ex) {
           // logger_.error("CampainAction.handle:" + ex.getMessage() + ", Username:" + req.getAttribute("ownerName").toString(), ex);
        }
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
     
//     public void getFriendList(HttpServletRequest req , HttpServletResponse resp)
//     {
//         Map<String, String> reqData = new HashMap<String,String>();
//        reqData = getDataJsonReq(req);
//        
//        String faceId = reqData.get(ShareMacros.FACEID);
//        String appId = reqData.get(ShareMacros.APPID);
//        
//        String key = KeysDefinition.getKey_ListFriends(faceId);
//        Set<String> friendsFace = new HashSet<String>();        
//       friendsFace =  Redis_Rd.getInstance().smember(key);
//       
////         for (int i = 0; i < friendsFace.size(); i++) 
////         {
////             String friendId = KeysDefinition.getKeyUser(friendsFace.toArray()[i].toString());
////             if(Redis_Rd.getInstance().isExits( friendId) == false)
////             {
////                 friendsFace.remove(friendId);
////             }
////            
////         }
//       
//         List<Object> data = new ArrayList<Object>();
//        
//        for(int j = 0; j < friendsFace.size(); j++) 
//        {
//            String friendId = friendsFace.toArray()[j].toString();
//            
//            Map<String,String> ids = new HashMap<String,String>();
//            ids = Util.getUserId(reqData);
//            String meID = ids.get(ShareMacros.MEID);
//            String faceID = ids.get(ShareMacros.FACEID);
//            String uid = ids.get(ShareMacros.ID);
//        
//            String name = Redis_Rd.getInstance().Hget(uid, ShareMacros.NAME);
//            String score = Redis_Rd.getInstance().Hget(KeysDefinition.getKeyAppUser(uid, appId), ShareMacros.SCORE);
//            if(score != null && name != null)
//            {
//                Map<String,String> friendProfile = new HashMap<String,String>();
//                friendProfile.put(ShareMacros.NAME, name);
//                friendProfile.put(ShareMacros.SCORE, score);
//                friendProfile.put(ShareMacros.FACEID, friendId);
//                
//                data.add( friendProfile);
//            } 
//                
//        }
//        
//       
//       JSONObject mapjson = new JSONObject();
//       mapjson.put(ShareMacros.FRIENDLIST,data);
//       
//        out(mapjson.toJSONString(), resp);
//     }
     
     public void setFriendList(ClientRequest req , HttpServletResponse resp)
     {
        Gson gson = new Gson();
        
         String listStr = gson.toJson(req._data.get(ShareMacros.FRIENDLIST));
         
         ArrayList<String> friendList = (ArrayList<String>) JSONValue.parse( listStr );
        
        String meID = req._meID;//ids.get(ShareMacros.MEID);
        String faceID = req._fbID;//ids.get(ShareMacros.FACEID);
        String uid = req._uid;//ids.get(ShareMacros.ID);
        
        String key = KeysDefinition.getKey_ListFriends(uid);
      
        String[] friends = new String[friendList.size()]; 
         for (int i = 0; i < friendList.size(); i++) {
             friends[i]= String.valueOf(friendList.get(i));
         }
       
        
        String check;
        if (Redis_W.getInstance().sadd(key, friends) != -1)
            check = "true";
        else
            check = "false";
        
        JSONObject mapjson = new JSONObject();
       mapjson.put(ShareMacros.SUSSCES, check);
       out(mapjson.toJSONString(), resp);
     }
     
//     public void getFriendList2(HttpServletRequest req , HttpServletResponse resp)
//     {
//         Map<String, String> reqData = new HashMap<String,String>();
//        reqData = getDataJsonReq(req);
//        
//         Map<String,String> ids = new HashMap<String,String>();
//        ids = Util.getUserId(reqData);
//        String meID = ids.get(ShareMacros.MEID);
//        String faceID = ids.get(ShareMacros.FACEID);
//        String uid = ids.get(ShareMacros.ID); 
//        
//        String appId = reqData.get(ShareMacros.APPID);
//        
//        String[] key =  new String[2];
//       key[0] =  KeysDefinition.getKey_ListFriends(uid);
//       key[1] = KeysDefinition.getKeyUserList(appId);
//        Set<String> friends = new HashSet<String>();        
//       friends =  Redis_Rd.getInstance().sInter(key);
//       
//       
//         List<Object> data = new ArrayList<Object>();
//        
//        for(int j = 0; j < friends.size(); j++) 
//        {
//            String friendId = friends.toArray()[j].toString();
//            String keyFriend = "";
//            boolean isFaceUser = false;
//            if(faceID =="" || faceID==null || faceID.isEmpty())
//                keyFriend = KeysDefinition.getKeyUserME(friendId);
//            else
//            {
//                keyFriend = KeysDefinition.getKeyUserFB(friendId);
//                isFaceUser = true;
//            }
//            String name = Redis_Rd.getInstance().Hget(keyFriend, ShareMacros.NAME);
//            String score = Redis_Rd.getInstance().Hget(KeysDefinition.getKeyAppUser(keyFriend, appId), ShareMacros.SCORE);
//            if(score != null && name != null)
//            {
//                Map<String,String> friendProfile = new HashMap<String,String>();
//                friendProfile.put(ShareMacros.NAME, name);
//                friendProfile.put(ShareMacros.SCORE, score);
//                friendProfile.put(ShareMacros.ID, friendId);
//                
//                data.add( friendProfile);
//            } 
//                
//        }
//        
//       
//       JSONObject mapjson = new JSONObject();
//       mapjson.put(ShareMacros.FRIENDLIST,data);
//       
//        out(mapjson.toJSONString(), resp);
//     }
     
     public void getFriendList3(ClientRequest req , HttpServletResponse resp)
     {
         
        String meID = req._meID;
        String faceID = req._fbID;
        String uid = req._uid;
        
        String appId = req._appId;
        
        ModelFriend _friendMdl = new ModelFriend(uid, meID, appId);
        
        List<String> friendHaveScore = new ArrayList<String>();
       List<String> friends_list= new ArrayList<String>();
        List<String> friendApp = new ArrayList<String>();
        if(meID != "" && !meID.isEmpty() )
        {
            friendApp= _friendMdl.getMEFriendApp(uid, appId);
            friends_list.addAll(friendApp);
        }
        else if ((faceID != "" && !faceID.isEmpty() ))
        {
            friendApp = ModelFriend.getFBFriendApp(uid, appId);
            friends_list.addAll(friendApp);
        }
        
        friendHaveScore = _friendMdl.getFriendHaveScore(friends_list,appId);
       List<String> friendNotHaveScore = new ArrayList<String>(); 
       friendNotHaveScore = friends_list;
       friendNotHaveScore.removeAll(friendHaveScore);
       
         List<Object> data = new ArrayList<Object>();
        
        data.addAll(_friendMdl.getScoreFriends(friendHaveScore, appId));
        data.addAll(_friendMdl.getFakeScoreFriends(friendNotHaveScore));
        
       JSONObject mapjson = new JSONObject();
       mapjson.put(ShareMacros.FRIENDLIST,data);
       
        out(mapjson.toJSONString(), resp);
     }
     
     
     
}
