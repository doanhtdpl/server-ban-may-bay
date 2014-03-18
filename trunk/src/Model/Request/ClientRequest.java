/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model.Request;

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
            
           Map<String,String> ids = new HashMap<String,String>();
           ids = Util.getUserId(_data);
           _fbID = ids.get(ShareMacros.FACEID);
           _meID = ids.get(ShareMacros.MEID);
           _uid = ids.get(ShareMacros.ID);
           
            List<String> attributeNames = new ArrayList<String>();
            attributeNames= (ArrayList)req.getAttributeNames();            
            for (String att : attributeNames) 
            {
                _attribute.put(att, req.getAttribute(att));
            }
            
           Map<String,String[]> parameter = new HashMap<String,String[]>();
           parameter = req.getParameterMap();           
             if(parameter.containsKey(ShareMacros.DATA))
             {
                 _data = parseDataJsonReq(parameter.get(ShareMacros.DATA)[0]);
             }
             
             if(parameter.containsKey(ShareMacros.METHOD))
             {
                 _method = parameter.get(ShareMacros.METHOD)[0];
             }
            
             if(parameter.containsKey(ShareMacros.SIGN))
             {
                 _sign = parameter.get(ShareMacros.SIGN)[0];
             }
             
             if(parameter.containsKey(ShareMacros.APPID))
             {
                 _appId = parameter.get(ShareMacros.APPID)[0];
             }
            
        }
        
        public Map<String,String> parseDataJsonReq(String string)
        {
             Map<String,String> reqData = new HashMap<String,String>();

            String data2Json = string;

           Gson g = new Gson();
            JSONObject j = g.fromJson(data2Json, JSONObject.class);

            reqData = (HashMap<String,String>) j;

            return reqData;
        }
}
