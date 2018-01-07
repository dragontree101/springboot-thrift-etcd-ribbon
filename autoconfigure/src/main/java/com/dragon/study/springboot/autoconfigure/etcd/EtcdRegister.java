package com.dragon.study.springboot.autoconfigure.etcd;

import lombok.Data;
import mousio.etcd4j.EtcdClient;


@Data
public class EtcdRegister {

  private boolean isStart = false;
  private EtcdClient client;
  private String path;
  private String key;
  private String value;
}
