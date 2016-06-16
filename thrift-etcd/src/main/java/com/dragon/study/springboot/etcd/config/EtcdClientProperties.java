package com.dragon.study.springboot.etcd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.List;

import lombok.Data;

/**
 * Created by dragon on 16/4/13.
 */
@Data
@ConfigurationProperties(prefix = "etcd")
public class EtcdClientProperties {
  List<URI> uris;
  int retryTimes = 3;
  int beforeRetryTime = 200;
}
