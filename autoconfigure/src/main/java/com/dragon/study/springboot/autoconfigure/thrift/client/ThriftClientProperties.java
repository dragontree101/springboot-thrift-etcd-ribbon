package com.dragon.study.springboot.autoconfigure.thrift.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;


@Data
@ConfigurationProperties(prefix = "thrift.client")
public class ThriftClientProperties {
  private int poolMaxTotalPerKey = 200;
  private int poolMaxIdlePerKey = 40;
  private int poolMinIdlePerKey = 10;
  private long poolMaxWait = 1000;
}
