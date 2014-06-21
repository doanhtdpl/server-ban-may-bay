package scriberlog;

import java.util.HashMap;
//import org.log.game.LogData;

/**
 * Context of zing game service, instance created with ThreadLocal, i.e. each thread receive its
 * own instance when calling getCurrentInstance()
 * Usage is based on the fact that each servlet request is served in its own thread
 * @author anhn
 */
public class DataCoreContext {
    public int userid;
    public String username;
    public String avatarURL;
    public String displayName;
    public byte gender;
    public String email;
    public String serverIp = "";
    public String requestDomain  = "";
    public String requestUri  = "";
    public String clientIp = "";
    //log
    public LogData logData = new LogData();
    //////
    public boolean isDev;
    public HashMap cache = new HashMap();

    private static ThreadLocal<DataCoreContext> instance = new ThreadLocal<DataCoreContext>() {
        @Override
        protected DataCoreContext initialValue() {
            return (null);
        }
    };

    //Private constructor
    private DataCoreContext(int id, String name) {
        userid = id;
        username = name;
    }

    public static DataCoreContext createThreadInstance(int id, String name) {
        DataCoreContext context = new DataCoreContext(id, name);
        setCurrentInstance(context);
        return context;
    }

    protected static void setCurrentInstance(DataCoreContext context) {
        instance.set(context);
    }

    public static DataCoreContext getCurrentInstance() {
        return instance.get();
    }

    public static void release() {
        instance.set(null);
    }
}
