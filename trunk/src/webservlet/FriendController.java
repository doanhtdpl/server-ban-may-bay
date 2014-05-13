/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet;

import Model.Request.ClientRequest;
import Security.Authenticate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import libCore.Config;
import libCore.LogUtil;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import share.ShareMacros;
import webservlet.Action.FriendAction;

/**
 *
 * @author LinhTA
 */
public class FriendController extends ServerServlet{
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
            FriendAction action = new FriendAction(); 
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
        List<Object> list = new ArrayList<Object>();
        data.put(ShareMacros.FRIENDLIST,list);
        data.put(ShareMacros.SUSSCES, "false");
        data.put(ShareMacros.TIME,"0" );
        return  data;
    }
    
    
}
