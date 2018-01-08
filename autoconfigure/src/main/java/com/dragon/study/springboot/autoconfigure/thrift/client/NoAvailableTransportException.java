package com.dragon.study.springboot.autoconfigure.thrift.client;


public class NoAvailableTransportException extends Exception {

  public NoAvailableTransportException(String message, String className) {
    this(message, className, null);
  }

  public NoAvailableTransportException(String message, String className, Throwable cause) {
    super(message + " class name is " + className, cause);
  }
}
