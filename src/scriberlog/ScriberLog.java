/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scriberlog;

/**
 *
 * @author LinhTA
 */
import libCore.LogUtil;
import java.util.ArrayList;
import java.util.List;
import jcommon.transport.client.ClientFactory;
import jcommon.transport.client.TClientInfo;
import org.apache.log4j.Logger;
import org.apache.thrift.protocol.TBinaryProtocol;
import scriberlog.LogEntry;
import scriberlog.ScribeService;
import configuration.Configuration;

public class ScriberLog implements LogInterface {

    private static ScriberLog m_instance = new ScriberLog();
    private static final Logger logger_ = Logger.getLogger(ScriberLog.class);

    public static ScriberLog instance() {
        return m_instance;
    }

    public TClientInfo getClientInfo() {
        TClientInfo aInfo = ClientFactory.getClient(Configuration.LOG_HOST, Configuration.LOG_PORT, ScribeService.Client.class, TBinaryProtocol.class);
        return aInfo;
    }

    public boolean writeLog(String category, String logData) {
        boolean ret = false;
        List<LogEntry> logEntryList = new ArrayList<LogEntry>();
        LogEntry logEntry = new LogEntry();
        logEntry.category = category;
        logEntry.message = logData;
        logEntryList.add(logEntry);

        if (logEntryList.size() > 0) {
            TClientInfo clientInfo = getClientInfo();
            ScribeService.Client aClient = clientInfo.getClientT();
            if (aClient != null) {
                try {
                    aClient.Log2(logEntryList);
                    ret = true;
                } catch (Exception e) {
                    logger_.warn(LogUtil.stackTrace(e));
                    clientInfo.close();
                    aClient = clientInfo.getClientT();
                    //retry
                    try {
                        aClient.Log2(logEntryList);
                        ret = true;
                    } catch (Exception e2) {
                        logger_.error(LogUtil.stackTrace(e2));
                        ret = false;
                    }
                }
                clientInfo.cleanUp();
            } else {
                System.out.println("aClient == null");
            }
        }
        return ret;
    }

    public boolean writeLogs(List<String> categorys, List<String> logs) {
        boolean ret = false;
        List<LogEntry> logEntryList = new ArrayList<LogEntry>();
        for (int i = 0; i < categorys.size(); i++) {
            LogEntry logEntry = new LogEntry();
            logEntry.category = categorys.get(i);
            logEntry.message = logs.get(i);
            logEntryList.add(logEntry);
        }
        if (logEntryList.size() > 0) {
            TClientInfo clientInfo = getClientInfo();
            ScribeService.Client aClient = clientInfo.getClientT();
            if (aClient != null) {
                try {
                    aClient.Log2(logEntryList);
                    ret = true;
                } catch (Exception e) {
                    logger_.warn(LogUtil.stackTrace(e));
                    clientInfo.close();
                    aClient = clientInfo.getClientT();
                    //retry
                    try {
                        aClient.Log2(logEntryList);
                        ret = true;
                    } catch (Exception e2) {
                        logger_.error(LogUtil.stackTrace(e2));
                        ret = false;
                    }
                }
                clientInfo.cleanUp();
            } else {
                logger_.error("aClient = null");
            }
        }
        return ret;
    }
}
