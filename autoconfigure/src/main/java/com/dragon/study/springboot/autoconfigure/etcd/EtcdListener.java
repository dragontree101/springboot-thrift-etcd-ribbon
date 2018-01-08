package com.dragon.study.springboot.autoconfigure.etcd;

import java.util.concurrent.CancellationException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mousio.client.promises.ResponsePromise;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdKeysResponse;


@Data
@Slf4j
public abstract class EtcdListener
    implements ResponsePromise.IsSimplePromiseResponseHandler<EtcdKeysResponse> {

  public EtcdListener(EtcdClient etcdClient, String listenPath) {
    this.etcdClient = etcdClient;
    this.listenPath = listenPath;
  }

  private EtcdClient etcdClient;
  private String watchPath;
  protected String listenPath;

  @Override
  public void onResponse(ResponsePromise<EtcdKeysResponse> responsePromise) {
    boolean hasClosed = false;
    try {
      EtcdKeysResponse response = responsePromise.get();
      if (response != null) {
        switch (response.action) {
          case expire:
          case delete:
          case set:
          case update:
          case create:
          case compareAndDelete:
          case compareAndSwap:
            changeEvent();
          default:
            log.warn("unknown action is {}", response.action.toString());
            break;
        }
      }
    } catch (Exception e) {
      if(e.getCause() instanceof CancellationException) {
        log.warn("etcd client was closed");
        hasClosed = true;
      } else {
        log.error(e.getMessage(), e);
      }
    } finally {
      while (true) {
        if(hasClosed) {
          break;
        }
        try {
          EtcdKeysResponse keysResponse = etcdClient.get(listenPath).send().get();

          if (keysResponse != null) {
            long modifyIndex = keysResponse.etcdIndex;
            etcdClient.get(listenPath).recursive().waitForChange(modifyIndex + 1).send()
                .addListener(this);
          } else {
            log.warn("keys response is null");
          }
          break;
        } catch (Exception e) {
          log.warn(e.getMessage(), e);
        }
      }
    }
  }

  protected abstract void changeEvent();

}
