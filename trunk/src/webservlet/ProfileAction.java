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
    
    private ProfileAction() {
    }
    
    public static ProfileAction getInstance() {
        return ProfileActionHolder.INSTANCE;
    }
    
    private static class ProfileActionHolder {

        private static final ProfileAction INSTANCE = new ProfileAction();
    }
    
    public void updateInfo( HttpServletRequest req, HttpServletResponse resp )
    {
        Map<String, String> reqData = new HashMap<String,String>();
        reqData = getDataJsonReq(req);
        
         Map<String,String> ids = new HashMap<String,String>();
        ids = Util.getUserId(reqData);
        String meID = ids.get(ShareMacros.MEID);
        String faceID = ids.get(ShareMacros.FACEID);
        String uid = ids.get(ShareMacros.ID);
        
        
        String name = reqData.get(ShareMacros.NAME);
        String email = reqData.get(ShareMacros.EMAIL);
        
        
       
        Map<String,String> data = new HashMap<>();
        data.put(ShareMacros.NAME, name);
        data.put(ShareMacros.EMAIL, email);
        
        String check;
        if (Redis_W.getInstance().hset(uid, data) != "-1" && Redis_W.getInstance().sadd(KeysDefinition.getKeyUserList(),uid) != -1)
            check = "true";
        else
            check = "false";
        
        JSONObject mapjson = new JSONObject();
       mapjson.put(ShareMacros.SUSSCES, check);
       out(mapjson.toJSONString(), resp);
       
       String appId = reqData.get(ShareMacros.APPID);
        addAppId(uid, appId);
    }
    
    public void getInfo( HttpServletRequest req, HttpServletResponse resp )
    {
        Map<String, String> reqData = new HashMap<String,String>();
        reqData = getDataJsonReq(req);
        
        Map<String,String> ids = new HashMap<String,String>();
        ids = Util.getUserId(reqData);
        String meId = ids.get(ShareMacros.MEID);
        String faceId = ids.get(ShareMacros.FACEID);
        String uid = ids.get(ShareMacros.ID);
       
        Map<String,String> data = new HashMap<>();
        
       data =  Redis_Rd.getInstance().hget(uid);
       data.put(ShareMacros.FACEID, faceId);
       data.put(ShareMacros.MEID,meId);
       JSONObject mapjson = new JSONObject();
       mapjson.putAll(data);
       
        out(mapjson.toJSONString(), resp);
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
                getInfo(req, resp);
            else
                updateInfo(req, resp);
            
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
     
     private void addAppId(String uid, String appId)
     {
         boolean checkExits = false;
         
         String key = KeysDefinition.getKeyAppUser(uid, appId);
         checkExits = Redis_Rd.getInstance().isExits(key);
         
         if(!checkExits)
         {
             Map<String,String> dataFake = new HashMap<String,String>();
             dataFake.put(ShareMacros.SCORE, "0");
             dataFake.put(ShareMacros.TIME, "-1");
             
             Redis_W.getInstance().hset(key, dataFake);
         } 
     }
}
