/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet.Action;

import Model.Request.ClientRequest;
import Security.Scr_Base64;
import SqlDB.MySqlConnection_Rd;
import SqlDB.Utils.DataRow;
import SqlDB.Utils.DataTable;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import share.ShareMacros;
import libCore.Util;
import org.json.simple.JSONObject;

/**
 *
 * @author lenguyen
 */
public class GetInboxAction {
    
    
    /*
    data = 
    {
        data:
        {
            appId: 'DBPTK',
            platform: 'facebook',
            userId: '1230423857227'
        },
        sign: md5,
        appId: 'DBPTK'
    }
    */
    public void handle(ClientRequest request, HttpServletResponse resp) {
        //PrintWriter writer = null;
        
        try {
            //writer = resp.getWriter();
            
            String appId = request._appId;
            String userId = request._uid;
            
            String condition = "";
            condition += share.ShareMacros.APP_ID_COL + " = " + Util.quote(appId);
            condition += " AND ";
            condition += share.ShareMacros.RECEIVER_ID_COL + " = " + Util.quote(userId);
            condition += " AND ";
            condition += share.ShareMacros.STATUS_COL + " = " + Util.quote(share.ShareMacros.STATUS_NOT_RECEIVED);
                        
            DataTable dt = MySqlConnection_Rd.getInstance().selectAllColumnCmdWhere(
                    share.ShareMacros.INBOX_TABLE, condition );
            
            String str = parseData(dt);
            
            //writer.print(str);
            //writer.flush();
            //writer.close();
            out(str, resp);
            
        } catch (Exception ex) {
            Logger.getLogger(GetInboxAction.class.getName()).log(Level.SEVERE, null, ex);
            responseDefault(resp);
        } finally {
        }
    }
 
    private String parseData(DataTable dt) {
        String dataStr  =   "{ \"isSuccess\": \"true\", \"list\":[";
        
        if( dt != null && dt.size() > 0 ) {
            int nsize = dt.size();
            for( int i = 0; i < nsize; ++i ) {
                DataRow dw  =   dt.get(i);
                
                String senderId = dw.get( share.ShareMacros.SENDER_ID_COL );
                senderId = Util.removePrefix(senderId);
                
                String itemId = dw.get(share.ShareMacros.ITEM_ID_COL);
                long count = Long.parseLong(dw.get(share.ShareMacros.COUNT_COL));
                long time = Long.parseLong(dw.get(share.ShareMacros.TIME_COL));
                
                Map<String, String> data    =   new HashMap<String, String>();
                data.put( ShareMacros.SENDER_ID_JSON, senderId );
                data.put( ShareMacros.ITEM_ID_JSON, itemId);
                data.put( ShareMacros.COUNT_JSON,String.valueOf( count));
                data.put( ShareMacros.TIME_JSON, String.valueOf(time));
                
                JSONObject mapJson = new JSONObject();
                mapJson.putAll(data);
                
                dataStr     =   dataStr + mapJson.toJSONString();
                if( i != ( nsize - 1 )) {
                    dataStr =   dataStr + ",";
                }
            }
        }
        
        dataStr     +=  "]}";
        return dataStr;
        
    }
        
    
    public static void responseDefault(HttpServletResponse resp) {
        String str = "{\"isSuccess\": \"false\", \"list\": []}";
        out(str, resp);
    }
    
       public static void out(String content, HttpServletResponse respon) {

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
}
