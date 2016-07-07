package com.dragon.study.springboot.etcd.watcher;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by dragon on 16/6/3.
 */
@Configuration
@ConditionalOnBean(EtcdListener.class)
@EnableScheduling
@AutoConfigureAfter(name = "com.dragon.study.springboot.thrift.client.client.ThriftClientConfiguration")
@Import(WatcherAutoConfiguration.class)
public class EtcdWatcherConfiguration {

  @Autowired
  EtcdWatcher etcdWatcher;


  @Scheduled(initialDelayString = "${etcd.discovery.heartbeat:5000}", fixedRateString = "${spring.cloud.etcd.discovery.heartbeat:5000}")
  protected void sendHeartbeat() {
    watcher();
  }


  public void watcher() {
    Iterator<EtcdListener> iterator = etcdWatcher.getListeners().iterator();

    while (iterator.hasNext()) {
      EtcdListener listener = iterator.next();
      try {
        String path = listener.getWatchPath();
        if (!path.isEmpty()) {
          etcdWatcher.addWatchPath(listener);
          listener.setWatchPath(new String());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
