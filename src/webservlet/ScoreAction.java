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
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.util.ajax.JSON;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import share.KeysDefinition;
import share.ShareMacros;
import utilities.time.UtilTime;

/**
 *
 * @author LinhTA
 */
public class ScoreAction {
    
    private ScoreAction() {
    }
    
    public static ScoreAction getInstance() {
        return ScoreActionHolder.INSTANCE;
    }
    
    private static class ScoreActionHolder {

        private static final ScoreAction INSTANCE = new ScoreAction();
    }
    
     public void updateScore( HttpServletRequest req, HttpServletResponse resp )
    {
        Map<String, String> reqData = new HashMap<String,String>();
        reqData = getDataJsonReq(req);
        
        String appId = reqData.get(ShareMacros.APPID);
        String faceId = reqData.get(ShareMacros.FACEID);
        String score =String.valueOf( reqData.get(ShareMacros.SCORE));
         String time = String.valueOf(UtilTime.getTimeNow());//String.valueOf(reqData.get(ShareMacros.TIME));
        
        String key = KeysDefinition.getKeyAppUser(faceId, appId);
        Map<String,String> data = new HashMap<>();
        data.put(ShareMacros.SCORE,score);
        data.put(ShareMacros.TIME, time);
        
        String check;
        if (Redis_W.getInstance().hset(key, data) != "-1")
            check = "true";
        else
            check = "false";
        
        JSONObject mapjson = new JSONObject();
       mapjson.put(ShareMacros.SUSSCES, check);
       
        out(mapjson.toJSONString(), resp);
    }
    
    public void getScore( HttpServletRequest req, HttpServletResponse resp )
    {
        Map<String, String> reqData = new HashMap<String,String>();
        reqData = getDataJsonReq(req);
        
         String appId = reqData.get(ShareMacros.APPID);
        String faceId = reqData.get(ShareMacros.FACEID);
        
        String key = KeysDefinition.getKeyAppUser(faceId, appId);
        Map<String,String> data = new HashMap<>();
        
       data =  Redis_Rd.getInstance().hget(key);
       
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
                getScore(req, resp);
            else
                updateScore(req, resp);
            
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
}
