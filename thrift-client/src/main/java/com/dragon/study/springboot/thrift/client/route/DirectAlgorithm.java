package com.dragon.study.springboot.thrift.client.route;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DirectAlgorithm implements RouterAlgorithm {

  private final String directAddress;
  private static final int MAX_INDEX = Integer.MAX_VALUE - 100000;

  private List<Node> nodeList = new CopyOnWriteArrayList<>();
  private AtomicInteger index = new AtomicInteger(0);

  public DirectAlgorithm(String directAddress) {
    this.directAddress = directAddress;
    init();
  }

  @Override
  public void init() {
    String addressList[] = directAddress.split(",");
    int length = addressList.length;
    for (int i = 0; i < length; i++) {
      String address[] = addressList[i].split(":");
      String ip = address[0];
      int port = new Integer(address[1]).intValue();
      Node node = new Node();
      node.setPort(port);
      node.setIp(ip);
      nodeList.add(node);
    }
  }

  @Override
  public synchronized Node getTransportNode() {
    int size = nodeList.size();
    if(size == 0) {
      return null;
    }
    int i = index.getAndIncrement();
    if (i > MAX_INDEX) {
      index.set(0);
    }
    return nodeList.get(i % size);
  }
}
