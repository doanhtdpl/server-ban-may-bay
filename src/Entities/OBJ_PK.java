package Entities;


import java.util.Map;
import share.ShareMacros;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LinhTA
 */
public class OBJ_PK {
    
    public long _score ;
    public long _time ;
    public String _typeMoney ;
    public long  _money;
          
          public OBJ_PK(Map<String,String> data)
          {
              _score =data.containsKey(ShareMacros.SCORE) ?Long.parseLong( data.get(ShareMacros.SCORE)):0;
              _money =data.containsKey(ShareMacros.MONEY) ?Long.parseLong( data.get(ShareMacros.MONEY)):0;
              _time =data.containsKey(ShareMacros.TIME) ?Long.parseLong( data.get(ShareMacros.TIME)):0;
              _typeMoney = data.containsKey(ShareMacros.TYPE)? data.get(ShareMacros.TYPE):"";
          }
    
}
