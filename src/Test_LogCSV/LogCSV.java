/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test_LogCSV;

import java.io.FileWriter;
import libCore.Config;
import utilities.time.UtilTime;

/**
 *
 * @author LinhTA
 */
public class LogCSV {
    
    static FileWriter fw;
    static 
    {
       
         fw = null;
          try{
            fw = new FileWriter(Config.getParam("linkLog", "csv"), true);
          }
          catch(Exception e)
          {}
              
    }
    
    public static void log(String name ,String data)
    {
        try{
            fw = new FileWriter(Config.getParam("linkLog", "csv"), true);
          fw.write(UtilTime.getTimeNowStr() + "-"+name+":" +","+ data+" \n");
          fw.close();
        }
        catch(Exception e)
        {
        }
    }
    
    
    public static void main(String[] args) {
        for (int i = 0; i < 10000000; i++) {
            log("a","sdsdsd,sds,sd,sd,sd,s,ds,d,s,ds,d,s,ds,d,s,ds,d,s,s,ds,ds,ds,d,sd," );
        }
    }
}
