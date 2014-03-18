/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import share.KeysDefinition;
import share.ShareMacros;

/**
 *
 * @author LinhTA
 */
public class ModelScore {
    
    public String _id ;
    public String _meID;
    public String _fbID;
    public long _score;
    
    public ModelScore(String id,String meId, String fbId,long score)
    {
        _id = id;
        _meID = meId;
        _fbID = fbId;
        _score = score;
    }
    
    public List<String> getFriends4Push(String appID)
    {
        List<String> isFriend4Push =  new ArrayList<String>();
         ModelFriend friendMd = new ModelFriend(_id, _meID, _fbID);
        String appId = appID;
        
        List<String> friendHaveScore = new ArrayList<String>();
       List<String> friends_list= new ArrayList<String>();
       
        List<String> friendApp = new ArrayList<String>();
        if(_meID != "" && !_meID.isEmpty() )
        {
            friendApp= friendMd.getMEFriendApp(_id, appId);
            friends_list.addAll(friendApp);
        }
        else if ((_fbID != "" && !_fbID.isEmpty() ))
        {
            friendApp = ModelFriend.getFBFriendApp(_id, appId);
            friends_list.addAll(friendApp);
        }
        
       
        friendHaveScore = friendMd.getFriendHaveScore(friendApp, appId);
       List<Object> data = new ArrayList<Object>();
       data = friendMd.getScoreFriends(friendHaveScore, appId); 
        
       for (Iterator<Object> it = data.iterator(); it.hasNext();) {
            Map<String, Object> object = new HashMap<String,Object>();
            object = libCore.Util.obj2Map(it.next());
            long scoreF = Long.valueOf(object.get(ShareMacros.SCORE).toString());
            if(_score > scoreF)
            {
                String k = object.get(ShareMacros.ID).toString();
                if(_meID != "" && !_fbID.isEmpty())
                {
                    k = KeysDefinition.getKeyUserME(k);
                }
                else
                {
                    k =  KeysDefinition.getKeyUserFB(k);
                }
                
                 isFriend4Push.add(k);   
            }
        }
       
       return isFriend4Push;
        
    }
    
}
