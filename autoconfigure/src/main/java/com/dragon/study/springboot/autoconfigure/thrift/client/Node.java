package com.dragon.study.springboot.autoconfigure.thrift.client;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(exclude = {"timeout"})
@ToString
public class Node {
  private String ip;
  private int port;
  private int timeout = 500;
}
