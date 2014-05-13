/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import db.Redis_Pipeline;
import db.Redis_Rd;
import db.Redis_W;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import share.KeysDefinition;
import share.ShareMacros;

/**
 *
 * @author LinhTA
 */
public class ModelFriend {
    
    public String _id ;
    public String _meID;
    public String _fbID;
    
    public ModelFriend(String id,String meId, String fbId)
    {
        _id = id;
        _meID = meId;
        _fbID = fbId;
    }
    
    
    public List<Object> getDataFriend( Set<String> friends)
     {
          List<Object> data = new ArrayList<Object>();
          
          
          
          return data;        
    }
     
     public List<String> getMEFriendApp(String uid, String appId)
     {
         List<String> friendList = new ArrayList<String>();
         
         String[] key =  new String[2];
       key[0] =  KeysDefinition.getKey_ListFriends(uid);
       key[1] = KeysDefinition.getAllUserME(appId);
        Set<String> friends = new HashSet<String>();        
       friends =  Redis_Rd.getInstance().sInter(key);
       
       friendList.addAll(friends);
       
       return friendList;
     }
     
     public static List<String> getFBFriendApp(String uid, String appId)
     {
         List<String> friendList = new ArrayList<String>();
         
         String[] key =  new String[2];
       key[0] =  KeysDefinition.getKey_ListFriends(uid);
       key[1] = KeysDefinition.getAllUserFB(appId);
        Set<String> friends = new HashSet<String>();        
       friends =  Redis_Rd.getInstance().sInter(key);
       
       friendList.addAll(friends);
       
       return friendList;
     }
     
     public  List<String> getFriendHaveScore(List<String> friends,String appid)
     {
         List<String> friendHaveScore = new ArrayList<String>();
         
         friendHaveScore = Redis_Pipeline.getInstance().getExitsScore(friends,appid,_meID,_fbID);         
         
         return friendHaveScore;
     }
     
     //last time receive
     public long getTimeGift(String uid, String fid)
     {
         
         String keyTime = KeysDefinition.getKeyFriendTime(uid, fid);
         String ret = null;
             try
             {
                 ret = Redis_Rd.getInstance().getRetries(keyTime);
             }
             catch(Exception e)
             {
                 
             }
             if(ret == null)
             {
                long  ret2 = Redis_W.getInstance().setRetries(keyTime, "0");
                long time2 = 0;
                if(ret2 == -1)
                {
                    time2 = -1;
                }
                else
                {
                    time2 = 0;
                }
                return time2;
            }
             else
             {
                 return Long.parseLong(ret);
             }
         

     }
     
     public List<Object> getScoreFriends(List<String> friends,String appId, Map<String , String> timeResendLife)
     {
         List<Object> data = new ArrayList<Object>();
         for(int j = 0; j < friends.size(); j++) 
        {
            String key = "";
            String friendId = friends.toArray()[j].toString();
            if(_meID != "" && !_meID.isEmpty())
            {  
                key = KeysDefinition.getKeyUserME(friendId);
                
            }
            else
            {
                key = KeysDefinition.getKeyUserFB(friendId);
            }
            
            long time = getTimeGift(_id, friendId);
            
            //key = KeysDefinition.getKeyAppUser(key, appId);
            String name = Redis_Rd.getInstance().Hget(key, ShareMacros.NAME);
            String score = Redis_Rd.getInstance().Hget(KeysDefinition.getKeyAppUser(key, appId), ShareMacros.SCORE);
            if(score != null && name != null)
            {
                Map<String,String> friendProfile = new HashMap<String,String>();
                friendProfile.put(ShareMacros.NAME, name);
                friendProfile.put(ShareMacros.SCORE, score);
                friendProfile.put(ShareMacros.ID, friendId);
                friendProfile.put(ShareMacros.TIMELAZE, String.valueOf(time));
                
                String timeRL = "0";
                if(timeResendLife.containsKey(key))
                    timeRL = timeResendLife.get(key);
                
                friendProfile.put(ShareMacros.TIMESENDLIFE, timeRL);
                
                data.add( friendProfile);
            } 
                
        }
         
         return data;
     }
     
     public List<Object> getFakeScoreFriends(List<String> friends,Map<String , String> timeResendLife)
     {
          List<Object> data = new ArrayList<Object>();
         for(int j = 0; j < friends.size(); j++) 
        {
            String friendId = friends.toArray()[j].toString();
            String key = "";
            if(_meID != "" && !_meID.isEmpty())
            {  
                key = KeysDefinition.getKeyUserME(friendId);
                
            }
            else
            {
                key = KeysDefinition.getKeyUserFB(friendId);
            }
            
            long time = getTimeGift(_id, friendId);
            String name = Redis_Rd.getInstance().Hget(key, ShareMacros.NAME);
            String score = "0";
            if(score != null && name != null)
            {
                Map<String,String> friendProfile = new HashMap<String,String>();
                friendProfile.put(ShareMacros.NAME, name);
                friendProfile.put(ShareMacros.SCORE, score);
                friendProfile.put(ShareMacros.ID, friendId);
                friendProfile.put(ShareMacros.TIMELAZE, String.valueOf(time));
                String timeRL = "0";
                if(timeResendLife.containsKey(key))
                    timeRL = timeResendLife.get(key);
                
                friendProfile.put(ShareMacros.TIMESENDLIFE, timeRL);
                
                
                data.add( friendProfile);
            } 
                
        }
         
         return data;
     }
     
}
