package com.dragon.study.springboot.autoconfigure.thrift.client;


public interface RouterAlgorithm {
  void init();

  Node getTransportNode();
}
