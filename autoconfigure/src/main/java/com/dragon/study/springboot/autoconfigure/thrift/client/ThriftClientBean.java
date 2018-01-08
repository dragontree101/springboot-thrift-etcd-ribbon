package com.dragon.study.springboot.autoconfigure.thrift.client;



import java.lang.reflect.Constructor;

import lombok.Data;


@Data
public class ThriftClientBean {
  private RouterAlgorithm router;
  private int timeout;
  private int retryTimes;
  private Constructor<?> clientConstructor;
}
