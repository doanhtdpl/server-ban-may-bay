/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Security;

import java.io.ByteArrayInputStream;
import java.io.IOError;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 *
 * @author LinhTA
 */
public class Scr_MD5 {
    
    public static String parseMD5(String data)
    {
        String key = "";
     
        try{
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            // chuoi input lưu dưới dạng inputstream
            ByteArrayInputStream binput2 = new ByteArrayInputStream(data.getBytes());
             // tao gia tri cho md tu chuoi input
            DigestInputStream dinput = new DigestInputStream(binput2, md);
            
            key = new String(md.digest());
            
        }
        catch(Exception e)
        {
            
        }
        
        return key;
    }
    
}
