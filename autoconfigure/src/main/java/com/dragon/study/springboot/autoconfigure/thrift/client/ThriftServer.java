package com.dragon.study.springboot.autoconfigure.thrift.client;

import com.netflix.loadbalancer.Server;


public class ThriftServer extends Server {

  private final MetaInfo metaInfo;

  public ThriftServer(final String appName, String host, int port) {
    super(host, port);
    metaInfo = new MetaInfo() {
      @Override
      public String getAppName() {
        return appName;
      }

      @Override
      public String getServerGroup() {
        return null;
      }

      @Override
      public String getServiceIdForDiscovery() {
        return null;
      }

      @Override
      public String getInstanceId() {
        return null;
      }
    };
  }

  @Override
  public MetaInfo getMetaInfo() {
    return metaInfo;
  }
}
