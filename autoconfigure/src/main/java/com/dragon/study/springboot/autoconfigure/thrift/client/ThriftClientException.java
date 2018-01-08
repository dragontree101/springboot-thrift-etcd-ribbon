package com.dragon.study.springboot.autoconfigure.thrift.client;


public class ThriftClientException extends RuntimeException {

  public ThriftClientException(String message) {
    super(message);
  }

  public ThriftClientException(String message, Throwable t) {
    super(message, t);
  }
}
