/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scriberlog;

import java.util.ArrayList;
import java.util.List;
import jcommon.transport.client.*;
import jcommon.transport.*;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import scriberlog.core.LogEntry;
import scriberlog.core.ResultCode;
import scriberlog.core.ScribeService;
/**
 *
 * @author LinhTA
 */
public class LOG2 {
   
    public static void log()
    {
        String host = libCore.Config.getParam("scribelog_service", "host");
        int port = Integer.parseInt(libCore.Config.getParam("scribelog_service","port"));
        String category = "TEST_LINH";
        
        String mess = String.format(LogData.LOG_ACTIONS_TEMPLATE, "zingme","login","ccvv","0001","498568");
        scriberlog.core.LogEntry log_entry = new LogEntry(category, mess);
        TSocket socket = new TSocket(host, port);
        TTransport transport = new TFramedTransport(socket);
        TProtocol protocol = new TBinaryProtocol(transport, false, false);
        scriberlog.core.ScribeService.Client client = new ScribeService.Client(protocol, protocol);
        
        List<LogEntry> logs = new ArrayList<LogEntry>();
        logs.add(log_entry);
        
        try{
            transport.open();
            ResultCode result = client.Log(logs);
            System.out.print(result);
            transport.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        finally
        {            
            transport.close();
        }
    }
    
    public static void main(String[] args) {
        log();
    }
}
