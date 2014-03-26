/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Security;

import java.security.MessageDigest;

/**
 *
 * @author LinhTA
 */
public class Scr_Sha1 {
    
     
    public static String parseSha1(String data)
    {
        String key = "";
     
        try{
            
            MessageDigest md = MessageDigest.getInstance("SHA-1");
               md.update(data.getBytes());
 
            byte byteData[] = md.digest();
 
            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
             sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            key = sb.toString();
            System.out.println("Digest(in hex format):: " + sb.toString());
 
        }
        catch(Exception e)
        {
            
        }
        
        return key;
    }
    
    public static void main(String[] args) {
        Scr_Sha1.parseSha1("abdfdgfggfg");
    }
    
}
