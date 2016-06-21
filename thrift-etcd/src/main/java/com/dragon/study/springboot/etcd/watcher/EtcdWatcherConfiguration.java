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
@AutoConfigureAfter(name = "com.ricebook.rhllor.spring.boot.thrift.client.ThriftClientConfiguration")
@Import(WatcherAutoConfiguration.class)
public class EtcdWatcherConfiguration {


  @Scheduled(initialDelayString = "${etcd.discovery.heartbeat:5000}", fixedRateString = "${spring.cloud.etcd.discovery.heartbeat:5000}")
  protected void sendHeartbeat(EtcdWatcher etcdWatcher) {
    watcher(etcdWatcher);
  }


  public void watcher(EtcdWatcher etcdWatcher) {
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
