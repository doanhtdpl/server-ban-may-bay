/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet.Action;

import Model.ModelItem;
import Model.Request.ClientRequest;
import Security.Scr_Base64;
import SqlDB.MySqlConnection_Rd;
import SqlDB.MySqlConnection_W;
import SqlDB.Utils.DataTable;
import com.google.common.math.IntMath;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import share.ShareMacros;
import libCore.Util;

/**
 *
 * @author PhamTanLong
 */
public class RemoveItemAction {
    
    
    /*
    {
	data:
	{
            appId: 'DBPTK',
            platform: 'facebook',
            userId: '1',
            senderId: '2',
            time: '1398074361929'
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
            String senderId = request._data.get(ShareMacros.SENDER_ID_JSON);
            if (userId.startsWith(ShareMacros.PREFIX_FB)) {
                senderId = ShareMacros.PREFIX_FB + senderId;
            } else {
                senderId = ShareMacros.PREFIX_ME + senderId;
            }
            
            String item = request._data.get(ShareMacros.ITEM_ID_JSON);
            
            if(!ModelItem.checkAddItem(userId, item))
            {
                out("{\"isSuccess\": \"false\"}", resp);
            }
            
            long time = Long.parseLong(request._data.get(ShareMacros.TIME_JSON));
            
            String condition = "";
            condition += share.ShareMacros.APP_ID_COL + " = " + Util.quote(appId);
            condition += " AND ";
            condition += share.ShareMacros.RECEIVER_ID_COL + " = " + Util.quote(userId);
            condition += " AND ";
            condition += share.ShareMacros.SENDER_ID_COL + " = " + Util.quote(senderId);
            condition += " AND ";
            condition += share.ShareMacros.TIME_COL + " = " + Util.quote(String.valueOf(time));
            
            String[] colums =   { ShareMacros.STATUS_COL };
            String[] values  =   { Util.quote(ShareMacros.STATUS_REMOVED) };
            MySqlConnection_W.getInstance().updateTable( ShareMacros.INBOX_TABLE, colums, values, condition );
            
            String condition2 = condition + " AND "+ ShareMacros.STATUS_COL +" = "+Util.quote(ShareMacros.STATUS_REMOVED);
            DataTable dt = MySqlConnection_Rd.getInstance().selectAllColumnCmdWhere(
                    share.ShareMacros.INBOX_TABLE, condition2 );
            
            
            String str ="";
            if(dt.size()>0 )
            {
                if(ModelItem.addItem(userId, dt.get(0).get(ShareMacros.ITEM_ID_COL)))
                        str="{\"isSuccess\": \"true\"}";
                else
                    str="{\"isSuccess\": \"false\"}";
            }
             
            else
                str="{\"isSuccess\": \"false\"}";
            //writer.print(str);
            //writer.flush();
            //writer.close();
            out(str, resp);
            
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            responseDefault(resp);
        } finally {
        }
    }
 
    
    public static void responseDefault(HttpServletResponse resp) {
        String str = "{\"isSuccess\": \"false\"}";
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
