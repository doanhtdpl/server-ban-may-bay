/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet.Action;

import DB_REDIS.Redis_W;
import Entities.OBJ_PK;
import Model.ModelItem;
import Model.ModelPK;
import Model.ModelUser;
import Model.Request.ClientRequest;
import Security.Scr_Base64;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import share.KeysDefinition;
import share.ShareMacros;
import utilities.time.UtilTime;
import Model.ModelPK;
import java.util.ArrayList;
import libCore.Config;

/**
 *
 * @author LinhTA
 */
public class PkAction {
    
     private void out(String content, HttpServletResponse respon) {

        PrintWriter out = null;
        try {
            out = respon.getWriter();
            out.print(Scr_Base64.Encode(content));
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
            
            if(request._method.equals("send"))
                sendPk(request, resp);
            else if(request._method.equals("response"))
                reqPk(request, resp);
            else if(request._method.equals("get"))
                getPks(request, resp);
            else if(request._method.equals("update"))
                updatePks(request,resp);
        } catch (Exception ex) {
           // logger_.error("CampainAction.handle:" + ex.getMessage() + ", Username:" + req.getAttribute("ownerName").toString(), ex);
        }
    }
     
    private void sendPk( ClientRequest req, HttpServletResponse resp )
    {
        String typeMoney = req._data.get(ShareMacros.TYPE);
        String uid = req._uid;
        String number = req._data.get(ShareMacros.MONEY);
        String score = req._data.get(ShareMacros.SCORE);
        String frdID = req._data.get(ShareMacros.FRIENDID);
        
        if(!checkMoney(uid, typeMoney, Long.getLong(number)))
        {
            outFalse(resp);
            return ;
        }
        
         ModelPK modelPk = new ModelPK(uid);
         
         //add pk
         boolean addPk = modelPk.newPK(frdID, score, typeMoney, number);
        if(!addPk)
        {
            outFalse(resp);
            return;
        }
        
        //add list u1
        boolean addList = modelPk.add2List_PK(frdID);
        if(!addList)
        {
            outFalse(resp);
            return;
        }
        
        //add list u2
        boolean addListFrd = modelPk.add2List_PK2Frd(frdID,req._meID,req._fbID);
        if(!addList)
        {
            outFalse(resp);
            return;
        }
        
        //-money         
        boolean subMoney = ModelItem.subItem(uid, typeMoney,Long.parseLong(number) );
        if(!subMoney)
        {
            outFalse(resp);
            return;
        }
        
        //out 
                
        JSONObject mapjson = new JSONObject();
              mapjson.put(ShareMacros.SUSSCES, "true");
       
        out(mapjson.toJSONString(), resp);
                       
        //push notifycation
        ModelUser modelUser = new ModelUser(uid);
        try{        
            Controller.CoreController.pushNotificationPK( req._meID, req._fbID, modelUser.getINFO().get(ShareMacros.NAME), frdID);
        }
        catch (Exception e)
        {
            
        }

    }
     
    
    private void reqPk( ClientRequest req, HttpServletResponse resp )
    {
        String idWinner = "";
         String idLosser = "";
        
         String idme_winner  = "";
         String idfb_winner = "";
         String id_frd = req._data.get(ShareMacros.FRIENDID);
         
         
        String frdID = "";
        String id = "";
        if(req._meID !=null && req._meID != "")
        {
            frdID =KeysDefinition.getKeyUserME( id_frd); 
            id = req._meID;
            
        }
        else if(req._fbID !=null && req._fbID != "")
        {
            frdID =KeysDefinition.getKeyUserFB(id_frd);
            id = req._fbID;
        }
        
        ModelPK modelPK = new ModelPK(frdID);
        OBJ_PK pk = modelPK.getPK2Me(id);
        
        if( pk._money <= 0 )
        {
            outFalse(resp);
            return;
        }
        
        long score = Long.parseLong(req._data.get(ShareMacros.SCORE));
        if(score > pk._score)
        {
            idWinner= id;
            idLosser = frdID;
        }
        else 
        {
            idWinner = frdID;
            idLosser = id;
        }
        
        if(!modelPK.del2List_PK2Frd(id, req._meID, req._fbID))
            {            
                outFalse(resp);            
                return;        
            }
                  
        if(!modelPK.del2List_PK(id))
            {            
                outFalse(resp);            
                return;        
            }
        
        if(!modelPK.delPK(id))
            {            
                outFalse(resp);            
                return;        
            }
        
        if(!ModelItem.addItem(idWinner,pk._typeMoney,pk._money ))
            {            
                outFalse(resp);            
                return;        
            }
       
        if(!ModelItem.subItem(idLosser,pk._typeMoney,pk._money ))
            {            
                outFalse(resp);            
                return;        
            }
        
        JSONObject dataOut = new JSONObject();
        dataOut.put("winner", idWinner);
        
        out(dataOut.toJSONString(), resp);
        
        if(req._meID !=null && req._meID != "")
        {
            idme_winner = idWinner.equals(id) ? id : id_frd;
        }
        else if(req._fbID !=null && req._fbID != "")
        {
            idfb_winner = idWinner.equals(id) ? id : id_frd;
        }
        ModelUser modelUser = new ModelUser(idWinner);
        try{        
            Controller.CoreController.pushNotificationPK_response(id,frdID,idme_winner,idfb_winner, modelUser.getINFO().get(ShareMacros.NAME));
        }
        catch (Exception e)
        {
            
        }
        
    }
     
      private void getPks( ClientRequest req, HttpServletResponse resp )
    {
         String uid = req._uid;
         ModelPK modelPK = new ModelPK(uid);
         
         Map<String,Map<String,String>> data = modelPK.getListPk2Me();
         
         JSONObject dataObj = new JSONObject();
         dataObj.putAll(data);
         
         out(dataObj.toJSONString(), resp);
    }
     
      private void updatePks(ClientRequest req,HttpServletResponse resp)
      {
          Map<String,Map<String,String>> list = new HashMap<String,Map<String,String>>();
          Map<String,Long> money = new  HashMap<String,Long>();
          
          ModelPK modelPK = new ModelPK(req._uid);
          
          Map<String,Map<String,String>> data = new HashMap<String,Map<String,String>>();
          data = modelPK.getListPk();
          
          long now = UtilTime.getTimeNow();
          long timeCountdown = Long.parseLong(Config.getParam(ShareMacros.COUNTDOWN,ShareMacros.PK));
          
          for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
              String k = entry.getKey();
              Map<String, String> map = entry.getValue();
              OBJ_PK pk = new OBJ_PK(map);
              
              
              if(pk._time +timeCountdown > now  )
              {
                  if(!modelPK.del2List_PK2Frd(k, req._meID, req._fbID))
                    continue;
                  
                  if(!modelPK.del2List_PK(k))
                      continue;                 
                  
                  if(!modelPK.delPK(k))
                      continue;
                  
                  if(!ModelItem.addItem(req._uid,pk._typeMoney,pk._money ))
                      continue;
                  
                  Map<String ,String> dt = new HashMap<String,String>();
                  dt.put(ShareMacros.TYPE, pk._typeMoney);
                  dt.put(ShareMacros.MONEY, String.valueOf(pk._money));
                  list.put(k, dt);
              }
          }
          
          JSONObject jsonData = new JSONObject();
          jsonData.putAll(list);
          
          out(jsonData.toJSONString(), resp);
          
      }
     
    public boolean checkMoney(String uid,String type, long number)
  {
      long money = Long.getLong(  ModelItem.getCountItem(uid, type));
      if(money<number)
          return false;
      else
          return true;
  }
    
     public void outFalse(HttpServletResponse resp)
     {
         JSONObject mapjson = new JSONObject();
       mapjson = defaultResponse_False();
       
        out(mapjson.toJSONString(), resp);
     }
     
      private JSONObject defaultResponse_False()
    {
        JSONObject data = new JSONObject();
        data.put(share.ShareMacros.SUSSCES, "false");
         data.put(ShareMacros.Coin, "0");
         data.put(ShareMacros.NAME, "");
          data.put(ShareMacros.EMAIL, "");
        data.put(ShareMacros.FACEID, "");
       data.put(ShareMacros.MEID,"");
 
       
        return  data;
    }
    
      
}
