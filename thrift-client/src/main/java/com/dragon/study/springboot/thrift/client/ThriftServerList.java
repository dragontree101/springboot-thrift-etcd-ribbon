package com.dragon.study.springboot.thrift.client;


import com.dragon.study.springboot.thrift.client.route.Node;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdKeysResponse;

/**
 * Created by dragon on 16/6/8.
 */
@Slf4j
public class ThriftServerList extends AbstractServerList<ThriftServer> {

  private final EtcdClient etcd;
  private String serviceId;

  public ThriftServerList(EtcdClient etcd, String serviceId) {
    this.etcd = etcd;
    this.serviceId = serviceId;
  }

  @Override
  public void initWithNiwsConfig(IClientConfig iClientConfig) {
    this.serviceId = iClientConfig.getClientName();
  }

  @Override
  public List<ThriftServer> getInitialListOfServers() {
    return getServers();
  }

  @Override
  public List<ThriftServer> getUpdatedListOfServers() {
    return getServers();
  }

  private List<ThriftServer> getServers() {
    if (etcd == null) {
      log.warn("etcd is null, url is [ ]");
      return Collections.emptyList();
    }

    try {
      EtcdKeysResponse response = etcd.getDir("/dragon/service/" + serviceId).send().get();

      if (response.node.nodes == null || response.node.nodes.isEmpty()) {
        log.warn("response node is empty, url is [ ]");
        return Collections.emptyList();
      }

      List<ThriftServer> servers = new ArrayList<>();
      for (EtcdKeysResponse.EtcdNode node : response.node.nodes) {
        String nodePath = node.getKey();
        String appName = extractAppName(nodePath);
        Node thriftNodeAddress = addressToNode(nodePath);

        ThriftServer server = new ThriftServer(appName, thriftNodeAddress.getIp(), thriftNodeAddress.getPort());
        servers.add(server);
      }
      log.info("service id " + serviceId + " url is [" + servers.stream().map(s -> s.getHostPort()).collect(
          Collectors.joining(", "))+ "]");
      return servers;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Collections.emptyList();
    }
  }

  private Node addressToNode(String key) {
    String address = extractLastPath(key);
    Node node = new Node();
    String[] ipAndPort = address.split(":");
    node.setIp(ipAndPort[0]);
    node.setPort(new Integer(ipAndPort[1]));
    return node;
  }

  private String extractLastPath(String key) {
    int lastSlash = key.lastIndexOf("/");
    return key.substring(lastSlash + 1, key.length());
  }

  private String extractAppName(String key) {
    int lastSlash = key.lastIndexOf("/");
    String lastPah = key.substring(0, lastSlash);
    return extractLastPath(lastPah);
  }

}
