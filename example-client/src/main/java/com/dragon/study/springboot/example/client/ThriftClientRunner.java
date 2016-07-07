package com.dragon.study.springboot.example.client;

import com.dragon.study.springboot.example.api.Calculator;
import com.dragon.study.springboot.thrift.client.annotation.ThriftClient;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by dragon on 16/7/7.
 */
@Component
public class ThriftClientRunner implements CommandLineRunner {

  @ThriftClient
  Calculator.Client client;

  @Override
  public void run(String... args) throws Exception {
    System.out.println("hello spring boot");
    int result = client.add(10, 20);
    System.out.println("10 + 20 = " + result);
  }
}
