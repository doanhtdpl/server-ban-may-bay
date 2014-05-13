/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet.Action;

import Model.ModelItem;
import Model.Request.ClientRequest;
import Security.Scr_Base64;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import db.Redis_Rd;
import db.Redis_W;
import java.io.PrintWriter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.util.ajax.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import share.KeysDefinition;
import share.ShareMacros;

/**
 *
 * @author LinhTA
 */
public class ItemAction {
    
    public List<String> _items = Arrays.asList(ShareMacros.LAZE,ShareMacros.Coin);
    
    

    private void prepareHeader(HttpServletResponse resp) {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html; charset=UTF-8");
        resp.addHeader("Server", "DBPTK");
    }
    
     public void useLife(ClientRequest req, HttpServletResponse resp)
     {
         Map<String,String> data = new HashMap<String,String>();
         
         Map<String,String> timeVsLife = new HashMap<String,String>();
         timeVsLife = ModelItem.getLifeVSTime(req._uid, resp);
         
         int life = Integer.parseInt(timeVsLife.get(ShareMacros.LIFE));
         long timeOld = Long.parseLong(timeVsLife.get(ShareMacros.LASTTIME));
         
         if( life>= 1 )
         {
             long timeNow = utilities.time.UtilTime.getTimeNow();
             long timeAdd = Long.parseLong(libCore.Config.getParam(ShareMacros.COUNTDOWN, ShareMacros.LIFE));
             
              String ret ="";
              int lifeMax = Integer.parseInt(libCore.Config.getParam(ShareMacros.MAX, ShareMacros.LIFE));
             
              long time = timeOld;
             if(timeNow >= (timeAdd+timeOld) || life == lifeMax)
                time = timeNow;
              
              life = life -1;          
             
            
             
             data.put(ShareMacros.LIFE,String.valueOf(life));
             data.put(ShareMacros.LASTTIME, String.valueOf(time));
             
             ret = Redis_W.getInstance().hset(req._uid, data);
             
             if(ret == null )
             {
                 outFalse(resp);
                 return;
             }
             else
             {
                 data.put(ShareMacros.SUSSCES, "true");
                 data.putAll(data);             
             }
         }
         else
         {
             data.put(ShareMacros.SUSSCES, "false");
             data.put(ShareMacros.LIFE, String.valueOf(life));
             data.put(ShareMacros.LASTTIME, "-1");
         }
         
         data.put(ShareMacros.TIME, String.valueOf(utilities.time.UtilTime.getTimeNow()));
            JSONObject mapjson = new JSONObject();
       mapjson.putAll(data);
        ModelItem.out(mapjson.toJSONString(), resp);
     }
    
     public void handle(ClientRequest request, HttpServletResponse resp) {
        try {
            prepareHeader(resp);
            
            switch(request._method)
            {
                case "get":
                    getItems(request, resp);
                    break;
                case "buy":
                     buyItems(request, resp);
                    break;
                case "add":
                    addItems(request,resp);
                    break;
               case "use":
                   useItems(request,resp);
                   break;
                case "useLife":
                    useLife(request, resp);
                   break;
                case "getLife":
                    getLife(request, resp);
                   break;
                  
               
               default:
                   outFalse(resp);
                   
                   
            }
//            if(request._method.equals("get"))
//                getItems(request, resp);
//            else if (request._method.equals("buy"))
//                buyItems(request, resp);
            
        } catch (Exception ex) {
           // logger_.error("CampainAction.handle:" + ex.getMessage() + ", Username:" + req.getAttribute("ownerName").toString(), ex);
        }
    }
     
     
     
     
     public void addItems(ClientRequest req, HttpServletResponse resp)
     {
         Map<String,String> data = new HashMap<String,String>();
          
          String item = "";
          if(req._data.containsKey(ShareMacros.TYPE))
              item = req._data.get(ShareMacros.TYPE);
          else
          {
              outFalse(resp);
              return;
          }
          
           int max = 0;
           try
           {
               max = Integer.valueOf( libCore.Config.getParam(ShareMacros.MAX, item));
           }
           catch(Exception e)
           {}
           
         String countStr = ModelItem.getCountItem(req._uid, item);
         int countIt = 0;
         if(countStr != null)
             countIt = Integer.parseInt(countStr);
         else
         {
             outFalse(resp);
             return;
         }
         if(max != 0 && countIt >= max )
         {
             data.put(ShareMacros.SUSSCES, "false");                
                data.put(ShareMacros.TYPE, item);
                data.put(ShareMacros.COUNT, String.valueOf(countIt));    
         }
         else
         {
          

            int count = 0;
            count = countIt + 1;
           
            String key = KeysDefinition.getKeyItem(req._uid, item);
            long ret = Redis_W.getInstance().setRetries(key, String.valueOf(count));
            if(ret == -1)
            {
                outFalse(resp);
                return;
            }

                data.put(ShareMacros.SUSSCES, "true");
                data.put(ShareMacros.TYPE, item);
                data.put(ShareMacros.COUNT, String.valueOf(count));   
            
         }
          
          JSONObject mapjson = new JSONObject();
            mapjson.putAll(data);
            ModelItem.out(mapjson.toJSONString(), resp);
     }
     
     public void useItems(ClientRequest req, HttpServletResponse resp)
     {
          Map<String,String> data = new HashMap<String,String>();
          
          String item = "";
          if(req._data.containsKey(ShareMacros.TYPE))
              item = req._data.get(ShareMacros.TYPE);
          else
          {
              outFalse(resp);
              return;
          }
           
         String countStr = ModelItem.getCountItem(req._uid, item);
         int countIt = 0;
         if(countStr != null)
             countIt = Integer.parseInt(countStr);
         else
         {
             outFalse(resp);
             return;
         }
         
         if( countIt <= 0 )
         {
             data.put(ShareMacros.SUSSCES, "false");                
                data.put(ShareMacros.TYPE, item);
                data.put(ShareMacros.COUNT, String.valueOf(countIt));    
         }
         else
         {
         
            try
            {
              
            }
            catch(Exception e)
            {
                outFalse(resp);
                return;
            }

            int count = 0;
            count = countIt - 1;
           
            String key = KeysDefinition.getKeyItem(req._uid, item);
            long ret = Redis_W.getInstance().setRetries(key, String.valueOf(count));
            if(ret == -1)
            {
                outFalse(resp);
                return;
            }

                data.put(ShareMacros.SUSSCES, "true");
                data.put(ShareMacros.TYPE, item);
                data.put(ShareMacros.COUNT, String.valueOf(count));   
            
         }
          
          JSONObject mapjson = new JSONObject();
            mapjson.putAll(data);
            ModelItem.out(mapjson.toJSONString(), resp);
     }
     
     public void getItems(ClientRequest req, HttpServletResponse resp)
     {
         Map<String,String> data = new HashMap<String,String>();
         
         List<String> items = new ArrayList<String>();
         items = getListItems(req);
         for (String item : items) 
         {
             String count = ModelItem.getCountItem(req._uid, item);
             if(count == null)
             {
                 outFalse(resp);
                 return;
             }
             else 
                 data.put(item, count);
                 
         }
        data.put(ShareMacros.SUSSCES, "true");
        
        JSONObject mapjson = new JSONObject();
        mapjson.putAll(data);
        ModelItem.out(mapjson.toJSONString(), resp);
     }
     
      public void buyItems(ClientRequest req, HttpServletResponse resp)
     {
         
         
         Map<String,String> data = new HashMap<String,String>();
         
         long coin = 0;
          String keycoin = KeysDefinition.getKeyItem(req._uid,ShareMacros.Coin );
          
          String coinStr = ModelItem.getCountItem(req._uid, ShareMacros.Coin);
          Test_LogCSV.LogCSV.log("Coin : ", coinStr);
          if(coinStr == null)
              return;          
          coin = Long.valueOf(coinStr);
          
          String item = "";
          if(req._data.containsKey(ShareMacros.TYPE))
              item = req._data.get(ShareMacros.TYPE);
          else
          {
              outFalse(resp);
              return;
          }
          
            
           if(item == "revive")
          {
              buyRevive(req,resp,keycoin,coin,item);
              return;
          }
          
          
           int max = 0;
           try
           {
               max = Integer.valueOf( libCore.Config.getParam(ShareMacros.MAX, item));
           }
           catch(Exception e)
           {}
         
          
         String countStr = ModelItem.getCountItem(req._uid, item);
         int countIt = 0;
         if(countStr == null)
         {
             outFalse(resp);
             return;
         }
            
         countIt = Integer.parseInt(countStr);
         
         
          int count = 0;
            if(req._data.containsKey(ShareMacros.COUNT))
                count = Integer.valueOf(req._data.get(ShareMacros.COUNT));
            else
            {
                count = 0;
            }
         
         if(max != 0 && (countIt +count) > max )
         {
             data.put(ShareMacros.SUSSCES, "false");
                data.put(ShareMacros.Coin, String.valueOf(coin));
                data.put(ShareMacros.TYPE, item);
                data.put(ShareMacros.COUNT, String.valueOf(countIt));    
         }
         else
         {
          
            long requireCoin = 0;
            try
            {
              String requireCoinStr = libCore.Config.getParam(ShareMacros.PRICE, item);
              requireCoin = Long.valueOf(requireCoinStr);
            }
            catch(Exception e)
            {
                outFalse(resp);
                return;
            }

            

            if(requireCoin*count <= 0)
            {
                outFalse(resp);
                return;
            }

            if(requireCoin*count > coin)
            {
                data.put(ShareMacros.SUSSCES, "false");
                data.put(ShareMacros.Coin, String.valueOf(coin));
                data.put(ShareMacros.TYPE, item);
                data.put(ShareMacros.COUNT, String.valueOf(count));             
            }
            else
            {
                coin = coin-(requireCoin*count);
                int newCount = countIt + count;
                 
                long ret2 = Redis_W.getInstance().setRetries(  KeysDefinition.getKeyItem(req._uid, item), String.valueOf(newCount));
                if(ret2 == -1)
                {
                    outFalse(resp);
                    return;
                }
                
                long ret = Redis_W.getInstance().setRetries(keycoin, String.valueOf(coin));
                
                if(ret == -1 )
                {
                    outFalse(resp);
                    return;
                }

                data.put(ShareMacros.SUSSCES, "true");
                data.put(ShareMacros.Coin, String.valueOf(coin));
                data.put(ShareMacros.TYPE, item);
                data.put(ShareMacros.COUNT, String.valueOf(count));   
            }
         }
          
          JSONObject mapjson = new JSONObject();
            mapjson.putAll(data);
            ModelItem.out(mapjson.toJSONString(), resp);
          
     }
      
      public void buyRevive(ClientRequest req, HttpServletResponse resp,String keycoin,long coin,String item)
      {
          Map<String,String> data = new HashMap<String,String>();
          
          long price = Long.parseLong(libCore.Config.getParam(ShareMacros.PRICE,item ));
          
          Test_LogCSV.LogCSV.log("BUY REVIVE", "coin:"+String.valueOf(coin)+","+"price:"+String.valueOf(price));
          
          if(price > coin)
          {
              data.put(ShareMacros.SUSSCES, "false");
                data.put(ShareMacros.Coin, String.valueOf(coin));
                data.put(ShareMacros.TYPE, item);
                data.put(ShareMacros.COUNT, "0");   
          }
          else
          {
              long newCoin = coin - price;
              long ret = Redis_W.getInstance().setRetries(keycoin, String.valueOf(newCoin));
              if(ret == -1)
              {
                  data.put(ShareMacros.SUSSCES, "false");
                data.put(ShareMacros.Coin, String.valueOf(coin));
                data.put(ShareMacros.TYPE, item);
                data.put(ShareMacros.COUNT, "0");  
              }
              else
              {
                data.put(ShareMacros.SUSSCES, "true");
                data.put(ShareMacros.Coin, String.valueOf(newCoin));
                data.put(ShareMacros.TYPE, item);
                data.put(ShareMacros.COUNT, "0");  
              }
          }
      }
      
      public void getLife(ClientRequest req, HttpServletResponse resp)
      {
          JSONObject mapjson = new JSONObject();
          mapjson.put(ShareMacros.TIME, String.valueOf(utilities.time.UtilTime.getTimeNow()));
             mapjson.putAll(ModelItem.getLifeVSTime(req._uid, resp));
             ModelItem.out(mapjson.toJSONString(), resp);
      }
      
      
      
      private List<String> getListItems(ClientRequest req)
      {
          List<String> list = new ArrayList<String>();
            
          Map<String,String> str = new HashMap<String, String>();
          str = req._data;
          if(str.containsKey(ShareMacros.TYPEARR))
          {
              Object obj = str.get(ShareMacros.TYPEARR);
              list = (ArrayList) obj;
          }
                    
          return list;
      }
      
       private JSONObject defaultResponse_False()
    {
        JSONObject data = new JSONObject();
        
        data.put(ShareMacros.SUSSCES, "false");
        data.put(ShareMacros.TIME, String.valueOf(utilities.time.UtilTime.getTimeNow()));
        data.put(ShareMacros.LIFE, "-1");
        data.put(ShareMacros.Coin, "-1");
        
        return  data;
    }
       
       private void outFalse(HttpServletResponse resp)
       {
           JSONObject mapjson = new JSONObject();
            mapjson = defaultResponse_False();
            ModelItem.out(mapjson.toJSONString(), resp);
       }
    
}
