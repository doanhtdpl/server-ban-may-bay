/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package logging.report.scribe.core;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author LinhTA
 */

public class ScribeClientPool<T extends TServiceClient> implements
  AutoCloseable {

  private static final Logger LOGGER = LoggerFactory
   .getLogger(ScribeClientPool.class);

  private final GenericObjectPool<T> internalPool;

  public ScribeClientPool(ClientFactory<T> clientFactory, GenericObjectPool.Config poolConfig, String host, int port) 
  {
    this(
            clientFactory, 
            new BinaryOverSocketProtocolFactory(host, port),
            poolConfig
    );
 }

  public ScribeClientPool(ClientFactory<T> clientFactory,  ProtocolFactory protocolFactory, GenericObjectPool.Config poolConfig)
  {
  this.internalPool = new GenericObjectPool<T>(new ThriftClientFactory(
    clientFactory, protocolFactory), poolConfig);
 }

  class ThriftClientFactory extends BasePoolableObjectFactory<T> {

   private ClientFactory<T> clientFactory;
  private ProtocolFactory protocolFactory;

   public ThriftClientFactory(ClientFactory<T> clientFactory,
    ProtocolFactory protocolFactory) {
   this.clientFactory = clientFactory;
   this.protocolFactory = protocolFactory;
  }

   @Override
  public T makeObject() throws Exception {
   try {
    TProtocol protocol = protocolFactory.make();
    return clientFactory.make(protocol);
   } catch (Exception e) {
    LOGGER.warn("whut?", e);
    throw new ThriftClientException(
      "Can not make a new object for pool", e);
   }
  }

   @Override
  public void destroyObject(T obj) throws Exception {
   if (obj.getOutputProtocol().getTransport().isOpen()) {
    obj.getOutputProtocol().getTransport().close();
   }
   if (obj.getInputProtocol().getTransport().isOpen()) {
    obj.getInputProtocol().getTransport().close();
   }
  }
 }

  public static interface ClientFactory<T> {

   T make(TProtocol tProtocol);
 }

  public static interface ProtocolFactory {

   TProtocol make();
 }

  public static class BinaryOverSocketProtocolFactory implements
   ProtocolFactory {

   private String host;
  private int port;

   public BinaryOverSocketProtocolFactory(String host, int port) {
   this.host = host;
   this.port = port;
  }

   public TProtocol make() {
   TTransport transport = new TFramedTransport(new TSocket(host, port));
   try {
    transport.open();
   } catch (TTransportException e) {
    LOGGER.warn("whut?", e);
    throw new ThriftClientException("Can not make protocol", e);
   }
   //return new TBinaryProtocol(transport);
   return new TBinaryProtocol(transport, false, false);
  }
 }

  public static class ThriftClientException extends RuntimeException {

   // Fucking Eclipse
  private static final long serialVersionUID = -2275296727467192665L;

   public ThriftClientException(String message, Exception e) {
   super(message, e);
  }

  }

  public T getResource() {
  try {
   return (T) internalPool.borrowObject();
  } catch (Exception e) {
   throw new ThriftClientException(
     "Could not get a resource from the pool", e);
  }
 }

  public void returnResourceObject(T resource) {
  try {
   internalPool.returnObject(resource);
  } catch (Exception e) {
   throw new ThriftClientException(
     "Could not return the resource to the pool", e);
  }
 }

  public void returnBrokenResource(T resource) {
  returnBrokenResourceObject(resource);
 }

  public void returnResource(T resource) {
  returnResourceObject(resource);
 }

  protected void returnBrokenResourceObject(T resource) {
  try {
   internalPool.invalidateObject(resource);
  } catch (Exception e) {
   throw new ThriftClientException(
     "Could not return the resource to the pool", e);
  }
 }

  public void destroy() {
  close();
 }

  public void close() {
  try {
   internalPool.close();
  } catch (Exception e) {
   throw new ThriftClientException("Could not destroy the pool", e);
  }
 }
}