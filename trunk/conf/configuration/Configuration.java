package configuration;

import libCore.Config;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Configuration {

    public static String LOG_HOST;
    public static int LOG_PORT;
    
    public static String RedisDB_timeout;
    public static String RedisDB_host;
    public static String RedisDB_port;
    public static String RedisDB_pass;
    public static String RedisDB_database;
    public static String RedisDB_max_active;
    public static String RedisDB_max_idle;
    public static String RedisDB_max_wait;
    
    public static String KEY_BLOGCOM_EVENT_CACHING = "blogcom_event.%d";
    public static String KEY_BLOGCOM_CATE_CACHING = "blogcom_cate.%d";
    public static String KEY_BLOGCOM_NOTICE_CACHING = "blogcom_notice.%d";
    public static String KEY_BLOGCOM_LIST_CATEGORY_CACHING = "blogcom_listcategory.%d";
    public static final List<String> listFanPagesGame = new ArrayList<String>();
    
    public static String PaymentPrivateKey;
    public static String PaymentCoin8500;
    public static String PaymentCoin8700;
    
    public static String Coin_UserDefault;
    
    public static int retries;

    static {
        try {
//            listFanPagesGame.add("sgamechanlong");
            

            LOG_HOST = Config.getParam("scriber_log", "host");
            LOG_PORT = Integer.parseInt(Config.getParam("scriber_log", "port"));
            
            RedisDB_host        = Config.getParam("redis", "host");
            RedisDB_port        = Config.getParam("redis", "port");
            RedisDB_timeout     = Config.getParam("redis", "timeout");
            RedisDB_pass        = Config.getParam("redis", "pass");
            RedisDB_database    = Config.getParam("redis", "database");
            RedisDB_max_active  = Config.getParam("redis", "max_active");
            RedisDB_max_idle    = Config.getParam("redis", "max_idle");
            RedisDB_max_wait    = Config.getParam("redis", "max_wait"); 
            
            PaymentPrivateKey   = Config.getParam("payment", "privatekey");
            PaymentCoin8500     = Config.getParam("payment", "p8500");
            PaymentCoin8700     = Config.getParam("payment", "p8700");
            
            Coin_UserDefault    = Config.getParam("defaultUser", "coin");

            retries = 3;
            
        } catch (Exception e) {
        }
    }
    

}