package com.dragon.study.springboot.etcd.watcher;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;

/**
 * Created by dragon on 16/4/29.
 */
public class EtcdWatcher {

  private EtcdClient etcdClient;

  public EtcdWatcher(EtcdClient etcdClient)  {
    this.etcdClient = etcdClient;
  }

  private Set<EtcdListener> listeners = new HashSet<>();

  public void addWatchPath(EtcdListener listener) throws IOException {
    listeners.add(listener);
    EtcdResponsePromise responsePromise = etcdClient.get(listener.getListenPath()).recursive().waitForChange().send();
    responsePromise.addListener(listener);
  }

  public Set<EtcdListener> getListeners() {
    return listeners;
  }
}
