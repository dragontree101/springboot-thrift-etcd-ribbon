package com.dragon.study.springboot.etcd.watcher;

import mousio.client.promises.ResponsePromise;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdKeysResponse;

/**
 * Created by dragon on 16/5/3.
 */
public abstract class EtcdListener
    implements ResponsePromise.IsSimplePromiseResponseHandler<EtcdKeysResponse> {
  public EtcdListener(){}

  public EtcdListener(EtcdClient etcdClient, String listenPath) {
    this.etcdClient = etcdClient;
    this.listenPath = listenPath;
  }

  private EtcdClient etcdClient;
  private String watchPath = new String();
  protected String listenPath = new String();

  public String getWatchPath() {
    return watchPath;
  }

  public void setWatchPath(String watchPath) {
    this.watchPath = watchPath;
  }

  public String getListenPath() {
    return listenPath;
  }

  @Override
  public void onResponse(ResponsePromise<EtcdKeysResponse> responsePromise) {
    while (true) {
      EtcdKeysResponse response = responsePromise.getNow();
      if (response != null) {
        try {
          switch (response.action) {
            case expire:
            case delete:
            case set:
            case update:
            case create:
            case compareAndDelete:
            case compareAndSwap:
              changeEvent(response);
              break;
            default:
              break;
          }
        } catch (Exception e) {
        } finally {
          try {
            EtcdKeysResponse keysResponse = etcdClient.get(listenPath).send().get();
            long modifyIndex = keysResponse.etcdIndex;

            etcdClient.get(listenPath).recursive().waitForChange(modifyIndex + 1).send()
                .addListener(this);
          } catch (Exception e) {
            watchPath = listenPath;
          }
        }
        break;
      } else {
        Throwable t = responsePromise.getException();
        if (t != null) {
          watchPath = listenPath;
          break;
        }
      }
    }
  }

  protected abstract void changeEvent(EtcdKeysResponse response);

}
