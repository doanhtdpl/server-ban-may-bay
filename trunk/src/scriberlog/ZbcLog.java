/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scriberlog;

/**
 *
 * @author trung
 */
import java.util.ArrayList;
import java.util.List;
import scriberlog.LogInterface;
import scriberlog.ScriberLog;

public class ZbcLog {
    private static ZbcLog _instance = new ZbcLog();
    public static ZbcLog instance(){
        return _instance;
    }
    private LogInterface logActive = null;
    public ZbcLog(){
        logActive = ScriberLog.instance();
    }

    public void writeLog(LogData logData){
        String logRequestData = logData.getLogRequestData();
        String logActionData = logData.getLogActionData();

        List<String> categoryList = new ArrayList<String>();
        List<String> logDataList = new ArrayList<String>();
        if(logRequestData.length() >0){
            categoryList.add(LogData.GAME_REQUEST);
            logDataList.add(logRequestData);
        }
        if(logActionData.length() >0){
            categoryList.add(LogData.GAME_ACTION);
            logDataList.add(logActionData);
        }

        if(logActive != null){
            if(logActive.writeLogs(categoryList, logDataList))
                System.out.println("ghi log thanh cong");
            else
                System.out.println("ghi log khong thanh cong");
        }
    }

    public void writeLog_viewPageEvent(String logData){
        String category = LogData.VISIT_PAGEEVENT;
      
        if(logActive != null){
            if(logActive.writeLog(category, logData))
                System.out.println("ghi log thanh cong");
            else
                System.out.println("ghi log khong thanh cong");
        }
    }
     
     public void writeLog_visitByBanner (String logData)
     {
         String category = LogData.VISIT_BANNER;
         
         if(logActive != null){
            if(logActive.writeLog(category, logData))
                System.out.println("ghi log thanh cong");
            else
                System.out.println("ghi log khong thanh cong");
        }
     }

    
    public static void testWriteLog(){
        ZingContext context = ZingContext.getCurrentInstance();
        LogData logData = context.logData;

        LogData.LogRequestData logRequestData = logData.getCurrentLogRequest();
        logRequestData.appData = "";
        logRequestData.clientIp = context.clientIp;
        logRequestData.memory = "0";
        logRequestData.requestDomain = context.requestDomain;
        logRequestData.requestUri = context.requestUri;
        logRequestData.serverIp = context.serverIp;
        logRequestData.username = context.username;

        LogData.LogActionData logActionData = logData.getCurrentLogAction();
        logActionData.actionId = "1";
        logActionData.appData = "abc";
        logActionData.clientIp = context.clientIp;
        logActionData.domain = context.requestDomain;
        logActionData.serverIp = context.serverIp;
        logActionData.username = context.username;
        logData.finishLogAction();

        ZbcLog.instance().writeLog(logData);
    }
}
