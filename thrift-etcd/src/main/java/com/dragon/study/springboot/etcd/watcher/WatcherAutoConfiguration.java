package com.dragon.study.springboot.etcd.watcher;


import com.dragon.study.springboot.etcd.EtcdAutoConfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mousio.etcd4j.EtcdClient;

/**
 * Created by dragon on 16/5/10.
 */
@Configuration
@Import(EtcdAutoConfiguration.class)
public class WatcherAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public EtcdWatcher etcdWatcher(EtcdClient etcdClient) {
    return new EtcdWatcher(etcdClient);
  }
}
