/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet.Action;

import Model.Request.ClientRequest;
import Security.Scr_Base64;
import DB_MYSQL.MySqlConnection_Rd;
import DB_MYSQL.MySqlConnection_W;
import DB_MYSQL.Utils.DataTable;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import share.ShareMacros;
import libCore.*;

/**
 *
 * @author lenguyen
 */
public class SendItemAction {
    
    
    /*
    {
        data:
        {
            appId: 'DBPTK',
            platform: 'facebook',
            userId: '1230423857227',
            receiverId: '235195969593',
            itemId: 'lazer',
            count: 1,
            metadata: ''
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
            String itemId = request._data.get(ShareMacros.ITEM_ID_JSON);
            long count = Long.parseLong(request._data.get(ShareMacros.COUNT_JSON));
            String metadata = request._data.get(ShareMacros.META_DATA_JSON);
                    
            String senderId = request._uid;
            String receiverId = request._data.get(ShareMacros.RECEIVER_ID_JSON);
            if (request._meID == null || request._meID =="" || request._meID.isEmpty()) {
                receiverId = ShareMacros.PREFIX_FB + receiverId;
            } else {
                receiverId = ShareMacros.PREFIX_ME + receiverId;
            }
            
            //send by himself
            if (receiverId.equals(senderId)) {
                responseDefault(resp);
                return;
            }
            
            if(!checkTimeSend(senderId, receiverId, appId, itemId))
            {
                responseDefault(resp);
                return;
            }
            //('phamtanlong', 'nguyengiangchau', 'DBPTK', 0123456789, 'not_received', 'life', 1, '')
            String[] values  =   {
                Util.quote(senderId),
                Util.quote(receiverId),
                Util.quote(appId),
                "" + utilities.time.UtilTime.getTimeNow(),
                Util.quote(ShareMacros.STATUS_NOT_RECEIVED),
                Util.quote(itemId),
                "" + count,
                Util.quote(metadata),
            };
            int result = MySqlConnection_W.getInstance().insertTable( share.ShareMacros.INBOX_TABLE, share.ShareMacros.INBOX_COLUMNS, values );
            System.out.println( "__________SEND ITEM: " + result );
            
            String str = "{\"isSuccess\": \"true\"}";
            //writer.print(str);
            //writer.flush();
            //writer.close();
            out(str, resp);
            
        } catch (Exception ex) {
            Logger.getLogger(SendItemAction.class.getName()).log(Level.SEVERE, null, ex);
            responseDefault(resp);
        } finally {
        }
    }
        
    public static void responseDefault(HttpServletResponse resp) {
        String str = "{\"isSuccess\": \"false\"}";
        out(str, resp);
    }
    
   public static void  out(String content, HttpServletResponse respon) {

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

    public static boolean checkTimeSend(String uid,String fid,String appID, String item)
    {
        boolean check = false;
        
         long timeMin = 0;
        try
        {
            String t = Config.getParam(ShareMacros.INBOX_MIN, item);
            timeMin = Long.parseLong(t);
        }
        catch(Exception e)
        {
            
        }
        
        long timeNow = utilities.time.UtilTime.getTimeNow();
        
        long time = timeNow - timeMin;
        
        String where = "";
        where += ShareMacros.SENDER_ID_COL;
            where +=" = ";
            where += Util.quote(uid);    
            where += " AND ";
        where += ShareMacros.RECEIVER_ID_COL;
            where +=" = ";
            where +=Util.quote(fid);    
            where +=" AND ";
        where +=ShareMacros.APP_ID_COL;
            where +=" = ";
            where +=Util.quote(appID);    
            where +=" AND ";
        where +=ShareMacros.TIME_COL;
            where +=" > ";
            where +=Util.quote(String.valueOf(time));
            
         DataTable re = MySqlConnection_Rd.getInstance().selectCmdWhere(ShareMacros.INBOX_TABLE,where, ShareMacros.TIME_COL);    
         
         if(re.size() == 0)
             return true;
         
        return  check;
    }
   
  
    public static void main(String[] args) {
        
        boolean a = checkTimeSend("Fb_100001986079146", "FB_100006639370902", "DBPTK", "life");
        DataTable re = MySqlConnection_Rd.getInstance().selectCmdWhere(ShareMacros.INBOX_TABLE,"Time >= 0", ShareMacros.TIME_COL);
        System.out.print("sd");
    }
   
    
}
