/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet.Action;

import Model.Request.ClientRequest;
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
public class DeviceAction {
    
    public DeviceAction() {
    }
    
   
    public void addDevice ( ClientRequest req, HttpServletResponse resp )
    {
         
        
        String deviceId = req._data.get(ShareMacros.DEVICEID);
        
      
        String uId = req._uid;//ids.get(ShareMacros.ID);
        
        String token = req._data.get(ShareMacros.TOKEN);
        String config = req._data.get(ShareMacros.CONFIG);
        String phone = req._data.get(ShareMacros.PHONENUMBER);
        
        
        String key = KeysDefinition.getKeyDevice(uId, deviceId);
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
    
    public void getDevice( ClientRequest req, HttpServletResponse resp )
    {
        
        
         String deviceId = req._data.get(ShareMacros.DEVICEID);
        
        String meID = req._meID;//ids.get(ShareMacros.MEID);
        String faceID = req._fbID;//ids.get(ShareMacros.FACEID);
        String uid = req._uid;//ids.get(ShareMacros.ID);
         
        
        String key = KeysDefinition.getKeyDevice(uid, deviceId);
        Map<String,String> data = new HashMap<>();
        
       data =  Redis_Rd.getInstance().hget(key);
       data.put(ShareMacros.DEVICEID, deviceId);
       data.put(ShareMacros.FACEID, faceID);
       data.put(ShareMacros.MEID, meID);
       data.put(ShareMacros.MEID, meID);
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
        resp.addHeader("Server", "DBPTK");
    }
    
     public void handle(ClientRequest request, HttpServletResponse resp) {
        try {
            prepareHeader(resp);
            
            if(request._method.matches("get"))
               getDevice(request, resp); 
            else
                addDevice(request, resp);
            
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
           
}
