/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Security;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
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
            //ByteArrayInputStream binput2 = new ByteArrayInputStream(data.getBytes());
             // tao gia tri cho md tu chuoi input
            //DigestInputStream dinput = new DigestInputStream(binput2, md);
            //md.update(data.getBytes(),0,data.length());
            
//            byte[] bytesOfMessage = data.getBytes("UTF-8");   
//            byte[] thedigest = md.digest(bytesOfMessage);
//            
//            key = new String(md.digest());
            
               md.update(data.getBytes());
 
        byte byteData[] = md.digest();
 
        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        key = sb.toString();
        System.out.println("Digest(in hex format):: " + sb.toString());
 
//        //convert the byte to hex format method 2
//        StringBuffer hexString = new StringBuffer();
//    	for (int i=0;i<byteData.length;i++) {
//    		String hex=Integer.toHexString(0xff & byteData[i]);
//   	     	if(hex.length()==1) hexString.append('0');
//   	     	hexString.append(hex);
//    	}
//    	System.out.println("Digest(in hex format):: " + hexString.toString());
//            
        }
        catch(Exception e)
        {
            
        }
        
        return key;
    }
    
    public static void main(String[] args) {
       
        Authenticate auth = new Authenticate("DBPTK", "4517d88b7a25ed89b82752c5c6a261a0", "Fb1", "");
        auth.checkAuth();
        parseMD5("DBPTKcGk@proJectDBPtkkEy-askdjhsakdhajakshdwiFb1");
    }
    
}
