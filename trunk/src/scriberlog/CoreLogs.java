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

public class CoreLogs {
    private static CoreLogs _instance = new CoreLogs();
    public static CoreLogs instance(){
        return _instance;
    }
    private LogInterface logActive = null;
    public CoreLogs(){
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
        DataCoreContext context = DataCoreContext.getCurrentInstance();
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

        CoreLogs.instance().writeLog(logData);
    }
    
    public static void main(String[] args) {
         LogData.LogAction_visitByBanner logAct_banner = new LogData.LogAction_visitByBanner(null);
                logAct_banner.actionId = LogData.ActionIdDescription.ACTION_VISITBLOG_BANNER;
                   CoreLogs.instance().writeLog_visitByBanner(logAct_banner.getData());
    }
}
