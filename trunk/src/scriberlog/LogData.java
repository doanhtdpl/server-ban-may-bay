/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scriberlog;

/**
 *
 * @author LinhTA
 */
//import common.ZingContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import libCore.Util;

public class LogData {

    public static class ActionIdDescription {

        public static String GET_FRIEND_REQUEST = "1";
        public static String GET_VIP_DATA = "2";
        public static String AUTO_ADD_FRIEND = "3";
        public static String GET_SUGGEST_FRIEND = "4";
        public static String LOAD_BLOGCOMM_PAGE = "200";
        public static String GET_LISTBLOGGER_VIP = "201";
        public static String LOAD_CAMPAIN_HOME = "202";
        public static String LOAD_CAMPAIN_DAITEL = "202";
        public static String LOAD_CONFESSTION = "203";
        public static String ACTION_VISITBLOG_BANNER = "701";

        public static String ACTION_HOTTOPPIC = "208";
        public static String ACTION_MOVEFROMYAHOO = "209";
        public static String LOAD_HOME_MEBLOG_FROM_LEFTMENU = "500";
        public static String LOAD_HOME_MEBLOG_FROM_OTHER = "501";
        public static String LOAD_EVENT_HOME_FROM_LEFTMENU = "505";
        public static String LOAD_EVENT_HOME_FROM_OTHER = "506";
        public static String LOAD_EVENT_DETAIL_FROM = "507";

       
        
       
    }
    public static String GAME_REQUEST = "BLOGCOMM_REQUEST";
    public static String LOG_REQUEST_TEMPLATE = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s";
// server_ip, request_domain, request_uri, request_time, execution_time, memory, client_ip, username, application_data
//* memory=0, application_data=num_action_in_request|total_time_of_action
//* execution_time tinh bang milisecond
//* request_time: unix time stamp
//* client_ip: x_forwarded_for, remote_host....
    public static String GAME_ACTION = "BLOGCOMM_ACTION";
    public static String LOG_ACTION_TEMPLATE = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s";
// server_ip, request_domain, client_ip, username, actionid, request_time, appdata, execution_time
//* appdata=??? tam thoi de trong
//* execution_time: milisecond
//* request_time: unix time stamp
    
    public static  String VISIT_PAGEEVENT = "BLOGCOMM_VIEWEVENT";
    public static String LOG_VISIT_ACTION_TEMPLATE = "%s\t%s\t%s\t%s";
    
    
     public static  String VISIT_BANNER = "BLOGCOMM_BANNEREVENT";
      public static String LOG_BANNER_ACTION_TEMPLATE = "%s\t%s\t%s\t%s\t%s";
    
    private static LogData _instance = new LogData();
//    public static Map<String, String> mapCatetoActionID = new HashMap<String, String>();

    public static LogData instance() {
        return _instance;
    }
    private LogData.LogRequestData m_logRequest = null;
    private List<LogActionData> m_logActionList = new ArrayList<LogActionData>();
    private LogActionData m_currentLogActionData = null;

    public LogRequestData getCurrentLogRequest() {
        if (m_logRequest == null) {
            m_logRequest = new LogRequestData();
        }
        return m_logRequest;
    }

    public LogActionData getCurrentLogAction() {
        if (m_currentLogActionData == null) {
            m_currentLogActionData = new LogActionData();
        }
        return m_currentLogActionData;
    }

    public void finishLogAction() {
        if (m_currentLogActionData != null) {
            m_currentLogActionData.finish();
            m_logActionList.add(m_currentLogActionData);
        }
        m_currentLogActionData = null;
    }

    public String getLogRequestData() {
        if (m_logRequest != null) {
            return m_logRequest.getData();
        }
        return "";
    }

    public String getLogActionData() {
        String data = "";
        for (int i = 0; i < m_logActionList.size(); i++) {
            LogActionData logData = m_logActionList.get(i);
            if (logData != null && logData.getData().length() > 0) {
                if (data.length() == 0) {
                    data = logData.getData();
                } else {
                    data = data + "\n" + logData.getData();
                }
            } else {
                System.out.println("logData == null");
            }
        }
        return data;
    }

    public static class LogRequestData {

        public LogRequestData() {
            timeBegin = System.currentTimeMillis();
            requestTime = String.valueOf(timeBegin / 1000); // chuyen tu milisecond ve second

            ZingContext context = ZingContext.getCurrentInstance();
            serverIp = context.serverIp;
            requestDomain = context.requestDomain;
            requestUri = context.requestUri;
            clientIp = context.clientIp;
            username = context.username;
        }

        public void finish() {
            long currentTime = System.currentTimeMillis();
            long exTime = currentTime - timeBegin;
            executionTime = String.valueOf(exTime);
        }

        public String getData() {
            String data = String.format(LOG_REQUEST_TEMPLATE, serverIp, requestDomain, requestUri, requestTime, executionTime, memory, clientIp, username, appData);
            return data;
        }
        //public static String LOG_REQUEST_TEMPLATE = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s";
        // server_ip, request_domain, request_uri, request_time, execution_time, memory, client_ip, username, application_data
        private long timeBegin;
        public String serverIp = "";
        public String requestDomain = "";
        public String requestUri = "";
        public String requestTime = "";
        public String executionTime = "";
        public String memory = "0";
        public String clientIp = "";
        public String username = "";
        public String appData = "DBPTK";
    }

    public static class LogActionData {

        public LogActionData() {
            timeBegin = System.currentTimeMillis();
            time = String.valueOf(timeBegin / 1000);

            ZingContext context = ZingContext.getCurrentInstance();
            serverIp = context.serverIp;
            domain = context.requestDomain;
            clientIp = context.clientIp;
            username = context.username;
            appData = "";
        }

        public void finish() {
            //alo
            long currentTime = System.currentTimeMillis();
            long exTime = currentTime - timeBegin;
            exectionTime = String.valueOf(exTime);
        }

        public String getData() {
            if (actionId.length() == 0) {
                System.out.println("actionId of LogActionData invalid");
                return "";
            }
            String data = String.format(LOG_ACTION_TEMPLATE, serverIp, domain, clientIp, username, actionId, time, appData, exectionTime);
            return data;
        }
        private long timeBegin;
        public String serverIp = "";
        public String domain = "";
        public String clientIp = "";
        public String username = "";
        public String actionId = "";
        public String time = "";
        public String appData = "";
        public String exectionTime = "";
    }
      //LinhTA
    public static class LogAction_viewPageEvent
    {
        public LogAction_viewPageEvent(HttpServletRequest req) 
        {
              serverIp = req.getLocalAddr();
            domain= req.getServerName();
            requestUri= req.getRequestURI();
            clientIp= Util.getClientIP(req);
            
        }
        
          public void finish() {
            
        }

        public String getData() {
            String data = String.format(LOG_VISIT_ACTION_TEMPLATE,serverIp, domain, clientIp, actionId);
            return data;
        }
        
        public String serverIp = "";
        public String domain = "";
        public String requestUri = "";
        public String clientIp = "";
        public String actionId = "";
    }
    
    public static class LogAction_visitByBanner
    {
        public LogAction_visitByBanner(HttpServletRequest req) 
        {
           
             serverIp = req.getLocalAddr();
            domain= req.getServerName();
            requestUri= req.getRequestURI();
            
           clientIp= Util.getClientIP(req);
            
            appData = req.getParameterValues("_banner")[0];
        }
        
          public void finish() {
            
        }

        public String getData() {
            String data = String.format(LOG_BANNER_ACTION_TEMPLATE,serverIp, domain, clientIp, actionId, appData);
            return data;
        }
        
        public String serverIp = "";
        public String domain = "";
        public String requestUri = "";
        public String clientIp = "";
        public String appData = "";
         public String actionId = "";
    }
    
}
