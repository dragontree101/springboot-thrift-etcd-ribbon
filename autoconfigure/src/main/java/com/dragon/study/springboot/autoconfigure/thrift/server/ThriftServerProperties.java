package com.dragon.study.springboot.autoconfigure.thrift.server;

import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;


@Data
@ConfigurationProperties(prefix = "thrift.server")
public class ThriftServerProperties {

  @NotNull
  //服务使用的端口
  private int port;

  private String prefixPath = "/dragon/service/";

  //服务进程的工作队列最小值
  private int minWorker = Runtime.getRuntime().availableProcessors();

  //服务进程的工作队列最大值
  private int maxWorker = Runtime.getRuntime().availableProcessors();

  private int workerQueueCapacity = 1024;

  private String serviceName;

}
