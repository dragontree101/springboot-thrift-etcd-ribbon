package com.dragon.study.springboot.autoconfigure.thrift.client;


import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.concurrent.ConcurrentHashMap;

public class TransportPoolFactory extends BaseKeyedPooledObjectFactory<Node, TTransport> {

  private static ConcurrentHashMap<String, Long> forbidMap = new ConcurrentHashMap<>();

  private static final int FORBID_TIME = 5000;

  @Override
  public void destroyObject(Node key, PooledObject<TTransport> value) throws Exception {
    TTransport transport = value.getObject();
    if (transport.isOpen()) {
      transport.close();
    }
  }

  /**
   * 把对象放入对象池，返回是否安全的放入
   */
  @Override
  public boolean validateObject(Node key, PooledObject<TTransport> value) {
    try {
      TTransport transport = value.getObject();
      return transport.isOpen();
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public TTransport create(Node key) {
    TTransport transport = new TFramedTransport(
        new TSocket(key.getIp(), key.getPort(), key.getTimeout()));
    String address = key.getIp() + ":" + key.getPort();
    try {
      if (forbidMap.containsKey(address)) {
        if (System.currentTimeMillis() - forbidMap.get(address) < FORBID_TIME) {
          throw new ThriftClientException(
              FORBID_TIME + " ms forbid to connect the node :" + address);
        } else {
          transport.open();
        }
      } else {
        transport.open();
      }
    } catch (TTransportException e) {
      forbidMap.put(address, System.currentTimeMillis());
      throw new ThriftClientException(
          ExceptionUtils.getMessage(e) + " ip is " + key.getIp() + ", port is " + key.getPort(), e);
    }
    return transport;
  }

  @Override
  public PooledObject<TTransport> wrap(TTransport value) {
    return new DefaultPooledObject<>(value);
  }

}
