package com.dragon.study.springboot.thrift.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by Reilost on 15/11/27.
 */
@Data
@ConfigurationProperties(prefix = "thrift.client")
public class ThriftClientProperties {
  private int poolMaxTotalPerKey = 200;
  private int poolMaxIdlePerKey = 40;
  private int poolMinIdlePerKey = 10;
  private long poolMaxWait = 1000;
}
