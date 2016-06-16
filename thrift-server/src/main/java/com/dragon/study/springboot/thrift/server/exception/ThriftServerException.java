package com.dragon.study.springboot.thrift.server.exception;

/**
 * Created by dragon on 16/4/28.
 */
public class ThriftServerException extends RuntimeException {

  public ThriftServerException(String message) {
    super(message);
  }

  public ThriftServerException(String message, Throwable t) {
    super(message, t);
  }

}
