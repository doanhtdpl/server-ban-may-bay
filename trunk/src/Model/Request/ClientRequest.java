/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model.Request;

import Security.Scr_Base64;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import share.ShareMacros;
import libCore.Util;

/**
 *
 * @author LinhTA
 */
public class ClientRequest {
    public Map<String,Object> _attribute; //atributes
        public Map<String,String> _data; //String json -> base 64
        public String _sign; //MD5(appId + keyOfApp + jsonData)
        public String _method; //method : set or get
        public String _appId; // appID 
        
        public String _uid; 
        public String _meID;
        public String _fbID;
    
        public ClientRequest(HttpServletRequest req)
        {
            _attribute = new HashMap<String,Object>(); 
            _data = new HashMap<String,String>(); 
            _sign = ""; 
            _method = "";
            _appId = ""; 
            
            _uid = "";
            _meID = "";
            _fbID = "";
            
            List<String> attributeNames = new ArrayList<String>();
            attributeNames= (ArrayList)req.getAttributeNames();            
            for (String att : attributeNames) 
            {
                _attribute.put(att, req.getAttribute(att));
            }
		
            Map<String,String> dataReq = new HashMap<String,String>();
            dataReq = parseDataJsonReq(req);            
           
             if(dataReq.containsKey(ShareMacros.DATA))
             {
                 _data = Util.string2Map(dataReq.get(ShareMacros.DATA));//parseDataJsonReq(parameter.get(ShareMacros.DATA)[0]);
             }
             
             if(dataReq.containsKey(ShareMacros.METHOD))
             {
                 _method = dataReq.get(ShareMacros.METHOD);
             }
            
             if(dataReq.containsKey(ShareMacros.SIGN))
             {
                 _sign = dataReq.get(ShareMacros.SIGN);
             }
             
             if(dataReq.containsKey(ShareMacros.APPID))
             {
                 _appId = dataReq.get(ShareMacros.APPID);
             }
             
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

                data2Json = string;
            }
        
            data2Json = Scr_Base64.Decode(data2Json);           

            reqData = Util.string2Map(data2Json);

            return reqData;
        }
}
