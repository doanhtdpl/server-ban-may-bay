/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scriberlog;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
import scriberlog.core.ScribeClientPool;
import org.apache.log4j.Logger;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
/**
 *
 * @author LinhTA
 */
public class Scribelog2 {
   
    private static final Lock createLock_ = new ReentrantLock();
    private static Map<String, Scribelog2> _instances = new NonBlockingHashMap();
    private static final Logger logger_ = Logger.getLogger(Scribelog2.class);
    
    private ScribeClientPool<ScribeService.Client> _pool ;
    
    
    private Scribelog2(String host,int port ) {
        
        Config  poolconf= new Config();
         poolconf.maxActive = 80;
            poolconf.minIdle = 5;
            poolconf.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
            poolconf.testOnBorrow = true;
            poolconf.testWhileIdle = true;
            poolconf.numTestsPerEvictionRun = 10;
            poolconf.maxWait = 3000;
        
            _pool = new ScribeClientPool<ScribeService.Client>( 
                    new ScribeClientPool.ClientFactory<ScribeService.Client>() {
     @Override
     public ScribeService.Client make(TProtocol tProtocol) {
      return new ScribeService.Client(tProtocol);
     }
    }, poolconf, host, port);
    }
    
    public static Scribelog2 getInstance(String host, int port) {
        String key = host + port ;
        if (!_instances.containsKey(key)) {
            createLock_.lock();
            try {
                if (_instances.get(key) == null) {
                    _instances.put(key, new Scribelog2(host, port));
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instances.get(key);
    }
    
    public static Scribelog2 getInstance() 
    {    
        String host= libCore.Config.getParam("scribelog_service", "host");
        int port  = Integer.parseInt(libCore.Config.getParam("scribelog_service","port"));
        return  getInstance(host, port);
    }
    
    public ResultCode writeLog_resp(List<LogEntry> le)
    {
        ResultCode resp = null;
                
        ScribeService.Client scribe = null;
        
        try{
            scribe = _pool.getResource();
            resp = scribe.Log(new ArrayList<LogEntry>(le));
            //_pool.returnResource(scribe);
        }
        catch(Exception e)
        {
            logger_.error("Exception in writelog", e);
            resp = ResultCode.TRY_LATER;
            //_pool.returnBrokenResource(scribe);
        }
        finally
        {
            _pool.returnResourceObject(scribe);
        }
        
        return resp;
    }
    
    public void writeLog(List<LogEntry> le)
    {
        ScribeService.Client scribe = null;
        
        try{
            scribe = _pool.getResource();
            scribe.Log(new ArrayList<LogEntry>(le));
            //_pool.returnResource(scribe);
        }
        catch(Exception e)
        {
            logger_.error("Exception in writelog", e);
            //_pool.returnBrokenResource(scribe);
        }
        finally
        {
            _pool.returnResourceObject(scribe);
        }
    }
    
    public static void main(String[] args) {
        List<LogEntry> logs = new ArrayList<LogEntry>();
        logs.add(new LogEntry("POOLSCRIBE", "assd sd sd sdsds" +  Calendar.getInstance(TimeZone.getDefault()).getTime().toString()));
        
        Scribelog2.getInstance().writeLog(logs);
    }
}
