package com.dragon.study.springboot.thrift.client.route;

/**
 * Created by dragon on 16/5/6.
 */
public interface RouterAlgorithm {
  void init();

  Node getTransportNode();
}
