/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet;

import Model.Request.PaymentRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import libCore.LogUtil;
import org.apache.log4j.Logger;
import webservlet.Action.FriendAction;
import utilities.time.UtilTime;
/**
 *
 * @author LinhTA
 */
public class PaymentController extends ServerServlet
{
     private static final Logger log = Logger.getLogger(ScoreController.class);
     private static String ErrorSign = "1|Invalid signature";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        try {
			doProcess(req, resp);
		} catch (Exception ex) {
			log.error(LogUtil.stackTrace(ex));
		}
    
    }

    private void doProcess(HttpServletRequest req, HttpServletResponse resp) {
        
        try
        { 
            PaymentRequest request = new PaymentRequest(req);
            if(request.checkSign())
            {
                FileWriter fw = null;
                fw = new FileWriter("data.csv", true);
                fw.write(UtilTime.getTimeNowStr() + "-MO:" + request._moid+","+request._serviceNum+","+request._phone+","+request._syntax+","+request._mesageRequest+" \n");
                fw.close();
                
            }
            else 
                echo(ErrorSign, resp);
        }catch(Exception e)
        {
        }
    }
}
