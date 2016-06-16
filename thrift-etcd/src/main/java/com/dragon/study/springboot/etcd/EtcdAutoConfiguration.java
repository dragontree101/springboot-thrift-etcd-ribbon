package com.dragon.study.springboot.etcd;


import com.dragon.study.springboot.etcd.config.EtcdClientProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import mousio.client.retry.RetryNTimes;
import mousio.etcd4j.EtcdClient;

/**
 * Created by dragon on 16/6/3.
 */
@Configuration
@EnableConfigurationProperties(EtcdClientProperties.class)
public class EtcdAutoConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(EtcdAutoConfiguration.class);

  @Bean
  @ConditionalOnMissingBean
  public EtcdClient etcdClient(EtcdClientProperties etcdClientProperties) {
    List<URI> uriList = etcdClientProperties.getUris();
    if (uriList == null || uriList.isEmpty()) {
      logger.error("uri has not been set");
      return null;
    }

    EtcdClient client = new EtcdClient(uriList.toArray(new URI[uriList.size()]));
    client.setRetryHandler(new RetryNTimes(etcdClientProperties.getBeforeRetryTime(),
        etcdClientProperties.getRetryTimes()));

    if (client.version() == null) {
      logger.info("etcd urls are [ {} ] is invalid",
          etcdClientProperties.getUris().stream().map(uri -> uri.toString())
              .collect(Collectors.joining(", ")));
    } else {
      logger.info("etcd version is {} , urls are [ {} ]", client.version().getCluster(),
          etcdClientProperties.getUris().stream().map(uri -> uri.toString())
              .collect(Collectors.joining(", ")));
    }
    return client;
  }
}
