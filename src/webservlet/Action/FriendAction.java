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
import Model.ModelItem;
import Model.Request.ClientRequest;
import Security.Scr_Base64;
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
                getFriendList3(request, resp);
            else if(request._method.equals("set"))
                setFriendList(request, resp);
            else if(request._method.equals("getGift"))
                getGift(request, resp);
            
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
     
     public void getGift(ClientRequest req , HttpServletResponse resp)
     {
         Map<String,String> data = new HashMap<String,String>();
         
         String timeRevive = libCore.Config.getParam(ShareMacros.COUNTDOWN, ShareMacros.GIFT);
         long time = Long.parseLong(timeRevive);
         
         String fid = "";
         if(req._data.containsKey(ShareMacros.FRIENDID))
             fid = req._data.get(ShareMacros.FRIENDID);
         else
         {
             outFalse(resp);
             return;
         }
         
         ModelFriend modelF = new ModelFriend(req._uid, req._meID, req._fbID);
         long oldTime = modelF.getTimeGift(req._uid,fid);
         
         int max =Integer.valueOf( libCore.Config.getParam(ShareMacros.MAX, ShareMacros.LAZE));
         String countStr = getCountItem(req._uid, ShareMacros.LAZE, resp);
         int count = 0;
         if(countStr != null)
             count = Integer.parseInt(countStr);
         if(count >= max )
         {
              data.put(ShareMacros.SUSSCES, "false");
                 data.put(ShareMacros.TIMELAZE, String.valueOf(oldTime));
                 data.put(ShareMacros.LAZE, String.valueOf(count));
         }
         else
         {

            long newTime = utilities.time.UtilTime.getTimeNow();

            if((newTime - oldTime) >= time )
            {
                if(addLaze(req,fid,newTime))
                {   
                    data.put(ShareMacros.SUSSCES, "true");
                   data.put(ShareMacros.TIMELAZE, String.valueOf(newTime));
                }
                else
                {
                    data.put(ShareMacros.SUSSCES, "false");
                    data.put(ShareMacros.TIMELAZE, String.valueOf(oldTime));
                }
            }
            else
            {
                data.put(ShareMacros.SUSSCES, "false");
                data.put(ShareMacros.TIMELAZE, String.valueOf(oldTime));
            }
         }
         
            JSONObject mapjson = new JSONObject();
       mapjson.putAll(data);
       out(mapjson.toJSONString(), resp);
     }
     
     public boolean addLaze(ClientRequest req,String fid,long time)
     {
         boolean ret = false;
         String keyLaze = KeysDefinition.getKeyItem(req._uid, ShareMacros.LAZE);
         String lazeStr = "";
         lazeStr = Redis_Rd.getInstance().getRetries(keyLaze);
         if(lazeStr ==null)
             return false;
         
         int laze = 0;
         laze = Integer.parseInt(lazeStr);
         
         laze = laze + 1;
         
         long ret2 = Redis_W.getInstance().setRetries(keyLaze, String.valueOf(laze));
         long ret3 = Redis_W.getInstance().setRetries(KeysDefinition.getKeyFriendTime(req._uid, fid), String.valueOf(time));
         
         if(ret2 == -1 || ret3 == -1)
         {
             return false;
         }
         
         
         return true;
     }
     
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
     
      public String getCountItem(String uid,String type,HttpServletResponse resp)
      {
            String key = KeysDefinition.getKeyItem(uid, type);
             String ret = null;
             try
             {
                 ret = Redis_Rd.getInstance().getRetries(key);
             }
             catch(Exception e)
             {
                 
             }
             if(ret == null)
             {
                 String count = "0";
                 try
                {
                    count = libCore.Config.getParam(ShareMacros.DEFAULTUSER, type);
                }
                catch(Exception e)
                {
                    count = "0";
                }
                 
                 long ret2 =-1;
                try
                {
                    ret2= Redis_W.getInstance().setRetries(key, count);
                }
                catch(Exception e)
                {
                    
                }
                
                if(ret2 == -1)
                {
                    outFalse(resp);
                    return null;
                }
                else
                {
                    return count;
                }

            }
             else
             {
                 return ret;
             }
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
        
         Map<String,String> timeResendLife = new HashMap<String,String>();
         timeResendLife = ModelItem.getTimeSendLife(uid, appId);
         
        data.addAll(_friendMdl.getScoreFriends(friendHaveScore, appId,timeResendLife));
        data.addAll(_friendMdl.getFakeScoreFriends(friendNotHaveScore,timeResendLife));
        
       JSONObject mapjson = new JSONObject();
       mapjson.put(ShareMacros.FRIENDLIST,data);
       mapjson.put(ShareMacros.TIME,String.valueOf( utilities.time.UtilTime.getTimeNow()));
        out(mapjson.toJSONString(), resp);
     }
     
     
     
      private JSONObject defaultResponse_False()
    {
        JSONObject data = new JSONObject();
        List<Object> list = new ArrayList<Object>();
        data.put(ShareMacros.FRIENDLIST,list);
        data.put(ShareMacros.SUSSCES, "false");
        data.put(ShareMacros.TIME,"0" );
        return  data;
    }
      
        private void outFalse(HttpServletResponse resp)
       {
           JSONObject mapjson = new JSONObject();
            mapjson = defaultResponse_False();
            out(mapjson.toJSONString(), resp);
       }
}
