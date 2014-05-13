/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet;

import Model.Request.PaymentRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import libCore.LogUtil;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import share.ShareMacros;
import webservlet.Action.FriendAction;
import utilities.time.UtilTime;
import webservlet.Action.PaymentAction;
/**
 *
 * @author LinhTA
 */
public class PaymentController extends HttpServlet
{
     private static final Logger log = Logger.getLogger(ScoreController.class);
     
    
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
             Test_LogCSV.LogCSV.log("-MO:" + request._moid,request._serviceNum+","+request._phone+","+request._syntax+","+request._messageRequesst+" \n");
             Test_LogCSV.LogCSV.log("check sign",String.valueOf(request.checkSign()) );
            if(request.checkSign())
            {
               if (request._telco.isEmpty() || request._serviceNum.isEmpty() || request._phone.isEmpty())
               {
                   Test_LogCSV.LogCSV.log("error", "loi he thong sms");
			//return array('SendSMSResult' => 0,	'strMessageReturn' =>'Error 102.'.$moid.'. Vui long lien he 0908661840' );
                    echo( "Error 102."+request._moid+'.'+request._telco+"===="+request._serviceNum+"===="+request._phone+"==="+request._syntax+"=="+request._messageRequesst+" Vui long lien he 0908661840", resp);
			// tra về lỗi của hệ thống sms
                    return;
		}
                
                PaymentAction action = new PaymentAction();
               echo(action.SMSPAYMENT(request._messageRequesst, request._serviceNum),resp);
               
            }
            else 
            {
                Test_LogCSV.LogCSV.log("error", "invalid sign");
                echo(ShareMacros.Payment_Error_InvalidSign, resp);
            }
        }catch(Exception e)
        {
        }
    }
    
    protected void echo(Object text, HttpServletResponse response) throws Exception{
       // PrintWriter out = null;
        
        MessageFactory messageFactory = MessageFactory.newInstance();
       
        SOAPMessage soapMessage = messageFactory.createMessage();
        response.setContentType("text/xml;charset=UTF-8");
        ServletOutputStream replyOS = response.getOutputStream();
        try {
             
            
         
            SOAPBody soapBody = soapMessage.getSOAPBody();
            
            String body ="<ReceiveResponse xmlns=\"http://tempuri.org/\"> <ReceiveResult>"+text.toString()+"</ReceiveResult></ReceiveResponse>";
      
           DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();  
            builderFactory.setNamespaceAware(true);  
            InputStream stream  = new ByteArrayInputStream(body.getBytes());  
            Document doc = builderFactory.newDocumentBuilder().parse(stream);  

        soapBody.addDocument(doc);
                
        soapMessage.writeTo(replyOS);
        replyOS.flush();
       
//            response.setContentType("text/xml;charset=UTF-8");
//            
//            out = response.getWriter();
//            out.print(text);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        } finally {
             replyOS.close();  
        }
    }
}
