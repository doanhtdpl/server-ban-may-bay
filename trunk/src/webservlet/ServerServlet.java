/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webservlet;

/**
 *
 * @author LinhTA
 */
import Security.Scr_Base64;
import DB_REDIS.Redis_Rd;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import libCore.Config;
import org.apache.log4j.Logger;

public class ServerServlet extends HttpServlet {

    private static Logger logger_ = Logger.getLogger(ServerServlet.class);

    protected boolean pingRedis(HttpServletResponse response)
    {
        String ping = "";
        ping = Redis_Rd.getInstance().ping();
        if(ping != null)
        {
            ping = ping.toLowerCase();
            if(ping.equals("pong"))
                return true;
            else
                return false;
        }
        else
            return false;
        
        
    }
    
    protected void echo(Object text, HttpServletResponse response) {
        PrintWriter out = null;
         Test_LogCSV.LogCSV.log("Response",text.toString() );
        try {
            response.setContentType("text/html;charset=UTF-8");
            
            out = response.getWriter();
            out.print(Scr_Base64.Encode(text.toString()));
        } catch (IOException ex) {
            logger_.error(ex.getMessage());
           
        } finally {
            out.close();
        }
    }

}
