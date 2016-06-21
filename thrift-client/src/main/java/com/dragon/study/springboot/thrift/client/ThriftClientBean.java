package com.dragon.study.springboot.thrift.client;


import com.dragon.study.springboot.thrift.client.route.RouterAlgorithm;

import java.lang.reflect.Constructor;

import lombok.Data;

/**
 * Created by dragon on 16/6/3.
 */
@Data
public class ThriftClientBean {
  private RouterAlgorithm router;
  private int timeout;
  private int retryTimes;
  private Constructor<?> clientConstructor;
}
