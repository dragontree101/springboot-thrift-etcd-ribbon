package com.dragon.study.springboot.example.client;

import com.dragon.study.springboot.autoconfigure.thrift.client.ThriftClient;
import com.dragon.study.springboot.example.api.Calculator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


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
