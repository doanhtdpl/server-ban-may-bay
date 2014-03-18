/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Security;

import org.apache.commons.codec.binary.Base64;
/**
 *
 * @author LinhTA
 */
public class Scr_Base64 {
    
    public static String Encode(String data)
    {
        String base64 = "";
        base64 = new String(Base64.encodeBase64(data.getBytes()));
        
        return base64;
    }
    
     public static String Decode(String base64)
    {
         String data = "";
        
        data = new String( Base64.decodeBase64(base64.getBytes()));
        
        return data;
    }
    
     public static void main(String[] args) {
         
         String str = "linh dep trai ghê ghớm !";
        String base64 = Encode(str);
         System.out.println(base64);
        System.out.println(Decode(base64));
    }
}
