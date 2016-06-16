package com.dragon.study.springboot.etcd.register;

import lombok.Data;
import mousio.etcd4j.EtcdClient;

/**
 * Created by dragon on 16/4/13.
 */
@Data
public class EtcdRegister {

  private boolean isStart = false;
  private EtcdClient client;
  private String path;
  private String key;
  private String value;
}
