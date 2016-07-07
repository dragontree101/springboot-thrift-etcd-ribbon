package com.dragon.study.springboot.example.server;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by dragon on 16/6/18.
 */
@Slf4j
public class Application {

  public static void main(String[] args) {
    System.out.println("---");
    try {
      throw new RuntimeException("1234", new ArrayIndexOutOfBoundsException("0000"));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    log.info("===");
  }
}
