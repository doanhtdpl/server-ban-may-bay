/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet.Action;

import Model.Request.ClientRequest;
import Security.Scr_Base64;
import DB_REDIS.Redis_Rd;
import DB_REDIS.Redis_W;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import share.KeysDefinition;

/**
 *
 * @author LinhTA
 */
public class BuyAction {
    
    public void buyLaze(ClientRequest req, HttpServletResponse resp)
    {
        
    }
    
    public void buyLife(ClientRequest req, HttpServletResponse resp)
    {
        
    }
       
    
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
            
            if(request._method.equals("laze"))
               buyLaze(request, resp); 
            else if(request._method.equals("life"))
                buyLife(request, resp);
            
        } catch (Exception ex) {
           // logger_.error("CampainAction.handle:" + ex.getMessage() + ", Username:" + req.getAttribute("ownerName").toString(), ex);
        }
    }
     
     public String getCoin(String uid)
     {
         String keyCoin = KeysDefinition.getKeyCoinUser(uid);
         String coin="";
          int retries = configuration.Configuration.retries;
         while(retries>0)
         {
             coin = Redis_Rd.getInstance().get(keyCoin);
             if(coin.isEmpty() || coin == null)
                 retries --;
             else break;
         }
               
         if(coin.isEmpty() || coin == null)
         {
             retries = configuration.Configuration.retries;
              while(retries>0)
            {
                long ret = Redis_W.getInstance().set(keyCoin,configuration.Configuration.Coin_UserDefault);
                if(ret == -1)
                    retries --;
                else break;
            }
         }
         
         return coin;
         
     }
    
}
