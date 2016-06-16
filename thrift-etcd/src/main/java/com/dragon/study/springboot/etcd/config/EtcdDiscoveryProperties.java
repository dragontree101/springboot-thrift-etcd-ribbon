package com.dragon.study.springboot.etcd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by dragon on 16/4/13.
 */
@Data
@ConfigurationProperties(prefix = "etcd.discovery")
public class EtcdDiscoveryProperties {
  String root = "/dragon";
  int heartbeat = 5000;
  int ttl = 10;

}
