package com.dragon.study.springboot.example.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by dragon on 16/6/18.
 */
@SpringBootApplication
public class Application {

  public static void main(String[] args) throws Exception {
    SpringApplication app = new SpringApplication(Application.class);
    app.setWebEnvironment(false);
    app.run(args);
  }
}
