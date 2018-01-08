package com.dragon.study.springboot.autoconfigure.thrift.client;

import java.util.Arrays;
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
    Arrays.stream(directAddress.split(",")).forEach(address -> {
      String parts[] = address.split(":");
      String ip = parts[0];
      int port = Integer.valueOf(parts[1]);
      Node node = new Node();
      node.setPort(port);
      node.setIp(ip);
      nodeList.add(node);
    });
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
