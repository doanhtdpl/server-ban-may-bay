/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Notification;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import libCore.Config;
import share.ShareMacros;
/**
 *
 * @author LinhTA
 */
public class GCMSender  {
    
    private static Sender sender ;
    private static  MulticastResult multicatstResult ;
    
    private GCMSender() {
        
        sender = new Sender(Config.getParam("gcm", "apiKey"));
        multicatstResult = new MulticastResult.Builder(0, 0, 0, 100).build();
    }
    
    public static GCMSender getInstance() {
        return GCMSenderHolder.INSTANCE;
    }
    
    private static class GCMSenderHolder {

        private static final GCMSender INSTANCE = new GCMSender();
    }
    
    public MulticastResult  pushNotificationScore( List<String> regIds, String meId,String fbId,String name,long newScore ) throws IOException
    {
         Message msg = new Message.Builder()
                .addData(ShareMacros.FACEID, fbId)
                 .addData(ShareMacros.MEID, meId)
                .addData(ShareMacros.NAME, name)
                .addData(ShareMacros.SCORE, String.valueOf(newScore))
                .build();
         
         int retries = 0;
         retries = Integer.parseInt(Config.getParam("gcm", "retries"));
               
         multicatstResult = sender.send(msg, regIds, retries);
         
         return multicatstResult;
    }
    
//     public MulticastResult  newFriendHightScore( List<String> friendIds, String uid,String newScore ) throws IOException
//    {
//         Message msg = new Message.Builder()
//                .addData(ShareMacros.ID, uid)
//                .addData(ShareMacros.SCORE, newScore)
//                .build();
//         List<String> regIds = new ArrayList<String>();
//         int retries = 0;
//         retries = Integer.parseInt(Config.getParam("gcm", "retries"));
//         String regId = "APA91bH2qXl_v_t8e5D0kIl9gLs5BcAb73WB5CueVmHuL0rFqs-VAWHcj2xsoVUV_PdsGGRWADhhyHyCETjbCISnd4ri2oPvGAaZctXj2Rlk9WwS4wp0I7gDQ3a2wGrbWSIVC_mnsTAzuofEaYSrDaKH80po1EGAoKhkNfFQYxCh2Mj68tj6bGs";
//         
//         regIds.add(regId);
//         
//         multicatstResult = sender.send(msg, regIds, retries);
//         
//         return multicatstResult;
//    }
//    
    public static void main(String[] args) throws IOException{
        
      // System.out.print(GCMSender.getInstance().newFriendHightScore(new ArrayList<String>(),"a","c"));
        
    }
}
