package com.dragon.study.springboot.thrift.client.client.route;


import com.dragon.study.springboot.etcd.watcher.EtcdWatcher;
import com.dragon.study.springboot.thrift.client.client.EtcdNotificationUpdate;
import com.dragon.study.springboot.thrift.client.client.ThriftServer;
import com.dragon.study.springboot.thrift.client.client.ThriftServerList;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.DummyPing;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAffinityServerListFilter;

import mousio.etcd4j.EtcdClient;

/**
 * Created by dragon on 16/5/6.
 */
public class RibbonAlgorithm implements RouterAlgorithm {

  private final EtcdWatcher etcdWatcher;

  private final String className;

  private final EtcdClient etcdClient;

  private DynamicServerListLoadBalancer<ThriftServer> loadBalancer;

  public RibbonAlgorithm(String className, EtcdClient etcdClient, EtcdWatcher etcdWatcher) {
    this.className = className;
    this.etcdClient = etcdClient;
    this.etcdWatcher = etcdWatcher;
    init();
  }

  @Override
  public void init() {
    DefaultClientConfigImpl config = DefaultClientConfigImpl.getClientConfigWithDefaultValues();
    config.setProperty(CommonClientConfigKey.ServerListUpdaterClassName,
        EtcdNotificationUpdate.class.getName());

    String path = "/rhllor/service/" + className;
    loadBalancer = new DynamicServerListLoadBalancer<>(config, new AvailabilityFilteringRule(),
        new DummyPing(), new ThriftServerList(etcdClient, className),
        new ZoneAffinityServerListFilter<>(),
        new EtcdNotificationUpdate(etcdClient, etcdWatcher, path));
  }

  @Override
  public Node getTransportNode() {
    Server server = loadBalancer.chooseServer();
    if(server == null) {
      return null;
    }
    Node node = new Node();
    node.setIp(server.getHost());
    node.setPort(server.getPort());
    return node;
  }

  public DynamicServerListLoadBalancer<ThriftServer> getLoadBalancer() {
    return loadBalancer;
  }
}
