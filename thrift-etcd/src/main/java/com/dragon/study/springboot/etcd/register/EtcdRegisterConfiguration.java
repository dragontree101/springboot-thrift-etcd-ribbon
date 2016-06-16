package com.dragon.study.springboot.etcd.register;

import com.dragon.study.springboot.etcd.config.EtcdDiscoveryProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.regex.Pattern;

import mousio.etcd4j.EtcdClient;

/**
 * Created by dragon on 16/6/3.
 */
@Configuration
@EnableScheduling
@ConditionalOnBean(name = "thriftServer")
@AutoConfigureAfter(name = "com.dragon.study.springboot.thrift.server.ThriftServerBootstrap")
@EnableConfigurationProperties(EtcdDiscoveryProperties.class)
public class EtcdRegisterConfiguration {

  private static final Pattern DEFAULT_ADDRESS_PATTERN = Pattern
      .compile("\\d{1,3}(?:\\.\\d{1,3}){3}(?::\\d{1,5})?");

  @Autowired
  private EtcdDiscoveryProperties etcdRegisterProperties;

  @Autowired
  private EtcdClient etcdClient;

  @Autowired
  private EtcdRegister etcdRegister;

  private boolean isRefresh = false;

  @Scheduled(initialDelayString = "${etcd.discovery.heartbeat:5000}", fixedRateString = "${spring.cloud.etcd.discovery.heartbeat:5000}")
  protected void sendHeartbeat() {
    register();
  }

  private void register() {
    if (!etcdRegister.isStart()) {
      return;
    }

    String registerPath = etcdRegister.getPath();
    String registerKey = etcdRegister.getKey();
    String value = etcdRegister.getValue();
    if (registerPath != null && registerKey != null && value != null && DEFAULT_ADDRESS_PATTERN
        .matcher(registerKey).matches()) {

      String path = registerPath + "/" + registerKey;
      int sessionTime = etcdRegisterProperties.getTtl();
      try {
        if (isRefresh) {
          etcdClient.refresh(path, sessionTime).send().get();
        } else {
          etcdClient.put(path, value).ttl(sessionTime).send().get();
        }
      } catch (Exception e) {
        e.printStackTrace();
        try {
          etcdClient.put(path, value).ttl(sessionTime).send().get();
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      isRefresh = true;
    }
  }


}
