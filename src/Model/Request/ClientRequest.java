/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model.Request;

import Security.Scr_Base64;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import share.ShareMacros;
import libCore.Util;
import org.eclipse.jetty.util.ajax.JSON;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author LinhTA
 */
public class ClientRequest {
    public Map<String,String> _attribute; //atributes
        public Map<String,String> _data; //String json -> base 64
        public String _sign; //MD5(appId + keyOfApp + jsonData)
        public String _method; //method : set or get
        public String _appId; // appID 
        
        public String _uid; 
        public String _meID;
        public String _fbID;
    
        public ClientRequest(HttpServletRequest req)
        {
            _attribute = new HashMap<String,String>(); 
            _data = new HashMap<String,String>(); 
            _sign = ""; 
            _method = "";
            _appId = ""; 
            
            _uid = "";
            _meID = "";
            _fbID = "";
            
		
            Map<String,String> dataReq = new HashMap<String,String>();
            dataReq = parseDataJsonReq(req);            
            
            for (Map.Entry<String, String> entry : dataReq.entrySet()) {
                String k = entry.getKey();
                
                
            if(k.equals(ShareMacros.DATA))
             {
                 String val=  JSON.toString(entry.getValue());              
                 
                 _data = Util.string2Map(val);//Util.string2Map(val);//parseDataJsonReq(parameter.get(ShareMacros.DATA)[0]);
                 
                 Map<String,String> ids = new HashMap<String,String>();
                 ids = Util.getUserId(_data);
                 _uid = ids.get(ShareMacros.ID);
                 _fbID = ids.get(ShareMacros.FACEID);
                 _meID = ids.get(ShareMacros.MEID);
                 
             }
             else
             if(k.equals(ShareMacros.METHOD))
             {
                 String val = entry.getValue();
                 _method =val;
             }
            else
             if(k.equals(ShareMacros.SIGN))
             {
                 String val = entry.getValue();
                 _sign = val.toLowerCase();
             }
             else
             if(k.equals(ShareMacros.APPID))
             {
                 String val = entry.getValue();
                 _appId = val;
             }
             else
             {
                 String val = entry.getValue();
                 _attribute.put(k, val);
             }
            }
             
             Test_LogCSV.LogCSV.log("clientRequest","appID:"+ _appId+","+"fbid:"+_fbID+","+"method:"+_method);
        }
        
        public Map<String,String> parseDataJsonReq(HttpServletRequest req)
        {
             String data2Json = "";
             Map<String,String> reqData = new HashMap<String,String>();

             Map<String,String[]> parameter = new HashMap<String,String[]>();
                parameter = req.getParameterMap();
                 
            for (Map.Entry<String, String[]> entry : parameter.entrySet()) {
                String string = entry.getKey();
                String[] strings = entry.getValue();

                data2Json = strings[0];
            }
        
            data2Json = Scr_Base64.Decode(data2Json);           

            reqData = Util.string2Map(data2Json);

            return reqData;
        }
}
