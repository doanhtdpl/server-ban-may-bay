/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet;

import Model.Request.ClientRequest;
import Security.Authenticate;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import libCore.Config;
import libCore.LogUtil;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.omg.CORBA.DATA_CONVERSION;
import share.ShareMacros;
import webservlet.Action.ItemAction;

/**
 *
 * @author LinhTA
 */
public class PkController extends ServerServlet{
    private static final Logger log = Logger.getLogger(ScoreController.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        try {
			doProcess(req, resp);
		} catch (Exception ex) {
			log.error(LogUtil.stackTrace(ex));
		}
    
    }

    
/*
            data.put("score","");
        data.put("type", "");
        data.put("fbId1","");
        data.put("meId1", "");
        data.put("fbId2", "");
        data.put("meId2","");
        data.put("type_money","");
        data.put("number","");
    */    
     private void doProcess(HttpServletRequest req, HttpServletResponse resp) {
        
         if(pingRedis(resp))
        {
            
        }
        else
        {
            Test_LogCSV.LogCSV.log("Redis Connect Fail", Config.getParam("redis", "host"));
            JSONObject mapJson = new JSONObject();
            mapJson = defaultResponse_False();
            echo(mapJson.toJSONString(), resp);
            
            return;
        }
        
         ClientRequest request = new ClientRequest(req);
        Authenticate auth = new Authenticate(request._appId,request._sign,request._fbID,request._meID);
      
        if(auth.checkAuth())
        {
            ItemAction action = new ItemAction(); 
            action.handle(request, resp);
        }
        else
        {
            JSONObject mapJson = new JSONObject();
            mapJson = defaultResponse_False();
            echo(mapJson.toJSONString(), resp);
        }
       
    }
    
     private JSONObject defaultResponse_False()
    {
        JSONObject data = new JSONObject();
        
        data.put(ShareMacros.SUSSCES, "false");
        return  data;
    }
    
}
