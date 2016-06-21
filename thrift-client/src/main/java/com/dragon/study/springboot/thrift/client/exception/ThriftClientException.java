package com.dragon.study.springboot.thrift.client.exception;

/**
 * Created by dragon on 16/5/27.
 */
public class ThriftClientException extends RuntimeException {

  public ThriftClientException(String message) {
    super(message);
  }

  public ThriftClientException(String message, Throwable t) {
    super(message, t);
  }
}
