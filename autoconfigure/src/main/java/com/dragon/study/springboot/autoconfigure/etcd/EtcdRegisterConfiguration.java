package com.dragon.study.springboot.autoconfigure.etcd;

import com.dragon.study.springboot.autoconfigure.thrift.server.ThriftServerBootstrap;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import mousio.etcd4j.EtcdClient;
import org.apache.thrift.server.TServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
@EnableScheduling
@ConditionalOnBean(value = TServer.class)
@AutoConfigureAfter(value = ThriftServerBootstrap.class)
@Slf4j
public class EtcdRegisterConfiguration {

  @Autowired
  private EtcdRegister etcdRegister;

  @Autowired
  private EtcdClient etcdClient;

  @Value("${etcd.discovery.ttl:10}")
  private int ttl;

  private static final Pattern DEFAULT_ADDRESS_PATTERN = Pattern
      .compile("\\d{1,3}(?:\\.\\d{1,3}){3}(?::\\d{1,5})?");

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
      try {
        if (isRefresh) {
          etcdClient.refresh(path, ttl).send().get();
        } else {
          etcdClient.put(path, value).ttl(ttl).send().get();
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        try {
          etcdClient.put(path, value).ttl(ttl).send().get();
        } catch (Exception ex) {
          log.error(ex.getMessage(), ex);
        }
      }
      isRefresh = true;
    }
  }


}
