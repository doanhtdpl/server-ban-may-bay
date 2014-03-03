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

/**
 *
 * @author LinhTA
 */
public class DeviceAction {
    
    private DeviceAction() {
    }
    
    public static DeviceAction getInstance() {
        return DeviceActionHolder.INSTANCE;
    }
    
    private static class DeviceActionHolder {

        private static final DeviceAction INSTANCE = new DeviceAction();
    }
    
    public void addDevice ( HttpServletRequest req, HttpServletResponse resp )
    {
         Map<String, String> reqData = new HashMap<String,String>();
        reqData = getDataJsonReq(req);
        
         String deviceId = reqData.get(ShareMacros.DEVICEID);
        String faceId = reqData.get(ShareMacros.FACEID);
        String token = reqData.get(ShareMacros.TOKEN);
        String config = reqData.get(ShareMacros.CONFIG);
        String phone = reqData.get(ShareMacros.PHONENUMBER);
        
        
        String key = KeysDefinition.getKeyDevice(faceId, deviceId);
        Map<String,String> data = new HashMap<>();
        data.put(ShareMacros.TOKEN, token);
        data.put(ShareMacros.CONFIG, config);
        data.put(ShareMacros.PHONENUMBER, phone);
        
        String check;
        if (Redis_W.getInstance().hset(key, data) != "-1")
            check = "true";
        else
            check = "false";
        
        JSONObject mapjson = new JSONObject();
       mapjson.put(ShareMacros.SUSSCES, check);
       out(mapjson.toJSONString(), resp);
    }
    
    public void getDevice( HttpServletRequest req, HttpServletResponse resp )
    {
         Map<String, String> reqData = new HashMap<String,String>();
        reqData = getDataJsonReq(req);
        
         String deviceId = reqData.get(ShareMacros.APPID);
        String faceId = reqData.get(ShareMacros.FACEID);
        
        String key = KeysDefinition.getKeyDevice(faceId, deviceId);
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
               getDevice(req, resp); 
            else
                addDevice(req, resp);
            
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
