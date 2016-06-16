package com.dragon.study.springboot.thrift.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by Reilost on 15/11/27.
 */
@Data
@ConfigurationProperties(prefix = "thrift.server")
public class ThriftServerProperties {
  //服务使用的端口
  private int port;

  //服务进程的工作队列最小值
  private int minWorker = Runtime.getRuntime().availableProcessors();

  //服务进程的工作队列最大值
  private int maxWorker = Runtime.getRuntime().availableProcessors();

  private int workerQueueCapacity = 1024;

  private String serviceName;

}
