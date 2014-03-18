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

/**
 *
 * @author LinhTA
 */
public class CoreController {
    
    public static void pushNotificationScore(String id,String meId, String fbId,String name,long score,String appId) throws IOException
    {
        List<String> tokens = new ArrayList<String>();
        List<String> friends4Push = new ArrayList<String>();
        
        ModelScore _scoreMd = new ModelScore(id, meId, fbId, score);
        ModelDevice _deviceMdl = new ModelDevice(id, meId, fbId);
        
        friends4Push = _scoreMd.getFriends4Push(appId);
        tokens = _deviceMdl.getTokens(friends4Push,appId);
        
        System.out.print(GCMSender.getInstance().pushNotificationScore(tokens,meId,fbId,name, score));
    }
    
    
}
