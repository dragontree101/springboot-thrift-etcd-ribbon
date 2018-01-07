package com.dragon.study.springboot.autoconfigure.etcd;

import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.List;

import lombok.Data;


@Data
@ConfigurationProperties(prefix = "etcd")
public class EtcdClientProperties {
  @NotNull
  List<URI> uris;
  int retryTimes = 3;
  int beforeRetryTime = 200;
}
