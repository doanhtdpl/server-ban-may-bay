package scriberlog;

import java.util.HashMap;
//import org.log.game.LogData;

/**
 * Context of zing game service, instance created with ThreadLocal, i.e. each thread receive its
 * own instance when calling getCurrentInstance()
 * Usage is based on the fact that each servlet request is served in its own thread
 * @author anhn
 */
public class ZingContext {
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

    private static ThreadLocal<ZingContext> instance = new ThreadLocal<ZingContext>() {
        @Override
        protected ZingContext initialValue() {
            return (null);
        }
    };

    //Private constructor
    private ZingContext(int id, String name) {
        userid = id;
        username = name;
    }

    public static ZingContext createThreadInstance(int id, String name) {
        ZingContext context = new ZingContext(id, name);
        setCurrentInstance(context);
        return context;
    }

    protected static void setCurrentInstance(ZingContext context) {
        instance.set(context);
    }

    public static ZingContext getCurrentInstance() {
        return instance.get();
    }

    public static void release() {
        instance.set(null);
    }
}
