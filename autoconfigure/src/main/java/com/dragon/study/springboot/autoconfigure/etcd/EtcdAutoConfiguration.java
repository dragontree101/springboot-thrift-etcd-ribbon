package com.dragon.study.springboot.autoconfigure.etcd;


import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mousio.client.retry.RetryNTimes;
import mousio.etcd4j.EtcdClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(EtcdClientProperties.class)
@Slf4j
public class EtcdAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public EtcdClient etcdClient(EtcdClientProperties etcdClientProperties) {
    List<URI> uriList = etcdClientProperties.getUris();
    EtcdClient client = new EtcdClient(uriList.toArray(new URI[uriList.size()]));
    client.setRetryHandler(new RetryNTimes(etcdClientProperties.getBeforeRetryTime(),
        etcdClientProperties.getRetryTimes()));

    if (client.version() == null) {
      log.info("etcd urls are [ {} ] is invalid",
          etcdClientProperties.getUris().stream().map(Object::toString)
              .collect(Collectors.joining(", ")));
    } else {
      log.info("etcd version is {} , urls are [ {} ]", client.version().getCluster(),
          etcdClientProperties.getUris().stream().map(Object::toString)
              .collect(Collectors.joining(", ")));
    }
    return client;
  }
}
