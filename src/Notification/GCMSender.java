/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Notification;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import java.io.IOException;
import java.util.AbstractList;
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
                 .addData(ShareMacros.TYPE, ShareMacros.VUOTMAT)  
                .build();
         
         
         int retries = 0;
         retries = Integer.parseInt(Config.getParam("gcm", "retries"));
               
         multicatstResult = sender.send(msg, regIds, retries);
         
         return multicatstResult;
    }
    
    public MulticastResult  pushNotificationPK( List<String> regIds, String meId,String fbId,String name ) throws IOException
    {
         Message msg = new Message.Builder()
                .addData(ShareMacros.FACEID, fbId)
                 .addData(ShareMacros.MEID, meId)
                .addData(ShareMacros.NAME, name)
                 .addData(ShareMacros.TYPE, ShareMacros.PK)  
                .build();         
         
         int retries = 0;
         retries = Integer.parseInt(Config.getParam("gcm", "retries"));
               
         multicatstResult = sender.send(msg, regIds, retries);
         
         return multicatstResult;
    }
    
     public MulticastResult  pushNotificationPK_response( String meid,String fbid,List<String> regIds,String name ) throws IOException
    {
         Message msg = new Message.Builder()
                .addData(ShareMacros.FACEID, fbid)
                 .addData(ShareMacros.MEID, meid)
                .addData(ShareMacros.NAME, name)
                 .addData(ShareMacros.TYPE, ShareMacros.PK) 
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
        
      //System.out.print(GCMSender.getInstance().pushNotificationScore(new ArrayList<String>(),);
        List<String> tokens = new ArrayList<String>();
       tokens.add("APA91bHLXng9mAp34suMA0ufNWmv_pKNfaZhLAQIqzv5IqtxonY1Utdz_wBfjNQHmKOuHA92eOUetduwx4CsrS1yR8dHBbvtstGZ4wXoB7pA9Sy-6lnFM0fa69Zy0_WI8qKOxbxWz8TzXtO1nhTOY06c9CJKjab4fw");
       //  tokens.add("Y");
        System.out.print(GCMSender.getInstance().pushNotificationScore(tokens, "", "100001384845416", "Linh dep trai", 1000000));
    }
}
