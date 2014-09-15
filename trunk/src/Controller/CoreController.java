/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Controller;

import Model.ModelDevice;
import Model.ModelScore;
import Notification.GCMSender;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import share.KeysDefinition;
import share.ShareMacros;

/**
 *
 * @author LinhTA
 */
public class CoreController {
    
    private static Logger logger_ = Logger.getLogger(CoreController.class);
    
    public static void pushNotificationScore(String id,String meId, String fbId,String name,long newScore, long oldScore,String appId) throws IOException
    {
        List<String> tokens = new ArrayList<String>();
        List<String> friends4Push = new ArrayList<String>();
        
        ModelScore _scoreMd = new ModelScore(id, meId, fbId, newScore);
        ModelDevice _deviceMdl = new ModelDevice(id, meId, fbId);
        
        friends4Push = _scoreMd.getFriends4Push(appId,oldScore);
        if(friends4Push.size() > 0)
        {
            tokens = _deviceMdl.getTokens(friends4Push,appId);

            logger_.info(GCMSender.getInstance().pushNotificationScore(tokens,meId,fbId,name, newScore));
        }
    }
    
     public static void pushNotificationPK(String meId, String fbId,String name,String frdID) throws IOException
    {
        List<String> tokens = new ArrayList<String>();
        
        String idFriend = "";
          if(meId!=null && meId != "")
        {
            idFriend =KeysDefinition.getKeyUserME( frdID);
        }
        else if(fbId !=null && fbId != "")
        {
            idFriend =KeysDefinition.getKeyUserFB(frdID);
        }
        
        ModelDevice _deviceMdl = new ModelDevice(idFriend, meId, fbId);
        
        tokens = _deviceMdl.getToken(idFriend);
       if(tokens.size() >0)
        {
           logger_.info(GCMSender.getInstance().pushNotificationPK(tokens,meId,fbId,name));
        }
    }
    
      public static void pushNotificationPK_response(String id,String frdID,String meid,String fbid,String name) throws IOException
    {
        List<String> tokens = new ArrayList<String>();
        
        ModelDevice _deviceMdl = new ModelDevice(id, "", "");        
        tokens = _deviceMdl.getToken(id);
        
        _deviceMdl = new ModelDevice(frdID, "", "");
        tokens.addAll(_deviceMdl.getToken(frdID));
        
       if(tokens.size() >0)
        {
           logger_.info(GCMSender.getInstance().pushNotificationPK_response(meid,fbid,tokens,name));
        }
    }
    
}
