/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet.Action;

import db.Redis_Rd;
import db.Redis_W;
import share.KeysDefinition;
import share.ShareMacros;

/**
 *
 * @author LinhTA
 */
public class PaymentAction {
    
    public String SMSPAYMENT (String message , String serviceNum)
    {
        Test_LogCSV.LogCSV.log("packet money", serviceNum);
        String msg = "";
        
        message = message.replace("  ", " ");
        String[] msgData = message.split(" ");
        
        switch (serviceNum)
        {
        case "8500":	
                msg = SMS8500(msgData);
                break;
        case "8700":	
                msg = SMS8700(msgData);
                break;

        default: 
                msg = "Hien nay chung toi chua cung cap dich vu nay. Vui long lien he 0932 068 068 hoac 0903 390 390";
            
        }
        
        return msg;
    }
    
     public String SMS8500 (String[] message)
     {
         try
         {
            int length = message.length;
            for (int j = 0; j < length; j++) {
                Test_LogCSV.LogCSV.log("message "+j, message[j]);
            }
         }
         catch(Exception e)
         {}
         
         String msg = "";
         String email = message[1];
         String uid = Redis_Rd.getInstance().get(email);
         if(uid .isEmpty() || uid =="-1")
         {
             msg = ShareMacros.Payment_Error_InvalidEmail;
             return  msg;
         }
         
         String keyCoin = KeysDefinition.getKeyCoinUser(uid);
         String coin = "";
         int retries = 3;
         while(retries>0)
         {
             coin = Redis_Rd.getInstance().get(keyCoin);
             if(coin.isEmpty() || coin =="-1" || coin == null)
                 retries --;
             else break;
         }
       
         Test_LogCSV.LogCSV.log("coin", coin);
         
         if(coin.isEmpty() || coin =="-1" || coin == null)
         {
             coin = "0";
         }
        
         long coinGame = Long.valueOf(coin);
         coinGame += Long.valueOf( configuration.Configuration.PaymentCoin8500);
         if(Redis_W.getInstance().set(keyCoin,String.valueOf(coinGame)) != -1)
             msg = ShareMacros.Payment_Success;
         else 
             msg = ShareMacros.Payment_Error_NotSuccess;
         
         return msg;
     }
     
     public String SMS8700 (String[] message)
     {
         try
         {
            int length = message.length;
            for (int j = 0; j < length; j++) {
                Test_LogCSV.LogCSV.log("message "+j, message[j]);
            }
         }
         catch(Exception e)
         {}
         
          String msg = "";
         String email = message[1];
         String uid = Redis_Rd.getInstance().get(email);
         if(uid .isEmpty() || uid =="-1")
         {
             msg = ShareMacros.Payment_Error_InvalidEmail;
             return  msg;
         }
         
         String keyCoin = KeysDefinition.getKeyCoinUser(uid);
         String coin = "";
         int retries = 3;
         while(retries>0)
         {
             coin = Redis_Rd.getInstance().get(keyCoin);
             if(coin.isEmpty() || coin =="-1" || coin == null)
                 retries --;
             else break;
         }
       
         Test_LogCSV.LogCSV.log("coin", coin);
         
         if(coin.isEmpty() || coin =="-1" || coin == null)
         {
             coin = "0";
         }
        
         long coinGame = Long.valueOf(coin);
         coinGame += Long.valueOf( configuration.Configuration.PaymentCoin8700);
         if(Redis_W.getInstance().set(keyCoin,String.valueOf(coinGame)) != -1)
             msg = ShareMacros.Payment_Success;
         else 
             msg = ShareMacros.Payment_Error_NotSuccess;
         
         return msg;
     }
     
     public static void main(String[] args) {
        String a = "adsad  asdsad  áds";
        a= a.replace("  ", " ");
         System.out.print(a);
    }
}
