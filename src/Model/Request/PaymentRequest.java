/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model.Request;

import Security.Scr_Base64;
import Security.Scr_Sha1;
import java.awt.List;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import libCore.Config;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import configuration.Configuration;
import java.io.FileWriter;
import utilities.time.UtilTime;

/**
 *
 * @author LinhTA
 */
public class PaymentRequest {
    
   public static String _moid; 
   public static String _telco; 
   public static String _serviceNum; 
   public static String _phone; 
   public static String _syntax; 
   public static String _encryptedMessage; 
   public static String _sign; 
   public static String _messageRequesst;
    
    static String MOID      = "MOId";
    static String TELCO     = "Telco";
    static String SERVICE_NUM = "ServiceNum";
    static String PHONE     = "Phone";
    static String SYNTAX    = "Syntax";
    static String ENCRYPTED_MESSAGE = "EncryptedMessage";
    static String SINGNATURE = "Signature";
    
     public PaymentRequest(HttpServletRequest req) throws Exception
        {
            
             Document doc = parseXML(req.getInputStream());
            
             NodeList nList = doc.getElementsByTagName("Receive");
 
             Test_LogCSV.LogCSV.log(" length getElementsByTagName Receive",String.valueOf( nList.getLength()));
	
            for (int temp = 0; temp < nList.getLength(); temp++) 
            {
                Node nNode = nList.item(temp);
                
              Test_LogCSV.LogCSV.log(" node Content",nNode.getTextContent());
                 Test_LogCSV.LogCSV.log(" node type",String.valueOf(nNode.getNodeType()==Node.ELEMENT_NODE));     
                
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;

                        _moid = eElement.getElementsByTagName(MOID).item(0).getTextContent();
                        _phone = eElement.getElementsByTagName(PHONE).item(0).getTextContent();
                        _serviceNum = eElement.getElementsByTagName(SERVICE_NUM).item(0).getTextContent();
                        _sign = eElement.getElementsByTagName(SINGNATURE).item(0).getTextContent();
                        _syntax = eElement.getElementsByTagName(SYNTAX).item(0).getTextContent();
                        _telco = eElement.getElementsByTagName(TELCO).item(0).getTextContent();
                        _encryptedMessage = eElement.getElementsByTagName(ENCRYPTED_MESSAGE).item(0).getTextContent();
                        
                        Test_LogCSV.LogCSV.log("-MO:" + _moid,"sign:"+_sign+","+"telco:"+_telco+","+"serviceNum:"+_serviceNum+","+"phone:"+_phone+","+"syntax:"+_syntax+","+"mesage:"+_messageRequesst+","+"encryptedMessage:"+_encryptedMessage+" \n");
            
                }
            }
             
        }
     
     private Document parseXML(InputStream stream)  throws Exception
    {
        DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder = null;
        Document doc = null;
        try
        {
            objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

            doc = objDocumentBuilder.parse(stream);
        }
        catch(Exception ex)
        {
            throw ex;
        }       

        return doc;
    }

     public boolean checkSign ()
     {
         _messageRequesst = "";
         _messageRequesst = Security.Scr_Base64.Decode(_encryptedMessage);
         
         String privateKey = Configuration.PaymentPrivateKey;
         
         String signStr = _moid + _serviceNum + _phone + _messageRequesst.toLowerCase() + privateKey;
         
         String sign = Scr_Base64.Encode(Scr_Sha1.parseSha1(signStr));
         
         if(_sign.equals(sign))
            return true;
         
         return false;
                 
     }
     
}
