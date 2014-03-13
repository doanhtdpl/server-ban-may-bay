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
import libCore.Util;
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
         Map<String,String> ids = new HashMap<String,String>();
        ids = Util.getUserId(reqData);
        String meID = ids.get(ShareMacros.MEID);
        String faceID = ids.get(ShareMacros.FACEID);
        String uid = ids.get(ShareMacros.ID);
        String score =String.valueOf( reqData.get(ShareMacros.SCORE));
         String time = String.valueOf(UtilTime.getTimeNow());//String.valueOf(reqData.get(ShareMacros.TIME));
        
        String key = KeysDefinition.getKeyAppUser(uid, appId);
        
        String check;
        JSONObject mapjson = new JSONObject();
        String oldScoreStr = Redis_Rd.getInstance().Hget(key, ShareMacros.SCORE);
        long oldScore = 0;
        if(oldScoreStr != null)
            oldScore = Long.parseLong(oldScoreStr);
        Long newScore = Long.parseLong(score);
        if( oldScore >= newScore)
        {
            check = "false";
            mapjson.put(ShareMacros.SCORE, String.valueOf(oldScore));
        }
        else
        {
            Map<String,String> data = new HashMap<>();
            data.put(ShareMacros.SCORE,score);
            data.put(ShareMacros.TIME, time);
            
            String keyAppScores = KeysDefinition.getKeyAppScores(appId);

            if (Redis_W.getInstance().hset(key, data) != "-1" && Redis_W.getInstance().zAdd(keyAppScores, Double.parseDouble(score),uid) != -1)
                check = "true";
            else
                check = "false";
        
        }
        
       
        
       mapjson.put(ShareMacros.SUSSCES, check);
       mapjson.put(ShareMacros.SCORE, score);
        out(mapjson.toJSONString(), resp);
    }
    
    public void getScore( HttpServletRequest req, HttpServletResponse resp )
    {
        Map<String, String> reqData = new HashMap<String,String>();
        reqData = getDataJsonReq(req);
        
         String appId = reqData.get(ShareMacros.APPID);
         Map<String,String> ids = new HashMap<String,String>();
        ids = Util.getUserId(reqData);
        String meID = ids.get(ShareMacros.MEID);
        String faceID = ids.get(ShareMacros.FACEID);
        String uid = ids.get(ShareMacros.ID);
        
        String key = KeysDefinition.getKeyAppUser(uid, appId);
        Map<String,String> data = new HashMap<>();
        
       data =  Redis_Rd.getInstance().hget(key);
       if(data.isEmpty())
       {
           data.put(ShareMacros.SCORE, "-1");
           data.put(ShareMacros.TIME, "-1");
       }
       JSONObject mapjson = new JSONObject();
       mapjson.putAll(data);
       
        out(mapjson.toJSONString(), resp);
    }
    
    public void getTopHighScore(HttpServletRequest req, HttpServletResponse resp)
    {
         Map<String, String> reqData = new HashMap<String,String>();
        reqData = getDataJsonReq(req);
        String appId = reqData.get(ShareMacros.APPID);
        String countStr = reqData.get(ShareMacros.TOPSCORECOUNT);
        long count = Long.valueOf(countStr);
        
        String key = KeysDefinition.getKeyAppScores(appId);
        List<Object> data = new ArrayList<Object>();
        data = getHighScore(key, 0, count);
       
        JSONObject mapjson = new JSONObject();
        mapjson.put(ShareMacros.APPID, appId);
        mapjson.put(ShareMacros.FRIENDLIST, data);
       
        out(mapjson.toJSONString(), resp);
    }
    
    public void getHighScoreList(HttpServletRequest req, HttpServletResponse resp)
    {
         Map<String, String> reqData = new HashMap<String,String>();
        reqData = getDataJsonReq(req);
        
        String appId = reqData.get(ShareMacros.APPID);
        String startStr = reqData.get(ShareMacros.HIGHSCORESTART);
        String endStr = reqData.get(ShareMacros.HIGHSCOREEND);
        long start = Long.valueOf(startStr);
        long end = Long.valueOf(endStr);        
        String key = KeysDefinition.getKeyAppScores(appId);       
       
        List<Object> data = new ArrayList<Object>();
        data = getHighScore(key, start, end);
       
        JSONObject mapjson = new JSONObject();
        mapjson.put(ShareMacros.APPID, appId);
        mapjson.put(ShareMacros.FRIENDLIST, data);
       
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
        resp.addHeader("Server", "BanMayBay");
    }
    
     public void handle(HttpServletRequest req, HttpServletResponse resp) {
        try {
            prepareHeader(resp);
            
            Object objTop = req.getAttribute("topscore");
            
            if( objTop != "" && objTop != null)
            {
                 String topScore =objTop.toString();
                if(topScore == ShareMacros.METHODTOP)
                {
                    getTopHighScore(req, resp);
                }
                else if(topScore == ShareMacros.METHODLIST)
                {
                    getHighScoreList(req, resp);
                }
            }            
            else
            {
                if(req.getParameter(ShareMacros.METHOD).matches("get"))
                    getScore(req, resp);
                else
                    updateScore(req, resp);
            }
            
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
     
     public List<Object> getHighScore(String key,long start, long end)
     {
        Map<String,Double> dataDB = new HashMap<String,Double>();
        dataDB = Redis_Rd.getInstance().zgetTopHighScore(key, start, end);
        
         List<Object> data = new ArrayList<Object>();
       // Map<String,String> data = new HashMap<String,String>();
        long i = start;
        for (Map.Entry<String, Double> entry : dataDB.entrySet()) {           
           
            String k = entry.getKey();
            Double sc = entry.getValue();
             
            Map<String,String> user = new HashMap<String,String>();
            Map<String,String> ids = new HashMap<String,String>();
            ids = KeysDefinition.getUidResponse(k);
            
            user.put(ShareMacros.FACEID, ids.get(ShareMacros.FACEID));
            user.put(ShareMacros.MEID, ids.get(ShareMacros.MEID));
            user.put(ShareMacros.SCORE, sc.toString());
            user.put(ShareMacros.INDEX, String.valueOf(i));
            
            data.add(user);
            i++;
            
        }
        
        return data;
     }
     
     public static void main(String[] args) {
        String a ="FB_fgvbmnbnmb";
         String[] b= a.split("_");
         System.out.print(b[1]);
    }
}
